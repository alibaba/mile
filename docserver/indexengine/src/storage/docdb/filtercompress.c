/*
 * =====================================================================================
 *
 *       Filename:  hi_filtercompress.c
 *
 *    Description:  filter compress accomplishment
 *
 *        Version:  1.0
 *        Created:  2011年09月02日 13时53分34秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  shuai.li (algol), shuai.li@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */


#include	"filtercompress.h"


#if 1

/** 
 * filter_compress_init
 * @name:  filter_compress_init
 * @desc:  filter compress index init
 * @param  file_info
 * @param  mem_pool
 * @return 
 */
struct filter_compress_manager * filter_compress_init(char *work_space,	MEM_POOL *mem_pool)
{
	struct filter_compress_manager * filter_compress = \
			(struct	filter_compress_manager *)mem_pool_malloc(mem_pool, sizeof(struct filter_compress_manager)); 
	if(filter_compress == NULL)
	{
		log_error("filter_compress_init: malloc error!\n");
		return NULL;
	}

	if(work_space == NULL)
	{
		log_error("filter_compress_init argument: work_space is NULL!\n");
		return NULL;
	}

	memset(filter_compress,0,sizeof(struct filter_compress_manager));

	//read n_row n_value index_type
	sprintf(filter_compress->info_file_name,"%s/filter_info_compress.dat", work_space);
	sprintf(filter_compress->data_file_name,"%s/filter_compress.dat", work_space);
	sprintf(filter_compress->null_file_name,"%s/filter_null_compress.dat", work_space);
	sprintf(filter_compress->index_file_name,"%s/filter_compress.idx", work_space);

	filter_compress->info_map = (uint32_t *)get_mmap_memory(filter_compress->info_file_name, 64);

	memcpy(&filter_compress->n_row, filter_compress->info_map, 4);


	if(filter_compress->n_row == 0)
	{
		//第一次启动
		log_info("first time filter_hash_init");
		return filter_compress;
	}

	memcpy(&filter_compress->n_value, filter_compress->info_map+4, 4);
	memcpy(&filter_compress->unit_size, filter_compress->info_map+8, 4);
	memcpy(&filter_compress->idx_size, filter_compress->info_map+12, 1);
	memcpy(&filter_compress->index_type, filter_compress->info_map+13, 1);


	//根据索引类型加载相应的文件
	if(!filter_compress->index_type)
	{
		//no index
		if(filter_compress->unit_size != 0)
		{
			filter_compress->data_mmap = (uint8_t *)get_mmap_memory(filter_compress->data_file_name, filter_compress->n_row*filter_compress->unit_size);
			if(filter_compress->data_mmap == NULL)
			{
				log_error("mmap file:%s error!", filter_compress->data_file_name);
				return NULL;
			}
		}
		
		filter_compress->index_mmap = NULL;
		struct bitmark_config bitmark_config;
		strcpy(bitmark_config.work_space, work_space);
		strcpy(bitmark_config.bitmark_name, "filter_compress");
		bitmark_config.row_limit = filter_compress->n_row;
		filter_compress->bitmark = bitmark_init(&bitmark_config, mem_pool);
		if(filter_compress->bitmark == NULL)
		{
			log_error("bitmark_init error!");
			return NULL;
		}
		return filter_compress;
	}
	else if(filter_compress->index_type == 1)
	{
		//index
		filter_compress->data_mmap = (uint8_t *)get_mmap_memory(filter_compress->data_file_name, filter_compress->n_value*filter_compress->unit_size);
		filter_compress->index_mmap = (uint8_t *)get_mmap_memory(filter_compress->index_file_name,filter_compress->n_row*filter_compress->idx_size);
		if(filter_compress->index_mmap == NULL)
		{
			log_error("mmap file:%s error!", filter_compress->index_file_name);
			return NULL;
		}
		filter_compress->bitmark = NULL;
		return filter_compress;
	}
	else
	{
		//index
		filter_compress->data_mmap = (uint8_t *)get_mmap_memory(filter_compress->data_file_name, filter_compress->n_value*filter_compress->unit_size);
		
		struct bitmark_config bitmark_config;
		strcpy(bitmark_config.work_space, work_space);
		strcpy(bitmark_config.bitmark_name, "bitmap_index");
		bitmark_config.row_limit = filter_compress->n_row;
		filter_compress->bitmap_index = bitmark_init(&bitmark_config, mem_pool);
		if(filter_compress->bitmap_index == NULL)
		{
			log_error("bitmark_init error!");
			return NULL;
		}

		strcpy(bitmark_config.bitmark_name, "filter_compress");
		filter_compress->bitmark = bitmark_init(&bitmark_config, mem_pool);
		if(filter_compress->bitmark == NULL)
		{
			log_error("bitmark_init error!");
			return NULL;
		}

		return filter_compress;
	}

	
	return NULL;
}


/** 
 * filter_compress_load
 * @name:  filter_compress_load
 * @desc:  rearrange filter index
 * @param  storage
 * @param  unit_size: sizeof one element
 * @param  mem_pool
 * @return segment_filter_compress
 */
struct filter_compress_manager * filter_compress_load( 	
		struct storage_manager* storage, 				
		uint32_t unit_size,								
		MEM_POOL *mem_pool)
{
	char work_space[FILENAME_MAX_LENGTH];
	int work_len = strrchr(storage->file_name, '/') - storage->file_name;
	strncpy(work_space, storage->file_name, work_len);
	work_space[work_len] = '\0';

	//ѹ??֮ǰ??ֹ֮ǰѹ??һ?????ɵ????ļ?
	char filename[FILENAME_MAX_LENGTH];
	memset(filename,0,sizeof(filename));
	sprintf(filename,"%s/filter_info_compress.dat", work_space);
	remove(filename);

	memset(filename,0,sizeof(filename));
	sprintf(filename,"%s/filter_compress.dat", work_space);
	remove(filename);

	memset(filename,0,sizeof(filename));
	sprintf(filename,"%s/filter_null_compress.dat", work_space);
	remove(filename);

	memset(filename,0,sizeof(filename));
	sprintf(filename,"%s/filter_compress.idx", work_space);
	remove(filename);
	

	//创建filter_compress
	struct filter_compress_manager * filter_compress = 	filter_compress_init(work_space, mem_pool);

	if(filter_compress == NULL)
	{
		log_error("filter_compress_init error!");
		return NULL;
	}

	uint32_t n_row = storage->row_limit;
	uint16_t value_size = storage->value_size;
	MEM_POOL * hashtable_mempool = mem_pool_init(n_row * sizeof(struct bucket_entry));
	struct bucket_entry *bucket = (struct bucket_entry*)mem_pool_malloc(hashtable_mempool, n_row * sizeof(struct bucket_entry));
	memset(bucket, 0, n_row * sizeof(struct bucket_entry));
	//由于是定长，可以将所有类型转成64位数字比较
	//遍历storage
	uint32_t i, pos;
	uint8_t flag = 0;
	uint64_t value;
	uint32_t n_value = 0, n_null = 0;

	for ( i = 0; i < n_row; i += 1 )
	{
		if(bitmark_query(storage->null_bitmark, i) == 1)
		{
			n_null ++;
			continue;
		}
		memcpy(&value, storage->mem_mmaped+i*value_size, value_size);
		pos = value%n_row;
		flag = 0;
		do
		{
			if(bucket[pos].pos == 0)
			{
				bucket[pos].pos = 1;
				n_value ++;
				bucket[pos].value = value;
				flag = 1;
				break;
			}
			if(bucket[pos].value == value)
			{
				flag = 1;
				break;
			}
			pos = (pos+1)%n_row; 
		}while(pos != value%n_row);
		if(flag == 0)
		{
			log_error("bucket is too small!\n");
			return NULL;
		}
	}

	log_info("filter compress, n_null:%d, n_row:%d, n_value:%d", n_null, n_row, n_value);
	//已知n_value, n_null, n_row, value_size, 实际unit_size
	//判断采取哪种索引
	uint32_t s0 = n_row / 8 + 1 + n_row * unit_size; 	//no index
	uint8_t idx_size;
	if(n_value < 128)
		idx_size = 1;
	else if(n_value < 32768)
		idx_size = 2;
	else
		idx_size = 4;
	uint32_t s1 = n_row * idx_size + n_value * unit_size; //new index

	//如果只有2种类型的数据，直接用bitmap就可以了
	if(n_value == 2 )
	{
		memset(bucket, 0, n_row*sizeof(struct bucket_entry));
		for ( i = 0; i < n_row; i += 1 )
		{
			bucket[i].pos = -1;
		}
		//初始化原始数据
		filter_compress->data_mmap = (uint8_t *)get_mmap_memory(filter_compress->data_file_name, n_value*unit_size);

		struct bitmark_config bitmark_config;
		strcpy(bitmark_config.work_space, work_space);
		strcpy(bitmark_config.bitmark_name, "bitmap_index");
		bitmark_config.row_limit = n_row;
		filter_compress->bitmap_index = bitmark_init(&bitmark_config, mem_pool);

		if(filter_compress->bitmap_index == NULL)
		{
			log_error("bitmark_init error!");
			return NULL;
		}

		if(n_null != 0)
		{
			strcpy(bitmark_config.bitmark_name, "filter_compress");
			filter_compress->bitmark = bitmark_init(&bitmark_config, mem_pool);
			memcpy(filter_compress->bitmark->mem_mmaped,storage->null_bitmark->mem_mmaped,storage->row_limit/BYTE_SIZE+1);
			bitmark_checkpoint(filter_compress->bitmark);	
		}
		else
		{
			filter_compress->bitmark = NULL;
		}
		
		n_value = 0;
		for ( i = 0; i < n_row; i += 1 )
		{
			if(bitmark_query(storage->null_bitmark, i) == 1)
			{
				continue;
			}
			memcpy(&value, storage->mem_mmaped+i*value_size, value_size);
			pos = value%n_row;
			flag = 0;
			do
			{
				if(bucket[pos].pos == -1)
				{
					memcpy(filter_compress->data_mmap+n_value*unit_size, &value, unit_size);
					bucket[pos].pos = n_value;
					
					if(bucket[pos].pos == 1)
						bitmark_set(filter_compress->bitmap_index,i);
					
					n_value ++;
					bucket[pos].value = value;
					flag = 1;
					break;
				}
				if(bucket[pos].value == value)
				{
					if(bucket[pos].pos == 1)
						bitmark_set(filter_compress->bitmap_index,i);
					flag = 1;
					break;
				}
				pos = (pos+1)%n_row; 
			}while(pos != value%n_row);
			if(flag == 0)
			{
				log_error("bucket is too small!\n");
				return NULL;
			}
		}

		filter_compress->index_type = 2;
		msync(filter_compress->index_mmap, n_row*idx_size, MS_ASYNC);
		msync(filter_compress->data_mmap, n_value*unit_size, MS_ASYNC);
	}
	else if(s1 < s0)
	{
		//new index for filter
		int32_t null = -1;
		memset(bucket, 0, n_row*sizeof(struct bucket_entry));
		for ( i = 0; i < n_row; i += 1 )
		{
			bucket[i].pos = -1;
		}
		filter_compress->index_mmap = (uint8_t *)get_mmap_memory(filter_compress->index_file_name, n_row*idx_size);
		filter_compress->data_mmap = (uint8_t *)get_mmap_memory(filter_compress->data_file_name, n_value*unit_size);
		filter_compress->bitmark = NULL;
		n_value = 0;
		for ( i = 0; i < n_row; i += 1 )
		{
			if(bitmark_query(storage->null_bitmark, i) == 1)
			{
				memcpy(filter_compress->index_mmap + i*idx_size, &null, idx_size);
				continue;
			}
			memcpy(&value, storage->mem_mmaped+i*value_size, value_size);
			pos = value%n_row;
			flag = 0;
			do
			{
				if(bucket[pos].pos == -1)
				{
					memcpy(filter_compress->data_mmap+n_value*unit_size, &value, unit_size);
					bucket[pos].pos = n_value;
					memcpy(filter_compress->index_mmap+i*idx_size, &n_value, idx_size);
					n_value ++;
					bucket[pos].value = value;
					flag = 1;
					break;
				}
				if(bucket[pos].value == value)
				{
					memcpy(filter_compress->index_mmap+i*idx_size, &bucket[pos].pos, idx_size);
					flag = 1;
					break;
				}
				pos = (pos+1)%n_row; 
			}while(pos != value%n_row);
			if(flag == 0)
			{
				log_error("bucket is too small!\n");
				return NULL;
			}
		}

		filter_compress->index_type = 1;
		msync(filter_compress->index_mmap, n_row*idx_size, MS_ASYNC);
		msync(filter_compress->data_mmap, n_value*unit_size, MS_ASYNC);

	}
	else if(unit_size < value_size)
	{
		struct bitmark_config bitmark_config;
		strcpy(bitmark_config.work_space, work_space);
		strcpy(bitmark_config.bitmark_name, "filter_compress");
		bitmark_config.row_limit = storage->row_limit;
		filter_compress->bitmark = bitmark_init(&bitmark_config, mem_pool);
		memcpy(filter_compress->bitmark->mem_mmaped,storage->null_bitmark->mem_mmaped,storage->row_limit/BYTE_SIZE+1);
		bitmark_checkpoint(filter_compress->bitmark);

		filter_compress->index_mmap = NULL;

		//compress  unit_size是0的话，说明索引没有值
		if(unit_size != 0)
		{
			filter_compress->data_mmap = (uint8_t *)get_mmap_memory(filter_compress->data_file_name, n_row*unit_size);
			for ( i = 0; i < n_row; i += 1 )
			{
				memcpy(filter_compress->data_mmap+i*unit_size, storage->mem_mmaped+i*value_size, unit_size);
			}
			filter_compress->index_type = 0;
			msync(filter_compress->data_mmap, n_row*unit_size, MS_ASYNC);
		}
	}
	else
	{
		//什么都不用操作
		mem_pool_destroy(hashtable_mempool);
		log_warn("压缩不划算，不进行压缩处理");
		if(filter_compress->info_map != NULL)
			munmap(filter_compress->info_map,64);
		remove(filter_compress->data_file_name);
		remove(filter_compress->index_file_name);
		remove(filter_compress->info_file_name);
		remove(filter_compress->null_file_name);
		return (struct filter_compress_manager*)0x1;
	}

	filter_compress->n_row = n_row;
	filter_compress->n_value = n_value;
	filter_compress->unit_size = unit_size;
	filter_compress->idx_size = idx_size;
	//同步信息到磁盘
	memcpy(filter_compress->info_map, &n_row, 4);
	memcpy(filter_compress->info_map+4, &n_value, 4);
	memcpy(filter_compress->info_map+8, &unit_size, 4);
	memcpy(filter_compress->info_map+12, &idx_size, 1);
	memcpy(filter_compress->info_map+13, &filter_compress->index_type, 1);
	//msync
	msync(filter_compress->info_map, 64, MS_ASYNC);
	mem_pool_destroy(hashtable_mempool);

	return filter_compress;
}



/** 
 * filter_compress_query
 * @name:  filter_compress_query
 * @desc:  get value by rowid 
 * @param  filter_index --> filter compress index
 * @param  docid
 * @param  mem_pool
 * @return value, null if not exist
 */
struct low_data_struct* filter_compress_query( 			
		struct filter_compress_manager* filter_index, 	
		uint32_t docid,									
		MEM_POOL *mem_pool)
{
	struct low_data_struct *result = NULL;
	result = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct));
	memset(result, 0, sizeof(struct low_data_struct));
	result->data = mem_pool_malloc(mem_pool, filter_index->unit_size);
	memset(result->data, 0, filter_index->unit_size);
	
	if(filter_index->index_type == 2)
	{
		if(filter_index->bitmark != NULL && bitmark_query(filter_index->bitmark, docid) == 1)
			return result; //null
			
		int32_t pos = 0;
		pos = bitmark_query(filter_index->bitmap_index,docid);
		if(pos < 0)
			return result;
		pos = pos == 0?1:0;
		memcpy(result->data, filter_index->data_mmap+pos*filter_index->unit_size, filter_index->unit_size);
		result->len = filter_index->unit_size;
	}
	else if(filter_index->index_type == 1)
	{
		int32_t pos = 0;
		if(filter_index->idx_size == 1)
			pos = (int32_t)*(int8_t *)(filter_index->index_mmap+docid*filter_index->idx_size);
		else if(filter_index->idx_size == 2)
			pos = (int32_t)*(int16_t *)(filter_index->index_mmap+docid*filter_index->idx_size);
		else
			pos = (int32_t)*(int32_t *)(filter_index->index_mmap+docid*filter_index->idx_size);

		if(pos == -1)
			return result; //null
		memcpy(result->data, filter_index->data_mmap+pos*filter_index->unit_size, filter_index->unit_size);
		result->len = filter_index->unit_size;
	}
	else
	{
		if(bitmark_query(filter_index->bitmark, docid) == 1)
			return result; //null

		memcpy(result->data, filter_index->data_mmap+docid*filter_index->unit_size, filter_index->unit_size);
		result->len = filter_index->unit_size;
	}
	return result;
}


/** 
 * filter_compress_release
 * @name:  filter_compress_release
 * @desc: release filter compress index
 * @param filter_compress
 * @return 
 */
void filter_compress_release(struct filter_compress_manager * filter_compress)
{
	uint32_t n_row = filter_compress->n_row;
	uint32_t n_value = filter_compress->n_value;
	uint32_t unit_size = filter_compress->unit_size;
	uint32_t idx_size = filter_compress->idx_size;
	char index_type = filter_compress->index_type;

	
	if(index_type == 2)
	{
		bitmark_release(filter_compress->bitmap_index);
		
		if(filter_compress->bitmark != NULL)
			bitmark_release(filter_compress->bitmark);
		
		if(filter_compress->data_mmap != NULL)
		{
			munmap(filter_compress->data_mmap, n_value*unit_size);
			filter_compress->data_mmap = NULL;
		}

	}
	else if(index_type)
	{
		if(filter_compress->index_mmap != NULL)
		{
			munmap(filter_compress->index_mmap, n_row*idx_size);
			filter_compress->index_mmap = NULL;
		}
		if(filter_compress->data_mmap != NULL)
		{
			munmap(filter_compress->data_mmap, n_value*unit_size);
			filter_compress->data_mmap = NULL;
		}
	}
	else
	{
		if(filter_compress->data_mmap != NULL)
		{
			munmap(filter_compress->data_mmap, n_row*unit_size);
			filter_compress->data_mmap = NULL;
		}
		bitmark_release(filter_compress->bitmark);
	}

	if(filter_compress->info_map != NULL)
		munmap(filter_compress->info_map,64);
}

/** 
 * filter_compress_destroy
 * @name:  filter_compress_destroy
 * @desc: destroy filter compress index
 * @param filter_compress
 * @return 
 */
void filter_compress_destroy(struct filter_compress_manager * filter_compress)
{
	filter_compress_release(filter_compress);
	char index_type = filter_compress->index_type;
	remove(filter_compress->data_file_name);
	if(index_type == 2)
	{
		bitmark_destroy(filter_compress->bitmap_index);
		if(filter_compress->bitmark != NULL)
			bitmark_destroy(filter_compress->bitmark);
	}
	else if(index_type == 1)
	{
		remove(filter_compress->index_file_name);
	}
	else
	{
		bitmark_destroy(filter_compress->bitmark);
	}
}

#endif


