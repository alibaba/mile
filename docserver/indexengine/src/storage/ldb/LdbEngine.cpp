// LdbEngine.cpp : LdbEngine
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#include "LdbEngine.h"
#include "LdbConfig.h"
#include "LdbTableImpl.h"
#include "LdbTableMgr.h"
#include "LdbIterator.h"

#include "../../common/log.h"
#include "../../common/mem.h"
#include "../../common/ConfigFile.h"
#include "../../protocol/packet.h"

#include <stdarg.h>

#include <set>
#include <boost/algorithm/string.hpp>

#define BINLOG_META_NAME "binlog.meta"

// split command to arguments, like shell does.
static int split_command(std::vector<std::string> &str_vec, const std::string &cmd);

LdbEngine::LdbEngine(const char *storage_dir, const ConfigFile &conf)
	: storage_dir_(storage_dir), conf_(&conf), mem_(NULL)
	, bl_enabled_(0), bl_sync_interval_(0), bl_writer_(NULL), sync_pos_(NULL)
{
	all_property_.push_back("ref-count");
	all_property_.push_back("memtables");
	all_property_.push_back("block-cache");
	all_property_.push_back("stats");
	all_property_.push_back("files-per-level");
	all_property_.push_back("sstables");
}

LdbEngine::~LdbEngine()
{
	// destory binlog
	if (sync_pos_) {
		munmap(sync_pos_, sizeof(*sync_pos_));
		sync_pos_ = NULL;
	}

	if (bl_enabled_ && NULL != bl_writer_) {
		// wait binlog sync thread
		if (bl_sync_interval_ > 0) {
			pthread_join(sync_pid_, NULL);
		}

		binlog_writer_destroy(bl_writer_);
		bl_writer_ = NULL;
	}

	for (STR_DBPTR_MAP::iterator it = db_map_.begin(); it != db_map_.end(); ++it) {
		it->second->UnRef();
		delete it->second;
	}

	if (mem_)
		mem_pool_destroy(mem_);
}

int LdbEngine::LoadConf(std::vector<std::string> &tables, leveldb::Options &opt)
{
	// binlog config
	bl_enabled_ = conf_->GetIntValue(CONF_SERVER_SESSION, "binlog_flag", 0);
	bl_sync_interval_ = conf_->GetInt64Value(CONF_SERVER_SESSION, "binlog_sync_interval", 0);

	// load tables
	tables.clear();
	std::string tables_str = conf_->GetValue(CONF_LDB_SESSION, CONF_LDB_TABLES, "");
	if (tables_str.empty()) {
		log_error("no leveldb tables configured");
		return -1;
	}
	boost::split(tables, tables_str, boost::is_any_of(",;"), boost::token_compress_on);

	std::vector<std::string>::iterator it = tables.begin();
	for (; it != tables.end(); ++it) {
		boost::trim(*it);
	}

	if (!CheckTableName(tables))
		return -1;

	// set default value
	opt.write_buffer_size = 32 << 20; // 32MB
	opt.max_open_files = 1 << 16; // 65536
	opt.block_size = 4 * 1024; // 4KB
	opt.block_restart_interval = 16;
	opt.paranoid_checks = 0;
	opt.target_file_size = 8 << 20;
	opt.block_cache_size = 40 << 20;
	opt.l0_compact_trigger = 4;
	opt.l0_slowdown_writes_trigger = 8;
	opt.l0_stop_writes_trigger = 12;
	opt.max_mem_compact_level = 2;
	opt.base_level_size = 20 << 20;
	opt.cut_threshold=0;
	LoadLevelDBConf(opt, *conf_, NULL);

	return 0;
}

void LdbEngine::LoadLevelDBConf(leveldb::Options &opt, const ConfigFile &conf, const char *prefix)
{
#define SET_LDB_OPT_VALUE(opt, field, conf, value_type, prefix) \
	(opt).field = (conf).Get ##value_type ## Value(CONF_LDB_SESSION, \
			(prefix == NULL) ? #field : (std::string(prefix) + "." + #field).c_str(), (opt).field)

	SET_LDB_OPT_VALUE(opt, write_buffer_size, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, max_open_files, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, block_size, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, block_restart_interval, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, paranoid_checks, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, target_file_size, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, block_cache_size, conf, Int64, prefix);
	SET_LDB_OPT_VALUE(opt, use_bloom_filter, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, l0_compact_trigger, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, l0_slowdown_writes_trigger, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, l0_stop_writes_trigger, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, max_mem_compact_level, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, base_level_size, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, allowed_seeks, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, allow_mmap_table, conf, Int, prefix);
	SET_LDB_OPT_VALUE(opt, cut_threshold,conf,Int,prefix);

#undef SET_LDB_OPT_VALUE
}
int32_t LdbEngine::getCutThreshold()
{
	return options_.cut_threshold;
}
bool LdbEngine::CheckTableName(const std::vector<std::string> &tables)
{
	std::set<std::string> s(tables.begin(), tables.end());
	if (s.size() != tables.size() || tables.size() == 0) {
		log_error("no leveldb table or has duplicate table");
		return false;
	}

	return true;
}

int LdbEngine::Init()
{

	std::string cmd = std::string("mkdir -p '") + storage_dir_ + "'";
	int rc = ::system(cmd.c_str());
	if (rc) {
		log_error("create storage dir %s failed", storage_dir_.c_str());
		return -1;
	}

	mem_ = mem_pool_init(1 << 10); // MB

	std::vector<std::string> tables;
	if (LoadConf(tables, options_) != 0) {
		log_error("load config failed");
		return -1;
	}

	if ((InitBinlog()) != 0) {
		log_error("init binlog failed");
		return -1;
	}

	std::vector<std::string>::const_iterator it = tables.begin();
	for ( ; it != tables.end(); ++it) {
		LdbTableImpl *table = new LdbTableImpl(*it, storage_dir_ + "/" + *it, bl_writer_);
		if (table->Init(*conf_, options_) != 0) {
			delete table;
			return -1;
		}

		table->Ref();
		db_map_.insert(std::make_pair(*it, table));
	}

	ASSERT(db_map_.size() == tables.size());
	ASSERT(db_map_.size() > 0);

	return 0;
}

TableManager *LdbEngine::GetTableManager(const char *table_name, MEM_POOL_PTR mem_pool)
{
	LdbTableImpl *impl = GetTableImpl(table_name);
	if (NULL == impl) {
		log_warn("table %s not found", table_name);
		return NULL;
	}

	return NEW(mem_pool, LdbTableMgr)(this, impl);
}


inline LdbTableImpl *LdbEngine::GetTableImpl(const std::string &table) {
	MUTEX_LOCK_T(mutex_, l);
	STR_DBPTR_MAP::iterator it = db_map_.find(table);
	if (it == db_map_.end()) {
		return NULL;
	}
	it->second->Ref();
	return it->second;
}


int LdbEngine::SpecialSql(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf){
	return -1;
}


int LdbEngine::Command(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf)
{
	if (msg_head->message_type != MT_CD_EXE_LDB_CONTROL)
		return -1;

	MEM_POOL_PTR mem_pool = mem_pool_init(MB_SIZE);

	// parse command
	char *cmd = read_cstring(rbuf, mem_pool);
	log_info("ldb control command: %s", cmd);

	std::string result;
	if (HandleClientCmd(&result, cmd + strlen("ldb")) != 0)  // skip "ldb" prefix
		result = "ldb process commmand [" + std::string(cmd) + "] failed.";
	mem_pool_destroy(mem_pool);

	// generate result packet
	clear_data_buffer(sbuf);
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DC_LDB_CONTROL, sbuf);
	write_int32(msg_head->message_id, sbuf);

	log_info("message type: %d", MT_DC_LDB_CONTROL);

	write_int32(result.size(), sbuf);
	write_bytes((uint8_t *)result.c_str(), result.size(), sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);

	return 0;
}

int LdbEngine::HandleClientCmd(std::string *result, std::string cmd)
{
	boost::trim(cmd);
	std::vector<std::string> parsed_cmd;
	split_command(parsed_cmd, cmd);

	if (parsed_cmd.size() < 1)
		return -1;

	str_vec_citer arg = parsed_cmd.begin();
	str_vec_citer arg_end = parsed_cmd.end();

	str_vec_citer c = arg++;

	if (*c == "stat") { // get stat for one table
		if (arg == arg_end) // no table specified
			return -1;
		str_vec_citer table = arg++;
		if (GetTableStat(result, *table, arg, arg_end) != 0)
			return -1;
	} else if (*c == "stat-all") { // get stat of all table
		std::vector<LdbTableImpl *> tables;  // get all tables
		{
			MUTEX_LOCK_T(mutex_, l);
			STR_DBPTR_MAP::iterator it = db_map_.begin();
			for (; it != db_map_.end(); ++it) {
				it->second->Ref();
				tables.push_back(it->second);
			}
		}
		std::vector<LdbTableImpl *>::iterator it = tables.begin();
		int rc = 0;
		for (; it != tables.end(); ++it) {
			if ( 0 == rc )
				rc = GetTableStat(result, *it, arg, arg_end);
			(*it)->UnRef();
		}
		return rc;
	} else if (*c == "create") { // create command
		if (arg == arg_end) return -1;
		str_vec_citer subarg = arg++;
		if (*subarg == "table") { // create table command
			if (arg == arg_end) return -1;
			str_vec_citer table = arg++;

			if (AddNewTable(result, *table, arg, arg_end) != 0)
				return -1;
		} else {
			log_error("unknow sub command [%s]", subarg->c_str());
			return -1;
		}
	} else if (*c == "delete") { // delete command
		if (arg == arg_end) return -1;
		str_vec_citer subarg = arg++;
		if (*subarg == "table") { // delete table command
			if (arg == arg_end) return -1;
			str_vec_citer table = arg++;

			int rc = DelTable(*table, arg != arg_end && *arg == "force");
			// generate response message
			*result = "delete table " + *table;
			*result += (rc == 0) ? " success!" : " failed!";
			return 0; // error response message generated, always return 0
		} else {
			log_error("unknow sub command [%s]", subarg->c_str());
			return -1;
		}
	} else {
		log_error("unknown ldb control command [%s]", c->c_str());
		return -1;
	}

	return 0;
}

int LdbEngine::GetTableStat(std::string *result, const std::string &table, str_vec_citer arg, str_vec_citer arg_end)
{
	LdbTableImpl *impl = GetTableImpl(table);
	if (NULL == impl)
		return -1;

	int rc = GetTableStat(result, impl, arg, arg_end);
	impl->UnRef();
	return rc;
}

int LdbEngine::GetTableStat(std::string *result, LdbTableImpl *table, str_vec_citer arg, str_vec_citer arg_end)
{
	if (arg == arg_end) { // get all stat
		arg = all_property_.begin();
		arg_end = all_property_.end();
	}

	result->append(80, '=');

	for (int i = 0; i < 2; i++) {
		if (i >= 1 && !table->HasCumulativeIndex())
			break;

		*result += "\n[TABLE: " + table->GetName() + (i == 0 ? "]\n" : ".cumulative_index]\n");

		for (str_vec_citer it = arg; it != arg_end; ++it) {
			std::string value;
			if (table->GetProperty(&value, i == 0 ? LdbTableImpl::STORE_DB : LdbTableImpl::INDEX_DB, *it) != 0)
				return -1;
			*result += "[" + *it + "]:\n" + value + "\n";
		}
	}

	return 0;
}

int LdbEngine::AddNewTable(std::string *new_conf_str, const std::string &table, str_vec_citer arg, str_vec_citer arg_end)
{
	MUTEX_LOCK_T(add_table_mutex_, l1);
	LdbTableImpl *impl = GetTableImpl(table);
	if (impl != NULL) {
		impl->UnRef();
		log_error("table %s already exist!", table.c_str());
		return -1;
	}

	// genrate config contain new table
	ConfigFile new_conf(*conf_);
	std::string session(CONF_LDB_SESSION);
	for (; arg != arg_end; ++arg) {
		new_conf.ParseLine(&session, *arg);
	}

	// create table
	impl = new LdbTableImpl(table, storage_dir_ + "/" + table, bl_writer_);
	if (impl->Init(new_conf, options_) != 0) {
		delete impl;
		return -1;
	}

	*new_conf_str = new_conf.Dump();

	// add to db
	impl->Ref();
	MUTEX_LOCK_T(mutex_, l2);
	db_map_.insert(std::make_pair(table, impl));

	return 0;
}

int LdbEngine::DelTable(const std::string &table, bool remove_data)
{
	MUTEX_LOCK_T(add_table_mutex_, l1);

	LdbTableImpl *impl = NULL;
	{
		MUTEX_LOCK_T(mutex_, l2);
		STR_DBPTR_MAP::iterator it = db_map_.find(table);
		if (it == db_map_.end())
			impl = NULL;
		else {
			impl = it->second;
			db_map_.erase(it);
		}
	}

	if (!impl) {
		log_error("table [%s] not found", table.c_str());
		return -1;
	}

	impl->UnRef();
	delete impl;

	if (!remove_data)
		return 0;

	std::string cmd = "rm -rf '" + storage_dir_ + "/" + table + "'";
	int rc = ::system(cmd.c_str());
	if (rc != 0) {
		log_error("rm table dir failed, cmd [%s]", cmd.c_str());
	}

	return rc;
}

// split command to arguments, like shell does.
int split_command(std::vector<std::string> &str_vec, const std::string &cmd)
{
	std::string token;
	bool in_escape = false;
	char quote_char = '\0'; // quote_char == '\0' means not in quote
	bool token_add = true; // token added to str_vec flag

	for (size_t i = 0; i < cmd.size(); i++) {
		const char c = cmd.at(i);
		// escaped by '\'
		if (in_escape) {
			in_escape = false;
		} else {
			if (c == '\\' && quote_char != '\'') {
				in_escape = true;
				continue;
			}

			// quote string
			if (c == '"' || c == '\'') {
				if (quote_char == '\0') { // not in quote
					quote_char = c;
					token_add = false; // support empty quote string
					continue;
				} else if (c == quote_char) { // end quote
					quote_char = '\0';
					continue;
				}
			}

			// add token to str_vec
			if (quote_char == '\0' && isspace(c)) {
				if (!token_add) {
					str_vec.push_back(token);
					token.clear();
					token_add = true;
				}
				continue;
			}
		}

		token.append(1, c);
		token_add = false;
	}

	if (!token_add)
		str_vec.push_back(token);

	// check whether quote string terminated
	return (quote_char != '\0') ? -1 : 0;
}

int LdbEngine::InitBinlog()
{
	// mmap sync pos
	const char *role = conf_->GetValue(CONF_SERVER_SESSION, "role", "");
	if (strcasecmp(role, "slave") == 0) {
		std::string path = storage_dir_ + "/" + BINLOG_META_NAME;
		sync_pos_ = (uint64_t *)get_mmap_memory((char *)path.c_str(), sizeof(*sync_pos_));
		ASSERT(NULL != sync_pos_);
	}

	// init binlog writer
	if (!bl_enabled_)
		return 0;

	const char *binlog_dir = conf_->GetValue(CONF_SERVER_SESSION, "binlog_dir", NULL);
	if (NULL == binlog_dir)
		return -1;
	// mkdir -p
	std::string cmd = std::string("mkdir -p ") + "'" + binlog_dir + "'";
	int rc = system(cmd.c_str());
	if (0 != rc) {
		log_error("make binlog_dir failed, cmd [%s], rc %d", cmd.c_str(), rc);
		return -1;
	}
	uint32_t binlog_maxsize = conf_->GetIntValue(CONF_SERVER_SESSION, "binlog_maxsize", 200 << 20);
	bl_writer_ = binlog_writer_init(binlog_dir, binlog_maxsize, mem_);
	if (NULL == bl_writer_)
		return -1;

	// start binlog sync thread
	if (bl_sync_interval_ == 0)
		bl_writer_->sync_immediately = 1;
	else
		pthread_create(&sync_pid_, NULL, &LdbEngine::BinlogSync, this);

	return 0;
}

void *LdbEngine::BinlogSync(void *arg)
{
	LdbEngine *engine = (LdbEngine *)arg;
	while (g_running_flag) {
		MILE_USLEEP(engine->bl_sync_interval_ * 1000);
		binlog_sync(engine->bl_writer_);
	}

	return NULL;
}

int LdbEngine::RecordBinlog(binlog_writer *writer, const char *table_name, 
			binlog_record_operation_code op, const char *data, uint32_t len, MEM_POOL_PTR mem)
{
	uint8_t tlen = ::strlen(table_name);

	binlog_record *record = create_binlog_record(len + tlen + 1, mem);
	record->op_code = op;
	*(uint8_t *)record->data = tlen;
	::memcpy(record->data + 1, table_name, tlen);
	::memcpy(record->data + 1 + tlen, data, len);

	return binlog_write_record(writer, record, mem);
}

int LdbEngine::RecordBinlog(binlog_writer *writer, const char *table_name,
		binlog_record_operation_code op, MEM_POOL_PTR mem, ...)
{
	uint8_t tlen = ::strlen(table_name);

	// get data len
	int data_len = 0;
	va_list vl;
	va_start(vl, mem);
	while (true) {
		const char *data = va_arg(vl, const char *);
		if (!data)
			break;
		data_len += 4 + va_arg(vl, uint32_t);
	}
	va_end(vl);

	// copy data
	binlog_record *record = create_binlog_record((uint32_t)tlen + 1 + data_len, mem);
	record->op_code = op;
	uint32_t off = 0;
	*(uint8_t *)record->data = tlen;
	off++;
	::memcpy(record->data + off, table_name, tlen);
	off += tlen;

	va_start(vl, mem);
	while (true) {
		const char *data = va_arg(vl, const char *);
		if (!data)
			break;
		uint32_t len = va_arg(vl, uint32_t);
		::memcpy(record->data + off, &len, 4);
		off += 4;
		::memcpy(record->data + off, data, len);
		off += len;
	}
	va_end(vl);

	ASSERT(off == (uint32_t)tlen + 1 + data_len);
	return binlog_write_record(writer, record, mem);
}

int LdbEngine::ParseBinlogNData(low_data_struct *result, int count, low_data_struct *input)
{
	uint32_t off = 0;
	int res = 0;
	for (; res < count; res++) {
		if (off + 1 >= input->len)
			break;
		low_data_struct *data = &result[res];
		data->len = *(uint32_t *)((char *)input->data + off);
		off += 4;
		data->data = ((char *)input->data + off);
		off += data->len;
	}

	return res;
}

static const char *extract_table_name(low_data_struct &remain, binlog_record *record, MEM_POOL_PTR mem)
{
	uint8_t tlen = *(uint8_t *)record->data;
	ASSERT(record->len >= tlen + 1 + sizeof(*record));

	char *name = (char *)mem_pool_malloc(mem, tlen + 1);
	::memcpy(name, record->data + 1, tlen);
	name[tlen] = '\0';

	remain.data = record->data + 1 + tlen;
	remain.len = record->len - 1 - tlen - sizeof(*record);

	return name;
}

/*
 * WARNNING: 
 * Problem with OPERATION_LDB_DELETE and OPERATION_LDB_UPDATE if LdbTableImpl::UseHostTime() is true.
 * Because value of time_key is generated by slave not from binlog.
 */
int LdbEngine::ApplyBinlog(struct binlog_record *record, MEM_POOL_PTR mem_pool)
{
	switch (record->op_code) {
	case OPERATION_INSERT:
	case OPERATION_LDB_DELETE:
	case OPERATION_LDB_UPDATE:
		{
			low_data_struct low;
			const char *name = extract_table_name(low, record, mem_pool);

			TableManager *mgr = GetTableManager(name, mem_pool);
			if (!mgr) {
				log_error("table %s not exist!", name);
				return -1;
			}

			int rc = 0;
			switch (record->op_code) {
			case OPERATION_INSERT:
				{
					struct row_data *row = lowdata_to_rowdata(&low, mem_pool);
					MileHandler* handler = NULL;
					rc = mgr->InsertRow(handler, row, mem_pool);
				}
				break;
			case OPERATION_LDB_DELETE:
				{
					LdbHandler ldb_handler;
					ldb_handler.key = low; // only key used in DeleteRow
					rc = mgr->DeleteRow(&ldb_handler, mem_pool);
				}
				break;
			case OPERATION_LDB_UPDATE:
				{
					low_data_struct values[3];
					int n = ParseBinlogNData(values, 3, &low);
					if (n != 3) {
						log_error("invalid update binlog");
						rc = -1;
					} else {
						LdbHandler ldb_handler;
						ldb_handler.key = values[0];
						ldb_handler.value = values[1];

						row_data *row = lowdata_to_rowdata(&values[2], mem_pool);
						rc = mgr->UpdateRow(&ldb_handler, row, mem_pool);
					}
				}
				break;
			}
			DELETE(mgr);
			return rc;
		}
		break;
	default:
		log_warn("unknow operation %d", record->op_code);
		// return 0 to let ldb engine can be docdb's slave, if only insert.
		return 0;
	}

	return 0;
}
