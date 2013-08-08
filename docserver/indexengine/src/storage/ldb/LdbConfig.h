// LdbConfig.h : LdbConfig
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-06

#ifndef LDBCONFIG_H
#define LDBCONFIG_H

#ifndef CONF_LDB_SESSION
#define CONF_LDB_SESSION "ldb"
#endif

#define CONF_LDB_TABLES "tables"

#define CONF_WRITE_SYNC "write_sync"
#define CONF_READ_VERIFY_CHECKSUMS "read_verify_checksums"


#define CONF_ROW_KEY_SUFFIX ".row_key"
#define CONF_TIME_KEY_SUFFIX ".time_key"
#define CONF_TIME_KEY_SCALE_SUFFIX ".time_key_scale"
#define CONF_TIME_KEY_LEN_SUFFIX ".time_key_len"
#define CONF_EXPIRE_TIME_SUFFIX ".expire_time"

#define CONF_CUMULATIVE_STEP_SUFFIX ".cumulative_step"
#define CONF_AGGREGATE_DESC_SUFFIX ".aggregate_desc"

#endif // LDBCONFIG_H
