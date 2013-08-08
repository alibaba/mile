/*
 *
 *	parse mile protocol ; incode and decode message
 *
 */

#include "protocol.h"

struct mile_packet* init_mile_packet(int32_t socket_fd, struct event* event) {
	struct mile_packet* packet = (struct mile_packet*) malloc(
			sizeof(struct mile_packet));

	if (packet == NULL) {
		log_error("初始化mile packet时申请内存失败!");
		return NULL;
	}

	packet->socket_fd = socket_fd;
	packet->event = event;
	packet->launch_time = get_time_usec();
	packet->start_process_time = 0;
	packet->in_send_queue_time = 0;
	packet->out_send_queue_time = 0;
	packet->rbuf = init_data_buffer();
	packet->sbuf = init_data_buffer();
	packet->flags = -1;
	INIT_LIST_HEAD(&packet->packet_list);

	return packet;
}

void destroy_mile_packet(struct mile_packet* packet) {
	if (NULL != packet) {
		destroy_data_buffer(packet->rbuf);
		destroy_data_buffer(packet->sbuf);
		free(packet);
	}
}

void destroy_multi_mile_packet(struct multi_mile_packet* packet) {
	free(packet);
}

int loadavg_check(void) {
	static double last_load = 0;
	static time_t last_check_time = 0;

	double threshold = g_load_threshold;
	if (threshold < 10e-7 && threshold > -10e-7)
		return 0;

	double load = 0;
	time_t now = time(NULL);
	if (last_check_time > 0 && now < last_check_time + 30
			&& last_load < threshold) {
		load = last_load;
	} else {
		if (getloadavg(&load, 1) == -1) {
			log_warn("load average was unobtainable");
			return 0;
		}
		last_load = load;
		last_check_time = now;
	}

	return (load < threshold ? 0 : ERROR_OS_OVERLOADING);
}

int32_t process_mergeserver_message(struct mile_message_header* msg_head,
		struct data_buffer* rbuf, struct data_buffer* sbuf,
		int16_t exceed_cpu_used, uint64_t deadline_time) {
	int32_t result_code = MILE_RETURN_SUCCESS;
	MEM_POOL_PTR mem_pool;
	void* parsed_packet;
	ExecutePlan* plan = NULL;
	void* result;

	switch (msg_head->message_type) {
	case MT_MD_EXE_INSERT:
		mem_pool = mem_pool_init(MB_SIZE);
		parsed_packet = parse_insert_packet(mem_pool, rbuf);
		plan = SqlAnalyzer::Analyze(StorageEngine::storage,
				(struct insert_packet*) parsed_packet, mem_pool);
		if (plan == NULL) {
			result_code = -1;
		} else {
			result = plan->Execute(result_code, deadline_time);
			plan->~ExecutePlan();
		}
		if (result_code == MILE_RETURN_SUCCESS) {
			gen_insert_result_packet(*(uint64_t*) result, msg_head, sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	case MT_MD_EXE_DELETE:
		if (exceed_cpu_used) {
			log_warn("cpu使用率超出，拒绝UPDATE QUERY DELETE服务");
			break;
		}
		mem_pool = mem_pool_init(MB_SIZE);
		parsed_packet = parse_delete_packet(mem_pool, rbuf);
		plan = SqlAnalyzer::Analyze(StorageEngine::storage,
				(struct delete_packet*) parsed_packet, mem_pool);
		if (plan == NULL) {
			result_code = -1;
		} else {
			result = plan->Execute(result_code, deadline_time);
			plan->~ExecutePlan();
		}

		if (result_code == MILE_RETURN_SUCCESS) {
			gen_delete_result_packet(*(uint32_t*) result, msg_head, sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	case MT_MD_EXE_EXPORT: {
		mem_pool = mem_pool_init(MB_SIZE);
		parsed_packet = parse_export_packet(mem_pool, rbuf);
		plan = SqlAnalyzer::Analyze(StorageEngine::storage,
				(struct export_packet *) parsed_packet, mem_pool);
		if (plan == NULL) {
			result_code = -1;
		} else {
			result = plan->Execute(result_code, deadline_time);
			plan->~ExecutePlan();
		}

		if (result_code == MILE_RETURN_SUCCESS) {
			gen_export_result_packet(*(uint64_t*) result, msg_head, sbuf);
		}

		mem_pool_destroy(mem_pool);
	}
		break;
	case MT_MD_EXE_UPDATE:
		if (exceed_cpu_used) {
			log_warn("cpu使用率超出，拒绝UPDATE QUERY DELETE服务");
			break;
		}
		mem_pool = mem_pool_init(MB_SIZE);
		parsed_packet = parse_update_packet(mem_pool, rbuf);
		plan = SqlAnalyzer::Analyze(StorageEngine::storage,
				(struct update_packet*) parsed_packet, mem_pool);
		if (plan == NULL) {
			result_code = -1;
		} else {
			result = plan->Execute(result_code, deadline_time);
			plan->~ExecutePlan();
		}

		if (result_code == MILE_RETURN_SUCCESS) {
			gen_update_result_packet(*(uint32_t*) result, msg_head, sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	case MT_MD_EXE_QUERY:
		if (exceed_cpu_used) {
			log_warn("cpu使用率超出，拒绝UPDATE QUERY DELETE服务");
			break;
		}
		mem_pool = mem_pool_init(MB_SIZE);

		result_code = loadavg_check(); // check overloading
		if (ERROR_OS_OVERLOADING == result_code) {
			log_warn(
					"OS overloading, discard packet msg id %"PRIu32, msg_head->message_id);
		} else if (0 == result_code) {
			parsed_packet = parse_query_packet(mem_pool, rbuf);
			plan = SqlAnalyzer::Analyze(StorageEngine::storage,
					(struct query_packet*) parsed_packet, mem_pool);
			if (plan == NULL) {
				result_code = -1;
			} else {
				result = plan->Execute(result_code, deadline_time);
				plan->~ExecutePlan();
			}
		}

		if (result_code == MILE_RETURN_SUCCESS) {
			gen_query_result_packet((ResultSet*) result, msg_head, sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	case MT_MD_GET_STATE:
		mem_pool = mem_pool_init(KB_SIZE);
		parsed_packet = parse_get_state_packet(mem_pool, rbuf);
		result = StateManager::QueryStates(StorageEngine::storage,
				(struct get_state_packet*) parsed_packet, mem_pool);
		if (result == NULL) {
			result_code = ERROR_GET_DOCSERVER_STATE;
		} else {
			gen_state_result_packet((struct stat_info_array*) result, msg_head,
					sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	default:
		result_code = StorageEngine::storage->SpecialSql(msg_head, rbuf, sbuf);
		break;
	}

	if (result_code < 0) {
		gen_error_packet(result_code, msg_head, sbuf);
	}
	return result_code;
}

int32_t process_client_message(struct mile_message_header* msg_head,
		struct data_buffer* rbuf, struct data_buffer* sbuf) {
	int32_t result_code = MILE_RETURN_SUCCESS;
	MEM_POOL_PTR mem_pool;
	void* packet;
	struct slave_sync_res *binlog_res;

	switch (msg_head->message_type) {
	case MT_CD_EXE_GET_LOAD_THRESHOLD: {
		log_debug("get get_load_threshold command");
		double load = g_load_threshold;
		gen_dc_get_load_threshold_packet(load, msg_head, sbuf);
	}
		break;
	case MT_CD_EXE_SET_LOAD_THRESHOLD: {
		log_debug("get set_load_threshold command");
		double old_load = g_load_threshold;
		mem_pool = mem_pool_init(KB_SIZE);
		struct set_load_threshold_packet *p = parse_set_load_threshold_packet(
				mem_pool, rbuf);

		g_load_threshold = p->value;
		log_info(
				"change os overloading threshold from %g to %g", old_load, g_load_threshold);
		gen_dc_set_load_threshold_packet(old_load, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
	}
		break;
	default:
		result_code = -1;
		break;
	}

	return result_code;
}

int32_t process_master_message(struct mile_message_header* msg_head,
		struct data_buffer* rbuf, struct data_buffer* sbuf, void **bl_reader,
		MEM_POOL_PTR session_mem) {
	int32_t result_code = MILE_RETURN_SUCCESS;
	MEM_POOL_PTR mem_pool;
	void* packet;
	struct slave_sync_res *binlog_res;

	switch (msg_head->message_type) {
	case MT_SM_GET_BINLOG:
		mem_pool = mem_pool_init(MB_SIZE);
		// STATIC_ASSERT( MB_SIZE > sizeof(struct slave_sync_res) + MAX_BINLOG_DATA_PER_PACKET, MAX_BINLOG_PACKAGE_SHOULD_LESS_THAN_MEM_POOL_SIZE);
		packet = parse_slave_sync_req(mem_pool, rbuf);
		binlog_res = execute_slave_sync(mem_pool,
				(struct slave_sync_req *) packet, bl_reader, session_mem);
		assert(NULL != binlog_res);
		gen_slave_sync_res_packet(binlog_res, msg_head, sbuf);
		result_code = 0;
		mem_pool_destroy(mem_pool);
		break;
	default:
		result_code = -1;
		break;
	}

	return result_code;
}

int32_t process_test_message(struct mile_message_header* msg_head,
		struct data_buffer* rbuf, struct data_buffer* sbuf) {
	int32_t result_code = MILE_RETURN_SUCCESS;
	MEM_POOL_PTR mem_pool;

	switch (msg_head->message_type) {
	case MT_TEST_REQ_ECHO: // echo message for testing.
	{
		mem_pool = mem_pool_init(MB_SIZE);
		const char *str = read_cstring(rbuf, mem_pool);
		log_debug("get echo message [%s]", str);

		write_int32(0, sbuf);
		write_int8(msg_head->version_major, sbuf);
		write_int8(msg_head->version_minor, sbuf);
		write_int16(MT_TEST_RES_ECHO, sbuf);
		write_int32(msg_head->message_id, sbuf);

		write_int32(strlen(str), sbuf);
		write_bytes((uint8_t *) str, strlen(str), sbuf);

		fill_int32(sbuf->data_len, sbuf, 0);
		result_code = MILE_RETURN_SUCCESS;
		mem_pool_destroy(mem_pool);
	}
		break;
	default:
		result_code = -1;
		break;
	}

	return result_code;
}

int32_t parse_mile_message(struct mile_packet *packet, void **bl_reader,
		int16_t exceed_cpu_used, MEM_POOL_PTR session_mem) {
	int32_t result_code = MILE_RETURN_SUCCESS;
	//读buffer和写buffer
	struct data_buffer* rbuf = packet->rbuf;
	struct data_buffer* sbuf = packet->sbuf;
	//读取消息头
	struct mile_message_header msg_head;
	//当前时间与超时时间
	uint64_t now_time;
	uint64_t deadline_time;

	if (rbuf == NULL) {
		log_error("严重错误, 读buffer为空!");
		return ERROR_PACKET_FORMAT;
	}

	msg_head.version_major = read_int8(rbuf);
	msg_head.version_minor = read_int8(rbuf);
	msg_head.message_type = read_int16(rbuf);
	msg_head.message_id = read_int32(rbuf);
	packet->timeout = read_int32(rbuf);

	log_debug(
			"数据包包头信息 -- 主版本号: %d, 小版本号: %d, 消息类型: 0x%x, 消息id: %d, 超时: %d", msg_head.version_major, msg_head.version_minor, msg_head.message_type, msg_head.message_id, packet->timeout);

	now_time = get_time_msec();
	if (packet->timeout != 0
			&& now_time - packet->launch_time / 1000 > packet->timeout) {
		log_warn(
				"执行sql超时, 收到sql命令时的时间 %llu, 当前时间 %llu, 超时时间 %u", packet->launch_time / 1000, now_time, packet->timeout);
		return ERROR_TIMEOUT;
	} else {
		deadline_time = now_time + packet->timeout;
	}

	uint16_t msg_head_type = (msg_head.message_type & 0xFF00);

	if (msg_head_type == MT_VG_MD) {
		result_code = process_mergeserver_message(&msg_head, rbuf, sbuf,
				exceed_cpu_used, deadline_time);
	} else if (msg_head_type == MT_VG_SC) {
		result_code = StorageEngine::storage->Command(&msg_head, rbuf, sbuf);
		if (MILE_RETURN_SUCCESS != result_code)
			gen_docserver_client_error_packet(result_code, &msg_head, sbuf);
	} else if (msg_head_type == MT_VG_CD) {
		result_code = process_client_message(&msg_head, rbuf, sbuf);
	} else if (msg_head_type == MT_VG_SM) {
		result_code = process_master_message(&msg_head, rbuf, sbuf, bl_reader,
				session_mem);
	} else if (msg_head_type == MT_VG_TEST_REQ) {
		result_code = process_test_message(&msg_head, rbuf, sbuf);
	} else {
		result_code = -1;
	}

	if (result_code != MILE_RETURN_SUCCESS) {
		log_error(
				"执行过程中出错, 错误码 %d, 错误原因 %s", result_code, error_msg(result_code));
	}

	return result_code;
}

struct mile_packet *pre_parse_packet(char *buf, uint32_t buf_len,
		uint32_t *parsed_len) {
	struct mile_packet *packet = NULL;
	if (buf_len < 4)
		return NULL;

	uint32_t packet_len = convert_int32(*(uint32_t *) buf);
	if (buf_len < packet_len)
		return NULL;

	// TODO: remove args.
	packet = init_mile_packet(0, NULL);
	if (NULL == packet)
		return NULL;

	packet->rbuf->array_border = 0;
	packet->rbuf->rpos = 0;
	packet->rbuf->wpos = 0;
	packet->rbuf->data_len = packet_len - 4;
	packet->rbuf->data = (uint8_t *) malloc(packet_len - 4);
	if (NULL == packet->rbuf) {
		log_error( "malloc failed, errno %d", errno);
		destroy_mile_packet(packet);
		return NULL;
	}

	memcpy(packet->rbuf->data, buf + 4, packet_len - 4);
	*parsed_len = packet_len;

	print_data_buffer(packet->rbuf);
	packet->message_type = read_pos_int16(packet->rbuf, 2);

	return packet;
}

struct slave_sync_res *parse_master_response(struct mile_packet *packet) {
	struct data_buffer* rbuf = packet->rbuf;
	struct mile_message_header header;

	if (NULL == rbuf) {
		log_error( "read buffer is empty");
		return NULL;
	}

	header.version_major = read_int8(rbuf);
	header.version_minor = read_int8(rbuf);
	header.message_type = read_int16(rbuf);
	header.message_id = read_int32(rbuf);

	log_debug(
			"数据包包头信息 -- 主版本号: %d, 小版本号: %d, 消息类型: 0x%x, 消息id: %d", header.version_major, header.version_minor, header.message_type, header.message_id);

	if (MT_DM_RS != header.message_type) {
		log_error( "unknown message type: %hu", header.message_type);
		return NULL;
	}

	// read body
	struct slave_sync_res *res = (struct slave_sync_res *) (rbuf->data
			+ rbuf->rpos);
	rbuf->rpos += res->len + sizeof(struct slave_sync_res);
	if (rbuf->rpos > rbuf->data_len) {
		log_error( "wrong packet lenght.");
		return NULL;
	}
	return res;
}

int32_t read_message_from_mergeserver(struct mile_packet *packet) {
	if (NULL == packet) {
		return ERROR_DATA_RECEIVE;
	}
	struct data_buffer* rbuf = packet->rbuf;
	int32_t result;
	int32_t rec_len;
	int32_t data_len;

	log_debug("从merge server接收查询命令!");

	//获取数据包的长度
	rec_len = 4;
	while (rec_len > 0) {
		result = recv(packet->socket_fd, (uint8_t*) (&data_len) + 4 - rec_len,
				rec_len, 0);
		if (result == 0) {
			return ERROR_DATA_RECEIVE;
		}
		if (result == -1) {
			if (errno == EAGAIN || errno == EWOULDBLOCK) {
				continue;
			}
			return ERROR_DATA_RECEIVE;
		}
		rec_len -= result;
	}

	data_len = convert_int32(data_len);

	if (data_len <= 0) {
		log_error("从merge server接收到 %d 字节数据, 可能是错误的数据报文!", data_len);
		return ERROR_DATA_RECEIVE;
	} else {
		log_debug("从merge server接收到 %d 字节数据", data_len);
	}

	data_len -= 4;
	rec_len = data_len;

	rbuf->array_border = 0;
	rbuf->rpos = 0;
	rbuf->wpos = 0;
	//在这里向系统申请内存, 一定要注意释放
	databuf_resize(rbuf, data_len);
	rbuf->data_len = data_len;

	if (rbuf->data == NULL) {
		log_error("接收消息时申请内存失败, 申请内存长度 %d", data_len);
		return ERROR_NOT_ENOUGH_MEMORY;
	}

	while (rec_len > 0) {
		result = recv(packet->socket_fd, rbuf->data + data_len - rec_len,
				rec_len, 0);

		if (result == 0) {
			return ERROR_DATA_RECEIVE;
		}
		if (result == -1) {
			if (errno == EAGAIN || errno == EWOULDBLOCK) {
				log_warn("在接收数据时遇到EAGAIN错误!");
				continue;
			}
			return ERROR_DATA_RECEIVE;
		}
		rec_len -= result;
	}

	packet->message_type = read_pos_int16(rbuf, 2);
	return MILE_RETURN_SUCCESS;
}

int32_t send_result_to_master(struct mile_packet *packet) {
	// blocking io.

	if (NULL == packet) {
		return ERROR_DATA_SEND;
	}

	struct data_buffer* sbuf = packet->sbuf;

	if (NULL == sbuf) {
		log_error( "send buf is empty");
		return ERROR_DATA_SEND;
	}

	log_debug( "send request to master");

	int rc = write(packet->socket_fd, sbuf->data, sbuf->data_len);
	if (rc < (int) sbuf->data_len) {
		log_error( "send packet to master failed, rc %d, errno %d", rc, errno);
		return ERROR_DATA_SEND;
	}

	return MILE_RETURN_SUCCESS;
}

int32_t send_result_to_mergeserver(struct mile_packet *packet) {
	if (NULL == packet) {
		return ERROR_DATA_SEND;
	}

	struct data_buffer* sbuf = packet->sbuf;
	int32_t result;
	int32_t send_len;
	uint64_t now_time = get_time_msec();

	if (packet->timeout != 0
			&& now_time - packet->launch_time / 1000 > packet->timeout) {
		log_warn(
				"执行sql超时, 收到sql命令时的时间 %llu, 当前时间 %llu, 超时时间 %u", packet->launch_time / 1000, now_time, packet->timeout);
		return ERROR_TIMEOUT;
	}

	if (sbuf == NULL) {
		log_error("严重错误, 发送buffer为空!");
		return ERROR_DATA_SEND;
	}

	log_debug("向merge server发送查询结果!");

	send_len = sbuf->data_len;
	while (send_len > 0) {
		result = send(packet->socket_fd, sbuf->data + sbuf->data_len - send_len,
				send_len, 0);
		if (result == 0) {
			return ERROR_DATA_SEND;
		}
		if (result == -1) {
			if (errno == EAGAIN || errno == EWOULDBLOCK) {
				log_warn("在发送数据时遇到EAGAIN错误!");
				continue;
			}
			return ERROR_DATA_SEND;
		}
		send_len -= result;
	}

	return MILE_RETURN_SUCCESS;

}

/* send back a batch of result  */
int32_t send_multi_result_to_mergeserver(struct mile_packet *packet) {
	return 0;

}

struct multi_mile_packet * multi_init_mile_packet(int32_t socket_fd,
		uint32_t max_len) {
	struct multi_mile_packet* packet = (struct multi_mile_packet*) malloc(
			sizeof(struct multi_mile_packet));

	if (packet == NULL) {
		log_error("初始化multi mile packet时申请内存失败!");
		return NULL;
	}

	packet->packet = NULL;
	packet->socket_fd = socket_fd;
	packet->sql_count = 0;
	packet->len = max_len;
	INIT_LIST_HEAD(&packet->packet_list_h);

	return packet;
}

int32_t get_message_count(struct multi_mile_packet *multi_packet) {
	int32_t result;
	int32_t rec_len;
	int32_t sql_count = 0;
	int32_t ret = 0;

	log_debug("从merge server接收查询命令!");

	// get count of sql request in this packet
	rec_len = 4;
	while (rec_len > 0) {
		result = recv(multi_packet->socket_fd,
				(uint8_t*) (&sql_count) + 4 - rec_len, rec_len, 0);
		if (result == 0) {
			return ERROR_DATA_RECEIVE;
		}
		if (result == -1) {
			if (errno == EAGAIN || errno == EWOULDBLOCK) {
				continue;
			}
			return ERROR_DATA_RECEIVE;
		}
		rec_len -= result;
	}

	sql_count = convert_int32(sql_count);

	if (sql_count <= 0) {
		log_error("从merge server接收到 %d 字节数据, 可能是错误的数据报文!", sql_count);
		return ERROR_DATA_RECEIVE;
	}

	multi_packet->sql_count = sql_count;

	return ret;

}

