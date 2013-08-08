// LdbEngine.h : LdbEngine
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#ifndef LDBENGINE_H
#define LDBENGINE_H

#include <string>
#include <map>
#include <vector>

#include "../StorageEngine.h"
#include "../binlog.h"
#include "LdbHandler.h"
#include "../../common/Mutex.h"
#include "../../common/def.h"
#include "../../common/file_op.h"

#include "leveldb/options.h"

class LdbTableImpl;
class ConfigFile;

typedef std::map<std::string, LdbTableImpl *> STR_DBPTR_MAP;

class LdbEngine : public StorageEngine
{
public:
	LdbEngine(const char *storage_dir, const ConfigFile &conf);	
	virtual ~LdbEngine();

	virtual int Init();
	virtual TableManager *GetTableManager(const char *table_name, MEM_POOL_PTR mem_pool);

	virtual int Command(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf);

	virtual int SpecialSql(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf);

	virtual const binlog_writer *GetBinlogWriter() { return bl_writer_; }

	virtual int ApplyBinlog(struct binlog_record *record, MEM_POOL_PTR mem_pool);

	virtual uint64_t SlaveSyncPos() { return *sync_pos_; }
	virtual int SetSlaveSyncPos(uint64_t offset_cur, uint64_t offset_left) { /* TODO */ *sync_pos_ = offset_cur; return 0; }
	virtual void SetReadable(bool readable) { /*TODO */ }
	virtual bool GetReadable() {return true;}

	static void LoadLevelDBConf(leveldb::Options &opt, const ConfigFile &conf, const char *prefix);
	int32_t getCutThreshold();

protected:
	virtual void Ref() {};
	virtual void UnRef() {};

private:
	typedef std::vector<std::string>::const_iterator str_vec_citer;
	int LoadConf(std::vector<std::string> &tables, leveldb::Options &opt);
	static bool CheckTableName(const std::vector<std::string> &tables);

	int HandleClientCmd(std::string *result, std::string cmd);

	int GetTableStat(std::string *result, const std::string &table, str_vec_citer arg, str_vec_citer arg_end);
	int GetTableStat(std::string *result, LdbTableImpl *table, str_vec_citer arg, str_vec_citer arg_end);

	LdbTableImpl *GetTableImpl(const std::string &table);

	int AddNewTable(std::string *new_conf_str, const std::string &table, str_vec_citer arg, str_vec_citer arg_end);

	int DelTable(const std::string &table, bool remove_data);

	int InitBinlog();

	static void *BinlogSync(void *arg);

	static int RecordBinlog(binlog_writer *writer, const char *table_name, 
			binlog_record_operation_code op, const char *data, uint32_t len, MEM_POOL_PTR mem);

	// va_arg :  const char *data, uint32_t len, [const char *data, uint32_t len] ..., NULL
	static int RecordBinlog(binlog_writer *writer, const char *table_name,
			binlog_record_operation_code op, MEM_POOL_PTR mem, ...);

	// parse binlog record's data part (exclude table_name) which si write by RecordBinlog (va_arg version)
	// return parsed low_data_struct count.
	static int ParseBinlogNData(low_data_struct *result, int count, low_data_struct *input);

	friend class LdbTableImpl;
private:
	SpinMutex mutex_;  // protect db_map_
	STR_DBPTR_MAP db_map_;

	Mutex add_table_mutex_; // synchronize AddNewTable and DelTable

	std::string storage_dir_;
	const ConfigFile *conf_;

	leveldb::Options options_;

	// all ldb property names
	std::vector<std::string> all_property_;

	// binlog
	MEM_POOL_PTR mem_;
	pthread_t sync_pid_;
	int bl_enabled_;
	int64_t bl_sync_interval_;
	binlog_writer *bl_writer_;
	uint64_t *sync_pos_;
};

#endif // LDBENGINE_H
