/*
 * AggrFunc.h
 *
 *  Created on: 2012-9-12
 *      Author: yuzhong.zhao
 */

#ifndef AGGRFUNC_H_
#define AGGRFUNC_H_

#include "def.h"
#include "common_util.h"
#include "MileList.h"
#include "HashSet.h"
#include "MileArray.h"
#include "LDHash.h"
#include "LDEquals.h"
#include <math.h>


int32_t Count(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value);
int32_t MergeCount(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct *new_value);

int32_t Max(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value);
inline int32_t MergeMax(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct *new_value) { return Max(mem_pool, cur_value, new_value); }

int32_t Min(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value);
inline int32_t MergeMin(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct *new_value) { return Min(mem_pool, cur_value, new_value); }

int32_t Sum(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value);
inline int32_t MergeSum(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct *new_value) { return Sum(mem_pool, cur_value, new_value); }

int32_t SquareSum(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value);

int32_t Var(MEM_POOL_PTR mem_pool, struct low_data_struct* cur_value,
		struct low_data_struct* new_value);

int32_t Avg(MEM_POOL_PTR mem_pool, struct low_data_struct* cur_value,
		struct low_data_struct* new_value);

int32_t CountDistinct(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value);

int32_t ComputeFunc(MEM_POOL_PTR mem_pool, enum function_type agr_type,
		struct low_data_struct *cur_value, struct low_data_struct* new_value);

int32_t MergeFunc(MEM_POOL_PTR mem_pool, enum function_type agr_type,
		struct low_data_struct *cur_value, struct low_data_struct *new_value);

// linear search, name is not case-sensitive.
// return function_type on success, -1 for not found
int func_name2type(const char *name);

// linear search, return lower case name.
// return function name, NULL for not found
const char *func_type2name(enum function_type type);

#endif /* AGGRFUNC_H_ */
