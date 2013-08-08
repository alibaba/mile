/*
 * CountAllStep.h
 *
 *  Created on: 2012-10-29
 *      Author: yuzhong.zhao
 */

#ifndef COUNTALLSTEP_H_
#define COUNTALLSTEP_H_

#include "CommonRS.h"
#include "ExecuteStep.h"
#include "RowClone.h"


class CountAllStep: public ExecuteStep {
private:
	char* field_name;
public:
	CountAllStep(char* field_name);
	virtual ~CountAllStep();
	virtual void* Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool);
};

#endif /* COUNTALLSTEP_H_ */
