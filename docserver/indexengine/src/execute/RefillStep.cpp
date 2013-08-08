/*
 * RefillStep.cpp
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#include "RefillStep.h"


RefillStep::RefillStep(struct select_field_array* select_field, MEM_POOL_PTR mem_pool){
	uint32_t i;

	this->n = select_field->n;
	this->fields = (char**) mem_pool_malloc(mem_pool, sizeof(char*)*this->n);
	for(i = 0; i < this->n; i++){
		this->fields[i] = select_field->select_fields[i].field_name;
	}
}



RefillStep::~RefillStep(){

}



void* RefillStep::Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool){
	uint32_t i;
	ResultSet* result = (ResultSet*) input;
	MileIterator* iter = result->CreateIterator();
	struct select_row_t* row;
	struct select_row_t* refill_row;
	uint32_t* refill_ref;
	struct select_fields_t* refill_fields = (struct select_fields_t*) mem_pool_malloc(mem_pool, sizeof(struct select_fields_t));
	memset(refill_fields, 0, sizeof(struct select_fields_t));


	for(iter->First(); !iter->IsDone(); iter->Next()){
		row = (struct select_row_t*) iter->CurrentItem();

		refill_fields->n = 0;
		refill_fields->fields_name = (char**) mem_pool_malloc(mem_pool, sizeof(char*)*row->n);
		refill_fields->select_type = (enum select_types_t*) mem_pool_malloc(mem_pool, sizeof(enum select_types_t)*row->n);
		refill_ref = (uint32_t*) mem_pool_malloc(mem_pool, sizeof(uint32_t)*row->n);

		for(i = 0; i < row->n; i++){
			if(row->select_type[i] != SELECT_TYPE_ORIGINAL){
				refill_fields->fields_name[refill_fields->n] = fields[i];
				refill_fields->select_type[refill_fields->n] = SELECT_TYPE_ORIGINAL;
				refill_ref[refill_fields->n] = i;
				refill_fields->n++;
			}
		}
		break;
	}
	if(refill_fields->n == 0){
		return result;
	}

	table->IdentifyQueryWay(refill_fields, mem_pool);

	for(iter->First(); !iter->IsDone(); iter->Next()){
		row = (struct select_row_t*) iter->CurrentItem();
		refill_row = table->QueryRow((MileHandler*) row->handler, refill_fields, mem_pool);
		if(NULL == refill_row){
			result_code = ERROR_QUERY_BY_ROWID;
			return NULL;
		}

		for(i = 0; i < refill_fields->n; i++){
			memcpy(&row->data[refill_ref[i]], &refill_row->data[i], sizeof(low_data_struct));
			row->select_type[refill_ref[i]] = SELECT_TYPE_ORIGINAL;
		}
	}

	return result;
}
