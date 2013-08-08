/*
 * ResultSet.h
 *
 *  Created on: 2012-8-15
 *      Author: yuzhong.zhao
 */

#ifndef RESULTSET_H_
#define RESULTSET_H_

#include "def.h"
#include "list.h"
#include "MileIterator.h"


class ResultSet {
public:
	virtual ~ResultSet() {};
	virtual int32_t AddResult(void* data) = 0;
	virtual MileIterator* CreateIterator() = 0;
	virtual uint32_t Size() = 0;
};

#endif /* RESULTSET_H_ */
