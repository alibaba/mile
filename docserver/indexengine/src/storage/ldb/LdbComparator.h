// LdbComparator.h : LdbComparator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#ifndef LDBCOMPARATOR_H
#define LDBCOMPARATOR_H

#include "leveldb/comparator.h"
#include "leveldb/slice.h"

#include <stdint.h>
#include <string>

/* Key format:
 *
 *   | row key  | time field |
 *   |==========|============|
 *
 *   time field is fixed length of (time_len_)
 */

class LdbComparator : public leveldb::Comparator {
public:
	LdbComparator(size_t time_len, int time_scale, bool use_host_time, uint64_t expire_time);
	virtual ~LdbComparator(){}
 
	virtual int Compare(const leveldb::Slice &a, const leveldb::Slice &b) const;
	virtual const char *Name() const { return "ldb.comparator"; }

	virtual void FindShortestSeparator(std::string *start, const leveldb::Slice &limit) const;


	virtual void FindShortSuccessor(std::string *key) const;

	virtual bool Drop(const leveldb::Slice &key) const;

	// Get row key for filter
	virtual leveldb::Slice FilterKey(const leveldb::Slice &key) const {
		return leveldb::Slice(key.data(), key.size() - time_len_);
	}

private:
	// time field (uint32_t or uint64_t) length, 
	size_t time_len_;

	// time_field * 10^time_scale = time(NULL);
	// time_scale_value_ = 10^abs(time_scale) * time_scale/(abs(time_scale));
	int time_scale_value_;

	// no time field and time_len_ > 0, time field is filled by (uint32_t)time(NULL)
	bool use_host_time_;

	// expire time (second)
	uint64_t expire_time_;

};

#endif // LDBCOMPARATOR_H
