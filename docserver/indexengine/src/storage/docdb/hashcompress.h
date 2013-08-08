/*
 * =====================================================================================
 *
 *       Filename:  hi_hashcompress.h
 *
 *    Description:  当一个段满的时候，对hash列进行压缩，减小内存开销
 *
 *        Version:  1.0
 *        Created: 	2011年08月18日 11时41分55秒 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */

#ifndef HASHCOMPRESS_H
#define HASHCOMPRESS_H

#ifndef USE_MEM_COMPRESS

#include "../../common/def.h"
#include "hash_index.h"
#include "doclist.h"
#include "rowid_list.h"
#include "../../common/hash.h"

#define BUF_NUM 10000

#define HASH_SEEKS_MEMAP_SIZE(hash_compress) (hash_compress->hash_mod/hash_compress->hash_compress_num+1)*sizeof(struct hash_seeks)+sizeof(uint32_t)

struct hash_compress_config{
	/*工作路径*/
	char work_space[FILENAME_MAX_LENGTH];
	uint32_t row_limit;
	uint32_t hash_compress_num;
};

//一个单元的存储结构，放在磁盘上的
struct hash_docs{
	uint64_t hash_value;
	
	//定位docids
	uint32_t num;
	uint32_t docids[0];
}__attribute__ ((packed));


//每10个hash docs记录一个偏移量,存储在内存中
struct hash_seeks{
	uint64_t hash_value;

	//10个hash_docs的偏移地址以及长度
	uint32_t offset;
	uint32_t len;
}__attribute__ ((packed));



struct hash_compress_manager{
	char index_file_name[FILENAME_MAX_LENGTH];
	char data_file_name[FILENAME_MAX_LENGTH];

	/*数据文件的描述符字*/
	int data_fd;

	/*索引的结构，映射到内存中*/
	struct hash_seeks* index_mmap;

	/*索引数量*/
	uint32_t* index_count;

	/*hash桶*/
	uint32_t hash_mod;

	uint32_t row_limit;

	/*hash压缩的比例*/
	uint32_t hash_compress_num;
};




/**
  * hash压缩初始化，用于加载已经初始化的数据文件
  * @param  config 配置信息，包括工作目录，hash桶等
  * @param  mem_pool内存池
  * @return 成功返回segment_hash_compress结构，失败返回NULL
  **/ 
struct hash_compress_manager* hash_commpress_init(struct hash_compress_config* config,MEM_POOL* mem_pool);



/**
  * 对hash列进行压缩，构建压缩的二分查找索引。
  * @param  hash_field 要压缩的hash列索引
  * @param  hash_compress_num 每隔多少个hash桶建一个二分查找索引
  * @param  mem_pool内存池
  * @return 成功返回segment_hash_compress，失败返回NULL
  **/ 
struct hash_compress_manager* hash_compress_load(struct hash_index_manager* hash_index,uint32_t hash_compress_num,MEM_POOL* mem_pool);



/**
  * hash compress的压缩查询，首先根据二分查找法，定位到在哪个块，然后从磁盘中读取数据
  * @param  hash_compress 列结构信息
  * @param  data 要查询的值
  * @param  mem_pool内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct rowid_list* hash_compress_query(struct hash_compress_manager* hash_compress,struct low_data_struct* data,MEM_POOL* mem_pool);



/**
  * 释放结构，主要关闭文件
  * @param  hash_compress 需要释放的结构
  **/
void hash_compress_release(struct hash_compress_manager* hash_compress);


/**
  * 释放结构，主要关闭文件，以及删除数据文件，慎用!!!
  * @param  hash_compress 需要释放的结构
  **/
void hash_compress_destroy(struct hash_compress_manager* hash_compress);



#endif // USE_MEM_COMPRESS

#endif // HASHCOMPRESS_H
