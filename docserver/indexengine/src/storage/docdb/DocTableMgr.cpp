// DocTableMgr.cpp : DocTableMgr
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#include "DocTableMgr.h"
#include "DocIterator.h"
#include "../../common/log.h"
#include "suffix.h"

#include "db.h"

#include <new>
#include <cstring>

DocTableMgr::DocTableMgr(StorageEngine *engine, const char *table_name,
		MEM_POOL_PTR mem) :
		TableManager(engine) {
	int len = strlen(table_name);

	assert(len > 0);
	table_name_ = (char *) mem_pool_malloc(mem, len + 1);
	::strcpy(table_name_, table_name);
}

DocTableMgr::~DocTableMgr() {
}

void DocTableMgr::Ref() {
	db_readlock_table(table_name_);
}

void DocTableMgr::UnRef() {
	db_unreadlock_table(table_name_);
}

int DocTableMgr::InsertRow(MileHandler* &handler, struct row_data *rdata,
		MEM_POOL_PTR mem_pool) {
	DocHandler* doc_handler = NEW(mem_pool, DocHandler)();
	handler = doc_handler;

	uint16_t sid = 0;
	uint32_t docid = 0;

	int rc = db_lock_table(table_name_);
	if (rc != 0) {
		log_error("lock table failed, rc %d", rc);
		return -1;
	}

	rc = db_insert(table_name_, &sid, &docid, rdata, DOCID_BY_SELF, mem_pool);
	db_unlock_table(table_name_);

	doc_handler->docid = ((uint64_t) sid << 32) + docid;

	if (0 != rc)
		log_error("docdb insert failed, rc %d", rc);
	return rc;
}

int DocTableMgr::DeleteRow(MileHandler* handler, MEM_POOL_PTR mem_pool) {
	DocHandler* doc_handler = (DocHandler*) handler;
	uint64_t full_docid = doc_handler->docid;
	uint16_t sid = full_docid >> 32;
	uint32_t docid = (uint32_t) full_docid;

	// FIXME : db_lock_table ?
	int rc = db_del_docid(table_name_, sid, docid, mem_pool);
	if (0 != rc)
		log_error(
				"db_del_docid failed, rc %d table %s, sid %"PRIu16", docid %u"PRIu32, rc, table_name_, sid, docid);

	return rc;
}

int DocTableMgr::UpdateRow(MileHandler* handler, struct row_data *new_data,
		MEM_POOL_PTR mem_pool) {
	DocHandler* doc_handler = (DocHandler*) handler;
	uint64_t full_docid = doc_handler->docid;
	uint16_t sid = full_docid >> 32;
	uint32_t docid = (uint32_t) full_docid;

	// only support one field
	assert(new_data->field_count >= 1);

	struct low_data_struct *new_value = &new_data->datas[0];
	struct low_data_struct *old_value = NULL;

	int rc = db_update(table_name_, sid, docid, new_value, &old_value,
			mem_pool);
	if (0 != rc) {
		log_error(
				"db_update failed, rc %d table %s, sid %"PRIu16", docid %u"PRIu32, rc, table_name_, sid, docid);
	}

	return rc;
}

MileIterator *DocTableMgr::UseIndex(struct condition_array *cond,
		struct hint_array *hint, MEM_POOL_PTR mem_pool) {
	DocIterator *iter = NEW(mem_pool, DocIterator)((TableManager *) this,
			table_name_, mem_pool);

	struct list_head *seg_list = db_seghint_query(table_name_, hint, mem_pool);
	if (NULL == seg_list) {
		log_error("error in db_seghint_query");
		DELETE(iter);
		return NULL;
	}

	if (NULL != cond && cond->n > 0) {
		seg_list = query_by_hash_conditions(table_name_, cond, seg_list,
				mem_pool);
		if (NULL == seg_list) {
			log_error("error in query_by_hash_conditions");
			DELETE(iter);
			return NULL;
		}
	}

	iter->SetSegmentList(seg_list, !(NULL != cond && cond->n > 0));

	return iter;
}

struct select_row_t *DocTableMgr::QueryRow(MileHandler* handler,
		struct select_fields_t *sel_fields, MEM_POOL_PTR mem_pool) {
	DocHandler* doc_handler = (DocHandler*) handler;
	uint64_t full_docid = doc_handler->docid;
	uint16_t sid = full_docid >> 32;
	uint32_t docid = (uint32_t) full_docid;
	uint32_t i, n;

	select_row_t *row = (select_row_t *) mem_pool_malloc(mem_pool,
			sizeof(select_row_t));
	row->n = sel_fields->n;
	row->select_type = (select_types_t *) mem_pool_malloc(mem_pool,
			sizeof(select_types_t) * row->n);
	row->data = (low_data_struct *) mem_pool_malloc(mem_pool,
			sizeof(low_data_struct) * row->n);
	memset(row->data, 0, sizeof(low_data_struct) * row->n);

	struct access_way_t* access_way =
			(struct access_way_t*) sel_fields->access_way;

	if (access_way == NULL) {
		log_error("the db access way is null");
		return NULL;
	}

	low_data_struct **result = db_data_query_multi_col(table_name_, sid, docid,
			access_way->actual_fields_name, access_way->actual_sel_n,
			access_way->access_type, mem_pool);
	if (NULL == result) {
		log_error("db_data_query_multi_col failed");
		return NULL;
	}

	if (access_way->access_type == DATA_ACCESS_ORIGINAL) {
		for (i = 0; i < row->n; i++) {
			row->data[i] = *result[i];
			row->select_type[i] = SELECT_TYPE_ORIGINAL;
		}
	} else {
		n = 0;
		for (i = 0; i < row->n; i++) {
			if (sel_fields->select_type[i] == SELECT_TYPE_DELAY
					|| sel_fields->select_type[i] == SELECT_TYPE_SKIP) {
				row->select_type[i] = sel_fields->select_type[i];
				::memset(&row->data[i], 0, sizeof(low_data_struct));
				// link field name
				row->data[i].field_name = sel_fields->fields_name[i];
			} else {
				row->data[i] = *result[n]; // memcpy
				row->select_type[i] = access_way->actual_select_type[n];
				n++;
			}
		}
	}

	for (i = 0; i < row->n; i++) {
		if (strcmp(sel_fields->fields_name[i], "matchscore") == 0) {
			row->data[i].type = HI_TYPE_DOUBLE;
			row->data[i].len = sizeof(double);
			row->data[i].data = mem_pool_malloc(mem_pool, sizeof(double));
			*(double*) row->data[i].data = doc_handler->match_score;
			row->select_type[i] = SELECT_TYPE_ORIGINAL;
		}
		row->data[i].field_name = sel_fields->fields_name[i];
	}

	return row;
}

void DocTableMgr::IdentifyQueryWay(struct select_fields_t* sel_fields,
		MEM_POOL_PTR mem_pool) {

	if (NULL == sel_fields) {
		return;
	}

	struct access_way_t* access_way = (struct access_way_t*) mem_pool_malloc(
			mem_pool, sizeof(struct access_way_t));
	memset(access_way, 0, sizeof(struct access_way_t));
	access_way->actual_sel_n = 0;
	access_way->actual_select_type = (enum select_types_t*) mem_pool_malloc(
			mem_pool, sizeof(enum select_types_t) * sel_fields->n);
	memset(access_way->actual_select_type, 0,
			sizeof(enum select_types_t) * sel_fields->n);
	access_way->actual_fields_name = (char**) mem_pool_malloc(mem_pool,
			sizeof(char*) * sel_fields->n);
	memset(access_way->actual_fields_name, 0, sizeof(char*) * sel_fields->n);
	access_way->access_type = DATA_ACCESS_ORIGINAL;

	sel_fields->access_way = access_way;

	// check select type, select original or use filter index
	uint32_t i = 0;
	for (i = 0; i < sel_fields->n; i++) {
		if (strcmp(sel_fields->fields_name[i], "matchscore") == 0) {
			// the matchscore doesn't need to read from the storage
			sel_fields->select_type[i] = SELECT_TYPE_SKIP;
			continue;
		}

		if (sel_fields->select_type[i] != SELECT_TYPE_DELAY) {
			// TODO remove table_read_lock from db_get_data_access_type because already locked in UseIndex.
			enum field_access_type_t fat = db_get_data_access_type(table_name_,
					sel_fields->fields_name[i]);

			access_way->actual_fields_name[access_way->actual_sel_n] =
					sel_fields->fields_name[i];
			if (fat == FIELD_ACCESS_FILTER_HASHED) {
				access_way->actual_select_type[access_way->actual_sel_n] =
						SELECT_TYPE_HASH;
			} else {
				access_way->actual_select_type[access_way->actual_sel_n] =
						SELECT_TYPE_ORIGINAL;
			}
			access_way->actual_sel_n++;

			if (fat == FIELD_ACCESS_ORIGINAL)
				break;
			else if (fat == FIELD_ACCESS_FILTER_HASHED) {
				if (sel_fields->select_type[i] == SELECT_TYPE_ORIGINAL)
					break;
			}
		}
	}

	// select value
	if (i < sel_fields->n) { // select original
		access_way->access_type = DATA_ACCESS_ORIGINAL;
		access_way->actual_sel_n = sel_fields->n;
		access_way->actual_fields_name = sel_fields->fields_name;
		for (i = 0; i < sel_fields->n; i++) {
			access_way->actual_select_type[i] = SELECT_TYPE_ORIGINAL;
		}
	} else { // select from filter index
		access_way->access_type = DATA_ACCESS_FILTER;
	}

	return;
}

int64_t DocTableMgr::TotalRowCount(MEM_POOL_PTR mem_pool) {

	struct list_head *seg_list = db_seghint_query(table_name_, NULL, mem_pool);
	if (!seg_list) {
		log_error("get all segment failed, table %s", table_name_);
		return -1;
	}

	return db_get_record_num(table_name_, seg_list);
}

struct row_data *DocTableMgr::GetRowData(MileHandler *handler,
		MEM_POOL_PTR mem_pool) {
	if (!handler)
		return NULL;
	DocHandler *doc_handler = (DocHandler*) handler;
	uint16_t sid = doc_handler->docid >> 32;
	uint32_t docid = (uint32_t) doc_handler->docid;

	return db_data_query_row(table_name_, sid, docid, mem_pool);
}

