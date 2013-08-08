/*
 * SelectSubstep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef SELECTSUBSTEP_H_
#define SELECTSUBSTEP_H_

#include "ExecuteSubstep.h"
#include "../common/ResultSet.h"
#include "../common/def.h"
#include "../storage/TableManager.h"

class SelectSubstep: public ExecuteSubstep {
private:
	struct select_fields_t* sel_fields;
public:
	SelectSubstep(struct select_fields_t* sel_fields);
	~SelectSubstep();
	virtual int32_t Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool);
};

#endif /* SELECTSUBSTEP_H_ */
