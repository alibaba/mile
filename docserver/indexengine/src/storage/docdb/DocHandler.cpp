/*
 * DocHandler.cpp
 *
 *  Created on: 2012-10-15
 *      Author: yuzhong.zhao
 */

#include "DocHandler.h"

DocHandler::DocHandler() {
	this->docid = 0;
	this->match_score = 0;
}

DocHandler::DocHandler(uint64_t docid){
	this->docid = docid;
	this->match_score = 0;
}


DocHandler::~DocHandler() {

}


uint64_t DocHandler::GetHandlerId(){
	return docid;
}

