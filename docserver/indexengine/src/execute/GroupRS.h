/*
 * GroupRS.h
 *
 *  Created on: 2012-8-26
 *      Author: yuzhong.zhao
 */

#ifndef GROUPRS_H_
#define GROUPRS_H_

#include "../common/ResultSet.h"
#include "../common/HashSet.h"
#include "../common/MileHashMap.h"
#include "../common/AggrFunc.h"
#include "../protocol/packet.h"
#include "RowEquals.h"
#include "RowHash.h"
#include "Expression.h"

class GroupRS: public ResultSet {
private:
	// all the select fields
	struct select_fields_t* sel_fields;
	// the number of the group fields;
	uint32_t grp_n;
	// the location of the group fields in the select fields
	uint32_t* grp_index;
	// the number of the aggregate function
	uint32_t agr_n;
	// the array of the aggregate function
	enum function_type* agr_func_types;
	// the array of the aggregate range function expression
	Expression** agr_range_exp;
	// the location of the aggregate function in the select fields
	uint32_t* agr_index;
	// the location of the reference field of the aggregation function in the source fields;
	uint32_t* agr_ref_index;
	// the hashmap to compute the group
	MileHashMap* map;
	MEM_POOL_PTR mem_pool;
public:
	GroupRS(struct select_field_array* sel_array, struct group_field_array* group_array, MEM_POOL_PTR mem_pool);
	~GroupRS();
	// all the fields that can be used in the group computing, including the group fields, reference fields in the aggregate function
	struct select_fields_t* src_fields;
	virtual int32_t AddResult(void* data);
	virtual uint32_t Size();
	virtual MileIterator* CreateIterator();
};

#endif /* GROUPRS_H_ */
