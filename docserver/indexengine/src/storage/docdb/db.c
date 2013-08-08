/*
 * =====================================================================================
 *
 *       Filename:  hi_db.c
 *
 *    Description:  整个DB层的接口实现
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


#include "db.h"


/*db的全局变量*/
static struct db_manager* db = NULL;

static void* db_checkpoint_thread(void* arg);
static int32_t db_recover_data();

static struct binlog_record* db_insert_to_binrecord(char* table_name, 
                                                    uint16_t sid, 
                                                    uint32_t docid,
                                                    struct row_data* rdata,
                                                    MEM_POOL* mem_pool);

static struct binlog_record* db_update_to_binrecord(char* table_name, 
													uint16_t sid, 
													uint32_t docid,
													struct low_data_struct* new_data,
													MEM_POOL* mem_pool);

static struct binlog_record* db_delete_to_binrecord(char* table_name, 
													 uint16_t sid, 
													 uint32_t row_id,
													 MEM_POOL* mem_pool);

static struct binlog_record* db_index_to_binrecord(char* table_name, 
												   char* field_name,
												   enum index_key_alg index_type,
												   enum field_types data_type,
                                                   MEM_POOL* mem_pool);

static struct binlog_record* db_dindex_to_binrecord(char* table_name, 
												   char* field_name,
												   enum index_key_alg index_type,
                                                   MEM_POOL* mem_pool);

static struct binlog_record* db_load_to_binrecord(char* table_name, 
												  uint16_t sid, 
												  char* segment_dir,
												  MEM_POOL* mem_pool);

static struct binlog_record* db_unload_to_binrecord(char* table_name, 
  												  	uint16_t sid, 
  												  	MEM_POOL* mem_pool);


static struct binlog_record* db_compress_to_binrecord(char* table_name, 
												      MEM_POOL* mem_pool);




static struct table_manager* db_get_table_instance(char* table_name);


static int32_t db_checkpoint_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data);

static int32_t db_release_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data);

void* db_checkpoint_thread(void* arg)
{
	while(g_running_flag)
	{
		MILE_SLEEP(db->checkpoint_interval);
		if(!g_running_flag)
			break;

		//执行checkpoint
		db_checkpoint();
	}
	return NULL;
}


void* db_sync_binlog(void* arg)
{
	while(g_running_flag)
	{
		MILE_USLEEP(db->binlog_sync_interval * 1000);
		// do sync at exit
		binlog_sync(db->binlog_writer);
		continue;
	}
	return NULL;
}


int32_t db_init(struct db_conf* conf)
{
	uint8_t i;
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);
	
	//置为线程安全
	mem_pool_set_threadsafe(mem_pool);

	db = (struct db_manager*)mem_pool_malloc(mem_pool,sizeof(struct db_manager));
	memset(db,0,sizeof(struct db_manager));

	//初始化profile日志，默认10ms
	init_profile(conf->profiler_threshold, mem_pool);

	db->mem_pool = mem_pool;
	db->role = conf->role;


	//初始化工作目录
	db->storage_dirs = all_str_append(&conf->storage_dirs, mem_pool, "%s", ""); // copy
	assert(db->storage_dirs->n > 0);
	db->work_space = db->storage_dirs->strs[0];

	if(mkdirs(db->work_space))
	{
		log_error("db工作目录:%s初始化失败",db->work_space);
		return ERROR_DB_INIT_FAILED;
	}

	strcpy( db->binlog_dir, conf->binlog_dir );
	if(conf->binlog_flag && mkdirs(db->binlog_dir)) {
		log_error( "init db binlog dir [%s] failed",db->binlog_dir );
		return ERROR_DB_INIT_FAILED;
	}

	db->binlog_maxsize = conf->binlog_maxsize;
	db->binlog_flag = conf->binlog_flag;

	db->checkpoint_interval = conf->checkpoint_interval;
	db->hash_compress_num = conf->hash_compress_num;
	db->max_segment_num = conf->max_segment_num;
	db->row_limit = conf->row_limit;

	db->table_only_store_index = conf->table_store_only_index;

	/*初始化所有的table的meta信息*/
	char table_meta_filename[FILENAME_MAX_LENGTH];
	memset(table_meta_filename,0,sizeof(table_meta_filename));
	sprintf(table_meta_filename,"%s/table.meta",db->work_space);

	db->table_count = (uint8_t *)get_mmap_memory(table_meta_filename,TABLE_RUNTIME_SIZE);
	assert(db->table_count);

	db->table_metas = (struct table_meta_data*)(db->table_count + sizeof(uint8_t));

	/*初始化所有的表信息*/
	db->tables = init_string_map(db->mem_pool,MAX_TABLE_NUM);
	db->table_metas_hash = init_string_map(db->mem_pool, MAX_TABLE_NUM);
	struct table_meta_data* table_meta = db->table_metas;
	for(i=0;i< *db->table_count; i++,table_meta++)
	{	
		if(!(Mile_AtomicGetPtr(&table_meta->stat) & TABLE_INIT))
			continue;
		string_map_put(db->table_metas_hash,table_meta->table_name,table_meta,1);
		log_error("开始加载:%s表",table_meta->table_name);
		db_read_lock();
		db_create_table(table_meta->table_name);
		log_error("加载完毕:%s表",table_meta->table_name);
		db_read_unlock();
	}
	log_error("加载完毕所有段");
	/*初始化binlog的元数据信息*/
	char bl_file_name[MAX_FIELD_NAME];
	memset(bl_file_name,0,sizeof(bl_file_name));
	sprintf(bl_file_name,"%s/binlog.meta",db->work_space);
	
	db->bl_meta =(BL_META_PTR)get_mmap_memory(bl_file_name,sizeof(struct binlog_meta));
	if(db->bl_meta == NULL)
	{
		log_error("binlog meta信息映射失败");
		return ERROR_DB_INIT_FAILED;
	}

	//记录开始同步的偏移量
	db->offset_start = db->bl_meta->offset;
	
	//调用checkpoint线程
	pthread_create(&db->checkpoint_tid,NULL,db_checkpoint_thread,NULL);

	db->binlog_sync_interval = conf->binlog_sync_interval;

	//初始化binlog
	if(db->binlog_flag)
	{
		db->db_stat = RECOVER_DATA;

		//数据恢复模块，指定checkpoint时间点，传入到binlog模块中，遍历获取记录，进行插入
		if(db_recover_data() != MILE_RETURN_SUCCESS)
		{
			log_error("数据恢复失败");
			return ERROR_DB_INIT_FAILED;
		}

		db->db_stat = RECOVER_FIN;
		
		db->binlog_writer = binlog_writer_init(db->binlog_dir, db->binlog_maxsize, db->mem_pool);

		if(db->binlog_writer == NULL)
		{
			log_error("binlog初始化失败");
			return ERROR_DB_INIT_FAILED;
		}

		if( db->binlog_sync_interval == 0 )
		{
			db->binlog_writer->sync_immediately = 1;
		}
		else if(db->binlog_sync_interval > 0)
		{
			pthread_create(&db->sync_tid,NULL,db_sync_binlog,NULL);
		}
	}

	//初始化读写锁锁
	if(pthread_rwlock_init(&db->locker,NULL) != 0)
	{
		log_error("读写锁初始化失败");
		return ERROR_DB_INIT_FAILED;
	}


	//初始化互斥锁
	if(pthread_mutex_init(&db->task_locker,NULL) != 0)
	{
		log_error("互斥锁初始化失败");
		return ERROR_DB_INIT_FAILED;
	}

	if( MASTER_ROLE == db->role )
		set_db_readable();
	
	return MILE_RETURN_SUCCESS;
}


struct db_manager* get_db()
{
	return db;
}


struct table_manager* db_create_table(char* table_name)
{	
	//互斥锁
	struct table_meta_data* table_meta = NULL;
	struct table_manager* table = NULL;

	db_read_unlock();

	//先查看有没有这个表
	pthread_rwlock_rdlock(&db->locker);
	table = db_get_table_instance(table_name);
	pthread_rwlock_unlock(&db->locker);
	if(table != NULL)
	{
		log_error("重复建表:%s",table_name);
		db_read_lock();
		return table;
	}

	PROFILER_BEGIN("db_create_table");
	pthread_rwlock_wrlock(&db->locker);
	
	table_meta = (struct table_meta_data *)string_map_get(db->table_metas_hash,table_name);
	
	//补上信息 ,新家的表
	if(table_meta == NULL)
	{
		table_meta = db->table_metas + *db->table_count;
		strcpy(table_meta->table_name,table_name);
		(*db->table_count)++;
	}

	struct table_config config;
	memset(&config,0,sizeof(struct table_config));
	config.storage_dirs = db->storage_dirs;
	config.hash_compress_num = db->hash_compress_num;
	config.max_segment_num = db->max_segment_num;
	config.row_limit = db->row_limit;
	config.table_meta = table_meta;

	if(string_map_get(db->table_only_store_index, table_name) != NULL){
		config.store_raw = 0;
	}else{
		config.store_raw = 1;
	}
	
	

	table = table_init(&config,db->mem_pool);
	assert(table);

	string_map_put(db->tables,table_name,(void*)table ,1);
	pthread_rwlock_unlock(&db->locker);

	PROFILER_END();

	db_read_lock();
	return table;
}


int32_t db_del_table(char* table_name)
{
	int32_t ret;
	
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	ret = table_del(table);
	return ret;
}



int32_t db_ensure_index(char* table_name,
						   char* field_name,
						   enum index_key_alg index_type,
						   enum field_types data_type, 
						   MEM_POOL* mem_pool)
{
	int32_t ret;
	//参数检查
	if(index_type < HI_KEY_ALG_HASH || index_type > HI_KEY_ALG_FULLTEXT)
	{
		log_error("索引类型不正确 index_type:%d", index_type);
		return -1;
	}

	if(data_type < HI_TYPE_TINY || data_type > HI_TYPE_STRING){
		log_error("数据类型不正确，data_type:%d", data_type);
		return -1;
	}

	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		table = db_create_table(table_name);
	}

	pthread_mutex_lock(&db->task_locker);
    ret = table_ensure_index(table,field_name,index_type,data_type,db->mem_pool);
	pthread_mutex_unlock(&db->task_locker);

	// write binlog after operation, just for syncing operation to slave
	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		binlog_lock_writer( db->binlog_writer );
		if( binlog_write_record(db->binlog_writer, db_index_to_binrecord(table_name,field_name,index_type,data_type,mem_pool), mem_pool)
				|| ( MILE_RETURN_SUCCESS == ret ?
				  binlog_confirm_ok(db->binlog_writer, mem_pool) :  binlog_confirm_fail(db->binlog_writer, mem_pool) ) != 0 ) {
			log_error( "write binlog record failed" );
			if( MILE_RETURN_SUCCESS == ret ) 
				ret = ERROR_BINLOG_FAILED;
		}
		binlog_unlock_writer( db->binlog_writer );
	}

	return ret;
}


int32_t db_del_index(char* table_name,char* field_name,enum index_key_alg index_type,MEM_POOL* mem_pool)
{
	int32_t ret;
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_NOT_EXIT;
	}

	pthread_mutex_lock(&db->task_locker);
	ret = table_del_index(table,field_name,index_type);
	pthread_mutex_unlock(&db->task_locker);

	// write binlog after operation, just for syncing operation to slave
	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		binlog_lock_writer( db->binlog_writer );
		if( binlog_write_record(db->binlog_writer, db_dindex_to_binrecord(table_name,field_name,index_type,mem_pool), mem_pool)
				|| ( MILE_RETURN_SUCCESS == ret ?
				  binlog_confirm_ok(db->binlog_writer, mem_pool) :  binlog_confirm_fail(db->binlog_writer, mem_pool) ) != 0 ) {
			log_error( "write binlog record failed" );
			if( MILE_RETURN_SUCCESS == ret ) 
				ret = ERROR_BINLOG_FAILED;
		}
		binlog_unlock_writer( db->binlog_writer );
	}

	return ret;
}



//解析binlog日志的数据，转换成insert的参数接口
struct row_data* db_binrecord_to_insert(struct binlog_record* bl_record,char** table_name, MEM_POOL* mem_pool)
{
	struct low_data_struct data;
	uint8_t len;
	data.len = bl_record->len - sizeof(struct binlog_record);

	len = *(uint8_t*)bl_record->data;
	
	*table_name = (char*)mem_pool_malloc(mem_pool,len+1);
	memset(*table_name,0,len+1);

	memcpy(*table_name,bl_record->data+sizeof(uint8_t),len);
	
	data.data = bl_record->data+len+sizeof(uint8_t);
	
	return lowdata_to_rowdata(&data,mem_pool);
}


//解析binlog日志，转换成update的参数接口
struct low_data_struct* db_binrecord_to_update(struct binlog_record* bl_record,char** table_name, char** field_name,MEM_POOL* mem_pool)
{
	
	uint32_t offset = 0;
	uint8_t len;
	enum field_types type;

	//表名
	len = *(uint8_t*)(bl_record->data + offset);
	*table_name = (char*)mem_pool_malloc(mem_pool,len+1);
	memset(*table_name, 0, len+1);
	offset += sizeof(uint8_t);

	memcpy(*table_name,bl_record->data + offset,len);
	offset += len;

	len = *(uint8_t*)(bl_record->data + offset);
	*field_name = (char*)mem_pool_malloc(mem_pool,len+1);
	memset(*field_name,0,len+1);
	offset += sizeof(uint8_t);
	
	memcpy(*field_name,bl_record->data + offset,len);
	offset += len;

	type = (enum field_types)*(uint8_t*)(bl_record->data + offset);
	offset += sizeof(uint8_t);
	
	struct low_data_struct* data = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
	memset(data,0,sizeof(struct low_data_struct));

	data->len = *(uint32_t*)(bl_record->data + offset);
	offset += sizeof(uint32_t);
	data->data = bl_record->data + offset;
	data->field_name = *field_name;
	data->type = type;
	
	return data;
}



enum index_key_alg  db_binrecord_to_index(struct binlog_record* bl_record,
											     char** table_name,
											     char** field_name,
											     enum field_types* data_type,
											     MEM_POOL* mem_pool)
{
	uint32_t offset = 0;
	uint8_t len;

	//表名
	*data_type = (enum field_types)*(uint8_t*)(bl_record->data + offset);
	offset += sizeof(uint8_t);
	len = *(uint8_t*)(bl_record->data + offset);
	*table_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*table_name, 0, len + 1);
	offset += sizeof(uint8_t);

	memcpy(*table_name,bl_record->data + offset,len);
	offset += len;

	len = *(uint8_t*)(bl_record->data + offset);
	*field_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*field_name,0,len + 1);
	offset += sizeof(uint8_t);
	
	memcpy(*field_name,bl_record->data + offset,len);
	offset += len;

	return (enum index_key_alg)*(uint8_t*)(bl_record->data + offset);
}

static struct binlog_record* db_index_to_binrecord(char* table_name, 
												   char* field_name,
												   enum index_key_alg index_type,
												   enum field_types data_type,
                                                   MEM_POOL* mem_pool)
{
    uint32_t data_len = 0; 
	uint8_t fld_len = 0;
	uint8_t tbl_len = 0;
    char* buf = NULL;
 
  	fld_len = strlen(field_name);
	tbl_len = strlen(table_name);
    data_len = 4*sizeof(uint8_t) + fld_len + tbl_len;
    buf = (char*)mem_pool_malloc(mem_pool,data_len);
    memset(buf,0,data_len);
 
   
    uint32_t offset = 0; 

	memcpy(buf + offset,&data_type,sizeof(uint8_t));
	offset += sizeof(uint8_t);

	//表名长度
    memcpy(buf + offset,&tbl_len,sizeof(uint8_t));
    offset += sizeof(uint8_t);

	//表名
	memcpy(buf + offset, table_name, tbl_len);
	offset += tbl_len;

	//列名长度
	*(uint8_t*)(buf + offset) = fld_len;
	offset += sizeof(uint8_t);

	//列名
	memcpy(buf + offset, field_name, fld_len);
	offset += fld_len;

	*(uint8_t*)(buf + offset) = index_type;

	struct binlog_record* bl_record = create_binlog_record(data_len,mem_pool);
    bl_record->op_code = OPERATION_INDEX;
    memcpy(bl_record->data,buf,data_len);

    return bl_record;
}



enum index_key_alg  db_binrecord_to_dindex(struct binlog_record* bl_record,
											     char** table_name,
											     char** field_name,
											     MEM_POOL* mem_pool)
{
	uint32_t offset = 0;
	uint8_t len;

	//表名
	len = *(uint8_t*)(bl_record->data + offset);
	*table_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*table_name, 0, len + 1);
	offset += sizeof(uint8_t);

	memcpy(*table_name,bl_record->data + offset,len);
	offset += len;

	len = *(uint8_t*)(bl_record->data + offset);
	*field_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*field_name,0,len + 1);
	offset += sizeof(uint8_t);
	
	memcpy(*field_name,bl_record->data + offset,len);
	offset += len;

	return (enum index_key_alg)*(uint8_t*)(bl_record->data + offset);
}

static struct binlog_record* db_dindex_to_binrecord(char* table_name, 
												   char* field_name,
												   enum index_key_alg index_type,
                                                   MEM_POOL* mem_pool)
{
    uint32_t data_len = 0; 
	uint8_t fld_len = 0;
	uint8_t tbl_len = 0;
    char* buf = NULL;
 
  	fld_len = strlen(field_name);
	tbl_len = strlen(table_name);
    data_len = 3*sizeof(uint8_t) + fld_len + tbl_len;
    buf = (char*)mem_pool_malloc(mem_pool,data_len);
    memset(buf,0,data_len);
 
   
    uint32_t offset = 0; 

	//表名长度
    memcpy(buf + offset,&tbl_len,sizeof(uint8_t));
    offset += sizeof(uint8_t);

	//表名
	memcpy(buf + offset, table_name, tbl_len);
	offset += tbl_len;

	//列名长度
	*(uint8_t*)(buf + offset) = fld_len;
	offset += sizeof(uint8_t);

	//列名
	memcpy(buf + offset, field_name, fld_len);
	offset += fld_len;

	*(uint8_t*)(buf + offset) = index_type;

	struct binlog_record* bl_record = create_binlog_record(data_len,mem_pool);
    bl_record->op_code = OPERATION_DINDEX;
    memcpy(bl_record->data,buf,data_len);

    return bl_record;
}


//将insert参数包装成binlog record  一个字节的长度 + table name
static struct binlog_record* db_insert_to_binrecord(char* table_name, 
                                                    uint16_t sid, 
                                                    uint32_t docid,
                                                    struct row_data* rdata,
                                                    MEM_POOL* mem_pool)
{

	struct low_data_struct* buf = NULL;
	uint8_t len = strlen(table_name);
	
	//将rdata转换成buf缓存
    buf = rowdata_to_lowdata(rdata,mem_pool);
    if(buf==NULL)
    {	
	return NULL;
    }
    struct binlog_record* bl_record = create_binlog_record(buf->len + strlen(table_name) +1,mem_pool);

    bl_record->docid = docid;
    bl_record->sid = sid;
    bl_record->op_code = OPERATION_INSERT;
	*(uint8_t*)bl_record->data = len;
	memcpy(bl_record->data + sizeof(uint8_t),table_name,len);
    memcpy(bl_record->data + len +sizeof(uint8_t),buf->data,buf->len);
	
    return bl_record;
}


//将update的数据转换成binlog record记录
//table_name + field_name + 数据
static struct binlog_record* db_update_to_binrecord(char* table_name, 
													uint16_t sid, 
													uint32_t docid,
													struct low_data_struct* new_data,
													MEM_POOL* mem_pool)
{
    uint32_t data_len = 0; 
	uint8_t fld_len = 0;
	uint8_t tbl_len = 0;
    char* buf = NULL;
 
  	fld_len = strlen(new_data->field_name);
	tbl_len = strlen(table_name);
    data_len = 3*sizeof(uint8_t) + fld_len + tbl_len + new_data->len + sizeof(uint32_t);
    buf = (char*)mem_pool_malloc(mem_pool,data_len);
    memset(buf,0,data_len);
 
   
    uint32_t offset = 0; 

	//表名长度
    memcpy(buf,&tbl_len,sizeof(uint8_t));
    offset += sizeof(uint8_t);

	//表名
	memcpy(buf + offset, table_name, tbl_len);
	offset += tbl_len;

	//列名长度
	*(uint8_t*)(buf + offset) = fld_len;
	offset += sizeof(uint8_t);

	//列名
	memcpy(buf + offset, new_data->field_name, fld_len);
	offset += fld_len;

	*(uint8_t*)(buf + offset) = new_data->type;
	offset += sizeof(uint8_t);
	
    memcpy(buf+offset,&new_data->len,sizeof(uint32_t));
    offset+=sizeof(uint32_t);
	
    memcpy(buf+offset,new_data->data,new_data->len);
	
    struct binlog_record* bl_record = create_binlog_record(data_len,mem_pool);

    bl_record->docid = docid;
    bl_record->sid = sid; 
    bl_record->op_code = OPERATION_UPDATE;
    memcpy(bl_record->data,buf,data_len);

    return bl_record;
}


//将删除参数包装为binlog record记录
static struct binlog_record* db_delete_to_binrecord(char* table_name, 
													uint16_t sid, 
													uint32_t docid,
													MEM_POOL* mem_pool)
{
	uint8_t len = strlen(table_name);
	struct binlog_record* bl_record = create_binlog_record(len+1,mem_pool);

	bl_record->docid = docid;
	bl_record->sid = sid;
	bl_record->op_code = OPERATION_DELETE;

	*(uint8_t*)bl_record->data = len;
	memcpy(bl_record->data + sizeof(uint8_t),table_name,len);
	return bl_record;
}

//将删除参数包装为binlog record记录
static struct binlog_record* db_load_to_binrecord(char* table_name, 
												  uint16_t sid, 
												  char* segment_dir,
												  MEM_POOL* mem_pool)
{
	uint8_t tblen = strlen(table_name);
	uint8_t dirlen = strlen(segment_dir);
	uint32_t offset = 0;
	uint8_t len = tblen + dirlen + 2 * sizeof(uint8_t);
	
	struct binlog_record* bl_record = create_binlog_record(len,mem_pool);

	bl_record->sid = sid;
	bl_record->op_code = OPERATION_LOAD;

	*(uint8_t*)(bl_record->data + offset) = tblen;
	offset += sizeof(uint8_t);
	memcpy(bl_record->data + offset,table_name,tblen);
	offset += tblen;

	*(uint8_t*)(bl_record->data + offset) = dirlen;
	offset += sizeof(uint8_t);
	memcpy(bl_record->data + offset,segment_dir,dirlen);
	
	return bl_record;
}

void db_binrecord_to_load(struct binlog_record* bl_record,
								 char** table_name,  
 							     char** segment_dir,
 							     MEM_POOL* mem_pool)
{
	uint32_t offset = 0;
	uint8_t len;

	//表名
	len = *(uint8_t*)(bl_record->data + offset);
	*table_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*table_name, 0, len + 1);
	offset += sizeof(uint8_t);

	memcpy(*table_name,bl_record->data + offset,len);
	offset += len;

	len = *(uint8_t*)(bl_record->data + offset);
	*segment_dir = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*segment_dir,0,len + 1);
	offset += sizeof(uint8_t);

	return;
}

void db_binrecord_to_unload(struct binlog_record* bl_record,
								   char** table_name, 
								   MEM_POOL* mem_pool)
{
	uint32_t offset = 0;
	uint8_t len;

	//表名
	len = *(uint8_t*)(bl_record->data + offset);
	*table_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*table_name, 0, len + 1);
	offset += sizeof(uint8_t);

	memcpy(*table_name,bl_record->data + offset,len);

	return;
}


void db_binrecord_to_compress(struct binlog_record* bl_record,
								     char** table_name, 
								     MEM_POOL* mem_pool)
{
	uint32_t offset = 0;
	uint8_t len;

	//表名
	len = *(uint8_t*)(bl_record->data + offset);
	*table_name = (char*)mem_pool_malloc(mem_pool,len + 1);
	memset(*table_name, 0, len + 1);
	offset += sizeof(uint8_t);

	memcpy(*table_name,bl_record->data + offset,len);

	return;
}




//将删除参数包装为binlog record记录
static struct binlog_record* db_unload_to_binrecord(char* table_name, 
												    uint16_t sid, 
												    MEM_POOL* mem_pool)
{
	uint8_t tblen = strlen(table_name);
	uint8_t len = tblen  + sizeof(uint8_t);
	
	struct binlog_record* bl_record = create_binlog_record(len,mem_pool);

	bl_record->sid = sid;
	bl_record->op_code = OPERATION_UNLOAD;

	*(uint8_t*)bl_record->data= tblen;
	memcpy(bl_record->data + sizeof(uint8_t),table_name,tblen);
	
	return bl_record;
}

//将删除参数包装为binlog record记录
static struct binlog_record* db_compress_to_binrecord(char* table_name, 
												      MEM_POOL* mem_pool)
{
	uint8_t tblen = strlen(table_name);
	uint8_t len = tblen  + sizeof(uint8_t);
	
	struct binlog_record* bl_record = create_binlog_record(len,mem_pool);
	bl_record->op_code = OPERATION_COMPRESS;

	*(uint8_t*)bl_record->data= tblen;
	memcpy(bl_record->data + sizeof(uint8_t),table_name,tblen);
	
	return bl_record;
}

// make sure segment data be truncated befor insert.
static int db_recover_check(struct recover_check_param_t *param, struct binlog_record *rec)
{
	if (rec->op_code != OPERATION_INSERT)
		return 0;

	if(rec->sid >= db->max_segment_num) {
		log_error( "invalid sid %u, db max segment num %u", rec->sid, db->max_segment_num );
		return -1;
	}

	// get table name from binlog record
	uint8_t len = *(uint8_t *)rec->data;
	char name_buf[len + 1];
	strncpy(name_buf, (char *)rec->data + sizeof(uint8_t), len);
	name_buf[len] = '\0';
	char *table = name_buf;

	struct key_value_t *kv = NULL;
	if (NULL == param->last_table || strcmp(param->last_table, table) != 0 ) {
		kv = (struct key_value_t *)string_map_get(param->table_sid, table);

		if (NULL == kv) {
			// add table to param->table_sid
			kv = (struct key_value_t*)mem_pool_malloc(param->mem, sizeof(*kv));

			kv->key = (char *)mem_pool_malloc(param->mem, len + 1);
			strcpy(kv->key, table);

			kv->value = mem_pool_malloc(param->mem, db->max_segment_num);
			memset(kv->value, 0, db->max_segment_num);

			string_map_put(param->table_sid, kv->key, kv, 0);
		}
	}
	else {
		if (rec->sid == param->last_sid)
			return 0;
	}
	
	// get segment flag
	if (NULL == kv) {
		kv = (key_value_t *)string_map_get(param->table_sid, table);
		assert(NULL != kv);
	}
	table = kv->key;
	char *seg_flag = (char *)kv->value; // segment flag, 1 for truncated

	param->last_sid = rec->sid;
	param->last_table = table;

	struct table_manager *table_instance;
	if (seg_flag[rec->sid] || // segment been truncated
			(table_instance = db_get_table_instance(table)) == NULL ) { // table not exist
		return 0;
	}

	log_info("table_recover table name %s, sid %d, docid %u", table, rec->sid, rec->docid);
	int rc = table_recover(table_instance, rec->sid, rec->docid);
	if (0 == rc)
		seg_flag[rec->sid] = 1;
	return rc;
}


//数据恢复
static int32_t db_recover_data()
{
	BL_READER_PTR binlog_reader;
	int32_t ret;

	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct recover_check_param_t check_param;
	check_param.mem = mem_pool;
	check_param.last_sid = 0;
	check_param.last_table = NULL;
	check_param.table_sid = init_string_map(mem_pool, MAX_TABLE_NUM);

	binlog_reader =  binlog_reader_init_bytime(db->binlog_dir,
											   db->binlog_maxsize, 
											   db->bl_meta->last_checkpoint,
											   mem_pool);

	if(binlog_reader == NULL)
	{
		log_warn("启动开始时，binlog初始化失败");
		mem_pool_destroy(mem_pool);
		return ERROR_BINLOG_FAILED;
	}

	log_info("初始化binlog checkpoint time:%llu binlog_dir:%s binlog_maxsize:%u",
			db->bl_meta->last_checkpoint, db->binlog_dir, db->binlog_maxsize);

	//开始日志恢复
	MEM_POOL* mem_pool_record = mem_pool_init(MB_SIZE);
	log_info("开始启动binlog恢复...");
	while(1)
	{
		int break_flag = 0;
		struct binlog_record *rec = NULL, *pre_rec = NULL;
		for( ; 1; pre_rec = rec) {
			ret = binlog_read_record(binlog_reader,&rec,mem_pool_record);
			if( ret <= 0 ) {
				log_info("binlog读取结束，或失败 ret:%d",ret);
				break_flag = 1;
				break;
			}
			log_debug( "binlog record time %u, sid %u, docid %u, op %d", rec->time, rec->sid, rec->docid, rec->op_code );
			if( NULL == pre_rec || IS_CONFIRM_RECORD(pre_rec) )
				continue;
			if( !IS_CONFIRM_RECORD(rec) ) { // previous record not confirmed
				log_warn( "binlog record not confirmed, time %u, op_code %d, sid %u, docid %u",
						pre_rec->time, pre_rec->op_code, pre_rec->sid, pre_rec->docid );
				continue;
			}

			if( rec->op_code == OPERATION_CONFIRM_FAIL ) {
				continue;
			} else { // confirm OK
				break;
			}
		}
		if( break_flag )
			break;

		ret = db_recover_check(&check_param, pre_rec);
		if (0 != ret)
			break;

		ret = db_execute_binrecord(pre_rec,mem_pool_record);

		//重复插入，在数据恢复的时候忽略
		if(ret == MILE_RETURN_SUCCESS || 
		   ret == ERROR_INSERT_REPEAT || 
		   ret == ERROR_PK_CONFLICT   ||
		   ret == ERROR_ONLY_FILTER_SUPPORT ||
		   ret == ERROR_INDEX_FIELD_COMPRESSED)
		{
			mem_pool_reset(mem_pool_record);
			ret = MILE_RETURN_SUCCESS;
		}
		else
			break;
	}
	if( MILE_RETURN_SUCCESS == ret ) {
		if( binlog_is_read_all( binlog_reader ) != 1) {
			ret = ERROR_BINLOG_FAILED;
		}
	}

	binlog_reader_destroy(binlog_reader);
	mem_pool_destroy(mem_pool_record);
	mem_pool_destroy(mem_pool);

	log_info("binlog恢复结束 ret:%d", ret);
	return ret;
}



//slave在开始追赶的时候，需要记录开始的时间点，用于测速
//并返回需要同步的偏移量
uint64_t db_start_catch_up()
{
	db->catch_up_start = get_time()/USEC_PER_MSEC;
	return db->bl_meta->offset;
}

//记录缓存的当前执行成功的位置，以及根据开始同步的位置，以及当前的同步的位置，计算速度，从而获取同步的状态
int32_t db_slave_set_offset(uint64_t offset_cur,uint64_t offset_left)
{
	db->bl_meta->offset = offset_cur;
	
	//sync到磁盘中
	if(msync(db->bl_meta,sizeof(db->bl_meta),MS_SYNC) != 0) 	   // make sure synced
	{
		log_error("msync error while flush: %s", strerror(errno));
		return ERROR_SYNC_FAILED;
	}

	/*计算速度*/
	uint64_t now = get_time()/USEC_PER_MSEC;
	uint64_t finish_offset = offset_cur-db->offset_start;
	uint64_t speed = now - db->catch_up_start > 0 ? finish_offset/(now - db->catch_up_start) : 0;

	/*剩余的时间*/
	uint64_t time_left = 0 != speed ? offset_left/speed : UINT64_MAX;

	/*标志多长时间内追赶上的门限值*/
	log_info("当前偏移量:%llu byte 剩余偏移量:%llu byte  追赶速度:%llu byte/ms 剩余时间:%llu ms",offset_cur,offset_left,speed,time_left);

	return (time_left<db->binlog_threshold)?MILE_SLAVE_CACTCH_UP:MILE_RETURN_SUCCESS;

}

//slave接收到信息后调用，当本次缓存的数据全部执行完后，需要调用db_slave_set_offset记录当前执行成功的位置
//数据恢复会调
int32_t db_execute_binrecord(struct binlog_record* bl_record,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* field_name = NULL;
	int32_t ret=0;

	//根据binlog record 执行
	db_read_lock();
	switch(bl_record->op_code)
	{
		case OPERATION_DELETE:
		{
			table_name = (char*)mem_pool_malloc(mem_pool,*(uint8_t*)bl_record->data+1);
			memset(table_name,0,*(uint8_t*)bl_record->data+1);
			memcpy(table_name, bl_record->data + 1, *(uint8_t*)bl_record->data);
			db_readlock_table(table_name);
			ret = db_del_docid(table_name,bl_record->sid,bl_record->docid,mem_pool);
			db_unreadlock_table(table_name);
		}
			break;
			
		case OPERATION_INSERT:
		{
			struct row_data* rdata;
			rdata = db_binrecord_to_insert(bl_record, &table_name,mem_pool);
			db_readlock_table(table_name);
			ret = db_insert(table_name, &bl_record->sid,&bl_record->docid,rdata,DOCID_BY_BINLOG,mem_pool);
			db_unreadlock_table(table_name);
		}
			break;
			
		case OPERATION_UPDATE:
		{
			struct low_data_struct* new_data;
			struct low_data_struct* old_data;
			new_data = db_binrecord_to_update(bl_record,&table_name,&field_name,mem_pool);
			db_readlock_table(table_name);
			ret =  db_update(table_name,bl_record->sid,bl_record->docid,new_data,&old_data,mem_pool);
			db_unreadlock_table(table_name);
		}
			break;

		case OPERATION_INDEX:
		{	
			enum index_key_alg index_type;
			enum field_types data_type;
			index_type = db_binrecord_to_index(bl_record,&table_name,&field_name,&data_type,mem_pool);
			ret = db_ensure_index(table_name,field_name,index_type,data_type,mem_pool);
		}
			 break;
			 
		case OPERATION_DINDEX:
		{
			enum index_key_alg index_type;
			index_type = db_binrecord_to_dindex(bl_record,&table_name,&field_name,mem_pool);
			ret = db_del_index(table_name,field_name,index_type,mem_pool);
		}
			break;

		case OPERATION_COMPRESS:
		{
			db_binrecord_to_compress(bl_record,&table_name,mem_pool);
			ret = db_compress(table_name,mem_pool);
		}
			break;

		case OPERATION_LOAD:
		{
			char* segment_dir = NULL;
			db_binrecord_to_load(bl_record,&table_name,&segment_dir, mem_pool);

			ret = db_load_segment(table_name,bl_record->sid,segment_dir,mem_pool);
		}
			break;

		case OPERATION_UNLOAD:
		{
			db_binrecord_to_unload(bl_record,&table_name, mem_pool);

			ret = db_unload_segment(table_name,bl_record->sid,mem_pool);
		}
			break;
		default:
			log_warn("不支持的命令 %u",bl_record->op_code);
			break;
	}

	db_read_unlock();
	return ret;
}


int32_t db_insert(char* table_name, uint16_t* sid, uint32_t* docid, struct row_data* rdata, uint8_t flag, MEM_POOL* mem_pool)
{
	int32_t ret;
	uint64_t now = 0;

	PROFILER_START("db insert");
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		table = db_create_table(table_name);
	}

	//准备插入，获取段id，以及row_id，如果flag是由上层提供的其实是不用获取的
	PROFILER_BEGIN("table prepare insert");
	if((ret = table_prepare_insert(table,sid,docid,flag,db->mem_pool)) != MILE_RETURN_SUCCESS)
	{
		goto RET;
	}
	PROFILER_END();
	

	//先写binlog日志
	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		PROFILER_BEGIN("binlog write record");
		binlog_lock_writer( db->binlog_writer );
		ret = binlog_write_record(db->binlog_writer,
  								  db_insert_to_binrecord(table_name,*sid,*docid,rdata,mem_pool),
  								  mem_pool);
		PROFILER_END();
		if(ret != 0)
		{
			log_error("binlog日志写入失败");
			binlog_unlock_writer( db->binlog_writer );
			goto RET;
		}
	}

	PROFILER_BEGIN("table insert");
	now = get_time_usec();
	
	table_read_lock(table);
	ret = table_insert(table,*sid,*docid,rdata,mem_pool);
	table_read_unlock(table);
	
	sc_record_value(STAT_ITEM_INSERT, get_time_usec()-now);
	PROFILER_END();
	
	//执行成功确认
	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		PROFILER_BEGIN("binlog write confirm");
		if( MILE_RETURN_SUCCESS == ret )
			ret = binlog_confirm_ok(db->binlog_writer, mem_pool);
		else 
			binlog_confirm_fail(db->binlog_writer, mem_pool);
		PROFILER_END();
		binlog_unlock_writer( db->binlog_writer );
		
		if(ret != 0)
		{
			log_error("binlog确认日志写入失败");
			goto RET;
		}
	}

	RET:
		PROFILER_DUMP();
		PROFILER_STOP();
		return ret;
}



int32_t db_update(char* table_name,
				uint16_t sid,
				uint32_t docid, 
				struct low_data_struct* new_data,
				struct low_data_struct** old_data,
				MEM_POOL* mem_pool)

{
	int32_t ret;
	uint64_t now = 0;

	PROFILER_START("db update");
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		ret = ERROR_TABLE_NOT_EXIT;
		goto RET;
	}

	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		PROFILER_BEGIN("binlog write record");
		binlog_lock_writer( db->binlog_writer );
		ret = binlog_write_record(db->binlog_writer,
							      db_update_to_binrecord(table_name,sid,docid,new_data,mem_pool),
							      mem_pool);
		PROFILER_END();
		if(ret != 0)
		{
			log_error("binlog日志写入失败");
			binlog_unlock_writer( db->binlog_writer );
			goto RET;
		}

	}

	PROFILER_BEGIN("table update");
	now = get_time_usec();
    ret = table_update(table,sid,docid,new_data,old_data,mem_pool);
	sc_record_value(STAT_ITEM_UPDATE, get_time_usec()-now);
	PROFILER_END();

	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{		
			PROFILER_BEGIN("binlog write confirm");
			if( MILE_RETURN_SUCCESS == ret )
				ret = binlog_confirm_ok(db->binlog_writer, mem_pool);
			else 
				binlog_confirm_fail(db->binlog_writer, mem_pool);
			PROFILER_END();
			binlog_unlock_writer( db->binlog_writer );
			if(ret != 0)
			{
				log_error( "write binlog confirm record failed" );
				goto RET;
			}
	}

	RET:
		PROFILER_DUMP();
		PROFILER_STOP();
		return ret;
}



int32_t db_del_docid(char* table_name,uint16_t sid,uint32_t docid,MEM_POOL* mem_pool)
{	
	int32_t ret;

	PROFILER_START("db del docid");
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		ret = ERROR_TABLE_INIT_FAILED;
		goto RET;
	}
	
	//先写binlog日志
	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		PROFILER_BEGIN("binlog write record");
		binlog_lock_writer( db->binlog_writer );
		ret = binlog_write_record(db->binlog_writer,
								  db_delete_to_binrecord(table_name,sid,docid,mem_pool),
								  mem_pool);
		PROFILER_END();
		if(ret != 0)
		{
			log_error("binlog日志写入失败");
			binlog_unlock_writer( db->binlog_writer );
			goto RET;
		}
	}

	//执行删除指令
	PROFILER_BEGIN("table del docid");
	ret = table_del_docid(table,sid,docid);	
	PROFILER_END();

	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		//执行成功确认
		PROFILER_BEGIN("binlog write confirm");
		if( MILE_RETURN_SUCCESS == ret )
			ret = binlog_confirm_ok(db->binlog_writer, mem_pool);
		else 
			binlog_confirm_fail(db->binlog_writer, mem_pool);
		PROFILER_END();
		binlog_unlock_writer( db->binlog_writer );
		
		if(ret != 0)
		{
			log_error("binlog确认日志写入失败");
			goto RET;
		}
	}

	RET:
		PROFILER_DUMP();
		PROFILER_STOP();
		return ret;
}


static struct table_manager* db_get_table_instance(char* table_name)
{
	struct table_manager* table = NULL;
	PROFILER_BEGIN("db get table instance");
		
	table = (struct table_manager *)string_map_get(db->tables, table_name);

	PROFILER_END();
	return table;
}








struct low_data_struct** db_data_query_multi_col(char* table_name,uint16_t sid, uint32_t docid, char** field_names, uint32_t field_num, enum data_access_type_t data_access_type, MEM_POOL* mem_pool)
{
	struct row_data* query_row_data = NULL;
	struct low_data_struct* col_data = NULL;
	uint16_t i,j;

	uint64_t now = get_time_usec();
	
	PROFILER_START("db_data_query_multi_col");
	struct table_manager* table = db_get_table_instance(table_name);
	

	struct low_data_struct** ret = NULL;
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
	}else{
		ret = (struct low_data_struct**)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct*) * field_num);
		memset(ret,0,sizeof(struct low_data_struct*) * field_num);
		for(i = 0; i < field_num; i++)
		{
			ret[i] = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct));
			memset(ret[i], 0, sizeof(struct low_data_struct));
		}

		PROFILER_BEGIN("table data query row");
		if(data_access_type == DATA_ACCESS_FILTER)
		{	
			//如果从索引中可以读出所需要的值
			//则直接去filter查询
			for(i=0; i<field_num; i++)
			{
				col_data = table_index_value_query(table,sid,field_names[i],docid,mem_pool);
				if(col_data != NULL)
					memcpy(ret[i], col_data, sizeof(struct low_data_struct));
			}
		}else{
			//必须读原始值，需要读磁盘
			query_row_data = table_data_query_row(table,sid,docid,mem_pool);
			if(query_row_data == NULL)
			{
				ret = NULL;
			}else{
				for(i=0; i<field_num; i++)
				{
					col_data = query_row_data->datas;
					for(j=0; j<query_row_data->field_count;j++,col_data++)
					{
						if(strlen(col_data->field_name) == strlen(field_names[i]) &&
							strcmp(col_data->field_name,field_names[i]) == 0)
						{
							memcpy(ret[i], col_data, sizeof(struct low_data_struct));
							break;
						}
					}
				}
			}
		}
		PROFILER_END();
	}


	PROFILER_DUMP();
	PROFILER_STOP();

	sc_record_value(STAT_ITEM_DATA_QUERY,get_time_usec()-now);
	return ret;
}


struct low_data_struct* db_data_query_col(char* table_name,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* query_col_data = NULL;

	PROFILER_START("db_data_query_col");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table data query col");
	uint64_t now = get_time_usec();
	query_col_data = table_data_query_col(table,sid,field_name,docid,mem_pool);
	sc_record_value(STAT_ITEM_DATA_QUERY,get_time_usec()-now);
	PROFILER_END();

	PROFILER_DUMP();
	PROFILER_STOP();
	return query_col_data;
}


struct row_data* db_data_query_row(char* table_name,uint16_t sid,uint32_t docid,MEM_POOL* mem_pool)
{
	struct row_data* query_row_data = NULL;

	PROFILER_START("db data query row");
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table data query row");
	uint64_t now = get_time_usec();
	query_row_data = table_data_query_row(table,sid,docid,mem_pool);
	sc_record_value(STAT_ITEM_DATA_QUERY,get_time_usec()-now);
	PROFILER_END();

	PROFILER_DUMP();
	PROFILER_STOP();
	return query_row_data;
}



struct list_head* db_index_range_query(char* table_name, char* field_name, \
			struct hint_array* time_cond, struct db_range_query_condition * range_condition, MEM_POOL* mem_pool)
{
	struct list_head* ret = NULL;
	
	PROFILER_START("db index range query");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table index range query");
	uint64_t now = get_time_usec();
	ret = table_index_range_query(table,field_name,time_cond,range_condition,mem_pool);
	sc_record_value(STAT_ITEM_INDEX_RANGE, now-get_time_usec());
	PROFILER_END();

	PROFILER_DUMP();
	PROFILER_STOP();
	return ret;
}



struct low_data_struct* db_index_value_query(char* table_name,uint16_t sid,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* ret = NULL;

	PROFILER_START("db index value query");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table index value query");
	ret = table_index_value_query(table,sid,field_name,docid,mem_pool);
	PROFILER_END();

	PROFILER_DUMP();
	PROFILER_STOP();
	return ret;
}





struct list_head* db_seghint_query(char* table_name, struct hint_array* time_cond, MEM_POOL* mem_pool)
{
	struct list_head* ret = NULL;

	PROFILER_START("db index equal query");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table index equal query");
	ret = table_seghint_query(table, time_cond, mem_pool);
	PROFILER_END();
	
	PROFILER_DUMP();
	PROFILER_STOP();
	return ret;
}



uint32_t db_fulltext_index_length_query(char* table_name, struct list_head* seg_list, struct low_data_struct* data, MEM_POOL_PTR mem_pool)
{
	uint32_t ret = NULL;

	PROFILER_START("db index equal query");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table index equal query");
	uint64_t now = get_time_usec();
	ret = table_index_count_query(table,seg_list,data,mem_pool);
	sc_record_value(STAT_ITEM_INDEX_EQUAL, get_time_usec()-now);
	PROFILER_END();
	
	PROFILER_DUMP();
	PROFILER_STOP();
	return ret;
}



struct list_head* db_index_equal_query(char* table_name,
 								       struct list_head* seg_list,
 								       struct low_data_struct* data,
 								       MEM_POOL* mem_pool)
{
	struct list_head* ret = NULL;

	PROFILER_START("db index equal query");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table index equal query");
	uint64_t now = get_time_usec();
	ret = table_index_equal_query(table,seg_list,data,mem_pool);
	sc_record_value(STAT_ITEM_INDEX_EQUAL, get_time_usec()-now);
	PROFILER_END();
	
	PROFILER_DUMP();
	PROFILER_STOP();
	return ret;
}


struct list_head* db_fulltext_index_equal_query(char* table_name,
									 		    struct list_head* seg_list,
									            struct low_data_struct* data,
									            MEM_POOL* mem_pool)
{
	struct list_head* ret = NULL;

	PROFILER_START("db fulltext query");
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		PROFILER_DUMP();
		PROFILER_STOP();
		return NULL;
	}

	PROFILER_BEGIN("table fulltext query");
	uint64_t now = get_time_usec();
	ret = table_fulltext_index_equal_query(table,seg_list,data,mem_pool);
	sc_record_value(STAT_ITEM_INDEX_EQUAL, get_time_usec()-now);
	PROFILER_END();
	
	PROFILER_DUMP();
	PROFILER_STOP();
	return ret;
}




int32_t db_replace_all_segments(char* table_name,char* segments_dir,MEM_POOL* mem_pool)
{
	int32_t ret;
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	pthread_mutex_lock(&db->task_locker);
	ret = table_lock(table);
	
	if(ret != MILE_RETURN_SUCCESS)
		return ret;

	log_error("开始执行db_replace_all_segments");
	uint64_t now = get_time_usec();
	ret = table_replace_all_segments(table,segments_dir,db->mem_pool);
	log_error("执行db_replace_all_segments 消耗时间: %llums",(get_time_usec()-now)/1000);
	
	table_unlock(table);
	
	pthread_mutex_unlock(&db->task_locker);

	return ret;
}



int32_t db_unload_segment(char* table_name,uint16_t sid,MEM_POOL* mem_pool)
{
	int32_t ret;
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	pthread_mutex_lock(&db->task_locker);
	ret = table_lock(table);

	if(ret != MILE_RETURN_SUCCESS)
		return ret;
	
	ret = table_unload_segment(table,sid,1);

	table_unlock(table);

	pthread_mutex_unlock(&db->task_locker);

	return ret;
}

int32_t db_set_segment_current(char* table_name, uint16_t sid)
{
	struct table_manager* table = db_get_table_instance(table_name);
	
	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	return table_set_segment_current(table,sid);
}


int32_t db_load_segment(char* table_name,int16_t sid,char* segment_dir,MEM_POOL* mem_pool)
{
	int32_t ret;
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	pthread_mutex_lock(&db->task_locker);
	
	ret = table_lock(table);

	if(ret != MILE_RETURN_SUCCESS)
		return ret;
	
	ret = table_load_segment(table,sid,segment_dir,db->mem_pool);

	table_unlock(table);

	pthread_mutex_unlock(&db->task_locker);

	return ret;
}



int32_t db_is_docid_deleted(char* table_name,uint16_t sid,uint32_t docid)
{
	int32_t ret;

	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	ret = table_is_docid_deleted(table,sid,docid);
	return ret;
}


int64_t db_get_record_num(char* table_name, struct list_head* seg_list)
{
	int64_t ret;

	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	ret = table_get_record_num(table, seg_list);
	return ret;
}




int64_t db_get_delete_num(char* table_name, struct list_head* seg_list)
{
	int64_t ret;

	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	ret = table_get_delete_num(table, seg_list);
	return ret;
}





uint64_t db_get_segment_ctime(char* table_name,uint16_t sid)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	return table_get_segment_ctime(table,sid);
}


uint64_t db_get_segment_mtime(char* table_name,uint16_t sid)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	
	return table_get_segment_mtime(table,sid);
}



struct segment_meta_data* db_query_segment_stat(char* table_name,uint16_t* max_segment_num,MEM_POOL * mem_pool)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return NULL;
	}
	
	return table_query_segment_stat(table,max_segment_num,mem_pool);
}



struct index_field_meta* db_query_index_stat(char* table_name,uint16_t* index_field_count,MEM_POOL * mem_pool)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return NULL;
	}
	
	return table_query_index_stat(table,index_field_count,mem_pool);
}


enum field_types db_query_data_type(char* table_name,char* field_name)
{
	struct table_manager* table = db_get_table_instance(table_name);
	enum field_types data_type = (enum field_types)-1;


	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return data_type;
	}

	table_read_lock(table);
	if(check_index(table,field_name,HI_KEY_ALG_FILTER,&data_type))
	{
		table_read_unlock(table);
		return data_type;
	}

	if(check_index(table,field_name,HI_KEY_ALG_HASH,&data_type))
	{
		table_read_unlock(table);
		return data_type;
	}
	table_read_unlock(table);
	return data_type;
}





enum field_access_type_t db_get_data_access_type(char* table_name, char* field_name)
{
	struct table_manager* table = db_get_table_instance(table_name);
	enum field_access_type_t field_access_type = FIELD_ACCESS_ORIGINAL;
	enum field_types data_type;


	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return field_access_type;
	}

	table_read_lock(table);
	if(check_index(table,field_name,HI_KEY_ALG_FILTER,&data_type))
	{
		if(data_type == HI_TYPE_STRING)
		{
			field_access_type = FIELD_ACCESS_FILTER_HASHED;
		}else{
			field_access_type = FIELD_ACCESS_FILTER_ORIGINAL;
		}
	}
	table_read_unlock(table);
	return field_access_type;
}







int32_t db_lock_table(char* table_name)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表 需创建",table_name);
		table = db_create_table(table_name);
	}

	return table_lock(table);
}

void db_unlock_table(char* table_name)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表 需创建",table_name);
		table = db_create_table(table_name);
	}

	table_unlock(table);
	return;
}


void db_readlock_table(char* table_name)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表 需创建",table_name);
		table = db_create_table(table_name);
	}

	return table_read_lock(table);
}


void db_unreadlock_table(char* table_name)
{
	struct table_manager* table = db_get_table_instance(table_name);

	if(table == NULL)
	{
		log_warn("不存在%s 表 需创建",table_name);
		table = db_create_table(table_name);
	}

	return table_read_unlock(table);
}



void db_read_lock()
{
	pthread_rwlock_rdlock(&db->locker);
}


void db_read_unlock()
{
	pthread_rwlock_unlock(&db->locker);
}



int32_t db_compress(char* table_name,MEM_POOL* mem_pool)
{
	struct table_manager* table = db_get_table_instance(table_name);
	int32_t ret;

	if(table == NULL)
	{
		log_warn("不存在%s 表",table_name);
		return ERROR_TABLE_INIT_FAILED;
	}

	//执行压缩指令
	pthread_mutex_lock(&db->task_locker);
	ret = table_compress(table,db->mem_pool);
	pthread_mutex_unlock(&db->task_locker);

	// write binlog after operation, just for syncing operation to slave
	if(db->binlog_flag && db->db_stat == RECOVER_FIN)
	{
		binlog_lock_writer( db->binlog_writer );
		if( binlog_write_record(db->binlog_writer, db_compress_to_binrecord(table_name,mem_pool), mem_pool)
				|| ( MILE_RETURN_SUCCESS == ret ?
				  binlog_confirm_ok(db->binlog_writer, mem_pool) :  binlog_confirm_fail(db->binlog_writer, mem_pool) ) != 0 ) {
			log_error( "write binlog record failed" );
			if( MILE_RETURN_SUCCESS == ret ) 
				ret = ERROR_BINLOG_FAILED;
		}
		binlog_unlock_writer( db->binlog_writer );
	}
	
	return ret;
}


static int32_t db_checkpoint_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	table_checkpoint((struct table_manager*)value);
	return MILE_RETURN_SUCCESS;
}

static int32_t db_release_iterator_func(struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data)
{
	table_release((struct table_manager*)value);
	return MILE_RETURN_SUCCESS;
}

void db_release()
{
	g_running_flag = 0;

	pthread_join(db->checkpoint_tid, NULL);

	if(db->binlog_flag)
	{
		if(db->binlog_sync_interval > 0)
			pthread_join(db->sync_tid,NULL);
		binlog_writer_destroy(db->binlog_writer);
	}

	/*释放所有的表信息*/
	string_map_for_each(db->tables,db_release_iterator_func,NULL);
	
	pthread_rwlock_destroy(&db->locker);
	pthread_mutex_destroy(&db->task_locker);

	
	msync(db->table_count,TABLE_RUNTIME_SIZE,MS_SYNC);        // make sure synced
	munmap(db->table_count, TABLE_RUNTIME_SIZE);

	/*释放内存池*/
	mem_pool_destroy(db->mem_pool); 	
	return;
}


void db_checkpoint()
{
	time_t checkpoint_time = time(NULL);

	// sync到磁盘中
	if(msync(db->bl_meta,sizeof(struct binlog_meta),MS_SYNC) != 0)        // make sure synced
	{
		log_error("msync error while flush: %s", strerror(errno));
	}

	string_map_for_each(db->tables,db_checkpoint_iterator_func,NULL);

	// 将时间点写入文件
	db->bl_meta->last_checkpoint = checkpoint_time;
	if(msync(db->bl_meta,sizeof(struct binlog_meta),MS_SYNC) != 0)        // make sure synced
	{
		log_error("msync error while flush: %s", strerror(errno));
	}
		
	return;
}

void set_db_readable()
{
	if( !db->readable )
		log_info( "set db readable" );
	db->readable = 1;
}

void set_db_unreadable()
{
	log_info( "set db unreadable" );
	db->readable = 0;
}

void *db_mmap_switch( void *arg)
{
	struct segment_full_param_t *param = (struct segment_full_param_t *)arg;

	db_read_lock();
	struct table_manager *table = db_get_table_instance(param->tablename);
	if( NULL == table ) {
		log_warn( "table not exist, %s", param->tablename );
	} else {
		while( !table->new_seg_complete && g_running_flag ) {
			// unlock db while sleep
			db_read_unlock();
			MILE_USLEEP( 200 * 1000 ); // sleep 200ms
			db_read_lock();
			// get table instance again
			table = db_get_table_instance(param->tablename);
			if (NULL == table) {
				log_warn( "table not exist, %s", param->tablename );
				break;
			}
		}
		if (NULL != table && g_running_flag)
			table_mmap_switch( table, param->sid);
	}

	db_read_unlock();

	mem_pool_destroy(param->mem);

	// do checkpoint after mmap switch
	if( g_running_flag )
		db_checkpoint();

	return NULL;
}

int create_mmap_switch_thread(char *tablename, uint16_t sid)
{
	MEM_POOL_PTR mem = mem_pool_init(MB_SIZE); // destory this mem_pool in created thread
	if( NULL == mem )
		return -1;
	struct segment_full_param_t *param = (struct segment_full_param_t *)mem_pool_malloc(
			mem, sizeof(struct segment_full_param_t));

	param->mem = mem;
	param->tablename = (char *)mem_pool_malloc(mem, strlen(tablename) + 1);
	strcpy(param->tablename, tablename);
	param->sid = sid;

	pthread_attr_t attr;
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate( &attr, PTHREAD_CREATE_DETACHED );

	pthread_t tid;
	if( pthread_create(&tid, &attr, db_mmap_switch, param) != 0 ) {
		log_error( "create thread failed, errno %d", errno );
		mem_pool_destroy( mem );
		return -1;
	}
	return 0;
}

