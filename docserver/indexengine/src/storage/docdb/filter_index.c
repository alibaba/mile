/*
 * =====================================================================================
 *
 *       Filename:  hi_filter_index.c
 *
 *    Description:  对不定长和定长的封装
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

#include "filter_index.h"

// 
static inline int is_fixed_size(enum field_types type)
{
	return get_unit_size(type) > 0;
}


struct filter_index_manager* filter_index_init(struct filter_index_config* config, MEM_POOL* mem_pool)
{
	struct filter_index_manager * filter_index = 
		(struct filter_index_manager *)mem_pool_malloc(mem_pool,sizeof(struct filter_index_manager));
	memset(filter_index,0,sizeof(struct filter_index_manager));

	filter_index->type = config->type;

	//根据数据类型初始化底层存储引擎
	if(!is_fixed_size(filter_index->type))
	{
		struct vstorage_config vsconfig;	

		memset(&vsconfig,0,sizeof(vsconfig));
		strcpy(vsconfig.work_space,config->work_space);
		sprintf(vsconfig.vstorage_name,"filter_vstore");
		vsconfig.row_limit = config->row_limit;

		filter_index->vstorage = vstorage_init(&vsconfig, mem_pool);

		assert(filter_index->vstorage != NULL);
	}
	else
	{	
		struct storage_config sconfig;	

		memset(&sconfig,0,sizeof(struct storage_config));
		strcpy(sconfig.work_space,config->work_space);
		strcpy(sconfig.storage_name,"filter_store");
		sconfig.row_limit = config->row_limit;
		sconfig.unit_size = config->unit_size;

		filter_index->storage = storage_init(&sconfig, mem_pool);

		assert(filter_index->storage != NULL);
	}
	
	return filter_index;
}



int32_t filter_index_insert(struct filter_index_manager * filter_index,struct low_data_struct* data,uint32_t docid)
{	
    int32_t ret;
	
	//不定长
	if(!is_fixed_size(filter_index->type))
	{
		ret = vstorage_insert(filter_index->vstorage,data,docid);
	}
	//定长
	else
	{
		ret = storage_insert(filter_index->storage,data,docid);
	}

	return ret;
}


int32_t filter_index_update(struct filter_index_manager * filter_index,
					struct low_data_struct* new_data,
					struct low_data_struct** old_data,
					uint32_t docid,
					MEM_POOL* mem_pool)

{
	//不定长
	if(!is_fixed_size(filter_index->type))
	{
		return vstorage_update(filter_index->vstorage,new_data,old_data,docid,mem_pool);
	}
	//定长
	else
	{
		return storage_update(filter_index->storage,new_data,old_data,docid,mem_pool);
	}
}	

struct low_data_struct* filter_index_query(struct filter_index_manager * filter_index,uint32_t docid,MEM_POOL* mem_pool)
{
    //不定长
	if(!is_fixed_size(filter_index->type))
	{
		return vstorage_query(filter_index->vstorage,docid,mem_pool);
	}
	//定长
	else
	{
		return storage_query(filter_index->storage,docid,mem_pool);
	}
}


void filter_index_release(struct filter_index_manager* filter_index)
{
	 //不定长
	if(!is_fixed_size(filter_index->type))
	{
		return vstorage_release(filter_index->vstorage);
	}
	//定长
	else
	{
		return storage_release(filter_index->storage);
	}
}

void filter_index_checkpoint(struct filter_index_manager* filter_index)
{
	 //不定长
	if(!is_fixed_size(filter_index->type))
	{
		return vstorage_checkpoint(filter_index->vstorage);
	}
	//定长
	else
	{
		return storage_checkpoint(filter_index->storage);
	}
}


void filter_index_destroy(struct filter_index_manager* filter_index)
{
	 //不定长
	if(!is_fixed_size(filter_index->type))
	{
		return vstorage_destroy(filter_index->vstorage);
	}
	//定长
	else
	{
		return storage_destroy(filter_index->storage);
	}
}

int filter_index_recover(struct filter_index_manager *filter_index, uint32_t docid)
{
	if(!is_fixed_size(filter_index->type))
		return vstorage_recover(filter_index->vstorage, docid);
	else
		return storage_recover(filter_index->storage, docid);
}

