// CumulativeQueryStep.h : CumulativeQueryStep
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-11

#ifndef CUMULATIVEQUERYSTEP_H
#define CUMULATIVEQUERYSTEP_H

#include "ExecuteStep.h"

struct select_field_array;
struct condition_array;

class CumulativeQueryStep : public ExecuteStep
{
public :
	CumulativeQueryStep(select_field_array *sel, condition_array *cond)
		: select_field_(sel), cond_(cond) {}

	virtual ~CumulativeQueryStep() {};

	virtual void *Execute(TableManager *table, void *input, int32_t &result_code,
			int64_t timeout, MEM_POOL_PTR mem_pool);

private:
	select_field_array *select_field_;
	condition_array *cond_;
};

#endif // CUMULATIVEQUERYSTEP_H
