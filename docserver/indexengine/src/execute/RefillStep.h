/*
 * RefillStep.h
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#ifndef REFILLSTEP_H_
#define REFILLSTEP_H_

#include "ExecuteStep.h"
#include "../common/ResultSet.h"
#include "../protocol/packet.h"

class RefillStep: public ExecuteStep {
private:
	uint32_t n;
	char** fields;
public:
	RefillStep(struct select_field_array* select_field, MEM_POOL_PTR mem_pool);
	~RefillStep();
	virtual void* Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool);
};

#endif /* REFILLSTEP_H_ */
