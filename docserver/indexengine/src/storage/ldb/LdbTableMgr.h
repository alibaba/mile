// LdbTableMgr.h : LdbTableMgr
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#ifndef LDBTABLEMGR_H
#define LDBTABLEMGR_H

#include "../TableManager.h"

class LdbTableImpl;
class LdbEngine;
struct select_field_array;

class LdbTableMgr : public TableManager
{
public:
	LdbTableMgr(LdbEngine *engine, LdbTableImpl *table) : TableManager((StorageEngine *)engine), table_(table) {}
	virtual ~LdbTableMgr();

	virtual int InsertRow(MileHandler* &handler, struct row_data* rdata, MEM_POOL_PTR mem_pool);

	virtual int DeleteRow(MileHandler* handler, MEM_POOL_PTR mem_pool);

	virtual int UpdateRow(MileHandler* handler, struct row_data* new_data, MEM_POOL_PTR mem_pool);

	virtual struct select_row_t* QueryRow(MileHandler* handler, struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool);

	virtual void IdentifyQueryWay(struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool);

	virtual struct row_data *GetRowData(MileHandler *handler, MEM_POOL_PTR mem_pool);

	// only and operator supported.
	// only row_key's equal condition and between condition (or equivalently: left_limit and right_limit) supported.
	virtual MileIterator *UseIndex(struct condition_array* cond, struct hint_array* hint, MEM_POOL_PTR mem_pool);

	// res: low_data_struct array, size == sel->n
	virtual int CumulativeQuery(low_data_struct *res, select_field_array *sel, condition_array* cond, MEM_POOL_PTR mem_pool);

	virtual bool HasCumulativeIndex();

	virtual int64_t TotalRowCount(MEM_POOL_PTR mem_pool) { return -1; }

protected:
	virtual void Ref() {}
	virtual void UnRef() {}

private:
	LdbTableImpl *table_;
};

#endif // LDBTABLEMGR_H
