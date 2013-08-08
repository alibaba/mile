// LdbIterator.h : LdbIterator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-08

#ifndef LDBITERATOR_H
#define LDBITERATOR_H

#include "../RecordIterator.h"
#include "leveldb/slice.h"
#include "leveldb/iterator.h"
#include "leveldb/comparator.h"
#include "LdbHandler.h"

struct range_t
{
	const static uint8_t INCLUSIVE_START = 1;
	const static uint8_t INCLUSIVE_END = 1 << 1;
	const static uint8_t HAS_START = 1 << 2;
	const static uint8_t HAS_END = 1 << 3;

	// invalid flag, invalid case: start > end 
	const static uint8_t INVALID_FLAG = 1 << 4;

	uint8_t border;

	leveldb::Slice start;
	leveldb::Slice end;

public:
	range_t() : border(0) {}
	bool InclusiveStart() const { return border & INCLUSIVE_START; }
	bool InclusiveEnd() const { return border & INCLUSIVE_END; }
	bool HasStart() const { return border & HAS_START; }
	bool HasEnd() const { return border & HAS_END; }
	// have start and end
	bool IsWhole() const { return HasStart() && HasEnd(); }
	bool IsValid() const { return !(border & INVALID_FLAG); }
};

class LdbComparator;


class LdbIterator : public RecordIterator
{
public:
	LdbIterator(TableManager *table_mgr, const range_t &range, const leveldb::Comparator *comp,
			leveldb::Iterator *iter, MEM_POOL_PTR mem);
	virtual ~LdbIterator();

	virtual void First();
	virtual void Next();
	virtual int8_t IsDone() { return !valid_; }
	virtual void* CurrentItem();

private:
	//  for CurrentItem() value store
	MEM_POOL_PTR mem_;

	range_t range_;
	const leveldb::Comparator *comparetor_;
	leveldb::Iterator *iter_;
	bool valid_;
};

#endif // LDBITERATOR_H
