/*
 * =====================================================================================
 *
 *       Filename:  hi_storage.h
 *
 *    Description:  定长列的存储接口定义，以及管理信息定义
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
#include "../../common/profiles.h"
#include "bitmark.h"

#ifndef STORAGE_H
#define STORAGE_H

#define STORAGE_MMAP_SIZE(storage) storage->value_size*storage->row_limit

struct storage_config{
	char	work_space[FILENAME_MAX_LENGTH];
	char	storage_name[FILENAME_MAX_LENGTH];
	uint16_t  unit_size;
	uint32_t  row_limit;
};


struct storage_manager{
	char   file_name[FILENAME_MAX_LENGTH];	/*文件的全路径*/
	uint16_t  value_size;
	uint32_t  row_limit;
	char *  mem_mmaped;     /*memap到文件的内存*/

	/*记录为空值的rowid*/
	struct bitmark_manager* null_bitmark;
};


/**
  * storage_manager模块的初始化函数，memap到内存中
  * @param  config 配置信息
  * @param mem_pool 内存池模块
  * @return 返回split_block的二维数组
  **/ 
struct storage_manager * storage_init(struct storage_config* config,MEM_POOL* mem_pool);


/**
  * 将data值插入到指定的storage中
  * @param  storage 块信息
  * @param  data 需要插入的值，其中rdata的len字段定长时不起作用
  * @param  docid 指定行号
  * @return 成功返回行号，失败返回-1
  **/ 
int32_t storage_insert(struct storage_manager * storage, struct low_data_struct * data, uint32_t docid);


/**
  * 根据row_id，计算出偏移地址，查询low_data_struct，这个low_data_struct是不需要释放的
  * @param  block 块信息
  * @param  row_id 行号
  * @param  mem_pool 内存池
  * @return 成功返回查询到的值low_data_struct，失败返回NULL
  **/
struct low_data_struct * storage_query(struct storage_manager * storage, uint32_t docid,MEM_POOL* mem_pool);


/**
  * 根据docid，计算出偏移地址，更新low_data_struct
  * @param  block 块信息
  * @param  new_data 更新的内容
  * @param  old_data 老的数据项
  * @param  docid 行号
  * @return 成功返回查询到的值，失败返回NULL
  **/
int32_t storage_update(struct storage_manager * storage,
				    struct low_data_struct * new_data,
				    struct low_data_struct** old_data, 
				    uint32_t docid,
				    MEM_POOL* mem_pool);


/**
  * 释放storage信息
  * @param  storage 需释放的storage信息
  **/
void storage_release(struct storage_manager * storage);

/**
  * 删除文件信息
  * @param  storage 需释放的storage信息
  **/
void storage_destroy(struct storage_manager * storage);



/**
  * 将内存flush到磁盘中
  * @param  storage storage信息
  **/
void storage_checkpoint(struct storage_manager * storage);

/**
 * Recover data to docid. (clear data which >= docid).
 */
int storage_recover(struct storage_manager *storage, uint32_t docid);

#endif
