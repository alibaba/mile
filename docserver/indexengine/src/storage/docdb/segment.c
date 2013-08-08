/*
 * =====================================================================================
 *
 *       Filename:  hi_segment.c
 *
 *    Description:  段的管理信息定义，以及接口定义，相当于子表的概念
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

#include "segment.h"


static int32_t index_compress_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data);

static int32_t index_release_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data);

static int32_t index_checkpoint_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data);



struct segment_manager* segment_init(struct segment_config* config, MEM_POOL* mem_pool)
{	
	struct segment_manager* segment = (struct segment_manager*)mem_pool_malloc(mem_pool,sizeof(struct segment_manager));
	memset(segment,0,sizeof(struct segment_manager));
	
	//创建目录
	strcpy(segment->segment_name,config->segment_name);
	sprintf(segment->work_space,"%s/%s",config->work_space,config->segment_name);

	if(mkdirs(segment->work_space) < 0)
	{
		log_error("创建目录失败 %s",segment->work_space);
		return NULL;
	}

	segment->sid = config->sid;
	segment->row_limit = config->row_limit;
	segment->hash_compress_num = config->hash_compress_num;

	//初始化meta信息
	segment->meta_data = config->meta_data;

	//初始化删除标记
	struct bitmark_config del_mark_config;
	memset(&del_mark_config,0,sizeof(struct bitmark_config));
	strcpy(del_mark_config.work_space,segment->work_space);
	strcpy(del_mark_config.bitmark_name,"del");
	del_mark_config.row_limit = segment->row_limit;

	segment->del_bitmap = bitmark_init(&del_mark_config, mem_pool);
	assert(segment->del_bitmap);


	//初始化原始数据列
	struct data_field_config data_config;
	
	data_config.row_limit = segment->row_limit;
	strcpy(data_config.work_space,segment->work_space);

	segment->data_field = data_field_init(&data_config,mem_pool);

	assert(segment->data_field);


	//初始化string_map
	segment->index_fields = init_string_map(mem_pool,MAX_INDEX_FIELD_NUM*MAX_INDEX_PER_FIELD);

	//初始化索引列
	uint16_t i;
	uint8_t j;

	//用于将index_field实例，作为一个键值对，便于查询
	struct index_field_meta* index_fields = config->index_fields;
	for(i=0; i<config->index_field_count; i++,index_fields++)
	{
		for(j=0; j<index_fields->index_count; j++)
		{
			segment_ensure_index(segment,index_fields->field_name,index_fields->indexs[j].index_type, mem_pool);
		}
	}

   return segment;
}



int32_t segment_ensure_index(struct segment_manager* segment,char* field_name,enum index_key_alg index_type, MEM_POOL* mem_pool)
{
	//设置最后一次修改时间
	if( Mile_AtomicGetPtr( &segment->meta_data->modify_time ) == 0L ) {
		Mile_AtomicSetPtr(&segment->meta_data->modify_time,time(NULL));
	}

	//用于将index_field实例，作为一个键值对，便于查询
	char key[MAX_FIELD_NAME*2];  //存储的键
	
	struct index_field_config index_config;
	struct index_field_manager* index_field = NULL;
	
	memset(&index_config,0,sizeof(struct index_field_config));
	memset(key,0,sizeof(key));
	
	strcpy(index_config.field_name,field_name);
	strcpy(index_config.work_space,segment->work_space);
	index_config.row_limit = segment->row_limit;
	index_config.index_type = index_type;
	index_config.hash_compress_num = segment->hash_compress_num;
	index_config.is_full = !!(segment->meta_data->row_count == segment->row_limit);

	log_info("段内开始建立索引实例 sid %u field_name:%s limit:%u index_type:%u hash_compress_num:%u is_full:%s",
			segment->sid, field_name, index_config.row_limit, index_type, index_config.hash_compress_num,
			index_config.is_full ? "TRUE" : "FALSE");
	index_field = index_field_init(&index_config, mem_pool);
	
	if(index_field == NULL)
	{
		log_error("建立索引失败");
		return ERROR_FIELD_INIT_FAILED;
	}
	
	//拼接key
	sprintf(key,INDEX_FIELD_KEY,index_config.field_name,index_config.index_type);

	//存入到string_map中
	log_info("将实例存储到index_fields里");
	string_map_put(segment->index_fields,key,(void*)index_field,1);

	return MILE_RETURN_SUCCESS;
}


int32_t segment_del_index(struct segment_manager* segment,char* field_name,enum index_key_alg index_type)
{
	//设置最后一次修改时间
	if( Mile_AtomicGetPtr( &segment->meta_data->modify_time ) == 0L ) {
		Mile_AtomicSetPtr(&segment->meta_data->modify_time,time(NULL));
	}

	//用于将index_field实例，作为一个键值对，便于查询
	char key[MAX_FIELD_NAME*2];  //存储的键
	memset(key,0,sizeof(key));
	
	struct index_field_manager* index_field = NULL;

	log_info("删除建立的索引实例 sid %u field_name:%s index_type:%u",segment->sid,
																	 field_name,
																	 index_type);
	
	//拼接key
	sprintf(key,INDEX_FIELD_KEY,field_name,index_type);

	//从string_map中删除
	index_field = (struct index_field_manager *)string_map_remove(segment->index_fields, key);

	//删除索引
	index_field_destroy(index_field);

	return MILE_RETURN_SUCCESS;
}

uint32_t segment_get_rowcount(struct segment_manager* segment)
{
	return Mile_AtomicGetPtr(&segment->meta_data->row_count);
}


void segment_set_rowcount(struct segment_manager* segment, uint32_t docid)
{
	Mile_AtomicSetPtr(&segment->meta_data->row_count, docid + 1);
	return;
}



int32_t segment_exceed_limit(struct segment_manager* segment)
{
	return Mile_AtomicGetPtr(&segment->meta_data->row_count) >= segment->row_limit? ERROR_EXCEED_LIMIT:0;
}


int32_t segment_data_insert(struct segment_manager* segment,struct row_data* rdata,uint32_t docid, MEM_POOL *mem_pool)
{
	//设置最后一次修改时间
	Mile_AtomicSetPtr(&segment->meta_data->modify_time,time(NULL));

	return data_field_insert(segment->data_field,rdata,docid,mem_pool);	
}



int32_t segment_data_update(struct segment_manager* segment,
						  uint32_t docid, 
						  struct low_data_struct* new_data,
				   		  struct low_data_struct** old_data,
				   		  MEM_POOL* mem_pool)
{
	int32_t ret;
	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return ERROR_EXCEED_LIMIT;
	}
	
	ret = data_field_update(segment->data_field,new_data,old_data,docid,mem_pool);
	return ret;
}



int32_t segment_index_update(struct segment_manager* segment,
						   uint32_t docid, 
						   struct low_data_struct* new_data,
						   struct low_data_struct** old_data,
						   MEM_POOL* mem_pool)
{
	int32_t ret;
	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return ERROR_EXCEED_LIMIT;
	}
	
	struct index_field_manager* index_field = segment_get_index_instance(segment,new_data->field_name,HI_KEY_ALG_FILTER);
	if(index_field == NULL)
	{
		log_warn("没有此索引列 %s %d",new_data->field_name,HI_KEY_ALG_FILTER);
		return ERROR_EXCEED_LIMIT;
	}

	ret = index_field_update(index_field,new_data,old_data,docid,mem_pool);
	return ret;	
}




struct low_data_struct* segment_data_query_col(struct segment_manager* segment,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* ret;
	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return NULL;
	}

	ret = data_field_query_col(segment->data_field,field_name,docid,mem_pool);
	return ret;
}


struct row_data* segment_data_query_row(struct segment_manager* segment,uint32_t docid,MEM_POOL* mem_pool)
{
	struct row_data* ret;
	
	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return NULL;
	}

	ret = data_field_query_row(segment->data_field,docid,mem_pool);

	return ret;
}


struct index_field_manager* segment_get_index_instance(struct segment_manager* segment,char* field_name,enum index_key_alg index_type)
{
	struct index_field_manager* index_field = NULL;
	
	//用于将index_field实例，作为一个键值对，便于查询
	char key[MAX_FIELD_NAME*2];  //存储的键
	memset(key,0,sizeof(key));
	
	//拼接key
	sprintf(key,INDEX_FIELD_KEY,field_name,index_type);

	index_field = (struct index_field_manager*)string_map_get(segment->index_fields,key);

	return index_field;
}


int32_t segment_index_insert(struct segment_manager* segment,
						   struct low_data_struct* data,
						   enum index_key_alg index_type, 
						   uint32_t docid, 
						   MEM_POOL *mem_pool)

{
	int32_t ret;
	struct index_field_manager* index_field = segment_get_index_instance(segment,data->field_name,index_type);
	if(index_field == NULL)
	{
		log_warn("没有此索引列 %d",index_type);
		return ERROR_FIELD_INIT_FAILED;
	}

	ret = index_field_insert(index_field,data,docid);
	return ret;
}



struct rowid_list * segment_index_range_query(struct segment_manager* segment, char* field_name,\
		struct db_range_query_condition * range_condition, MEM_POOL* mem_pool)
{
	struct rowid_list* ret;
	struct index_field_manager* index_field = segment_get_index_instance(segment,field_name,HI_KEY_ALG_BTREE);
	if(index_field == NULL)
	{
		log_warn("没有此索引列 %s %d",field_name,HI_KEY_ALG_BTREE);
		return NULL;
	}

	ret = index_field_range_query(index_field,range_condition,mem_pool);
	return ret;
}



struct low_data_struct* segment_index_value_query(struct segment_manager* segment,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* ret;
	struct index_field_manager* index_field = segment_get_index_instance(segment,field_name,HI_KEY_ALG_FILTER);
	if(index_field == NULL)
	{
		log_warn("没有此索引列 %s %d",field_name,HI_KEY_ALG_FILTER);
		return NULL;
	}

	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return NULL;
	}

	ret = index_field_value_query(index_field,docid,mem_pool);
	return ret;

}


struct rowid_list* segment_index_equal_query(struct segment_manager* segment,struct low_data_struct* data, enum index_key_alg index_type, MEM_POOL* mem_pool)
{
	struct rowid_list* ret;
	struct index_field_manager* index_field = segment_get_index_instance(segment,data->field_name,index_type);
	if(index_field == NULL)
	{
		log_warn("没有此索引列 %s %d",data->field_name,index_type);
		return NULL;
	}

	ret = index_field_equal_query(index_field,data,mem_pool);

	return ret;
}

uint32_t segment_index_count_query(struct segment_manager* segment,struct low_data_struct* data,MEM_POOL* mem_pool)
{
	uint32_t ret;
	struct index_field_manager* index_field = segment_get_index_instance(segment,data->field_name,HI_KEY_ALG_FULLTEXT);
	if(index_field == NULL)
	{
		log_warn("没有此索引列 %s %d",data->field_name,HI_KEY_ALG_FULLTEXT);
		return 0;
	}

	ret = index_field_count_query(index_field,data,mem_pool);

	return ret;
}

int32_t segment_del_docid(struct segment_manager* segment,uint32_t docid)
{
	int32_t ret;
	
	//设置最后一次修改时间
	Mile_AtomicSetPtr(&segment->meta_data->modify_time,time(NULL));
		
	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return ERROR_EXCEED_LIMIT;
	}
	
	ret = bitmark_clear(segment->del_bitmap,docid);

	//如果成功，则增加删除记录数
	if(ret == 1) {
		Mile_AtomicAddPtr(&segment->meta_data->del_count,1);
		return MILE_RETURN_SUCCESS;
	}
	
	return ret;
}


int32_t segment_is_docid_deleted(struct segment_manager* segment, uint32_t docid)
{
	int32_t ret;

	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return ERROR_EXCEED_LIMIT;
	}
	ret = bitmark_query(segment->del_bitmap,docid);
	return ret;
}

int32_t segment_set_docid_inserted(struct segment_manager* segment, uint32_t docid)
{
	int32_t ret;

	if(docid >= segment->row_limit)
	{
		log_error("超过limit限制，row_count:%u row_limit:%u",segment->meta_data->row_count,segment->row_limit);
		return ERROR_EXCEED_LIMIT;
	}
	ret = bitmark_set(segment->del_bitmap,docid);
	return ret;
}



static int32_t index_compress_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	int32_t ret;
	
	struct index_field_manager* index_field = (struct index_field_manager*)value;
	
	ret = index_field_compress(index_field,(MEM_POOL *) user_data); 

	if(ret != ERROR_COMMPRESS_FAIL)
		return MILE_RETURN_SUCCESS;
	else
		return ERROR_COMMPRESS_FAIL;
}

//压缩切换
static int32_t index_switch_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	int32_t ret;
	
	struct index_field_manager* index_field = (struct index_field_manager*)value;
	
	ret = index_field_switch(index_field); 

	if(ret != ERROR_COMMPRESS_FAIL)
		return MILE_RETURN_SUCCESS;
	else
		return ERROR_COMMPRESS_FAIL;
}



static int32_t index_release_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	index_field_release((struct index_field_manager*)value);

	return MILE_RETURN_SUCCESS;
}


static int32_t index_checkpoint_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{

	index_field_checkpoint((struct index_field_manager*)value);

	return MILE_RETURN_SUCCESS;
}


int32_t segment_compress(struct segment_manager* segment,MEM_POOL* mem_pool)
{
	// flush data to disk befor compress
	segment_checkpoint(segment);

	return	string_map_for_each(segment->index_fields,index_compress_iterator_func,(void*)mem_pool);
}


static int32_t index_recover_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	return index_field_recover((struct index_field_manager*)value, *(uint32_t*)user_data);
}

int32_t segment_recover(struct segment_manager* segment, uint32_t docid)
{
	// if compressed, don't recover.
	if (segment->meta_data->flag & SEGMENT_COMPRESS)
		return 0;

	if( bitmark_recover(segment->del_bitmap, docid) != 0 )
		return -1;
	if( data_field_recover(segment->data_field, docid) != 0)
		return -1;

	if(string_map_for_each(segment->index_fields, index_recover_iterator_func, (void*) &docid) != 0)
		return -1;

	// recover row_count and del_count
	int real_row_count = bitmark_count(segment->del_bitmap);
	assert(real_row_count <= docid);
	segment->meta_data->row_count = docid;
	segment->meta_data->del_count = docid - real_row_count;

	return 0;
}

int32_t segment_compress_switch(struct segment_manager* segment)
{
	return string_map_for_each(segment->index_fields,index_switch_iterator_func,NULL);
}



void segment_set_flag(struct segment_manager* segment, enum segment_stat flag)
{
	Mile_AtomicOrPtr(&segment->meta_data->flag,flag);	
}

enum segment_stat segment_get_flag(struct segment_manager* segment)
{
	return (enum segment_stat)Mile_AtomicGetPtr(&segment->meta_data->flag);
}


void segment_release(struct segment_manager* segment)
{
	bitmark_release(segment->del_bitmap);
	
	data_field_release(segment->data_field);

	string_map_for_each(segment->index_fields,index_release_iterator_func,NULL);

	return;
}


void segment_checkpoint(struct segment_manager* segment)
{
	bitmark_checkpoint(segment->del_bitmap);
	data_field_checkpoint(segment->data_field);


	string_map_for_each(segment->index_fields,index_checkpoint_iterator_func,NULL);
	msync(segment->meta_data,sizeof(struct segment_meta_data),MS_SYNC);    

	return;
}

static int32_t mmap_switch_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	struct index_field_manager* index_field = (struct index_field_manager*)value;
	
	return index_mmap_switch(index_field); 
}

int segment_mmap_switch( struct segment_manager *segment)
{
	if( !(Mile_AtomicGetPtr(&segment->meta_data->flag) & SEGMENT_FULL) ) {
		log_error( "segment not full" );
		return -1;
	}

	return string_map_for_each(segment->index_fields, mmap_switch_iterator_func, NULL);
}

