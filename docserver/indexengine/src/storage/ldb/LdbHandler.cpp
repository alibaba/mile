/*
 * LdbHandler.cpp
 *
 *  Created on: 2012-10-15
 *      Author: yuzhong.zhao
 */

#include "LdbHandler.h"

LdbHandler::LdbHandler() {
	memset(&key, 0, sizeof(struct low_data_struct));
	memset(&value, 0, sizeof(struct low_data_struct));
}

LdbHandler::~LdbHandler() {
	// TODO Auto-generated destructor stub
}



uint64_t LdbHandler::GetHandlerId(){
	return 0;
}
