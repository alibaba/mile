/*
 * MileList.h
 *
 *  Created on: 2012-8-26
 *      Author: yuzhong.zhao
 */

#ifndef MILELIST_H_
#define MILELIST_H_

#include "MileIterator.h"
#include "list.h"


struct MileListItem{
	void* data;
	struct MileListItem* next;
	struct MileListItem* prev;
};



class MileListIterator : public MileIterator{
private:
	struct MileListItem* current;
	struct MileListItem* head;
public:
	MileListIterator(struct MileListItem* head);
	~MileListIterator();
	virtual void First();
	virtual void Next();
	virtual int8_t IsDone();
	virtual void *CurrentItem();
};



class MileList {
private:
	struct MileListItem* head;
	struct MileListItem* tail;
	MEM_POOL_PTR mem_pool;
public:
	MileList(MEM_POOL_PTR mem_pool);
	~MileList();
	void Add(void* data);
	//get the tail of the list and delete it form the list
	void* Pop();
	int IsEmpty();
	MileIterator* CreateIterator();
};

#endif /* MILELIST_H_ */
