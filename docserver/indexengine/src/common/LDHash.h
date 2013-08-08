/*
 * LDHash.h
 *
 *  Created on: 2012-8-30
 *      Author: yuzhong.zhao
 */

#ifndef LDHASH_H_
#define LDHASH_H_

#include "HashCoding.h"

class LDHash: public HashCoding {
public:
	LDHash();
	virtual ~LDHash();
	virtual uint32_t Coding(void* value);
};

#endif /* LDHASH_H_ */
