extern "C"
{
#include "../src/common/mem.h"
#include "../src/storage/db.h"
#include "../src/storage/config_parser.h"
}

uint8 running_flag = 1;


struct hint_array * get_time_hint(MEM_POOL* mem_pool)
{
        struct hint_array* time_hint = (struct hint_array*)mem_pool_malloc(mem_pool,sizeof(struct hint_array));
        memset(time_hint,0,sizeof(time_hint));

        time_hint->n = 2;

        time_hint->hints = (uint64*)mem_pool_malloc(mem_pool,2*sizeof(uint64));
        time_hint->hints[0] = 0;
        time_hint->hints[1] = 0xFFFFFFFFFFFFFFFF;

        return time_hint;
}

void db_initialize(MEM_POOL* mem_pool)
{
	struct db_conf* config = config_parser("docserver.conf",mem_pool);
	running_flag = 1;
	config->binlog_maxsize = 64*MB_SIZE;
	config->binlog_flag = 1;
	config->binlog_threshold = 1;
	config->binlog_flag = 0;
	config->row_limit = 1000;
	system("rm -rf storage");
	system("rm -rf binlog");
	system("rm -rf log");
	db_init(config);
}



void get_low_data_bydocid(struct low_data_struct* data,enum field_types type,uint32 docid,MEM_POOL* mem_pool)
{
	switch(type)
	{
		case HI_TYPE_STRING:
			data->len = 100;
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_STRING;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_STRING");
			memset(data->data,0,data->len);
			sprintf((char*)data->data,"ali%u",docid);
			break;
		case HI_TYPE_LONG:
			data->len = get_unit_size(HI_TYPE_LONG);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_LONG;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_LONG");
			*(int32*)(data->data) = 6000+docid;
			break;
		case HI_TYPE_LONGLONG:
			data->len = get_unit_size(HI_TYPE_LONGLONG);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_LONGLONG;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_LONGLONG");
			*(int64*)(data->data) = 8000+docid;
			break;
		case HI_TYPE_TINY:
			data->len = get_unit_size(HI_TYPE_TINY);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_TINY;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_TINY");
			*(int8*)(data->data) = docid%5;
			break;
		case HI_TYPE_DOUBLE:
			data->len = get_unit_size(HI_TYPE_DOUBLE);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_DOUBLE;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_DOUBLE");
			*(double*)(data->data) = 2.0+docid;
			break;
		default:
			break;
	}

	return;
}

struct row_data* get_row_data_bydocid(uint16 field_count,enum field_types* types,uint32 docid,MEM_POOL* mem_pool)
{
	struct row_data*  rdata = (struct row_data*)mem_pool_malloc(mem_pool,sizeof(struct row_data));
	memset(rdata,0,sizeof(struct row_data));
	rdata->field_count = field_count;

	rdata->datas = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct) * field_count);
	memset(rdata->datas, 0, sizeof(struct low_data_struct) * field_count);

	uint16 i;
	struct low_data_struct* data = rdata->datas;
	for(i=0; i<field_count; i++,data++)
	{
		get_low_data_bydocid(data,types[i],docid,mem_pool);
	}
	return rdata;
}

void* db_insert_thread(void* arg)
{
	uint64 i = 0;
	struct row_data* rdata = NULL;
	uint16 field_count = 5;
	uint16 sid = 0;
	uint32 docid = 0;
	uint32 ret = 0;
	enum field_types types[5] = {HI_TYPE_STRING,HI_TYPE_LONG,HI_TYPE_TINY,HI_TYPE_LONGLONG,HI_TYPE_DOUBLE};
	MEM_POOL* mem_pool_local = mem_pool_init(MB_SIZE);
	
	while(running_flag)
	{
		mem_pool_reset(mem_pool_local);
		rdata = get_row_data_bydocid(field_count,types,i,mem_pool_local);

		db_read_lock();
		db_lock_table("CTU");
		ret = db_insert("CTU",&sid,&docid,rdata,DOCID_BY_SELF,mem_pool_local);
		db_unlock_table("CTU");
		db_read_unlock();
		i++;
		usleep(100);
	}
	
	mem_pool_destroy(mem_pool_local);
	log_warn("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~insert线程退出~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
	return NULL;
}


//(HI_TYPE_STRING(hash) HI_TYPE_LONG(filter) HI_TYPE_LONGLONG(filter) HI_TYPE_TINY(fitler) HI_TYPE_DOUBLE(filter))
void* db_query_thread(void* arg)
{
	uint32 docid = 0;
	MEM_POOL* mem_pool_local = mem_pool_init(MB_SIZE);
	struct low_data_struct data;
	struct low_data_struct* filter_ret = NULL;
	struct list_head* hash_ret = NULL;
	while(running_flag)
	{
		mem_pool_reset(mem_pool_local);
		docid = rand();
		get_low_data_bydocid(&data,HI_TYPE_STRING,docid,mem_pool_local);
		db_read_lock();
		db_readlock_table("CTU");
		
		//通过HI_TYPE_STRING的hash查
		hash_ret = db_index_equal_query("CTU",get_time_hint(mem_pool_local),&data,mem_pool_local);
		filter_ret = db_index_value_query("CTU",docid/100000,"HI_TYPE_LONG",docid%100000,mem_pool_local);
		filter_ret = db_index_value_query("CTU",docid/100000,"HI_TYPE_LONGLONG",docid%100000,mem_pool_local);
		filter_ret = db_index_value_query("CTU",docid/100000,"HI_TYPE_TINY",docid%100000,mem_pool_local);
		filter_ret = db_index_value_query("CTU",docid/100000,"HI_TYPE_DOUBLE",docid%100000,mem_pool_local);
		

		db_unreadlock_table("CTU");
		db_read_unlock();
		usleep(100);
	}
	
	mem_pool_destroy(mem_pool_local);
	log_warn("query线程退出");
	return NULL;
}


void* db_build_index_thread(void* arg)
{
	MEM_POOL* mem_pool_local = mem_pool_init(MB_SIZE);
	while(running_flag)
	{
		mem_pool_reset(mem_pool_local);
		db_ensure_index("CTU","HI_TYPE_STRING",HI_KEY_ALG_HASH,HI_TYPE_STRING,mem_pool_local);
		db_ensure_index("CTU","HI_TYPE_LONG",HI_KEY_ALG_FILTER,HI_TYPE_LONG,mem_pool_local);
		db_ensure_index("CTU","HI_TYPE_LONGLONG",HI_KEY_ALG_FILTER,HI_TYPE_LONGLONG,mem_pool_local);
		db_ensure_index("CTU","HI_TYPE_TINY",HI_KEY_ALG_FILTER,HI_TYPE_TINY,mem_pool_local);
		db_ensure_index("CTU","HI_TYPE_DOUBLE",HI_KEY_ALG_FILTER,HI_TYPE_DOUBLE,mem_pool_local);

		
		sleep(20);
		
		db_del_index("CTU","HI_TYPE_STRING",HI_KEY_ALG_HASH,mem_pool_local);
		db_del_index("CTU","HI_TYPE_LONG",HI_KEY_ALG_FILTER,mem_pool_local);
		db_del_index("CTU","HI_TYPE_LONGLONG",HI_KEY_ALG_FILTER,mem_pool_local);
		db_del_index("CTU","HI_TYPE_TINY",HI_KEY_ALG_FILTER,mem_pool_local);
		db_del_index("CTU","HI_TYPE_DOUBLE",HI_KEY_ALG_FILTER,mem_pool_local);
		
	}

	mem_pool_destroy(mem_pool_local);
	log_warn("build index线程退出");
	return NULL;
}

void* db_dump_thread(void* arg)
{
	uint32 docid = 0;
	MEM_POOL* mem_pool_local = mem_pool_init(MB_SIZE);
	while(running_flag)
	{
		docid = rand()%1024;
		db_unload_segment("CTU",docid,mem_pool_local);

		sleep(2);
	}
	
	mem_pool_destroy(mem_pool_local);
	log_warn("dump线程退出");
	return NULL;
}

void* db_compress_thread(void* arg)
{
	MEM_POOL* mem_pool_local = mem_pool_init(MB_SIZE);
	while(running_flag)
	{
		db_compress("CTU",mem_pool_local);
		usleep(100);
	}
	
	mem_pool_destroy(mem_pool_local);
	log_warn("compress线程退出");
	return NULL;
}




/*
1、造数据，所有数据类型，加上docid进行级联 HI_TYPE_STRING HI_TYPE_LONG HI_TYPE_LONGLONG HI_TYPE_TINY HI_TYPE_DOUBLE
2、起一个插入线程,所有的数据按行插入
3、起一个索引建立线程，(HI_TYPE_STRING(hash) HI_TYPE_LONG(filter) HI_TYPE_LONGLONG(filter) HI_TYPE_TINY(fitler) HI_TYPE_DOUBLE(filter))
建完一轮，接着删除
4、起一个线程进行查询，先随机一个数，模10，根据indexwhere 查询HI_TYPE_STRING，出来后查询HI_TYPE_LONG HI_TYPE_LONGLONG HI_TYPE_TINY HI_TYPE_DOUBLE
5、起一个压缩线程
6、随机一个段进行dump
*/


int main(int argc, char* argv) { 
	pthread_t insert_thread;
	pthread_t query_thread;
	pthread_t compress_thread;
	pthread_t dump_thread;
	pthread_t build_index_thread;

	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);
	db_initialize(mem_pool);
	
	pthread_create(&insert_thread,NULL,db_insert_thread,NULL);
	sleep(10);
	
	pthread_create(&query_thread,NULL,db_query_thread,NULL);
	pthread_create(&compress_thread,NULL,db_compress_thread,NULL);
	pthread_create(&dump_thread,NULL,db_dump_thread,NULL);
	pthread_create(&build_index_thread,NULL,db_build_index_thread,NULL);


	sleep(60*60);
	running_flag = 0;

	pthread_join(insert_thread,NULL);
	pthread_join(query_thread,NULL);
	pthread_join(compress_thread,NULL);
	pthread_join(dump_thread,NULL);
	pthread_join(build_index_thread,NULL);

	mem_pool_destroy(mem_pool);
	return 0;
}


















