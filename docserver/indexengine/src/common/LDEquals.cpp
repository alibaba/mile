/*
 * LDEquals.cpp
 *
 *  Created on: 2012-8-30
 *      Author: yuzhong.zhao
 */

#include "LDEquals.h"

LDEquals::LDEquals() {
	// TODO Auto-generated constructor stub

}

LDEquals::~LDEquals() {
	// TODO Auto-generated destructor stub
}




int LDEquals::IsEqual(void* a, void* b){
	return is_ld_equal((struct low_data_struct*)a, (struct low_data_struct*)b);
}
