/*
 * Equals.h
 *
 *  Created on: 2012-8-17
 *      Author: yuzhong.zhao
 */

#ifndef EQUALS_H_
#define EQUALS_H_

class Equals {
public:
	virtual ~Equals() {};
	//if a = b, return 1; else return 0
	virtual int IsEqual(void* a, void* b) = 0;
};

#endif /* EQUALS_H_ */
