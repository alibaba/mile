// Copyright (c) 2011 The LevelDB Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file. See the AUTHORS file for names of contributors.

#include "leveldb/options.h"

#include "leveldb/comparator.h"
#include "leveldb/env.h"

namespace leveldb {

Options::Options()
  : comparator(BytewiseComparator()),
  create_if_missing(false),
  error_if_exists(false),
  paranoid_checks(false),
  env(Env::Default()),
  info_log(NULL),
  write_buffer_size(4<<20),
  max_open_files(1000),
  block_cache(NULL),
  block_cache_size(8<<20),
  block_size(4096),
  block_restart_interval(16),
  compression(kSnappyCompression),
  filter_policy(NULL),
  use_bloom_filter(0),
  l0_compact_trigger(4),
  l0_slowdown_writes_trigger(8),
  l0_stop_writes_trigger(12),
  target_file_size(2<<20),
  base_level_size(20<<20),
  max_mem_compact_level(2),
  allow_mmap_table(1)
  {
    // We arrange to automatically compact this file after
    // a certain number of seeks.  Let's assume:
    //   (1) One seek costs 10ms
    //   (2) Writing or reading 1MB costs 10ms (100MB/s)
    //   (3) A compaction of 1MB does 25MB of IO:
    //         1MB read from this level
    //         10-12MB read from next level (boundaries may be misaligned)
    //         10-12MB written to next level
    // This implies that 25 seeks cost the same as the compaction
    // of 1MB of data.  I.e., one seek costs approximately the
    // same as the compaction of 40KB of data.  We are a little
    // conservative and allow approximately one seek for every 16KB
    // of data before triggering a compaction.
    allowed_seeks = target_file_size / 16384;
  }


}  // namespace leveldb
