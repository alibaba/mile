/*
 * InsertStep.h
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#ifndef INSERTSTEP_H_
#define INSERTSTEP_H_

#include "ExecuteStep.h"

class InsertStep: public ExecuteStep {
private:
	struct row_data* row;
public:
	InsertStep(uint32_t n, struct low_data_struct* data, MEM_POOL_PTR mem_pool);
	~InsertStep();
	virtual void* Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool);
};

#endif /* INSERTSTEP_H_ */
