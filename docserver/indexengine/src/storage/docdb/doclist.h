/*
 * =====================================================================================
 *
 *       Filename:  hi_doclist.h
 *
 *    Description:  以链表形式，存储所有hash值相同的row id，提供插入以及遍历接口
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
#include "storage.h"
#include "vstorage.h"
#include "../../common/profiles.h"


#ifndef DOCLIST_H
#define DOCLIST_H


#define DOCLIST_MMAP_SIZE(doclist) doclist->row_limit*sizeof(struct doc_row_unit)+sizeof(uint32_t)

/*根据offset获取struct doc_row_unit结构*/
#define GET_DOC_ROW_STRUCT(doclist,doc_id) (struct doc_row_unit*)(doclist->mem_mmaped+sizeof(uint32_t) + doc_id * sizeof(struct doc_row_unit)) 

#define NEXT_DOC_ROW_STRUCT(doclist, next) (struct doc_row_unit*)(doclist->mem_mmaped + next)

#define GET_OFFSET(doc_id) (sizeof(uint32_t) + doc_id * sizeof(struct doc_row_unit))

/*存储doc_id的结构，以链表的形式存储*/
struct doc_row_unit{
	uint32_t	doc_id;
	uint32_t  next;
}__attribute__ ((packed));


struct doclist_config{
	char work_space[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
	uint8_t is_full;
};


struct doclist_manager{
	/*文件名和描述符*/
	char    file_name[FILENAME_MAX_LENGTH];

	/*最大存储的row数量*/
	uint32_t	row_limit;

    /*原来是偏移，目前改为版本，版本号最高位为1*/
	uint32_t version;

	/*当前偏移量*/
	uint32_t* cur_offset;

	/* segment is full */
	volatile uint8_t is_full;
	
	char* mem_mmaped; /*memap到内存中的数据结构*/
};


/**
  * doclist的初始化函数，根据上层传入的不同数据类型，来初始化定长或者不定长的存储块
  * @param  file_info 工作目录，以及limit等信息
  * @param  mem_pool 内存池
  * @return 返回segment_doclist信息
  **/ 
struct doclist_manager* doclist_init(struct doclist_config* config,MEM_POOL* mem_pool);



/**
  * doclist v2的初始化函数，根据上层传入的不同数据类型，来初始化定长或者不定长的存储块
  * @param  config 工作目录，以及limit等信息
  * @param  is_create 是否创建
  * @param  index 文件索引
  * @param  mem_pool 内存池
  * @return 返回segment_doclist信息
  **/ 
struct doclist_manager* doclist_init_v2(struct doclist_config* config, uint8_t is_create, uint16_t index, MEM_POOL* mem_pool);



/**
  * V2 插入一个值，这里的head需要注意下，hash层传入的head_offset可能为0，说明hash还没有冲突，如果head_offset不为0，则采用头插法，hash更新头
  * @param  doclist 
  * @param  doc_id 行号
  * @param  head_offset 见函数说明
  * @return 返回 偏移量
  **/ 
uint32_t doclist_insert_v2(struct doclist_manager* doclist,uint32_t doc_id,uint32_t head_offset, uint32_t bucket_no);


/**
  * 插入一个值，这里的head需要注意下，hash层传入的head_offset可能为0，说明hash还没有冲突，如果head_offset不为0，则采用头插法，hash更新头
  * @param  doclist 
  * @param  doc_id 行号
  * @param  head_offset 见函数说明
  * @return 返回 偏移量
  **/ 
uint32_t doclist_insert(struct doclist_manager* doclist,uint32_t doc_id,uint32_t head_offset, uint32_t bucket_no);


/**
  * 把memap到内存的数据结构flush到磁盘
  * @param  doclist
  * @return 成功返回0 失败返回-1
  **/ 
int32_t doclist_checkpoint(struct doclist_manager* doclist);

/**
  * 把memap到内存的数据结构flush到磁盘
  * @param  doclist
  * @param  next 
  * @return 返回next指向的doc_row_unit
  **/ 
struct doc_row_unit *doclist_next(struct doclist_manager* doclist, uint32_t next);

/**
  * 释放doclist的文件，并把memap到内存的数据结构flush到磁盘，根据类型释放定长或不定长的数据结构
  * @param  doclist
  * @return 
  **/ 
void doclist_release(struct doclist_manager* doclist);


/**
  * 慎用!!!!!释放doclist的文件，并把memap到内存的数据结构flush到磁盘，删除对应的索引和数据结构
  * @param  doclist
  * @return 
  **/ 
void doclist_destroy(struct doclist_manager* doclist);

/**
 * switch mmap to real file when segment full
 * @param doclist
 */
int doclist_mmap_switch(struct doclist_manager *doclist);

#endif /* DOCLIST_H */


