/*
 * DeleteSubstep.cpp
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#include "DeleteSubstep.h"



DeleteSubstep::DeleteSubstep(){

}


DeleteSubstep::~DeleteSubstep(){

}



int32_t DeleteSubstep::Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool){
	uint32_t* del_num = (uint32_t*)output;
	int32_t result_code = table->DeleteRow(handler, mem_pool);
	if(result_code == MILE_RETURN_SUCCESS){
		(*del_num)++;
	}
	return result_code;
}
