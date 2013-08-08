// LdbTableImpl.cpp : LdbTableImpl
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#include "LdbTableImpl.h"
#include "LdbConfig.h"
#include "LdbComparator.h"
#include "LdbIterator.h"
#include "LdbFilter.h"
#include "LdbEngine.h"

#include "../../common/ConfigFile.h"
#include "../../common/log.h"
#include "../../common/MPAlloc.h"
#include "../../common/common_util.h"
#include "../../common/AggrFunc.h"
#include "../../protocol/packet.h"

#include "leveldb/write_batch.h"
#include "leveldb/env.h"

#include <list>

#include <boost/shared_ptr.hpp>
#include <boost/scoped_ptr.hpp>
#include <boost/bind.hpp>
#include <boost/algorithm/string.hpp>

/*
 * only HI_TYPE_UNSIGNED_LONG, HI_TYPE_UNSIGNED_LONGLONG, HI_TYPE_STRING supported.
 * return :
 * 	-1 : slice < ldata
 * 	0  : slice == ldata
 * 	1  : slice > ldata
 *
 * 	-2 : error
 */
static inline int compare_slice_ldata(const leveldb::Slice *slice, const low_data_struct *ldata)
{
	ASSERT(NULL != slice && NULL != ldata);
	const struct low_data_struct s = { NULL, ldata->type, (void *)slice->data(), slice->size() };
	return compare_ld(&s, ldata);
}

/*
 * ldata's position relative to range
 * return:
 *  -1 : on left of range
 *  0  : in range
 *  1  : ont right of range
 *
 *  -2 : error
 */
static int range_position(const range_t &range, const low_data_struct &ldata);

// use condition to shrink range
static int shrink_range(range_t &range, const condition_t &cond);

// return new value, but orgin_value also be modified.
static row_data *update_row_data(row_data *orgin_value, const row_data *update_value, MEM_POOL_PTR mem);

LdbTableImpl::LdbTableImpl(std::string name, std::string dir, binlog_writer *bl_writer)
	: name_(name), time_key_len_(0), storage_dir_(dir), bl_writer_(bl_writer)
	, env_(NULL), cumulative_env_(NULL), db_(NULL), refs_(0)
	, mem_(NULL), cumulative_db_(NULL)
{
	mem_ = mem_pool_init(1<<20);
	::memset(&aggregate_desc_, sizeof(aggregate_desc_), 0);
}

LdbTableImpl::~LdbTableImpl()
{
	// wait until no reference
	int sleep_cnt = 0;
	while(refs_) {
		usleep(10 * 1000); // 10ms

		if ((++sleep_cnt) % 100 == 0)
			log_warn("waiting query terminate, reference count %" PRIu64, refs_);
	}

	delete db_;
	delete cumulative_db_;

	// create in Init
	delete options_.filter_policy;
	delete options_.comparator;

	delete cumulative_env_;
	delete env_;

	if (mem_)
		mem_pool_destroy(mem_);
}

int LdbTableImpl::Init(const ConfigFile &conf, const leveldb::Options &opt)
{
	options_ = opt;
	options_.create_if_missing = true;

	// posix env
	env_ = leveldb::NewPosixEnv();
	options_.env = env_;

	// config write options
	woptions_.sync = conf.GetIntValue(CONF_LDB_SESSION, CONF_WRITE_SYNC, 0);

	// config read options
	roptions_.fill_cache = true;
	roptions_.verify_checksums = conf.GetIntValue(CONF_LDB_SESSION, CONF_READ_VERIFY_CHECKSUMS, 0);

	LdbEngine::LoadLevelDBConf(options_, conf, (name_ + ".opt").c_str());

	// get row key and time key
	const char *value = conf.GetValue(CONF_LDB_SESSION,
			(name_ + CONF_ROW_KEY_SUFFIX).c_str(), NULL);
	if (NULL == value) {
		log_error("no row key configured, table %s", name_.c_str());
		return -1;
	}
	row_key_ = value;

	value = conf.GetValue(CONF_LDB_SESSION,
			(name_ + CONF_TIME_KEY_SUFFIX).c_str(), NULL);
	if (NULL != value)
		time_key_ = value;

	// init comparator
	time_key_len_ = conf.GetIntValue(CONF_LDB_SESSION,
			(name_ + CONF_TIME_KEY_LEN_SUFFIX).c_str(), 4);
	ASSERT(time_key_len_ % 4 == 0);
	if (time_key_len_ > 0 && time_key_.empty())
		time_key_len_ = 4;

	int scale = conf.GetIntValue(CONF_LDB_SESSION,
			(name_ + CONF_TIME_KEY_SCALE_SUFFIX).c_str(), 0);

	uint64_t expire_time = conf.GetInt64Value(CONF_LDB_SESSION,
			(name_ + CONF_EXPIRE_TIME_SUFFIX).c_str(), 1LL<<48);

	LdbComparator *cmp = new LdbComparator(time_key_len_, scale, UseHostTime(), expire_time);
	options_.comparator = cmp;

	if (options_.use_bloom_filter)
		options_.filter_policy = new LdbFilter(cmp);
	else
		options_.filter_policy = NULL;

	// open db
	leveldb::Status s = leveldb::DB::Open(options_, storage_dir_, &db_);
	if (!s.ok()) {
		log_error("open leveldb instance failed, dir %s, table %s, error info %s",
				storage_dir_.c_str(), name_.c_str(), s.ToString().c_str());
		return -1;
	}
	log_info("open leveldb instace success dir %s, table %s",
				storage_dir_.c_str(), name_.c_str());

	// init cumulative index
	if (InitCumulativeIndex(conf) != 0)
		return -1;

	if (UseHostTime() && HasCumulativeIndex()) {
		log_error("time_key must set, if use cumulative index");
		return -1;
	}

	return 0;
}

int LdbTableImpl::InitCumulativeIndex(const ConfigFile &conf)
{
	std::string cumulative_step = conf.GetValue(CONF_LDB_SESSION,
			(name_ + CONF_CUMULATIVE_STEP_SUFFIX).c_str(), "");
	std::string aggregate_desc = conf.GetValue(CONF_LDB_SESSION,
			(name_ + CONF_AGGREGATE_DESC_SUFFIX).c_str(), "");

	// no cumulative index
	if (cumulative_step.empty() || aggregate_desc.empty())
		return 0;

	// parse aggregate_desc
	std::vector<std::string> aggregate_strs;
	boost::split(aggregate_strs, aggregate_desc, boost::is_any_of(",;"), boost::token_compress_on);
	aggregate_desc_.size = aggregate_strs.size();
	aggregate_desc_.data = (aggregate_desc_t *)mem_pool_malloc(mem_, aggregate_strs.size() * sizeof(aggregate_desc_t));

	for (uint32_t i = 0; i < aggregate_desc_.size; i++) {
		aggregate_desc_t &desc = aggregate_desc_.data[i];
		::memset(&desc, 0, sizeof(desc));
		std::vector<std::string> parsed_desc;
		boost::split(parsed_desc, aggregate_strs.at(i), boost::is_any_of("()"), boost::token_compress_on);

		if (parsed_desc.size() < 3) {
			log_error("invalid aggregate_desc [%s]", aggregate_strs.at(i).c_str());
			return -1;
		}

		boost::trim(parsed_desc[0]);
		boost::trim(parsed_desc[1]);

		int type = func_name2type(parsed_desc.at(0).c_str());
		if (type == -1) {
			log_error("function [%s] not found", parsed_desc.at(0).c_str());
			return -1;
		}
		desc.func_type = (enum function_type)type;
		char *name = (char *)mem_pool_malloc(mem_, parsed_desc.at(1).size() + 1);
		::strcpy(name, parsed_desc.at(1).c_str());
		desc.name = name;
	}

	std::sort(aggregate_desc_.data, aggregate_desc_.data + aggregate_desc_.size);

	// start cumulative_db_
	leveldb::Options opt = options_;
	LdbEngine::LoadLevelDBConf(opt, conf, (name_ + ".cumulative_index.opt").c_str());
	std::string dir = storage_dir_ + ".cumulative_index";

	cumulative_env_ = leveldb::NewPosixEnv();
	opt.env = cumulative_env_;

	leveldb::Status s = leveldb::DB::Open(opt, dir.c_str(), &cumulative_db_);
	if (!s.ok()) {
		log_error("open cumulative index leveldb instance failed, dir %s, table %s, error info %s",
				dir.c_str(), name_.c_str(), s.ToString().c_str());
		return -1;
	}
	log_info("open leveldb instace success,  dir %s", dir.c_str());

	// setup cumulative_step_
	std::vector<std::string> step_strs;
	boost::split(step_strs, cumulative_step, boost::is_any_of(",;"), boost::token_compress_on);

	cumulative_step_.resize(step_strs.size());
	unsigned long long old_step = 0, step = 0;
	for (size_t i = 0; i < step_strs.size(); i++, old_step = step) {
		step = ::atoll(step_strs.at(i).c_str());
		cumulative_step_[i] = std::make_pair(step, i > 0 ? cumulative_db_ : db_);
		if (step == 0 || (i > 0 && (step <= old_step || step % old_step != 0))) {
			log_error("cumulative_step must increasing, and big step [%llu]"
					" should be divisible by small step [%llu]", step, old_step);
			return -1;
		}
	}

	return 0;
}

int LdbTableImpl::InsertRow(struct row_data *rdata, MEM_POOL_PTR mem_pool)
{
	struct low_data_struct *row_key = NULL;
	struct low_data_struct *time_key = NULL;

	// find row_key field and time_key field
	int rc = FindRowKey(row_key, time_key, rdata);
	if (rc)
		return rc;

	// splice value
	struct low_data_struct *ldata = rowdata_to_lowdata(rdata, mem_pool);

	// write binlog
	binlog_lock_writer(bl_writer_);
	boost::shared_ptr<void> unlock_guard(static_cast<void *>(NULL), boost::bind(binlog_unlock_writer, bl_writer_));

	if (bl_writer_ && LdbEngine::RecordBinlog( bl_writer_, name_.c_str(),
				OPERATION_INSERT, (const char *)ldata->data, ldata->len, mem_pool) != 0)
		return -1;

	// insert
	if (!HasCumulativeIndex()) {
		rc = NormalInsert(row_key, time_key, ldata, mem_pool);
	} else {
		rc = CumulativeInsert(row_key, time_key, rdata, mem_pool);
	}

	if (bl_writer_) {
		if (rc == 0)
			binlog_confirm_ok(bl_writer_, mem_pool);
		else
			binlog_confirm_fail(bl_writer_, mem_pool);
	}
	return rc;
}

int LdbTableImpl::NormalInsert(const low_data_struct *row_key, const low_data_struct *time_key,
		const low_data_struct *value, MEM_POOL_PTR mem)
{
	low_data_struct key;
	SpliceKey(&key, row_key, time_key, mem);

	leveldb::Status s = db_->Put(woptions_, leveldb::Slice((const char *)key.data, key.len),
			leveldb::Slice((const char *)value->data, value->len));
	if (!s.ok()) {
		log_error("insert into leveldb failed, error %s", s.ToString().c_str());
		return -1;
	}

	return 0;
}

int LdbTableImpl::CumulativeInsert(const low_data_struct *row_key, const low_data_struct *time_key,
		struct row_data *rdata, MEM_POOL_PTR mem)
{
	aggregate_desc_array_t new_value;
	new_value.size = aggregate_desc_.size;
	aggregate_desc_t _data_[new_value.size];
	new_value.data = _data_;
	::memcpy(new_value.data, aggregate_desc_.data, sizeof(aggregate_desc_t) * new_value.size);

	// compute value of insert row
	for (uint32_t i = 0; i < new_value.size; i++) {
		// for count(*)
		if (strcmp(new_value.data[i].name, "*") == 0) {
			if (ComputeFunc(mem, new_value.data[i].func_type, &new_value.data[i].value, NULL) != 0) {
				log_error("compute count(*) failed");
				return -1;
			}
			continue;
		}
		for (uint16_t k = 0; k < rdata->field_count; k++) {
			if (strcmp(new_value.data[i].name, rdata->datas[k].field_name) == 0) {
				if (ComputeFunc(mem, new_value.data[i].func_type, &new_value.data[i].value, &rdata->datas[k]) != 0) {
					log_error("compute func failed, filed %s", rdata->datas[k].field_name);
					return -1;
				}
				break;
			}
		}
	}

	low_data_struct key;
	key.len = row_key->len + time_key->len + 1;
	key.data = mem_pool_malloc(mem, key.len);
	::memcpy(key.data, row_key->data, row_key->len);

	// FIXME: Ref ?
	uint8_t level = std::numeric_limits<uint8_t>::max();
	uint64_t time_value = TimeValue((const char *)time_key->data, time_key->len);
	for (std::vector<cumulative_step_t>::iterator it = cumulative_step_.begin();
			it != cumulative_step_.end(); ++it, --level) {

		const uint64_t rem = time_value % it->first;
		uint64_t t = rem ? time_value - rem + it->first : time_value;
		::memcpy((char *)key.data + row_key->len, &level, 1);
		::memcpy((char *)key.data + row_key->len + 1, &t, time_key->len); // NOTE: portable problem

		// query
		std::string value_str;
		leveldb::Status s = it->second->Get(roptions_, leveldb::Slice((const char *)key.data, key.len), &value_str);
		if (!s.ok() && !s.IsNotFound()) {
			log_error("leveldb::DB::Get failed, error string %s", s.ToString().c_str());
			return -1;
		}

		aggregate_desc_array_t *value = &new_value;
		aggregate_desc_array_t queried_value;
		if (!s.IsNotFound()) {
			low_data_struct ldata;
			ldata.data = (char *)value_str.data();
			ldata.len = value_str.size();
			if (queried_value.FromLowData(ldata, mem) != 0) {
				log_error("parsed quaried value failed %s", value_str.c_str());
				return -1;
			}

			if (queried_value.Merge(new_value, mem) != 0) {
				log_error("merge aggregate func result failed!");
				return -1;
			}

			value = &queried_value;
		} 

		// FIXME: only ToLowData once if value point to new_value
		low_data_struct low_value = value->ToLowData(mem);
		s = it->second->Put(woptions_, leveldb::Slice((const char *)key.data, key.len),
				leveldb::Slice((const char *)low_value.data, low_value.len));
		if (!s.ok()) {
			log_error("insert into leveldb failed, error %s", s.ToString().c_str());
			return -1;
		}
	}

	return 0;
}

uint64_t LdbTableImpl::TimeValue(const char *value, uint32_t len)
{
	switch (len) {
	case 4:
		return *(uint32_t *)value;
		break;
	case 8:
		return *(uint64_t *)value;
		break;
	default:
		abort();
	}
}

int LdbTableImpl::GetRange(range_t &row_range, range_t &time_range, const condition_array *cond)
{
	for (uint32_t i = 0; i < cond->n; i++) {
		const condition_t &c = cond->conditions[i];
		switch (c.type) {
		case LOGIC_AND:
			continue;
		case CONDITION_EXP:
			if (row_key_ == c.field_name) {
				if (shrink_range(row_range, c) != 0) {
					log_error("shrink row range failed.");
					return -1;
				}
			} else if (time_key_ == c.field_name) {
				if (shrink_range(time_range, c) != 0) {
					log_error("shrink time range failed.");
					return -1;
				}
			} else {
				log_error("unsupported field [%s] in index condition", c.field_name);
				return -1;
			}
			break;
		default:
			log_error("unsupported condition: %d", c.type);
			return -1;
		}
	}

	return 0;
}

int LdbTableImpl::CondToRange(const condition_array *cond, range_t &range, range_t &rrange, MEM_POOL_PTR mem)
{
	// get row key range and time range
	range_t row_range;
	range_t time_range;

	if (GetRange(row_range, time_range, cond) != 0)
		return -1;

	if (!row_range.IsValid() || !time_range.IsValid()) {
		range.border |= range_t::INVALID_FLAG;
		return 0;
	}

	// check range
	if (!row_range.IsWhole()) {
		log_error("row key must be within a certain range");
		return -1;
	}

	if (time_key_len_ == 0) {
		range = row_range;
		return 0;
	}

	if ((time_range.HasStart() && time_range.start.size() != time_key_len_) ||
			(time_range.HasEnd() && time_range.end.size() != time_key_len_)) {
		log_error("time field length mismatch");
		return -1;
	}
	rrange = row_range;

	// merge row_range and time_range
	// set range start
	int len  = row_range.start.size() + time_key_len_;
	char *data = (char *)mem_pool_malloc(mem, len);
	::memcpy(data, row_range.start.data(), row_range.start.size());
	if (time_range.HasStart()) {
		::memcpy(data + row_range.start.size(), time_range.start.data(), time_key_len_);
		range.border |= (row_range.InclusiveStart() && time_range.InclusiveStart()) ?  range_t::INCLUSIVE_START : 0;
	}
	else {
		// set time to minimum
		::memset(data + row_range.start.size(), 0, time_key_len_);
		range.border |= row_range.InclusiveStart() ?  range_t::INCLUSIVE_START : 0;
	}
	if (!row_range.InclusiveStart()) // set time to maximum
		::memset(data + row_range.start.size(), 0xff, time_key_len_);

	range.start = leveldb::Slice(data, len);
	range.border |= range_t::HAS_START;

	// set range end
	len  = row_range.end.size() + time_key_len_;
	data = (char *)mem_pool_malloc(mem, len);
	::memcpy(data, row_range.end.data(), row_range.end.size());
	if (time_range.HasEnd()) {
		::memcpy(data + row_range.end.size(), time_range.end.data(), time_key_len_);
		range.border |= (row_range.InclusiveEnd() && time_range.InclusiveEnd()) ?  range_t::INCLUSIVE_END : 0;
	}
	else {
		// set time to maximum
		::memset(data + row_range.end.size(), 0xff, time_key_len_);
		range.border |= row_range.InclusiveEnd() ?  range_t::INCLUSIVE_END: 0;
	}
	if (!row_range.InclusiveEnd()) // set time to minimum
		::memset(data + row_range.start.size(), 0, time_key_len_);

	range.end = leveldb::Slice(data, len);
	range.border |= range_t::HAS_END;

	return 0;
}

int shrink_range(range_t &range, const condition_t &cond)
{
	ASSERT(cond.type == CONDITION_EXP);

	// do nothing for invalid range
	if (!range.IsValid())
		return 0;

	const enum compare_type c = cond.comparator;
	// FIXME: use original_values ?
	const low_data_struct *lvalue = &cond.values[0];
	const low_data_struct *rvalue = lvalue;
	if (is_between_compare(c)) 
		rvalue = &cond.values[1];

	int rc = 0;
	// set range.start
	if (c == CT_EQ || c == CT_GT || c == CT_GE ||
			is_between_compare(c)) {
		rc = range_position(range, *lvalue);
		switch (rc) {
		case -2:
			return -1;
		case 1: // on rigth, set invalid
			range.border |= range_t::INVALID_FLAG;
			return 0;
		case -1:
			break;
		case 0:
			range.start = leveldb::Slice((char *)lvalue->data, lvalue->len);
			range.border |= range_t::HAS_START;
			if (c == CT_EQ || c == CT_GE || c == EXP_COMPARE_BETWEEN_LEGE ||
					c == EXP_COMPARE_BETWEEN_LGE)
				range.border |= range_t::INCLUSIVE_START;
			break;
		default: // never come here
			abort();
		}
	}

	// set range.end
	if (c == CT_EQ || c == CT_LT || c == CT_LE ||
			is_between_compare(c)) {
		rc = range_position(range, *rvalue);
		switch (rc) {
		case -2:
			return -1;
		case -1: // on left, set invalid
			range.border |= range_t::INVALID_FLAG;
			return 0;
		case 1:
			break;
		case 0:
			range.end = leveldb::Slice((char *)rvalue->data, rvalue->len);
			range.border |= range_t::HAS_END;
			if (c == CT_EQ || c == CT_LE || c == EXP_COMPARE_BETWEEN_LEGE ||
					c == EXP_COMPARE_BETWEEN_LEG)
				range.border |= range_t::INCLUSIVE_END;
			break;
		default: // never come here
			abort();
		}
	}

	if (!is_between_compare(c) && !(c == CT_EQ || c == CT_GT ||
				c == CT_GE || c == CT_LT || c == CT_LE)) {
		log_error("unsupported condition: %d", c);
		return -1;
	}

	return 0;
}

int range_position(const range_t &range, const low_data_struct &ldata)
{
	ASSERT(range.IsValid());
	int rc = 0;

	// check left
	if (range.HasStart()) {
		rc = compare_slice_ldata(&range.start, &ldata);
		if (rc == -2)
			return -2;
		else if (rc == 1)
			return -1;
		else if (rc == 0)
			return range.InclusiveStart() ? 0 : -1;
	}

	// check right
	if (range.HasEnd()) {
		rc = compare_slice_ldata(&range.end, &ldata);
		if (rc == -2)
			return -2;
		else if (rc == -1)
			return 1;
		else if (rc == 0)
			return range.InclusiveEnd() ? 0 : 1;
	}

	return 0;
}

MileIterator *LdbTableImpl::UseIndex(LdbTableMgr *table_mgr, struct condition_array* cond, MEM_POOL_PTR mem)
{
	range_t range;
	range_t row_range;
	if (CondToRange(cond, range, row_range, mem) != 0) {
		log_error("convert condition to range failed");
		return NULL;
	}

	leveldb::Iterator *iter = NULL;
	if (options_.filter_policy && row_range.InclusiveStart() &&
			row_range.InclusiveEnd() && row_range.start == row_range.end) {
		leveldb::ReadOptions rops = roptions_;
		rops.filter_hint = true;
		std::string value;
		leveldb::Status s = db_->Get(rops, range.start, &value);
		if (s.IsNotFound()) {
			log_debug("bloom filter no match, row key [%s]", row_range.start.ToString().c_str());
			range.border |= range_t::INVALID_FLAG;
		}
	}

	if (range.IsValid()) {
		iter = db_->NewIterator(roptions_);
	}

	return  NEW(mem, LdbIterator)((TableManager *)table_mgr, range, GetComparator(), iter, mem);
}

/*
 * TODO: speed up field lookup.
 * 1. rowdata_to_lowdata: make stored fields are sorted by field name.
 * 2. lowdata_to_rowdata: keep the stored field order.
 * 3. QureRow: use bsearch to speed up field search.
 */
struct select_row_t *LdbTableImpl::QueryRow(low_data_struct *ldata, struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool)
{
	row_data *row = lowdata_to_rowdata(ldata, mem_pool);
	select_row_t *result = (select_row_t *)mem_pool_malloc(mem_pool, sizeof(select_row_t));
	::memset(result, 0, sizeof(*result));

	result->n =  sel_fields->n;
	result->select_type = (select_types_t *)mem_pool_malloc(mem_pool, sizeof(select_types_t) * sel_fields->n);
	result->data = (low_data_struct *)mem_pool_malloc(mem_pool, sizeof(low_data_struct) * sel_fields->n);
	::memset(result->data, 0, sizeof(low_data_struct) * sel_fields->n);

	for (uint32_t i = 0; i < sel_fields->n; i++) {
		result->select_type[i] = SELECT_TYPE_ORIGINAL; // always return original value

		low_data_struct *field = NULL;
		for (uint32_t k = 0; k < row->field_count; k++) {
			if (strcmp(sel_fields->fields_name[i], row->datas[k].field_name) == 0) {
				field = &row->datas[k];
				break;
			}
		}

		if (field != NULL)
			result->data[i] = *field;
		else
			// link returned field name to select field name
			result->data[i].field_name = sel_fields->fields_name[i];
	}

	return result;
}

int LdbTableImpl::DeleteRow(const struct low_data_struct *key, MEM_POOL_PTR mem)
{
	binlog_lock_writer(bl_writer_);
	boost::shared_ptr<void> unlock_guard(static_cast<void *>(NULL), boost::bind(binlog_unlock_writer, bl_writer_));
	if (bl_writer_ && LdbEngine::RecordBinlog( bl_writer_, name_.c_str(),
				OPERATION_LDB_DELETE, (const char *)key->data, key->len, mem) != 0)
		return -1;

	leveldb::Status s = db_->Delete(woptions_, leveldb::Slice((char *)key->data, key->len));
	if (!s.ok()) {
		log_error("delete failed, error %s", s.ToString().c_str());
		if (bl_writer_)
			binlog_confirm_fail(bl_writer_, mem);
		return -1;
	}
	if (bl_writer_)
		binlog_confirm_ok(bl_writer_, mem);

	return 0;
}

int LdbTableImpl::GetProperty(std::string *value, property_ldb_selector ldb_type, const std::string &property_name)
{
	if (property_name == "ref-count") {
		char buf[64];
		snprintf(buf, sizeof(buf), "\t%"PRIu64"\n", refs_);
		*value = buf;
		return 0;
	}
	std::string name = "leveldb." + property_name;
	leveldb::Slice p(name.c_str(), name.size());
	if ((ldb_type == STORE_DB ? db_ : cumulative_db_)->GetProperty(p, value))
		return 0;
	log_error("get property %s failed", name.c_str());
	return -1;
}

int LdbTableImpl::CumulativeQuery(aggregate_desc_array_t *result_array, struct condition_array *cond, MEM_POOL_PTR mem)
{
	range_t row_range, time_range;
	if (GetRange(row_range, time_range, cond) < 0)
		return -1;
	if (!row_range.IsWhole() || row_range.start != row_range.end ||
			!row_range.InclusiveStart() || !row_range.InclusiveEnd()) {
		log_error("row key [%s] must specified", row_key_.c_str());
		return -1;
	}

	if (!row_range.IsValid() || !time_range.IsValid()) {
		log_info("invalid range");
		return 0;
	}

	if ((time_range.HasStart() && time_range.start.size() != time_key_len_) ||
			(time_range.HasEnd() && time_range.end.size() != time_key_len_)) {
		log_error("time field length mismatch");
		return -1;
	}

	// make sure time_range HasStart() and HasEnd().
	if (!time_range.HasStart()) {
		// set to minimum
		char *data = (char *)mem_pool_malloc(mem, time_key_len_);
		::memset(data, 0, time_key_len_);
		time_range.start = leveldb::Slice(data, time_key_len_);
		time_range.border |= range_t::INCLUSIVE_START | range_t::HAS_START;
	}

	if (!time_range.HasEnd()) {
		// set to maximum
		char *data = (char *)mem_pool_malloc(mem, time_key_len_);
		::memset(data, 0xff, time_key_len_);
		time_range.end = leveldb::Slice(data, time_key_len_);
		time_range.border |= range_t::INCLUSIVE_END | range_t::HAS_END;
	}

	low_data_struct lkey = low_data_struct();
	low_data_struct rkey = low_data_struct();
	rkey.len = lkey.len = row_range.start.size() + 1 + time_key_len_;
	lkey.data = (char *)mem_pool_malloc(mem, lkey.len);
	rkey.data = (char *)mem_pool_malloc(mem, rkey.len);

	::memcpy(lkey.data, row_range.start.data(), row_range.start.size());
	::memcpy(rkey.data, row_range.start.data(), row_range.start.size());


	return InnerCumulativeQuery(cumulative_step_.size() - 1, result_array, &lkey, &rkey, time_range, mem);
}

int LdbTableImpl::InnerCumulativeQuery(int level, aggregate_desc_array_t *result_array,
		low_data_struct *lkey, low_data_struct *rkey, const range_t &time_range, MEM_POOL_PTR mem)
{
	*((uint8_t *)((char *)lkey->data + lkey->len - time_key_len_ - 1)) = 0; // just for debug to print key

	// time value
	uint64_t lt = TimeValue(time_range.start.data(), time_range.start.size());
	uint64_t rt = TimeValue(time_range.end.data(), time_range.end.size());

	log_debug("query key [%s], level [%d], time %c%llu, %llu%c", (const char *)lkey->data, level,
			time_range.InclusiveStart() ? '[' : '(', (unsigned long long)lt,
			(unsigned long long)rt, time_range.InclusiveEnd() ? ']' : ')');

	// set left bound, right bound
	*((uint8_t *)((char *)lkey->data + lkey->len - time_key_len_ - 1)) = std::numeric_limits<uint8_t>::max() - level;
	*((uint8_t *)((char *)rkey->data + rkey->len - time_key_len_ - 1)) = std::numeric_limits<uint8_t>::max() - level;
	::memcpy((char *)lkey->data + lkey->len - time_key_len_, time_range.start.data(), time_key_len_);
	::memcpy((char *)rkey->data + rkey->len - time_key_len_, time_range.end.data(), time_key_len_);

	// for convenience
	const leveldb::Slice lb((char *)lkey->data, lkey->len);
	const leveldb::Slice rb((char *)rkey->data, rkey->len);

	// time ranges for next level query
	// left time range: (/[ time_range.start, lr_end ]
	// right time range: [ rr.start, time_range.end )/]
	range_t lr, rr;
	lr.start = time_range.start;
	rr.end = time_range.end;
	uint64_t lr_end = 0, rr_start = 0; // value buffer
	lr.end = leveldb::Slice((char *)&lr_end, time_key_len_);
	rr.start = leveldb::Slice((char *)&rr_start, time_key_len_);
	lr.border = range_t::HAS_START | range_t::HAS_END | (time_range.border & range_t::INCLUSIVE_START) | range_t::INCLUSIVE_END;
	rr.border = range_t::HAS_START | range_t::HAS_END | (time_range.border & range_t::INCLUSIVE_END);

	bool has_valid_value = false;
	const leveldb::Comparator *cmp = GetComparator();
	uint64_t step = cumulative_step_.at(level).first;

	boost::scoped_ptr<leveldb::Iterator> iter(cumulative_step_.at(level).second->NewIterator(roptions_));
	for (iter->Seek(leveldb::Slice(lb)); iter->Valid(); iter->Next()) {

		leveldb::Slice k = iter->key();
		// check right bound
		int rc = cmp->Compare(k, rb);
		if (rc > 0)
			break;
		else if (rc == 0) {
			if (time_range.InclusiveEnd())
				rr.border |= range_t::INVALID_FLAG;
			else
				break;
		}

		uint64_t tv = TimeValue(k.data() + k.size() - time_key_len_, time_key_len_);
		// log_debug("level [%d] time [%llu]", level, (unsigned long long)tv);

		// check left bound
		if (!has_valid_value) {
			rc = cmp->Compare(k, lb);
			ASSERT(rc >= 0);
			if (rc == 0) {
				if (time_range.InclusiveStart() && (step == 1 || lt == 0)) {
					lr.border |= range_t::INVALID_FLAG;
					has_valid_value = true;
				}
			} else {
				if (lt + step == tv) {
					if (!time_range.InclusiveStart()) {
						lr.border |= range_t::INVALID_FLAG;
						has_valid_value = true;
					}
				} else if (lt + step < tv) {
					has_valid_value = true;
					lr_end = tv - step;
				}
			}
		}

		// merge result
		if (has_valid_value) {
			// log_debug("level [%d] valid time [%llu]", level, (unsigned long long)tv);

			rr_start = tv; // set right time range's start every time.

			low_data_struct ldata = low_data_struct();
			ldata.data = (char *)iter->value().data();
			ldata.len = iter->value().size();
			aggregate_desc_array_t v;
			if (v.FromLowData(ldata, mem) != 0) {
				log_error("parsed quaried value failed %s", iter->value().ToString().c_str());
				return -1;
			}

			if (result_array->Merge(v, mem) != 0) {
				log_error("merge aggregate func result failed!");
				return -1;
			}
		}
	}

	if (level == 0)
		return 0;

	int rc = 0;
	if (!has_valid_value) {
		rc = InnerCumulativeQuery(level - 1, result_array, lkey, rkey, time_range, mem);
	} else {
		if ((lr.IsValid() && lt > lr_end) || (rr.IsValid() && rr_start > rt)) {
			log_error("invalid range");
			rc = -1;
		}

		if (rc == 0 && lr.IsValid())
			rc = InnerCumulativeQuery(level - 1, result_array, lkey, rkey, lr, mem);
		if (rc == 0 && rr.IsValid())
			rc = InnerCumulativeQuery(level - 1, result_array, lkey, rkey, rr, mem);
	}

	return rc;
}

static row_data *update_row_data(row_data *orgin_row, const row_data *update_row, MEM_POOL_PTR mem)
{
	MPAlloc<low_data_struct *> alloc(mem);
	typedef std::list<low_data_struct *, MPAlloc<low_data_struct *> > low_data_list_t;
	low_data_list_t remain_list(alloc);

	// replace value
	for (uint16_t i = 0; i < update_row->field_count; i++) {
		bool found = false;
		for (uint16_t k = 0; k < orgin_row->field_count; k++) {
			if (strcmp(orgin_row->datas[k].field_name, update_row->datas[i].field_name) == 0) {
				orgin_row->datas[k] = update_row->datas[i];
				found = true;
				break;
			}
		}
		if (!found)
			remain_list.push_back(&update_row->datas[i]);
	}

	if (remain_list.empty())
		return orgin_row;

	// append not found fields
	row_data *new_row = NEW(mem, row_data);
	new_row->field_count = orgin_row->field_count + remain_list.size();

	new_row->datas = (low_data_struct *)mem_pool_malloc(mem, sizeof(low_data_struct) * new_row->field_count);
	::memcpy(new_row->datas, orgin_row->datas, sizeof(low_data_struct) * orgin_row->field_count);

	low_data_list_t::const_iterator it = remain_list.begin();
	for (uint16_t i = orgin_row->field_count; it != remain_list.end(); ++it, ++i) {
		new_row->datas[i] = *(*it);
	}

	return new_row;
}

int LdbTableImpl::UpdateRow(low_data_struct *old_key, low_data_struct *old_value, struct row_data *rdata, MEM_POOL_PTR mem)
{
	row_data *orgin_row = lowdata_to_rowdata(old_value, mem);
	row_data *new_row = update_row_data(orgin_row, rdata, mem);

	binlog_lock_writer(bl_writer_);
	boost::shared_ptr<void> unlock_guard(static_cast<void *>(NULL), boost::bind(binlog_unlock_writer, bl_writer_));

	int rc = 0;
	if (bl_writer_) {
		low_data_struct *update_value = rowdata_to_lowdata(rdata, mem);
		rc = LdbEngine::RecordBinlog(bl_writer_, name_.c_str(), OPERATION_LDB_UPDATE, mem,
				old_key->data, old_key->len,
				old_value->data, old_value->len,
				update_value->data, update_value->len, NULL);
		if (rc)
			return rc;
	}

	rc = InnerUpdateRow(old_key, new_row, mem);

	if (bl_writer_) {
		if (!rc)
			binlog_confirm_ok(bl_writer_, mem);
		else
			binlog_confirm_fail(bl_writer_, mem);
	}

	return rc;
}

int LdbTableImpl::InnerUpdateRow(low_data_struct *key, row_data *new_row, MEM_POOL *mem)
{
	low_data_struct *row_key = NULL, *time_key = NULL;
	int rc = FindRowKey(row_key, time_key, new_row);
	if (rc)
		return rc;

	low_data_struct new_key;
	SpliceKey(&new_key, row_key, time_key, mem);

	low_data_struct *row = rowdata_to_lowdata(new_row, mem);

	leveldb::WriteBatch batch;

	// delete previous key.
	if (key->len != new_key.len || ::memcmp(key->data, new_key.data, key->len) != 0) {
		batch.Delete(leveldb::Slice((const char *)key->data, key->len));
	}
	batch.Put(leveldb::Slice((const char *)new_key.data, new_key.len),
		leveldb::Slice((const char *)row->data, row->len));

	leveldb::Status s = db_->Write(woptions_, &batch);
	if (!s.ok()) {
		log_error("batch write failed, error %s", s.ToString().c_str());
		return -1;
	}

	return 0;
}

int LdbTableImpl::FindRowKey(low_data_struct *&row_key, low_data_struct *&time_key, row_data *rdata)
{
	for (int i = 0; i < (int)rdata->field_count; i++) {
		if (row_key_ == rdata->datas[i].field_name) {
			row_key = &rdata->datas[i];
			if (NULL != time_key || time_key_.empty())
				break;
		}

		if (!time_key_.empty() && time_key_ == rdata->datas[i].field_name) {
			time_key = &rdata->datas[i];
			if (NULL != row_key)
				break;
		}
	}

	if (NULL == row_key || (!time_key_.empty() && NULL == time_key)) {
		log_error("no row key field %s, or time_key field %s", row_key_.c_str(), time_key_.c_str());
		return -1;
	}
	if (row_key->type != HI_TYPE_STRING && row_key->type != HI_TYPE_NULL) {
		log_error("unsupported row key type %d", row_key->type);
		return -1;
	}

	if (NULL != time_key && time_key->len != time_key_len_) {
		log_error("time key length mismatch, real length %d, configured %d",
				time_key->len, time_key_len_);
		return -1;
	}

	return 0;
}

void LdbTableImpl::SpliceKey(low_data_struct *key, const low_data_struct *row_key,
		const low_data_struct *time_key, MEM_POOL *mem)
{
	key->len = row_key->len;
	if (NULL != time_key)
		key->len += time_key->len;
	else if (UseHostTime()) {
		key->len += 4;
	}

	key->data = (char *)mem_pool_malloc(mem, key->len);
	::memcpy(key->data, row_key->data, row_key->len);
	int pos = row_key->len;

	if (NULL != time_key) {
		::memcpy((char *)key->data + pos, time_key->data, time_key->len);
	} else if (UseHostTime()) {
		uint32_t now = (uint32_t)::time(NULL);
		::memcpy((char *)key->data + pos, &now, 4);
	}
}

