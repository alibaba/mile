/*
 * ExecuteSubstep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef EXECUTESUBSTEP_H_
#define EXECUTESUBSTEP_H_

#include "../common/list.h"
#include "../common/def.h"
#include "../storage/StorageEngine.h"
#include "../storage/TableManager.h"

#ifndef NULL
#define NULL ((void *)0)
#endif

class ExecuteSubstep {
public:
	virtual ~ExecuteSubstep() {};
	/**
	 * return value: <0 indicate error, 0 indicate success, >0 indicate continue
	 */
	virtual int32_t Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool) = 0;
};

#endif /* EXECUTESUBSTEP_H_ */
