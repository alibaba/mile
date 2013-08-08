/*
 * Comparator.h
 *
 *  Created on: 2012-8-16
 *      Author: yuzhong.zhao
 */

#ifndef COMPARATOR_H_
#define COMPARATOR_H_

class Comparator {
public:
	virtual ~Comparator() {};
	//if a < b, return -1; if a = b, return 0; if a > b, return 1
	virtual int Compare(void* a, void* b) = 0;
};

#endif /* COMPARATOR_H_ */
