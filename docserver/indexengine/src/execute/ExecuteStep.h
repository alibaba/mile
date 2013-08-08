/*
 * ExecuteStep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef EXECUTESTEP_H_
#define EXECUTESTEP_H_

#include "../storage/StorageEngine.h"
#include "../storage/TableManager.h"


class ExecuteStep {
public:
	virtual ~ExecuteStep() {};
	virtual void* Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool) = 0;
};

#endif /* EXECUTESTEP_H_ */
