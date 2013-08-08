/*
 * table_manager.h
 *
 *  Created on: 2012-7-30
 *      Author: yuzhong.zhao
 */

#ifndef TABLE_MANAGER_H_
#define TABLE_MANAGER_H_

#include "../common/def.h"
#include "../common/mem.h"
#include "../common/MileIterator.h"
#include "MileHandler.h"

class StorageEngine;

class TableManager {
public:
	TableManager(StorageEngine *se);
	virtual ~TableManager();

	virtual int InsertRow(MileHandler* &handler, struct row_data* rdata, MEM_POOL_PTR mem_pool) = 0;

	virtual int DeleteRow(MileHandler* handler, MEM_POOL_PTR mem_pool) = 0;

	virtual int UpdateRow(MileHandler* handler, struct row_data* new_data, MEM_POOL_PTR mem_pool) = 0;

	virtual struct select_row_t* QueryRow(MileHandler* handler, struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool) = 0;

	virtual void IdentifyQueryWay(struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool) = 0;

	// Get row's all data.
	virtual struct row_data *GetRowData(MileHandler *handler, MEM_POOL_PTR mem_pool) = 0;

	// NOTE: The returnd iterator is created by placement new with memory allocated from mem_pool
	// caller must call result->~MileIterator() when the result is no longer needed.
	virtual MileIterator *UseIndex(struct condition_array* cond, struct hint_array* hint, MEM_POOL_PTR mem_pool) = 0;

	// return total rou count.
	// return < 0 for error.
	virtual int64_t TotalRowCount(MEM_POOL_PTR mem_pool) = 0;

	int32_t getCutThreshold() ;
protected:
	friend class RecordIterator;

	virtual void Ref() = 0;
	virtual void UnRef() = 0;

private:
	StorageEngine *storage_engine_;

private:
	TableManager(const TableManager &);
	const TableManager &operator=(const TableManager &);
};

#endif /* TABLE_MANAGER_H_ */
