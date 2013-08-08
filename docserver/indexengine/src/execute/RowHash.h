/*
 * RowHash.h
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#ifndef ROWHASH_H_
#define ROWHASH_H_

#include "../common/HashCoding.h"
#include "../common/mem.h"
#include "../common/def.h"

class RowHash: public HashCoding {
public:
	RowHash();
	~RowHash();
	uint32_t Coding(void* value);
};

#endif /* ROWHASH_H_ */
