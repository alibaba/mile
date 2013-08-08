/*
 * FilterSubstep.cpp
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#include "FilterSubstep.h"

FilterSubstep::FilterSubstep(struct condition_array* cond_array, MEM_POOL_PTR mem_pool){
	this->exp = Expression::GenExp(cond_array, mem_pool);
	this->sel_fields = exp->exp_fields;
}



FilterSubstep::~FilterSubstep(){

}



int32_t FilterSubstep::Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool){
	int32_t result;
	struct select_row_t* row;

	row = table->QueryRow(handler, sel_fields, mem_pool);
	if(row == NULL){
		return -1;
	}
	result = exp->Execute(row);
	if(result == 0){
		return 1;
	}else if(result == 1){
		return 0;
	}else{
		return result;
	}
}
