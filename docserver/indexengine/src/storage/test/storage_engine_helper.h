// storage_engine_helper.h : storage_engine_helper
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-16

#ifndef STORAGE_ENGINE_HELPER_H
#define STORAGE_ENGINE_HELPER_H

#include "../../protocol/packet.h"
#include "../../common/MileIterator.h"
#include "../../common/def.h"

#include <gtest/gtest.h>
#include <string>
#include <vector>

row_data *string2row(const char *sql, MEM_POOL_PTR mem);

select_fields_t *string2select_field(const char *sql, MEM_POOL_PTR mem);

select_field_array *string2func_select(const char *sql, MEM_POOL_PTR mem);

int row_count(MileIterator *iter);

class ConditionHelper
{
public:
	struct cond_desc_t {
		const char *field_name;
		enum condition_type cond_type;
		enum field_types field_type;
		enum compare_type compartor;;
		const char *value1;
		const char *value2;
	};
	explicit ConditionHelper(MEM_POOL_PTR mem) : mem_(mem) {};

	ConditionHelper &Add(const cond_desc_t &cond) { conds_.push_back(cond); return *this; }
	ConditionHelper &Add(const char *field_name, field_types field_type, compare_type comparator,
			const char *value1 = NULL, const char *value2 = NULL) {
		cond_desc_t desc = { field_name, CONDITION_EXP, field_type, comparator, value1, value2 };
		return Add(desc);
	}
	ConditionHelper &Add(condition_type cond_type) {
		cond_desc_t desc = { NULL, cond_type, (field_types)0, (compare_type)0, NULL, NULL};
		return Add(desc);
	}

	condition_array *ToCondArray() const
	{
		condition_array *array = (condition_array *)mem_pool_malloc(mem_, sizeof(condition_array));
		array->n = conds_.size();
		array->conditions = (condition_t *)mem_pool_malloc(mem_, sizeof(condition_t) * array->n);

		for (size_t i = 0; i < conds_.size(); i++) {
			const cond_desc_t &desc = conds_[i];
			condition_t &c = array->conditions[i];
			::memset(&c, 0, sizeof(c));
			c.type = desc.cond_type;
			if (c.type != CONDITION_EXP) {
				continue;
			}

			// comparator type
			c.comparator = desc.compartor;

			// field name
			c.field_name = (char *)mem_pool_malloc(mem_, ::strlen(desc.field_name) + 1);
			::strcpy(c.field_name, desc.field_name);

			// value number
			c.value_num = is_between_compare(c.comparator) ? 2 : 1;

			// values
			const char *va[] = { desc.value1, desc.value2 };
			c.values = (low_data_struct *)mem_pool_malloc(mem_, sizeof(low_data_struct) * c.value_num);
			for (uint32_t i = 0; i < c.value_num; i++) {
				low_data_struct &l = c.values[i];
				l.field_name = c.field_name;
				l.type = desc.field_type;
				if (l.type == HI_TYPE_STRING) {
					l.len = strlen(va[i]);
					l.data = mem_pool_malloc(mem_, l.len);
					::memcpy(l.data, va[i], l.len);
				} else { // treat as integer
					l.len = get_unit_size(l.type);
					l.data = mem_pool_malloc(mem_, l.len);
					int64_t v = atoll(va[i]);
					::memcpy(l.data, &v, l.len);
				}
			}
		}

		return array;
	}

	ConditionHelper &Clear() { conds_.clear(); return *this;}

private:
	MEM_POOL_PTR mem_;
	std::vector<cond_desc_t> conds_;
};


#endif // STORAGE_ENGINE_HELPER_H
