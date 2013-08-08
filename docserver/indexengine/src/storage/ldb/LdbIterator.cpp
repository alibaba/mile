/// LdbIterator.cpp : LdbIterator
/// CopyRight (c) 2012 Vobile, Inc. All Rights Reserved.
/// Author: liu bin <liu_bin@vobile.cn>
/// Created: 2012-08-08

// FIXME: detect error and log it.

#include "LdbIterator.h"
#include "LdbComparator.h"

LdbIterator::LdbIterator(TableManager *table_mgr, const range_t &range,
		const leveldb::Comparator *comp, leveldb::Iterator *iter, MEM_POOL_PTR mem)
	: RecordIterator(table_mgr), mem_(mem), range_(range), comparetor_(comp), iter_(iter), valid_(false)
{
	ASSERT(NULL != comparetor_);
}

LdbIterator::~LdbIterator()
{
	if (NULL != iter_)
		delete iter_;
}

void LdbIterator::First()
{
	if (!range_.IsValid())
		return;

	ASSERT(range_.IsWhole());
	ASSERT(NULL != iter_);

	iter_->Seek(range_.start);
	if (!iter_->Valid()) // not found
		return;


	if (!range_.InclusiveStart() && comparetor_->Compare(iter_->key(), range_.start) == 0){
		iter_->Next();
		if (!iter_->Valid())
			return;
	}

	int rc = comparetor_->Compare(iter_->key(), range_.end);
	if (rc > 0 || (rc == 0 && !range_.InclusiveEnd()))
		return;

	valid_ = true;
}

void LdbIterator::Next()
{
	if (!valid_)
		return;

	iter_->Next();
	if (!iter_->Valid()) {
		valid_ = false;
		return;
	}

	int rc = comparetor_->Compare(iter_->key(), range_.end);
	if (rc > 0 || (rc == 0 && !range_.InclusiveEnd())) {
		valid_ = false;
		return;
	}
}

void *LdbIterator::CurrentItem()
{
	if (!valid_)
		return NULL;

	LdbHandler *handler = NEW(mem_, LdbHandler)();
	leveldb::Slice kvarray[] = { iter_->key(), iter_->value() };

	// make a copy of key and value
	struct low_data_struct *dest[] = { &handler->key, &handler->value };
	for (size_t i = 0; i < sizeof(dest)/sizeof(low_data_struct *); i++) {
		dest[i]->len = kvarray[i].size();
		dest[i]->data = mem_pool_malloc(mem_, dest[i]->len);
		::memcpy(dest[i]->data, kvarray[i].data(), dest[i]->len);
	}

	return handler;
}

