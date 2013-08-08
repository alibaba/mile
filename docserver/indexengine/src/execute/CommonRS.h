/*
 * CommonRS.h
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#ifndef COMMONRS_H_
#define COMMONRS_H_

#include "../common/ResultSet.h"
#include "../common/MileArray.h"
#include "../common/Clonable.h"

class CommonRS: public ResultSet {
private:
	uint32_t limit;
	MileArray* array;
	Clonable* clone;
	MEM_POOL_PTR mem_pool;
public:
	CommonRS(uint32_t limit, Clonable* clone, MEM_POOL_PTR mem_pool);
	~CommonRS();
	virtual int32_t AddResult(void* data);
	virtual MileIterator* CreateIterator();
	virtual uint32_t Size();
};

#endif /* COMMONRS_H_ */
