/*
 * RowClone.cpp
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#include "RowClone.h"



RowClone::RowClone(MEM_POOL_PTR mem_pool){
	this->mem_pool = mem_pool;
}


RowClone::~RowClone(){

}


void* RowClone::Clone(void* data){
	uint32_t i;
	struct select_row_t* rowa = (struct select_row_t*) data;
	struct select_row_t* rowb = (struct select_row_t*) mem_pool_malloc(mem_pool, sizeof(struct select_row_t));
	memset(rowb, 0, sizeof(struct select_row_t));

	rowb->n = rowa->n;
	rowb->handler = rowa->handler;
	rowb->select_type = (enum select_types_t*) mem_pool_malloc(mem_pool, sizeof(enum select_types_t)*rowa->n);
	memset(rowb->select_type, 0, sizeof(enum select_types_t)*rowa->n);
	rowb->data = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct)*rowa->n);
	memset(rowb->data, 0, sizeof(struct low_data_struct)*rowa->n);

	for(i = 0; i < rowa->n; i++){
		rowb->select_type[i] = rowa->select_type[i];
		copy_low_data_struct(mem_pool, &rowb->data[i], &rowa->data[i]);
	}

	return rowb;
}
