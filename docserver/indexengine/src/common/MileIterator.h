/*
 * mile_iterator.h
 *
 *  Created on: 2012-7-31
 *      Author: yuzhong.zhao
 */

#ifndef MILE_ITERATOR_H_
#define MILE_ITERATOR_H_

#include "def.h"
#include "list.h"
#include "mem.h"
#include <new>

class TableManager;

class MileIterator{
public:
	MileIterator() {};

	virtual ~MileIterator() = 0;

	virtual void First() = 0;

	virtual void Next() = 0;

	virtual int8_t IsDone() = 0;

	virtual void* CurrentItem() = 0;

};

#endif /* MILE_ITERATOR_H_ */
