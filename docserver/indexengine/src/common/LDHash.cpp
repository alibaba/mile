/*
 * LDHash.cpp
 *
 *  Created on: 2012-8-30
 *      Author: yuzhong.zhao
 */

#include "LDHash.h"

LDHash::LDHash() {
	// TODO Auto-generated constructor stub

}

LDHash::~LDHash() {
	// TODO Auto-generated destructor stub
}



uint32_t LDHash::Coding(void* value) {
	uint32_t i;
	uint8_t* p;
	struct low_data_struct* ld = (struct low_data_struct*) value;
	uint32_t hash = 1315423911;

	p = (uint8_t*) ld->data;
	for (i = 0; i < ld->len; i++) {
		hash ^= ((hash << 5) + (*p++) + (hash >> 2));
	}

	return (hash & 0x7FFFFFFF);
}
