/*
 * RowClone.h
 *
 *  Created on: 2012-8-23
 *      Author: yuzhong.zhao
 */

#ifndef ROWCLONE_H_
#define ROWCLONE_H_

#include "../common/Clonable.h"
#include "../common/def.h"
#include "../common/common_util.h"


class RowClone: public Clonable {
private:
	MEM_POOL_PTR mem_pool;
public:
	RowClone(MEM_POOL_PTR mem_pool);
	~RowClone();
	virtual void* Clone(void* data);
};

#endif /* ROWCLONE_H_ */
