// Copyright (c) 2011 The LevelDB Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file. See the AUTHORS file for names of contributors.

#ifndef STORAGE_LEVELDB_UTIL_CONFIG_H_
#define STORAGE_LEVELDB_UTIL_CONFIG_H_

#include "leveldb/options.h"
#include <stdint.h>

namespace leveldb {
struct config {
	static const int kNumLevels = 7;

	// Level-0 compaction is started when we hit this many files.
	static int kL0_CompactionTrigger;

	// Soft limit on number of level-0 files.  We slow down writes at this point.
	static int kL0_SlowdownWritesTrigger;

	// Maximum number of level-0 files.  We stop writes at this point.
	static int kL0_StopWritesTrigger;

	// Maximum level to which a new compacted memtable is pushed if it
	// does not create overlap.  We try to push to level 2 to avoid the
	// relatively expensive level 0=>1 compactions and to avoid some
	// expensive manifest file operations.  We do not push all the way to
	// the largest level since that can generate a lot of wasted disk
	// space if the same key space is being repeatedly overwritten.
	static int kMaxMemCompactLevel;

	// sstable size
	static size_t kTargetFileSize;

	static size_t kBaseLevelSize;

	static int kAllowedSeeks;
	
	static bool kAllowMmapTable;

	static void SetConfig(const Options &opt) {
		kL0_CompactionTrigger = opt.l0_compact_trigger;
		kL0_SlowdownWritesTrigger = opt.l0_slowdown_writes_trigger;
		kL0_StopWritesTrigger = opt.l0_stop_writes_trigger;

		kMaxMemCompactLevel = opt.max_mem_compact_level;
		kTargetFileSize = opt.target_file_size;
		kBaseLevelSize = opt.base_level_size;
		kAllowedSeeks = opt.allowed_seeks;
		kAllowMmapTable = opt.allow_mmap_table;

	}
};
}

#endif // STORAGE_LEVELDB_UTIL_CONFIG_H_
