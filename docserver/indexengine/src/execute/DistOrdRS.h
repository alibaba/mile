/*
 * DistOrdRS.h
 *
 *  Created on: 2012-8-21
 *      Author: yuzhong.zhao
 */

#ifndef DISTORDRS_H_
#define DISTORDRS_H_

#include "../common/ResultSet.h"
#include "../common/HashCoding.h"
#include "../common/Equals.h"
#include "../common/Clonable.h"
#include "../common/Comparator.h"
#include "../common/HashSet.h"
#include "../common/MileArray.h"

class DistOrdRS: public ResultSet {
private:
	uint32_t limit;
	HashSet* set;
	Clonable* clone;
	Comparator* comp;
	MileArray* array;
	int8_t heaped;
	MEM_POOL_PTR mem_pool;
public:
	DistOrdRS(Equals* equals, HashCoding* hash, Comparator* comp, Clonable* clone, uint32_t limit, MEM_POOL_PTR mem_pool);
	~DistOrdRS();
	virtual int32_t AddResult(void* data);
	virtual MileIterator* CreateIterator();
	virtual uint32_t Size();
};

#endif /* DISTORDRS_H_ */
