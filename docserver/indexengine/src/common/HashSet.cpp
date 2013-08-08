/*
 * HashSet.cpp
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#include "HashSet.h"

HashSetIterator::HashSetIterator(uint32_t bucket_size, MileList** data_array) {
	this->bucket_size = bucket_size;
	this->data_array = data_array;
	this->loc = 0;
	this->current = NULL;
}



HashSetIterator::~HashSetIterator(){

}


void HashSetIterator::First() {
	for (loc = 0; loc < bucket_size; loc++) {
		if (data_array[loc] != NULL ) {
			current = data_array[loc]->CreateIterator();
			current->First();
			return;
		}
	}
}

void HashSetIterator::Next() {
	current->Next();
	if (current->IsDone()) {
		for (loc++; loc < bucket_size; loc++) {
			if (data_array[loc] != NULL ) {
				current = data_array[loc]->CreateIterator();
				current->First();
				return;
			}
		}
	}
}

void* HashSetIterator::CurrentItem() {
	return current->CurrentItem();
}

int8_t HashSetIterator::IsDone() {
	return loc >= bucket_size;
}

HashSet::HashSet(Equals* equals, HashCoding* hash, MEM_POOL_PTR mem_pool) {
	uint32_t i;

	this->bucket_size = 1000;
	this->mem_pool = mem_pool;
	this->num = 0;
	this->equal_func = equals;
	this->hash_func = hash;
	this->data_array = (MileList**) mem_pool_malloc(mem_pool,
			sizeof(MileList*) * this->bucket_size);
	for (i = 0; i < this->bucket_size; i++) {
		this->data_array[i] = NULL;
	}
}

HashSet::~HashSet() {

}

void* HashSet::Get(void* data) {
	uint32_t key_hash = hash_func->Coding(data) % bucket_size;
	MileList* list = data_array[key_hash];
	MileIterator* iter;

	if (list == NULL ) {
		return NULL ;
	}

	iter = list->CreateIterator();
	for (iter->First(); !iter->IsDone(); iter->Next()) {
		if (equal_func->IsEqual(iter->CurrentItem(), data)) {
			return iter->CurrentItem();
		}
	}

	return NULL ;
}

int HashSet::Contains(void* data) {
	if(num == 0){
		return 0;
	}
	return Get(data) != NULL ;
}

void HashSet::Add(void* data) {
	uint32_t key_hash = hash_func->Coding(data) % bucket_size;
	MileList* list = data_array[key_hash];
	MileIterator* iter;

	if (list == NULL ) {
		list = new(mem_pool_malloc(mem_pool, sizeof(MileList)))MileList(mem_pool);
		data_array[key_hash] = list;
	} else {
		iter = list->CreateIterator();
		for (iter->First(); !iter->IsDone(); iter->Next()) {
			if (equal_func->IsEqual(iter->CurrentItem(), data)) {
				return;
			}
		}
	}

	num++;
	list->Add(data);
	return;
}

MileIterator* HashSet::CreateIterator() {
	HashSetIterator* iter = new(mem_pool_malloc(mem_pool, sizeof(HashSetIterator)))HashSetIterator(bucket_size, data_array);
	return iter;
}

int HashSet::Size() {
	return num;
}
