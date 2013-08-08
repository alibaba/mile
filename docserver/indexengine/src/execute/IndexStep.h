/*
 * IndexStep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef INDEXSTEP_H_
#define INDEXSTEP_H_

#include "ExecuteStep.h"
#include "../protocol/packet.h"

class IndexStep: public ExecuteStep {
private:
	struct condition_array* cond;
	struct hint_array* hint;
	MileIterator* iter;
public:
	IndexStep(struct condition_array* cond, struct hint_array* hint);
	~IndexStep();
	virtual void* Execute(TableManager* table, void* input, int32_t &result_code, int64_t timeout, MEM_POOL_PTR mem_pool);
};

#endif /* INDEXSTEP_H_ */
