// LdbComparator.cpp : LdbComparator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-07

#include "LdbComparator.h"
#include "leveldb/slice.h"
#include "../../common/log.h"

#include <time.h>
#include <stdint.h>
#include <stdlib.h>

LdbComparator::LdbComparator(size_t time_len, int time_scale, bool use_host_time, uint64_t expire_time)
	: time_len_(time_len), use_host_time_(use_host_time), expire_time_(expire_time)
{
	time_scale_value_ = 1;
	if (time_scale < 0) {
		time_scale_value_ = -1;
		time_scale = -time_scale;
	}

	for (int i = 0; i < time_scale; i++)
		time_scale_value_ *= 10;
}

int LdbComparator::Compare(const leveldb::Slice &a, const leveldb::Slice &b) const
{
	ASSERT(a.size() >= time_len_ && b.size() >= time_len_);
	size_t min = std::min(a.size(), b.size()) - time_len_; // skip time field
	int rc = ::memcmp(a.data(), b.data(), min);
	if (rc != 0)
		return rc;
	if (a.size() < b.size())
		return -1;
	else if (a.size() > b.size())
		return 1;

	if (time_len_ == 0 || use_host_time_)
		return 0;

	// compare time field
	uint64_t a_v, b_v;
	switch (time_len_) {
	case 4:
		a_v = *(uint32_t *)(a.data() + a.size() - 4);
		b_v = *(uint32_t *)(b.data() + b.size() - 4);
		break;
	case 8:
		a_v = *(uint64_t *)(a.data() + a.size() - 8);
		b_v = *(uint64_t *)(b.data() + b.size() - 8);
		break;
	default:
		abort();
	}

	return a_v < b_v ?  -1 : (a_v > b_v ? 1 : 0);
}

bool LdbComparator::Drop(const leveldb::Slice &key) const
{
	uint64_t t = 0;
	ASSERT(key.size() >= time_len_);

	switch (time_len_) {
	case 0:
		return false;
	case 4:
		t = *(uint32_t *)(key.data() + key.size() - 4);
		break;
	case 8:
		t = *(uint64_t *)(key.data() + key.size() - 8);
		break;
	default:
		abort();
	}

	if (time_scale_value_ > 0)
		t *= time_scale_value_;
	else
		t /= -time_scale_value_;


	uint64_t now = (uint64_t)::time(NULL);
	return (t + expire_time_ >= now) ? false : true;
}

void LdbComparator::FindShortestSeparator( std::string* start, const leveldb::Slice& limit) const {
	// Find length of common prefix
	size_t min = std::min(start->size(), limit.size());
	ASSERT(min >= time_len_);
	min -= time_len_;
	size_t diff_index = 0;
	while ((diff_index < min) &&
			((*start)[diff_index] == limit[diff_index])) {
		diff_index++;
	}

	if (diff_index >= min) {
		// Do not shorten if one string is a prefix of the other
	} else {
		uint8_t diff_byte = static_cast<uint8_t>((*start)[diff_index]);
		if (diff_byte < static_cast<uint8_t>(0xff) &&
				diff_byte + 1 < static_cast<uint8_t>(limit[diff_index])) {
			(*start)[diff_index]++;
			// Keep time field unchanged
			start->erase(diff_index + 1, start->size() - time_len_ - diff_index - 1);
			ASSERT(Compare(*start, limit) < 0);
		}
	}
}

void LdbComparator::FindShortSuccessor(std::string* key) const {
	// Find first character that can be incremented
	size_t n = key->size();
	ASSERT(n >= time_len_);
	n -= time_len_;
	for (size_t i = 0; i < n; i++) {
		const uint8_t byte = (*key)[i];
		if (byte != static_cast<uint8_t>(0xff)) {
			(*key)[i] = byte + 1;
			// Keep time field unchanged
			key->erase(i + 1, key->size() - time_len_ - i - 1);
			return;
		}
	}
	// *key is a run of 0xffs.  Leave it alone.
}

