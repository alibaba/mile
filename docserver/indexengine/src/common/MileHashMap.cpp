/*
 * MileHashMap.cpp
 *
 *  Created on: 2012-8-27
 *      Author: yuzhong.zhao
 */

#include "MileHashMap.h"



MileHashMapIterator::MileHashMapIterator(uint32_t bucket_size,
		MileList** data_array) {
	this->bucket_size = bucket_size;
	this->data_array = data_array;
	this->loc = 0;
	this->current = NULL;

}


MileHashMapIterator::~MileHashMapIterator(){

}


void MileHashMapIterator::First() {
	for (loc = 0; loc < bucket_size; loc++) {
		if(data_array[loc] != NULL){
			current = data_array[loc]->CreateIterator();
			current->First();
			return;
		}
	}
}

void MileHashMapIterator::Next() {
	current->Next();
	if(current->IsDone()){
		for(loc++; loc < bucket_size; loc++){
			if(data_array[loc] != NULL){
				current = data_array[loc]->CreateIterator();
				current->First();
				return;
			}
		}
	}
}

void* MileHashMapIterator::CurrentItem() {
	return current->CurrentItem();
}

int8_t MileHashMapIterator::IsDone() {
	return loc >= bucket_size;
}






MapValueIterator::MapValueIterator(MileHashMapIterator* iter) {
	this->iter = iter;
}


MapValueIterator::~MapValueIterator(){

}


void MapValueIterator::First() {
	iter->First();
}

void MapValueIterator::Next() {
	iter->Next();
}

void* MapValueIterator::CurrentItem() {
	MileEntry* entry = (MileEntry*)iter->CurrentItem();
	return entry->value;
}

int8_t MapValueIterator::IsDone() {
	return iter->IsDone();
}










MileHashMap::MileHashMap(Equals* equals, HashCoding* hash,
		MEM_POOL_PTR mem_pool) {
	uint32_t i;

	this->equal_func = equals;
	this->hash_func = hash;
	this->mem_pool = mem_pool;
	this->num = 0;

	this->bucket_size = 1000;
	this->data_array = (MileList**) mem_pool_malloc(mem_pool,
			sizeof(MileList*) * this->bucket_size);
	for (i = 0; i < this->bucket_size; i++) {
		this->data_array[i] = NULL;
	}
}


void MileHashMap::Put(void* key, void* value){
	uint32_t key_hash = hash_func->Coding(key) % bucket_size;
	MileList* list = data_array[key_hash];
	MileIterator* iter;
	MileEntry* entry;

	if (list == NULL) {
		list = new(mem_pool_malloc(mem_pool, sizeof(MileList)))MileList(mem_pool);
		data_array[key_hash] = list;
	} else {
		iter = list->CreateIterator();
		for (iter->First(); !iter->IsDone(); iter->Next()) {
			entry = (MileEntry*)iter->CurrentItem();
			if (equal_func->IsEqual(entry->key, key)) {
				entry->value = value;
				return;
			}
		}
	}

	entry = (MileEntry*) mem_pool_malloc(mem_pool, sizeof(MileEntry));
	entry->key = key;
	entry->value = value;
	list->Add(entry);
	num++;
	return;
}

void* MileHashMap::Get(void* key){
	uint32_t key_hash = hash_func->Coding(key) % bucket_size;
	MileList* list = data_array[key_hash];
	MileIterator* iter;
	MileEntry* entry;

	if (list == NULL) {
		return NULL;
	}

	iter = list->CreateIterator();
	for (iter->First(); !iter->IsDone(); iter->Next()) {
		entry = (MileEntry*) iter->CurrentItem();
		if (equal_func->IsEqual(entry->key, key)) {
			return entry->value;
		}
	}

	return NULL;
}



int MileHashMap::Size(){
	return num;
}




int MileHashMap::Contains(void* key){
	return Get(key) != NULL;
}


MileIterator* MileHashMap::CreateIterator(){
	MileIterator* iter = new(mem_pool_malloc(mem_pool, sizeof(MileHashMapIterator)))MileHashMapIterator(bucket_size, data_array);
	return iter;
}

MileIterator* MileHashMap::CreateValueIterator(){
	MileHashMapIterator* map_iter = new(mem_pool_malloc(mem_pool, sizeof(MileHashMapIterator)))MileHashMapIterator(bucket_size, data_array);
	return new(mem_pool_malloc(mem_pool, sizeof(MapValueIterator)))MapValueIterator(map_iter);
}

