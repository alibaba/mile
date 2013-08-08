// Copyright (c) 2011 The LevelDB Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file. See the AUTHORS file for names of contributors.

#include "config.h"

namespace leveldb {
int config::kL0_CompactionTrigger = 4;
int config::kL0_SlowdownWritesTrigger = 8;
int config::kL0_StopWritesTrigger = 12;
int config::kMaxMemCompactLevel = 2;
size_t config::kTargetFileSize = 2 << 20;
size_t config::kBaseLevelSize = 20 << 20;
int config::kAllowedSeeks = 1 << 30;
bool config::kAllowMmapTable = true;
}
