/*
 * =====================================================================================
 *
 *       Filename:  hi_bitmark.h
 *
 *    Description:  标记位的接口定义，用作删除标记
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
#include "../../common/log.h"
#include "../../common/mem.h"
#include "../../common/file_op.h"

#ifndef BITMARK_H
#define BITMARK_H


#define BITMARK_MMAP_SIZE(bitmark) (bitmark->row_limit/BYTE_SIZE+1)

struct bitmark_config{
	char work_space[FILENAME_MAX_LENGTH];
	char bitmark_name[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
};


struct bitmark_manager{
	/*文件的全路径*/
	char  file_name[FILENAME_MAX_LENGTH];

	char*  mem_mmaped;
	uint32_t row_limit;
};



/**
  * 删除标记的初始化函数
  * @param  config 配置信息
  * @param  mem_pool 内存池
  * @return 返回segment_del_bitmask结构信息，失败返回null
  **/ 
struct bitmark_manager * bitmark_init(struct bitmark_config* config, MEM_POOL* mem_pool);


/**
  * 设置row_id指定的标记，标记该行已经成功插入
  * @param  bitmark   
  * @param  row_id   要设置的bit位
  * @return 0表示成功，<0失败
  **/
int32_t bitmark_set(struct bitmark_manager * bitmark, uint32_t docid);


/**
  * 清除row_id指定的标记，标记该行已经删除
  * @param  bitmark   标记结构信息
  * @param  row_id   要清空的bit位
  * @return 1表示成功，0表示已删除
  **/
int32_t bitmark_clear(struct bitmark_manager * bitmark,uint32_t docid);


/**
  * 获取row_id指定的标记
  * @param  bitmark   标记结构信息
  * @param  row_id   要查询的bit位
  * @return 0表示未删除，1表示已标记为删除，<0失败
  **/
int32_t bitmark_query(struct bitmark_manager * bitmark, uint32_t docid);


/**
  * 将所有的标记位置清0
  * @param  bitmark   标记结构信息
  * @return 0表示成功，<0失败
  **/
int32_t bitmark_reset(struct bitmark_manager * bitmark);


/**
  * 释放bitmark_manager信息，并把内存的数据flush到磁盘中
  * @param  bitmark   释放内存空间
  * @return 
  **/
void bitmark_release(struct bitmark_manager* bitmark);


/**
  * 把内存的数据flush到磁盘中
  * @param  bitmark 释放内存空间
  * @return 
  **/
void bitmark_checkpoint(struct bitmark_manager* bitmark);

/**
 * Recover data to docid. (clear data which >= docid).
 */
int bitmark_recover(struct bitmark_manager *bitmark, uint32_t docid);

/**
 * Return marked bits number.
 */
int bitmark_count(struct bitmark_manager *bitmark);


/**
  * 把数据文件删除，慎用!!!
  * @param  bitmark 释放内存空间
  * @return 
  **/
void bitmark_destroy(struct bitmark_manager* bitmark);


#endif

