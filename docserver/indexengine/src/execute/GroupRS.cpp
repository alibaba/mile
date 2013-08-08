/*
 * GroupRS.cpp
 *
 *  Created on: 2012-8-26
 *      Author: yuzhong.zhao
 */

#include "GroupRS.h"

GroupRS::GroupRS(struct select_field_array* sel_array,
		struct group_field_array* group_array, MEM_POOL_PTR mem_pool) {
	uint32_t i, j, k;

	this->mem_pool = mem_pool;
	src_fields = init_select_fields_t(mem_pool, 1000);
	sel_fields = init_select_fields_t(mem_pool, sel_array->n);
	agr_n = 0;
	grp_n = 0;
	grp_index = (uint32_t*) mem_pool_malloc(mem_pool,
			sizeof(uint32_t) * sel_array->n);
	memset(grp_index, 0, sizeof(uint32_t) * sel_array->n);
	agr_func_types = (enum function_type*) mem_pool_malloc(mem_pool,
			sizeof(uint32_t) * sel_array->n);
	memset(agr_func_types, 0, sizeof(uint32_t) * sel_array->n);
	agr_index = (uint32_t*) mem_pool_malloc(mem_pool,
			sizeof(uint32_t) * sel_array->n);
	memset(agr_index, 0, sizeof(uint32_t) * sel_array->n);
	agr_ref_index = (uint32_t*) mem_pool_malloc(mem_pool,
			sizeof(uint32_t) * sel_array->n);
	memset(agr_ref_index, 0, sizeof(uint32_t) * sel_array->n);
	agr_range_exp = (Expression**) mem_pool_malloc(mem_pool,
			sizeof(Expression*) * sel_array->n);
	memset(agr_range_exp, 0, sizeof(Expression*) * sel_array->n);

	// add the group fields into the source fields
	grp_n = group_array->n;
	src_fields->n = 0;
	for (i = 0; i < group_array->n; i++) {
		src_fields->fields_name[src_fields->n] =
				group_array->group_fields[i].field_name;
		src_fields->select_type[src_fields->n] = SELECT_TYPE_HASH;
		src_fields->n++;
		for (j = 0; j < sel_array->n; j++) {
			if (sel_array->select_fields[j].type != FUNCTION_SELECT) {
				if (strcmp(group_array->group_fields[i].field_name,
						sel_array->select_fields[j].field_name) == 0) {
					grp_index[i] = j;
					break;
				}
			}
		}
	}

	// add the reference fields into the source fields
	for (i = 0; i < sel_array->n; i++) {
		sel_fields->fields_name[i] = sel_array->select_fields[i].field_name;

		if (sel_array->select_fields[i].type == FUNCTION_SELECT) {
			agr_func_types[agr_n] = sel_array->select_fields[i].func_type;
			agr_index[agr_n] = i;
			if (agr_func_types[agr_n] != FUNC_COUNT) {
				for (j = 0; j < src_fields->n; j++) {
					if (strcmp(src_fields->fields_name[j],
							sel_array->select_fields[i].field_name) == 0) {
						break;
					}
				}

				if (j == src_fields->n) {
					src_fields->fields_name[src_fields->n] =
							sel_array->select_fields[i].field_name;
					if(agr_func_types[agr_n] != FUNC_COUNT_DISTINCT){
					    src_fields->select_type[src_fields->n] =
					        SELECT_TYPE_ORIGINAL;
					}else{
					    src_fields->select_type[src_fields->n] = SELECT_TYPE_HASH;
					}
					src_fields->n++;
				}
				agr_ref_index[agr_n] = j;
			} else {
				agr_ref_index[agr_n] = 0;
			}

			if (sel_array->select_fields[i].range_exp != NULL) {
				agr_range_exp[agr_n] = Expression::GenExp(
						sel_array->select_fields[i].range_exp, mem_pool);

				// bad implement, need to optimize
				for (j = 0; j < agr_range_exp[agr_n]->exp_fields->n; j++) {
					for (k = 0; k < src_fields->n; k++) {
						if (strcmp(src_fields->fields_name[k],
								agr_range_exp[agr_n]->exp_fields->fields_name[j])
								== 0) {
							break;
						}
					}
					if (k < src_fields->n) {
						if (agr_range_exp[agr_n]->exp_fields->select_type[j]
								== SELECT_TYPE_ORIGINAL) {
							src_fields->select_type[k] = SELECT_TYPE_ORIGINAL;
						}
					} else {
						src_fields->fields_name[src_fields->n] =
								agr_range_exp[agr_n]->exp_fields->fields_name[j];
						src_fields->select_type[src_fields->n] =
								agr_range_exp[agr_n]->exp_fields->select_type[j];
						src_fields->n++;
					}
				}

			} else {
				agr_range_exp[agr_n] = NULL;
			}

			agr_n++;
		}
	}

	Equals* equals =
			new (mem_pool_malloc(mem_pool, sizeof(RowEquals))) RowEquals();
	HashCoding* hash =
			new (mem_pool_malloc(mem_pool, sizeof(RowHash))) RowHash();
	map = new (mem_pool_malloc(mem_pool, sizeof(MileHashMap))) MileHashMap(
			equals, hash, mem_pool);
}

GroupRS::~GroupRS() {

}

int32_t GroupRS::AddResult(void* data) {
	int32_t i;
	int32_t result_code;
	struct select_row_t* src_row;
	struct select_row_t* grp_row;
	struct select_row_t* sel_row;

	src_row = (struct select_row_t*) data;
	grp_row = init_select_row_t(mem_pool, grp_n);
	grp_row->handler = src_row->handler;
	for (i = 0; i < grp_n; i++) {
		grp_row->select_type[i] = src_row->select_type[grp_index[i]];
		copy_low_data_struct(mem_pool, &grp_row->data[i], &src_row->data[i]);
	}

	sel_row = (struct select_row_t*) map->Get(grp_row);
	if (sel_row == NULL) {
		sel_row = init_select_row_t(mem_pool, sel_fields->n);
		sel_row->handler = src_row->handler;
		for (i = 0; i < sel_fields->n; i++) {
			sel_row->select_type[i] = SELECT_TYPE_ORIGINAL;
		}
		for (i = 0; i < grp_n; i++) {
			copy_low_data_struct(mem_pool, &sel_row->data[grp_index[i]],
					&src_row->data[i]);
			sel_row->select_type[grp_index[i]] = src_row->select_type[i];
		}

		map->Put(grp_row, sel_row);
	}

	for (i = 0; i < agr_n; i++) {
		if (agr_range_exp[i] != NULL) {
			result_code = agr_range_exp[i]->Execute(src_row);
			if (result_code < 0) {
				return result_code;
			}
			if (result_code == 0) {
				continue;
			}
		}

		result_code = ComputeFunc(mem_pool, agr_func_types[i],
				&sel_row->data[agr_index[i]],
				&(src_row->data[agr_ref_index[i]]));

		if (result_code < 0) {
			return result_code;
		}
	}

	return MILE_RETURN_SUCCESS;
}

MileIterator* GroupRS::CreateIterator() {
	return map->CreateValueIterator();
}

uint32_t GroupRS::Size() {
	return map->Size();
}

