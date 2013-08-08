/*
 * =====================================================================================
 *
 *       Filename:  hi_table.c
 *
 *    Description:  表的结构信息，管理所有的段
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
 

#include "table.h"

static int32_t table_pk_check(struct table_manager* table,uint32_t row_id,struct row_data* rdata,MEM_POOL* mem_pool);

static struct segment_manager* init_segment_manager_instance(struct table_manager* table,struct segment_meta_data* meta_data, uint16_t sid);

static int32_t table_add_segment(struct table_manager* table,int32_t error,MEM_POOL* mem_pool);

static int32_t table_build_index(struct table_manager* table,struct index_field_meta* index_meta,char* field_name,char* index_field_name,enum index_key_alg index_type,enum field_types data_type);



static void query_data_and_insert_index(struct segment_manager* segment,
									    uint32_t docid,
									    enum index_key_alg index_type,
									    enum field_types data_type,
									    char* field_name,
										char* index_field_name,
									    MEM_POOL* mem_pool);



struct table_manager* table_init(struct table_config* config,MEM_POOL* mem_pool)
{
	struct table_manager* table = (struct table_manager*)mem_pool_malloc(mem_pool,sizeof(struct table_manager));
	memset(table,0,sizeof(struct table_manager));

	/*导入memap到内存中的运行时数据信息*/
	table->table_meta = config->table_meta;

	table->mem_pool = mem_pool;

	/*初始化目录*/
	table->storage_dirs = all_str_append(config->storage_dirs, mem_pool, "/%s", table->table_meta->table_name);
	table->work_space = table->storage_dirs->strs[0];
	if(mkdirs(table->work_space) < 0)
	{
		log_error("table建立目录不准确");
		return NULL;
	}

	if(config->max_segment_num == 0)
		table->max_segment_num = MAX_SEGMENT_NUM;
	
	table->max_segment_num = config->max_segment_num;
	table->row_limit = config->row_limit;
	table->hash_compress_num = config->hash_compress_num;

	//初始化所有的段meta信息
	sprintf(table->segment_meta_filename,"%s/segment.meta",table->work_space);
	table->segment_meta = (struct segment_meta_data*)get_mmap_memory(table->segment_meta_filename,SEGMENT_RUNTIME_SIZE(table));
	assert(table->segment_meta);

	table->segments = (struct segment_manager**)mem_pool_malloc(mem_pool,sizeof(struct segment_manager*)*
		table->max_segment_num);
	memset(table->segments,0,sizeof(struct segment_manager*) * table->max_segment_num);
		
	
	//初始化各个段信息
	struct segment_meta_data* meta_data = table->segment_meta;
	uint16_t i;
	struct segment_manager* segment = NULL;
	for(i=0;i<table->max_segment_num;i++,meta_data++)
	{
		//判断段是否已经初始化过
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;	

		segment = init_segment_manager_instance(table,meta_data,i);

		/*如果系统刚开始启动时，创建时间需要设置*/
	    if(meta_data->create_time== 0L)
	    {
	   	   meta_data->create_time = time(NULL);
		   Mile_AtomicOrPtr(&meta_data->flag,SEGMENT_INIT);
	    }

		assert(segment);

		table->segments[i] = segment;
	}


	//将所有列信息转换成string_map结构
	table->index_meta_hash = init_string_map(mem_pool,MAX_INDEX_FIELD_NUM);
	struct index_field_meta* index_meta = NULL;
	struct table_meta_data* table_meta = table->table_meta;
	for(i=0; i<table_meta->index_field_count; i++)
	{
		index_meta = (struct index_field_meta*)mem_pool_malloc(mem_pool,sizeof(struct index_field_meta));
		memset(index_meta,0,sizeof(struct index_field_meta));
		memcpy(index_meta,table_meta->index_meta+i,sizeof(struct index_field_meta));
		
		string_map_put(table->index_meta_hash, table_meta->index_meta[i].field_name,(void*)index_meta,1);
	}

	//初始化互斥锁
	if(pthread_mutex_init(&table->write_protect_locker,NULL) != 0)
	{
		log_error("互斥锁初始化失败");
		return NULL;
	}

	if(pthread_rwlock_init(&table->read_protect_locker,NULL) != 0)
	{
		log_error("读写锁初始化失败");
		return NULL;
	}

	table->store_raw = config->store_raw;

	//初始化好设置table的标志
	Mile_AtomicOrPtr(&table->table_meta->stat,TABLE_INIT);

	return table;
}



//添加一段信息
//需要区别如果segment_current段已经被初始化了，说明这个时候，所有的段已经开辟完毕
static int32_t table_add_segment(struct table_manager* table,int32_t error,MEM_POOL* mem_pool)
{
	char segment_name[MAX_SEGMENT_NAME];
	struct segment_meta_data* meta_data;
	struct table_meta_data* table_meta = table->table_meta;
	uint16_t i;
	int32_t ret = MILE_RETURN_SUCCESS;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	//找一个段的坑
	meta_data = table->segment_meta;

	if(error == ERROR_EXCEED_LIMIT)
	{
		for(i=0;i<table->max_segment_num;i++)
		{
			//如果该段未初始化，则找到
			if(!(Mile_AtomicGetPtr(&(meta_data + (i + table->table_meta->segment_current) % table->max_segment_num)->flag)&SEGMENT_INIT))
				break;

		}
		if(i == table->max_segment_num)
		{
			log_error("所有的段已经开辟完了");
			return ERROR_EXCEED_SEGMENT_NUM;
		}

		i = (i + table->table_meta->segment_current) % table->max_segment_num;
		meta_data += i;

		log_info("找到%u段号，并置为当前段",i);

		Mile_AtomicSetPtr(&table->table_meta->segment_current,i);

		//重新导入index_meta信息到string_map里，这里涉及到修改index_meta_hash，和查询调用check_index发生线程安全性问题
		pthread_rwlock_wrlock(&table->read_protect_locker);
		for(i=0; i<table_meta->index_field_count; i++)
		{
			string_map_remove(table->index_meta_hash,(table_meta->index_meta+i)->field_name);
			string_map_put(table->index_meta_hash, (table_meta->index_meta+i)->field_name,(void*)(table_meta->index_meta+i),1);
		}
		pthread_rwlock_unlock(&table->read_protect_locker);
	}
	else
	{
		meta_data += Mile_AtomicGetPtr(&table->table_meta->segment_current);	
	}
	
	memset(segment_name,0,sizeof(segment_name));
	sprintf(segment_name,"%s_segment_%06u",table->table_meta->table_name,Mile_AtomicGetPtr(&table->table_meta->segment_current));
	
	memset(meta_data,0,sizeof(struct segment_meta_data));

	//在添加段的时候，查看对应的段目录是否存在，如果存在则报错
	char segment_workspace[FILENAME_MAX_LENGTH];
	struct stat stats;
	memset(segment_workspace,0,sizeof(segment_workspace));
	sprintf(segment_workspace,"%s/%s",table->work_space,segment_name);
	if (lstat(segment_workspace, &stats) == 0 && S_ISDIR (stats.st_mode)) 
    {
		log_error("目录已经存在 %s",segment_workspace);
		return ERROR_SEGMENT_INIT_FAILED;
    }

	//这里会涉及到访问索引的原始信息，所以需要加读锁
	pthread_rwlock_rdlock(&table->read_protect_locker);
	table->segments[Mile_AtomicGetPtr(&table->table_meta->segment_current)] = 
		init_segment_manager_instance(table,meta_data,Mile_AtomicGetPtr(&table->table_meta->segment_current));

	if(table->segments[Mile_AtomicGetPtr(&table->table_meta->segment_current)] == NULL)
	{
		pthread_rwlock_unlock(&table->read_protect_locker);
		return ERROR_SEGMENT_INIT_FAILED;
	}

	/*bugfix 将修改段的时间放到外面 由于将段初始化就将标志放开，这个时候，段的信息还未赋值到table->segments数组里
	进行table_index_equal_query的时候，就会出现线程安全问题 by yunliang.shi 2012-12-21*/
    if(meta_data->create_time== 0L)
    {
   	   meta_data->create_time = time(NULL);
	   Mile_AtomicOrPtr(&meta_data->flag,SEGMENT_INIT);
    }
	pthread_rwlock_unlock(&table->read_protect_locker);

	return ret;
}



int32_t table_ensure_index(struct table_manager* table,
						 char* field_name,
						 enum index_key_alg index_type,
						 enum field_types data_type,
						 MEM_POOL* mem_pool)
{
	struct table_meta_data* table_meta = table->table_meta;
	struct index_field_meta* index_meta = table_meta->index_meta;

	uint16_t i;
	uint8_t j;
	int32_t ret;


	char  index_field_name[MAX_FIELD_NAME];
	memset(index_field_name, 0, sizeof(index_field_name));
	if(index_type == HI_KEY_ALG_FULLTEXT)
	{
		sprintf(index_field_name, "$%s", field_name);
	}
	else
	{
		strcpy(index_field_name, field_name);
	}


	pthread_rwlock_rdlock(&table->read_protect_locker);
	for(i=0; i<table_meta->index_field_count; i++,index_meta++)
	{
		if(strcmp(index_meta->field_name,index_field_name) == 0)
		{
			//先查找，判断有没有，防止重复提交
			for(j=0; j<index_meta->index_count; j++)
			{
				if(index_meta->indexs[j].index_type == index_type)
				{
					log_warn("%s %d索引已建立",index_field_name,index_type);
					pthread_rwlock_unlock(&table->read_protect_locker);
					return MILE_RETURN_SUCCESS;
				}
			}

			//没找到，则加入
			pthread_rwlock_unlock(&table->read_protect_locker);

			//索引的原始信息只有新增段的时候会访问到，只有这里会进行修改
			pthread_rwlock_wrlock(&table->read_protect_locker);
			index_meta->indexs[index_meta->index_count].index_type = index_type;
			index_meta->indexs[index_meta->index_count].data_type = data_type;
			index_meta->index_count++;
			pthread_rwlock_unlock(&table->read_protect_locker);
			
			ret = table_build_index(table,index_meta,field_name,index_field_name,index_type,data_type);
			
			return ret;
		}
	}

	//连列名都没找到
	pthread_rwlock_unlock(&table->read_protect_locker);

	pthread_rwlock_wrlock(&table->read_protect_locker);
	index_meta = table_meta->index_meta + table_meta->index_field_count;
	strcpy(index_meta->field_name,index_field_name);
	index_meta->index_count = 1;
	index_meta->indexs[0].index_type = index_type;
	index_meta->indexs[0].data_type = data_type;
	table_meta->index_field_count++;
	pthread_rwlock_unlock(&table->read_protect_locker);
	
	ret = table_build_index(table,index_meta,field_name,index_field_name,index_type,data_type);

	return ret;
}


int32_t table_del_index(struct table_manager* table,char* field_name,enum index_key_alg index_type)
{
	struct table_meta_data* table_meta = table->table_meta;
	struct index_field_meta* index_meta = table_meta->index_meta;

	uint16_t i;
	uint8_t j;
	uint8_t found = 0;

	char  index_field_name[MAX_FIELD_NAME];
	memset(index_field_name, 0, sizeof(index_field_name));
	if(index_type == HI_KEY_ALG_FULLTEXT)
	{
		sprintf(index_field_name, "$%s", field_name);
	}
	else
	{
		strcpy(index_field_name, field_name);
	}
	

	log_info("开始执行删除索引列%s 索引类型%u",index_field_name,index_type);

	log_info("先删除索引的元数据信息");

	//索引的元数据信息，只有在删除和新建索引的时候，db上层控制
	for(i=0; i<table_meta->index_field_count; i++,index_meta++)
	{
		if(strcmp(index_meta->field_name,index_field_name) == 0)
		{
			//先查找，判断有没有，防止重复提交
			for(j=0; j<index_meta->index_count; j++)
			{
				if(index_meta->indexs[j].index_type == index_type)
				{
					uint8_t k;
					found = 1;
					
					for(k = j;k < index_meta->index_count-1; k++)
					{	
						memmove(index_meta->indexs+k,index_meta->indexs+k+1,sizeof(struct index_field_stat));
					}
					
					//清空最后一个
					memset(index_meta->indexs+k,0,sizeof(struct index_field_stat));

					//说明这个列只有唯一的一个索引
					if(--index_meta->index_count == 0)
					{
						uint16_t m;
						for(m = i;m<table_meta->index_field_count-1; m++)
						{
							memmove(table_meta->index_meta + m, table_meta->index_meta+m+1, sizeof(struct index_field_meta));
						}

						//清空最后一个
						memset(table_meta->index_meta + m,0,sizeof(struct index_field_meta));
						
						table_meta->index_field_count--;
					}

					break;
				}
			}
		}

		if(found)
			break;
	}	

	if(!found)
	{
		log_warn("未找到需要删除的索引列 %s  %u",index_field_name,index_type);
		return MILE_RETURN_SUCCESS;
	}

	log_info("开始删除string_map的索引，插入将停止该索引的插入...");

	//加写锁
	//读保护，防止删除索引的时候，防止读的时候，会有线程安全问题
	pthread_rwlock_wrlock(&table->read_protect_locker);
	
	string_map_remove(table->index_meta_hash,index_field_name);

	for(i=0; i<table_meta->index_field_count; i++)
	{
		string_map_remove(table->index_meta_hash,(table_meta->index_meta+i)->field_name);
		string_map_put(table->index_meta_hash, (table_meta->index_meta+i)->field_name,(void*)(table_meta->index_meta+i),1);
	}

	struct segment_meta_data* meta_data = table->segment_meta;
	
	for(i=0;i<table->max_segment_num;i++,meta_data++)
	{
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;

		segment_del_index(table->segments[i],index_field_name,index_type);
	}
	
	pthread_rwlock_unlock(&table->read_protect_locker);

	return MILE_RETURN_SUCCESS;
}



static void query_data_and_insert_index(struct segment_manager* segment,
									    uint32_t docid,
									    enum index_key_alg index_type,
									    enum field_types data_type,
									    char* field_name,
										char* index_field_name,
									    MEM_POOL* mem_pool)
{
	struct low_data_struct* data = NULL;
	struct low_data_struct null_data;
	int32_t ret;
	null_data.len = 0;
	
	//判断有没有删除
	if(segment_is_docid_deleted(segment,docid))
		return;
	
	//查询原始数据列
	data = segment_data_query_col(segment,index_field_name,docid,mem_pool);


	//插入到索引列
	if(data != NULL)
	{
		//替换成索引列名字
		data->field_name = index_field_name;

		//校验数据类型是否正确
		if(data->type != data_type)
		{
			if(data->type != HI_TYPE_NULL)
				log_error("%s %u 索引类型不匹配 data_type:%u  插入数据 data_type:%u",field_name,
																					 index_type,
																					 data_type,
																					 data->type);
			null_data.field_name = data->field_name;
			ret = segment_index_insert(segment,&null_data,index_type,docid,mem_pool);
		}
		else
		{
			ret = segment_index_insert(segment,data,index_type,docid,mem_pool);
		}

		
		if(ret < 0)
		{
			log_error("%s %d索引插入失败 ret %d",field_name,index_type,ret);
			return;
		}
	}

	return;
}

static int32_t table_build_index(struct table_manager* table,struct index_field_meta* index_meta,char* field_name, char* index_field_name, enum index_key_alg index_type,enum field_types data_type)
{
	MEM_POOL* mem_pool_local = mem_pool_init(MB_SIZE);
	uint16_t i;
	uint8_t j;
	int32_t ret;	
	struct segment_meta_data* meta_data;

	log_info("开始建立 列名:%s 索引:%u...",index_field_name,index_type);
	
	//先对当前段添加索引
	uint16_t sid = Mile_AtomicGetPtr(&table->table_meta->segment_current);

	if(!(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag) & SEGMENT_INIT))
	{
		log_info("当前段未初始化 %u",sid);
		goto FIN;
	}
	
	log_info("开始对当前段:%u建立索引...",sid);


	//这个地方也必须加写锁，因为会对segment层的index的hash管理结构进行操作，在读取的时候，存在线程安全问题
	pthread_rwlock_wrlock(&table->read_protect_locker);
	ret = segment_ensure_index(table->segments[sid],index_field_name,index_type,table->mem_pool);
	pthread_rwlock_unlock(&table->read_protect_locker);
	
	if(ret != 0)
		return ret;

	//先追当前段
	uint32_t docid;
	struct index_field_meta* index_hash_meta;
	for(docid = 0; docid < segment_get_rowcount(table->segments[sid]); docid++)
	{
		mem_pool_reset(mem_pool_local);

		//这个地方不用加读锁，只会读取原始的数据，对新的索引列插入数据，而这个新的索引数据是不会被查的
		query_data_and_insert_index(table->segments[sid],
									docid,
									index_type,
									data_type,
									field_name,
									index_field_name,
									mem_pool_local);
	

		//如果还在当前段，并且查看当前的docid是否和当前段的docid在10个以内，则将表锁住
		if(sid ==  Mile_AtomicGetPtr(&table->table_meta->segment_current) &&
			segment_get_rowcount(table->segments[sid])-docid < 10)
		{
			//锁表
			table_lock(table);

			log_info("已锁表，当前段:%u 当前追加索引的docid:%u 总的docid数:%u",sid,
																			   docid,
																			   segment_get_rowcount(table->segments[sid]));

			//将剩余的10个数据全部插入
			while(++docid < segment_get_rowcount(table->segments[sid]))
			{
				mem_pool_reset(mem_pool_local);
				
				query_data_and_insert_index(table->segments[sid],
											docid,
											index_type,
											data_type,
											field_name,
											index_field_name,
											mem_pool_local);
			}

			//这里涉及到修改index_meta_hash，和读得check_index发生资源竞争
			pthread_rwlock_wrlock(&table->read_protect_locker);
			log_info("更新表的索引信息，当前段索引处于可插入状态.....");
			index_hash_meta = (struct index_field_meta*)string_map_get(table->index_meta_hash,index_field_name);
			if(index_hash_meta == NULL)
			{
				string_map_put(table->index_meta_hash,index_field_name,(void*)index_meta ,1);
			}
			else
				memmove(index_hash_meta,index_meta,sizeof(struct index_field_meta));

			//如果已压缩，则需要再触发压缩
			if(Mile_AtomicGetPtr(&table->segments[sid]->meta_data->flag) & SEGMENT_COMPRESS)
			{
				Mile_AtomicAndPtr(&table->segments[sid]->meta_data->flag,~SEGMENT_COMPRESS);
				Mile_AtomicOrPtr(&table->segments[sid]->meta_data->flag,SEGMENT_FULL);
				log_info("该段%u 处于已压缩状态 重置为待压缩",sid);
			}
			
			pthread_rwlock_unlock(&table->read_protect_locker);
			
			table_unlock(table);
		}

	}

	
	
	FIN:
	
	meta_data = table->segment_meta;
	
	log_info("开始对其余的段进行索引建立...");

	//对其余的段进行插入
	for(i=0; i<table->max_segment_num; i++,meta_data++)
	{
		if(!(Mile_AtomicGetPtr(&meta_data->flag) & SEGMENT_FULL))
			continue;

		if(i == sid)
			continue;

		log_info("开始对段%u %s列 建立%u索引",i,index_field_name,index_type);

		segment_ensure_index(table->segments[i],index_field_name,index_type,table->mem_pool);

		for(docid = 0; docid < table->row_limit; docid++)
		{

			mem_pool_reset(mem_pool_local);
			query_data_and_insert_index(table->segments[i],
										docid,
										index_type,
										data_type,
										field_name,
										index_field_name,
										mem_pool_local);
		}

		//如果已压缩，则需要再触发压缩
		if(Mile_AtomicGetPtr(&meta_data->flag) & SEGMENT_COMPRESS)
		{
			Mile_AtomicAndPtr(&meta_data->flag,~SEGMENT_COMPRESS);
			Mile_AtomicOrPtr(&meta_data->flag,SEGMENT_FULL);
			log_info("该段%u 处于已压缩状态 重置为待压缩",i);
		}
	}

	// flush index to disk.
	table_checkpoint(table);

	//索引建立完毕，可以接受查询
	log_info("其余段索引建立完毕...");

	
	//这里涉及到修改index_meta_hash，和读的check_index发生资源竞争
	pthread_rwlock_wrlock(&table->read_protect_locker);
	
	for(j=0; j< index_meta->index_count; j++)
	{
		if(index_meta->indexs[j].index_type == index_type)
		{
			index_meta->indexs[j].flag = 1;
			log_info("更新表的索引状态，%s 索引%u 处于可查询状态",index_meta->field_name,index_meta->indexs[j].index_type);
		}
	}
	
	string_map_remove(table->index_meta_hash,index_field_name);

	string_map_put(table->index_meta_hash,index_field_name,(void*)index_meta ,1);

	pthread_rwlock_unlock(&table->read_protect_locker);
	
	mem_pool_destroy(mem_pool_local);

	return MILE_RETURN_SUCCESS;
}

static int make_segment_dir(const struct str_array_t *storage_dirs, const char *work_space, const char *segment_name, uint16_t sid)
{
	char work_dir[PATH_MAX];
	sprintf(work_dir, "%s/%s", work_space, segment_name);
	
	struct stat st;
	if (stat(work_dir, &st) == 0 && S_ISDIR(st.st_mode)) // segment dir exist
		return 0;

	// create real dir
	char real_dir[PATH_MAX];
	sprintf(real_dir, "%s/%s", storage_dirs->strs[sid % storage_dirs->n], segment_name);
	if (mkdirs(real_dir) != 0) {
		return -1;
	}

	if (strcmp(work_dir, real_dir) == 0)
		return 0;

	// symbolic link real_dir to work_dir
	if (real_dir[0] != '/') { // relative path
		char absolute_path[PATH_MAX];
		if (realpath(real_dir, absolute_path) == NULL) {
			log_error("get absolute path failed, path real_dir %s, errno %d", real_dir, errno);
			return -1;
		}
		strcpy(real_dir, absolute_path);
	}
	if (symlink(real_dir, work_dir) != 0) {
		log_error("symlink %s to %s failed, errno %d", real_dir, work_dir, errno);
		return -1;
	}

	return 0;
}


static struct segment_manager* init_segment_manager_instance(struct table_manager* table,struct segment_meta_data* meta_data, uint16_t sid)
{
	//初始化段信息
	struct segment_manager* segment = NULL;
	struct segment_config seg_config;

	memset(&seg_config,0,sizeof(struct segment_config));
	sprintf(seg_config.segment_name,"%s_segment_%06u",table->table_meta->table_name,sid);
	strcpy(seg_config.work_space,table->work_space);
	seg_config.hash_compress_num = table->hash_compress_num;
	seg_config.index_field_count = table->table_meta->index_field_count;
	seg_config.row_limit = table->row_limit;
	seg_config.meta_data = meta_data;
	seg_config.sid = sid;
	seg_config.index_fields = table->table_meta->index_meta;

	if (make_segment_dir(table->storage_dirs, seg_config.work_space, seg_config.segment_name, sid) != 0) {
		log_error("make segment dir failed, segment name %s", seg_config.segment_name);
		return NULL;
	}

	if((segment = segment_init(&seg_config,table->mem_pool)) == NULL)
    {
    	log_error("段初始化出错 段号:%u 段名:%s",sid,seg_config.segment_name);
		memset(meta_data,0,sizeof(struct segment_meta_data));
    	return NULL;
	}
	
	return segment;
}


int32_t table_recover(struct table_manager* table, uint16_t sid, uint32_t docid)
{	
	struct segment_meta_data* meta_data;
	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	meta_data = table->segment_meta + sid;

	if(!(Mile_AtomicGetPtr(&meta_data->flag) & SEGMENT_INIT))
	{
		return ERROR_SEGMENT_NOT_INIT;	

	}

	return segment_recover(table->segments[sid], docid);
}

uint64_t table_get_segment_ctime(struct table_manager* table,uint16_t sid)
{
	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	/*通过段的标志来判断*/
	//获取meta data
	if(!(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag) & SEGMENT_INIT))
		return 0;
    else
		return Mile_AtomicGetPtr(&(table->segment_meta+sid)->create_time);
}


uint64_t table_get_segment_mtime(struct table_manager* table,uint16_t sid)
{
	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	/*通过段的标志来判断*/
	//获取meta data
	if(!(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag)&SEGMENT_INIT))
		return 0;
    else
		return Mile_AtomicGetPtr(&(table->segment_meta+sid)->modify_time);
}



int32_t table_prepare_insert(struct table_manager* table,uint16_t* sid,uint32_t* docid,uint8_t flag,MEM_POOL* mem_pool)
{
	struct segment_meta_data* meta_data;
	int32_t ret;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	//是由上层提供sid
	if(flag == DOCID_BY_BINLOG)
	{
		struct segment_manager* segment;
		
		meta_data = table->segment_meta + *sid;
		segment = table->segments[*sid];
		
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
		{
			table->segments[*sid] = init_segment_manager_instance(table,meta_data,*sid);
			if(table->segments[*sid] == NULL)
			{
				return ERROR_SEGMENT_INIT_FAILED;
			}

			if(meta_data->create_time== 0L)
		    {
		   	   meta_data->create_time = time(NULL);
			   Mile_AtomicOrPtr(&meta_data->flag,SEGMENT_INIT);
		    }
			table->new_seg_complete = 1;
		}

		//调整当前段
		Mile_AtomicSetPtr(&table->table_meta->segment_current,*sid);

		return MILE_RETURN_SUCCESS;
	}
	
	//获取段的元数据信息
	meta_data = table->segment_meta + Mile_AtomicGetPtr(&table->table_meta->segment_current);

	//第一次启动时，可能发生段号为0，一个段都没有初始化
	if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
		ret = ERROR_SEGMENT_NOT_INIT;
	else
		ret = segment_exceed_limit(table->segments[Mile_AtomicGetPtr(&table->table_meta->segment_current)]);

	if(ret == ERROR_EXCEED_LIMIT || ret == ERROR_SEGMENT_NOT_INIT )
	{		
		//需要创建一个segment
		log_info("需要创建一个段 当前段:%u",Mile_AtomicGetPtr(&table->table_meta->segment_current));
		PROFILER_BEGIN("add one segment");
		if((ret=table_add_segment(table,ret,mem_pool))<0)
		{
			PROFILER_END();
			// set new segment add complete flag
			table->new_seg_complete = 1;
			return ret;
		}
		PROFILER_END();
		log_info("段创建完毕 当前段:%u",Mile_AtomicGetPtr(&table->table_meta->segment_current));
		// set new segment add complete flag
		table->new_seg_complete = 1;
	}

	*sid = Mile_AtomicGetPtr(&table->table_meta->segment_current);
	*docid = segment_get_rowcount(table->segments[Mile_AtomicGetPtr(&table->table_meta->segment_current)]);

	return ret;
}



static int32_t table_pk_check(struct table_manager* table,uint32_t row_id,struct row_data* rdata,MEM_POOL* mem_pool)
{
	/*
	uint16_t i;
	struct schema_info* schema = table->schema;
	struct hint_array* time_cond = get_time_hint(mem_pool);
	struct list_head* rowids_list_h;
	struct segment_query_rowids* node;

	for(i=0;i<rdata->field_count;i++)
	{
		//判断该列是否唯一性
		if(schema->fld_info[rdata->field_datas[i].fid]->unique)
		{
			rowids_list_h = table_query_by_value(table,rdata->field_datas[i].fid,time_cond,rdata->field_datas[i].data,mem_pool);

			//判断是否为空
			list_for_each_entry(node,rowids_list_h,rowids_list){
				if(node->rowids != NULL && node->rowids->rowid_num != 0)
					return PK_FAIL;
			}

		}
	}
	*/

	return PK_OK;
}





int32_t table_insert(struct table_manager* table,uint16_t sid,uint32_t docid,struct row_data* rdata,MEM_POOL* mem_pool)
{
	int32_t ret = MILE_RETURN_SUCCESS;
	uint16_t i;
	uint8_t j;
	j = 0;
	
	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
		
	}

	if(!(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag)&SEGMENT_INIT))
    {
     	return ERROR_SEGMENT_NOT_INIT;
	}

	//判断有没有PK列
	if(table_pk_check(table,docid,rdata,mem_pool))
		return ERROR_PK_CONFLICT;

	//先插入原始数据
	if( table->store_raw && (ret = segment_data_insert(table->segments[sid],rdata,docid,mem_pool)) < 0)
	{
		return ret;
	}

	//如果不存原始值，则需要将modify time改掉
	if( !table->store_raw ){
		Mile_AtomicSetPtr(&table->segments[sid]->meta_data->modify_time,time(NULL));
	}

	//插入索引列
	struct low_data_struct* data = rdata->datas;
	struct index_field_meta* index_meta = NULL;
	struct low_data_struct null_data;
	null_data.len = 0;

	PROFILER_BEGIN("index insert");
	for(i=0; i<rdata->field_count; i++,data++)
	{
		//取出索引信息
		index_meta = (struct index_field_meta *)string_map_get(table->index_meta_hash, data->field_name);

		if(index_meta == NULL)
		{
			log_debug("%s 没有索引列",data->field_name);
			continue;
		}

		for(j=0; j<index_meta->index_count; j++)
		{
			//校验数据类型是否正确
			if(data->type != index_meta->indexs[j].data_type)
			{
				if(data->type != HI_TYPE_NULL)
					log_error("%s %u 索引类型不匹配 data_type:%u  插入数据 data_type:%u",index_meta->field_name,
	  																					 index_meta->indexs[j].index_type,
	  																					 index_meta->indexs[j].data_type,
	  																					 data->type);
				null_data.field_name = data->field_name;
				ret = segment_index_insert(table->segments[sid],&null_data,index_meta->indexs[j].index_type,docid,mem_pool);
			}
			else
			{
				ret = segment_index_insert(table->segments[sid],data,index_meta->indexs[j].index_type,docid,mem_pool);
			}
			
			if(ret < 0)
			{
				log_warn("%s列 索引%d 插入失败 ret:%d",data->field_name,index_meta->indexs[j].index_type,ret);

				//即使插入失败了，也要将docid自增
				segment_set_rowcount(table->segments[sid], docid);
				PROFILER_END();
				return ret;
			}
		}
	}
	PROFILER_END();

	//所有数据列和索引列全部插入成功后，做标记
	segment_set_docid_inserted(table->segments[sid],docid);

	//自增
	segment_set_rowcount(table->segments[sid], docid);

	if( Mile_AtomicGetPtr(&table->segments[sid]->meta_data->row_count) == table->row_limit ) { // segment full
		segment_set_flag(table->segments[sid],SEGMENT_FULL);

		// set segment add complete flag
		table->new_seg_complete = 0;

		if( create_mmap_switch_thread(table->table_meta->table_name, sid) != 0) {
			log_error( "create segment full thread failed" );
			return ERROR_MMAP_SWITCH;
		}
	}

	return ret;
}


int32_t table_update(struct table_manager* table,
				   uint16_t sid,
				   uint32_t docid, 
				   struct low_data_struct* new_data,
				   struct low_data_struct** old_data,
				   MEM_POOL* mem_pool)
{
	int32_t ret;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	/*通过段的标志来判断*/
    //获取meta data
	if(sid >= table->max_segment_num || !(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag)&SEGMENT_INIT))
         return ERROR_SEGMENT_NOT_INIT;

	//查看是否查过当前段的rowcount
	if(docid >= segment_get_rowcount(table->segments[sid]))
         return ERROR_EXCEED_CURRENT;
	
	//检查是否支持hash
	if(check_index(table,new_data->field_name,HI_KEY_ALG_HASH,NULL))
		return ERROR_ONLY_FILTER_SUPPORT;

	ret = segment_data_update(table->segments[sid],docid,new_data,old_data,mem_pool);
	
	if(ret < 0)
		return ret;

	if(check_index(table,new_data->field_name,HI_KEY_ALG_FILTER,NULL))
	{
		ret = segment_index_update(table->segments[sid],docid,new_data,old_data,mem_pool);
	}

	return ret;
}



//查询，上层传入的参数是所有要查询的列
//首先对每个列进行遍历，一旦发现没有存储原始值，则直接解析merge列
struct row_data* table_data_query_row(struct table_manager* table,uint16_t sid,uint32_t docid,MEM_POOL* mem_pool)
{
	struct row_data* ret = NULL;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL || !table->store_raw)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	/*通过段的标志来判断*/
    //获取meta data
	if(sid >= table->max_segment_num || !(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag)&SEGMENT_INIT))
         return NULL;

	ret = segment_data_query_row(table->segments[sid],docid,mem_pool);
	
	return ret;
}


struct low_data_struct* table_data_query_col(struct table_manager* table,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* ret = NULL;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	/*通过段的标志来判断*/
	//获取meta data
	if(sid >= table->max_segment_num || !(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag)&SEGMENT_INIT))
		 return NULL;

	//如果本身建了filter索引的话，可以不去磁盘查
	enum field_types data_type;
	if(check_index(table,field_name,HI_KEY_ALG_FILTER,&data_type) == 1 && data_type != HI_TYPE_STRING)
	{
		ret = segment_index_value_query(table->segments[sid],field_name,docid,mem_pool);
		
		//如果不是空值的话，则说明已经查到了
		if(ret->len != 0)
		{
			ret->type = data_type;
			ret->field_name = field_name;
			return ret;
		}
	}

	if(!table->store_raw)
		return ret;
	
	ret = segment_data_query_col(table->segments[sid],field_name,docid,mem_pool);

	return ret;
}



int32_t check_index(struct table_manager* table,char* field_name,enum index_key_alg index_type,enum field_types* data_types)
{
	//检验该列是否支持filter索引
	PROFILER_BEGIN("check_index");
	struct index_field_meta* index_meta =(struct index_field_meta*)string_map_get(table->index_meta_hash, field_name);
	if(index_meta == NULL)
	{
		log_debug("%s 列，没有建 %d 索引",field_name,index_type);
		PROFILER_END();
		return 0;
	}

	uint8_t i;
	for(i=0; i<index_meta->index_count; i++)
	{
		if(index_meta->indexs[i].index_type == index_type && index_meta->indexs[i].flag == 1)
		{
			if(data_types != NULL)
				*data_types = index_meta->indexs[i].data_type;

			PROFILER_END();
			return 1;
		}
	}

	log_debug("%s 列，没有建 %d 索引",field_name,index_type);
	PROFILER_END();
	return 0;
}

struct low_data_struct* table_index_value_query(struct table_manager* table,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* ret = NULL;
	
	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	/*通过段的标志来判断*/
	//获取meta data
	if(sid >= table->max_segment_num || !(Mile_AtomicGetPtr(&(table->segment_meta+sid)->flag)&SEGMENT_INIT))
		 return NULL;

	enum field_types data_type;
	if(!check_index(table,field_name,HI_KEY_ALG_FILTER,&data_type))
		return NULL;

	ret = segment_index_value_query(table->segments[sid],field_name,docid,mem_pool);

	//补全元数据信息
	if(ret != NULL)
	{
		//这里需要做一个转换，如果是字符串的话，则转换为LONGLONG
		if(data_type == HI_TYPE_STRING)
			ret->type = HI_TYPE_LONGLONG;
		else
			ret->type = data_type;
		
		ret->field_name = field_name;
	}
	
	return ret;
}








struct list_head* table_seghint_query(struct table_manager* table, struct hint_array* time_cond, MEM_POOL* mem_pool)
{
	uint16_t i;
	struct list_head* rowids_list_h;
	struct segment_query_rowids* node;
	struct segment_meta_data* meta_data;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	//初始化头
	rowids_list_h = (struct list_head*)mem_pool_malloc(mem_pool,sizeof(struct list_head));
	memset(rowids_list_h,0,sizeof(struct list_head));
	
	INIT_LIST_HEAD(rowids_list_h);

	//获取元数据信息
	meta_data = table->segment_meta;
	for(i=0;i<table->max_segment_num;i++,meta_data++)
	{
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;


		if(!time_cond || !(Mile_AtomicGetPtr(&table->segments[i]->meta_data->modify_time) <= time_cond->hints[0] || 
			Mile_AtomicGetPtr(&table->segments[i]->meta_data->create_time) >= time_cond->hints[1]))
		{
			//查到结果则添加到列表中
			node = (struct segment_query_rowids*)mem_pool_malloc(mem_pool,sizeof(struct segment_query_rowids));
			node->rowids = NULL;
			node->sid = i;
			node->max_docid = meta_data->row_count;
			INIT_LIST_HEAD(&node->rowids_list);
			list_add(&node->rowids_list,rowids_list_h);
			continue;
		}
		
	}

	return rowids_list_h;
}



uint32_t table_index_count_query(struct table_manager* table, 
							     struct list_head* seg_list,
								 struct low_data_struct* data,
								 MEM_POOL * mem_pool)
{
	uint32_t ret = 0;
	struct segment_query_rowids *p;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	if(!check_index(table,data->field_name,HI_KEY_ALG_FULLTEXT,NULL))
		return 0;

	list_for_each_entry(p, seg_list, rowids_list)
	{

		//对段执行相应的步骤
		ret += segment_index_count_query(table->segments[p->sid],data,mem_pool);
	
	}


	return ret;
}

static struct list_head* index_equal_query(struct table_manager* table,
										   struct list_head* seg_list,
										   struct low_data_struct* data,
										   enum index_key_alg index_type,
										   MEM_POOL* mem_pool)
{
	struct segment_query_rowids *p;
	struct list_head* rowids_list_h;
	struct segment_query_rowids* node;
	struct rowid_list* rowids;
	struct segment_meta_data* meta_data;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	if(!check_index(table,data->field_name,index_type,NULL))
		return NULL;

	//初始化头
	rowids_list_h = (struct list_head*)mem_pool_malloc(mem_pool,sizeof(struct list_head));
	memset(rowids_list_h,0,sizeof(struct list_head));
	
	INIT_LIST_HEAD(rowids_list_h);



	list_for_each_entry(p, seg_list, rowids_list)
	{

		//对段执行相应的步骤
		rowids = segment_index_equal_query(table->segments[p->sid],data,index_type,mem_pool);
	
		//查到结果则添加到列表中
		node = (struct segment_query_rowids*)mem_pool_malloc(mem_pool,sizeof(struct segment_query_rowids));
		node->rowids = rowids;
		node->sid = p->sid;
		node->max_docid = p->max_docid;
		INIT_LIST_HEAD(&node->rowids_list);
		list_add(&node->rowids_list,rowids_list_h);
	}


	return rowids_list_h;
}



struct list_head* table_fulltext_index_equal_query(struct table_manager* table,
											   	   struct list_head* seg_list,
										           struct low_data_struct* data,
										           MEM_POOL* mem_pool)
{
	return index_equal_query(table,seg_list,data,HI_KEY_ALG_FULLTEXT,mem_pool);
}



//对所有状态为SEGMENT_INIT的段进行查询，并对段的创建时间和修改时间进行过滤
struct list_head* table_index_equal_query(struct table_manager* table,
										  struct list_head* seg_list,
										  struct low_data_struct* data,
										  MEM_POOL* mem_pool)
{
	return index_equal_query(table,seg_list,data,HI_KEY_ALG_HASH,mem_pool);
}





/*-----------------------------------------------------------------------------
 *  对btree索引, 向指定的表里的列号，进行范围查询，所有段扫
 *-----------------------------------------------------------------------------*/
struct list_head* table_index_range_query(struct table_manager* table, char* field_name, \
		struct hint_array* time_cond, struct db_range_query_condition * range_condition, MEM_POOL* mem_pool)
{
	uint16_t i;
	struct list_head * rowids_list_h;
	struct rowid_list * rowids;
	struct segment_query_rowids * seg_node;
	struct segment_meta_data* meta_data;


	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	if(!check_index(table,field_name,HI_KEY_ALG_BTREE,NULL))
		return NULL;
	

	//初始化头
	rowids_list_h = (struct list_head*)mem_pool_malloc(mem_pool, sizeof(struct list_head));
	memset(rowids_list_h, 0, sizeof(struct list_head));

	INIT_LIST_HEAD(rowids_list_h);

	//获取元数据信息
	meta_data = table->segment_meta;

	for ( i = 0; i < table->max_segment_num; i += 1,meta_data++)
	{
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;
		
		if(!(Mile_AtomicGetPtr(&table->segments[i]->meta_data->modify_time) <= time_cond->hints[0] || 
			Mile_AtomicGetPtr(&table->segments[i]->meta_data->create_time) >= time_cond->hints[1]))

		{
			rowids = segment_index_range_query(table->segments[i], field_name, range_condition, mem_pool);

			//查到结果则添加到列表中
			seg_node = (struct segment_query_rowids*)mem_pool_malloc(mem_pool, sizeof(struct segment_query_rowids));
			seg_node->rowids = rowids;
			seg_node->sid = i;
			INIT_LIST_HEAD(&seg_node->rowids_list);
			list_add(&seg_node->rowids_list, rowids_list_h);
		}
	}

	return rowids_list_h;

}





int64_t table_get_record_num(struct table_manager* table, struct list_head* seg_list)
{
	struct segment_meta_data* meta_data;
	struct segment_query_rowids *p;
	int64_t record_num;


	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	//获取元数据信息
	meta_data = table->segment_meta;
	record_num = 0;

	list_for_each_entry(p, seg_list, rowids_list)
	{
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data[p->sid].flag)&SEGMENT_INIT))
			continue;
		record_num += Mile_AtomicGetPtr(&meta_data[p->sid].row_count)-Mile_AtomicGetPtr(&meta_data[p->sid].del_count);
	}


	return record_num;
}





int64_t table_get_delete_num(struct table_manager* table, struct list_head* seg_list)
{
	struct segment_meta_data* meta_data;
	struct segment_query_rowids *p;
	int64_t delete_num;


	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	//获取元数据信息
	meta_data = table->segment_meta;
	delete_num = 0;

	list_for_each_entry(p, seg_list, rowids_list)
	{
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data[p->sid].flag)&SEGMENT_INIT))
			continue;
		delete_num += Mile_AtomicGetPtr(&meta_data[p->sid].del_count);
	}


	return delete_num;
}





int32_t table_del_docid(struct table_manager* table,uint16_t sid,uint32_t docid)
{	int32_t ret;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	/*通过段的标志来判断*/
	//获取meta data
	if(!(Mile_AtomicGetPtr(&(table->segment_meta + sid)->flag)&SEGMENT_INIT))
		return ERROR_SEGMENT_NOT_INIT;

	ret = segment_del_docid(table->segments[sid],docid);
	return ret;
}


int32_t table_is_docid_deleted(struct table_manager* table,uint16_t sid,uint32_t docid)
{
	int32_t ret;
	
	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	/*判断段号有没超出范围*/
	if(!(Mile_AtomicGetPtr(&(table->segment_meta + sid)->flag)&SEGMENT_INIT))
		return ERROR_SEGMENT_NOT_INIT;

	ret = segment_is_docid_deleted(table->segments[sid],docid);
	return ret;
}



int32_t table_set_segment_current(struct table_manager* table, uint16_t sid)
{
	if(table_lock(table) != MILE_RETURN_SUCCESS)
	{
		return ERROR_LOCK_FAILED;
	}
	
	table->table_meta->segment_current = sid;
	table_unlock(table);
	return MILE_RETURN_SUCCESS;
}

int32_t table_lock(struct table_manager* table)
{
	if(pthread_mutex_lock(&table->write_protect_locker) != 0)
	{
		log_error("加写锁失败");
		return ERROR_LOCK_FAILED;
	}

	return MILE_RETURN_SUCCESS;
}

 
void table_unlock(struct table_manager* table)
{
	pthread_mutex_unlock(&table->write_protect_locker);
}


void table_read_lock(struct table_manager* table)
{
	pthread_rwlock_rdlock(&table->read_protect_locker);
}


void table_read_unlock(struct table_manager* table)
{
	pthread_rwlock_unlock(&table->read_protect_locker);
}


int32_t table_del(struct table_manager* table)
{
	Mile_AtomicOrPtr(&table->table_meta->stat,TABLE_DEL);
	return 0;
}


void print_query_rowids(struct list_head* rowids_list_h)
{
	struct segment_query_rowids* node;

	/*遍历*/
	list_for_each_entry(node,rowids_list_h,rowids_list){
			fprintf(stdout,"sid:%u rowid:",node->sid);
			print_rowid_list(node->rowids);
	}	
}


void table_release(struct table_manager* table)
{
	uint16_t i;
	struct segment_meta_data* meta_data;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return;
	}

	//释放各个段
	//获取元数据信息
	meta_data = table->segment_meta;
	for(i=0;i<table->max_segment_num;i++,meta_data++)
	{	
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;
		
		segment_release(table->segments[i]);
	}

	msync(table->segment_meta,SEGMENT_RUNTIME_SIZE(table),MS_SYNC);        // make sure synced
	munmap(table->segment_meta, SEGMENT_RUNTIME_SIZE(table));

	pthread_mutex_destroy(&table->write_protect_locker);
	pthread_rwlock_destroy(&table->read_protect_locker);
	
	return;
}



int32_t table_compress(struct table_manager* table,MEM_POOL* mem_pool)
{
	int32_t ret;
	uint16_t i;
	struct segment_meta_data* meta_data;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}
	
	ret = MILE_RETURN_SUCCESS;
	//释放各个段
	//获取元数据信息
	meta_data = table->segment_meta;

	for(i=0; i<table->max_segment_num; i++,meta_data++)
	{
		//查看该段是否已被压缩
		if(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_COMPRESS)
		{
			continue;
		}
				
		//查看该段是否处于待压缩状态
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_FULL))
		{
			continue;
		}
		
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
		{
			continue;
		}
		
		log_info("segment %u 开始压缩 当前段 :%u",i,Mile_AtomicGetPtr(&table->table_meta->segment_current));

		//压缩的时候加读锁
		pthread_rwlock_rdlock(&table->read_protect_locker);
		ret = segment_compress(table->segments[i],mem_pool);
		pthread_rwlock_unlock(&table->read_protect_locker);
		
	
		if(ret != MILE_RETURN_SUCCESS)
		{
			log_error("segment %u 压缩失败 ret:%d",i,ret);
			break;
		}

		log_info("segment %u 压缩完成 ret:%d",i,ret);

		//切换的时候加写锁
		pthread_rwlock_wrlock(&table->read_protect_locker);
		
		ret = segment_compress_switch(table->segments[i]);
		//将该段记为已压缩
		Mile_AtomicOrPtr(&meta_data->flag,SEGMENT_COMPRESS);
		
		pthread_rwlock_unlock(&table->read_protect_locker);
	}
	
	return ret;
}



void table_checkpoint(struct table_manager* table)
{
	uint16_t i;
	struct segment_meta_data* meta_data;

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return;
	}
	
	//释放各个段
	//获取元数据信息
	meta_data = table->segment_meta;
	
	//在checkpoint的时候，需要加读锁
	pthread_rwlock_rdlock(&table->read_protect_locker);
	for(i=0;i<table->max_segment_num;i++,meta_data++)
	{	
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;


		segment_checkpoint(table->segments[i]);
	}
	pthread_rwlock_unlock(&table->read_protect_locker);

	msync(table->segment_meta,SEGMENT_RUNTIME_SIZE(table),MS_SYNC);        // make sure synced
	return;
}




int32_t table_load_segment(struct table_manager* table,int16_t sid,char* segment_dir,MEM_POOL* mem_pool)
{
	struct segment_meta_data* old_meta_data;
	struct segment_meta_data* new_meta_data;
	void* mem_mmaped = NULL;
	int32_t ret = MILE_RETURN_SUCCESS;
	MEM_POOL* mem_pool_local = (MEM_POOL*)mem_pool_init(MB_SIZE);

	do
	{

		log_info("开始进行加载段 ...");

		/*--------------获取段的元数据信息-----------------*/
		//获取老的段得元数据信息，并判断
		old_meta_data = table->segment_meta + sid;
		if((Mile_AtomicGetPtr(&(old_meta_data)->flag)&SEGMENT_INIT))
		{
			log_error("此坑已被占:%u",sid);
			ret = ERROR_SEGMENT_IS_TAKEN;
			break;
		}

		log_info("获取到加载的目的段号 %u",sid);

		//获取新的段的元数据信息
		char meta_file_name[FILENAME_MAX_LENGTH];
		memset(meta_file_name,0,sizeof(meta_file_name));
		sprintf(meta_file_name,"%s/meta.dat",segment_dir);

		//mmap映射处理
		new_meta_data =(struct segment_meta_data*)get_mmap_memory(meta_file_name,sizeof(struct segment_meta_data)); 
		if(new_meta_data == NULL){
			log_error("map file failed: %s", strerror(errno));
			ret = ERROR_FILE_OP_FAILED;
			break;
		}

		log_info("加载段的元数据信息 ctime:%lu mtime:%lu ptime:%lu flag:%u rowcount:%u delcount:%u",new_meta_data->create_time,
    																								new_meta_data->modify_time,
    																								new_meta_data->checkpoint_time,
    																								new_meta_data->flag,
    																								new_meta_data->row_count,
    																								new_meta_data->del_count);
		
		/*-----------将用户指定的段目录，拷贝到当前table工作目录，并重命名--------------------*/

		/*-----------------初始化段-----------------*/
		struct segment_config seg_config;
		memset(&seg_config,0,sizeof(struct segment_config));
		
		/*---------------------对索引进行补全--------------------------*/
		
		char index_meta_filename[FILENAME_MAX_LENGTH];
		memset(index_meta_filename,0,sizeof(index_meta_filename));
		sprintf(index_meta_filename,"%s/index.dat",segment_dir);

		mem_mmaped = get_mmap_memory(index_meta_filename,sizeof(struct index_field_meta)*MAX_INDEX_FIELD_NUM+ sizeof(uint16_t)); 
		if(mem_mmaped == NULL)
		{
			log_error("map file failed: %s", strerror(errno));
			ret = ERROR_FILE_OP_FAILED;
			break;
		}
		char segment_work_space[FILENAME_MAX_LENGTH];
		sprintf(seg_config.segment_name,"%s_segment_%06u",table->table_meta->table_name,sid);
		sprintf(segment_work_space,"%s/%s",table->work_space,seg_config.segment_name);
		seg_config.meta_data = old_meta_data;
		seg_config.row_limit = table->row_limit;
		seg_config.hash_compress_num = table->hash_compress_num;
		seg_config.index_field_count = *(uint16_t*)mem_mmaped;  /*初始化时，不建索引*/
		seg_config.index_fields = (struct index_field_meta*)((char *)mem_mmaped + sizeof(uint16_t));
		strcpy(seg_config.work_space,table->work_space);

		
		memset(segment_work_space,0,sizeof(segment_work_space));
		sprintf(segment_work_space,"%s/%s",table->work_space,seg_config.segment_name);

		log_info("重命名段目录 old:%s new:%s",segment_dir,segment_work_space);
		if(rename(segment_dir,segment_work_space) < 0)
		{
			log_error("rename file [%s] failed",strerror(errno));
			ret = -1;
			break;
		}

		
		if((table->segments[sid] = segment_init(&seg_config,mem_pool)) == NULL)
		{
			ret = ERROR_SEGMENT_INIT_FAILED;
			break;
		}
		log_info("初始化段的数据结构");

		uint16_t i;
		uint8_t j;

		
		struct index_field_meta* index_meta = table->table_meta->index_meta;
		struct index_field_manager* index_field = NULL;
		struct low_data_struct* data = NULL;
		uint32_t docid;

		log_info("开始补全索引 ...");
		
		//这个虽然是段的索引元数据进行访问，但是改得话，只会在新建索引的地方修改，上层会控制，load unload和新建索引，压缩三个任务不能
		//同时进行
		for(i=0; i<table->table_meta->index_field_count; i++,index_meta++)
		{
			for(j=0; j<index_meta->index_count; j++)
			{
				mem_pool_reset(mem_pool_local);
				
				index_field = segment_get_index_instance(table->segments[sid],index_meta->field_name,index_meta->indexs[j].index_type);

				log_info("当前表索引信息 field name:%s index type:%u data type:%u flag:%u",index_meta->field_name,
																						index_meta->indexs[j].index_type,
																						index_meta->indexs[j].data_type,
																						index_meta->indexs[j].flag);
				
				//索引缺失
				if(index_field == NULL)
				{
					log_info("索引缺失，补建索引 ...");

					//这个只会对sid的索引管理结构进行修改，而当时这个sid的标识未被置为INIT，所以不存在线程安全问题
					segment_ensure_index(table->segments[sid],index_meta->field_name,index_meta->indexs[j].index_type,table->mem_pool);

					for(docid = 0; docid<table->row_limit; docid++)
					{
						//查询原始数据列
						data = segment_data_query_col(table->segments[sid],index_meta->field_name,docid,mem_pool_local);

						//插入到索引列
						if(data != NULL)
						{
							ret = segment_index_insert(table->segments[sid],data,index_meta->indexs[j].index_type,docid,mem_pool_local);
							if(ret < 0)
							{
								log_error("%s %d索引插入失败 ret %d",index_meta->field_name,index_meta->indexs[j].index_type,ret);
								continue;
							}
						}
					}

				}
			}

		}


		//将段标识设为INIT
		log_info("索引补全完毕，将段%u标识为INIT状态",sid);
		
		//bugfix 再load段的时候，在segment信息还未初始化好，就直接将meta信息拷贝过来，线程安全 by yunliang.shi 2011-12-10
		memcpy(old_meta_data,new_meta_data,sizeof(struct segment_meta_data));
		Mile_AtomicAndPtr(&table->segments[sid]->meta_data->flag,~SEGMENT_DUMP);
		Mile_AtomicOrPtr(&table->segments[sid]->meta_data->flag,SEGMENT_INIT);

	}while(0);

	munmap(mem_mmaped, sizeof(struct index_field_meta)*MAX_INDEX_FIELD_NUM+ sizeof(uint16_t));
	munmap(new_meta_data, sizeof(struct segment_meta_data));
	mem_pool_destroy(mem_pool_local);
	return MILE_RETURN_SUCCESS;
}


static int32_t traversal_dir_func(char* dir,void* arg)
{
	uint16_t i;
	
	struct table_manager* table = (struct table_manager*) arg;
	struct segment_meta_data* meta_data = table->segment_meta;

	for(i=0;i < table->max_segment_num;i++, meta_data++)
	{
		//如果该段未初始化，则找到
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			break;

	}
	if(i == table->max_segment_num)
	{
		log_error("所有的段已经开辟完了");
		return ERROR_EXCEED_SEGMENT_NUM;
	}

	return table_load_segment(table,i,dir,table->mem_pool);
}



int32_t table_replace_all_segments(struct table_manager* table,char* segments_dir,MEM_POOL* mem_pool)
{
	struct segment_meta_data* meta_data;
	uint16_t i;
	int32_t ret;
	
	//先加写锁，读排他
	pthread_rwlock_wrlock(&table->read_protect_locker);

	//卸载所有的段
	meta_data = table->segment_meta;
	for(i=0;i<table->max_segment_num;i++,meta_data++)
	{
		//如果该段未初始化，则不需要再去查询
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
			continue;
	
		ret = table_unload_segment(table,i,0);
		if(ret < 0)
		{
			log_error("卸载段失败 %s  %u",table->table_meta->table_name,i);
			goto RET;
		}

	}

	if(segments_dir != NULL)
		//遍历segments_dir指定目录下所有的段
		ret = traversal_single_deep_childdir(segments_dir,traversal_dir_func,(void*)table);


	RET:
		pthread_rwlock_unlock(&table->read_protect_locker);
		return ret;
}




//遍历表内所有的段，设置标记为IS_SEGMENT_DUMP，把过期的段移动到指定的目录下，并把runtime.dat复制到指定的目录下
int32_t table_unload_segment(struct table_manager* table,int16_t sid,uint8_t thread_safe)
{
	int32_t ret = MILE_RETURN_SUCCESS;
	struct segment_manager* segment;
	struct segment_meta_data* meta_data;

	do
	{
		log_info("开始进行段卸载 ...");
		
		if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
		{
			log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
			return ERROR_TABLE_DELETED;
			break;
		}

		/*-----------首先通过meta查看此段的状态---------------*/
		meta_data = table->segment_meta + sid;
		
		//如果该段未初始化，则失败
		if(!(Mile_AtomicGetPtr(&meta_data->flag)&SEGMENT_INIT))
		{
			log_error("dump的段:%u 未初始化:%d",sid,meta_data->flag);
			ret = ERROR_SEGMENT_NOT_INIT;
		}

		segment = table->segments[sid];

		//不太可能发生
		if(segment == NULL)
		{
			log_error("段未初始化");
			ret = ERROR_SEGMENT_NOT_INIT;
			break;
		}

		/*------将段的元数据信息写入到段的目录里-----------*/
		char meta_file_name[FILENAME_MAX_LENGTH];
		void* meta_memaped = NULL;
		memset(meta_file_name,0,sizeof(meta_file_name));
		sprintf(meta_file_name,"%s/meta.dat",segment->work_space);

		log_info("段的元数据信息拷贝到段内 ctime:%lu mtime:%lu ptime:%lu flag:%u rowcount:%u delcount:%u",   meta_data->create_time,
  																										     meta_data->modify_time,
  																										     meta_data->checkpoint_time,
  																										     meta_data->flag,
  																										     meta_data->row_count,
  																										     meta_data->del_count);

		//mmap映射处理
		meta_memaped = get_mmap_memory(meta_file_name,sizeof(struct segment_meta_data)); 
		if(meta_memaped == NULL){
			log_error("map file failed: %s", strerror(errno));
			ret = ERROR_FILE_OP_FAILED;
			break;
		}

		//设置标记量，重命名段目录，flush到磁盘中
		Mile_AtomicOrPtr(&meta_data->flag,SEGMENT_DUMP);
		log_info("将段标记为 SEGMENT_DUMP flag:%d",meta_data->flag);

		//拷贝数据
		memcpy(meta_memaped,meta_data,sizeof(struct segment_meta_data));

		//flush到磁盘里
		msync(meta_memaped,sizeof(struct segment_meta_data),MS_SYNC);        // make sure synced
	   	munmap(meta_memaped,sizeof(struct segment_meta_data));	


		/*------将段当前的索引拷贝到段内，因为当前肯定是没有进行索引建立的，所以不用拷贝string_map的索引，segment已建立的索引一定是index_meta一致*/
		char index_file_name[FILENAME_MAX_LENGTH];
		memset(index_file_name,0,sizeof(index_file_name));
		sprintf(index_file_name,"%s/index.dat",segment->work_space);

		log_info("将当前索引信息拷贝到段内 ...");

		//索引信息加锁
		meta_memaped = get_mmap_memory(index_file_name,sizeof(struct index_field_meta)*MAX_INDEX_FIELD_NUM+ sizeof(uint16_t)); 
		if(meta_memaped == NULL){
			log_error("map file failed: %s", strerror(errno));
			ret = ERROR_FILE_OP_FAILED;
			break;
		}
		
		*(uint16_t*)meta_memaped = table->table_meta->index_field_count;
		memcpy((char *)meta_memaped + sizeof(uint16_t), table->table_meta->index_meta, sizeof(struct index_field_meta)*MAX_INDEX_FIELD_NUM);

		uint16_t i;
		uint8_t j;
		struct index_field_meta* index_meta = table->table_meta->index_meta;
		for(i=0; i<table->table_meta->index_field_count; i++,index_meta++)
		{
			for(j=0; j<index_meta->index_count; j++)
			{
				log_info("当前表索引信息 field name:%s index type:%u data type:%u flag:%u", index_meta->field_name,
																							index_meta->indexs[j].index_type,
																							index_meta->indexs[j].data_type,
																							index_meta->indexs[j].flag);
			}
		}

		msync(meta_memaped,sizeof(struct index_field_meta)*MAX_INDEX_FIELD_NUM+ sizeof(uint16_t),MS_SYNC);        // make sure synced
	   	munmap(meta_memaped, sizeof(struct index_field_meta)*MAX_INDEX_FIELD_NUM+ sizeof(uint16_t));	


		/*---------------释放该段----------------------*/
		//加写锁
		if(thread_safe)
			pthread_rwlock_wrlock(&table->read_protect_locker);
		log_info("将段元数据信息清空");
			
		//清空段的元数据信息
		memset(segment->meta_data,0,sizeof(struct segment_meta_data));
	    segment_release(segment);
		table->segments[sid] = NULL;
		if(thread_safe)
			pthread_rwlock_unlock(&table->read_protect_locker);

		/*-----------------重命名段的工作目录------------------------*/
		char segment_file_name[FILENAME_MAX_LENGTH];
		memset(segment_file_name,0,sizeof(segment_file_name));

		sprintf(segment_file_name,"%s/%s_dump",table->work_space,segment->segment_name);


		log_info("重命名目录 old:%s new:%s",segment->work_space,segment_file_name);
		
		if(rename(segment->work_space,segment_file_name) < 0)
		{
			log_error("rename file [%s] failed",strerror(errno));
			ret = ERROR_FILE_OP_FAILED;
			break;
		}

	}while(0);

	return ret;
}





struct segment_meta_data* table_query_segment_stat(struct table_manager* table,uint16_t* max_segment_num,MEM_POOL* mem_pool)
{
	struct segment_meta_data* segment_meta = (struct segment_meta_data*)mem_pool_malloc(mem_pool,table->max_segment_num * sizeof(struct segment_meta_data));
	memset(segment_meta,0,table->max_segment_num * sizeof(struct segment_meta_data));

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}

	//获取元数据信息
	memcpy(segment_meta,table->segment_meta,table->max_segment_num * sizeof(struct segment_meta_data));
	
	*max_segment_num = table->max_segment_num;

	return segment_meta;
}



struct index_field_meta* table_query_index_stat(struct table_manager* table,uint16_t* index_field_count,MEM_POOL* mem_pool)
{
	struct index_field_meta* index_meta = (struct index_field_meta*)mem_pool_malloc(mem_pool,table->table_meta->index_field_count * sizeof(struct index_field_meta));
	memset(index_meta,0,table->table_meta->index_field_count * sizeof(struct index_field_meta));

	if(Mile_AtomicGetPtr(&table->table_meta->stat)&TABLE_DEL)
	{
		log_warn("该表已删除，tablename:%s",table->table_meta->table_name);
		return NULL;
	}


	//获取索引元数据信息
	pthread_rwlock_rdlock(&table->read_protect_locker);
	memcpy(index_meta,table->table_meta->index_meta,table->table_meta->index_field_count * sizeof(struct index_field_meta));
	*index_field_count = table->table_meta->index_field_count;
	pthread_rwlock_unlock(&table->read_protect_locker);
	return index_meta;
}

int table_mmap_switch( struct table_manager *table, uint16_t sid)
{
	if( Mile_AtomicGetPtr(&table->table_meta->stat) & TABLE_DEL) {
		log_warn("table deleted, tablename:%s",table->table_meta->table_name);
		return ERROR_TABLE_DELETED;
	}

	int ret;

	table_read_lock( table );
	ret = segment_mmap_switch( table->segments[sid] );
	table_read_unlock( table );

	return ret;
}



