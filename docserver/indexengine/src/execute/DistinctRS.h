/*
 * DistinctRS.h
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#ifndef DISTINCTRS_H_
#define DISTINCTRS_H_

#include "../common/ResultSet.h"
#include "../common/HashSet.h"
#include "../common/HashCoding.h"
#include "../common/Equals.h"
#include "../common/Clonable.h"

class DistinctRS: public ResultSet {
private:
	uint32_t limit;
	uint32_t num;
	Clonable* clone;
	HashSet* set;
	MEM_POOL_PTR mem_pool;
public:
	DistinctRS(Equals* equals, HashCoding* hash, Clonable* clone, uint32_t limit, MEM_POOL_PTR mem_pool);
	~DistinctRS();
	virtual int32_t AddResult(void* data);
	virtual MileIterator* CreateIterator();
	virtual uint32_t Size();
};

#endif /* DISTINCTRS_H_ */
