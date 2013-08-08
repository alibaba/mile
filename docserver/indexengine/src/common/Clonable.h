/*
 * Clonable.h
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#ifndef CLONABLE_H_
#define CLONABLE_H_

class Clonable {
public:
	virtual ~Clonable() {};
	virtual void* Clone(void* data) = 0;
};

#endif /* CLONABLE_H_ */
