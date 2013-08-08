/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_table.h
 *
 *    Description:  表的结构信息，管理所有的段
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


#include "../../common/def.h"
#include "segment.h"
#include <time.h>
#include <pthread.h>
#include <memory.h>


#define MAX_SEGMENT_NUM (1<<10)

#define MAX_TABLE_NAME 100

#define SEGMENT_RUNTIME_SIZE(table) table->max_segment_num * sizeof(struct segment_meta_data) 

#define TABLE_RUNTIME_SIZE sizeof(struct table_meta_data)*MAX_TABLE_NUM + sizeof(uint8_t)


#ifndef TABLE_H
#define TABLE_H


/*数据表状态*/
enum table_stat{
	TABLE_INIT    = 0x01,  /*表示该表已初始化*/
    TABLE_DEL     = 0x02        /*表示该表已经满了*/
};


/*PK的结果*/
#define PK_OK 0
#define PK_FAIL 1


#define STORE_RAW_DATA 1




struct segment_not_expired{
	uint16_t sid;
	struct list_head sid_list;
};

struct segment_query_values{
	/*segment id号*/
	uint8_t sid;

	/*data*/
	struct low_data_struct* data;

	/*列表*/
	struct list_head values_list;
};

struct table_config{
	/*存储目录*/
	struct str_array_t *storage_dirs;


	/*支持的最大段数*/
	uint16_t max_segment_num;

	/*压缩比，需要从配置文件里读取*/
	uint32_t hash_compress_num;

	uint32_t row_limit;

	uint8_t store_raw;

	/*table的元数据信息*/
	struct table_meta_data* table_meta;
};


struct table_meta_data{
	/*表名*/
	char   table_name[MAX_TABLE_NAME];

	/*table的标记*/
	enum table_stat	stat;

	/*当前正在写入的segment号*/
	uint16_t segment_current;

	/*索引列的个数*/
	uint16_t index_field_count;

	/*映射到内存的地址*/
	struct index_field_meta index_meta[MAX_INDEX_FIELD_NUM];
};

struct table_manager{
	// stroage dir
	struct str_array_t *storage_dirs;
	/*工作目录，为存储目录的第一个*/
	char *work_space;

	/*存储segment的元数据信息*/
	char segment_meta_filename[FILENAME_MAX_LENGTH];

	/*table的元数据信息*/
	struct table_meta_data* table_meta;

	/*该表下的所有段信息*/
	struct segment_manager** segments;

	/*段的元数据信息*/
	struct segment_meta_data* segment_meta;

	/*支持的最大段数*/
	uint16_t max_segment_num;

	/*用于加速索引使用，列名->index_field_meta*/
	struct string_map* index_meta_hash;

	/*压缩比，需要从配置文件里读取*/
	uint32_t hash_compress_num;

	uint32_t row_limit;

	uint8_t store_raw;;

	/*写保护锁*/
	pthread_mutex_t write_protect_locker;

	/*读保护锁，用于在压缩、段加载卸载的读保护*/
	pthread_rwlock_t read_protect_locker;

	/* 新段是否已加入完成的标记，只有新段加入完成之后才能开始对table做mmap_switch和checkpoint，将老段内容刷到磁盘 */
	/* 使用此标记以避免table_add_segment中的写锁等待mmap_switch和db_checkpoint中的读锁，造成插入时间过长 */
	uint8_t new_seg_complete;
	
	MEM_POOL* mem_pool;
};




/**
  * 初始化信息
  * @param  config 表配置信息
  * @param  mem_pool 内存池
  * @return 成功返回table信息，失败返回NULL
  **/ 
struct table_manager* table_init(struct table_config* config,MEM_POOL* mem_pool);



/**
  * 表恢复，将大于等于docid的数据清除
  * @param  
  **/
int32_t table_recover(struct table_manager* table, uint16_t sid, uint32_t docid);

/**
  * 建立索引
  * @param  table 表信息
  * @param  field_name 列名
  * @param  index_type 索引类型
  * @param  data_type 数据类型
  * @param  mem_pool 内存池
  * @return 成功返回0，失败返回<0
  **/ 
int32_t table_ensure_index(struct table_manager* table,
     					 char* field_name,
     					 enum index_key_alg index_type,
     					 enum field_types data_type,
     					 MEM_POOL* mem_pool);


/**
  * 检查索引
  * @param  table 表信息
  * @param  field_name 列名
  * @param  index_type 索引类型
  * @param  data_type 数据类型
  * @return 成功返回1，失败返回0
  **/ 
int32_t check_index(struct table_manager* table,char* field_name,enum index_key_alg index_type,enum field_types* data_type);


/**
  * 删除索引
  * @param  table 表信息
  * @param  field_name 列名
  * @param  index_type 索引类型
  * @return 成功返回0，失败返回<0
  **/
int32_t table_del_index(struct table_manager* table,char* field_name,enum index_key_alg index_type);



/**
  * 获取段的创建时间
  * @param  table 表信息 
  * @param  sid 段号
  * @return 如果段已经初始化好，则返回相应的创建时间，否则返回0
  **/ 
uint64_t table_get_segment_ctime(struct table_manager* table,uint16_t sid);


/**
  * 获取段的修改时间
  * @param  table 表信息 
  * @param  sid 段号
  * @return 如果段已经初始化好，则返回相应的创建时间，否则返回0
  **/ 
uint64_t table_get_segment_mtime(struct table_manager* table,uint16_t sid);


/**
  * 根据flag来判断，是由上层提供段号，还是自己生成，如果自己生成，段若已经超出limit，需要开辟新的段，如果上层指定，则查看sid是否
  * 已经存在，如果不存在则需要初始化，如果自己生产，还需要返回段的rowcount
  * @param  table 表信息
  * @param  sid 段号
  * @param  docid 行号
  * @flag 1表示自己生产，0表示由上层提供
  * @param  mem_pool 该内存池是用来开辟新的段
  * @return 成功返回0，失败返回<0
  **/ 
int32_t table_prepare_insert(struct table_manager* table,uint16_t* sid,uint32_t* docid,uint8_t flag,MEM_POOL* mem_pool);


/**
  * 向指定的表插入一行记录，包括数据列，以及索引列
  * @param  table 表信息
  * @param  sid 段号
  * @param  docid 行号
  * @param  rdata 一行的数据
  * @param  mem_pool 该内存池是用来开辟新的段
  * @return 成功返回0，失败返回<0
  **/ 
int32_t table_insert(struct table_manager* table,uint16_t sid,uint32_t docid,struct row_data* rdata,MEM_POOL* mem_pool);


/**
  * 向指定的表插入一条件记录，并返回值
  * @param  table 表信息
  * @param  sid 段号
  * @param  docid 行号
  * @param  new_data 新数据
  * @param  old_data 老数据
  * @param  flag:0表示由上层指定sid docid，1表示段自己生成段号
  * @param  mem_pool内存池
  * @return 成功返回0，失败返回<0
  **/ 
int32_t table_update(struct table_manager* table,
				   uint16_t sid,
				   uint32_t docid, 
				   struct low_data_struct* new_data,
				   struct low_data_struct** old_data,
				   MEM_POOL* mem_pool);



/**
  * 根据列名和docid查找对应的原始值
  * @param  table 表信息
  * @param  sid 段号
  * @param  field_name 列名
  * @param  docid 
  * @param  mem_pool 内存池
  * @return 成功返回low_data_struct，失败返回NULL
  **/ 
struct low_data_struct* table_data_query_col(struct table_manager* table,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool);



/**
  * 根据docid查找整个一行的数据
  * @param  table 表信息
  * @param  sid 段号
  * @param  docid
  * @param  mem_pool 内存池
  * @return 成功返回row_data，失败返回NULL
  **/ 
struct row_data* table_data_query_row(struct table_manager* table,uint16_t sid,uint32_t docid,MEM_POOL* mem_pool);


/**
  * btree范围查询，满足条件的row id list
  * @param  table 表信息
  * @param  sid 段号
  * @param  field_name 列名
  * @param  range_condition 查询的条件
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct list_head* table_index_range_query(struct table_manager* table, char* field_name, \
			struct hint_array* time_cond, struct db_range_query_condition * range_condition, MEM_POOL* mem_pool);




/**
  * 根据docid来查找字符串hash后的64位整型
  * @param  table 表信息
  * @param  sid 段号
  * @param  field_name 列名
  * @param  docid 行号
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct low_data_struct* table_index_value_query(struct table_manager* table,
												uint16_t sid,
												char* field_name,
												uint32_t docid,
												MEM_POOL* mem_pool);





/**
  * 根据seghint来查找对应的seg id lists
  * @param  table 表信息
  * @param  time_cond seghint信息
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/
struct list_head* table_seghint_query(struct table_manager* table, struct hint_array* time_cond, MEM_POOL* mem_pool);


/**
  * 根据value来查找对应的count值
  * @param  table 表信息
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
uint32_t table_index_count_query(struct table_manager* table, 
							     struct list_head* seg_list,
								 struct low_data_struct* data,
								 MEM_POOL * mem_pool);



/**
  * 根据value来查找对应的row id lists，等值查询，只有hash索引支持
  * @param  table 表信息
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct list_head* table_index_equal_query(struct table_manager* table,
										  struct list_head* seg_list,
										  struct low_data_struct* data,
										  MEM_POOL* mem_pool);


/**
  * 根据value来查找对应的row id lists，等值查询，只有full text索引支持
  * @param  table 表信息
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct list_head* table_fulltext_index_equal_query(struct table_manager* table,
											   	   struct list_head* seg_list,
										           struct low_data_struct* data,
										           MEM_POOL* mem_pool);


/**
  * 根据time_cond过滤段，并且统计过滤后的段得记录总数
  * @param	table 表信息 
  * @param  seg_list	段链表
  * @return 成功返回记录数
  **/ 
int64_t table_get_record_num(struct table_manager* table, struct list_head* seg_list);





/**
  * 根据time_cond过滤段，并且统计过滤后的段得删除记录总数
  * @param	table 表信息
  * @param  seg_list	段链表
  * @return 成功返回删除记录数
  **/
int64_t table_get_delete_num(struct table_manager* talbe, struct list_head* seg_list);




/**
  * 根据行号进行删除
  * @param	table 表信息
  * @param  sid 段号
  * @param  docid 行号
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
int32_t table_del_docid(struct table_manager* table,uint16_t sid,uint32_t docid);



/**
  * 判断指定的doc id是否标记为删除
  * @param	table 表信息
  * @param  sid 段号
  * @param  row_id 行号
  * @return 已删除返回1，未删除返回0  
  **/
int32_t table_is_docid_deleted(struct table_manager* table,uint16_t sid,uint32_t docid);


/**
  * 设置当前插入段号
  * @param	table 表信息
  * @param  sid段号
  * @return 成功返回列0
  **/
int32_t table_set_segment_current(struct table_manager* table, uint16_t sid);


/**
  * 删除表
  * @param	table 表信息
  * @return 成功返回0，失败<0
  **/ 
int32_t table_del(struct table_manager* table);


/**
  * 卸载sid这个段，并且把段得工作目录重名为带dump后缀的文件
  * @param	table 表信息
  * @param	sid段号
  * @param	thread_safe 是否要线程安全
  * @return 成功返回0，失败<0
  **/ 
int32_t table_unload_segment(struct table_manager* table,int16_t sid,uint8_t thread_safe);


/**
  * 用户指定需要load的段得工作目录，以及段号，该函数将把该段load到进程里，并把segment_dir移到table的工作目录下
  * @param	table 表信息
  * @param	sid段号
  * @param  segment_dir需要被load的段目录
  * @param  mem_pool 内存池
  * @return 成功返回0，失败<0
  **/ 
int32_t table_load_segment(struct table_manager* table,int16_t sid,char* segment_dir,MEM_POOL* mem_pool);


/**
  * 将指定table的所有段卸载掉，并从指定的segments_dir目录下加载所有段，这个时间段，将不能进行读取
  * @param	table 表信息
  * @param  segment_dir需要被load的段目录
  * @param  mem_pool 内存池
  * @return 成功返回0，失败<0
  **/ 
int32_t table_replace_all_segments(struct table_manager* table,char* segments_dir,MEM_POOL* mem_pool);


/**
  * 获取段的元数据信息
  * @param  table_name 表名
  * @param  max_segment_num 最大的段数
  * @param  mem_pool 内存池
  *	@return	segment_meta_data 段的元数据信息
  **/
struct segment_meta_data* table_query_segment_stat(struct table_manager* table,uint16_t* max_segment_num,MEM_POOL* mem_pool);


/**
  * 获取表的索引信息
  * @param  table_name 表名
  * @param  index_field_count 索引列数
  * @param  mem_pool 内存池
  *	@return	index_field_meta 表的索引数据
  **/
struct index_field_meta* table_query_index_stat(struct table_manager* table,uint16_t* index_field_count,MEM_POOL* mem_pool);


/**
  * 对指定的段加互斥锁
  * @param  tid 表号
  * @return 成功返回MILE_RETURN_SUCCESS，失败返回ERROR_LOCK_FAILED
  **/ 
int32_t table_lock(struct table_manager* table);


/**
  * 对指定的段解锁
  * @param  tid 表号
  **/ 
void table_unlock(struct table_manager* table);


void table_read_lock(struct table_manager* table);


void table_read_unlock(struct table_manager* table);



/**
  * 根据指定的段号，对指定的段的hash列进行压缩
  * @param	table 表信息
  * @param  sid 段号
  * @param  mem_pool 内存池
  * @return 成功返回list head，上层可以通过这个遍历，取到所有段结果，失败返回NULL
  **/ 
int32_t table_compress(struct table_manager* table,MEM_POOL* mem_pool);


/**
  * 打印segment_query_rowids结构信息
  * @param  rowids_list_h 链表头
  * @return 
  **/
void print_query_rowids(struct list_head* rowids_list_h);


/**
  * 释放表信息，但不释放内存
  * @param	table 表信息
  **/ 
void table_release(struct table_manager* table);


/**
  * 同步memap
  * @param	table 表信息
  **/ 
void table_checkpoint(struct table_manager* table);

/**
 * switch mmap to real file when segment full
 * @param table
 * @param sid
 */
int table_mmap_switch( struct table_manager *table, uint16_t sid);

// in db.h and db.c
extern int create_mmap_switch_thread(char *tablename, uint16_t sid);

#endif

