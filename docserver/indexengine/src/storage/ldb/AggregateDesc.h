// AggregateDesc.h : AggregateDesc
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-09-14

#ifndef AGGREGATEDESC_H
#define AGGREGATEDESC_H

#include "../../common/def.h"
#include "../../common/mem.h"

struct aggregate_desc_t {
	enum function_type func_type;
	const char *name; // field name
	low_data_struct value; // value

	/*
	 * only compare fun_type and name.
	 * reutrn :
	 * < 0 : this < r
	 * 0   : this == r
	 * > 0 : this > r
	 */
	int Compare(const aggregate_desc_t &r) const;
};

inline bool operator<(const aggregate_desc_t &l, const aggregate_desc_t &r) { return l.Compare(r) < 0; }
inline bool operator<=(const aggregate_desc_t &l, const aggregate_desc_t &r) { return l.Compare(r) <= 0; }
inline bool operator==(const aggregate_desc_t &l, const aggregate_desc_t &r) { return l.Compare(r) == 0; }
inline bool operator>(const aggregate_desc_t &l, const aggregate_desc_t &r) { return l.Compare(r) > 0; }
inline bool operator>=(const aggregate_desc_t &l, const aggregate_desc_t &r) { return l.Compare(r) >= 0; }

struct aggregate_desc_array_t {
	uint32_t size;
	aggregate_desc_t *data;

	aggregate_desc_t *Find(const aggregate_desc_t &v);

	low_data_struct ToLowData(MEM_POOL_PTR mem);
	int FromLowData(const low_data_struct &low, MEM_POOL_PTR mem);

	// merge other aggregate_desc_array_t's value if found, this->size never change.
	// this->data and other.data must be sorted
	int Merge(aggregate_desc_array_t &other, MEM_POOL_PTR mem);
};

#endif // AGGREGATEDESC_H
