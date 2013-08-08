/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_db.h
 *
 *    Description:  整个DB层的接口描述，以及DB的需要维护的结构信息定义
 *
 *        Version:  1.0
 *        Created: 	2011年04月09日 11时41分55秒 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */
 
#ifndef DB_H
#define DB_H
#include "../../common/def.h"
#include "table.h"
#include "../../common/file_op.h"
#include "../binlog.h"
#include "../../common/stat_collect.h"
#include "../../common/common_util.h"
#include <sys/types.h>
#include <stdint.h>

#define MAX_TABLE_NUM 0xff
#define LOG_LEVEL_NAME 8
#define USEC_PER_MSEC 1000

enum db_stat{
	RECOVER_DATA = 0,
	RECOVER_FIN = 1
};

/*db的配置信息*/
struct db_conf{
	/* server config */
	uint16_t cpu_threshold;
	char sync_addr[IP_ADDRESS_MAX_LENGTH];
	
	struct str_array_t storage_dirs;

	char binlog_dir[FILENAME_MAX_LENGTH];

	uint32_t checkpoint_interval;
	uint32_t binlog_maxsize;
	uint8_t  binlog_flag; /*是否打开binlog日志*/
	uint64_t binlog_threshold; /*标志多长时间内追赶上的门限值*/
	int64_t binlog_sync_interval; /*binlog的同步时间，毫秒,*/
	uint8_t role;
	uint32_t hash_compress_num;
	uint16_t max_segment_num;
	uint32_t row_limit;
	uint16_t profiler_threshold;

	struct string_map* table_store_only_index;
	
	int32_t max_result_size;
	
	int32_t cut_threshold;
};

struct data_import_field_info{
	 char field_name[MAX_FIELD_NAME];
     enum index_key_alg index_type;
     enum field_types data_type;
};

struct data_import_conf{
	char   table_name[MAX_TABLE_NAME];
	char   encode_type[128];
	char   split[5];
	uint16_t field_count;
	struct data_import_field_info* fields_info;
};



/*记录binlog的元数据信息*/
typedef struct binlog_meta{
	uint64_t last_checkpoint;  //the time of last checkpoint

	/*slave端用作记录binlog同步的位置信息*/
	uint64_t offset;
}BL_META, *BL_META_PTR;



/*db信息*/
struct db_manager{
	/*所有的表信息，用一个string_map结构映射起来 tablename->struct table_manager*/
	struct string_map* tables;

	/*表数量，持久化到磁盘中*/
	uint8_t* table_count;

	/*所有表名，持久化到磁盘中*/
	struct table_meta_data* table_metas;

	/*将表名和表元数据信息用hash 关联起来*/
	struct string_map* table_metas_hash;

	/*binlog的元数据信息*/
	BL_META_PTR bl_meta;

	/*binlog开始同步的时间点，毫秒*/
	uint64_t catch_up_start;

	/*binlog开始同步的时候已经完成的偏移量*/
	uint64_t offset_start;
	
	struct str_array_t *storage_dirs;
	char *work_space; // work space is the first dir of storage_dirs
	
	char binlog_dir[FILENAME_MAX_LENGTH];

	/*bin log信息*/
	BL_WRITER_PTR binlog_writer;

	/*checkpoint的时间间隔*/
	uint32_t checkpoint_interval;

	/*binlog单个文件的最大日志量*/
	uint32_t binlog_maxsize;

	/*是否打开binlog标志*/
	uint8_t binlog_flag;

	/*binlog的同步时间，毫秒，<0表示从不sync*/
	int64_t binlog_sync_interval;

	uint32_t hash_compress_num;

	uint16_t max_segment_num;

	uint32_t row_limit;

    MEM_POOL* mem_pool;

	pthread_t checkpoint_tid;

	pthread_t sync_tid;
	
	/*读写锁*/
	pthread_rwlock_t locker;

	pthread_mutex_t task_locker;

	/*标志多长时间内追赶上的门限值*/
	uint64_t binlog_threshold;

	/*db的状态，主要用来当恢复的时候不用再记binlog了*/
	uint8_t db_stat;

	uint8_t readable;

	uint8_t role;

	struct string_map* table_only_store_index;
};

/**
 * segment full thread parameters
 */
struct segment_full_param_t {
	char *tablename;
	uint16_t sid;
	MEM_POOL_PTR mem;
};

/**
 * parameters for db_recover_check()
 */
struct recover_check_param_t {
	MEM_POOL_PTR mem;
	struct string_map *table_sid;
	char *last_table;
	uint16_t last_sid;
};



struct access_way_t{
	enum data_access_type_t access_type;
	// number of actual select fields
	uint32_t actual_sel_n;
	// the actual select fields name
	char** actual_fields_name;
	// the array of the actual select type
	enum select_types_t* actual_select_type;
};


/**
  * 初始化一个db信息，并将其置为全局变量
  * @param  conf db的配置信息
  * @return 成功返回0，失败返回-1
  **/ 
int32_t db_init(struct db_conf* conf);


/**
  * 获取db信息
  * @return 成功返回db info，失败返回NULL
  **/
struct db_manager* get_db();


/**
  * 卸载sid这个段，并且把段得工作目录重名为带dump后缀的文件
  * @param	table_name 表名
  * @param	sid段号
  * @param  mem_pool内存池
  * @return 成功返回0，失败<0
  **/ 
int32_t db_unload_segment(char* table_name,uint16_t sid,MEM_POOL* mem_pool);


/**
  * 用户指定需要load的段得工作目录，以及段号，该函数将把该段load到进程里，并把segment_dir移到table的工作目录下
  * @param	table_name 表名
  * @param	sid段号
  * @param  segment_dir需要被load的段目录
  * @param  mem_pool内存池
  * @return 成功返回0，失败<0
  **/ 
int32_t db_load_segment(char* table_name,int16_t sid,char* segment_dir,MEM_POOL* mem_pool);


/**
  * 将指定table的所有段卸载掉，并从指定的segments_dir目录下加载所有段，这个时间段，将不能进行读取
  * @param	table_name 表名
  * @param  segments_dir需要被load的段目录
  * @param  mem_pool内存池
  * @return 成功返回0，失败<0
  **/ 
int32_t db_replace_all_segments(char* table_name,char* segments_dir,MEM_POOL* mem_pool);


/**
  * 设置当前插入段号
  * @param	table_name 表名
  * @param  sid段号
  * @return 成功返回0
  **/
int32_t db_set_segment_current(char* table_name, uint16_t sid);




/**
  * 创建一个表信息
  * @param  table_name 表名
  * @return 成功返回0，失败返回-1
  **/ 
struct table_manager* db_create_table(char* table_name);



/**
  * 删除一个表信息
  * @param	table_name 表名
  * @return 成功返回0，失败返回-1
  **/
int32_t db_del_table(char* table_name);



/**
  * 向指定的表异步添加一个索引列(同步)
  * @param  table_name 表名
  * @param  field_name 列名
  * @param  index_type 新家索引类型
  * @param  data_type  数据类型
  * @return 成功返回0，失败返回-1
  **/ 
int32_t db_ensure_index(char* table_name,
					  char* field_name,
					  enum index_key_alg index_type,
					  enum field_types data_type, 
					  MEM_POOL* mem_pool);


/**
  * 对指定的表删除索引
  * @param  table_name 表名
  * @param  field_name 列名
  * @param  index_type 新家索引类型
  * @param  mem_pool内存池
  * @return 成功返回0，失败返回-1
  **/ 
int32_t db_del_index(char* table_name,char* field_name,enum index_key_alg index_type,MEM_POOL* mem_pool);



/**
  * 向指定的表插入一行记录
  * @param  table_name 表名
  * @param  sid 段号
  * @param  docid 行号
  * @param  rdata 一行的数据
  * @param  flag:0表示由上层指定sid docid，1表示段自己生成段号
  * @param  mem_pool内存池
  * @return 成功返回0，失败返回<0
  **/ 
int32_t db_insert(char* table_name, uint16_t* sid, uint32_t* docid, struct row_data* rdata, uint8_t flag, MEM_POOL* mem_pool);


/**
  * 向指定的表插入一条件记录，并返回值
  * @param  table_name 表名
  * @param  sid 段号
  * @param  docid 行号
  * @param  new_data 新数据
  * @param  old_data 老数据
  * @param  flag:0表示由上层指定sid docid，1表示段自己生成段号
  * @param  mem_pool内存池
  * @return 成功返回0，失败返回<0
  **/ 
int32_t db_update(char* table_name,
				uint16_t sid,
				uint32_t docid, 
				struct low_data_struct* new_data,
				struct low_data_struct** old_data,
				MEM_POOL* mem_pool);



/**
  * 记录缓存的当前执行成功的位置，以及根据开始同步的位置，以及当前的同步的位置，计算速度，从而获取同步的状态
  * @param  offset_cur 当前的偏移量
  * @param  offset_left 剩余的日志偏移量
  * @return 如果已经追赶上master，则返回MILE_SLAVE_CACTCH_UP，否则返回MILE_RETURN_SUCCESS
  **/ 
int32_t db_slave_set_offset(uint64_t offset_cur,uint64_t offset_left);



/**
  * slave接收到master传的缓存后，由上层解析成bl_record，调用此接口执行
  * 另外数据恢复会调
  * @param  bl_record binlog里一条完整的数据记录
  * @param  mem_pool 内存池
  * @return 成功返回MILE_RETURN_SUCCESS，否则返回对应的错误码
  **/ 
int32_t db_execute_binrecord(struct binlog_record* bl_record,MEM_POOL* mem_pool);


/**
  * slave在开始追赶的时候，需要记录开始的时间点，用于测速，并返回需要同步的偏移量
  * @return 同步开始的位置
  **/
uint64_t db_start_catch_up();



/**
  * 根据行号进行删除
  * @param  table_name 表名
  * @param  sid 段号
  * @param  row_id 行号
  * @param  mem_pool内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
int32_t db_del_docid(char* table_name,uint16_t sid,uint32_t docid,MEM_POOL* mem_pool);



/**
  * 根据rowid来查找对应的value，一次可以查找多列
  * @param  table_name 表名
  * @param  sid 段号
  * @param  docid 行号
  * @param  field_names 要查询的列名
  *	@param	field_num 要查询的列数
  * @param  mem_pool 内存池
  * @return 成功返回多列value的数组，失败返回NULL
  **/
struct low_data_struct** db_data_query_multi_col(char* table_name,uint16_t sid, uint32_t docid, char** field_names,uint32_t field_num, enum data_access_type_t data_access_type, MEM_POOL* mem_pool);




/**
  * 根据列名和docid查找对应的原始值
  * @param  table_name 表名
  * @param  sid 段号
  * @param  field_name 列名
  * @param  docid 
  * @param  mem_pool 内存池
  * @return 成功返回low_data_struct，失败返回NULL
  **/ 
struct low_data_struct* db_data_query_col(char* table_name,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool);




/**
  * 根据docid查找整个一行的数据
  * @param  table_name 表名
  * @param  sid 段号
  * @param  docid
  * @param  mem_pool 内存池
  * @return 成功返回row_data，失败返回NULL
  **/ 
struct row_data* db_data_query_row(char* table_name,uint16_t sid,uint32_t docid,MEM_POOL* mem_pool);


/**
  * btree范围查询，满足条件的row id list
  * @param  table_name 表名
  * @param  sid 段号
  * @param  field_name 列名
  * @param  range_condition 查询的条件
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct list_head* db_index_range_query(char* table_name, char* field_name, \
			struct hint_array* time_cond, struct db_range_query_condition * range_condition, MEM_POOL* mem_pool);




/**
  * 根据docid来查找字符串hash后的64位整型，或者是filter列的原始值
  * @param  table_name 表名
  * @param  sid 段号
  * @param  field_name 列名
  * @param  docid 行号
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct low_data_struct* db_index_value_query(char* table_name,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool);





/**
  * 根据seghint来查找对应的segid lists
  * @param  table_name 表名
  * @param  time_cond seghint条件
  * @param  mem_pool 内存池
  * @return 成功返回list_head，失败返回NULL
  **/ 
struct list_head* db_seghint_query(char* table_name, struct hint_array* time_cond, MEM_POOL* mem_pool);





/**
  * 根据value来查找对应的row id lists，等值查询，只有hash索引支持
  * @param  table_name 表名
  * @param  seg_list 段列表
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回list_head，失败返回NULL
  **/ 
struct list_head* db_index_equal_query(char* table_name,
									   struct list_head* seg_list,
									   struct low_data_struct* data,
									   MEM_POOL* mem_pool);


/**
  * 根据value来查找对应的row id lists，等值查询，只有fulltext索引支持
  * @param  table_name 表名
  * @param  seg_list 段列表
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回list_head，失败返回NULL
  **/ 
struct list_head* db_fulltext_index_equal_query(char* table_name,
									 		    struct list_head* seg_list,
									            struct low_data_struct* data,
									            MEM_POOL* mem_pool);


/**
  * 根据分词来查询倒排索引的长度，只支持全文索引
  * @param  table_name 表名
  * @param  seg_list 段列表
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 返回倒排索引的长度，出错时返回值小于0
  **/
uint32_t db_fulltext_index_length_query(char* table_name, struct list_head* seg_list, struct low_data_struct* data, MEM_POOL_PTR mem_pool);



/**
  * 获取所有的docids，每个段包括最大的段号
  * @param  表名
  * @param  mem_pool 内存池
  * @return 成功返回segment_query_alldocids，失败返回NULL
  **/ 
struct list_head* db_query_all_docids(char* table_name,MEM_POOL* mem_pool);



/**
  * 对指定的表加互斥锁
  * @param  table_name 表名
  **/ 
int32_t db_lock_table(char* table_name);


/**
  * 对指定的表解锁
  * @param  table_name 表名
  **/ 
void db_unlock_table(char* table_name);


/**
  * 对指定的表加读
  * @param  table_name 表名
  **/ 
void db_readlock_table(char* table_name);


/**
  * 对指定的表解锁
  * @param  table_name 表名
  **/ 
void db_unreadlock_table(char* table_name);



void db_read_lock();


void db_read_unlock();



/**
  * 对指定的表和段进行压缩
  * @param  table_name 表名
  * @param  sid 段号
  **/ 
int32_t db_compress(char* table_name,MEM_POOL* mem_pool);


/**
  * 判断指定的doc id是否标记为删除
  * @param  table_name 表名
  * @param  sid 段号
  * @param  row_id 行号
  * @return 已删除返回1，未删除返回0  
  **/
int32_t db_is_docid_deleted(char* table_name,uint16_t sid,uint32_t docid);


/**
  * 通过给定的timehint来获得对应段的记录总数
  * @param  table_name	表名
  *	@param	seg_list	段链表
  *	@return	返回记录数
  **/
int64_t db_get_record_num(char* table_name, struct list_head* seg_list);




/**
  * 通过给定的timehint来获得对应段的删除记录总数
  * @param  table_name	表名
  *	@param	seg_list	段链表
  *	@return	返回删除记录数
  **/
int64_t db_get_delete_num(char* table_name, struct list_head* seg_list);



/**
  * 获取段的创建时间
  * @param  table_name 表名
  * @param  sid 段号
  *	@return	如果段已经初始化好，则返回相应的创建时间，否则返回0
  **/
uint64_t db_get_segment_ctime(char* table_name,uint16_t sid);

/**
  * 获取段的创建时间
  * @param  table_name 表名
  * @param  sid 段号
  *	@return	如果段已经初始化好，则返回相应的创建时间，否则返回0
  **/
uint64_t db_get_segment_mtime(char* table_name,uint16_t sid);


/**
  * 获取段的创建时间
  * @param  table_name 表名
  * @param  max_segment_num 最大的段数
  * @param  mem_pool 内存池
  *	@return	segment_meta_data 段的元数据信息
  **/
struct segment_meta_data* db_query_segment_stat(char* table_name,uint16_t* max_segment_num,MEM_POOL * mem_pool);


/**
  * 获取段的创建时间
  * @param  table_name 表名
  * @param  index_field_count 索引列数
  * @param  mem_pool 内存池
  *	@return	index_field_meta 表的索引数据
  **/
struct index_field_meta* db_query_index_stat(char* table_name,uint16_t* index_field_count,MEM_POOL * mem_pool);



/**
  * 获取某个列的数据类型
  * @param  table_name 表名
  * @param  field_name 列名
  *	@return	field_types 索引数据类型
  **/
enum field_types db_query_data_type(char* table_name,char* field_name);




/**
  * 获取某个列的数据访问类型
  * @param  table_name 表名
  * @param  field_name 列名
  *	@return	field_access_type_t 列的数据访问类型
  **/
enum field_access_type_t db_get_data_access_type(char* table_name, char* field_name);




/**
  * 释放db信息
  * @return 成功返回0，失败返回-1
  **/
void db_release();


/**
  * 同步memap
  * @return 
  **/
void db_checkpoint();



/**
 * change db status to readable.
 */
void set_db_readable( void );

/**
 * change db status to unreadable.
 */
void set_db_unreadable( void );

/**
 * create a thread to do mmap switch
 * @tablename
 * @sid
 */
int create_mmap_switch_thread(char *tablename, uint16_t sid);

enum index_key_alg  db_binrecord_to_index(struct binlog_record* bl_record,
												 char** table_name,
												 char** field_name,
												 enum field_types* data_type,
												 MEM_POOL* mem_pool);

enum index_key_alg  db_binrecord_to_dindex(struct binlog_record* bl_record,
											      char** table_name,
											      char** field_name,
											      MEM_POOL* mem_pool);

												 
struct row_data* db_binrecord_to_insert(struct binlog_record* bl_record,
											   char** table_name, 
											   MEM_POOL* mem_pool);

struct low_data_struct* db_binrecord_to_update(struct binlog_record* bl_record,
													  char** table_name, 
													  char** field_name,
													  MEM_POOL* mem_pool);

void db_binrecord_to_load(struct binlog_record* bl_record,
								 char** table_name,  
 							     char** segment_dir,
 							     MEM_POOL* mem_pool);

void db_binrecord_to_unload(struct binlog_record* bl_record,
								   char** table_name, 
								   MEM_POOL* mem_pool);

void db_binrecord_to_compress(struct binlog_record* bl_record,
								     char** table_name, 
								     MEM_POOL* mem_pool);


#endif
