/*
 * FilterSubstep.h
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#ifndef FILTERSUBSTEP_H_
#define FILTERSUBSTEP_H_

#include "ExecuteSubstep.h"
#include "../protocol/packet.h"
#include "Expression.h"



class FilterSubstep: public ExecuteSubstep {
private:
	Expression* exp;
	int32_t CompareValue(struct low_data_struct* value, struct condition_t *cond);
public:
	struct select_fields_t* sel_fields;
	FilterSubstep(struct condition_array* cond_array, MEM_POOL_PTR mem_pool);
	~FilterSubstep();
	virtual int32_t Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool);
};

#endif /* FILTERSUBSTEP_H_ */
