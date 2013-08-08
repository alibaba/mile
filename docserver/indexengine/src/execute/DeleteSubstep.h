/*
 * DeleteSubstep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef DELETESUBSTEP_H_
#define DELETESUBSTEP_H_

#include "ExecuteSubstep.h"

class DeleteSubstep: public ExecuteSubstep {
public:
	DeleteSubstep();
	~DeleteSubstep();
	virtual int32_t Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool);

};

#endif /* DELETESUBSTEP_H_ */
