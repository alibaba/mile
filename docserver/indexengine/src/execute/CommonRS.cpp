/*
 * CommonRS.cpp
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#include "CommonRS.h"




CommonRS::CommonRS(uint32_t limit, Clonable* clone, MEM_POOL_PTR mem_pool){
	this->limit = limit;
	this->mem_pool = mem_pool;
	this->clone = clone;
	this->array = new(mem_pool_malloc(mem_pool, sizeof(MileArray)))MileArray(mem_pool);
}


CommonRS::~CommonRS(){

}



int32_t CommonRS::AddResult(void* data){

	uint32_t i;

	if(array->Size() == limit && limit != 0){
		return WARN_EXCEED_QUERY_LIMIT;
	}

	return array->Add(clone->Clone(data));
}



MileIterator* CommonRS::CreateIterator(){
	return array->CreateIterator();
}



uint32_t CommonRS::Size(){
	return array->Size();
}

