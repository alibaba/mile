// LdbTableImpl.h : LdbTableImpl
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#ifndef LDBTABLEIMPL_H
#define LDBTABLEIMPL_H

#include "leveldb/db.h"
#include "leveldb/comparator.h"
#include "../../common/mem.h"
#include "AggregateDesc.h"

#include <string>
#include <vector>

class ConfigFile;
class MileIterator;
class LdbTableMgr;
class LdbHandler;
struct binlog_writer;

struct range_t;

typedef std::pair<uint64_t, leveldb::DB *> cumulative_step_t;

class LdbTableImpl
{
public:
	LdbTableImpl(std::string name, std::string storage_dir, binlog_writer *bl_writer);
	virtual ~LdbTableImpl();

	int Init(const ConfigFile &conf, const leveldb::Options &opt);
	int InitCumulativeIndex(const ConfigFile &conf);
	const leveldb::Comparator *GetComparator() { return options_.comparator; }

	const std::string &GetName() { return name_; }

	virtual int InsertRow(struct row_data* rdata, MEM_POOL_PTR mem_pool);

	virtual int DeleteRow(const struct low_data_struct *key, MEM_POOL_PTR mem);

	virtual int UpdateRow(low_data_struct *old_key, low_data_struct *old_value, struct row_data *rdata, MEM_POOL_PTR mem);

	MileIterator *UseIndex(LdbTableMgr *table_mgr, struct condition_array* cond, MEM_POOL_PTR mem);

	virtual struct select_row_t* QueryRow(low_data_struct *ldata, struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool);

	// result_array must be a sorted aggregate_desc_array_t
	int CumulativeQuery(aggregate_desc_array_t *result_array, struct condition_array *cond, MEM_POOL_PTR mem);

	bool HasCumulativeIndex() { return !cumulative_step_.empty() && aggregate_desc_.size > 0; }

	enum property_ldb_selector { STORE_DB, INDEX_DB };

	int GetProperty(std::string *value, property_ldb_selector ldb_type, const std::string &property_name);

	uint64_t Ref() { return __sync_add_and_fetch(&refs_, 1); }
	uint64_t UnRef() { return __sync_sub_and_fetch(&refs_, 1); }


private:
	bool UseHostTime() { return time_key_.empty() && time_key_len_ > 0; }

	int CondToRange(const condition_array *cond, range_t &range, range_t &row_range, MEM_POOL_PTR mem);
	int GetRange(range_t &row_range, range_t &time_range, const condition_array *cond);

	int NormalInsert(const low_data_struct *row_key, const low_data_struct *time_key,
			const low_data_struct *value, MEM_POOL_PTR mem);
	int CumulativeInsert(const low_data_struct *row_key, const low_data_struct *time_key, 
			struct row_data *rdata, MEM_POOL_PTR mem);
	int InnerCumulativeQuery(int level, aggregate_desc_array_t *result_array,
			low_data_struct *lkey, low_data_struct *rkey, const range_t &time_range, MEM_POOL_PTR mem);

	int InnerUpdateRow(low_data_struct *key, row_data *new_row, MEM_POOL *mem);

	void SpliceKey(low_data_struct *key, const low_data_struct *row_key,
			const low_data_struct *time_key, MEM_POOL *mem);

	int FindRowKey(low_data_struct *&row_key, low_data_struct *&time_key, row_data *rdata);

	static uint64_t TimeValue(const char *value, uint32_t len);

private:
	std::string name_;
	std::string row_key_;
	std::string time_key_;

	uint32_t time_key_len_;

	std::string storage_dir_;
	binlog_writer *bl_writer_;

	leveldb::Env *env_;
	leveldb::Env *cumulative_env_;

	leveldb::Options options_;
	leveldb::WriteOptions woptions_;
	leveldb::ReadOptions roptions_;
	leveldb::DB *db_;

	// reference count
	uint64_t refs_;

	MEM_POOL_PTR mem_;

	// for cumulative index
	leveldb::DB *cumulative_db_;
	std::vector<cumulative_step_t> cumulative_step_;
	aggregate_desc_array_t aggregate_desc_;
};

#endif // LDBTABLEIMPL_H
