// docserver.h : docserver
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-05-17

#ifndef DOCSERVER_H
#define DOCSERVER_H

#include <event.h>
#include <pthread.h>
#include <sched.h>
#include <string>
#include "common/mem.h"
#include "common/list.h"
#include "common/def.h"
#include "protocol/protocol.h"

// incoming packet queue
struct in_packet_queue {
	cpu_set_t cpu_set; // cpu schedule affinity
	uint32_t packet_num;
	struct list_head packet_list_h;
	pthread_mutex_t mutex;
	pthread_cond_t cond;
};
extern void init_in_packet_queue(struct in_packet_queue *queue);
// destroy queue and free packets in queue
// called after working thread quit
extern void destroy_in_packet_queue(struct in_packet_queue *queue);
// add packet to incoming packet queue
extern void add_in_packet(struct in_packet_queue *queue, struct mile_packet *packet);
// get packet from incoming packet queue
extern struct mile_packet *get_in_packet(struct in_packet_queue *queue);
// wake up all thread waiting on incoming queue
extern void wake_up_all(struct in_packet_queue *queue);


// output packet queue
struct out_packet_queue {
	struct list_head packet_list_h;
	int notify_fds[2]; // pipe
	pthread_mutex_t mutex;
};
extern int init_out_packet_queue(struct out_packet_queue *queue);
// destroy queue and free packets in queue
// called after working thread quit
extern void destroy_out_packet_queue(struct out_packet_queue *queue);
// add packet to output queue
extern int add_out_packet(struct out_packet_queue *queue, struct mile_packet *packet);
// get packet from output queue
extern struct mile_packet *get_out_packet(struct out_packet_queue *queue);
// get more packets from output queue by one pipe notification
extern int get_out_packet_array(struct out_packet_queue *queue, struct mile_packet **packet_ptrs, int array_size);


struct docserver_connect {
	int fd; // -1 for closed.
	struct event read_event;
	struct event write_event;

	char *rbuf; // receive data buffer
	uint32_t rbuf_size; // receive buffer size
	uint32_t rbytes;

	char *sbuf; // send data buffer
	uint32_t sbuf_size; // send buffer size
	uint32_t sbytes;

	// packets in incoming queue or output queue or in processing
	// (not include packets in docserver_connect's send_packet_list_h)
	uint32_t packet_num;

	void *bl_reader; // binlog reader : TODO (use BL_READER_PTR)
	MEM_POOL_PTR session_mem; // session memory

	struct list_head send_packet_list_h; // packet list head
	struct list_head conn_list; // list mark, list itself to docserver_config.conn_list_h
};
// create connection and add fd's read and write event to event_base
extern struct docserver_connect *new_docserver_connect(struct event_base *base, int fd);
// free associated memory
extern void destroy_docserver_connect(struct docserver_connect *conn);
// close socket fd, del event from event base,
// destroy connection if no packet associated with this connection
extern void close_docserver_connect(struct docserver_connect *conn);


struct docserver_config {
	std::string ip;
	int32_t port;
	int32_t sync_port;
	int32_t backlog; // queue length (1024)
	int32_t max_conn_num; // max connection number.
	int32_t total_conn_num;

	uint32_t max_result_size; // max result packet size ( 0 means no limit )

	struct list_head conn_list_h; // connection list head

	int32_t role; // master or slave
	std::string sync_addr;

	struct in_packet_queue insert_queue;
	struct in_packet_queue query_queue;
	struct out_packet_queue out_queue;

	int32_t insert_thread_num;
	int32_t query_thread_num;

	// cpu info
	int cpu_threshold;
	int cpu_num;

	struct event_base *event_base;
	struct event listen_event;
	struct event pipe_notify_event;

	MEM_POOL_PTR mem_pool; // doc server pool
};

extern struct docserver_config docserver;
extern void init_docserver_config(struct docserver_config *doc);
// free associated memory
extern void destroy_docserver_config(struct docserver_config *doc);


struct cpu_occupy  //定义一个cpu occupy的结构体
{
	char name[20];      //定义一个char类型的数组名name有20个元素
	unsigned int user; //定义一个无符号的int类型的user
	unsigned int nice; //定义一个无符号的int类型的nice
	unsigned int system; //定义一个无符号的int类型的system
	unsigned int idle; //定义一个无符号的int类型的idle
};

#endif // DOCSERVER_H
