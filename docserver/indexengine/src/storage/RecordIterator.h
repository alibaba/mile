// RecordIterator.h : RecordIterator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-28

#ifndef RECORDITERATOR_H
#define RECORDITERATOR_H

#include "../common/MileIterator.h"

class TableManager;

class RecordIterator : public MileIterator {
public:
	RecordIterator(TableManager *table_mgr);
	virtual ~RecordIterator();

	virtual void First() = 0;

	virtual void Next() = 0;

	virtual int8_t IsDone() = 0;

	virtual void* CurrentItem() = 0;

private:
	TableManager *table_mgr_;
};

#endif // RECORDITERATOR_H
