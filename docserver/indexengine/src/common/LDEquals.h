/*
 * LDEquals.h
 *
 *  Created on: 2012-8-30
 *      Author: yuzhong.zhao
 */

#ifndef LDEQUALS_H_
#define LDEQUALS_H_

#include "Equals.h"
#include "def.h"
#include "common_util.h"

class LDEquals: public Equals {
public:
	LDEquals();
	virtual ~LDEquals();
	virtual int IsEqual(void* a, void* b);
};

#endif /* LDEQUALS_H_ */
