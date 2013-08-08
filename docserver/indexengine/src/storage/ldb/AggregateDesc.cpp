// AggregateDesc.cpp : AggregateDesc
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-09-14

#include "AggregateDesc.h"
#include "../../common/AggrFunc.h"
#include <algorithm>

template <typename T>
inline int cmp(const T &l, const T &r)
{
	return (l == r) ? 0 : (l < r ? -1 : 1);
}

int aggregate_desc_t::Compare(const aggregate_desc_t &r) const
{
	int rc = cmp((int)func_type, (int)r.func_type);
	if (rc)
		return rc;

	if (!name || !r.name)
		return cmp(name, r.name);

	return ::strcmp(name, r.name);
}

aggregate_desc_t *aggregate_desc_array_t::Find(const aggregate_desc_t &v)
{
	aggregate_desc_t *p = std::lower_bound(&data[0], &data[size], v);
	return (p != &data[size] && *p == v) ? p : NULL;
}

low_data_struct aggregate_desc_array_t::ToLowData(MEM_POOL_PTR mem)
{
	size_t len = sizeof(uint32_t); // size
	for (uint32_t i = 0; i < size; i++) {
		len += 1 // func_type
			+ 1 // name length
			+ strlen(data[i].name) // name
			+ 1 // value type
			+ sizeof(uint32_t) // value length
			+ data[i].value.len; // value
	}
	low_data_struct low = low_data_struct();
	low.len = len;
	low.data = mem_pool_malloc(mem, len);

	size_t off = 0;
	*(uint32_t *)((char *)low.data + off) = size;
	off += sizeof(uint32_t);

	for (uint32_t i = 0; i < size; i++) {
		*(uint8_t *)((char *)low.data + off++) = data[i].func_type;
		len = strlen(data[i].name);
		*(uint8_t *)((char *)low.data + off++) = len;
		::memcpy((char *)low.data + off, data[i].name, len);
		off += len;
		*(uint8_t *)((char *)low.data + off++) = data[i].value.type;
		*(uint32_t *)((char *)low.data + off) = data[i].value.len;
		off += sizeof(uint32_t);
		::memcpy((char *)low.data + off, data[i].value.data, data[i].value.len);
		off += data[i].value.len;
	}

	return low;
}

int aggregate_desc_array_t::FromLowData(const low_data_struct &low, MEM_POOL_PTR mem)
{
	const char *start = (const char *)low.data;
	const char *end = start + low.len;

	// size
	size = *(uint32_t *)start;
	start += sizeof(uint32_t);
	data = (aggregate_desc_t *)mem_pool_malloc(mem, sizeof(*data) * size);
	::memset(data, 0, sizeof(*data) * size);

	for (uint32_t i = 0; i < size; i++) {
		if (start >= end) return -1;

		// function type
		data[i].func_type = (enum function_type) *(uint8_t *)(start++);

		// name length
		size_t len = *(uint8_t *)(start++);
		// name
		char *name = (char *)mem_pool_malloc(mem, len + 1);
		::memcpy(name, start, len);
		name[len] = '\0';
		data[i].name = name;
		start += len;

		// value type
		data[i].value.type = (enum field_types) *(uint8_t *)(start++);

		// value length
		data[i].value.len = *(uint32_t *)start;
		start += sizeof(uint32_t);

		// value
		data[i].value.data = mem_pool_malloc(mem, data[i].value.len);
		::memcpy(data[i].value.data, start, data[i].value.len);
		start += data[i].value.len;
	}
	return start == end ? 0 : -1;
}

int aggregate_desc_array_t::Merge(aggregate_desc_array_t &other, MEM_POOL_PTR mem)
{
	if (!size || !other.size)
		return 0;

	aggregate_desc_t *p = std::lower_bound(&other.data[0], &other.data[other.size], data[0]);
	aggregate_desc_t *end = &other.data[other.size];

	for (uint32_t i = 0; i < size && p < end; i++) {
		while (p < end && *p < data[i]) p++;
		if (p >= end)
			break;

		if (*p == data[i]) {
			if (MergeFunc(mem, data[i].func_type, &data[i].value, &p->value) != 0) {
				return -1;
			}
		}
	}

	return 0;
}
