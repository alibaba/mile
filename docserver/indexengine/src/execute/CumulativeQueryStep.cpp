// CumulativeQueryStep.cpp : CumulativeQueryStep
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-11


#include "CumulativeQueryStep.h"
#include "CumulativeRS.h"
#include "../protocol/packet.h"
#include "../storage/ldb/LdbTableMgr.h"

void *CumulativeQueryStep::Execute(TableManager *table, void *input, int32_t &result_code,
		int64_t timeout, MEM_POOL_PTR mem_pool)
{
	CumulativeRS *res = NEW(mem_pool, CumulativeRS)(mem_pool);
	// check select filed
	for (uint32_t i = 0; i < select_field_->n; i++) {
		if (select_field_->select_fields[i].type != FUNCTION_SELECT) {
			log_error("select type must be FUNCTION_SELECT");
			result_code = -1;
			return res;
		}
	}

	LdbTableMgr *ldb = dynamic_cast<LdbTableMgr *>(table);
	assert(ldb && ldb->HasCumulativeIndex());

	select_row_t *row = init_select_row_t(mem_pool, select_field_->n);
	result_code = ldb->CumulativeQuery(row->data, select_field_, cond_, mem_pool);
	if (result_code)
		return res;

	res->AddResult(row);
	return res;
}

