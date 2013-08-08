/*
 * SelectSubstep.cpp
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#include "SelectSubstep.h"

SelectSubstep::SelectSubstep(struct select_fields_t* sel_fields){
	this->sel_fields = sel_fields;
}



SelectSubstep::~SelectSubstep(){

}



int32_t SelectSubstep::Execute(TableManager* table, MileHandler* handler, void* output, MEM_POOL_PTR mem_pool){
	ResultSet* result = (ResultSet*) output;
	struct select_row_t* row;
	row = table->QueryRow(handler, sel_fields, mem_pool);
	if(NULL == row){
		return ERROR_QUERY_BY_ROWID;
	}else{
		row->handler = handler;
		return result->AddResult(row);
	}
}
