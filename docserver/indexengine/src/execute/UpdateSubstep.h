/*
 * UpdateSubstep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef UPDATESUBSTEP_H_
#define UPDATESUBSTEP_H_

#include "ExecuteSubstep.h"

class UpdateSubstep: public ExecuteSubstep {
private:
	struct row_data* new_data;
public:
	UpdateSubstep(char* up_field, struct low_data_struct* ld, MEM_POOL_PTR mem_pool);
	virtual ~UpdateSubstep();
	virtual int32_t Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool);
};

#endif /* UPDATESUBSTEP_H_ */
