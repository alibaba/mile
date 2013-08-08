/*
 * OrderedRS.h
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#ifndef ORDEREDRS_H_
#define ORDEREDRS_H_

#include "../common/ResultSet.h"
#include "../common/MileArray.h"
#include "../common/Clonable.h"
#include "../common/Comparator.h"

class OrderedRS: public ResultSet {
private:
	uint32_t limit;
	Comparator* comp;
	Clonable* clone;
	MileArray* array;
	int8_t heaped;
	MEM_POOL_PTR mem_pool;
public:
	OrderedRS(Comparator* comp, Clonable* clone, uint32_t limit, MEM_POOL_PTR mem_pool);
	~OrderedRS();
	virtual int32_t AddResult(void* data);
	virtual MileIterator* CreateIterator();
	virtual uint32_t Size();
};

#endif /* ORDEREDRS_H_ */
