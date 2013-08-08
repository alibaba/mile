/*
 * =====================================================================================
 *
 *       Filename:  hi_hash_index.h
 *
 *    Description:  一个数组形式的简单的hash结构，以及插入等值查询接口定义
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
#include "../../common/hash.h"
#include "doclist.h"
#include "rowid_list.h"

#ifndef HASH_INDEX_H
#define HASH_INDEX_H

//加1是为了留出一个空间，用于放空值
//这里0~hashmod-1都是用来做二级hash，但是第hashmod个桶，是用来存放所有为空值的项
#define HASH_INDEX_MMAP_SIZE(hash_index) (hash_index->hashmod+1)*sizeof(struct hash_bucket)


struct hash_bucket{
    /*hash后的值*/
	uint64_t	hash_value;
	
	/*记录doclist的偏移地址*/
	uint32_t offset;
}__attribute__ ((packed));


struct hash_index_config{
	/*工作路径*/
	char work_space[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
	uint8_t is_full;
};

struct hash_index_manager{
	/*hash的模*/
	uint32_t	hashmod;

	/*记录数限制*/
	uint32_t	limit;

    /*hash索引存储的文件名和描述符*/
	char    file_name[FILENAME_MAX_LENGTH];

	struct hash_bucket*	mem_mmaped;

	/* segment is full */
	volatile uint8_t is_full;

	/* protect checkpoint, release and mmap switch */
	pthread_mutex_t mmap_lock;

	/*doclist结构*/
	struct doclist_manager* doclist;
};


/**
  * hash index初始化函数，主要初始化hash索引结构，以及doclist结构
  * @param  file_info 上层配置信息
  * @param  mem_pool 内存池
  * @return 成功返回segment_field_hashindex结构 失败返回NULL
  **/ 
struct hash_index_manager* hash_index_init(struct hash_index_config* config,MEM_POOL* mem_pool);


/**
  * hash index索引恢复, 清除大于等于docid之后的数据。
  * @param 
  * @return
  **/ 
int32_t hash_index_recover(struct hash_index_manager* hash_index, uint32_t docid);

/**
  * hash index插入一个值，通过hash找到自己的桶，如果桶被占，则往下寻找直至遍历一圈
  * @param  hash_index hash_index值
  * @param  data 要插入的值
  * @param  doc_id行号
  * @return 成功返回0，失败返回-1
  **/ 
int32_t hash_index_insert(struct hash_index_manager* hash_index,struct low_data_struct* data,uint32_t docid);


/**
  * 根据data的hash值取模，定位到具体的桶，如果该桶没有值，则不存在，则往后遍历，碰到桶没有值，那肯定不会有了，最坏的情况下，就是遍历一圈，而这个时候
  * hash已经发生恶性冲突的情况了，获取到doclist的头后，然后封装成rowid_list结构返回给上层
  * @param  hash_index hash_index值
  * @param  data 要查询的值
  * @param  mem_pool内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list* hash_index_query(struct hash_index_manager* hash_index,struct low_data_struct* data,MEM_POOL* mem_pool);


/**
  * 获取该桶下面的所有的doclist
  * @param  hash_index 需要释放的结构
  * @param  doc_row 头
  * @param  mem_pool内存池
  * @return 返回所有的docid列表
  **/
struct rowid_list* get_rowid_list(struct hash_index_manager* hash_index,struct doc_row_unit* doc_row,MEM_POOL* mem_pool);


/**
  * 慎用!!!!!!!!释放结构，主要关闭文件，syns memap到文件中，并删除文件
  * @param  hash_index 需要释放的结构
  **/
void hash_index_destroy(struct hash_index_manager* hash_index);


/**
  * 释放结构，主要关闭文件，syns memap到文件中
  * @param  hash_index 需要释放的结构
  **/
void hash_index_release(struct hash_index_manager* hash_index);


/**
  * 将memap数据同步
  * @param  hash_index 需要释放的结构
  **/
void hash_index_checkpoint(struct hash_index_manager* hash_index);


/**
 * switch mmap to real file when segment full
 * @param hash_index
 */
int hash_index_mmap_switch(struct hash_index_manager *hash_index);


#endif /* HASH_INDEX_H */
