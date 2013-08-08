/*
 * DocHandler.h
 *
 *  Created on: 2012-10-15
 *      Author: yuzhong.zhao
 */

#ifndef DOCHANDLER_H_
#define DOCHANDLER_H_

#include "../../common/def.h"
#include "../MileHandler.h"

class DocHandler: public MileHandler {
public:
	uint64_t docid;
	double match_score;
	DocHandler();
	DocHandler(uint64_t docid);
	virtual ~DocHandler();
	virtual uint64_t GetHandlerId();
};

#endif /* DOCHANDLER_H_ */
