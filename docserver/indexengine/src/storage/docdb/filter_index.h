/*
 * =====================================================================================
 *
 *       Filename:  hi_filter_index.h
 *
 *    Description:  对不定长和定长的封装，提供统一的存储接口
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
#include "../../common/mem.h"
#include "../../common/file_op.h"
#include "storage.h"
#include "vstorage.h"
#include "rowid_list.h"


#ifndef FILTER_INDEX_H
#define FILTER_INDEX_H


struct filter_index_config{
	char work_space[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
	uint16_t unit_size;
	enum field_types type;
};

struct filter_index_manager{
	//定长时起作用
	struct storage_manager* storage;
	
	//不定长时起作用
	struct vstorage_manager* vstorage;

	/*数据类型，只是为了区分是filter索引还是原始数据存储*/
	enum field_types type;
};


/**
  * init filter结构信息，并根据table_field的信息，初始化定长和变长存储引擎
  * @param  field_info 域信息
  * @param  support_null 定长时需要传入这个参数，从而创建空值标记，不定长则不需要
  * @return 成功返回filter信息，失败返回NULL
  **/ 
struct filter_index_manager* filter_index_init(struct filter_index_config* config, MEM_POOL* mem_pool);


/**
  * 根据docid，向filter底层存储插入一条记录
  * @param  filter 
  * @param  data 要插入的值，包括长度
  * @param  docid 行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t filter_index_insert(struct filter_index_manager * filter_index,struct low_data_struct* data,uint32_t docid);


/**
  * 向filter底层存储更新一条记录，记录信息包含在filter的value字段，以及行号row_id
  * @param  filter 
  * @param  new_rdata 要更新的值，包括长度
  * @param  old_rdata 老的数据项
  * @param  row_id 行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t filter_index_update(struct filter_index_manager * filter_index,
					struct low_data_struct* new_data,
					struct low_data_struct** old_data,
					uint32_t docid,
					MEM_POOL* mem_pool);


/**
  * 向filter底层存储查询一条记录，记录信息包含在filter的value字段，以及行号row_id
  * @param  filter 
  * @param  row_id 行号
  * @param  mem_pool 内存池，用于在不定长时分配返回值内存
  * @return 成功返回low_data_struct值，失败返回NULL
  **/ 
struct low_data_struct* filter_index_query(struct filter_index_manager * filter_index,uint32_t docid,MEM_POOL* mem_pool);



/**
  * 释放filter结构信息，根据定长和不定长的情况分别释放，不释放内存
  * @param  filter 
  **/ 
void filter_index_release(struct filter_index_manager* filter_index);


/**
  * 释放filter结构信息，并删除数据文件，慎用!!
  * @param  filter 
  **/ 
void filter_index_destroy(struct filter_index_manager* filter_index);


/**
  * 同步memap
  * @param  filter 
  **/ 
void filter_index_checkpoint(struct filter_index_manager* filter_index);


/**
 * Recover data to docid. (clear data which >= docid).
 */
int filter_index_recover(struct filter_index_manager *filter_index, uint32_t docid);

#endif /* HI_FILTER_H */

