/*
 * MileList.cpp
 *
 *  Created on: 2012-8-26
 *      Author: yuzhong.zhao
 */

#include "MileList.h"




MileListIterator::MileListIterator(struct MileListItem* head){
	this->current = NULL;
	this->head = head;
}


MileListIterator::~MileListIterator(){

}


void MileListIterator::First(){
	current = head;
}


void MileListIterator::Next(){
	current = current->next;
}


void* MileListIterator::CurrentItem(){
	return current->data;
}


int8_t MileListIterator::IsDone(){
	return current == NULL;
}




MileList::MileList(MEM_POOL_PTR mem_pool){
	this->mem_pool = mem_pool;
	this->head = NULL;
	this->tail = NULL;
}

MileList::~MileList()
{
}

void MileList::Add(void* data){
	if(head == NULL){
		head = (struct MileListItem*) mem_pool_malloc(mem_pool, sizeof(struct MileListItem));
		memset(head, 0, sizeof(struct MileListItem));
		head->data = data;
		head->next = NULL;
		head->prev = NULL;
		tail = head;
	}else{
		tail->next = (struct MileListItem*) mem_pool_malloc(mem_pool, sizeof(struct MileListItem));
		tail->next->data = data;
		tail->next->next = NULL;
		tail->next->prev = tail;
		tail = tail->next;
	}
}


void* MileList::Pop(){
	void* data = NULL;
	if(head == NULL){
		return NULL;
	}else{
		data = tail->data;
		tail = tail->prev;
		if(tail == NULL){
			head = NULL;
		}else{
			tail->next = NULL;
		}
		return data;
	}
}



int MileList::IsEmpty(){
	return head == NULL;
}



MileIterator* MileList::CreateIterator(){
	return new(mem_pool_malloc(mem_pool, sizeof(MileListIterator)))MileListIterator(head);
}
