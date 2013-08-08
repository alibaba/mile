// LdbFilter.cpp : LdbFilter
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-11-05

#include "LdbFilter.h"
#include "LdbComparator.h"
#include "leveldb/slice.h"

static const int kBitsPerKey = 10; // suggest in filter_policy.h

LdbFilter::LdbFilter(const LdbComparator *comparetor) : ldb_comparator_(comparetor)
{
	bloom_filter_ = leveldb::NewBloomFilterPolicy(kBitsPerKey);
}

LdbFilter::~LdbFilter()
{
	delete bloom_filter_;
}

void LdbFilter::CreateFilter(const leveldb::Slice *keys, int n, std::string *dst) const
{
	char *buf = new char[sizeof(leveldb::Slice) * n];
	leveldb::Slice *new_keys = reinterpret_cast<leveldb::Slice *>(buf);
	for (int i = 0; i < n; i++)
		new (new_keys + i)leveldb::Slice(ldb_comparator_->FilterKey(keys[i]));
	bloom_filter_->CreateFilter(new_keys, n, dst);

	for (int i = 0; i < n; i++)
		(new_keys + i)->~Slice();

	delete []buf;
}

bool LdbFilter::KeyMayMatch(const leveldb::Slice &key, const leveldb::Slice &filter) const
{
	return bloom_filter_->KeyMayMatch(ldb_comparator_->FilterKey(key), filter);
}
