/*
 * =====================================================================================
 *
 *       Filename:  hi_index_field.h
 *
 *    Description: 索引列，包括hash和filter索引
 *
 *        Version:  1.0
 *        Created: 	2011年09月01日 11时41分55秒 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */

#include "../../common/def.h"
#include "hash_index.h"
#include "filter_index.h"
#include "hashcompress.h"
#include "hash_memcompress.h"
#include "filtercompress.h"
#include "dynamic_hash_index.h"

#ifndef INDEX_FIELD_H
#define INDEX_FIELD_H

#define MAX_FIELD_NAME 100


/*索引状态*/
enum index_field_flag{
	INDEX_FIELD_COMPRESS = 0x01,  /*该索引已被压缩，将拒绝插入*/
    INDEX_FIELD_DEL = 0x02        /*索引已被删除*/
};


/*索引列的配置*/
struct index_field_config{
	/*工作目录*/
	char    work_space[FILENAME_MAX_LENGTH];
	
	/*域名*/
	char    field_name[MAX_FIELD_NAME];

	/*域的索引类型*/
	enum index_key_alg index_type;

	/*压缩比，需要从配置文件里读取*/
	uint32_t hash_compress_num;

	/*行限制*/
	uint32_t  row_limit;

	/* is segment full */
	uint8_t is_full;
};


/*索引列的管理*/
struct index_field_manager{
	/*工作目录*/
	char    work_space[FILENAME_MAX_LENGTH];

	/*域名*/
	char    field_name[MAX_FIELD_NAME];

	/*行限制*/
	uint32_t  row_limit;

	/*域的索引结构*/
	enum index_key_alg index_type;


	/*索引的状态*/
	char stat_filename[FILENAME_MAX_LENGTH];
	enum index_field_flag* flag;

	
	/*--------索引列----------*/
	//filter索引
	struct filter_index_manager* filter_index;

	//hash索引
	struct hash_index_manager* hash_index; 

	//Btree索引
	struct btree * pBtreeIndex;

	//fulltext索引
	struct dyhash_index_manager* dyhash_index;


	/*--------压缩后的索引列-------*/
	//hash压缩
	struct hash_compress_manager* hash_compress;

	//filter压缩
	struct filter_compress_manager* filter_compress;

	uint32_t hash_compress_num;

	//记录当前field最大长度
	char len_filename[FILENAME_MAX_LENGTH];
	
	uint32_t *max_len;
};




/**
  * 索引列的初始化函数，并根据不同的索引类型来初始化不同的结构，目前支持hash和filter
  * @param  config 配置 
  * @param  mem_pool 内存池模块
  * @return 返回index_field_manager结构
  **/ 
struct index_field_manager* index_field_init(struct index_field_config* config,MEM_POOL* mem_pool);

/**
  * 索引列恢复
  * @param  
  **/
int32_t index_field_recover(struct index_field_manager* index_field, uint32_t docid);


/**
  * 根据不同的索引类型来插入指定的值
  * @param  index
  * @param  data 要插入的数据结构
  * @param  docid 行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t index_field_insert(struct index_field_manager* index_field,struct low_data_struct* data,uint32_t docid);



/**
  * 更新一个值，只有filter索引类型支持
  * @param  index
  * @param  new_data 要更新的值
  * @param  old_data 返回老的数据项
  * @param  docid 行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t index_field_update(struct index_field_manager* index_field,
						 struct low_data_struct* new_data,
						 struct low_data_struct** old_data,
						 uint32_t docid,
						 MEM_POOL* mem_pool);


/**
  * btree范围查询，满足条件的row id list
  * @param  index
  * @param  range_condition 查询的条件
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list * index_field_range_query(struct index_field_manager * index_field, \
		struct db_range_query_condition * range_condition, MEM_POOL* mem_pool);



/**
  * 根据docid来查找字符串hash后的64位整型
  * @param  index
  * @param  docid 行号
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct low_data_struct* index_field_value_query(struct index_field_manager* index_field, uint32_t docid, MEM_POOL* mem_pool);



/**
  * 根据value来查找对应的row id lists，等值查询，只有hash索引支持
  * @param  field
  * @param  data 数据
  * @param  row_count当前存储的数量
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list* index_field_equal_query(struct index_field_manager* index_field,struct low_data_struct* data,MEM_POOL* mem_pool);


/**
  * 根据value来查找对应的桶有多少
  * @param  field
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回uint32_t，失败返回0
  **/ 
uint32_t index_field_count_query(struct index_field_manager* index_field, struct low_data_struct* data, MEM_POOL* mem_pool);

/**
  * 对索引列进行压缩
  * @param  field 列信息
  * @param  mem_pool 内存池
  * @return 成功返回MILE_RETURN_SUCCESS，失败返回错误码
  **/ 
int32_t index_field_compress(struct index_field_manager* index_field,MEM_POOL* mem_pool);



/**
  * 对压缩进行导向切换，并删除未压缩的索引列
  * @param  field 列信息
  * @return 成功返回MILE_RETURN_SUCCESS，失败返回错误码
  **/
int32_t index_field_switch(struct index_field_manager* index_field);


/**
  * 释放底层数据结构，但是不释放内存
  *	@param field
  **/
void index_field_release(struct index_field_manager* index_field);


/**
  * 释放底层数据结构，删除数据文件，慎用!!!
  *	@param field
  **/
void index_field_destroy(struct index_field_manager* index_field);


/**
  * 同步memap
  *	@param field
  **/
void index_field_checkpoint(struct index_field_manager* index_field);

/**
 * switch mmap to real file, when segment full
 * @param index_field
 */
int index_mmap_switch(struct index_field_manager *index_field);

#endif // INDEX_FIELD_H

