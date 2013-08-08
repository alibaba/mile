/*
 * =====================================================================================
 *
 *       Filename:  hi_hashindex.c
 *
 *    Description:  一个数组形式的简单的hash结构，以及插入等值查询接口实现
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

#include "hash_index.h"


struct hash_index_manager* hash_index_init(struct hash_index_config* config,MEM_POOL* mem_pool)
{
	struct hash_index_manager* hash_index = (struct hash_index_manager*)mem_pool_malloc(mem_pool,sizeof(struct hash_index_manager));   

	assert(hash_index != NULL);
	
	memset(hash_index,0,sizeof(struct hash_index_manager));

	//赋值数据类型
	hash_index->limit = config->row_limit;
	
	//桶直接和row_limit一致
	hash_index->hashmod = config->row_limit;

	hash_index->is_full = config->is_full;
	pthread_mutex_init(&hash_index->mmap_lock, NULL);

	//初始化文件
	sprintf(hash_index->file_name,"%s/hash.idx",config->work_space);

	if( config->is_full || mile_conf.all_mmap ) {
		//mmap映射处理
		hash_index->mem_mmaped =(struct hash_bucket*)get_mmap_memory(hash_index->file_name,HASH_INDEX_MMAP_SIZE(hash_index)); 
	} else {
		// alloc memory like malloc
		hash_index->mem_mmaped = (struct hash_bucket*)alloc_file_memory(hash_index->file_name,HASH_INDEX_MMAP_SIZE(hash_index));
	}

	assert(hash_index->mem_mmaped != NULL);

	//初始化doclist
	struct doclist_config dconfig;
	dconfig.row_limit = hash_index->limit;
	dconfig.is_full = config->is_full;
	strcpy(dconfig.work_space,config->work_space);
	hash_index->doclist = doclist_init(&dconfig, mem_pool);
	
	assert(hash_index->doclist != NULL);
	
	return hash_index;
}


int32_t hash_index_recover(struct hash_index_manager* hash_index, uint32_t docid)
{
	//判断是否需要恢复，如果数据版本过久，则不需要恢复
	if(hash_index->doclist->version != DATA_STOAGE_VERSION)
	{
		log_warn("数据版本过期 %"PRIu32, hash_index->doclist->version);
		return 0;
	}

	
	//清空doclist在docid及后的数据
	memset(hash_index->doclist->mem_mmaped + sizeof(uint32_t) + docid * sizeof(struct doc_row_unit), 0, (hash_index->doclist->row_limit - docid) * sizeof(struct doc_row_unit));

	//清空hash索引所有大于docid的值的 offset
	struct hash_bucket* hbucket = NULL;
	uint32_t max_offset = sizeof(struct doc_row_unit)*docid +sizeof(uint32_t);
	uint32_t i;
	for(i=0; i<hash_index->hashmod + 1; i++)
	{
		hbucket = hash_index->mem_mmaped + i;
		if(hbucket->offset >= max_offset )
			hbucket->offset = 0;
	}

	if(docid > 0) {
		struct doc_row_unit* doc = NULL;
		uint32_t offset = 0;
		uint32_t bucket_no = 0;
		for(i = docid - 1; ; i--)
		{	
			doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);

			if(doc->doc_id == 0 && doc->next == 0)
			{
				if(i == 0)
					break;
				else
					continue;
			}	

			while(!(doc->next & 0x80000000))
			{
				doc = NEXT_DOC_ROW_STRUCT(hash_index->doclist, doc->next);
			}
			
			bucket_no = doc->next & 0x7fffffff;
			hbucket = hash_index->mem_mmaped + bucket_no;
			offset = i *sizeof(struct doc_row_unit) + sizeof(uint32_t);
			if(hbucket->offset < offset)
				hbucket->offset = offset;
			
			if(i == 0)
				break;
		}
	}

	// clear hash_value if doclist is empty.
	for(i=0; i<hash_index->hashmod + 1; i++)
	{
		hbucket = hash_index->mem_mmaped + i;
		if(hbucket->offset == 0)
			hbucket->hash_value = 0;
	}

	return 0;
}




//两种情况
//1.空值情况
//则直接定位到第hashmod个桶
//2.非空值情况
//则根据插入的值做hash，通过hash值对hashmod取模，从而定位到存储到哪个桶上，如果这个桶的hash value相等则直接插入，如果
//不等，则说明冲突了，则需要往后继续找到自己的桶
int32_t hash_index_insert(struct hash_index_manager* hash_index,struct low_data_struct* data,uint32_t docid)
{
	struct hash_bucket* bucket;
	uint64_t hash_value;
	uint32_t loc;
	uint32_t i;
	uint32_t offset;

	//根据value做一次hash
	PROFILER_BEGIN("get hash value");
	hash_value = get_hash_value(data);
	PROFILER_END();
	
	//判断是否为空值
	if(data->len == 0)
	{
		bucket = hash_index->mem_mmaped+hash_index->hashmod;

		//哈希这个位置没有这个值存在，调用doclist接口插入
		if(bucket->hash_value == 0)
		{
			if((offset = doclist_insert(hash_index->doclist,docid,0, hash_index->hashmod)) == 0)
				return ERROR_INSERT_REPEAT;
			
			//注意hash_value一定要在offset之后写，因为并发查询的时候会先去读value，判断value是否为0
			Mile_AtomicSetPtr(&bucket->offset, offset);
			Mile_AtomicSetPtr(&bucket->hash_value, hash_value);
		}
		else
		{
			offset = doclist_insert(hash_index->doclist,docid,bucket->offset, hash_index->hashmod);
			Mile_AtomicSetPtr(&bucket->offset, offset);
		}
		return MILE_RETURN_SUCCESS;
	}
	
	
	//取模
	loc = hash_value%hash_index->hashmod;
	i = loc;

	do
	{
		bucket = hash_index->mem_mmaped+i;

		//哈希这个位置没有这个值存在，调用doclist接口插入
		if(bucket->hash_value == 0)
		{
			if((offset = doclist_insert(hash_index->doclist,docid,0, i)) == 0 )
				return ERROR_INSERT_REPEAT;
			//注意hash_value一定要在offset之后写，因为并发查询的时候会先去读value，判断value是否为0
			Mile_AtomicSetPtr(&bucket->offset, offset);
			Mile_AtomicSetPtr(&bucket->hash_value, hash_value);
			return MILE_RETURN_SUCCESS;
		}

		//哈希这个位置有值存在，并且相等
		if(bucket->hash_value == hash_value)
		{
			if((offset = doclist_insert(hash_index->doclist,docid,bucket->offset, i)) == 0)
				return ERROR_INSERT_REPEAT;
			Mile_AtomicSetPtr(&bucket->offset, offset);
			return MILE_RETURN_SUCCESS;
		}
		
		i = (i+1)%hash_index->hashmod;
	}
	while(i!=loc);

	log_error("hash 恶性冲突");
	return ERROR_HASH_CONFLICT;
}


struct rowid_list* get_rowid_list(struct hash_index_manager* hash_index,struct doc_row_unit* doc_row,MEM_POOL* mem_pool)
{
	struct rowid_list* rowids = rowid_list_init(mem_pool);

	//遍历整个doc row列表
	while(doc_row != NULL)
	{
		rowid_list_add(mem_pool,rowids,doc_row->doc_id);
		if((doc_row->next & 0x80000000) || (doc_row->next == 0))
			break;
		doc_row = NEXT_DOC_ROW_STRUCT(hash_index->doclist,doc_row->next);
	}
	return rowids;
}



//也是分两种情况
//1.空值 直接返回桶里的所有doclist
//2.非空 则对data取hash值，找到自己的桶，如果该桶没有值，则说明不存在，如果存在，且相等，则返回该桶下的所有doclist，否则继续往后找
//一旦有桶为空，则说明这个值一定不存在，也不需要再往后找了
struct rowid_list* hash_index_query(struct hash_index_manager* hash_index,struct low_data_struct* data,MEM_POOL* mem_pool)
{
	struct hash_bucket* bucket;
	struct rowid_list* ret;
	uint64_t hash_value;
	uint64_t hash_value_in_hash_info;
	uint32_t loc;
	uint32_t offset;
	uint32_t i;

	//如果为空值的话，则把第hashmod的桶返回给上层
	if(data->len == 0)
	{
		bucket = hash_index->mem_mmaped+hash_index->hashmod;
		
		hash_value_in_hash_info = Mile_AtomicGetPtr(&bucket->hash_value);
		/*只要为空，则肯定不存在*/
		if(hash_value_in_hash_info == 0)
		{
			return NULL;
		}

		offset = Mile_AtomicGetPtr(&bucket->offset);
		ret = get_rowid_list(hash_index,NEXT_DOC_ROW_STRUCT(hash_index->doclist, offset),mem_pool);
		return ret;
	}
	
	
	//根据value做一次hash
	PROFILER_BEGIN("get hash value");
	hash_value = get_hash_value(data);
	PROFILER_END();

	//取模
	loc = hash_value%hash_index->hashmod;

	//如果定位到的hash有值，则需要往下寻找
	i=loc;
	do
	{
		bucket = hash_index->mem_mmaped+i;

		hash_value_in_hash_info = Mile_AtomicGetPtr(&bucket->hash_value);
		/*如果循环的过程中，只要有一个为空，则肯定不存在*/
		if(hash_value_in_hash_info == 0)
		{
			return NULL;
		}
		
		//找出hash值相等地方
		if(hash_value_in_hash_info == hash_value)
		{
		   offset = Mile_AtomicGetPtr(&bucket->offset);
		   ret = get_rowid_list(hash_index,NEXT_DOC_ROW_STRUCT(hash_index->doclist, offset),mem_pool);
		   return ret;
		}
		
		i = (i+1)%hash_index->hashmod;
	}
	while(i!=loc);

	log_debug("查询不到这个值");
	return NULL;
}




void hash_index_destroy(struct hash_index_manager* hash_index)
{
	if(hash_index->mem_mmaped != NULL)
	{
        munmap(hash_index->mem_mmaped, HASH_INDEX_MMAP_SIZE(hash_index));
	}

	//释放doclist结构
	doclist_destroy(hash_index->doclist);

	//删除对应的文件和索引
	remove(hash_index->file_name);
	return;
}


void hash_index_release(struct hash_index_manager* hash_index)
{
	// double checked locking
	if(hash_index->is_full || mile_conf.all_mmap) {
		doclist_release(hash_index->doclist);
		if(hash_index->mem_mmaped != NULL)
			msync(hash_index->mem_mmaped,HASH_INDEX_MMAP_SIZE(hash_index),MS_SYNC); // make sure synced

	} else {
		pthread_mutex_lock( &hash_index->mmap_lock );

		doclist_release(hash_index->doclist);

		if(NULL != hash_index->mem_mmaped) {
			if( hash_index->is_full )
				msync(hash_index->mem_mmaped,HASH_INDEX_MMAP_SIZE(hash_index),MS_SYNC); // make sure synced
			else
				flush_memory(hash_index->file_name, hash_index->mem_mmaped, HASH_INDEX_MMAP_SIZE(hash_index), 0); // no disk write limit
		}

		pthread_mutex_unlock( &hash_index->mmap_lock );
	}

	munmap(hash_index->mem_mmaped, HASH_INDEX_MMAP_SIZE(hash_index));
	return;
}



void hash_index_checkpoint(struct hash_index_manager* hash_index)
{
	// double checked locking
	if( hash_index->is_full || mile_conf.all_mmap ) {
		log_debug("mmap hash index checkpoint");

		doclist_checkpoint(hash_index->doclist);
		if(hash_index->mem_mmaped != NULL) 
			msync(hash_index->mem_mmaped,HASH_INDEX_MMAP_SIZE(hash_index),MS_SYNC); // make sure synced

	} else {
		log_debug("malloc hash index checkpoint");
		pthread_mutex_lock( &hash_index->mmap_lock );

		doclist_checkpoint(hash_index->doclist);

		if(NULL != hash_index->mem_mmaped) {
			if( hash_index->is_full )
				msync(hash_index->mem_mmaped,HASH_INDEX_MMAP_SIZE(hash_index),MS_SYNC); // make sure synced
			else 
				flush_memory(hash_index->file_name, hash_index->mem_mmaped, HASH_INDEX_MMAP_SIZE(hash_index), mile_conf.disk_write_limit);
		}

		pthread_mutex_unlock( &hash_index->mmap_lock );
	}
	
	return;
}

int hash_index_mmap_switch(struct hash_index_manager *hash_index)
{
	if( !hash_index->is_full && !mile_conf.all_mmap ) {
		pthread_mutex_lock( &hash_index->mmap_lock );
		// flush memory to disk
		if( flush_memory(hash_index->file_name, hash_index->mem_mmaped, HASH_INDEX_MMAP_SIZE(hash_index), mile_conf.disk_write_limit) != 0 ) {
			pthread_mutex_unlock( &hash_index->mmap_lock );
			return ERROR_MMAP_SWITCH;
		}

		// switch mmaped file to hash index file
		if( switch_mmaped_file(hash_index->file_name, hash_index->mem_mmaped, HASH_INDEX_MMAP_SIZE(hash_index)) != 0 ) {
			pthread_mutex_unlock( &hash_index->mmap_lock );
			return ERROR_MMAP_SWITCH;
		}
		usleep(MMAP_SWITCH_SLEEP_INTERVAL * 1000);

		int ret = doclist_mmap_switch(hash_index->doclist);
		if( MILE_RETURN_SUCCESS != ret ) {
			pthread_mutex_unlock( &hash_index->mmap_lock );
			return ERROR_MMAP_SWITCH;
		}
		hash_index->is_full = 1;
		pthread_mutex_unlock( &hash_index->mmap_lock );

		usleep(MMAP_SWITCH_SLEEP_INTERVAL * 1000);
	}

	return MILE_RETURN_SUCCESS;
}

