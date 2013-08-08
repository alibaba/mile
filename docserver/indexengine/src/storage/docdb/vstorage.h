/*
 * =====================================================================================
 *
 *       Filename:  hi_vstorage.h
 *
 *    Description:  不定长列的存储接口定义，以及管理信息定义，不定长为了和定长接口统一
 *                  添加了一个辅助结构，locate_info，这是一个定长的数组，记录每条记录
 *                  在文件中存储的偏移量以及长度
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

#ifndef VSTORAGE_H
#define VSTORAGE_H

#define VSTORAGE_MMAP_SIZE(vstorage) vstorage->row_limit*sizeof(struct locate_info)+sizeof(uint64_t)
#define VSTORAGE_MMAP_SIZE4(vstorage) vstorage->row_limit*sizeof(struct locate_info4)+sizeof(uint64_t)//for length of 4 bytes
struct locate_info{
	uint64_t offset;
    uint16_t len;
}__attribute__ ((packed));

struct locate_info4{
        uint64_t offset;
        uint32_t len;
}__attribute__ ((packed));

struct vstorage_config{
	char	work_space[FILENAME_MAX_LENGTH];
	char	vstorage_name[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
};


struct vstorage_manager{
	/*用于存储数据信息的文件*/
	char    data_file_name[FILENAME_MAX_LENGTH];
	int32_t	data_fd;

	/*用于存储位置信息的文件，	*/
	char    index_file_name[FILENAME_MAX_LENGTH];
	
	uint32_t  row_limit;
	uint64_t* offset;

	/*存储变长的结构信息*/
	struct locate_info *  loc_info;     /*memap到文件的内存*/
	struct locate_info4 * loc_info4;    /*for length with 4 bytes*/
};


/**
  * vstorage_manager模块的初始化函数，创建文件，创建vsplit_block信息
  * @param  file_info 文件信息
  * @param  mem_pool 内存池
  * @return 返回vsplit_block的指针
  **/ 
  struct vstorage_manager * vstorage_init(struct vstorage_config * config,MEM_POOL* mem_pool);



/**
  * 将value值插入到指定的split_block中
  * @param  vblock信息
  * @param  rdata 需要插入的值，包括值，和长度
  * @param  row_id 指定行号
  * @param  result 也是函数的返回值，包括记录的位置信息
  * @return 成功返回0，失败返回-1
  **/ 
  int32_t vstorage_insert(struct vstorage_manager * vstorage,struct low_data_struct* data,uint32_t docid);


/**
  * 将value值插入到指定的split_block中
  * @param  vblock信息
  * @param  new_data 需要更新的值，包括值，和长度
  * @param  old_data 老的数据项
  * @param  row_id 指定行号
  * @param  result 也是函数的返回值，包括记录的位置信息
  * @return 成功返回0，失败返回-1
  **/ 	
  int32_t vstorage_update(struct vstorage_manager * vstorage,
  					   struct low_data_struct* new_data,
  					   struct low_data_struct** old_data, 
  					   uint32_t docid,
  					   MEM_POOL* mem_pool);


/**
  * 根据指定的row_id，查询low_data_struct值
  * @param  vblock 块信息
  * @param  mem_pool内存池信息
  * @param  row_id 行号
  * @return 成功返回查询到的low_data_struct，否则返回NULL
  **/
  struct low_data_struct*  vstorage_query(struct vstorage_manager * vstorage,uint32_t docid,MEM_POOL* mem_pool);



/**
  * 释放vstorage信息
  * @param  vstorage 需释放的block信息，只是释放文件描述符，不释放内存
  **/
void vstorage_release(struct vstorage_manager* vstorage);


/**
  * 删除数据信息，慎用!!!
  * @param  vstorage 需释放的vstorage信息，只是释放文件描述符，不释放内存
  **/
void vstorage_destroy(struct vstorage_manager* vstorage);



/**
  * 将不定长的文件flush到磁盘中
  * @param  block 
  **/
void vstorage_checkpoint(struct vstorage_manager* vstorage);


/**
 * Recover data to docid. (clear data which >= docid).
 */
int vstorage_recover(struct vstorage_manager *vstorage, uint32_t docid);


#endif /* VSTORAGE_H */
