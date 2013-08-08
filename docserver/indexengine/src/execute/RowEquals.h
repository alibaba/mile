/*
 * RowEquals.h
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#ifndef ROWEQUALS_H_
#define ROWEQUALS_H_

#include "../common/Equals.h"
#include "../common/def.h"
#include "../common/common_util.h"


class RowEquals: public Equals {
public:
	RowEquals();
	~RowEquals();
	virtual int IsEqual(void* a, void* b);
};

#endif /* ROWEQUALS_H_ */
