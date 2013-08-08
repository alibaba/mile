/*
 * =====================================================================================
 *
 *       Filename:  hi_data_field.h
 *
 *    Description: 数据列
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

#ifndef DATA_FIELD_H
#define DATA_FIELD_H

#include "../../common/def.h"
#include "filter_index.h"


/*索引列的配置*/
struct data_field_config{
	/*工作目录*/
	char    work_space[FILENAME_MAX_LENGTH];
	
	/*行限制*/
	uint32_t  row_limit;
};


/*数据列的管理*/
struct data_field_manager{
	/*工作目录*/
	char    work_space[FILENAME_MAX_LENGTH];

	/*行限制*/
	uint32_t  row_limit;

	
	/*--------索引列----------*/
	//filter索引
	struct filter_index_manager* filter_data;
};


/**
  * 索引列的初始化函数，并根据不同的索引类型来初始化不同的结构，目前支持hash和filter
  * @param  config 配置 
  * @param  mem_pool 内存池模块
  * @return 返回index_field_manager结构
  **/ 
struct data_field_manager* data_field_init(struct data_field_config* config,MEM_POOL* mem_pool);


/**
  * 根据不同的索引类型来插入指定的值
  * @param  data_field
  * @param  rdata 要插入的一行数据，每列包括列名以及数据
  * @param  docid 行号
  * @param  mem_pool 内存池
  * @return 成功返回0，失败返回-1
  **/ 
int32_t data_field_insert(struct data_field_manager* data_field,struct row_data* rdata,uint32_t docid,MEM_POOL* mem_pool);


/**
  * 更新一个值，只有filter索引类型支持
  * @param  data_field
  * @param  new_data 要更新的值
  * @param  old_data 返回老的数据项
  * @param  docid 行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t data_field_update(struct data_field_manager* data_field,
						struct low_data_struct* new_data,
						struct low_data_struct** old_data,
						uint32_t docid,
						MEM_POOL* mem_pool);



/**
  * 根据列名和docid查找对应的原始值
  * @param  data_field
  * @param  field_name 列名
  * @param  docid 
  * @param  mem_pool 内存池
  * @return 成功返回low_data_struct，失败返回NULL
  **/ 
struct low_data_struct* data_field_query_col(struct data_field_manager* data_field,char* field_name,uint32_t docid,MEM_POOL* mem_pool);


/**
  * 根据docid查找整个一行的数据
  * @param  data_field
  * @param  docid
  * @param  mem_pool 内存池
  * @return 成功返回row_data，失败返回NULL
  **/ 
struct row_data* data_field_query_row(struct data_field_manager* data_field,uint32_t docid,MEM_POOL* mem_pool);


/**
  * 释放底层数据结构，但是不释放内存
  *	@param data_field
  **/
void data_field_release(struct data_field_manager* data_field);


/**
  * 同步memap
  *	@param data_field
  **/
void data_field_checkpoint(struct data_field_manager* data_field);

/**
 * Recover data to docid. (clear data which >= docid).
 */
int data_field_recover(struct data_field_manager *data_field, uint32_t docid);


#endif // DATA_FIELD_H


