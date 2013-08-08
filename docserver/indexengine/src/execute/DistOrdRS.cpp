/*
 * DistOrdRS.cpp
 *
 *  Created on: 2012-8-21
 *      Author: yuzhong.zhao
 */

#include "DistOrdRS.h"




DistOrdRS::DistOrdRS(Equals* equals, HashCoding* hash, Comparator* comp, Clonable* clone, uint32_t limit, MEM_POOL_PTR mem_pool){

	this->limit = limit;
	this->set = new(mem_pool_malloc(mem_pool, sizeof(HashSet)))HashSet(equals, hash, mem_pool);
	this->clone = clone;
	this->comp = comp;
	this->mem_pool = mem_pool;
	this->heaped = 0;
	this->array = new(mem_pool_malloc(mem_pool, sizeof(MileArray)))MileArray(mem_pool);
}


DistOrdRS::~DistOrdRS(){

}



int32_t DistOrdRS::AddResult(void* data){
	int cp_res;
	void* cp_data;

	if(set->Contains(data)){
		return MILE_RETURN_SUCCESS;
	}
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
			cp_data = clone->Clone(data);
			array->Set(0, cp_data);
			array->Heapify(0, comp);
			set->Add(cp_data);
		}
	}else{
		cp_data = clone->Clone(data);
		set->Add(cp_data);
		array->Add(cp_data);
	}


	return MILE_RETURN_SUCCESS;
}




MileIterator* DistOrdRS::CreateIterator(){
	return array->CreateIterator();
}



uint32_t DistOrdRS::Size(){
	return array->Size();
}
