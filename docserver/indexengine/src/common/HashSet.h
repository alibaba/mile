/*
 * HashSet.h
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#ifndef HASHSET_H_
#define HASHSET_H_
#include "def.h"
#include "MileIterator.h"
#include "Equals.h"
#include "HashCoding.h"
#include "MileList.h"



class HashSetIterator: public MileIterator{
private:
	uint32_t loc;
	MileIterator* current;
	uint32_t bucket_size;
	MileList** data_array;
public:
	HashSetIterator(uint32_t bucket_size, MileList** data_array);
	~HashSetIterator();
	virtual void First();
	virtual void Next();
	virtual int8_t IsDone();
	virtual void *CurrentItem();
};



class HashSet {
private:
	//等值函数
	Equals* equal_func;
	//编码函数
	HashCoding* hash_func;
	//元素个数
	uint32_t num;
	//桶的大小
	uint32_t bucket_size;
	//存储数据的数组
	MileList** data_array;
	//内存池
	MEM_POOL_PTR mem_pool;
public:
	HashSet(Equals* equals, HashCoding* hash, MEM_POOL_PTR mem_pool);
	~HashSet();
	void Add(void* data);
	void* Get(void* data);
	int Contains(void* data);
	int Size();
	MileIterator* CreateIterator();
};

#endif /* HASHSET_H_ */
