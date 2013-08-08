// DocIterator.cpp : DocIterator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#include "DocIterator.h"
#include "db.h"
#include "table.h"

DocIterator::DocIterator(TableManager *table_mgr, char *table_name,
		MEM_POOL_PTR mem) :
		RecordIterator(table_mgr), table_name_(table_name), mem_(mem), seg_list_(
				NULL), cur_seg_(NULL), cur_pos_(0), cur_node_(NULL), all_docid_(
				false) {
}

DocIterator::~DocIterator() {
}

void DocIterator::SetSegmentList(list_head *seg_list, bool all_docid) {
	seg_list_ = seg_list;
	all_docid_ = all_docid;
}

void DocIterator::NextSegment() {
	if (NULL == seg_list_ || list_empty(seg_list_)) {
		cur_seg_ = NULL;
		return;
	}

	list_head *l = seg_list_;
	if (cur_seg_ != NULL)
		l = &cur_seg_->rowids_list;

	for (l = l->next; l != seg_list_; l = l->next) {
		cur_seg_ = list_entry(l, segment_query_rowids, rowids_list);
		if (all_docid_) {
			if (cur_seg_->max_docid > 0)
				break;
		} else {
			if (NULL != cur_seg_->rowids && cur_seg_->rowids->rowid_num > 0) {
				cur_node_ = cur_seg_->rowids->head;
				break;
			}
		}
	}

	cur_pos_ = 0;
	if (l == seg_list_)
		cur_seg_ = NULL;
}

int8_t DocIterator::IsDone() {
	if (NULL != seg_list_ && cur_seg_ != NULL)
		return false;
	return true;
}

void DocIterator::First() {
	cur_seg_ = NULL;
	NextSegment();

	while (!IsDone() && IsDeleted())
		InternalNext();

}

void DocIterator::Next() {
	do {
		InternalNext();
	} while (!IsDone() && IsDeleted());
}

void DocIterator::InternalNext() {
	if (IsDone())
		return;

	++cur_pos_;
	if (all_docid_) {
		if (cur_pos_ >= cur_seg_->max_docid)
			NextSegment();
	} else {
		if (cur_pos_ >= cur_seg_->rowids->rowid_num)
			NextSegment();
		else if (cur_pos_ % ROWID_ARRAY_SIZE == 0) {
			cur_node_ = cur_node_->next;
		}
	}
}



void *DocIterator::CurrentItem() {
	assert(!IsDone());

	DocHandler* doc_handler = NEW(mem_, DocHandler)();
	if (all_docid_) {
		doc_handler->docid = ((uint64_t) cur_seg_->sid << 32) + cur_pos_;
		doc_handler->match_score = 0;
	} else {
		doc_handler->docid = ((uint64_t) cur_seg_->sid << 32)
				+ cur_node_->rowid_array[cur_pos_ % ROWID_ARRAY_SIZE];
		if (NULL == cur_node_->score_array) {
			doc_handler->match_score = 0;
		} else {
			doc_handler->match_score = cur_node_->score_array[cur_pos_
					% ROWID_ARRAY_SIZE];
		}
	}

	return doc_handler;
}

bool DocIterator::IsDeleted() {
	DocHandler* doc_handler = (DocHandler*) CurrentItem();
	uint64_t docid = doc_handler->docid;
	return db_is_docid_deleted(table_name_, docid >> 32, (uint32_t) docid);
}
