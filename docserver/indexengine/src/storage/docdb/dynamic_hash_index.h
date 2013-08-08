/*
 * =====================================================================================
 *
 *       Filename:  dynamic_hash_index.h
 *
 *    Description:  动态hash索引
 *
 *        Version:  1.0
 *        Created:  2012年09月14日 10时35分51秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi (zian), yunliang.shi@alipay.com
 *   Organization:  
 *
 * =====================================================================================
 */

#include "../../common/def.h"
#include "../../common/profiles.h"
#include "../../common/file_op.h"
#include "../../common/hash.h"
#include "doclist.h"
#include "set_operator.h"

#ifndef DYNMIC_HASH_H
#define DYNMIC_HASH_H

#define DOC_MULTIPLE_NUM 2

#define CONFLICT_RETRY_NUM 100

#define DYHASH_INDEX_MMAP_SIZE(dyhash_index) (dyhash_index->hashmod)*sizeof(struct dyhash_bucket)

struct dyhash_bucket{
    /*hash后的值*/
	uint64_t	hash_value;

	/*属于该hash的docid数目*/
	uint32_t  count;
	
	/*记录doclist的偏移地址*/
	uint32_t offset;
}__attribute__ ((packed));



struct dyhash_index_config{
	/*工作路径*/
	char work_space[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
	uint8_t is_full;
};

struct dyhash_signleindex_config{
	uint16_t index;
	char work_space[FILENAME_MAX_LENGTH];
	uint32_t hash_mod; 
	uint8_t is_full;
	uint8_t is_create;
};

struct dyhash_single_index{
	/*hash的模*/
	uint32_t	hashmod;

	/*记录数限制*/
	uint32_t	limit;

	/*编号*/
	uint16_t index;

    /*hash索引存储的文件名和描述符*/
	char    file_name[FILENAME_MAX_LENGTH];

	/*内存映射*/
	struct dyhash_bucket* mem_mmaped;


	/*doclist结构*/
	struct doclist_manager* doclist;

	/* 扩展hash */
	struct dyhash_single_index* next;
};


struct dyhash_index_manager{
	/*索引头，是一个链表，有扩展*/
	struct dyhash_single_index* head; 

	/*指针尾部*/
	struct dyhash_single_index* tail;

	/* 工作目录 */
	char work_space[FILENAME_MAX_LENGTH];

	/*hash的模*/
	uint32_t	hashmod;

	/*内存池*/
	MEM_POOL* mem_pool;

	/* segment is full */
	volatile uint8_t is_full;

	/* protect checkpoint, release and mmap switch */
	pthread_mutex_t mmap_lock;
	
};


/* 
 *  扩展hash索引初始化函数，根据文件名，初始化扩展hash桶
 *  @param config配置
 *  @param mem_pool 内存池
 *  @return 返回dyhash_index_manager，否则为NULL
 */
struct dyhash_index_manager * dyhash_index_init(struct dyhash_index_config* config, MEM_POOL* mem_pool);




/**
  * hash index索引恢复, 清除大于等于docid之后的数据。
  * @param 
  * @return
  **/ 
int32_t dyhash_index_recover(struct dyhash_index_manager* dyhash_index, uint32_t docid);

/**
  * hash index插入一个值，通过hash找到自己的桶，如果桶被占，则往下寻找直至遍历一圈
  * @param  dyhash_index dyhash_index值
  * @param  data 要插入的值
  * @param  doc_id行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t dyhash_index_insert(struct dyhash_index_manager* dyhash_index,struct low_data_struct* data,uint32_t docid);


/**
  * 根据data的hash值取模，定位到具体的桶，如果该桶没有值，则不存在，则往后遍历，碰到桶没有值，那肯定不会有了，最坏的情况下，就是遍历一圈，而这个时候
  * hash已经发生恶性冲突的情况了，获取到doclist的头后，然后封装成rowid_list结构返回给上层
  * @param  dyhash_index dyhash_index值
  * @param  data 要查询的值
  * @param  mem_pool内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list* dyhash_index_query(struct dyhash_index_manager* dyhash_index,struct low_data_struct* data,MEM_POOL* mem_pool);



/* 
 *  获取一个分词下面的docid数量
 *  @param
 *  @return
 */
uint32_t dyhash_index_count_query(struct dyhash_index_manager* dyhash_index, struct low_data_struct* data, MEM_POOL* mem_pool);



/**
  * 释放结构，主要关闭文件，syns memap到文件中
  * @param  dyhash_index 需要释放的结构
  **/
void dyhash_index_release(struct dyhash_index_manager* dyhash_index);


/**
  * 将memap数据同步
  * @param  dyhash_index 需要释放的结构
  **/
void dyhash_index_checkpoint(struct dyhash_index_manager* dyhash_index);


/**
 * switch mmap to real file when segment full
 * @param dyhash_index
 */
int dyhash_index_mmap_switch(struct dyhash_index_manager *dyhash_index);



#endif



