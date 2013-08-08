/*
 * DistinctRS.cpp
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#include "DistinctRS.h"





DistinctRS::DistinctRS(Equals* equals, HashCoding* hash, Clonable* clone, uint32_t limit, MEM_POOL_PTR mem_pool){
	this->num = 0;
	this->limit = limit;
	this->clone = clone;
	this->mem_pool = mem_pool;
	this->set = new(mem_pool_malloc(mem_pool, sizeof(HashSet)))HashSet(equals, hash, mem_pool);
}



DistinctRS::~DistinctRS(){

}




int32_t DistinctRS::AddResult(void* data){
	if(num == limit && limit != 0){
		return WARN_EXCEED_QUERY_LIMIT;
	}
	if(set->Contains(data)){
		return MILE_RETURN_SUCCESS;
	}
	set->Add(clone->Clone(data));
	num++;
	return MILE_RETURN_SUCCESS;
}



MileIterator* DistinctRS::CreateIterator(){
	return set->CreateIterator();
}



uint32_t DistinctRS::Size(){
	return set->Size();
}
