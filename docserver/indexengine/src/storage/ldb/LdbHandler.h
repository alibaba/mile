/*
 * LdbHandler.h
 *
 *  Created on: 2012-10-15
 *      Author: yuzhong.zhao
 */

#ifndef LDBHANDLER_H_
#define LDBHANDLER_H_

#include "../MileHandler.h"

class LdbHandler: public MileHandler {
public:
	struct low_data_struct key;
	struct low_data_struct value;
	LdbHandler();
	virtual ~LdbHandler();
	virtual uint64_t GetHandlerId();
};

#endif /* LDBHANDLER_H_ */
