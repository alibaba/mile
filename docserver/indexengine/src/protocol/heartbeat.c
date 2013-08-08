
/************************************************************************************
**
**	Copyright (c)  (C) 2011-2012 Alipay Inc.
**			All Rights Reserved.
**
**	Subsystem		:	mile::docserver
**	File			      	:	hi_heartbeat.c
**	Created By		:	balanth.quanxf@alibaba-inc.com 
**	Created On		:	06/24/2011
**	
**	Purpose: 	heartbeat monitor docserver
**	  
**	History
**	Authors 		   Date 				Rev 				Description
**	--------------- --------------- -------- ------------------------------
**	balanth.quanxf 	   06/24				0.1					 Created
**	
**************************************************************************************/
#include "heartbeat.h"

#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <sys/sysinfo.h> 
#include <sys/syscall.h>

#define MT_MD_EXE_HEARTBEAT 0x2200
#define MASTER_VALID 0x1010
#define MASTER_INVALID -1
#define MASTER_STATE_QUERY 0X0101
#ifndef HEARTBEAT_TIMEOUT
#define HEARTBEAT_TIMEOUT 1  /* heartbeat timeout is 1 second */
#endif

struct heartbeat_connect hb_conn;

static void set_hb_period(struct heartbeat_connect *conn,int timeout)
{
	if(!timeout){
		conn->timeout.tv_sec = HEARTBEAT_TIMEOUT;
	}else{
		conn->timeout.tv_sec = timeout;
	}
	
	conn->timeout.tv_usec = 0; 
}

/*
static int32_t broadcast_master_statue_event(struct node_health_info_pkg *packet)
{

	pthread_cond_broadcast(&heartbeat_cond);
}
*/


struct node_health_info_pkg *init_heartbeat_pkg(uint32_t node_id)
{
	struct node_health_info_pkg *hb_pkg;

	hb_pkg = (struct node_health_info_pkg *)malloc(sizeof(struct node_health_info_pkg));

	hb_pkg->msg_type = MT_MD_EXE_HEARTBEAT;
	hb_pkg->slave_id = node_id;
	hb_pkg->state = MASTER_STATE_QUERY;
	hb_pkg->timeout = HEARTBEAT_TIMEOUT;
	hb_pkg->start_time = get_time_msec();

	return hb_pkg;
}

/*
*		slave end
*
*/

int32_t read_message_from_slave(const int fd, struct node_health_info_pkg *packet)
{
	int32_t rec_len;
	int32_t recd_len = 0;
	int32_t result;

	if(!packet){
		log_error(" packet invalid. ");
		return -1;
	}

	rec_len = sizeof(struct node_health_info_pkg);
	
	while(rec_len > 0)
	{
		result = recv(fd, packet + recd_len, rec_len, 0);

		if(result == 0)
		{
			return ERROR_DATA_RECEIVE;
		}
		if(result == -1)
		{
			if(errno == EAGAIN || errno == EWOULDBLOCK)
			{
				log_warn("在接收数据时遇到EAGAIN错误!");
				continue;
			}
			return ERROR_DATA_RECEIVE;
		}
		rec_len -= result;
		recd_len += result;
	}

	return MILE_RETURN_SUCCESS;
}

int32_t send_hb_pkg_to_slave(const int fd,struct node_health_info_pkg *hb_pkg)
{
	int32_t send_len;
	int32_t result;

	if(!hb_pkg){
		log_error(" packet invalid. ");
		return -1;
	}
	
	send_len = sizeof(struct node_health_info_pkg);

	while(1)
	{
		result = send(fd, hb_pkg, send_len, 0);
		if(result == 0)
		{
			return ERROR_DATA_SEND;
		}
		if(result == -1)
		{
			if(errno == EAGAIN || errno == EWOULDBLOCK)
			{
				log_warn("在发送数据时遇到EAGAIN错误!");
				continue;
			}		
			return ERROR_DATA_SEND;
		}
		return MILE_RETURN_SUCCESS;
	}

}
void get_heartbeat_from_slave(const int fd, const short  stat,void *arg)
{
	struct node_health_info_pkg *packet = NULL;
	struct node_health_info_pkg *back_packet = NULL;
	int ret;
	
	packet = (struct node_health_info_pkg *)malloc(sizeof(struct node_health_info_pkg));
	ret = read_message_from_slave(fd,packet);
	if((ret<0)||(packet->state != MASTER_STATE_QUERY)){
		/* slave failure ,and close connect */
		//packet->state = MASTER_INVALID;
		//broadcast_master_statue_event(packet);
		close(fd);
		return;
	}
	
	/* report master state to slave */
	back_packet = init_heartbeat_pkg(packet->slave_id);
	back_packet->state = MASTER_VALID;
	ret = send_hb_pkg_to_slave(fd,back_packet);
	if(ret<0){
		close(fd);
		return;
	}

	return;
}


void *connect_docserver_slave(void *arg)
{
	struct heartbeat_connect *conn = (struct heartbeat_connect *)arg;
	struct event *ev_accept = NULL;
	struct sockaddr_in master_addr;
	socklen_t addr_len;
	int32_t master_fd;
	int32_t flags;

	addr_len = sizeof(struct sockaddr_in);
	master_fd = accept(conn->sfd,(struct sockaddr *)&master_addr,&addr_len);
	if(master_fd == -1){
		log_error("accept error.");
		exit(EXIT_FAILURE);
	}

	if((flags = fcntl(master_fd, F_GETFL, 0)) < 0 ||
    	fcntl(master_fd, F_SETFL, flags | O_NONBLOCK) < 0) {
        log_error("setting O_NONBLOCK");
        close(master_fd);
        exit(EXIT_FAILURE);
    }

	flags = 1;
	if(setsockopt(master_fd, IPPROTO_TCP, TCP_NODELAY, (void *)&flags, sizeof(flags)) != 0){
		log_error( "setsockopt %d TCP_NODELAY failed, errno %d", master_fd, errno );
	}

	set_hb_period(conn,0);
	conn->heartbeat_base = event_init();
	ev_accept = (struct event*)malloc(sizeof(struct event));
	event_set(ev_accept, master_fd, EV_READ | EV_PERSIST, get_heartbeat_from_slave, NULL);
    event_base_set(conn->heartbeat_base, ev_accept);
    event_add(ev_accept, &conn->timeout);

	/*  enter into master heartbeat service thread loop */
	event_base_loop(conn->heartbeat_base, 0);

	return NULL;
}

void create_hb_thread(const int fd, const short which, void *arg)
{
	pthread_t ms_hb_tid;

	if((pthread_create(&ms_hb_tid, NULL, connect_docserver_slave, arg)) != 0){
        log_error("Can't create ms heartbeat service thread.");
       	exit(EXIT_FAILURE);
    }
}

//void *create_master_heartbeat_server_socket(int32_t *hb_port)
void *create_master_heartbeat_server_socket(void *hb_port)
{
	int32_t socket_fd;
	int32_t flags;
	int32_t port = *(int32_t *)hb_port + 2;  /* 18518 + 2 */
	struct sockaddr_in heartbeat_addr;
	//struct heartbeat_connect hb_conn;
		
	if((socket_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		log_error("socket failure.");
        exit(EXIT_FAILURE);
    }

	if((flags = fcntl(socket_fd, F_GETFL, 0)) < 0 ||
		fcntl(socket_fd, F_SETFL, flags | O_NONBLOCK) < 0){
		log_error("setting O_NONBLOCK");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}

	flags = 1;
	if( setsockopt(socket_fd, SOL_SOCKET, SO_REUSEADDR, (void *)&flags, sizeof(flags)) != 0 ) {
		log_error( "setsockopt %d SO_REUSEADDR failed, errno %d", socket_fd, errno );
	}
	
	heartbeat_addr.sin_family=AF_INET;
	heartbeat_addr.sin_port=htons(port);
	heartbeat_addr.sin_addr.s_addr = INADDR_ANY;
	bzero(&(heartbeat_addr.sin_zero),8);
	
	while(1){
		if(bind(socket_fd, (struct sockaddr *)&heartbeat_addr, sizeof(struct sockaddr)) == -1) {
			log_error("bind");
			close(socket_fd);
			exit(EXIT_FAILURE);
		}else{
			break;
		}
	}

	if(listen(socket_fd, 1024) == -1){
		log_error("listen ...");
		close(socket_fd);
		exit(EXIT_FAILURE);
	}

	hb_conn.sfd = socket_fd;
	hb_conn.heartbeat_base = event_init();
	event_set(&hb_conn.conn_event, hb_conn.sfd, EV_READ | EV_PERSIST, create_hb_thread, (void *)&hb_conn);
	event_base_set(hb_conn.heartbeat_base, &hb_conn.conn_event);
	if(event_add(&hb_conn.conn_event, 0) == -1){
        log_error("event_add");
        exit(EXIT_FAILURE);
    }

	/*  enter into slave heartbeat loop */
	event_base_loop(hb_conn.heartbeat_base, 0);
	
	close(socket_fd);
	
	return NULL;
}


/*
*		slave end
*
*/


int32_t get_hb_pkg_from_master(const int fd,struct node_health_info_pkg *hb_pkg)
{
	int32_t rec_len;
	int32_t recd_len = 0;
	int32_t result = 0;
	
	if(!hb_pkg){
		log_error(" packet invalid. ");
		return -1;
	}

	rec_len = sizeof(struct node_health_info_pkg);
	
	while(rec_len > 0)
	{
		result = recv(fd, hb_pkg + recd_len, rec_len, 0);

		if(result == 0)
		{
			return ERROR_DATA_RECEIVE;
		}
		if(result == -1)
		{
			if(errno == EAGAIN || errno == EWOULDBLOCK)
			{
				log_warn("在接收数据时遇到EAGAIN错误!");
				continue;
			}
			return ERROR_DATA_RECEIVE;
		}
		rec_len -= result;
		recd_len +=result;
	}
	return MILE_RETURN_SUCCESS;
}

int32_t send_hb_pkg_to_master(const int fd,struct node_health_info_pkg *hb_pkg)
{
	int32_t send_len;
	int32_t result;

	if(!hb_pkg){
		log_error(" packet invalid. ");
		return -1;
	}
	
	send_len = sizeof(struct node_health_info_pkg);

	while(1)
	{
		result = send(fd, hb_pkg, send_len, 0);
		if(result == 0)
		{
			return ERROR_DATA_SEND;
		}
		if(result == -1)
		{
			if(errno == EAGAIN || errno == EWOULDBLOCK)
			{
				log_warn("在发送数据时遇到EAGAIN错误!");
				continue;
			}		
			return ERROR_DATA_SEND;
		}
		return MILE_RETURN_SUCCESS;
	}
	return MILE_RETURN_SUCCESS;
}

void connect_to_master_heartbeat(const int fd, const short stat,void *arg)
{
	struct sockaddr_in *ha_addr;
	struct heartbeat_server_dsp *hb_dsp = (struct heartbeat_server_dsp *)arg;
	int socket_fd = fd;
	struct node_health_info_pkg *hb_pkg;
	int32_t ret;

	ha_addr = &hb_dsp->ha_addr;

	log_debug("connect to master.");

	if(hb_dsp->conn_state == HEARTBEAT_CONNECT){
		log_debug("connect is active.");
		//return;
	}else{
		if(connect(socket_fd, (struct sockaddr *)ha_addr, sizeof(struct sockaddr)) == -1) {
			log_error("connect failure.");
			pthread_mutex_lock(&hb_dsp->hb_stat_lock);
			hb_dsp->conn_state = HEARTBEAT_DISCONNECT;
			pthread_cond_broadcast(&hb_dsp->hb_stat_cond);  //* broadcast master active event  *//
			pthread_mutex_unlock(&hb_dsp->hb_stat_lock);
			//exit(EXIT_FAILURE);
		}else{
			pthread_mutex_lock(&hb_dsp->hb_stat_lock);
			hb_dsp->conn_state = HEARTBEAT_CONNECT;
			pthread_cond_broadcast(&hb_dsp->hb_stat_cond);  //* broadcast master active event  *//
			pthread_mutex_unlock(&hb_dsp->hb_stat_lock);
		}

	}
		
	if(hb_dsp->conn_state == HEARTBEAT_CONNECT){
		hb_pkg = init_heartbeat_pkg(hb_dsp->slave_id);
		/* send master state query pkg */
		ret = send_hb_pkg_to_master(socket_fd,hb_pkg);
		if(ret<0){
			pthread_mutex_lock(&hb_dsp->hb_stat_lock);
			hb_dsp->conn_state = HEARTBEAT_DISCONNECT;
			pthread_cond_broadcast(&hb_dsp->hb_stat_cond);  //* broadcast master active event  *//
			pthread_mutex_unlock(&hb_dsp->hb_stat_lock);			
		}

		/* get master state  */
		memset(hb_pkg,0,sizeof(struct node_health_info_pkg));
		ret = get_hb_pkg_from_master(socket_fd,hb_pkg);
		if((ret<0)||(hb_pkg->state != MASTER_VALID)){
			pthread_mutex_lock(&hb_dsp->hb_stat_lock);
			/* master failure ,and broadcast this event */
			hb_dsp->conn_state = HEARTBEAT_DISCONNECT;
			pthread_cond_broadcast(&hb_dsp->hb_stat_cond);  //* broadcast master active event  *//
			pthread_mutex_unlock(&hb_dsp->hb_stat_lock);
		}
	}
	
	return;
}

void init_heartbeat_slave_dsp(void *dsp)
{
	struct heartbeat_server_dsp *hb_dsp;
	struct ha_slave_dsp *ha_dsp = (struct ha_slave_dsp *)dsp;
	
	hb_dsp = (struct heartbeat_server_dsp *)malloc(sizeof(struct heartbeat_server_dsp));
	hb_dsp->hb_port = ha_dsp->ha_port + 1;
	hb_dsp->master_ip = ha_dsp->master_ip;
	hb_dsp->slave_id = ha_dsp->slave_id;
	hb_dsp->conn_state = HEARTBEAT_DISCONNECT;
	hb_dsp->hb_base = event_init();

	if(pthread_mutex_init(&hb_dsp->hb_stat_lock, NULL) != 0){
		log_error("Failed to initialize lock(hb_stat_lock)");
		exit(EXIT_FAILURE);
	}
	
	if(pthread_cond_init(&hb_dsp->hb_stat_cond,NULL) != 0){
		log_error("Failed to initialize hb_stat_cond");
		exit(EXIT_FAILURE); 		
	}

	ha_dsp->hb_slave_dsp = hb_dsp;
}

void* create_slave_beartbeat_client_socket(void *dsp)
{
	struct event *ev_conn = NULL;
	struct timeval timeout;  /* heartbeat timeout */
	struct ha_slave_dsp *ha_dsp = (struct ha_slave_dsp *)dsp;
	struct heartbeat_server_dsp *hb_dsp;

	/*
	hb_dsp = (struct heartbeat_server_dsp *)malloc(sizeof(struct heartbeat_server_dsp));
	hb_dsp->hb_port = ha_dsp->ha_port + 1;
	hb_dsp->master_ip = ha_dsp->master_ip;
	hb_dsp->slave_id = ha_dsp->slave_id;
	hb_dsp->conn_state = HEARTBEAT_DISCONNECT;
	hb_dsp->hb_base = event_init();

	if(pthread_mutex_init(&hb_dsp->hb_stat_lock, NULL) != 0){
		log_error("Failed to initialize lock(hb_stat_lock)");
		exit(EXIT_FAILURE);
	}
	
	if(pthread_cond_init(&hb_dsp->hb_stat_cond,NULL) != 0){
		log_error("Failed to initialize hb_stat_cond");
		exit(EXIT_FAILURE); 		
	}
	*/

	hb_dsp = ha_dsp->hb_slave_dsp;

	if(!hb_dsp->master_ip ){
		log_error("host ip address null");
		exit(EXIT_FAILURE);
	}else{
		if(!inet_aton(hb_dsp->master_ip, (struct in_addr *)&hb_dsp->ha_addr.sin_addr.s_addr)){
			log_error("ip address");
			exit(EXIT_FAILURE);
		}
	}

	if((hb_dsp->socket_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		log_error("socket failure.");
        exit(EXIT_FAILURE);
    }

	hb_dsp->ha_addr.sin_family = AF_INET;
	hb_dsp->ha_addr.sin_port = htons(hb_dsp->hb_port);
	bzero(&(hb_dsp->ha_addr.sin_zero),8);

	ev_conn = (struct event *)malloc(sizeof(struct event));
	event_set(ev_conn, hb_dsp->socket_fd, EV_TIMEOUT | EV_PERSIST, connect_to_master_heartbeat, hb_dsp);
	event_base_set(hb_dsp->hb_base, ev_conn);
	timeout.tv_sec = HEARTBEAT_TIMEOUT; /* period 1s */
	timeout.tv_usec = 0;
	event_add(ev_conn, &timeout);

	/* enter into slave loop */
	event_base_loop(hb_dsp->hb_base, 0);

	free(hb_dsp);
	
	return NULL;
}

