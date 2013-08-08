/*
 * OrderComparator.h
 *
 *  Created on: 2012-8-16
 *      Author: yuzhong.zhao
 */

#ifndef ORDERCOMPARATOR_H_
#define ORDERCOMPARATOR_H_

#include "../common/Comparator.h"
#include "../common/def.h"
#include "../common/common_util.h"
#include "../protocol/packet.h"

class OrderComparator: public Comparator {
private:
	uint32_t n;
	uint32_t* ord_index;
	enum order_types* ord_type;
public:
	OrderComparator(struct select_field_array* select_field,
			struct order_field_array* order_array, MEM_POOL_PTR mem_pool);
	~OrderComparator();
	virtual int Compare(void* a, void* b);
};

#endif /* ORDERCOMPARATOR_H_ */
