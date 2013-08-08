/*
 * AggrFunc.cpp
 *
 *  Created on: 2012-9-12
 *      Author: yuzhong.zhao
 */

#include "AggrFunc.h"
#include <utility>

typedef std::pair<enum function_type, const char *> func_type_name_t;
const func_type_name_t g_func_type_names[] = { std::make_pair(FUNC_COUNT,
		"count"), std::make_pair(FUNC_SUM, "sum"), std::make_pair(FUNC_MAX,
		"max"), std::make_pair(FUNC_MIN, "min"), std::make_pair(FUNC_SQUARE_SUM,
		"squaresum"), std::make_pair(FUNC_AVG, "avg"), std::make_pair(FUNC_VAR,
		"variance"), std::make_pair(FUNC_STD, "stddev") };

int32_t Count(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value) {
	if (cur_value == NULL) {
		return -1;
	}
	if (cur_value->len == 0) {
		cur_value->len = sizeof(int64_t);
		cur_value->type = HI_TYPE_LONGLONG;
		cur_value->data = mem_pool_malloc(mem_pool, cur_value->len);
		*(int64_t*) (cur_value->data) = 1;
	} else {
		*(int64_t*) (cur_value->data) += 1;
	}
	return MILE_RETURN_SUCCESS;
}

int32_t MergeCount(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct *new_value) {
	if (!cur_value)
		return -1;

	if (cur_value->len == 0) {
		cur_value->len = sizeof(int64_t);
		cur_value->type = HI_TYPE_LONGLONG;
		cur_value->data = mem_pool_malloc(mem_pool, cur_value->len);
		*(int64_t*) (cur_value->data) = 0;
	}

	if (new_value && new_value->len > 0)
		*((uint64_t*) cur_value->data) += *(uint64_t *) new_value->data;
	return 0;
}

int32_t CountDistinct(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value) {

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (cur_value->data == NULL) {
		cur_value->type = HI_TYPE_SET;
		cur_value->len = 0;
		Equals * equals =
				new (mem_pool_malloc(mem_pool, sizeof(LDEquals))) LDEquals();
		HashCoding* hash =
				new (mem_pool_malloc(mem_pool, sizeof(LDHash))) LDHash();
		cur_value->data =
				new (mem_pool_malloc(mem_pool, sizeof(HashSet))) HashSet(equals,
						hash, mem_pool);
	}

	HashSet* set = (HashSet*) cur_value->data;
	if (set->Contains(new_value)) {
		return MILE_RETURN_SUCCESS;
	} else if (new_value->type != HI_TYPE_NULL) {
		struct low_data_struct* ld = (struct low_data_struct*) mem_pool_malloc(
				mem_pool, sizeof(struct low_data_struct));
		memset(ld, 0, sizeof(struct low_data_struct));
		copy_low_data_struct(mem_pool, ld, new_value);
		set->Add(ld);
		cur_value->len++;
	}

	return MILE_RETURN_SUCCESS;
}

int32_t Max(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value) {
	int32_t ret = 0;

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (cur_value->type == HI_TYPE_NOTCOMP) {
		;
	} else if (cur_value->type == HI_TYPE_NULL || cur_value->len == 0) {
		copy_low_data_struct(mem_pool, cur_value, new_value);
	} else if (new_value->type == HI_TYPE_NULL || new_value->len == 0) {
		;
	} else {
		ret = compare_ld(cur_value, new_value);
		if (ret == -1) {
			copy_low_data_struct(mem_pool, cur_value, new_value);
		}

		if (ret == -2) {
			cur_value->type = HI_TYPE_NOTCOMP;
			cur_value->len = 0;
		}
	}
	return MILE_RETURN_SUCCESS;
}

int32_t Min(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value) {
	int32_t ret = 0;

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (cur_value->type == HI_TYPE_NOTCOMP) {
		;
	} else if (cur_value->type == HI_TYPE_NULL || cur_value->len == 0) {
		copy_low_data_struct(mem_pool, cur_value, new_value);
	} else if (new_value->type == HI_TYPE_NULL || new_value->len == 0) {
		;
	} else {
		ret = compare_ld(cur_value, new_value);
		if (ret == 1) {
			copy_low_data_struct(mem_pool, cur_value, new_value);
		}

		if (ret == -2) {
			cur_value->type = HI_TYPE_NOTCOMP;
			cur_value->len = 0;
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t Sum(MEM_POOL_PTR mem_pool, struct low_data_struct *cur_value,
		struct low_data_struct* new_value) {
	double a, b;

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (new_value->type != HI_TYPE_NULL && new_value->len != 0) {
		if (cur_value->type == HI_TYPE_NULL || cur_value->len == 0) {
			cur_value->type = HI_TYPE_DOUBLE;
			cur_value->len = sizeof(double);
			cur_value->data = mem_pool_malloc(mem_pool, sizeof(double));
			*(double*) cur_value->data = 0;
		}

		a = ld_to_double(new_value);
		b = *(double*) cur_value->data;
		if (isnan(a)) {
			*(double*) cur_value->data = a;
			if (new_value->field_name != NULL) {
				log_error("%s列的类型不能转换为double, 无法进行sum操作", new_value->field_name);
			}
		} else if (!isnan(b)) {
			*(double*) cur_value->data = *(double*) cur_value->data
					+ ld_to_double(new_value);
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t SquareSum(MEM_POOL_PTR mem_pool, struct low_data_struct* cur_value,
		struct low_data_struct* new_value) {
	double a, b;

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (new_value->type != HI_TYPE_NULL && new_value->len != 0) {
		if (cur_value->type == HI_TYPE_NULL || cur_value->len == 0) {
			cur_value->type = HI_TYPE_DOUBLE;
			cur_value->len = sizeof(double);
			cur_value->data = mem_pool_malloc(mem_pool, sizeof(double));
			*(double*) cur_value->data = 0;
		}

		a = ld_to_double(new_value);
		b = *(double*) cur_value->data;
		if (isnan(a)) {
			*(double*) cur_value->data = a;
			if (new_value->field_name != NULL) {
				log_error("%s列的类型不能转换为double, 无法进行squaresum操作", new_value->field_name);
			}
		} else if (!isnan(b)) {
			*(double*) cur_value->data = b + a * a;
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t Avg(MEM_POOL_PTR mem_pool, struct low_data_struct* cur_value,
		struct low_data_struct* new_value) {
	struct low_data_struct *sum, *count;
	MileArray* array;
	double a, b;

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (new_value->type != HI_TYPE_NULL && new_value->len > 0) {
		if (cur_value->data == NULL) {
			array =
					new (mem_pool_malloc(mem_pool, sizeof(MileArray))) MileArray(
							mem_pool);
			cur_value->type = HI_TYPE_ARRAY;
			cur_value->len = 2;
			cur_value->data = array;
			//initial the sum
			sum = (struct low_data_struct*) mem_pool_malloc(mem_pool,
					sizeof(struct low_data_struct));
			memset(sum, 0, sizeof(struct low_data_struct));
			sum->type = HI_TYPE_DOUBLE;
			sum->len = sizeof(double);
			sum->data = mem_pool_malloc(mem_pool, sizeof(double));
			*(double*) sum->data = 0;
			array->Add(sum);

			//initial the count
			count = (struct low_data_struct*) mem_pool_malloc(mem_pool,
					sizeof(struct low_data_struct));
			memset(count, 0, sizeof(struct low_data_struct));
			count->type = HI_TYPE_LONGLONG;
			count->len = sizeof(uint64_t);
			count->data = mem_pool_malloc(mem_pool, sizeof(uint64_t));
			*(uint64_t*) count->data = 0;
			array->Add(count);
		}

		array = (MileArray*) cur_value->data;
		sum = (struct low_data_struct*) array->Get(0);
		count = (struct low_data_struct*) array->Get(1);
		*(uint64_t*) count->data = *(uint64_t*) count->data + 1;

		a = ld_to_double(new_value);
		b = *(double*) sum->data;
		if (isnan(a)) {
			*(double*) sum->data = a;
			if (new_value->field_name != NULL) {
				log_error("%s列的类型不能转换为double, 无法进行avg操作", new_value->field_name);
			}
		} else if (!isnan(b)) {
			*(double*) sum->data = *(double*) sum->data + a;
		}

	}

	return MILE_RETURN_SUCCESS;
}

int32_t Var(MEM_POOL_PTR mem_pool, struct low_data_struct* cur_value,
		struct low_data_struct* new_value) {
	struct low_data_struct *square_sum, *sum, *count;
	MileArray* array;
	double a, b;

	if (cur_value == NULL || new_value == NULL) {
		return -1;
	}

	if (new_value->type != HI_TYPE_NULL && new_value->len > 0) {
		if (cur_value->data == NULL) {
			array =
					new (mem_pool_malloc(mem_pool, sizeof(MileArray))) MileArray(
							mem_pool);
			cur_value->type = HI_TYPE_ARRAY;
			cur_value->len = 3;
			cur_value->data = array;

			//initial the square sum
			square_sum = (struct low_data_struct*) mem_pool_malloc(mem_pool,
					sizeof(struct low_data_struct));
			memset(square_sum, 0, sizeof(struct low_data_struct));
			square_sum->type = HI_TYPE_DOUBLE;
			square_sum->len = sizeof(double);
			square_sum->data = mem_pool_malloc(mem_pool, sizeof(double));
			*(double*) square_sum->data = 0;
			array->Add(square_sum);

			//initial the sum
			sum = (struct low_data_struct*) mem_pool_malloc(mem_pool,
					sizeof(struct low_data_struct));
			memset(sum, 0, sizeof(struct low_data_struct));
			sum->type = HI_TYPE_DOUBLE;
			sum->len = sizeof(double);
			sum->data = mem_pool_malloc(mem_pool, sizeof(double));
			*(double*) sum->data = 0;
			array->Add(sum);

			//initial the count
			count = (struct low_data_struct*) mem_pool_malloc(mem_pool,
					sizeof(struct low_data_struct));
			memset(count, 0, sizeof(struct low_data_struct));
			count->type = HI_TYPE_LONGLONG;
			count->len = sizeof(uint64_t);
			count->data = mem_pool_malloc(mem_pool, sizeof(uint64_t));
			*(uint64_t*) count->data = 0;
			array->Add(count);
		}

		array = (MileArray*) cur_value->data;
		square_sum = (struct low_data_struct*) array->Get(0);
		sum = (struct low_data_struct*) array->Get(1);
		count = (struct low_data_struct*) array->Get(2);

		*(uint64_t*) count->data = *(uint64_t*) count->data + 1;

		a = ld_to_double(new_value);
		b = *(double*) sum->data;
		if (isnan(a)) {
			*(double*) sum->data = a;
			*(double*) square_sum->data = a;
			if (new_value->field_name != NULL) {
				log_error("%s列的类型不能转换为double, 无法进行var操作", new_value->field_name);
			}
		} else if (!isnan(b)) {
			*(double*) square_sum->data = (*(double*) square_sum->data + a * a);
			*(double*) sum->data = (*(double*) sum->data) + a;
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t ComputeFunc(MEM_POOL_PTR mem_pool, enum function_type agr_type,
		struct low_data_struct *cur_value, struct low_data_struct* new_value) {
	int32_t result_code = MILE_RETURN_SUCCESS;

	switch (agr_type) {
	case FUNC_MIN:
		result_code = Min(mem_pool, cur_value, new_value);
		break;
	case FUNC_MAX:
		result_code = Max(mem_pool, cur_value, new_value);
		break;
	case FUNC_SUM:
		result_code = Sum(mem_pool, cur_value, new_value);
		break;
	case FUNC_COUNT:
		result_code = Count(mem_pool, cur_value, new_value);
		break;
	case FUNC_AVG:
		result_code = Avg(mem_pool, cur_value, new_value);
		break;
	case FUNC_VAR:
		result_code = Var(mem_pool, cur_value, new_value);
		break;
	case FUNC_STD:
		result_code = Var(mem_pool, cur_value, new_value);
		break;
	case FUNC_SQUARE_SUM:
		result_code = SquareSum(mem_pool, cur_value, new_value);
		break;
	case FUNC_COUNT_DISTINCT:
		result_code = CountDistinct(mem_pool, cur_value, new_value);
		break;
	default:
		return ERROR_UNSUPPORTED_AGRFUNC_TYPE;
	}
	return result_code;
}

int32_t MergeFunc(MEM_POOL_PTR mem_pool, enum function_type agr_type,
		struct low_data_struct *cur_value, struct low_data_struct *new_value) {
	switch (agr_type) {
	case FUNC_MIN:
		return MergeMin(mem_pool, cur_value, new_value);
	case FUNC_MAX:
		return MergeMax(mem_pool, cur_value, new_value);
	case FUNC_COUNT:
		return MergeCount(mem_pool, cur_value, new_value);
	case FUNC_SUM:
		return MergeSum(mem_pool, cur_value, new_value);
	default:
		log_error("func type %d not supported for merge.", agr_type);
		return ERROR_UNSUPPORTED_AGRFUNC_TYPE;
	}
}

int func_name2type(const char *name) {
	for (size_t i = 0; sizeof(g_func_type_names) / sizeof(g_func_type_names[0]);
			i++)
		if (strcasecmp(g_func_type_names[i].second, name) == 0)
			return g_func_type_names[i].first;

	return -1;

}

const char *func_type2name(enum function_type type) {
	for (size_t i = 0; sizeof(g_func_type_names) / sizeof(g_func_type_names[0]);
			i++)
		if (g_func_type_names[i].first == type)
			return g_func_type_names[i].second;
	return NULL;
}
