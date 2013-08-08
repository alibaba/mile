/*
 * OrderComparator.cpp
 *
 *  Created on: 2012-8-16
 *      Author: yuzhong.zhao
 */

#include "OrderComparator.h"

OrderComparator::OrderComparator(struct select_field_array* select_field,
		struct order_field_array* order_array, MEM_POOL_PTR mem_pool){
	uint32_t i, j;

	this->n = order_array->n;
	this->ord_index = (uint32_t*) mem_pool_malloc(mem_pool, sizeof(uint32_t)*order_array->n);
	this->ord_type = (enum order_types*) mem_pool_malloc(mem_pool, sizeof(enum order_types)*order_array->n);


	for(i = 0; i < order_array->n; i++){
		for(j = 0; j < select_field->n; j++){
			if(strcmp(order_array->order_fields[i].field_name, select_field->select_fields[j].field_name) == 0){
				this->ord_index[i] = j;
				break;
			}
		}
	}


	for(i = 0; i < order_array->n; i++){
		this->ord_type[i] = order_array->order_fields[i].order_type;
	}

}



OrderComparator::~OrderComparator(){

}




int OrderComparator::Compare(void* a, void* b) {
	uint32_t i;
	int ret = 0;
	struct select_row_t* rowa = (struct select_row_t*) a;
	struct select_row_t* rowb = (struct select_row_t*) b;

	for(i = 0; i < n; i++){
		ret = compare_ld(&rowa->data[ord_index[i]], &rowb->data[ord_index[i]]);
		if(ret == -2){
			return ret;
		}
		if(ord_type[i] == ORDER_TYPE_DESC){
			ret = -ret;
		}
		if(ret != 0){
			return ret;
		}
	}

	return ret;
}
