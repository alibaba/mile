// CumulativeRS.h : CumulativeRS
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-11

#ifndef CUMULATIVERS_H
#define CUMULATIVERS_H

#include "../common/mem.h"
#include "../common/ResultSet.h"
#include "../common/MileList.h"

struct select_row_t;

class CumulativeRS : public ResultSet {
public:
	CumulativeRS(MEM_POOL_PTR mem) : result_(mem) {}
	virtual ~CumulativeRS() {};

	// Must only be called once.
	virtual int32_t AddResult(void *data) { result_.Add(data); return MILE_RETURN_SUCCESS; }

	virtual uint32_t Size() { return result_.IsEmpty() ? 0 : 1; }

	virtual MileIterator *CreateIterator() { return result_.CreateIterator(); };

private:
	MileList result_;
};

#endif // CUMULATIVERS_H
