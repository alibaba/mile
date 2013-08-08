/*
 * ExecutePlan.cpp
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#include "ExecutePlan.h"



ExecutePlan::ExecutePlan(TableManager* table, MEM_POOL_PTR mem_pool){
	this->mem_pool = mem_pool;
	this->steps = new(mem_pool_malloc(mem_pool, sizeof(MileList)))MileList(mem_pool);
	this->table = table;
}



ExecutePlan::~ExecutePlan(){
	MileIterator* iter = steps->CreateIterator();
	ExecuteStep* step;

	for(iter->First(); !iter->IsDone(); iter->Next()){
		step = (ExecuteStep*) iter->CurrentItem();
		step->~ExecuteStep();
	}
	table->~TableManager();
}




void ExecutePlan::AddExecuteStep(ExecuteStep* step){
	steps->Add(step);
}

void* ExecutePlan::Execute(int32_t &result_code, int64_t timeout){
	MileIterator* iter = steps->CreateIterator();
	ExecuteStep* step;
	void* result = NULL;
	result_code = MILE_RETURN_SUCCESS;

	for(iter->First(); !iter->IsDone(); iter->Next()){
		step = (ExecuteStep*) iter->CurrentItem();
		result = step->Execute(table, result, result_code, timeout, mem_pool);
		if(result_code < 0){
			break;
		}
		if(get_time_msec() > timeout){
			result_code = ERROR_TIMEOUT;
			break;
		}
	}

	return result;
}
