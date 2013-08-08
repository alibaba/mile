/*
 * MainStep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef MAINSTEP_H_
#define MAINSTEP_H_

#include "ExecuteStep.h"
#include "ExecuteSubstep.h"
#include "../common/MileList.h"
#include "../storage/TableManager.h"

class MainStep: public ExecuteStep {
private:
	void* result;
	MileList* substeps;
public:
	MainStep(void* output, MEM_POOL_PTR mem_pool);
	~MainStep();
	virtual void* Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool);
	void AddSubstep(ExecuteSubstep* substep);
};

#endif /* MAINSTEP_H_ */
