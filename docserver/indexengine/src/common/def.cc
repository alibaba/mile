// hyperindex_def.c : hyperindex_def
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-06-13

#include "def.h"
#include <stdlib.h>

int32_t g_running_flag = 1;

double g_load_threshold = 0;

struct key_value_pair g_field_type_kvs[] = {
	{ "TINY", HI_TYPE_TINY }, 
	{ "SHORT", HI_TYPE_SHORT }, 
	{ "UNSIGNED_SHORT", HI_TYPE_UNSIGNED_SHORT }, 
	{ "LONG", HI_TYPE_LONG }, 
	{ "UNSIGNED_LONG", HI_TYPE_UNSIGNED_LONG }, 
	{ "FLOAT", HI_TYPE_FLOAT }, 
	{ "DOUBLE", HI_TYPE_DOUBLE }, 
	{ "NULL", HI_TYPE_NULL }, 
	{ "LONGLONG", HI_TYPE_LONGLONG }, 
	{ "UNSIGNED_LONGLONG", HI_TYPE_UNSIGNED_LONGLONG }, 
	{ "VARCHAR", HI_TYPE_VARCHAR },  
	{ "STRING", HI_TYPE_STRING }, 
	{ NULL, -1 }
};

struct key_value_pair g_store_type_kvs[] = {
	{ "MEM",STORE_TYPE_MEM},
	{ "DISK",STORE_TYPE_DISK},
	{ NULL, -1 }
};

struct key_value_pair g_index_key_kvs[] = {
	{ "BTREE", HI_KEY_ALG_BTREE }, 
	{ "HASH", HI_KEY_ALG_HASH }, 
	{ "FILTER", HI_KEY_ALG_FILTER }, 
	{ NULL, HI_KEY_ALG_UNDEF }
};

int get_kv_value( struct key_value_pair *kvs, const char *key )
{
	assert( NULL != kvs );
	struct key_value_pair *p = kvs;
	while( NULL != p->key && strcasecmp( p->key, key ) != 0 )
		p++;

	return p->value;
}

struct type_name_map_t g_type_name_map;

type_name_map_t::type_name_map_t()
{
	for (int i = 0; i < HI_TYPE_MAX; i++)
		names[i] = NULL;

	// TODO : use g_field_type_kvs to initialize names?

	names[HI_TYPE_NULL] = "null";
	names[HI_TYPE_TINY] = "int8";
	names[HI_TYPE_SHORT] = "int16";
	names[HI_TYPE_UNSIGNED_SHORT] = "uint16";
	names[HI_TYPE_LONG] = "int32";
	names[HI_TYPE_UNSIGNED_LONG] = "uint32";
	names[HI_TYPE_LONGLONG] = "int64";
	names[HI_TYPE_UNSIGNED_LONGLONG] = "uint64";
	names[HI_TYPE_FLOAT] = "float";
	names[HI_TYPE_DOUBLE] = "double";
	names[HI_TYPE_VARCHAR] = "varchar";
	names[HI_TYPE_STRING] = "string";
}


struct mile_config_t mile_conf = {
	0, // disk_write_limit
	0 // always use mmap
};

//根据类型，返回对应的数据长度，不定长返回0
uint32_t get_unit_size(enum field_types field_type)
{
	uint32_t ret = 0;

	switch(field_type){
		case HI_TYPE_TINY:
			ret = 1;
			break;
		case HI_TYPE_SHORT:
			ret = 2;
			break;
		case HI_TYPE_UNSIGNED_SHORT:
			ret = 2;
			break;
		case HI_TYPE_LONG:
			ret = 4;
			break;
		case HI_TYPE_UNSIGNED_LONG:
			ret = 4;
			break;
		case HI_TYPE_LONGLONG:
			ret = 8;
			break;
		case HI_TYPE_UNSIGNED_LONGLONG:
			ret = 8;
			break;
		case HI_TYPE_FLOAT:
			ret = 4;
			break;
		case HI_TYPE_DOUBLE:
			ret = 8;
			break;
		case HI_TYPE_VARCHAR:
			ret = 0;
			break;
		case HI_TYPE_STRING:
			ret = 0;
			break;
		default:
			ret = 0;
			break;

	}

	return ret;
}








char const* error_msg(int32_t result_code){
	char const* msg = NULL;
	switch(result_code){
		case WARN_EXCEED_QUERY_LIMIT:
			msg = "exceed query limit";
			break;
		case ERROR_FIELD_INIT_FAILED:
			msg = "field init failed";
			break;
		case ERROR_SEGMENT_INIT_FAILED:
			msg = "segment init failed";
			break;
		case ERROR_TABLE_INIT_FAILED:
			msg = "table init failed";
			break;
		case ERROR_DB_INIT_FAILED:
			msg = "db init failed";
			break;
		case ERROR_SEGMENT_IS_TAKEN:
			msg = "segment is taken";
			break;
		case ERROR_HASH_FILTER_FAILED:
			msg = "hash filter failed";
			break;
		case ERROR_SEGMENT_NOT_INIT:
			msg = "segment not init";
			break;
		case ERROR_FILE_OP_FAILED:
			msg = "file op failed";
			break;
		case ERROR_LOCK_FAILED:
			msg = "lock failed";
			break;
		case ERROR_BINLOG_FAILED:
			msg = "binlog failed";
			break;
		case ERROR_SYNC_FAILED:
			msg = "sync failed";
			break;
		case ERROR_INSERT_FAILDED:
			msg = "insert failed";
			break;
		case ERROR_PK_CONFLICT:
			msg = "pk conflict";
			break;
		case ERROR_INSERT_REPEAT:
			msg = "insert repeat";
			break;
		case ERROR_HASH_CONFLICT:
			msg = "hash conflict";
			break;
		case ERROR_MAP_FAIL:
			msg = "map fail";
			break;
		case ERROR_EXCEED_LIMIT:
			msg = "exceed limit";
			break;
		case ERROR_EXCEED_FIELD_LIMIT:
			msg = "exceed field limit";
			break;
		case ERROR_EXCEED_SEGMENT_NUM:
			msg = "exceed segment num limit";
			break;
		case ERROR_EXCEED_TABLE_NUM:
			msg = "exceed table num limit";
			break;
		case ERROR_FIELD_DELETED:
			msg = "field has been deleted";
			break;
		case ERROR_TABLE_DELETED:
			msg = "table has been deleted";
			break;
		case ERROR_ROWID_ISDELETED:
			msg = "row has been deleted";
			break;
		case ERROR_NOT_SUPPORT_INDEX:
			msg = "unsupported index";
			break;
		case ERROR_ONLY_FILTER_SUPPORT:
			msg = "only filter support";
			break;
		case ERROR_ONLY_HASH_SUPPORT:
			msg = "only hash support";
			break;
		case ERROR_NOT_SUPPORT_NULL:
			msg = "not support null";
			break;
		case ERROR_NOT_SUPPORT_DEL:
			msg = "not support delete";
			break;
		case ERROR_FIELD_NOT_WORK:
			msg = "field not work";
			break;
		case ERROR_UPDATE_ON_UNION_HASH:
			msg = "error update on union hash";
			break;
		case ERROR_DIR_CREATE_FAILED:
			msg = "dir create failed";
			break;
		case ERROR_SET_OPERATION:
			msg = "set operation fail";
			break;
		case ERROR_UNSUPPORTED_SORT_COLUMN:
			msg = "unsupported sort column";
			break;
		case ERROR_UNSUPPORTED_SQL_TYPE:
			msg = "unsupported sql type";
			break;
		case ERROR_UNSUPPORTED_DATA_TYPE:
			msg = "unsupported data type";
			break;
		case ERROR_HASH_CONDITION_PROCESSING:
			msg = "error in hash condition processing";
			break;
		case ERROR_FILTER_CONDITION_PROCESSING:
			msg = "error in filter condition processing";
			break;
		case ERROR_QUERY_BY_ROWID:
			msg = "error query by rowid";
			break;
		case ERROR_NOT_ENOUGH_MEMORY:
			msg = "not enough memory in mempool";
			break;
		case ERROR_UNSUPPORTED_AGRFUNC_TYPE:
			msg = "unsupported agrfunc type";
			break;
		case ERROR_GET_DOCSERVER_STATE:
			msg = "error when get docserver state";
			break;
		case ERROR_DATA_SEND:
			msg = "error when sending data";
			break;
		case ERROR_DATA_RECEIVE:
			msg = "erro when receiving data";
			break;
		case ERROR_PACKET_FORMAT:
			msg = "error packet format";
			break;
		case ERROR_TIMEOUT:
			msg = "timeout error";
			break;
		case ERROR_OS_OVERLOADING:
			msg = "os overloading";
			break;
		case ERROR_INDEX_QUERY:
			msg = "error in using index query";
			break;
		default:
			msg = "unknown error";
	}
	return msg;
}


uint64_t get_time_usec() 
{
	struct timeval time;
	gettimeofday(&time, NULL);
	return (time.tv_sec * 1000000 + time.tv_usec);
}


