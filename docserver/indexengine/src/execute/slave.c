// hi_slave.c : hi_slave
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-07-06

#include "../common/def.h"
#include "../common/ConfigFile.h"
#include "../protocol/protocol.h"
#include "../protocol/packet.h"
#include "../common/mem.h"
#include "../storage/binlog.h"
#include "../storage/StorageEngine.h"

static struct binlog_record *make_record_copy(struct binlog_record *record, MEM_POOL_PTR mem);
static int execute_binrecord(struct binlog_record **prev, struct binlog_record *record, MEM_POOL_PTR mem);

int sync_with_master(int master_fd, uint64_t cur_offset, struct data_buffer *buffer, uint32_t *message_id, MEM_POOL_PTR mem)
{
	struct binlog_record *prev_record = NULL, *record = NULL;

	int sleep_interval = 0;
	int pull_interval = ConfigFile::GlobalInstance()->GetIntValue(CONF_SERVER_SESSION, "slave_pull_interval", 200 * 1000); // 200ms
	int rc = 0;

	while (g_running_flag) {

		MILE_USLEEP(sleep_interval);
		if (!g_running_flag) {
			break;
		}

		// send request to master
		struct mile_packet *packet = init_mile_packet(master_fd, NULL);
		gen_slave_sync_req_packet( (*message_id)++, cur_offset, packet->sbuf);
		if (send_result_to_master(packet) != MILE_RETURN_SUCCESS) {
			log_error("send message to master failed.");
			rc = -1;
			destroy_mile_packet(packet);
			break;
		}

		// get response and process.
#define read_message_from_master read_message_from_mergeserver // same with message from mergeserver
		if (read_message_from_master(packet) != MILE_RETURN_SUCCESS) {
			log_error("read message from master failed.");
			rc = -1;
			destroy_mile_packet(packet);
			break;
		}
#undef read_message_from_master

		struct slave_sync_res *res = NULL;
		if ( ( res = parse_master_response(packet) ) == NULL) {
			log_error("parse master response failed.");
			rc = -1;
			destroy_mile_packet(packet);
			break;
		}

		if (res->return_code < 0 || 0 == res->len) {
			sleep_interval = pull_interval;
			destroy_mile_packet(packet);
			if (res->return_code < 0) {
				log_error("master error, try again after %d usec", sleep_interval);
			}
			else {
				log_debug("slave catch up with master");
				StorageEngine::storage->SetReadable(true);
			}
			continue;
		}
		else {
			log_debug("get %d binlog data from master", res->len);
			sleep_interval = 0;
		}

		// TODO : check check
		memcpy(buffer->data + buffer->wpos, res->data, res->len);
		buffer->wpos += res->len;

		while (buffer->wpos - buffer->rpos > 4) {  // bigger than sizeof( record->len )
			record = (struct binlog_record *)(buffer->data + buffer->rpos);
			if (record->len > buffer->wpos - buffer->rpos)  // less than a record
				break;
			if (execute_binrecord(&prev_record, make_record_copy(record, mem), mem) != MILE_RETURN_SUCCESS) {
				log_error("CRITICAL ERROR: execute binlog record failed, docserver exit");
				exit(EXIT_FAILURE);
			}
			buffer->rpos += record->len;
		}
		if (rc < 0) {
			destroy_mile_packet(packet);
			break;
		}
		// skip processed data
		memmove(buffer->data, buffer->data + buffer->rpos, buffer->wpos - buffer->rpos);
		buffer->wpos -= buffer->rpos;
		buffer->rpos = 0;

		cur_offset = res->offset;
		if (StorageEngine::storage->SetSlaveSyncPos(res->offset - buffer->wpos, res->remain_length + buffer->wpos) == MILE_SLAVE_CACTCH_UP) {
			StorageEngine::storage->SetReadable(true);
		}
		else {
			StorageEngine::storage->SetReadable(false);
		}

		destroy_mile_packet(packet);
	}

	return rc;
}

static struct binlog_record *make_record_copy(struct binlog_record *record, MEM_POOL_PTR mem)
{
	struct binlog_record *copy = (struct binlog_record *)mem_pool_malloc(mem, record->len);

	memcpy(copy, record, record->len);
	return copy;
}

static int execute_binrecord(struct binlog_record **prev, struct binlog_record *record, MEM_POOL_PTR mem)
{
	if (NULL == *prev || IS_CONFIRM_RECORD(*prev) ) {
		*prev = record;
		return 0;
	}
	if (!IS_CONFIRM_RECORD(record) ) { // previous record not confirmed
		log_warn("binlog record not confirmed, time %u, op_code %d, sid %u, docid %u",
				(*prev)->time, (*prev)->op_code, (*prev)->sid, (*prev)->docid);
		*prev = record;
		return 0;
	}

	if (record->op_code == OPERATION_CONFIRM_FAIL) {
		*prev = NULL;
		mem_pool_reset(mem);
		return 0;
	}
	else {   // confirm OK
		int ret = StorageEngine::storage->ApplyBinlog(*prev, mem);
		*prev = NULL;
		mem_pool_reset(mem);
		return ret;
	}
}

