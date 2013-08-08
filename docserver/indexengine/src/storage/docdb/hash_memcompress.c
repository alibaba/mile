
#ifdef USE_MEM_COMPRESS

#include "hash_memcompress.h"

static struct hash_bucket *global_bucket = NULL;

struct hash_compress_manager *hash_commpress_init( struct hash_compress_config *config, MEM_POOL *mem_pool )
{
	struct hash_compress_manager *hash_compress = (struct hash_compress_manager *)mem_pool_malloc( mem_pool, sizeof(struct hash_compress_manager));

	memset( hash_compress, 0, sizeof(struct hash_compress_manager));

	hash_compress->row_limit = config->row_limit;

	/*需要算上空值，那个指定的桶*/
	hash_compress->hash_mod = config->row_limit + 1;
	hash_compress->hash_compress_num = config->hash_compress_num;

	sprintf( hash_compress->data_file_name, "%s/hash_compress.dat", config->work_space );
	sprintf( hash_compress->index_file_name, "%s/hash_compress.idx", config->work_space );
	sprintf( hash_compress->info_file_name, "%s/hash_info_compress.idx", config->work_space );

	hash_compress->info_mmap =
		(char *)get_mmap_memory( hash_compress->info_file_name, 64 );

	if( hash_compress->info_mmap == NULL ) {
		log_error( "get mmap for file %s error!", hash_compress->info_file_name );
		return NULL;
	}

	memcpy( &hash_compress->bucket_count, hash_compress->info_mmap, 4 );
	memcpy( &hash_compress->doc_count, hash_compress->info_mmap + 4, 4 );

	if( hash_compress->bucket_count == 0 )
		return hash_compress;  //first time

	hash_compress->index_mmap =
		(struct hash_seeks *)get_mmap_memory( hash_compress->index_file_name, hash_compress->bucket_count * sizeof(struct hash_seeks));

	if( hash_compress->index_mmap == NULL )	{
		log_error( "get mmap for file %s error!", hash_compress->index_file_name );
		return NULL;
	}

	//打开数据文件
	hash_compress->data_mmap =
		(char *)get_mmap_memory( hash_compress->data_file_name, hash_compress->doc_count * sizeof(uint32_t));

	if( hash_compress->data_mmap == NULL ) {
		log_error( "get mmap for file %s error!", hash_compress->data_file_name );
		return NULL;
	}

	return hash_compress;
}

static int hash_index_compare( const void *p1, const void *p2 )
{
	if((global_bucket + *(uint32_t *)p1)->hash_value > (global_bucket + *(uint32_t *)p2)->hash_value )
		return 1;
	else if((global_bucket + *(uint32_t *)p1)->hash_value < (global_bucket + *(uint32_t *)p2)->hash_value )
		return -1;
	else
		return 0;
}

struct hash_compress_manager *hash_compress_load( struct hash_index_manager *hash_index, uint32_t hash_compress_num, MEM_POOL *mem_pool )
{
	//初始化segment_hash_compress结构
	struct hash_compress_config config;

	if( hash_index == NULL ) {
		log_error( "hash 列为空" );
		return NULL;
	}

	log_error( "start load" );

	memset( &config, 0, sizeof(struct hash_compress_config));
	config.row_limit = hash_index->limit;
	config.hash_compress_num = hash_compress_num;
	strncpy( config.work_space, hash_index->file_name, strrchr( hash_index->file_name, '/' ) - hash_index->file_name );


	//在压缩之前，先把上次压缩未成功遗留下来的脏文件清空
	char filename[FILENAME_MAX_LENGTH];
	
	memset(filename,0,sizeof(filename));
	sprintf( filename, "%s/hash_compress.dat", config.work_space );
	remove(filename);

	memset(filename,0,sizeof(filename));
	sprintf( filename, "%s/hash_compress.idx", config.work_space );
	remove(filename);

	memset(filename,0,sizeof(filename));
	sprintf( filename, "%s/hash_info_compress.idx", config.work_space );
	remove(filename);

	struct hash_compress_manager *hash_compress = hash_commpress_init( &config, mem_pool );
	if( !hash_compress )
		return NULL;


	uint32_t *hash_bucket = (uint32_t *)malloc((hash_index->hashmod + 1) * sizeof(uint32_t));

	uint32_t i, j;
	for( i = 0; i < hash_index->hashmod + 1; i++ ) {
		hash_bucket[i] = i;
	}

	//对hash_value进行排序
	struct hash_bucket *bucket = hash_index->mem_mmaped;
	global_bucket = hash_index->mem_mmaped;
	log_error( "%s begin qsort...", hash_compress->index_file_name );
	qsort( hash_bucket, hash_index->hashmod + 1, sizeof(uint32_t), hash_index_compare );
	log_error( "%s end qsort...", hash_compress->index_file_name );

	struct rowid_list *docids = NULL;
	struct rowid_list_node *p;

	size_t doc_len = sizeof(uint32_t);

	j = 0;
	while( j < hash_compress->hash_mod && (bucket + hash_bucket[j])->hash_value == 0 ) j++;

	hash_compress->bucket_count = hash_compress->hash_mod - j;

	hash_compress->index_mmap =	\
		(struct hash_seeks *)get_mmap_memory( hash_compress->index_file_name, hash_compress->bucket_count * sizeof(struct hash_seeks));


	hash_compress->data_mmap = \
		(char *)get_mmap_memory( hash_compress->data_file_name, hash_index->limit * sizeof(uint32_t));

	struct hash_seeks *h_seek = hash_compress->index_mmap;
	MEM_POOL *mem_pool_local = mem_pool_init( MB_SIZE );
	uint32_t current_offset = 0;

	for( i = j; i < hash_compress->hash_mod; i += 1 ) {
		(h_seek + i - j)->hash_value = (bucket + hash_bucket[i])->hash_value;
		(h_seek + i - j)->offset = current_offset;
		docids = get_rowid_list( hash_index, NEXT_DOC_ROW_STRUCT( hash_index->doclist, (bucket + hash_bucket[i])->offset ), mem_pool_local );
		(h_seek + i - j)->len = doc_len * docids->rowid_num;

		p = docids->head;
		while( p != docids->tail ) {
			memcpy( hash_compress->data_mmap + current_offset, p->rowid_array, ROWID_ARRAY_SIZE * sizeof(uint32_t));
			current_offset += ROWID_ARRAY_SIZE * sizeof(uint32_t);
			p = p->next;
		}
		memcpy( hash_compress->data_mmap + current_offset, p->rowid_array, (h_seek + i - j)->offset + (h_seek + i - j)->len - current_offset );
		current_offset = (h_seek + i - j)->offset + (h_seek + i - j)->len;
		mem_pool_reset( mem_pool_local );
	}
	free( hash_bucket );
	mem_pool_destroy( mem_pool_local );

	msync( hash_compress->index_mmap, hash_compress->bucket_count * sizeof(struct hash_seeks), MS_SYNC );
	msync( hash_compress->data_mmap, current_offset, MS_SYNC );

	//重新映射
	munmap( hash_compress->data_mmap, current_offset );
	hash_compress->data_mmap = \
		(char *)get_mmap_memory( hash_compress->data_file_name, current_offset );

	hash_compress->doc_count = current_offset / sizeof(uint32_t);

	memcpy( hash_compress->info_mmap, &hash_compress->bucket_count, 4 );
	memcpy( hash_compress->info_mmap + 4, &hash_compress->doc_count, 4 );
	msync( hash_compress->info_mmap, 64, MS_SYNC );

	return hash_compress;

}



struct rowid_list *hash_compress_query( struct hash_compress_manager *hash_compress, struct low_data_struct *data, MEM_POOL *mem_pool )
{
	uint64_t hash_value;
	struct hash_seeks *hseek = NULL;
	struct rowid_list *rlist = rowid_list_init( mem_pool );

	//根据value做一次hash
	PROFILER_BEGIN( "hash_compress_query" );
	if((hash_value = get_hash_value( data )) < 0 ) {
		PROFILER_END();
		return NULL;
	}

	//对边界做检查
	if(hash_compress->bucket_count == 0)
	{
		PROFILER_END();
		return rlist;
	}

	//二分查找法
	// TODO : use bsearch

	uint32_t left = 0;
	uint32_t right = hash_compress->bucket_count - 1;
	while( left <= right ) {
		uint32_t mid = (left + right + 1) / 2;
		hseek = hash_compress->index_mmap + mid;
		if( hseek->hash_value < hash_value ) {
			left = mid + 1;
		}
		else if( hseek->hash_value > hash_value ) {
			if( 0 == mid ) {
				break;
			}
			right = mid - 1;
		}
		else {
			break;
		}
	}

	if( hseek->hash_value != hash_value ) {
		log_debug( "hseek %llu 该hash值不存在 %llu", hseek->hash_value, hash_value );
		PROFILER_END();
		return rlist;
	}

	void *buffer = hash_compress->data_mmap + hseek->offset;

	rowid_list_batch_add( mem_pool, rlist, (uint32_t *)buffer, hseek->len / sizeof(uint32_t));

	PROFILER_END();
	return rlist;
}

void hash_compress_release( struct hash_compress_manager *hash_compress )
{
	assert( hash_compress != NULL );

	// unmap索引内存
	munmap( hash_compress->index_mmap, hash_compress->bucket_count * sizeof(struct hash_seeks));
	munmap( hash_compress->data_mmap, hash_compress->doc_count * sizeof(uint32_t));
	munmap( hash_compress->info_mmap, 64 );

	return;
}

void hash_compress_destroy( struct hash_compress_manager *hash_compress )
{
	if( hash_compress == NULL ) ;
	return;

	hash_compress_release( hash_compress );

	remove( hash_compress->data_file_name );
	remove( hash_compress->index_file_name );
	remove( hash_compress->info_file_name );

	return;
}

#endif // USE_MEM_COMPRESS
