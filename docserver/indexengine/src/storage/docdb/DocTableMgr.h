// DocTableMgr.h : DocTableMgr
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#ifndef DOCTABLEMGR_H
#define DOCTABLEMGR_H

#include "../TableManager.h"
#include "../../common/mem.h"

class DocTableMgr : public TableManager
{
public:
	DocTableMgr(StorageEngine *engine, const char *table_name, MEM_POOL_PTR mem);
	~DocTableMgr();

	virtual int InsertRow(MileHandler* &handler, struct row_data *rdata, MEM_POOL_PTR mem_pool);

	virtual int DeleteRow(MileHandler* handler, MEM_POOL_PTR mem_pool);

	virtual int UpdateRow(MileHandler* handler, struct row_data *new_data, MEM_POOL_PTR mem_pool);

	virtual struct select_row_t *QueryRow(MileHandler* handler, struct select_fields_t *sel_fields, MEM_POOL_PTR mem_pool);

	virtual void IdentifyQueryWay(struct select_fields_t* sel_fields, MEM_POOL_PTR mem_pool);

	virtual struct row_data *GetRowData(MileHandler *handler, MEM_POOL_PTR mem_pool);

	virtual MileIterator *UseIndex(struct condition_array *cond, struct hint_array *hint, MEM_POOL_PTR mem_pool);

	virtual int64_t TotalRowCount(MEM_POOL_PTR mem_pool);

protected:
	virtual void Ref();
	virtual void UnRef();

private:

	// make a table_name copy because of db_xxx functions use char *table_name as parameter
	char *table_name_;
};

#endif // DOCTABLEMGR_H
