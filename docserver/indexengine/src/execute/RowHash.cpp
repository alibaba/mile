/*
 * RowHash.cpp
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#include "RowHash.h"



RowHash::RowHash(){

}



RowHash::~RowHash(){

}



uint32_t RowHash::Coding(void* value){
	uint32_t i, j;
	uint8_t* p;
	struct select_row_t* row = (struct select_row_t*) value;
	uint32_t hash = 1315423911;

	for(i = 0; i < row->n; i++)
	{
		p = (uint8_t*) row->data[i].data;
		for(j = 0; j < row->data[i].len; j++)
		{
			hash ^= ((hash<<5) + (*p++) + (hash>>2));
		}
	}

	return (hash & 0x7FFFFFFF);
}
