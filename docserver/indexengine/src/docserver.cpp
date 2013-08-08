// docserver.c : docserver
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-05-17

#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <errno.h>
#include <sys/syscall.h>
#include <limits.h>

#include "common/mem.h"
#include "common/stat_collect.h"
#include "common/ConfigFile.h"
#include "docserver.h"
#include "protocol/protocol.h"
#include "execute/slave.h"
#include "storage/StorageEngineFactory.h"
#include "storage/StorageEngine.h"

#define CONNECTION_BUFFER_SIZE MB_SIZE
#define DEFAULT_BACKLOG (1024)
#define MAX_CONNECTION_NUM (1024)
#define DOC_SERVER_MMPOOL_SIZE (1UL << 27)  // document server memory pool capacity
#define DEFAULT_INSERT_THREAD_NUM (1)
#define DEFAULT_CONFIG_FILE "etc/docserver.conf"
#define SLAVE_CONNECT_INTERVAL (2)
#define DEFAULT_CONNECT_TIMEOUT (30)
#define TMP_BUFER_SIZE (1024)

struct docserver_config docserver;
int8_t exceed_cpu_used = 0;

// recv data callback
static void recv_data(const int fd, const short which, void *arg);
// send data callback
static void send_data(const int fd, const short which, void *arg);
static void pipe_notify_process(const int fd, const short which, void *arg);
static void connection_dispatch(const int fd, const short which, void *arg);
// exit signal callback
static void exit_signal_cb(const int fd, const short which, void *arg);
// log level signal callback
static void log_level_signal_cb(const int fd, const short which, void *arg);

// can only be called in the main thread(IO thread)
static void shutdown_server(void);

static int is_packet_timeouted(struct mile_packet *packet);

static void *packet_process_main(void *arg);
static void print_usage(void);

static int32_t create_listen_socket(int32_t port, struct event *ev, void (*cb)(int, short, void *) );
static int32_t set_nonblocking(int fd);

static void increase_connection_num(void);
static void decrease_connection_num(void);

static void record_packet_stat(struct mile_packet *packet);

void init_in_packet_queue(struct in_packet_queue *queue)
{
	CPU_ZERO(&queue->cpu_set);
	queue->packet_num = 0;
	INIT_LIST_HEAD(&queue->packet_list_h);
	pthread_mutex_init(&queue->mutex, NULL);
	pthread_cond_init(&queue->cond, NULL);
}

// destroy queue and free packets in queue
// called after working thread quit
void destroy_in_packet_queue(struct in_packet_queue *queue)
{
	struct mile_packet *packet = NULL;
	struct docserver_connect *conn = NULL;

	while (!list_empty(&queue->packet_list_h) ) {
		packet = list_entry(queue->packet_list_h.next, struct mile_packet, packet_list);
		--queue->packet_num;

		conn = packet->conn;
		list_del(&packet->packet_list);
		destroy_mile_packet(packet);
		--conn->packet_num;
		close_docserver_connect(conn);   // reclose is needed and no hurt
	}
}

// add packet to incoming packet queue
void add_in_packet(struct in_packet_queue *queue, struct mile_packet *packet)
{
	pthread_mutex_lock(&queue->mutex);
	list_add_tail(&packet->packet_list, &queue->packet_list_h);
	++queue->packet_num;
	pthread_cond_signal(&queue->cond);
	pthread_mutex_unlock(&queue->mutex);
}

// get packet from incoming packet queue
struct mile_packet *get_in_packet(struct in_packet_queue *queue)
{
	struct mile_packet *packet = NULL;

	pthread_mutex_lock(&queue->mutex);
	while (g_running_flag && list_empty(&queue->packet_list_h) ) {
		pthread_cond_wait(&queue->cond, &queue->mutex);
	}
	if (!g_running_flag) {
		pthread_mutex_unlock(&queue->mutex);
		return NULL;
	}
	packet = list_entry(queue->packet_list_h.next, struct mile_packet, packet_list);
	list_del(&packet->packet_list);
	--queue->packet_num;
	pthread_mutex_unlock(&queue->mutex);
	return packet;
}

void wake_up_all(struct in_packet_queue *queue)
{
	pthread_mutex_lock(&queue->mutex);
	pthread_cond_broadcast(&queue->cond);
	pthread_mutex_unlock(&queue->mutex);
}

int init_out_packet_queue(struct out_packet_queue *queue)
{
	INIT_LIST_HEAD(&queue->packet_list_h);
	queue->notify_fds[0] = queue->notify_fds[1] = -1;
	if (pipe(queue->notify_fds) != 0) {
		log_error("pipe failed, errno %d", errno);
		return -1;
	}
	// set read point to nonblock
	if (set_nonblocking(queue->notify_fds[0]) < 0) {
		return -1;
	}
	pthread_mutex_init(&queue->mutex, NULL);
	return 0;
}

// destroy queue and free packets in queue
// called after working thread quit
void destroy_out_packet_queue(struct out_packet_queue *queue)
{
	struct mile_packet *packet = NULL;
	struct docserver_connect *conn = NULL;

	while (!list_empty(&queue->packet_list_h) ) {
		packet = list_entry(queue->packet_list_h.next, struct mile_packet, packet_list);

		conn = packet->conn;
		list_del(&packet->packet_list);
		record_packet_stat(packet);
		destroy_mile_packet(packet);
		--conn->packet_num;
		close_docserver_connect(conn);   // reclose is needed and no hurt
	}

	close(queue->notify_fds[0]);
	close(queue->notify_fds[1]);
	queue->notify_fds[0] = queue->notify_fds[1] = -1;
}

// add packet to output queue
int add_out_packet(struct out_packet_queue *queue, struct mile_packet *packet)
{
	int ret = 0;

	pthread_mutex_lock(&queue->mutex);
	list_add_tail(&packet->packet_list, &queue->packet_list_h);
	pthread_mutex_unlock(&queue->mutex);
	if ( ( ret = write(queue->notify_fds[1], "S", 1) ) != 1) {
		log_error("write to notify fd failed, fd %d, ret %d, errno %d", queue->notify_fds[1], ret, errno);
		shutdown_server();
		ret = -1;
	}
	else
		ret = 0;
	return ret;
}

// get packet from output queue
struct mile_packet *get_out_packet(struct out_packet_queue *queue)
{
	struct mile_packet *packet = NULL;
	int ret = 0;
	char buf[1];

	if ( ( ret = read(queue->notify_fds[0], buf, 1) ) != 1) {
		log_error("read notify fd failed, fd %d, ret %d, errno %d", queue->notify_fds[0], ret, errno);
		return NULL;
	}
	pthread_mutex_lock(&queue->mutex);
	if (!list_empty(&queue->packet_list_h) ) {
		packet = list_entry(queue->packet_list_h.next, struct mile_packet, packet_list);
		list_del(&packet->packet_list);
	}
	pthread_mutex_unlock(&queue->mutex);
	return packet;
}

// get packet array from output queue
int get_out_packet_array(struct out_packet_queue *queue, struct mile_packet **packet_ptrs, int array_size)
{
	// read at most array_size bytes from notify fd
	char buf[TMP_BUFER_SIZE];
	int num = 0, ret = 0, len = 0;

	do {
		len = array_size - num < TMP_BUFER_SIZE ? array_size - num : TMP_BUFER_SIZE;
		if ( (ret = read(queue->notify_fds[0], buf, len) ) <= 0) {
			if (!(EAGAIN == errno || EWOULDBLOCK == errno || EINTR == errno) || 0 == ret) {
				log_error("read notify fd failed, fd %d, ret %d, errno %d", queue->notify_fds[0], ret, errno);
				return -1;
			}
			break;
		}
		num += ret;
	} while (num < array_size && ret == len);

	assert(num <= array_size);

	pthread_mutex_lock(&queue->mutex);
	// returned packets can more than num.
	for (ret = 0; ret < array_size && !list_empty(&queue->packet_list_h); ++ret) {
		packet_ptrs[ret] = list_entry(queue->packet_list_h.next, struct mile_packet, packet_list);
		list_del(&packet_ptrs[ret]->packet_list);
	}
	pthread_mutex_unlock(&queue->mutex);
	return ret;
}

// create connection and add fd's read and write event to event_base
struct docserver_connect *new_docserver_connect(struct event_base *base, int fd)
{
	assert(NULL != base && fd > 0);

	struct docserver_connect *conn = (struct docserver_connect *)malloc(sizeof( struct docserver_connect ) );
	if (NULL == conn)
		return conn;
	conn->fd = fd;
	if ( ( conn->rbuf = (char *)malloc(CONNECTION_BUFFER_SIZE) ) == NULL) {
		free(conn);
		log_error("malloc failed, errno %d", errno);
		return NULL;
	}
	conn->rbuf_size = CONNECTION_BUFFER_SIZE;
	conn->rbytes = 0;

	if ( ( conn->sbuf = (char *)malloc(CONNECTION_BUFFER_SIZE) ) == NULL) {
		free(conn->rbuf);
		free(conn);

		log_error("malloc failed, errno %d", errno);
		return NULL;
	}

	conn->sbuf_size = CONNECTION_BUFFER_SIZE;
	conn->sbytes = 0;

	conn->packet_num = 0;
	INIT_LIST_HEAD(&conn->send_packet_list_h);

	conn->bl_reader = NULL;
	conn->session_mem = mem_pool_init(MB_SIZE);

	// add read and write event
	event_assign(&conn->read_event, base, fd, EV_READ | EV_PERSIST, recv_data, conn);
	event_assign(&conn->write_event, base, fd, EV_WRITE | EV_PERSIST, send_data, conn);
	event_add(&conn->read_event, NULL);

	log_debug("new connection: %p", conn);
	list_add_tail(&conn->conn_list, &docserver.conn_list_h);
	increase_connection_num();

	return conn;
}

static void increase_connection_num()
{
	++docserver.total_conn_num;
	if (docserver.total_conn_num == docserver.max_conn_num) {
		// remove listen socket's event
		event_del(&docserver.listen_event);
	}
	if (docserver.total_conn_num >= docserver.max_conn_num) {
		log_warn("connections %d reach max connection limit %d", docserver.total_conn_num, docserver.max_conn_num);
	}
	log_debug("current connections: %d", docserver.total_conn_num);
}

static void decrease_connection_num(void)
{
	if (docserver.total_conn_num == docserver.max_conn_num) {
		// add listen socket's event
		event_add(&docserver.listen_event, NULL);
	}
	--docserver.total_conn_num;
	log_debug("current connections: %d", docserver.total_conn_num);
}

// free associated memory
void destroy_docserver_connect(struct docserver_connect *conn)
{
	list_del(&conn->conn_list);

	if (NULL != conn->bl_reader) {
		binlog_reader_destroy((BL_READER_PTR)conn->bl_reader);
		conn->bl_reader = NULL;
	}

	if (NULL != conn->session_mem) {
		mem_pool_destroy(conn->session_mem);
		conn->session_mem = NULL;
	}

	struct mile_packet *packet = NULL;
	while (!list_empty(&conn->send_packet_list_h) ) {
		packet = list_entry(conn->send_packet_list_h.next, struct mile_packet, packet_list);
		list_del(&packet->packet_list);
		packet->out_send_queue_time = get_time_usec();
		record_packet_stat(packet);
		destroy_mile_packet(packet);
	}

	free(conn->rbuf);
	free(conn->sbuf);
	log_debug("free connection: %p", conn);
	free(conn);
}

// close socket fd, delete event from event base,
// destroy connection if no packet associated with this connection
void close_docserver_connect(struct docserver_connect *conn)
{
	if (conn->fd >= 0) {
		event_del(&conn->read_event);
		event_del(&conn->write_event);   // no effect if write_event has never been added.
		close(conn->fd);
		conn->fd = -1;
		decrease_connection_num();
	}
	if (conn->packet_num <= 0)
		destroy_docserver_connect(conn);
}

static void recv_data(const int fd, const short which, void *arg)
{
	struct docserver_connect *conn = (struct docserver_connect *)arg;

	assert(fd == conn->fd);

	int32_t n = read(fd, conn->rbuf + conn->rbytes, conn->rbuf_size - conn->rbytes);
	if (n < 0) {  // error
		log_error("read failed, fd %d, errno %d", fd, errno);
		close_docserver_connect(conn);
		return;
	}
	else if (0 == n) {  // connection closed by peer
		log_info("connect closed, fd %d", fd);
		close_docserver_connect(conn);
		return;
	}

	conn->rbytes += n;
	struct mile_packet *packet = NULL;
	uint32_t parsed_len = 0;
	for (n = 0; n < (int32_t)conn->rbytes
			&& (packet = pre_parse_packet(conn->rbuf + n, conn->rbytes - n, &parsed_len) ) != NULL; n += parsed_len) {
		log_debug("get new packet");
		packet->conn = conn;

		if (MT_MD_EXE_INSERT == packet->message_type) {
			add_in_packet(&docserver.insert_queue, packet);
		}
		else {
			add_in_packet(&docserver.query_queue, packet);
		}
		++conn->packet_num;
	}

	if (n > 0) {
		memmove(conn->rbuf, conn->rbuf + n, conn->rbytes - n);
		conn->rbytes -= n;
		return;
	}

	// packet larger than data read buffer
	if (conn->rbytes == conn->rbuf_size) {
		uint32_t packet_len = convert_int32(*(uint32_t *)conn->rbuf);
		log_info("packet larger than data buffer, buffer size %u, adjust buffer size to %u",
				conn->rbuf_size, packet_len);

		char *buf = (char *)malloc(packet_len);
		if (NULL == buf) {
			log_error("malloc failed, errno %d, close connection, fd %d", errno, conn->fd);
			close_docserver_connect(conn);
		}

		memcpy(buf, conn->rbuf, conn->rbytes);
		free(conn->rbuf);
		conn->rbuf = buf;
		conn->rbuf_size = packet_len;
	}

	return;
}

// move send packets' data to send buffer
static void adjust_send_buffer(struct docserver_connect *conn)
{
	struct mile_packet *packet = NULL;

	while (!list_empty(&conn->send_packet_list_h) ) {
		packet = list_entry(conn->send_packet_list_h.next, struct mile_packet, packet_list);
		// never add to send buffer and timouted
		if (0 == packet->sbuf->rpos && is_packet_timeouted(packet) ) {
			list_del(&packet->packet_list);
			packet->out_send_queue_time = get_time_usec();
			record_packet_stat(packet);
			destroy_mile_packet(packet);
			continue;
		}
		if (conn->sbytes + packet->sbuf->data_len - packet->sbuf->rpos > conn->sbuf_size) {
			if (0 == conn->sbytes) {
				// packet larger than send buffer.
				log_debug("packet larger than send buffer");
				memcpy(conn->sbuf, packet->sbuf->data + packet->sbuf->rpos, conn->sbuf_size);
				conn->sbytes = conn->sbuf_size;
				packet->sbuf->rpos += conn->sbuf_size;
				return;
			}
			break;
		}
		memcpy(conn->sbuf + conn->sbytes, packet->sbuf->data + packet->sbuf->rpos,
				packet->sbuf->data_len - packet->sbuf->rpos);
		conn->sbytes += packet->sbuf->data_len - packet->sbuf->rpos;
		packet->sbuf->rpos = packet->sbuf->data_len;
		list_del(&packet->packet_list);
		packet->out_send_queue_time = get_time_usec();
		record_packet_stat(packet);
		destroy_mile_packet(packet);
	}
}

static void send_data_writev(const int fd, const short which, void *arg)
{
	struct docserver_connect *conn = (struct docserver_connect *)arg;
	assert(fd == conn->fd);

	// limit to 1024 in case of stack overflow, 1024 is enough
	const static int vec_buf_size = IOV_MAX > 1024 ? 1024 : IOV_MAX;
	struct iovec vec_buf[vec_buf_size];

	// read from /proc/sys/net/core/wmem_* ?
	const static int max_bytes_per_write = 256 << 10; // 256KB

	// fill vec_buf
	struct mile_packet *packet = NULL;
	int vec_n = 0;
	int bytes = 0;
	list_head *head = &conn->send_packet_list_h;
	list_head *node = head->next;

	while (node != head && vec_n < vec_buf_size && bytes < max_bytes_per_write) {
		packet = list_entry(node, struct mile_packet, packet_list);
		// never sent and timouted or no data to send
		if (0 == packet->sbuf->rpos && (is_packet_timeouted(packet) || packet->sbuf->data_len == 0)) {
			node = node->next;
			list_del(&packet->packet_list);
			packet->out_send_queue_time = get_time_usec();
			record_packet_stat(packet);
			destroy_mile_packet(packet);
			continue;
		}

		vec_buf[vec_n].iov_base = packet->sbuf->data + packet->sbuf->rpos;
		vec_buf[vec_n].iov_len = packet->sbuf->data_len - packet->sbuf->rpos;
		
		bytes += vec_buf[vec_n++].iov_len;
		node = node->next;
	}
	if (0 == vec_n) {
		event_del(&conn->write_event);
		return;
	}
	assert(bytes > 0);

	// send data
	int n = writev(fd, vec_buf, vec_n);
	if (n <= 0) {
		log_error("send failed, fd %d, errno %d", fd, errno);
		close_docserver_connect(conn);
		return;
	}
	log_debug("data length %u, send size %d", bytes, n);

	// update packet list
	node = head->next;
	while (n > 0) {
		packet = list_entry(node, struct mile_packet, packet_list);
		int len = packet->sbuf->data_len - packet->sbuf->rpos;
		if (n >= len) {
			n -= len;
			// remove packet
			node = node->next;
			list_del(&packet->packet_list);
			packet->out_send_queue_time = get_time_usec();
			record_packet_stat(packet);
			destroy_mile_packet(packet);
		} else {
			packet->sbuf->rpos += n;
			break;
		}
	}

	if (list_empty(head))
		event_del(&conn->write_event);
	return;
}

static void send_data(const int fd, const short which, void *arg)
{
	struct docserver_connect *conn = (struct docserver_connect *)arg;

	assert(fd == conn->fd);

	adjust_send_buffer(conn);
	if (0 == conn->sbytes) {
		event_del(&conn->write_event);
		return;
	}

	int n = write(fd, conn->sbuf, conn->sbytes);
	if (n <= 0) {
		log_error("send failed, fd %d, errno %d", fd, errno);
		close_docserver_connect(conn);
		return;
	}

	log_debug("data length %u, send size %d", conn->sbytes, n);
	memmove(conn->sbuf, conn->sbuf + n, conn->sbytes - n);
	conn->sbytes -= n;
	adjust_send_buffer(conn);
	if (0 == conn->sbytes) {
		event_del(&conn->write_event);
		return;
	}
}

static int is_packet_timeouted(struct mile_packet *packet)
{
	uint64_t now = get_time_msec();
	int ret = ( packet->timeout != 0 && now - packet->launch_time / 1000 > packet->timeout );

	if (ret) {
		log_warn("packet timeouted launch_time %" PRIu64 ", now %" PRIu64 ", used time %" PRIu64 ", "
				"packet timeout %u, discards this packet",
				packet->launch_time / 1000, now, now - packet->launch_time / 1000, packet->timeout);
	}
	return ret;
}


void pipe_notify_process(const int fd, const short which, void *arg)
{
	struct out_packet_queue *queue = (struct out_packet_queue *)arg;
	struct mile_packet *packet_ptr_array[TMP_BUFER_SIZE];
	int n = get_out_packet_array(queue, packet_ptr_array, TMP_BUFER_SIZE);

	if (n < 0) {
		log_error("get output packets failed, shutdown server");
		shutdown_server();
	}
	int i;
	for (i = 0; i < n; i++) {
		struct mile_packet *packet = packet_ptr_array[i];
		struct docserver_connect *conn = packet->conn;
		--conn->packet_num;

		if (conn->fd < 0) {  // connection closed
			log_info("connection has been closed, discard this packet");
		}
		else if (is_packet_timeouted(packet) ) {
		}
		else if (NULL == packet->sbuf) {
			log_info("bad packet, discard");
		}
		else if (packet->sbuf->data_len > docserver.max_result_size && docserver.max_result_size > 0) {
			log_error("result length %u larger than result limit %d", packet->sbuf->data_len, docserver.max_result_size);
		}
		else {
			// add write event if needed.
			if (list_empty(&conn->send_packet_list_h) && 0 == conn->sbytes) {
				log_debug("add write event");
				event_add(&conn->write_event, NULL);
			}
			list_add_tail(&packet->packet_list, &conn->send_packet_list_h);
			continue;
		}

		packet->out_send_queue_time = get_time_usec();
		record_packet_stat(packet);
		destroy_mile_packet(packet);
		if (conn->fd < 0)  // connection closed
			close_docserver_connect(conn);   // reclose is needed and no hurt
	}
}

static void *packet_process_main(void *arg)
{
	log_info("packet process thread start");

	// set cpu affinity
	struct in_packet_queue *queue = (struct in_packet_queue *)arg;
	if (docserver.cpu_num > docserver.insert_thread_num) {
		if (sched_setaffinity( (pid_t)syscall(SYS_gettid), sizeof(cpu_set_t), &queue->cpu_set) < 0) {
			log_error("set cpu affinity failed, errno %d", errno);
		}
	}

	struct mile_packet *packet = NULL;

	int ret = 0;
	while ( ( packet = get_in_packet(queue) ) != NULL) {
		if (packet->conn->fd < 0) {  // connection closed when packet in incoming queue
		}
		else {
			packet->start_process_time = get_time_usec();
			if ((ret = parse_mile_message(packet, &packet->conn->bl_reader, exceed_cpu_used, packet->conn->session_mem))
					!= MILE_RETURN_SUCCESS) {
				log_warn("packet process failed, return value %d, error msg [%s]", ret, error_msg(ret));
			}
		}

		packet->in_send_queue_time = get_time_usec();
		if (add_out_packet(&docserver.out_queue, packet) != 0) {
			log_error("append packet to output queue failed, server will be shutdown");
		}
	}
	log_info("packet process thread stop.");
	return NULL;
}

void init_docserver_config(struct docserver_config *doc)
{
	doc->port = DOCSERVER_PORT;
	doc->sync_port = DOCSERVER_PORT + 1;
	doc->backlog = DEFAULT_BACKLOG;
	doc->max_conn_num = MAX_CONNECTION_NUM;
	doc->total_conn_num = 0;
	doc->max_result_size = DEFAULT_MAX_RESULT_SIZE;

	INIT_LIST_HEAD(&doc->conn_list_h);

	doc->role = MASTER_ROLE;

	init_in_packet_queue(&doc->insert_queue);
	init_in_packet_queue(&doc->query_queue);

	if (init_out_packet_queue(&doc->out_queue) != 0)
		exit(EXIT_FAILURE);

	if ( ( doc->event_base = event_base_new() ) == NULL)
		exit(EXIT_FAILURE);

	doc->insert_thread_num = DEFAULT_INSERT_THREAD_NUM;
	doc->query_thread_num = DEFAULT_THREAD_NUM - doc->insert_thread_num;

	doc->cpu_threshold = 0;
	doc->cpu_num = sysconf(_SC_NPROCESSORS_CONF);

	event_assign(&doc->pipe_notify_event, doc->event_base, doc->out_queue.notify_fds[0], EV_READ | EV_PERSIST,
			pipe_notify_process, &doc->out_queue);
	event_add(&doc->pipe_notify_event, NULL);

	doc->mem_pool = mem_pool_init(DOC_SERVER_MMPOOL_SIZE);
	if (NULL == doc->mem_pool)
		exit(EXIT_FAILURE);
}

// free associated memory
void destroy_docserver_config(struct docserver_config *doc)
{
	destroy_in_packet_queue(&doc->insert_queue);
	destroy_in_packet_queue(&doc->query_queue);
	destroy_out_packet_queue(&doc->out_queue);

	struct docserver_connect *conn = NULL;
	while (!list_empty(&doc->conn_list_h) ) {
		conn = list_entry(doc->conn_list_h.next, struct docserver_connect, conn_list);
		close_docserver_connect(conn);
	}

	close(EVENT_FD(&doc->listen_event) );

	event_base_free(doc->event_base);
	mem_pool_destroy(doc->mem_pool);
}

static void print_usage(void)
{
	printf("Mile docment server.\nVersion: %d.%d\nBuild time: %s %s\n",
			MILE_DOC_SERVER_MAJOR_VER, MILE_DOC_SERVER_MINOR_VER, __DATE__, __TIME__);
#ifdef SVN_REVISION
	printf("SVN Revision: %d\n", SVN_REVISION);
#endif
	printf("Options:\n-p <num>      TCP port number to listen on (default: 18518)\n"
			"-d <ip_addr>  interface to listen on (default: INADDR_ANY, all addresses)\n"
			"-h            print this help and exit\n");
	printf("-t <num>      number of threads to use (default: 4)\n");
	printf("-m <ip_addr>  master ip address\n");
	printf("-r <num>      role: 0 -- master; 1 -- slave (default: master)\n");
	printf("-f <configure file>      configure file (default: %s)\n", DEFAULT_CONFIG_FILE);

	return;
}

static int32_t set_nonblocking(int fd)
{
	int flags = 0;

	if ( ( flags = fcntl(fd, F_GETFL, 0) ) < 0 ||
			fcntl(fd, F_SETFL, flags | O_NONBLOCK) < 0) {
		log_error("set non blocking failed, errno %d", errno);
		return -1;
	}
	return 0;
}

static int32_t create_listen_socket(int32_t port, struct event *ev, void (*cb)(int, short, void *) )
{
	int fd;

	if ((fd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		log_error("socket failure, errno %d", errno);
		return -1;
	}

	if (set_nonblocking(fd) != 0) {
		log_error("setting O_NONBLOCK");
		close(fd);
		return -1;
	}

	int flags = 1;
	if (setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (void *)&flags, sizeof(flags)) != 0) {
		log_error("setsockopt %d SO_REUSEADDR failed, errno %d", fd, errno);
	}

	struct sockaddr_in addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);
	if (docserver.ip.empty()) {
		addr.sin_addr.s_addr = INADDR_ANY;
	}
	else {
		if (!inet_aton(docserver.ip.c_str(), (struct in_addr *)&addr.sin_addr.s_addr)) {
			log_error("ip address");
			return -1;
		}
	}

	bzero(&(addr.sin_zero), 8);
	if (bind(fd, (struct sockaddr *)&addr, sizeof(struct sockaddr)) == -1) {
		log_error("bind failed, fd %d, errno %d", fd, errno);
		close(fd);
		return -1;
	}

	if (listen(fd, docserver.backlog) == -1) {
		log_error("listen failed, fd %d, errno %d", fd, errno);
		close(fd);
		return -1;
	}

	// add listen event.
	event_assign(ev, docserver.event_base, fd, EV_READ | EV_PERSIST, cb, ev);
	event_add(ev, NULL);

	return 0;
}

void connection_dispatch(const int fd, const short which, void *arg)
{
	int client = 0;
	struct sockaddr_in client_addr;
	socklen_t addr_len = sizeof( client_addr );

	if ( ( client = accept(fd, (struct sockaddr *)&client_addr, &addr_len) ) < 0) {
		if (EAGAIN == errno || EWOULDBLOCK == errno || EINTR == errno)
			return;
		log_info("accept failed, fd %d, errno %d", fd, errno);
		if (EMFILE == errno) {
			// TODO : close a fd, accept again, then close accepted fd, or just ignore this error.
		}
		shutdown_server();
	}
	log_info("accept client: %s:%hu", inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port) );
	if (set_nonblocking(client) != 0) {
		log_error("set nonblocking failed, fd %d", client);
		close(client);
	}

	int flags = 1;
	if (setsockopt(client, IPPROTO_TCP, TCP_NODELAY, (void *)&flags, sizeof(flags)) != 0) {
		log_error("setsockopt %d TCP_NODELAY failed, errno %d", client, errno);
		close(client);
	}

	struct docserver_connect *conn = new_docserver_connect(docserver.event_base, client);
	if (NULL == conn) {
		log_error("create docserver connection failed, may be out of memory");
		close(client);
		shutdown_server();
	}

	return;
}

static void exit_signal_cb(const int fd, const short which, void *arg)
{
	log_info("got signal %d, exit event loop", EVENT_SIGNAL((struct event *)arg));
	shutdown_server();
}

static void log_level_signal_cb(const int fd, const short which, void *arg)
{
	int sig = EVENT_SIGNAL((struct event *)arg);

	if (sig == 41) {
		g_level++;
	} else if(sig == 42) {
		g_level--;
	}

	log_error("change log level to %d", g_level);
}

static void shutdown_server()
{
	g_running_flag = 0;
	event_base_loopbreak(docserver.event_base);
}

int parse_options(ConfigFile *conf, char *config_file, int argc, char **argv)
{
	// reset optind
	optind = 1;

	int opt;
	while (-1 != (opt = getopt(argc, argv,
						"p:" /* TCP port number to listen on */
						"r:" /* role: 0 -- master; 1 -- slave (default: master) */
						"d:" /* interface to listen on */
						"m:" /* master ip address */
						"P:" /* ha port */
						"t:" /* number of threads to use */
						"h" /* print this help and exit */
						"c:" /* connect total */
						"f:" /* schema file name */
						))) {

		switch (opt) {
		case 'p':
			docserver.port = atoi(optarg);
			conf->SetValue(CONF_SERVER_SESSION, "port", optarg);
			break;
		case 'P':
			docserver.sync_port = atoi(optarg);
			conf->SetValue(CONF_SERVER_SESSION, "sync_port", optarg);
			break;
		case 'd':
			docserver.ip = optarg;
			conf->SetValue(CONF_SERVER_SESSION, "ip", optarg);
		case 'm':
			docserver.sync_addr = optarg;
			conf->SetValue(CONF_SERVER_SESSION, "sync_addr", optarg);
			break;
		case 'r':
			docserver.role = atoi(optarg);
			if (docserver.role == MASTER_ROLE)
				conf->SetValue(CONF_SERVER_SESSION, "role", "master");
			else
				conf->SetValue(CONF_SERVER_SESSION, "role", "slave");
			break;
		case 't':
			docserver.query_thread_num = atoi(optarg) - docserver.insert_thread_num;
			conf->SetValue(CONF_SERVER_SESSION, "thread_num", optarg);
			break;
		case 'c':
			docserver.max_conn_num = atoi(optarg);
			conf->SetValue(CONF_SERVER_SESSION, "max_conn_num", optarg);
			break;
		case 'h':
			print_usage();
			exit(EXIT_SUCCESS);
			break;
		case 'f':
			strcpy(config_file, optarg);
			break;
		default:
			log_error("Illegal argument \"%c\"\n", opt);
			print_usage();
			return -1;
		}
	}
	return 0;
}


int cal_cpuoccupy(struct cpu_occupy *o, struct cpu_occupy *n)
{
	unsigned long od, nd;
	unsigned long id, sd;
	int cpu_use = 0;

	od = (unsigned long)(o->user + o->nice + o->system + o->idle);
	nd = (unsigned long)(n->user + n->nice + n->system + n->idle);

	id = (unsigned long)(n->user - o->user);
	sd = (unsigned long)(n->system - o->system);
	if ((nd - od) != 0)
		cpu_use = (int)((sd + id) * 10000) / (nd - od);
	else
		cpu_use = 0;
	return cpu_use;
}

void get_cpuoccupy(int16_t cpu_num, struct cpu_occupy *cpust)
{
	char buff[256];
	FILE *fp;
	int16_t i;

	fp = fopen("/proc/stat", "r");
	fgets(buff, sizeof(buff), fp);

	for (i = 0; i < cpu_num; i++) {
		fgets(buff, sizeof(buff), fp);
		sscanf(buff, "%s %u %u %u %u",  (cpust + i)->name,
				&(cpust + i)->user,
				&(cpust + i)->nice,
				&(cpust + i)->system,
				&(cpust + i)->idle);
	}
	fclose(fp);
	return;
}


void *cal_cpu_occupy(void *arg)
{
	int16_t cpu_num = docserver.cpu_num;
	int16_t cpu_threshold = docserver.cpu_threshold;
	int16_t i;
	MEM_POOL *mem_pool = mem_pool_init(KB_SIZE);
	int16_t exceed_count;
	int16_t cpu_used;
	struct cpu_occupy *os = (struct cpu_occupy *)mem_pool_malloc(mem_pool,
			sizeof(struct cpu_occupy) * cpu_num);

	memset(os, 0, sizeof(struct cpu_occupy) * cpu_num);

	struct cpu_occupy *ns = (struct cpu_occupy *)mem_pool_malloc(mem_pool,
			sizeof(struct cpu_occupy) * cpu_num);
	memset(ns, 0, sizeof(struct cpu_occupy) * cpu_num);


	while (g_running_flag) {
		exceed_count = 0;
		get_cpuoccupy(cpu_num, os);

		MILE_SLEEP(1);
		get_cpuoccupy(cpu_num, ns);

		for (i = 0; i < cpu_num; i++) {
			cpu_used = cal_cpuoccupy(os + i, ns + i);
			if (cpu_used > cpu_threshold) {
				exceed_count++;
				log_warn("cpu:%s used:%d threshold:%d", (os + i)->name, cpu_used, cpu_threshold);
			}
		}

		if (exceed_count >= cpu_num - 1)
			Mile_AtomicSetPtr(&exceed_cpu_used, 1);
		else
			Mile_AtomicSetPtr(&exceed_cpu_used, 0);

	}

	mem_pool_destroy(mem_pool);
	return NULL;
}

// timeouted connect
static int connect_to_addr(const char *host, uint32_t port)
{
	if (NULL == host) {
		log_error("host is empty");
		return -1;
	}

	struct sockaddr_in addr;
	memset(&addr, 0, sizeof(addr));
	addr.sin_family = AF_INET;
	addr.sin_port = htons(port);

	char buf[TMP_BUFER_SIZE];
	struct hostent hentry;
	int herrno = 0;
	struct hostent *hp;

	if (gethostbyname_r(host, &hentry, buf, sizeof(buf), &hp, &herrno) != 0) {
		if (inet_aton(host, &addr.sin_addr) == 0) {
			log_error("gethostbyname failed, host %s, port %d, errno %d", host, port, errno);
			return -1;
		}
	}
	else {
		memcpy(&addr.sin_addr, hentry.h_addr_list[0], sizeof(struct in_addr) );
	}

	int sock = socket(AF_INET, SOCK_STREAM, 0);
	if (sock < 0) {
		log_error("create socket failed, host %s, port %d, errno %d", host, port, errno);
		return -1;
	}

	// set send timeout
	const static int interval = 200000;
	struct timeval tv;
	tv.tv_sec = 0;
	tv.tv_usec = interval;
	setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char *)&tv, sizeof(struct timeval));

	uint64_t total_time = 0;
	while (connect(sock, (struct sockaddr *)&addr, sizeof(addr) ) < 0) {
		if (EAGAIN == errno || EINTR == errno || EINPROGRESS == errno || EALREADY == errno || EINTR == errno) {
			if (!g_running_flag) {
				close(sock);
				return -1;
			}
			total_time += interval;
			if (total_time < DEFAULT_CONNECT_TIMEOUT * 1000000LL) {
				continue;
			}
		}
		log_error("connect to %s:%d failed, errno %d", host, port, errno);
		close(sock);
		return -1;
	}

	// reset send timeout to default
	tv.tv_sec = tv.tv_usec = 0;
	setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char *)&tv, sizeof(struct timeval));

	return sock;
}

static void *slave_client_handler(void *arg)
{
	// STATIC_ASSERT(MB_SIZE > MAX_BINLOG_DATA_PER_PACKET + sizeof( struct slave_sync_res ), MEM_POOL_SIZE_SHOULD_BIGER_THAN_PACKAGE_SIZE);

	MEM_POOL_PTR mem = mem_pool_init(MB_SIZE);
	struct data_buffer buffer;
	memset(&buffer, 0, sizeof( buffer ) );

	buffer.data = (uint8_t *)malloc(MAX_BINLOG_DATA_PER_PACKET * 10);
	buffer.data_len = MAX_BINLOG_DATA_PER_PACKET * 10;

	uint32_t message_id = 0;
	while (g_running_flag) {
		int fd = connect_to_addr(docserver.sync_addr.c_str(), docserver.sync_port);
		if (fd < 0) {
			log_error("connect to %s:%u failed, retry after %d second",
					docserver.sync_addr.c_str(), docserver.sync_port, SLAVE_CONNECT_INTERVAL);
			MILE_SLEEP(SLAVE_CONNECT_INTERVAL);
			continue;
		}
		if (!g_running_flag) {
			close(fd);
			break;
		}

		uint64_t cur_offset = StorageEngine::storage->SlaveSyncPos();
		buffer.rpos = buffer.wpos = 0;

		int rc = sync_with_master(fd, cur_offset, &buffer, &message_id, mem);
		if (rc < 0) {  // rc always >= 0 , if server still running.
			close(fd);
		}
	}
	log_info("slave client handler exit.");
	mem_pool_destroy(mem);
	free(buffer.data);
	return NULL;
}

// t : IN_QUEUE / PROCESS / OUT_QUEUE
#define RECORD_PACKET_STAT_HEALPER(t, p, v) do { \
		if (MT_MD_EXE_INSERT == (p)->message_type)												  \
			sc_record_value(STAT_ITEM_ ## t ## _INSERT, (v));														  \
		else		  \
			sc_record_value(STAT_ITEM_ ## t ## _QUERY, (v));														\
} while (0)

static void record_packet_stat(struct mile_packet *packet)
{
	if (packet->start_process_time > 0) {
		RECORD_PACKET_STAT_HEALPER(RECV_QUEUE, packet, packet->start_process_time - packet->launch_time);
		RECORD_PACKET_STAT_HEALPER(PROCESS, packet, packet->in_send_queue_time - packet->start_process_time);
	}
	else if (packet->in_send_queue_time)
		RECORD_PACKET_STAT_HEALPER(RECV_QUEUE, packet, packet->in_send_queue_time - packet->launch_time);

	if (packet->out_send_queue_time > 0)
		RECORD_PACKET_STAT_HEALPER(SEND_QUEUE, packet, packet->out_send_queue_time - packet->in_send_queue_time);
}
#undef RECORD_PACKET_STAT_HEALPER

int main(int argc, char *argv[])
{
	init_docserver_config(&docserver);
	char config_file_name[FILENAME_MAX_LENGTH] = DEFAULT_CONFIG_FILE;
	MEM_POOL_PTR mem_pool_config = mem_pool_init(MB_SIZE);
	ConfigFile *conf = ConfigFile::GlobalInstance();

	// parse options
	if (parse_options(conf, config_file_name, argc, argv) != 0) {
		exit(EXIT_FAILURE);
	}

	// parse config file
	if (conf->LoadFile(config_file_name) != 0) {
		log_error("parse configure failed.");
		exit(EXIT_FAILURE);
	}

	const char *value = conf->GetValue(CONF_SERVER_SESSION, "role", "master");
	if (strcmp(value, "master") == 0)
		docserver.role = MASTER_ROLE;
	else if (strcmp(value, "slave") == 0)
		docserver.role = SLAVE_ROLE;
	else {
		log_error("wrong role: %s", value);
		exit(EXIT_FAILURE);
	}

	docserver.port = conf->GetIntValue(CONF_SERVER_SESSION, "port", docserver.port);
	docserver.sync_port = conf->GetIntValue(CONF_SERVER_SESSION, "sync_port", docserver.sync_port);
	docserver.sync_addr = conf->GetValue(CONF_SERVER_SESSION, "sync_addr", "");
	docserver.query_thread_num = conf->GetIntValue(CONF_SERVER_SESSION, "thread_num", 64) - docserver.insert_thread_num;
	docserver.cpu_threshold = conf->GetIntValue(CONF_SERVER_SESSION, "cpu_threshold", docserver.cpu_threshold);
	docserver.max_result_size = conf->GetIntValue(CONF_SERVER_SESSION, "max_result_size", docserver.max_result_size);

	// parse options again and overwrite config values
	if (parse_options(conf, config_file_name, argc, argv) != 0) {
		exit(EXIT_FAILURE);
	}

	if (docserver.role == SLAVE_ROLE && (docserver.sync_addr.empty())) {
		log_error("pls set master ip address");
		print_usage();
		exit(EXIT_FAILURE);
	}

	// init log && profile
	char *log_dir = strdup(conf->GetValue(CONF_SERVER_SESSION, "log_dir", "./"));
	mkdirs(log_dir);
	init_log(conf->GetValue(CONF_SERVER_SESSION, "log_level", "INFO"), log_dir);

	log_info("docserver start");

	// init stat collect
	int perf_interval = conf->GetIntValue(CONF_SERVER_SESSION, "perf_interval", 30);
	stat_collect_init(perf_interval * 1000);

	// init g_load_threshold
	value = conf->GetValue(CONF_SERVER_SESSION, "load_threshold", "0");
	g_load_threshold = atof(value);

	// db init
	StorageEngine *storage = StorageEngineFactory::CreateEngine(*conf);
	if (NULL == storage || storage->Init() != 0) {
		log_error("init storage engine failed");
		exit(EXIT_FAILURE);
	}
	StorageEngine::storage = storage;
	log_debug("init mile db");

	// init working thread.
	// FIXME : set cpu affinity and schedule policy take no effect ??
	pthread_t *insert_threads = (pthread_t *)mem_pool_malloc(docserver.mem_pool, sizeof( pthread_t ) * docserver.insert_thread_num);
	pthread_t *query_threads = (pthread_t *)mem_pool_malloc(docserver.mem_pool, sizeof( pthread_t ) * docserver.query_thread_num);
	int32_t i;
	if (docserver.cpu_num > docserver.insert_thread_num) {
		// insert thread CPUs
		for (i = 0; i < docserver.insert_thread_num; i++) {
			CPU_SET(i, &docserver.insert_queue.cpu_set);
		}
		// query thread CPUs
		for (i = docserver.insert_thread_num; i < docserver.cpu_num; i++) {
			CPU_SET(i, &docserver.query_queue.cpu_set);
		}
	}

	// set schedule policy and priority
	pthread_attr_t insert_attr, query_attr;
	pthread_attr_init(&insert_attr);
	pthread_attr_setschedpolicy(&insert_attr, SCHED_RR);
	pthread_attr_init(&query_attr);
	pthread_attr_setschedpolicy(&query_attr, SCHED_RR);

	struct sched_param param;
	param.sched_priority = sched_get_priority_max(SCHED_RR);
	pthread_attr_setschedparam(&insert_attr, &param);

	// start working thread
	for (i = 0; i < docserver.insert_thread_num; i++) {
		if (pthread_create(insert_threads + i, &insert_attr, packet_process_main, &docserver.insert_queue) != 0) {
			log_error("pthread_create failed, errno %d", errno);
			exit(EXIT_FAILURE);
		}
	}
	for (i = 0; i < docserver.query_thread_num; i++) {
		if (pthread_create(query_threads + i, &query_attr, packet_process_main, &docserver.query_queue) != 0) {
			log_error("pthread_create failed, errno %d", errno);
			exit(EXIT_FAILURE);
		}
	}

	// create server listen socket
	if (create_listen_socket(docserver.port, &docserver.listen_event, connection_dispatch) != 0) {
		log_error("create docserver socket failed, port %u", docserver.port);
		exit(EXIT_FAILURE);
	}

	// create slave sync client thread
	pthread_t slave_sync_thread;
	if (docserver.role == SLAVE_ROLE) {
		if (pthread_create(&slave_sync_thread, NULL, slave_client_handler, NULL) != 0) {
			log_error("pthread_create failed, errno %d", errno);
			exit(EXIT_FAILURE);
		}
	}

	//  init exit signal handler
	struct event sig_int, sig_term, sig_41, sig_42;
	event_assign(&sig_int, docserver.event_base, SIGINT, EV_SIGNAL | EV_PERSIST, exit_signal_cb, &sig_int);
	event_assign(&sig_term, docserver.event_base, SIGTERM, EV_SIGNAL | EV_PERSIST, exit_signal_cb, &sig_term);
	event_assign(&sig_41, docserver.event_base, 41, EV_SIGNAL | EV_PERSIST, log_level_signal_cb, &sig_41);
	event_assign(&sig_42, docserver.event_base, 42, EV_SIGNAL | EV_PERSIST, log_level_signal_cb, &sig_42);
	event_add(&sig_int, NULL);
	event_add(&sig_term, NULL);
	event_add(&sig_41, NULL);
	event_add(&sig_42, NULL);

	// ignore signal: SIGPIPE, SIGQUIT, SIGHUP
	struct sigaction sigact;
	sigact.sa_handler = SIG_IGN;
	sigact.sa_flags = SA_RESTART; // restart system call.
	sigemptyset(&sigact.sa_mask);
	if (sigaction(SIGPIPE, &sigact, NULL) < 0
			|| sigaction(SIGQUIT, &sigact, NULL) < 0
			|| sigaction(SIGHUP, &sigact, NULL) < 0) {
		log_error("register signal handle failed");
		exit(EXIT_FAILURE);
	}

	// enter into main loop.
	event_base_loop(docserver.event_base, 0);

	/* event loop exit, wait working thread and clear up */

	// wake up working thead and join.
	wake_up_all(&docserver.insert_queue);
	wake_up_all(&docserver.query_queue);

	for (i = 0; i < docserver.insert_thread_num; i++)
		pthread_join(insert_threads[i], NULL);
	for (i = 0; i < docserver.query_thread_num; i++)
		pthread_join(query_threads[i], NULL);

	if (docserver.role == SLAVE_ROLE)
		pthread_join(slave_sync_thread, NULL);

	// db release
	log_debug("db release");
	delete StorageEngine::storage;
	StorageEngine::storage = NULL;

	// free memory
	destroy_docserver_config(&docserver);
	mem_pool_destroy(mem_pool_config);

	log_info("docserver stop");
	exit(EXIT_SUCCESS);
}

