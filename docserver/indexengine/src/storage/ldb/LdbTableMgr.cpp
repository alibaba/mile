// LdbTableMgr.cpp : LdbTableMgr
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#include "LdbTableMgr.h"
#include "LdbTableImpl.h"
#include "LdbIterator.h"
#include "AggregateDesc.h"
#include "../../protocol/packet.h"

#include "leveldb/slice.h"

#include <algorithm>

LdbTableMgr::~LdbTableMgr() {
	if (table_)
		table_->UnRef();
}

int LdbTableMgr::InsertRow(MileHandler* &handler, struct row_data* rdata,
		MEM_POOL_PTR mem_pool) {
	handler = NEW(mem_pool, LdbHandler);
	return table_->InsertRow(rdata, mem_pool);
}

MileIterator *LdbTableMgr::UseIndex(struct condition_array* cond,
		struct hint_array* hint, MEM_POOL_PTR mem_pool) {
	return table_->UseIndex(this, cond, mem_pool);
}

struct select_row_t* LdbTableMgr::QueryRow(MileHandler* handler,
		struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool) {
	if (NULL == handler)
		return NULL;

	LdbHandler* ldb_handler = (LdbHandler*) handler;
	return table_->QueryRow(&ldb_handler->value, sel_fields,
			mem_pool);
}

void LdbTableMgr::IdentifyQueryWay(struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool){
	if(sel_fields != NULL){
		sel_fields->access_way = NULL;
	}
	return;
}


struct row_data *LdbTableMgr::GetRowData(MileHandler *handler, MEM_POOL_PTR mem_pool)
{
	if (!handler)
		return NULL;

	LdbHandler *ldb_handler = (LdbHandler*) handler;
	return lowdata_to_rowdata(&ldb_handler->value, mem_pool);
}

int LdbTableMgr::DeleteRow(MileHandler* handler, MEM_POOL_PTR mem_pool) {
	if (NULL == handler) {
		return 0;
	}

	LdbHandler* ldb_handler = (LdbHandler*) handler;

	return table_->DeleteRow(&ldb_handler->key, mem_pool);
}

int LdbTableMgr::UpdateRow(MileHandler* handler, struct row_data* new_data, MEM_POOL_PTR mem_pool)
{
	ASSERT(handler);
	LdbHandler *h = (LdbHandler*)handler;
	return table_->UpdateRow(&h->key, &h->value, new_data, mem_pool);
}

bool LdbTableMgr::HasCumulativeIndex() {
	return table_->HasCumulativeIndex();
}

int LdbTableMgr::CumulativeQuery(low_data_struct *res, select_field_array *sel,
		condition_array* cond, MEM_POOL_PTR mem_pool) {
	// get aggregate desc
	aggregate_desc_t desc[sel->n];
	::memset(desc, 0, sizeof(desc));
	for (uint32_t i = 0; i < sel->n; i++) {
		if (sel->select_fields[i].type != FUNCTION_SELECT) {
			log_error("only function select suported in cumulative query");
			return -1;
		}
		desc[i].func_type = sel->select_fields[i].func_type;
		desc[i].name = sel->select_fields[i].field_name;
		if (desc[i].func_type == FUNC_COUNT
				&& (NULL == desc[i].name || strlen(desc[i].name) == 0))
			desc[i].name = "*";
	}

	// sort and find index
	aggregate_desc_t sorted_desc[sel->n];
	::memcpy(sorted_desc, desc, sizeof(sorted_desc));
	aggregate_desc_array_t array = { sel->n, sorted_desc };
	std::sort(array.data, array.data + array.size);

	int index[sel->n];
	for (uint32_t i = 0; i < array.size; i++) {
		aggregate_desc_t *p = array.Find(desc[i]);
		ASSERT(p);
		index[i] = p - array.data;
	}

	if (table_->CumulativeQuery(&array, cond, mem_pool) < 0)
		return -1;

	// set result
	for (uint32_t i = 0; i < sel->n; i++) {
		res[i] = array.data[index[i]].value;
	}

	return 0;
}
