/*
*
*	Hyperindex mile application protocol  header file
*
*/

#ifndef PROTOCOL_H
#define PROTOCOL_H
// #include "hyperindex_sql_analyzer.h"
#include "packet.h"
#include "../common/list.h"
#include "../common/def.h"
#include "../storage/docdb/db.h"
#include "../storage/binlog.h"
#include "../storage/StorageEngine.h"
#include "../storage/StateManager.h"
#include "../execute/SqlAnalyzer.h"
#include <sys/socket.h>

struct docserver_connect;

struct mile_packet{
	int32_t socket_fd;
	struct event *event;
	uint64_t launch_time; // us
	uint64_t start_process_time; // us
	uint64_t in_send_queue_time; // us
	uint64_t out_send_queue_time; // us
	uint32_t timeout;
	struct data_buffer *rbuf; /* read data buf */
	struct data_buffer *sbuf; /* send data buf */
	int32_t flags; /* -1 --> not process; 0 --> processing; 1 --> processed */
	/* 消息类型 */
	uint16_t message_type;
	struct list_head packet_list;
	struct docserver_connect *conn;
};


struct multi_mile_packet{
	int32_t socket_fd;
	uint32_t sql_count;
	uint32_t len;
	struct mile_packet *packet;
	struct list_head packet_list_h;
};




struct mile_packet* init_mile_packet(int32_t socket_fd, struct event* event);


void destroy_mile_packet(struct mile_packet * packet);

struct mile_packet *pre_parse_packet( char *buf, uint32_t buf_len, uint32_t *parsed_len );

int32_t parse_mile_message(struct mile_packet *packet, void **bl_reader, int16_t exceed_cpu_used, MEM_POOL_PTR session_mem);


int32_t read_message_from_mergeserver(struct mile_packet *packet);


int32_t send_result_to_mergeserver(struct mile_packet *packet);

void destroy_multi_mile_packet(struct multi_mile_packet* packet);

struct multi_mile_packet * multi_init_mile_packet(int32_t socket_fd, uint32_t max_len);

int32_t get_message_count(struct multi_mile_packet *multi_packet);


struct slave_sync_res *parse_master_response( struct mile_packet *packet);

int32_t send_result_to_master( struct mile_packet *packet );

/**
 * check OS overading
 * return 0 on OK, ERROR_OS_OVERLOADING for overloading, -1 for ERROR.
 */
int loadavg_check(void);

#endif //PROTOCOL_H



