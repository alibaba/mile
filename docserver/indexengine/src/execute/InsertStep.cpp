/*
 * InsertStep.cpp
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#include "InsertStep.h"



InsertStep::InsertStep(uint32_t n, struct low_data_struct* data, MEM_POOL_PTR mem_pool){
	this->row = (struct row_data*) mem_pool_malloc(mem_pool, sizeof(struct row_data));
	this->row->field_count = n;
	this->row->datas = data;
}



InsertStep::~InsertStep(){

}


void* InsertStep::Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool){
	MileHandler* handler;
	uint64_t*  handler_id = (uint64_t*) mem_pool_malloc(mem_pool, sizeof(uint64_t));

	result_code = table->InsertRow(handler, row, mem_pool);
	*(uint64_t*) handler_id = handler->GetHandlerId();

	return handler_id;
}
