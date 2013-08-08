/*
 * UpdateSubstep.cpp
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#include "UpdateSubstep.h"



UpdateSubstep::UpdateSubstep(char* up_field, struct low_data_struct* ld, MEM_POOL_PTR mem_pool){
	this->new_data = (struct row_data*) mem_pool_malloc(mem_pool, sizeof(struct row_data));
	this->new_data->field_count = 1;
	this->new_data->datas = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct) * this->new_data->field_count);
	this->new_data->datas[0] = *ld;
}


UpdateSubstep::~UpdateSubstep(){

}



int32_t UpdateSubstep::Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool){
	uint32_t* up_num = (uint32_t*)output;
	int32_t result_code = table->UpdateRow(handler, new_data, mem_pool);
	if(result_code == MILE_RETURN_SUCCESS){
		(*up_num)++;
	}
	return result_code;
}
