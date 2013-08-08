/*
 * HashCoding.h
 *
 *  Created on: 2012-8-21
 *      Author: yuzhong.zhao
 */

#ifndef HASHCODING_H_
#define HASHCODING_H_

#include "def.h"


class HashCoding {
public:
	virtual ~HashCoding() {};
	virtual uint32_t Coding(void* value) = 0;
};

#endif /* HASHCODING_H_ */
