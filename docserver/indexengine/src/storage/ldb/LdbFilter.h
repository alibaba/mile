// LdbFilter.h : LdbFilter
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-11-05

#ifndef LDBFILTER_H
#define LDBFILTER_H

#include "leveldb/filter_policy.h"

class LdbComparator;

class LdbFilter : public leveldb::FilterPolicy {
public:
	explicit LdbFilter(const LdbComparator *comparetor);
	virtual ~LdbFilter();

	virtual const char *Name() const { return "ldbfilter"; }

	virtual void CreateFilter(const leveldb::Slice *keys, int n, std::string *dst) const;

	virtual bool KeyMayMatch(const leveldb::Slice &key, const leveldb::Slice &filter) const;

private:
	const FilterPolicy *bloom_filter_;
	const LdbComparator *ldb_comparator_;
};

#endif // LDBFILTER_H
