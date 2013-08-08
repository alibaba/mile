/*
 * MileArray.cpp
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#include "MileArray.h"

MileArrayIterator::MileArrayIterator(uint32_t num, void** data_array) {
	this->num = num;
	this->data_array = data_array;
	this->loc = 0;
}

MileArrayIterator::~MileArrayIterator() {

}

void MileArrayIterator::First() {
	loc = 0;
}

void MileArrayIterator::Next() {
	loc++;
}

void* MileArrayIterator::CurrentItem() {
	return data_array[loc];
}

int8_t MileArrayIterator::IsDone() {
	return loc >= num;
}

MileArray::MileArray(MEM_POOL_PTR mem_pool) {
	this->size = 1000;
	this->num = 0;
	this->mem_pool = mem_pool;
	this->data_array = (void**) mem_pool_malloc(mem_pool, sizeof(void*) * size);
}

int32_t MileArray::Add(void* data) {
	if (num == size) {
		void** tmp = (void**) mem_pool_malloc(mem_pool,
				sizeof(void*) * size * 2);
		if (tmp == NULL ) {
			return ERROR_NOT_ENOUGH_MEMORY;
		} else {
			memset(tmp, 0, sizeof(void*) * size * 2);
			memcpy(tmp, data_array, sizeof(void*) * size);
			size = size * 2;
			data_array = tmp;
		}
	}

	data_array[num++] = data;
	return MILE_RETURN_SUCCESS;
}

void* MileArray::Get(int i) {
	if (i >= num)
		return NULL ;
	return data_array[i];
}

void MileArray::Set(int index, void* data) {
	if (index < num) {
		data_array[index] = data;
	}
}

uint32_t MileArray::Size() {
	return num;
}

void MileArray::BuildHeap(Comparator* comp) {
	int i;

	if (num >= 2) {
		for (i = (num - 2) / 2; i >= 0; i--) {
			Heapify(i, comp);
		}
	}
}

void MileArray::Heapify(int root, Comparator* comp) {
	int max = root;
	void* tmp;

	if (root * 2 + 1 >= num) {
		return;
	} else if (comp->Compare(data_array[root * 2 + 1], data_array[max]) == 1) {
		max = root * 2 + 1;
	}

	if (root * 2 + 2 < num
			&& comp->Compare(data_array[root * 2 + 2], data_array[max]) == 1) {
		max = root * 2 + 2;
	}

	if (max != root) {
		tmp = data_array[max];
		data_array[max] = data_array[root];
		data_array[root] = tmp;
		Heapify(max, comp);
	}
}

MileIterator* MileArray::CreateIterator() {
	return new (mem_pool_malloc(mem_pool, sizeof(MileArrayIterator))) MileArrayIterator(
			num, data_array);
}
