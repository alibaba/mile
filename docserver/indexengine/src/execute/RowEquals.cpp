/*
 * RowEquals.cpp
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#include "RowEquals.h"




RowEquals::RowEquals(){

}


RowEquals::~RowEquals(){

}


int RowEquals::IsEqual(void* a, void* b){
	uint32_t i;
	struct select_row_t* rowa = (struct select_row_t*) a;
	struct select_row_t* rowb = (struct select_row_t*) b;

	if(rowa->n != rowb->n){
		return 0;
	}

	for(i = 0; i < rowa->n; i++){
		if(!is_ld_equal(&rowa->data[i], &rowb->data[i])){
			return 0;
		}
	}

	return 1;
}


