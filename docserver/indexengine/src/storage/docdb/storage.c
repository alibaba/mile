/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_storage.c
 *
 *    Description:  定长列的存储接口实现，定长列是通过memap机制来存储的
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

#include "storage.h"


struct storage_manager * storage_init(struct storage_config* config,MEM_POOL* mem_pool)
{
   struct storage_manager* storage = 
   				(struct storage_manager *)mem_pool_malloc(mem_pool,sizeof(struct storage_manager));

   assert(storage != NULL);
   
   memset(storage,0,sizeof(struct storage_manager));

   //初始化block信息
   storage->value_size = config->unit_size;
   storage->row_limit = config->row_limit;

   //拼接:文件名
   sprintf(storage->file_name,"%s/%s.dat",config->work_space,config->storage_name);

   //mmap映射处理
   storage->mem_mmaped =(char*)get_mmap_memory(storage->file_name,STORAGE_MMAP_SIZE(storage)); 

   assert(storage->mem_mmaped != NULL);

   //初始化空值标志位
   struct bitmark_config bitmark_config;
   strcpy(bitmark_config.work_space,config->work_space);
   strcpy(bitmark_config.bitmark_name,"null");
   bitmark_config.row_limit = config->row_limit;

   storage->null_bitmark = bitmark_init(&bitmark_config,mem_pool);
   
   return storage;   
}


int32_t storage_insert(struct storage_manager * storage, struct low_data_struct * data, uint32_t docid)
{
   //判断是否为空
   //如果支持空值的话，那么如果存储的值不为空的话， 则需要把标记为置1；
   //如果是空值的话，则不需要对标记进行操作，默认为空值。
   if(data->len != 0)
   	{
		bitmark_set(storage->null_bitmark,docid);
   	}
   if(data->len == 0)
   	{
   		return 0;
   	}

   //拷贝存储的数据值，这里注意数据的长度不是以rdata的len为准，而是以block的value_size为准
   memcpy(storage->mem_mmaped+storage->value_size*docid,data->data,data->len);
   return 0;
}


struct low_data_struct * storage_query(struct storage_manager * storage, uint32_t docid,MEM_POOL* mem_pool)
{
    struct low_data_struct* result = NULL;

	result = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
	memset(result,0,sizeof(struct low_data_struct));

	result->data = mem_pool_malloc(mem_pool,storage->value_size);
	memset(result->data,0,storage->value_size);

	//如果支持空值，且标记为0的话，说明此rowid的数据为空
	if(bitmark_query(storage->null_bitmark,docid) == 1)
	{
		return result;
	}

	//根据row_id*每个数据的长度，可以算到row_id对应的数据
	memcpy(result->data,storage->mem_mmaped+docid*storage->value_size,storage->value_size);
	result->len = storage->value_size;
	return result;
}


int32_t storage_update(struct storage_manager * storage,
					struct low_data_struct * new_data,
					struct low_data_struct** old_data, 
					uint32_t docid,
					MEM_POOL* mem_pool)
{	
	//如果更新的数据的长度为0，并且此列支持空值，则将标记位置为0
	if(new_data->len == 0 )
	{
		//获取老的数据
		*old_data = storage_query(storage,docid,mem_pool);
		
		bitmark_clear(storage->null_bitmark,docid);
		return 0;
	}

	//先获取老的数据
	*old_data = storage_query(storage,docid,mem_pool);

	//覆盖老的数据
    memcpy(storage->mem_mmaped+docid*storage->value_size, new_data->data, storage->value_size);

	//如果支持空值，则需要把标记位设置为1
	bitmark_set(storage->null_bitmark,docid);
	return 0;
}



void storage_release(struct storage_manager * storage)
{
	if(storage->mem_mmaped != NULL)
	{
		//同步到磁盘
		msync(storage->mem_mmaped,STORAGE_MMAP_SIZE(storage),MS_SYNC);        // make sure synced
        munmap(storage->mem_mmaped, STORAGE_MMAP_SIZE(storage));
	}

	//如果支持空值，则还需要把标记位释放
	if(storage->null_bitmark != NULL)
		bitmark_release(storage->null_bitmark);
	return;
}


void storage_destroy(struct storage_manager * storage)
{
	if(storage->mem_mmaped != NULL)
	{
        munmap(storage->mem_mmaped, STORAGE_MMAP_SIZE(storage));
	}

	remove(storage->file_name);

	//如果支持空值，则还需要把标记位释放
	if(storage->null_bitmark != NULL)
		bitmark_destroy(storage->null_bitmark);
	return;


}



void storage_checkpoint(struct storage_manager * storage)
{
	if(storage->mem_mmaped != NULL)
	{
		//同步到磁盘
		msync(storage->mem_mmaped,STORAGE_MMAP_SIZE(storage),MS_SYNC);        // make sure synced
	}

	//如果支持空值，则还需要把标记位释放
	bitmark_checkpoint(storage->null_bitmark);
	
	return;
}

int storage_recover(struct storage_manager *storage, uint32_t docid)
{
	if(docid >= storage->row_limit) {
		log_error("invalid docid, docid %u, row_limit %u", docid, storage->row_limit);
		return ERROR_EXCEED_LIMIT;;
	}

	if(bitmark_recover(storage->null_bitmark, docid) != 0) {
		log_error( "recover null bitmark failed" );
		return -1;
	}

	memset(storage->mem_mmaped + storage->value_size * docid, 0, (storage->row_limit - docid) * storage->value_size );

	return 0;
}
