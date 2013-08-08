/*
 * CountAllStep.cpp
 *
 *  Created on: 2012-10-29
 *      Author: yuzhong.zhao
 */

#include "CountAllStep.h"

CountAllStep::CountAllStep(char* field_name) {
	this->field_name = field_name;
}

CountAllStep::~CountAllStep() {
	// TODO Auto-generated destructor stub
}

void* CountAllStep::Execute(TableManager* table, void* input,
		int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool) {

	Clonable* clone =
				new (mem_pool_malloc(mem_pool, sizeof(RowClone))) RowClone(
						mem_pool);
	ResultSet* result = new (mem_pool_malloc(mem_pool, sizeof(CommonRS))) CommonRS(
			1, clone, mem_pool);

	struct select_row_t* row = (struct select_row_t*) mem_pool_malloc(mem_pool, sizeof(struct select_row_t));
	memset(row, 0, sizeof(struct select_row_t));

	row->handler = NULL;
	row->n = 1;
	row->select_type = (enum select_types_t*) mem_pool_malloc(mem_pool, sizeof(enum select_types_t)) ;
	row->select_type[0] = SELECT_TYPE_ORIGINAL;
	row->data = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct));
	row->data[0].type = HI_TYPE_LONGLONG;
	row->data[0].len = sizeof(int64_t);
	row->data[0].data = mem_pool_malloc(mem_pool, sizeof(int64_t));
	*(int64_t*)(row->data[0].data) = table->TotalRowCount(mem_pool);
	row->data[0].field_name = field_name;

	result->AddResult(row);

	return result;
}
