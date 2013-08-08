/*
 * =====================================================================================
 *
 *       Filename:  hi_index_field.c
 *
 *    Description:  对不同类型的列的封装，并包含merge列的实现
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


#include "index_field.h"

//根据类型初始化对应的列
//其中索引类型的列，需要根据store value，是否存储原始值来决定，要不要初始化一个额外的filter列来存储索引列的原始值
//hash btree filter_hash本身是不存储原始值的
struct index_field_manager* index_field_init(struct index_field_config* config,MEM_POOL* mem_pool)
{
	struct index_field_manager* index_field =  (struct index_field_manager*)mem_pool_malloc(mem_pool,sizeof(struct index_field_manager));
	memset(index_field,0,sizeof(struct index_field_manager));

	assert(index_field != NULL);

	/*创建工作目录*/
	sprintf(index_field->work_space,"%s/%s",config->work_space,config->field_name);
	if(mkdirs(index_field->work_space) < 0)
	{
		log_error("field工作目录创建失败");
		return NULL;
	}

	index_field->index_type = config->index_type;
	index_field->row_limit = config->row_limit;
	strcpy(index_field->field_name,config->field_name);
	index_field->hash_compress_num = config->hash_compress_num;


	switch(index_field->index_type)
	{		
		case HI_KEY_ALG_FULLTEXT:
			{
				struct dyhash_index_config dyhash_config;
				strcpy(dyhash_config.work_space, index_field->work_space);
				dyhash_config.row_limit = index_field->row_limit;
				dyhash_config.is_full = config->is_full;
				index_field->dyhash_index = dyhash_index_init(&dyhash_config, mem_pool);
				assert(index_field->dyhash_index != NULL);
				break;
			}
	
		case HI_KEY_ALG_HASH:
			{
				/*初始化列的状态信息*/
				sprintf(index_field->stat_filename,"%s/hash_stat.inf",index_field->work_space);
				index_field->flag= (enum index_field_flag*)get_mmap_memory(index_field->stat_filename,sizeof(enum index_field_flag));
				assert(index_field->flag!= NULL);
	
				//如果已压缩,初始化hash_compress
				if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
				{
					struct hash_compress_config hcompress_config;
					memset(&hcompress_config,0,sizeof(struct hash_compress_config));
					
					strcpy(hcompress_config.work_space,index_field->work_space);
					hcompress_config.row_limit = index_field->row_limit;
					hcompress_config.hash_compress_num = index_field->hash_compress_num;

					index_field->hash_compress = hash_commpress_init(&hcompress_config,mem_pool);

					assert(index_field->hash_compress != NULL);
				}

				//未压缩，初始化hash_index
				else
				{
					/*初始化hash索引*/
					struct hash_index_config hash_config; 
					memset(&hash_config,0,sizeof(struct hash_index_config));
					
					hash_config.row_limit = index_field->row_limit;
					hash_config.is_full = config->is_full;
					strcpy(hash_config.work_space,index_field->work_space);
					
					index_field->hash_index = hash_index_init(&hash_config,mem_pool);
					assert(index_field->hash_index != NULL);

				}
				
				break;
			}
		case HI_KEY_ALG_FILTER:
			{
				/*初始化列的状态信息*/
				sprintf(index_field->stat_filename,"%s/filter_stat.inf",index_field->work_space);
				index_field->flag= (enum index_field_flag*)get_mmap_memory(index_field->stat_filename,sizeof(enum index_field_flag));
				assert(index_field->flag!= NULL);
				
				/* 初始化列大小 */
				sprintf(index_field->len_filename,"%s/len.inf",index_field->work_space);
				index_field->max_len = (uint32_t *)get_mmap_memory(index_field->len_filename, sizeof(uint32_t));
				assert(index_field->max_len);
				
				//如果已压缩，初始化filter
				if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
				{
					index_field->filter_compress = filter_compress_init(index_field->work_space,mem_pool);
					assert(index_field->filter_compress);
				}
				//未压缩，初始化hash_index
				else
				{
					struct filter_index_config filter_config;

					/*初始化存储hash的filter*/
					filter_config.row_limit = index_field->row_limit;

					/*建filter索引的话，默认是64位长整型，无论字符串，还是数字都可表示*/
					filter_config.type = HI_TYPE_LONGLONG;
					filter_config.unit_size = get_unit_size(HI_TYPE_LONGLONG);
					strcpy(filter_config.work_space,index_field->work_space);

					index_field->filter_index = filter_index_init(&filter_config, mem_pool);
					assert(index_field->filter_index != NULL);
			
				}

				break;
			}
			
			
		//TODO 加入btree
		case HI_KEY_ALG_BTREE:
			{
				

				break;
			}

		default:
			log_error("不支持的列索引 %d",index_field->index_type);
			return NULL;
	}	

	return index_field;
}



int32_t index_field_recover(struct index_field_manager* index_field, uint32_t docid)
{
	int32_t ret;

	//拒绝插入的条件
	if(index_field == NULL) 
	{
		log_warn("此列未初始化%s",index_field->field_name);
		return ERROR_FIELD_NOT_WORK;
	}
	
	if(index_field->flag != NULL && (Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS))
    	return 0;

	switch(index_field->index_type)
	{
		case HI_KEY_ALG_HASH:
			 ret = hash_index_recover(index_field->hash_index, docid);    
			 break;
		case HI_KEY_ALG_FILTER:
			 ret = filter_index_recover(index_field->filter_index, docid);
			 break;
		case HI_KEY_ALG_FULLTEXT:
			 ret = dyhash_index_recover(index_field->dyhash_index, docid);
			 break;
		case HI_KEY_ALG_BTREE:
			{
				return MILE_RETURN_SUCCESS;
			}
		default:
			log_error("该列的索引类型不正确,%d",index_field->index_type);
			return ERROR_NOT_SUPPORT_INDEX;
	}
	
	return ret;
}


int32_t index_field_insert(struct index_field_manager* index_field,struct low_data_struct* data,uint32_t docid)
{
	int32_t ret;

	//拒绝插入的条件
	if(index_field == NULL) 
	{
		log_warn("此列未初始化%s",index_field->field_name);
		return ERROR_FIELD_NOT_WORK;
	}
	
	if(index_field->flag != NULL && (Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS))
    	return ERROR_INDEX_FIELD_COMPRESSED;

	switch(index_field->index_type)
	{
		case HI_KEY_ALG_FULLTEXT:
			{
				/*全文列插入hash*/
				PROFILER_BEGIN("dyhash index insert");
				if((ret = dyhash_index_insert(index_field->dyhash_index,data,docid)) < 0)
				{
					PROFILER_END();
					return ret;
				}
				PROFILER_END();

				return MILE_RETURN_SUCCESS;

			}
		case HI_KEY_ALG_HASH:
			{
				/*哈希列插入hash*/
				PROFILER_BEGIN("hash index insert");
				if((ret = hash_index_insert(index_field->hash_index,data,docid)) < 0)
				{
					PROFILER_END();
					return ret;
				}
				PROFILER_END();

				return MILE_RETURN_SUCCESS;
			}
		case HI_KEY_ALG_BTREE:
			{
				return MILE_RETURN_SUCCESS;
			}
		case HI_KEY_ALG_FILTER:
			{
				//如果是字符串，需要对数据进行预处理
				if(data->type == HI_TYPE_STRING)
				{
					struct low_data_struct hash_data;
					
					PROFILER_BEGIN("get hash value");
					uint64_t hash_value = get_hash_value(data);
					PROFILER_END();
					
					hash_data.data = &hash_value;
					hash_data.len = get_unit_size(HI_TYPE_LONGLONG);
					hash_data.type = HI_TYPE_LONGLONG;
					hash_data.field_name = data->field_name;

					if(*index_field->max_len < get_unit_size(HI_TYPE_LONGLONG))
					{
						*index_field->max_len = get_unit_size(HI_TYPE_LONGLONG);
						msync(index_field->max_len,sizeof(uint32_t),MS_SYNC);
					}

					PROFILER_BEGIN("filter index insert");
					if((ret = filter_index_insert(index_field->filter_index,&hash_data,docid) < 0) )
					{
						PROFILER_END();
						return ret;
					}
					PROFILER_END();
					
				}
				else
				{
					if(data->len > get_unit_size(HI_TYPE_LONGLONG))
					{
						log_error("数据长度超过8个字节，len:%u",data->len);
						return ERROR_INSERT_FAILDED;
			
					}
					if(*index_field->max_len < data->len)
					{
						*index_field->max_len = data->len;
						msync(index_field->max_len,sizeof(uint32_t),MS_SYNC);
					}
					
					PROFILER_BEGIN("filter index insert");
					if((ret = filter_index_insert(index_field->filter_index,data,docid) < 0) )
					{
						PROFILER_END();
						return ret;
					}
					PROFILER_END();
				}

				return MILE_RETURN_SUCCESS;
			}
		default:
			log_error("该列的索引类型不正确,%d",index_field->index_type);
			return ERROR_NOT_SUPPORT_INDEX;
	}
}

//只有filter列才能更新
int32_t index_field_update(struct index_field_manager* index_field,
						 struct low_data_struct* new_data,
						 struct low_data_struct** old_data,
						 uint32_t docid,
						 MEM_POOL* mem_pool)
{
	int32_t ret;
	
	//拒绝更新的条件
	if(index_field == NULL)
	{
		log_warn("此列未初始化%s",index_field->field_name);
		return ERROR_FIELD_NOT_WORK;
	}

	if(index_field->flag != NULL && Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
		return ERROR_INDEX_FIELD_COMPRESSED;

	switch(index_field->index_type)
	{

		case HI_KEY_ALG_FILTER:
		{
			//如果是字符串，需要对数据进行预处理
			if(new_data->type == HI_TYPE_STRING)
			{
				struct low_data_struct hash_data;

				PROFILER_BEGIN("get_hash_value");
				uint64_t hash_value = get_hash_value(new_data);
				PROFILER_END();
				
				hash_data.data = &hash_value;
				hash_data.len = get_unit_size(HI_TYPE_LONGLONG);

				PROFILER_BEGIN("filter index update");
				if((ret = filter_index_update(index_field->filter_index,&hash_data,old_data,docid,mem_pool) < 0) )
				{
					PROFILER_END();
					return ret;
				}
				PROFILER_END();
			}
			else
			{
				PROFILER_BEGIN("filter index update");
				if((ret = filter_index_update(index_field->filter_index,new_data,old_data,docid,mem_pool) < 0) )
				{
					PROFILER_END();
					return ret;
				}
				PROFILER_END();
			}

			return MILE_RETURN_SUCCESS;
		}	
		default:
			log_warn("只有filter列才能update");
			return ERROR_ONLY_FILTER_SUPPORT;
	}
}



struct low_data_struct* index_field_value_query(struct index_field_manager* index_field, uint32_t docid, MEM_POOL* mem_pool)
{
	struct low_data_struct* ret = NULL;
	if(index_field == NULL)
	{
		struct low_data_struct* data = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
		memset(data,0,sizeof(struct low_data_struct));
		log_warn("此列未创建存储实体");
		return data;
	}

	if(index_field->index_type != HI_KEY_ALG_FILTER )
	{
		log_warn("只有hash btree filterhash列才能根据value查询");
		return NULL;
	}

	PROFILER_BEGIN("filter index query");

	if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
	{
		PROFILER_BEGIN("compress");
		ret = filter_compress_query(index_field->filter_compress,docid,mem_pool);
		PROFILER_END();
	}
	else
	{
		PROFILER_BEGIN("no compress");
		ret = filter_index_query(index_field->filter_index,docid,mem_pool);
		PROFILER_END();
	}

	PROFILER_END();

	return ret;
}

uint32_t index_field_count_query(struct index_field_manager* index_field, struct low_data_struct* data, MEM_POOL* mem_pool)
{
	uint32_t ret = NULL;
	if(index_field == NULL)
	{
		log_warn("此列未创建存储实体");
		return NULL;
	}
	
	if(index_field->index_type != HI_KEY_ALG_FULLTEXT)
	{
		log_warn("只有fulltext列才能根据value查询对应分词的个数");
		return 0;
	}

	/* hash */
	PROFILER_BEGIN("dyhash index query");

	//dyhash索引
	if(index_field->index_type == HI_KEY_ALG_FULLTEXT){
		PROFILER_BEGIN("count query");

		ret = dyhash_index_count_query(index_field->dyhash_index, data, mem_pool);

		PROFILER_END();
	}


	PROFILER_END();
	return ret;
}


/*只有hash btree filterhash hashfilterhash支持*/
struct rowid_list* index_field_equal_query(struct index_field_manager* index_field,struct low_data_struct* data,MEM_POOL* mem_pool)
{
	struct rowid_list* ret = NULL;
	if(index_field == NULL)
	{
		log_warn("此列未创建存储实体");
		return NULL;
	}
	
	if(index_field->index_type != HI_KEY_ALG_HASH && index_field->index_type != HI_KEY_ALG_FULLTEXT)
	{
		log_warn("只有hash btree filterhash  fulltext列才能根据value查询");
		return NULL;
	}

	/* hash */
	PROFILER_BEGIN("hash index query");

	//hash索引
	if(index_field->index_type  == HI_KEY_ALG_HASH){

		if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
		{
			PROFILER_BEGIN("compress");
			ret = hash_compress_query(index_field->hash_compress,data,mem_pool);
			PROFILER_END();
		}
		else
		{
			PROFILER_BEGIN("no compress");
			ret = hash_index_query(index_field->hash_index,data,mem_pool);
			PROFILER_END();
		}
	}

	//dyhash索引
	if(index_field->index_type == HI_KEY_ALG_FULLTEXT){
		PROFILER_BEGIN("no compress");

		ret = dyhash_index_query(index_field->dyhash_index, data, mem_pool);
		PROFILER_END();
	}


	PROFILER_END();
	return ret;
}


int32_t index_field_compress(struct index_field_manager* index_field,MEM_POOL* mem_pool)
{
	if(index_field == NULL || 
	   (Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS))
	{
		log_warn("此列已压缩");
		return ERROR_NOT_NEED_COMPRESS;
	}

	log_info("开始压缩 %s 列 索引类型 :%u",index_field->field_name,index_field->index_type);
	
	switch(index_field->index_type)
	{
		case HI_KEY_ALG_HASH:

			index_field->hash_compress = hash_compress_load(index_field->hash_index,index_field->hash_compress_num,mem_pool);
			
			if(index_field->hash_compress == NULL)
			{
				log_error("hash 压缩失败 %s",index_field->field_name);
				return ERROR_COMMPRESS_FAIL;
			}
			
			break;
		case HI_KEY_ALG_FILTER:
			index_field->filter_compress = filter_compress_load(index_field->filter_index->storage,*index_field->max_len,mem_pool);
			if(index_field->filter_compress == NULL)
			{
				log_error("filter压缩失败 %s",index_field->field_name);
				return ERROR_COMMPRESS_FAIL;
			}

			if(index_field->filter_compress == (struct filter_compress_manager*)0x1)
			{
				log_error("filter不需要压缩 %s",index_field->field_name);
				return ERROR_NOT_NEED_COMPRESS;
			}
			
			break;
		default:
			return ERROR_NOT_SUPPORT_COMMPRESS;
	}

	log_info("压缩完成");
	return MILE_RETURN_SUCCESS;

}


int32_t index_field_switch(struct index_field_manager* index_field)
{
	if(index_field == NULL || 
	   (Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS))
	{
		log_warn("此列已压缩");
		return ERROR_FIELD_NOT_WORK;
	}
	switch(index_field->index_type)
	{
		case HI_KEY_ALG_HASH:
			if(index_field->hash_compress == NULL)
			{
				log_error("切段的时候，发现hash压缩未成功，field_name:%s",index_field->field_name);
				return ERROR_COMMPRESS_FAIL;
			}

			//切换标志量
			Mile_AtomicOrPtr(index_field->flag,INDEX_FIELD_COMPRESS);

			//确保在删除hash索引之前，标志位已切
			msync(index_field->flag,sizeof(enum index_field_flag),MS_SYNC);
			
			//删除hash索引的数据
			hash_index_destroy(index_field->hash_index);
			index_field->hash_index = NULL;

			break;
		case HI_KEY_ALG_FILTER:
			if(index_field->filter_compress == NULL)
			{
				log_error("切段的时候，发现filter压缩未成功，field_name:%s",index_field->field_name);
				return ERROR_COMMPRESS_FAIL;
			}

			if(index_field->filter_compress == (struct filter_compress_manager*)0x1)
			{
				log_error("切段的时候，发现filter并不需要压缩，field_name:%s",index_field->field_name);
				return ERROR_NOT_NEED_COMPRESS;
			}
			
			//切换标志量
			Mile_AtomicOrPtr(index_field->flag,INDEX_FIELD_COMPRESS);

			//确保在删除filter索引之前，标志位已切
			msync(index_field->flag,sizeof(enum index_field_flag),MS_SYNC);
			
			//删除filter索引的数据
			filter_index_destroy(index_field->filter_index);
			index_field->filter_index = NULL;

			break;
		default:
			return ERROR_NOT_SUPPORT_COMMPRESS;
	}
	
	return MILE_RETURN_SUCCESS;
}

void index_field_checkpoint(struct index_field_manager* index_field)
{
	//压缩后不需要再checkpoint
	if(index_field == NULL || (index_field->flag != NULL && (Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)))
	{
		log_warn("此列未创建存储实体 或已压缩");
		return;
	}

	switch(index_field->index_type)
	{
		case HI_KEY_ALG_HASH:
			if(!(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS))
				hash_index_checkpoint(index_field->hash_index);
			break;
		case HI_KEY_ALG_FILTER:
			if(!(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS))
				filter_index_checkpoint(index_field->filter_index);
			break;
		case HI_KEY_ALG_FULLTEXT:
				dyhash_index_checkpoint(index_field->dyhash_index);
			break;
		case HI_KEY_ALG_BTREE:
			//TODO btree
			break;
		default:
			log_error("不支持的类型索引,%d",index_field->index_type);
			break; ;
	}
	
	msync(index_field->flag,sizeof(enum index_field_flag),MS_SYNC);
	msync(index_field->max_len,sizeof(uint32_t),MS_SYNC);
	return;
}


void index_field_release(struct index_field_manager* index_field)
{
	if(index_field == NULL)
	{
		log_warn("此列未创建存储实体");
		return;
	}
	
	switch(index_field->index_type)
	{
		case HI_KEY_ALG_FULLTEXT:
			dyhash_index_release(index_field->dyhash_index);		

			break;
		case HI_KEY_ALG_HASH:
			
			if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
				hash_compress_release(index_field->hash_compress);
			else
				hash_index_release(index_field->hash_index);
			
			break;
		case HI_KEY_ALG_FILTER:
			if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
				filter_compress_release(index_field->filter_compress);
			else
				filter_index_release(index_field->filter_index);

			msync(index_field->max_len,sizeof(uint32_t),MS_SYNC);
			munmap(index_field->max_len,sizeof(uint32_t));
			break;
		case HI_KEY_ALG_BTREE:
			//TODO btree
			break;
		default:
			log_error("不支持的类型索引,%d",index_field->index_type);
			break; ;
	}
	
	msync(index_field->flag,sizeof(enum index_field_flag),MS_SYNC);
    munmap(index_field->flag, sizeof(enum index_field_flag));
	
	return;
}


void index_field_destroy(struct index_field_manager* index_field)
{
	if(index_field == NULL)
	{
		log_warn("此列未创建存储实体");
		return;
	}
	
	switch(index_field->index_type)
	{
		case HI_KEY_ALG_HASH:
			
			if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
				hash_compress_destroy(index_field->hash_compress);
			else
				hash_index_destroy(index_field->hash_index);
			
			break;
		case HI_KEY_ALG_FILTER:
			if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
			{
				filter_compress_destroy(index_field->filter_compress);
			}
			else
				filter_index_destroy(index_field->filter_index);

			
			 munmap(index_field->max_len,sizeof(uint32_t));
			 remove(index_field->len_filename);
			break;
		case HI_KEY_ALG_BTREE:
			//TODO btree
			break;
		default:
			log_error("不支持的类型索引,%d",index_field->index_type);
			break; ;
	}
	
	 munmap(index_field->flag, sizeof(enum index_field_flag));
	 remove(index_field->stat_filename);

	 //删除索引列目录
	 remove(index_field->work_space);
	 return;
}

int index_mmap_switch(struct index_field_manager *index_field)
{
	if(NULL == index_field) {
		log_warn( "no index storage" );
		return MILE_RETURN_SUCCESS;
	}

	switch(index_field->index_type) {
	case HI_KEY_ALG_HASH:
		if(Mile_AtomicGetPtr(index_field->flag) & INDEX_FIELD_COMPRESS)
			return MILE_RETURN_SUCCESS;
		return hash_index_mmap_switch(index_field->hash_index);
	case HI_KEY_ALG_FULLTEXT:
		return dyhash_index_mmap_switch(index_field->dyhash_index);
		break;
	default:
		;
	}
	return MILE_RETURN_SUCCESS;
}

/*-----------------------------------------------------------------------------
 *  btree范围查询，满足条件的row id list
 *-----------------------------------------------------------------------------*/

//TODO 集成btree
struct rowid_list * index_field_range_query(struct index_field_manager * index_field, \
			struct db_range_query_condition * range_condition, MEM_POOL* mem_pool)
{

	return NULL;
}

