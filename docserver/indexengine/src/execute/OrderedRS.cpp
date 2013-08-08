/*
 * OrderedRS.cpp
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#include "OrderedRS.h"



OrderedRS::OrderedRS(Comparator* comp, Clonable* clone, uint32_t limit, MEM_POOL_PTR mem_pool){
	this->limit = limit;
	this->clone = clone;
	this->mem_pool = mem_pool;
	this->comp = comp;
	this->heaped = 0;
	this->array = new(mem_pool_malloc(mem_pool, sizeof(MileArray)))MileArray(mem_pool);
}


OrderedRS::~OrderedRS(){

}


int32_t OrderedRS::AddResult(void* data){
	uint32_t i;
	int cp_res;

	if(array->Size() == limit && limit != 0){
		if(!heaped){
			array->BuildHeap(comp);
			heaped = 1;
		}
		cp_res = comp->Compare(data, array->Get(0));
		if(cp_res == -2){
			return -2;
		}
		if(cp_res == -1){
			array->Set(0, clone->Clone(data));
			array->Heapify(0, comp);
		}
	}else{
		array->Add(clone->Clone(data));
	}
	return MILE_RETURN_SUCCESS;
}




MileIterator* OrderedRS::CreateIterator(){
	return array->CreateIterator();
}



uint32_t OrderedRS::Size(){
	return array->Size();
}
