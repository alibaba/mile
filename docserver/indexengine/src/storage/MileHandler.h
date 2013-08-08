/*
 * MileHandler.h
 *
 *  Created on: 2012-10-15
 *      Author: yuzhong.zhao
 */

#ifndef MILEHANDLER_H_
#define MILEHANDLER_H_
#include "../common/def.h"

class MileHandler {
public:
	MileHandler();
	virtual ~MileHandler();
	virtual uint64_t GetHandlerId() = 0;
};

#endif /* MILEHANDLER_H_ */
