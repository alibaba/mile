// DocIterator.h : DocIterator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#ifndef DOCITERATOR_H
#define DOCITERATOR_H

#include "DocHandler.h"
#include "../RecordIterator.h"
#include "rowid_list.h"

struct segment_query_rowids;



class DocIterator : public RecordIterator
{
public:
	DocIterator(TableManager *table_mgr, char *table_name, MEM_POOL_PTR mem);
	~DocIterator();

	virtual void First();
	virtual void Next();

	virtual int8_t IsDone();

	virtual void *CurrentItem();

	// go to next nonempty segment, and set cur_pos_ to 0
	void SetSegmentList(list_head *seg_list, bool all_docid);

private:
	void NextSegment();
	void InternalNext();
	bool IsDeleted();

	uint64_t CurDocId();

private:
	// table name
	char *table_name_;

	// mem pool for current item store
	MEM_POOL_PTR mem_;

	// struct segment_query_rowids list
	list_head *seg_list_;

	// current segment
	segment_query_rowids *cur_seg_;
	// current rowid position or docid
	uint32_t cur_pos_;
	// current rowid node
	rowid_list_node *cur_node_;

	// empty condition, return all docid
	bool all_docid_;

	// storage for CurrentItem()
	uint64_t full_docid_;
};

#endif // DOCITERATOR_H
