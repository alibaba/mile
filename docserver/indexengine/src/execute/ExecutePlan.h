/*
 * ExecutePlan.h
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#ifndef EXECUTEPLAN_H_
#define EXECUTEPLAN_H_

#include "../common/MileList.h"
#include "../storage/TableManager.h"
#include "ExecuteStep.h"


class ExecutePlan {
private:
	MileList* steps;
	TableManager* table;
	MEM_POOL_PTR mem_pool;
public:
	ExecutePlan(TableManager* table, MEM_POOL_PTR mem_pool);
	~ExecutePlan();
	void* Execute(int32_t &result_code, int64_t timeout);
	void AddExecuteStep(ExecuteStep* step);
};

#endif /* EXECUTEPLAN_H_ */
