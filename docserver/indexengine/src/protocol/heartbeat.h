/************************************************************************************
**
**	Copyright (c)  (C) 2011-2012 Alipay Inc.
**			All Rights Reserved.
**
**	Subsystem		:	mile::docserver
**	File			      	:	hyperindex_heartbeat.h
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
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <event.h>
#include <netdb.h>
#include <pthread.h>
#include <unistd.h>
	
#include "../common/def.h"
#include "../common/list.h"

#ifndef HEARTBEAT_H
#define HEARTBEAT_H

#define HEARTBEAT_TIMEOUT 1  /* heartbeat timeout is 1 second */
#define MASTER_NORMAL 0
#define MASTER_FAILURE 1
struct event_base *heartbeat_base;


pthread_mutex_t heartbeat_lock;
pthread_cond_t	heartbeat_cond;

#define HEARTBEAT_CONNECT 1
#define HEARTBEAT_DISCONNECT 0

struct heartbeat_server_dsp{
	int socket_fd;
	char *master_ip;
	int32_t hb_port;
	int32_t conn_state; /* 1 -- connect; 0 -- disconnect */
	int32_t slave_id;
	struct event_base *hb_base;
	struct sockaddr_in ha_addr;
	pthread_mutex_t hb_stat_lock; /* protect master state */
	pthread_cond_t  hb_stat_cond;  /* notify master state */
};

/* descript ha slave struct */
struct ha_slave_dsp{
	pthread_t slave_tid; /* slave thread */
	pthread_t slave_hb_tid; /* slave heartbeat thread */
	int32_t sc_fd; /* slave connect fd */
	int32_t master_state;
	int32_t slave_id;
	int32_t ha_port;
	char *master_ip;
	struct event_base *ha_base;
	struct sigaction disconn_event;
	struct heartbeat_server_dsp *hb_slave_dsp;
	pthread_mutex_t ms_stat_lock; /* protect master state */
	pthread_cond_t  ms_stat_cond;  /* notify master state */
};


struct heartbeat_connect{
	int    sfd;
    //enum conn_states  state;

	struct event_base *heartbeat_base;

	struct event conn_event; /* listening connect event */
	struct timeval timeout;  /* heartbeat timeout */
	int32_t event_flags;
	int16_t event_type; /* libevent event type */
	int32_t cur_event;

	char   *rbuf;  /* receive data buffer */
	uint32_t rbuf_size; /* read buffer size */
	uint32_t rbytes;
	uint32_t max_len;  /* read buffer max length */

	char   *sbuf;  /* send data buffer */
	uint32_t sbytes;
	uint32_t sbuf_size; /* send buffer size */

	int32_t cur_msg; /* point current message  */

	struct list_head conn_list;  /* documnet server connect list */
	struct doc_server_thread *thread;  /* task process thread */
	int32_t  ds_tid; /* docserver worker trhread id */

	struct list_head result_pkg_list_h; /* result data list header */
	pthread_mutex_t pkg_lock;		/* shared resource lock */
    pthread_cond_t  pkg_cond;
	pthread_t slave_hb_tid; /* slave heartbeat thread */
};


struct sync_connect{
	int    sfd;
    //enum conn_states  state;

	struct event conn_event; /* listening connect event */
	struct timeval timeout;  /* heartbeat timeout */
	int32_t event_flags;
	int16_t event_type; /* libevent event type */
	int32_t cur_event;

	char   *rbuf;  /* receive data buffer */
	uint32_t rbuf_size; /* read buffer size */
	uint32_t rbytes;
	uint32_t max_len;  /* read buffer max length */

	char   *sbuf;  /* send data buffer */
	uint32_t sbytes;
	uint32_t sbuf_size; /* send buffer size */

	int32_t cur_msg; /* point current message  */

	struct list_head conn_list;  /* documnet server connect list */
	struct doc_server_thread *thread;  /* task process thread */
	int32_t  ds_tid; /* docserver worker trhread id */

	struct list_head result_pkg_list_h; /* result data list header */
	pthread_mutex_t pkg_lock;		/* shared resource lock */
    pthread_cond_t  pkg_cond;
};

struct node_health_info_pkg{
	uint32_t version;
	uint32_t msg_type;
	uint32_t slave_id;
	uint64_t start_time;
	int32_t state;  /* master state: -1 --> not valid; 0 --> valid */
	uint32_t timeout; 
	
};


void *create_master_heartbeat_server_socket(void *hb_port);
void* create_slave_beartbeat_client_socket(void *dsp);
void init_heartbeat_slave_dsp(void *ha_dsp);



/*
int32_t heartbeat_init();
int32_t send_hb_pkg();
int32_t receive_hb_pkg();
//int32_t set_hb_period();
int32_t set_slave_node_id();
int32_t get_slave_node_id();
int32_t create_hb_connect();
int32_t launch_data_recovery();
int32_t broadcast_node_failure_event();
int32_t broadcast_node_active_event();
int32_t judge_other_node_statue();
*/

#endif // HEARTBEAT_H
