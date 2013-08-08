/*
 * IndexStep.cpp
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#include "IndexStep.h"

IndexStep::IndexStep(struct condition_array* cond, struct hint_array* hint){
	this->cond = cond;
	this->hint = hint;
	this->iter = NULL;
}


IndexStep::~IndexStep(){
	if(NULL != iter){
		iter->~MileIterator();
	}
}



void* IndexStep::Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool){
	struct index_condition* index_cond = (struct index_condition*)input;

	iter = table->UseIndex(cond, hint, mem_pool);
	if(NULL == iter){
		result_code = ERROR_INDEX_QUERY;
	}else{
		result_code = MILE_RETURN_SUCCESS;
	}
	return iter;
}
