
#ifndef DEF_H
#define DEF_H
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/time.h>
#include <sys/select.h>
#include <signal.h>
#include <sys/mman.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>
#include <inttypes.h>
#include "log.h"

#ifndef NULL
#define NULL ((void *)0)
#endif


#define DATA_STOAGE_VERSION 0x80000001


// sleep interval after mmap switched, millisecond
#define MMAP_SWITCH_SLEEP_INTERVAL (100)
#define FILENAME_MAX_LENGTH 256
#define IP_ADDRESS_MAX_LENGTH 256
#define MB_SIZE (1<<20)
#define KB_SIZE (1<<10)

#define MASTER_ROLE 0
#define SLAVE_ROLE 1

#define DOCSERVER_PORT 18518 /* document server port */
#define DEFAULT_THREAD_NUM (2)
#define BYTE_SIZE 8


#define DEFAULT_MAX_RESULT_SIZE (0) /* no limit */

#define MAX_BINLOG_DATA_PER_PACKET (KB_SIZE*512)

#define Mile_AtomicSetPtr(a, v) do{__sync_lock_test_and_set(a, v);__sync_synchronize();}while(0)


#define Mile_AtomicAndPtr(a,v) __sync_and_and_fetch(a,v)


#define Mile_AtomicOrPtr(a,v) __sync_or_and_fetch(a,v)


#define Mile_AtomicGetPtr(a) (__sync_synchronize(), *(a))


#define Mile_AtomicSubPtr(a, v)do{__sync_sub_and_fetch(a,v);__sync_synchronize();}while(0)


#define Mile_AtomicAddPtr(a,v) 	do{__sync_add_and_fetch(a,v);__sync_synchronize();}while(0)

#define STATIC_ASSERT_HELPER(expr, msg) (!!sizeof(struct{unsigned int STATIC_ASSERATION__##msg: (expr) ? 1 : -1;}))
#define STATIC_ASSERT(expr, msg) extern int (*assert_function_(void))[STATIC_ASSERT_HELPER(expr, msg)]

extern int32_t g_running_flag;
// os overloading threshold
extern double g_load_threshold;

#define MILE_USLEEP(usec) do{int32_t usec_=(usec);for(;usec_ > 0 && g_running_flag;usec_-=100000){ usleep( usec_> 100000 ? 100000 : usec_);}} while(0)
#define MILE_SLEEP(sec) do{int64_t usec_=(sec)*1000000LL;for(;usec_ > 0 && g_running_flag;usec_-=100000){ usleep( 100000 );}} while(0)

// call a function with errno unchanged
#define BAKENO_CALL(func, ...) do { int errno_save_ = errno; func(__VA_ARGS__); errno = errno_save_;} while(0)

#define CONF_SERVER_SESSION "server"
#define CONF_DOCDB_SESSION "docdb"
#define CONF_LDB_SESSION "ldb"

struct key_value_pair {
	const char *key;
	int value;
};

// find value by key, return default value( key == NULL) if not found.
int get_kv_value( struct key_value_pair *kvs, const char *key );



enum sync_mod { 
	DOCID_BY_BINLOG           = 0,
	DOCID_BY_SELF             = 1
};

enum store_type{
	STORE_TYPE_MEM 		  = 0,
	STORE_TYPE_DISK 	  = 1
};

extern struct key_value_pair g_store_type_kvs[];


enum field_types { 
	HI_TYPE_NULL              = 0,
	HI_TYPE_TINY              = 1,
	HI_TYPE_SHORT             = 2,
	HI_TYPE_UNSIGNED_SHORT    = 3,
	HI_TYPE_LONG              = 4,
	HI_TYPE_UNSIGNED_LONG     = 5,
	HI_TYPE_FLOAT             = 6,
	HI_TYPE_DOUBLE            = 7,
	HI_TYPE_TIMESTAMP         = 9,
	HI_TYPE_LONGLONG          = 10,
	HI_TYPE_UNSIGNED_LONGLONG = 11, 
	HI_TYPE_VARCHAR           = 18,
	HI_TYPE_SET				  = 19,
	HI_TYPE_ARRAY			  = 50,
	HI_TYPE_BYTES             = 51,
	HI_TYPE_NOTCOMP           = 52,
	HI_TYPE_STRING            = 254,
	HI_TYPE_MAX
};

extern struct key_value_pair g_field_type_kvs[];

struct type_name_map_t {
	const char *names[HI_TYPE_MAX];
	type_name_map_t();
	const char *get(enum field_types type) { return names[type];}
};

extern type_name_map_t g_type_name_map;

enum index_key_alg {
	HI_KEY_ALG_UNDEF            = 0,              /* no index */
	HI_KEY_ALG_BTREE            = 1,              /* B-tree          */
	HI_KEY_ALG_HASH             = 2,              /* HASH keys (HEAP tables)  default one*/
	HI_KEY_ALG_FILTER           = 3,
	HI_KEY_ALG_FULLTEXT          = 4               /*full text*/
};
extern struct key_value_pair g_index_key_kvs[];


/* 数据访问类型，在某些情况下允许对数据访问做优化 */
enum data_access_type_t{
	DATA_ACCESS_ORIGINAL 		= 0,
	DATA_ACCESS_FILTER		= 1
};


enum field_access_type_t{
	//must get the original value
	FIELD_ACCESS_ORIGINAL		= 0,
	//could get the original value from filter index
	FIELD_ACCESS_FILTER_ORIGINAL = 1,
	//could get the hash value from filter index instead of the original value
	FIELD_ACCESS_FILTER_HASHED	= 2
};


/*结果码*/
enum result_code_t{
		MERGESERVER_HEALTH = 2,
		MILE_SLAVE_CACTCH_UP = 1,
		MILE_RETURN_SUCCESS = 0,

		WARN_EXCEED_QUERY_LIMIT = -9999,

		/*初始化错误*/
		ERROR_FIELD_INIT_FAILED = -3997,
		ERROR_SEGMENT_INIT_FAILED = -3996,
		ERROR_TABLE_INIT_FAILED = -3995,
		ERROR_DB_INIT_FAILED = -3994,
		ERROR_SEGMENT_IS_TAKEN = -3993,
		ERROR_HASH_FILTER_FAILED = -3992,
		ERROR_SEGMENT_NOT_INIT = -3991,
		ERROR_FILE_OP_FAILED = -3990,
		ERROR_LOCK_FAILED = -3989,
		ERROR_TABLE_NOT_EXIT = -3988,
		
		/*binlog 初始化出错*/
		ERROR_BINLOG_FAILED = -3988,
		ERROR_SYNC_FAILED = -3987,

		/* 插入错误 */
		ERROR_INSERT_FAILDED = -4000,
		ERROR_PK_CONFLICT = -4001,
		ERROR_INSERT_REPEAT = -4002,
		ERROR_HASH_CONFLICT = -4003,
		ERROR_MAP_FAIL = -4004,
		ERROR_COMMPRESS_FAIL = -4005,
		ERROR_UPDATE_FAIL = -4006,
		ERROR_MMAP_SWITCH = -4007,

		/*一些超出错误*/
		ERROR_EXCEED_LIMIT = -3899,
		ERROR_EXCEED_FIELD_LIMIT = -3897,
		ERROR_EXCEED_SEGMENT_NUM = -3896,
		ERROR_EXCEED_TABLE_NUM = -3895,
		ERROR_EXCEED_CURRENT = -3894,

		/*当列或者表已被删除的时候，应用仍操作*/
		ERROR_FIELD_DELETED = -3798,
		ERROR_TABLE_DELETED = -3797,
		ERROR_ROWID_ISDELETED = -3796,

		/*一些列的服务范畴*/
		ERROR_NOT_SUPPORT_INDEX = -3699,
		ERROR_ONLY_FILTER_SUPPORT = -3695,
		ERROR_ONLY_HASH_SUPPORT = -3694,
		ERROR_NOT_SUPPORT_NULL = -3693,
		ERROR_NOT_SUPPORT_DEL = -3692,
		ERROR_FIELD_NOT_WORK = -3691,
		ERROR_UPDATE_ON_UNION_HASH = -3690,
		ERROR_NOT_SUPPORT_COMMPRESS = -3689,
		ERROR_NOT_NEED_COMPRESS = -3688,
		ERROR_INDEX_FIELD_COMPRESSED = -3687,

		ERROR_DIR_CREATE_FAILED = -3599,
		ERROR_FILTER_VALUE_COMPARE = -3600,

		/* 进行集合运算时出现错误 */
		ERROR_SET_OPERATION = -4099,

		ERROR_UNSUPPORTED_SORT_COLUMN = -4100,


		ERROR_UNSUPPORTED_SQL_TYPE = -3000,
		ERROR_UNSUPPORTED_DATA_TYPE = -3001,
		ERROR_HASH_CONDITION_PROCESSING = -3002,
		ERROR_FILTER_CONDITION_PROCESSING = -3003,
		ERROR_QUERY_BY_ROWID = -3004,
		ERROR_SEGHINT_PROCESSING = -3005,
		ERROR_INDEX_QUERY = -3100,


		/* 内存池中内存不足 */
		ERROR_NOT_ENOUGH_MEMORY = -3005,
		/* 不支持的聚合函数类型 */
		ERROR_UNSUPPORTED_AGRFUNC_TYPE = -3006,
		/* 获取docserver状态错误 */
		ERROR_GET_DOCSERVER_STATE = -3007,

		/* 数据网络传输错误 */
		ERROR_DATA_SEND = -1000,
		ERROR_DATA_RECEIVE = -1001,

		/* 报文格式错误 */
		ERROR_PACKET_FORMAT  = -1002,

		/* 超时错误 */
		ERROR_TIMEOUT = -999,

		/* OS overloading */
		ERROR_OS_OVERLOADING = -991
};

//排序类型
enum order_types{
	ORDER_TYPE_ASC = 101,
	ORDER_TYPE_DESC = 102
};


//函数类型
enum function_type{
	FUNC_COUNT = 50,
	FUNC_SUM = 51,
	FUNC_MAX = 52,
	FUNC_MIN = 53,
	FUNC_COUNT_DISTINCT = 54,
	FUNC_SQUARE_SUM = 55,
	FUNC_AVG = 56,
	FUNC_VAR = 57,
	FUNC_STD = 58
};

struct low_data_struct{
	char* field_name;
	enum field_types type;
	void* data;
	uint32_t len;
};

//提示列数组
struct hint_array{
	uint32_t n;
	uint64_t* hints;
};


struct field_data{
	struct low_data_struct* data;
	uint16_t fid;
};

struct row_data{
	struct low_data_struct* datas;
	uint16_t field_count;
};


enum select_types_t{
	// the selected value must be the original value
	SELECT_TYPE_ORIGINAL = 0,
	// the selected value can be hashed value instead of the original value
	SELECT_TYPE_HASH = 1,
	// the selected value can be ignored in this query, the selection will be delayed
	SELECT_TYPE_DELAY = 2,
	// the selected value can not be get from the storage, so it can be skipped
	SELECT_TYPE_SKIP = 3
};




struct select_fields_t{
	// number of select fields
	uint32_t n;
	// the array of the select fields name
	char** fields_name;
	// the array of the expected select type
	enum select_types_t* select_type;
	// the access way of the storage engine
	void* access_way;
};





struct select_row_t{
	// number of select fields
	uint32_t n;
	// the array of the values of the select fields
	struct low_data_struct* data;
	// the array of the select_type
	enum select_types_t* select_type;
	// the handler to visit this row
	void* handler;
};


/* 范围查询条件 */
struct db_range_query_condition{
	/* 较小的key */
	struct low_data_struct* min_key;

	/* 0 1 -1分别表示>= > 无*/
	short min_flag;

	/* 较大的key */
	struct low_data_struct* max_key;

	/* 0 1 -1分别表示<= < 无*/
	short max_flag;
};


//比较子的类型
enum compare_type{
	//等于
	CT_EQ = 7,
	//大于
	CT_GT = 8,
	//大于等于
	CT_GE = 9,
	//小于
	CT_LT = 10,
	//小于等于
	CT_LE = 11,
	//In
	EXP_COMPARE_IN = 13,
	//BETWEEN ()
	EXP_COMPARE_BETWEEN_LG = 15,
	//BETWEEN (]
	EXP_COMPARE_BETWEEN_LGE = 16,
	//BETWEEN [)
	EXP_COMPARE_BETWEEN_LEG = 17,
	//BETWEEN []
	EXP_COMPARE_BETWEEN_LEGE = 18,
	// <>, not equel
	CT_NE = 20,
	// match
	CT_MATCH = 21
};

inline bool is_between_compare(enum compare_type t)
{
	return (t == EXP_COMPARE_BETWEEN_LG || t == EXP_COMPARE_BETWEEN_LGE ||
			t == EXP_COMPARE_BETWEEN_LEG || t == EXP_COMPARE_BETWEEN_LEGE);
}

// globle config item
struct mile_config_t {
	// disk write limit byte/sec
	uint32_t disk_write_limit;
	// always use mmap
	uint8_t all_mmap;
};

extern struct mile_config_t mile_conf;


//根据类型，返回对应的数据长度，不定长返回-1
uint32_t get_unit_size(enum field_types field_type);



char const* error_msg(int32_t result_code);


uint64_t get_time_usec();

static inline uint64_t get_time_msec() { return get_time_usec() / 1000; }


#endif /* DEF_H */

