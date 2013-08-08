/*
 * MileArray.h
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#ifndef MILEARRAY_H_
#define MILEARRAY_H_

#include "def.h"
#include "MileIterator.h"
#include "Comparator.h"



class MileArrayIterator: public MileIterator{
private:
	uint32_t loc;
	uint32_t num;
	void** data_array;
public:
	MileArrayIterator(uint32_t num, void** data_array);
	~MileArrayIterator();
	virtual void First();
	virtual void Next();
	virtual int8_t IsDone();
	virtual void *CurrentItem();
};




class MileArray {
private:
	MEM_POOL_PTR mem_pool;
	uint32_t size;
	uint32_t num;
	void** data_array;
public:
	MileArray(MEM_POOL_PTR mem_pool);
	~MileArray();
	int32_t Add(void* data);
	void* Get(int i);
	void Set(int index, void* data);
	uint32_t Size();
	void BuildHeap(Comparator* comp);
	void Heapify(int root, Comparator* comp);
	MileIterator* CreateIterator();
};

#endif /* MILEARRAY_H_ */
