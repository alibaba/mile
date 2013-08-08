/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_segment.h
 *
 *    Description:  段的管理信息定义，以及接口定义，相当于子表的概念
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

#ifndef SEGMENT_H 
#define SEGMENT_H

#include "index_field.h"
#include "data_field.h"
#include "bitmark.h"
#include "../../common/string_map.h"

#define MAX_SEGMENT_NAME 100

#define MAX_INDEX_FIELD_NUM 100

#define MAX_INDEX_PER_FIELD 10

#define INDEX_FIELD_KEY "%s %d"


/*索引状态*/
enum segment_stat{
	SEGMENT_INIT  = 0x01,  /*表示该段已初始化*/
    SEGMENT_DUMP  = 0x02,      /*表示该段已被dump*/
    SEGMENT_FULL  = 0x04,        /*表示该段已经满了*/
    SEGMENT_COMPRESS = 0x08     /*表示该段已经压缩*/
};


struct index_field_stat{
	/*索引类型*/
	enum index_key_alg index_type;

	enum field_types data_type;

	/*1:索引已经建立完毕  0:表示索引仍在建立 -1:表示建立失败*/
	uint8_t flag;
}__attribute__ ((packed));


/*列索引类型，由table层维护*/
struct index_field_meta{
	/*列名*/
	char field_name[MAX_FIELD_NAME];

	/*索引数量*/
	uint8_t index_count;

	/*索引信息*/
	struct index_field_stat indexs[MAX_INDEX_PER_FIELD];

}__attribute__ ((packed));


struct segment_config{
	char work_space[FILENAME_MAX_LENGTH];

	/*段名*/
	char segment_name[MAX_SEGMENT_NAME];
	
	/*索引列数量，以及索引信息*/
	uint8_t index_field_count;

	/*索引列信息*/
	struct index_field_meta* index_fields;
	
	/*压缩比，需要从配置文件里读取*/
	uint32_t hash_compress_num;
	
	uint32_t row_limit;

	struct segment_meta_data* meta_data;

	/*段号*/
	uint16_t sid;
};


struct segment_meta_data{
	/*创建时间 需持久化*/
	time_t create_time;

	/*最后修改时间 需持久化*/
	time_t modify_time;

	/*各个列的标志位*/
	time_t checkpoint_time;

    /*段的标志*/
	uint8_t flag;
	
	/*已插入的数量，这个以后需要加锁，根据这个值来生成doc id 需持久化*/
	uint32_t row_count;

	/*已删除的数量，需持久化*/
	uint32_t del_count;
}__attribute__ ((packed));


struct segment_manager{
	/*工作路径*/
	char work_space[FILENAME_MAX_LENGTH];

	/*段名*/
	char segment_name[MAX_SEGMENT_NAME];

	/*段的元数据信息文件名*/
	char meta_filename[FILENAME_MAX_LENGTH];

	/*段号*/
	uint16_t sid;

	/*原始数据列*/
	struct data_field_manager* data_field;

	/*索引列们，将列名+索引类型作为key值，index_field_manager作为value值*/
	struct string_map* index_fields;
	
	/*容量*/
	uint32_t row_limit;

	/*段的元数据信息*/
	struct segment_meta_data* meta_data;

	/*删除标记*/
	struct bitmark_manager* del_bitmap;	

	/*压缩比，需要从配置文件里读取*/
	uint32_t hash_compress_num;
};


/**
  * 段的初始化函数
  * @param  config segment的配置
  * @param mem_pool 内存池模块
  * @return 返回segment_info结构
  **/ 
struct segment_manager* segment_init(struct segment_config* config, MEM_POOL* mem_pool);



/**
  * 根据列名和索引类型，获取索引列的实例
  * @param  segment的配置
  * @param  field_name 列名
  * @param  index_type 索引类型
  * @return 返回index_field_manager结构
  **/ 
struct index_field_manager* segment_get_index_instance(struct segment_manager* segment,char* field_name,enum index_key_alg index_type);


/**
  * 向指定的段插入一行原始数据记录
  * @param  segment 段信息
  * @param  rdata 一行数据信息
  * @param  docid 行号
  * @return 成功返回0，失败返回<0
  **/ 
int32_t segment_data_insert(struct segment_manager* segment,struct row_data* rdata,uint32_t docid, MEM_POOL *mem_pool);



/**
  * 向指定的段更新一列原始数据记录
  * @param  segment 段信息
  * @param  field_name 列名
  * @param  docid 行号
  * @param  new_data 新的列数据
  * @param  old_data 老的数据
  * @param  mem_pool 内存池
  * @return 成功返回0，失败返回<0
  **/ 
int32_t segment_data_update(struct segment_manager* segment,
						  uint32_t docid, 
						  struct low_data_struct* new_data,
				   		  struct low_data_struct** old_data,
				   		  MEM_POOL* mem_pool);



/**
  * 向指定的段更新一列索引数据记录，只有filter索引支持
  * @param  segment 段信息
  * @param  field_name 列名
  * @param  docid 行号
  * @param  new_data 新的列数据
  * @param  old_data 老的数据
  * @param  mem_pool 内存池
  * @return 成功返回0，失败返回<0
  **/ 
int32_t segment_index_update(struct segment_manager* segment,
						   uint32_t docid, 
						   struct low_data_struct* new_data,
						   struct low_data_struct** old_data,
						   MEM_POOL* mem_pool);


/**
  * 根据列名和docid查找对应的原始值
  * @param  segment 段信息
  * @param  field_name 列名
  * @param  docid 
  * @param  mem_pool 内存池
  * @return 成功返回low_data_struct，失败返回NULL
  **/ 
struct low_data_struct* segment_data_query_col(struct segment_manager* segment,char* field_name,uint32_t docid,MEM_POOL* mem_pool);



/**
  * 根据docid查找整个一行的数据
  * @param  segment 段信息
  * @param  docid
  * @param  mem_pool 内存池
  * @return 成功返回row_data，失败返回NULL
  **/ 
struct row_data* segment_data_query_row(struct segment_manager* segment,uint32_t docid,MEM_POOL* mem_pool);


/**
  * 向指定的段插入一列索引记录
  * @param  segment 段信息
  * @param  data 一列数据信息
  * @param  index_type 索引类型
  * @param  docid 行号
  * @return 成功返回0，失败返回<0
  **/ 
int32_t segment_index_insert(struct segment_manager* segment,
						   struct low_data_struct* data,
						   enum index_key_alg index_type, 
						   uint32_t docid, 
						   MEM_POOL *mem_pool);

/**
  * 建立索引
  * @param  segment 段信息
  * @param  field_name 列名
  * @param  index_type 索引类型
  * @return 成功返回0，失败返回<0
  **/ 
int32_t segment_ensure_index(struct segment_manager* segment,char* field_name,enum index_key_alg index_type, MEM_POOL* mem_pool);



/**
  * 删除索引
  * @param  segment 段信息
  * @param  field_name 列名
  * @param  index_type 索引类型
  * @return 成功返回0，失败返回<0
  **/ 
int32_t segment_del_index(struct segment_manager* segment,char* field_name,enum index_key_alg index_type);



/**
  * btree范围查询，满足条件的row id list
  * @param  index
  * @param  range_condition 查询的条件
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list * segment_index_range_query(struct segment_manager* segment, char* field_name,\
		struct db_range_query_condition * range_condition, MEM_POOL* mem_pool);



/**
  * 根据docid来查找字符串hash后的64位整型
  * @param  index
  * @param  docid 行号
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct low_data_struct* segment_index_value_query(struct segment_manager* segment,char* field_name,uint32_t docid,MEM_POOL* mem_pool);



/**
  * 根据value来查找对应的row id lists，等值查询，只有hash索引支持
  * @param  field
  * @param  data 数据
  * @param  row_count当前存储的数量
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list* segment_index_equal_query(struct segment_manager* segment,struct low_data_struct* data, enum index_key_alg index_type, MEM_POOL* mem_pool);


/**
  * 根据value来查找对应的桶下面的数据，等值查询，只有分词索引支持
  * @param  segment
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回uint32，失败返回NULL
  **/ 
uint32_t segment_index_count_query(struct segment_manager* segment,struct low_data_struct* data,MEM_POOL* mem_pool);


/**
  * 段恢复
  * @param  
  **/
int32_t segment_recover(struct segment_manager* segment, uint32_t docid);

/**
  * 对指定的段的索引列进行压缩
  * @param  segment 段信息
  * @param  mem_pool 内存池
  * @return 成功返回0，失败返回<0
  **/
int32_t segment_compress(struct segment_manager* segment,MEM_POOL* mem_pool);


/**
  * 对指定的段的索引列进行压缩切换
  * @param  segment 段信息
  * @return 成功返回0，失败返回<0
  **/
int32_t segment_compress_switch(struct segment_manager* segment);


/**
  * 根据行号进行删除
  * @param  segment 段信息
  * @param  row_id 行号
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
int32_t segment_del_docid(struct segment_manager* segment,uint32_t docid);



/**
  * 判断指定的doc id是否标记为删除
  * @param  segment 段信息
  * @param  docid 行号
  * @return 已删除返回1，未删除返回0 
  **/
int32_t segment_is_docid_deleted(struct segment_manager* segment, uint32_t docid);


/**
  * 插入数据成功时，设置标记
  * @param  segment 段信息
  * @param  docid 行号
  * @return 返回0，失败<0
  **/
int32_t segment_set_docid_inserted(struct segment_manager* segment, uint32_t docid);


/**
  * 插入数据成功时，设置标记
  * @param  segment 段信息
  * @param  flag 段标记
  * @return 
  **/
void segment_set_flag(struct segment_manager* segment, enum segment_stat flag);


/**
  * 获取段标记
  * @param  segment 段信息
  * @return 
  **/
enum segment_stat segment_get_flag(struct segment_manager* segment);


/**
  * 释放底层数据结构，但是不释放内存
  * @param  segment 段信息
  **/
void segment_release(struct segment_manager* segment);


/**
  * 同步memap
  * @param  segment 段信息
  **/
void segment_checkpoint(struct segment_manager* segment);


/**
  * 获取指定段的当前行号
  * @param  segment 段信息
  **/
uint32_t segment_get_rowcount(struct segment_manager* segment);


/**
  * set rowcount to docid + 1
  * @param  segment 段信息
  * @param docid
  **/
void segment_set_rowcount(struct segment_manager* segment, uint32_t docid);


/**
  * 查看指定段是否超过limit
  * @param  segment 段信息
  * @return 超过返回1，否则返回0
  **/
int32_t segment_exceed_limit(struct segment_manager* segment);

/**
 * switch all index's mmap to real file, when segment full
 * @param segment
 */
int segment_mmap_switch( struct segment_manager *segment);

#endif //SEGMENT_H

