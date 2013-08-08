// DocEngine.cpp : DocEngine
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#include "DocEngine.h"
#include "DocTableMgr.h"

#include "../../common/ConfigFile.h"

#include "db.h"

static void set_storage_dir(struct str_array_t *dirs, const char *config_value,
		MEM_POOL_PTR mem);

DocEngine::DocEngine(const char *storage_dir, const ConfigFile &conf) :
		storage_dir_(storage_dir), conf_(&conf), db_conf_(NULL) {
	mem_ = mem_pool_init(1 << 20);
}

DocEngine::~DocEngine() {
	db_release();
	mem_pool_destroy(mem_);
	mem_ = NULL;
}

void DocEngine::Ref() {
	db_read_lock();
}

void DocEngine::UnRef() {
	db_read_unlock();
}

TableManager *DocEngine::GetTableManager(const char *table_name,
		MEM_POOL_PTR mem_pool) {
	assert(NULL != table_name);

	return NEW(mem_pool, DocTableMgr)(this, table_name, mem_pool);
}

int DocEngine::Init() {
	if (LoadConfig() != 0) {
		log_error("load config failed");
		return -1;
	}

	int rc = db_init(db_conf_);
	if (0 != rc) {
		log_error("db_init failed, rc %d", rc);
		return -1;
	}

	return 0;
}

int DocEngine::LoadConfig() {
	db_conf_ = (db_conf *) mem_pool_malloc(mem_, sizeof(*db_conf_));
	::memset(db_conf_, 0, sizeof(*db_conf_));

	::strcpy(db_conf_->binlog_dir,
			conf_->GetValue(CONF_SERVER_SESSION, "binlog_dir",
					DEFAULT_BINLOG_DIR));
	db_conf_->binlog_maxsize = conf_->GetIntValue(CONF_SERVER_SESSION,
			"binlog_maxsize", DEFAULT_BINLOG_FILESIZE);
	db_conf_->binlog_flag = conf_->GetIntValue(CONF_SERVER_SESSION,
			"binlog_flag", 0);
	db_conf_->binlog_threshold = conf_->GetIntValue(CONF_SERVER_SESSION,
			"binlog_threshold", 0);
	db_conf_->binlog_sync_interval = conf_->GetIntValue(CONF_SERVER_SESSION,
			"binlog_sync_interval", 1 * 1000); // 1 second

	db_conf_->checkpoint_interval = conf_->GetIntValue(CONF_DOCDB_SESSION,
			"checkpoint_interval", 60 * 30);
	db_conf_->cpu_threshold = conf_->GetIntValue(CONF_DOCDB_SESSION,
			"cpu_threshold", 90);

	

	db_conf_->table_store_only_index = init_string_map(mem_, 256);
	const char* table_names = conf_->GetValue(CONF_DOCDB_SESSION, "store_only_index", NULL);

	if(table_names != NULL){
		char tmp[1000];
		strcpy(tmp, table_names);
		char* table_name;
		table_name = strtok(tmp, ",");
		while(table_name != NULL){
			string_map_put(db_conf_->table_store_only_index, table_name, (void*)1, 1);
			table_name = strtok(NULL, ",");
		}
	}

	 

	const char *value = conf_->GetValue(CONF_SERVER_SESSION, "role", NULL);
	if (strcmp(value, "master") == 0)
		db_conf_->role = MASTER_ROLE;
	else if (strcmp(value, "slave") == 0)
		db_conf_->role = SLAVE_ROLE;
	else {
		log_error("invalid role: %s", value);
		return -1;
	}

	db_conf_->hash_compress_num = conf_->GetIntValue(CONF_DOCDB_SESSION,
			"hash_compress_num", 10);
	db_conf_->max_segment_num = conf_->GetIntValue(CONF_DOCDB_SESSION,
			"max_segment_num", 1024);
	db_conf_->row_limit = conf_->GetIntValue(CONF_DOCDB_SESSION, "row_limit",
			10000000);
	db_conf_->profiler_threshold = conf_->GetIntValue(CONF_DOCDB_SESSION,
			"profiler_threshold", 100000);

	db_conf_->max_result_size = conf_->GetIntValue(CONF_DOCDB_SESSION,
			"max_result_size", 0); // no limit

	db_conf_->cut_threshold=conf_->GetIntValue(CONF_DOCDB_SESSION,"cut_threshold",0);
	
	set_storage_dir(&db_conf_->storage_dirs, storage_dir_.c_str(), mem_);

	mile_conf.disk_write_limit = conf_->GetInt64Value(CONF_DOCDB_SESSION,
			"disk_write_limit", 0);
	mile_conf.all_mmap = conf_->GetIntValue(CONF_DOCDB_SESSION, "all_mmap", 0);

	return 0;
}

int32_t DocEngine::getCutThreshold()
{
	return db_conf_->cut_threshold;
}

ResultSet* DocEngine::GetKvs(struct get_kvs_packet* packet,
		MEM_POOL_PTR mem_pool) {
	int32_t result_code = MILE_RETURN_SUCCESS;
	uint32_t i, j;
	uint16_t sid;
	uint32_t rowid;
	struct select_row_t* row;
	struct low_data_struct** sel_fields;

	Clonable* clone =
			new (mem_pool_malloc(mem_pool, sizeof(RowClone))) RowClone(
					mem_pool);
	CommonRS* result =
			new (mem_pool_malloc(mem_pool, sizeof(CommonRS))) CommonRS(packet->docid_num, clone,
					mem_pool);
	char** field_names = (char**) mem_pool_malloc(mem_pool,
			sizeof(char*) * packet->select_field.n);
	for (i = 0; i < packet->select_field.n; i++) {
		field_names[i] = packet->select_field.select_fields[i].field_name;
	}

	for (i = 0; i < packet->docid_num; i++) {
		sid = packet->docids[i] >> 32;
		rowid = (uint32_t)packet->docids[i];

		sel_fields = db_data_query_multi_col(packet->table_name, sid, rowid, field_names,
				packet->select_field.n, DATA_ACCESS_ORIGINAL, mem_pool);
		if(NULL == sel_fields){
			return NULL;
		}

		row = init_select_row_t(mem_pool, packet->select_field.n);
		for(j = 0; j < packet->select_field.n; j++){
			row->data[j] = *sel_fields[j];
			row->select_type[j] = SELECT_TYPE_ORIGINAL;
		}
		row->handler = new(mem_pool_malloc(mem_pool, sizeof(DocHandler)))DocHandler(packet->docids[i]);

		result->AddResult(row);
	}

	return result;
}


int DocEngine::SpecialSql(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf){
	MEM_POOL_PTR mem_pool;
	void* packet;
	void* result;
	int32_t result_code = MILE_RETURN_SUCCESS;
	uint16_t msg_type = msg_head->message_type;


	switch (msg_type) {
	case MT_MD_EXE_GET_KVS:
		mem_pool = mem_pool_init(MB_SIZE);
		packet = parse_get_kvs_packet(mem_pool, rbuf);
		result = GetKvs((struct get_kvs_packet*) packet, mem_pool);

		if(NULL == result){
			result_code = ERROR_QUERY_BY_ROWID;
		}else{
			gen_query_result_packet((ResultSet*) result, msg_head,
					sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	default:
		log_error("unsupported command, packet type %d", msg_type);
		result_code = ERROR_UNSUPPORTED_SQL_TYPE;
		break;
	}

	return result_code;
}





int DocEngine::Command(struct mile_message_header* msg_head,
		struct data_buffer* rbuf, struct data_buffer* sbuf) {
	MEM_POOL_PTR mem_pool;
	void* parsed_packet;
	void* result;
	int32_t result_code = 0;
	uint16_t msg_type = msg_head->message_type;

	switch (msg_type) {
	case MT_MD_EXE_GET_KVS:
		mem_pool = mem_pool_init(MB_SIZE);
		parsed_packet = parse_get_kvs_packet(mem_pool, rbuf);
		result = GetKvs((struct get_kvs_packet*) parsed_packet, mem_pool);

		if(NULL == result){
			result_code = ERROR_QUERY_BY_ROWID;
		}else{
			gen_query_result_packet((ResultSet*) result, msg_head,
					sbuf);
		}
		mem_pool_destroy(mem_pool);
		break;
	case MT_CD_EXE_COMPRESS:
		mem_pool = mem_pool_init(KB_SIZE);
		parsed_packet = parse_compress_packet(mem_pool, rbuf);

		log_debug(
				"接收到compress命令:%s", ((struct compress_packet*)parsed_packet)->table_name);

		db_read_lock();
		result_code = db_compress(
				((struct compress_packet*) parsed_packet)->table_name,
				mem_pool);
		db_read_unlock();

		gen_dc_response_packet(result_code, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
		break;
	case MT_CD_EXE_INDEX:
		mem_pool = mem_pool_init(KB_SIZE);
		parsed_packet = parse_ensure_index_packet(mem_pool, rbuf);

		log_debug(
				"接收到ensure_index命令 %s %s %u %u", ((struct ensure_index_packet*)parsed_packet)->table_name, ((struct ensure_index_packet*)parsed_packet)->field_name, ((struct ensure_index_packet*)parsed_packet)->index_type, ((struct ensure_index_packet*)parsed_packet)->data_type);

		db_read_lock();
		result_code = db_ensure_index(
				((struct ensure_index_packet*) parsed_packet)->table_name,
				((struct ensure_index_packet*) parsed_packet)->field_name,
				((struct ensure_index_packet*) parsed_packet)->index_type,
				((struct ensure_index_packet*) parsed_packet)->data_type,
				mem_pool);

		db_read_unlock();
		gen_dc_response_packet(result_code, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
		break;

	case MT_CD_EXE_UNINDEX:
		mem_pool = mem_pool_init(KB_SIZE);
		parsed_packet = parse_del_index_packet(mem_pool, rbuf);

		log_debug(
				"接收到del_index命令 %s %s %u", ((struct del_index_packet*)parsed_packet)->table_name, ((struct del_index_packet*)parsed_packet)->field_name, ((struct del_index_packet*)parsed_packet)->index_type);

		db_read_lock();
		result_code = db_del_index(
				((struct del_index_packet*) parsed_packet)->table_name,
				((struct del_index_packet*) parsed_packet)->field_name,
				((struct del_index_packet*) parsed_packet)->index_type,
				mem_pool);
		db_read_unlock();

		gen_dc_response_packet(result_code, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
		break;

	case MT_CD_EXE_LOAD:
		mem_pool = mem_pool_init(KB_SIZE);
		parsed_packet = parse_load_segment_packet(mem_pool, rbuf);

		log_debug(
				"接收到load_segment命令 %s %u %s", ((struct load_segment_packet*)parsed_packet)->table_name, ((struct load_segment_packet*)parsed_packet)->sid, ((struct load_segment_packet*)parsed_packet)->segment_dir);

		db_read_lock();
		result_code = db_load_segment(
				((struct load_segment_packet*) parsed_packet)->table_name,
				((struct load_segment_packet*) parsed_packet)->sid,
				((struct load_segment_packet*) parsed_packet)->segment_dir,
				mem_pool);
		db_read_unlock();

		gen_dc_response_packet(result_code, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
		break;

	case MT_CD_EXE_REPLACE:
		mem_pool = mem_pool_init(KB_SIZE);
		parsed_packet = parse_replace_segment_packet(mem_pool, rbuf);

		log_debug(
				"接收到replace segments命令 %s %s", ((struct replace_segment_packet*)parsed_packet)->table_name, ((struct replace_segment_packet*)parsed_packet)->segment_dir);

		db_read_lock();
		result_code = db_replace_all_segments(
				((struct replace_segment_packet*) parsed_packet)->table_name,
				((struct replace_segment_packet*) parsed_packet)->segment_dir,
				mem_pool);
		db_read_unlock();

		gen_dc_response_packet(result_code, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
		break;

	case MT_CD_EXE_UNLOAD:
		mem_pool = mem_pool_init(KB_SIZE);

		parsed_packet = parse_unload_segment_packet(mem_pool, rbuf);

		log_debug(
				"接收到unload_segment命令 %s %u", ((struct unload_segment_packet*)parsed_packet)->table_name, ((struct unload_segment_packet*)parsed_packet)->sid);

		db_read_lock();
		result_code = db_unload_segment(
				((struct unload_segment_packet*) parsed_packet)->table_name,
				((struct unload_segment_packet*) parsed_packet)->sid, mem_pool);
		db_read_unlock();

		gen_dc_response_packet(result_code, msg_head, sbuf);
		mem_pool_destroy(mem_pool);
		break;

	case MT_CD_EXE_CP:
		log_debug( "get checkpoint command");
		db_checkpoint();
		gen_dc_response_packet(0, msg_head, sbuf);
		break;
	case MT_CD_STAT:
		mem_pool = mem_pool_init(MB_SIZE);
		parsed_packet = parse_doc_stat_packet(mem_pool, rbuf);

		log_debug(
				"接收到stat命令 %s %u", ((struct doc_stat_packet*)parsed_packet)->table_name, ((struct doc_stat_packet*)parsed_packet)->type);

		if (((struct doc_stat_packet*) parsed_packet)->type == 1) {
			uint16_t max_segment_num = 0;
			struct segment_meta_data* result = NULL;

			db_read_lock();
			result = db_query_segment_stat(
					((struct doc_stat_packet*) parsed_packet)->table_name,
					&max_segment_num, mem_pool);
			db_read_unlock();

			gen_dc_segment_stat_packet(result, max_segment_num, msg_head, sbuf);
		}

		if (((struct doc_stat_packet*) parsed_packet)->type == 2) {
			uint16_t index_field_count = 0;
			struct index_field_meta* result = NULL;

			db_read_lock();
			result = db_query_index_stat(
					((struct doc_stat_packet*) parsed_packet)->table_name,
					&index_field_count, mem_pool);
			db_read_unlock();

			gen_dc_index_stat_packet(result, index_field_count, msg_head, sbuf);
		}

		mem_pool_destroy(mem_pool);
		break;
	default:
		log_error("unsupported command, packet type %d", msg_type);
		result_code = ERROR_UNSUPPORTED_SQL_TYPE;
		break;
	}

	return result_code;
}

static void set_storage_dir(struct str_array_t *dirs, const char *config_value,
		MEM_POOL_PTR mem) {
	assert(NULL != config_value);

	// get dir number
	dirs->n = 0;
	for (const char *p = config_value; *p != '\0';) {
		int n = strcspn(p, ";");
		if (n > 0)
			dirs->n++;
		p += n;
		if (*p == ';')
			p++;
	}

	assert(dirs->n > 0);
	// config_value only contain ',' will fail

	// alloc memory
	dirs->strs = (char **) mem_pool_malloc(mem, sizeof(char *) * dirs->n);

	char *p = (char *) mem_pool_malloc(mem, strlen(config_value) + 1);
	strcpy(p, config_value);

	// set dir pointers
	for (int i = 0; *p != '\0';) {
		dirs->strs[i] = p;
		int n = strcspn(p, ";");
		if (p[n] == '\0')
			break;
		else {
			p[n] = '\0';
			p += n + 1;
			if (n > 0)
				i++;
		}
	}
}

const binlog_writer *DocEngine::GetBinlogWriter() {
	return get_db()->binlog_writer;
}

int DocEngine::ApplyBinlog(struct binlog_record *record,
		MEM_POOL_PTR mem_pool) {
	return db_execute_binrecord(record, mem_pool);
}

void DocEngine::SetReadable(bool readable) {
	if (readable)
		set_db_readable();
	else
		set_db_unreadable();
}


bool DocEngine::GetReadable(){
	return get_db()->readable == 1;
}


uint64_t DocEngine::SlaveSyncPos() {
	return db_start_catch_up();
}

int DocEngine::SetSlaveSyncPos(uint64_t offset_cur, uint64_t offset_left) {
	return db_slave_set_offset(offset_cur, offset_left);
}
