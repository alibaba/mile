#include "def.h"
extern "C"
{
#include "../../indexengine/src/common/mem.h"
#include "../../indexengine/src/storage/filter_index.h"
#include "../../indexengine/src/storage/filtercompress.h"
}




void test_filter_compress1(int n, int type)
{
	MEM_POOL_PTR mem_pool = mem_pool_init(M_1M);
    struct filter_index_config config;
    char dir_path[] = "/tmp/filter_test";
    config.unit_size = 8;
	config.row_limit = 30000;
	strcpy(config.work_space,dir_path);
	config.type = HI_TYPE_LONGLONG;
	mkdirs(dir_path);

	uint64 time_old, time_now;

	time_old = time_now = get_time_usec();
	struct filter_index_manager* filter_index = filter_index_init(&config,mem_pool);
	time_now = get_time_usec();
	printf("filter_index_init cost:%lld us\n", time_now - time_old);


	enum field_types ftype;
	switch(type)
	{
		case 1:
			ftype = HI_TYPE_TINY;
			break;
		case 2:
			ftype = HI_TYPE_SHORT;
			break;
		case 4:
			ftype = HI_TYPE_LONG;
			break;
		case 8:
			ftype = HI_TYPE_LONGLONG;
			break;
		default:
			ftype = HI_TYPE_LONGLONG;
	}

	int32 i;
	int32 ret;
	struct low_data_struct insert_data;

	time_old = time_now = get_time_usec();
	//插入3遍，这样重复数据就是3遍
	for ( i = 0; i < n; i += 1 )
	{
		get_low_data2(&insert_data, i, ftype, mem_pool);
		ret = filter_index_insert(filter_index,&insert_data, i);
		ASSERT_EQ(ret, 0);
	}
	for ( i = 0; i < n; i += 1 )
	{
		get_low_data2(&insert_data, i, ftype, mem_pool);
		ret = filter_index_insert(filter_index,&insert_data, n+i);
		ASSERT_EQ(ret, 0);
	}
	for ( i = 0; i < n; i += 1 )
	{
		get_low_data2(&insert_data, i, ftype, mem_pool);
		ret = filter_index_insert(filter_index,&insert_data, 2*n+i);
		ASSERT_EQ(ret, 0);
	}
	time_now = get_time_usec();
	printf("filter_index_insert cost:%lld us\n", time_now - time_old);

	//压缩
	time_old = time_now = get_time_usec();
	struct filter_compress_manager *seg_filter_com = filter_compress_load(filter_index->storage, type, mem_pool);
	time_now = get_time_usec();
	printf("filter_compress_load cost:%lld us\n", time_now - time_old);

	struct low_data_struct* query_data = NULL;
	time_old = time_now = get_time_usec();
	for ( i = 0; i < n; i += 1 )
	{
		query_data = filter_index_query(filter_index, i, mem_pool);
		switch(type)
		{
			case 1:
				ASSERT_EQ(*((char*)query_data->data), (char)i);
				break;
			case 2:
				ASSERT_EQ(*((short*)query_data->data), (short)i);
				break;
			case 4:
				ASSERT_EQ(*((int*)query_data->data), (int)i);
				break;
			case 8:
				ASSERT_EQ(*((int64*)query_data->data), (int64)i);
				break;
		}
		ASSERT_EQ(query_data->len, 8);
	}
	time_now = get_time_usec();
	printf("filter_index_query cost:%lld us\n", time_now - time_old);

	time_old = time_now = get_time_usec();
	for ( i = 0; i < n; i += 1 )
	{
		query_data = filter_compress_query(seg_filter_com, i, mem_pool);
		switch(type)
		{
			case 1:
				ASSERT_EQ(*((char*)query_data->data), (char)i);
				break;
			case 2:
				ASSERT_EQ(*((short*)query_data->data), (short)i);
				break;
			case 4:
				ASSERT_EQ(*((int*)query_data->data), (int)i);
				break;
			case 8:
				ASSERT_EQ(*((int64*)query_data->data), (int64)i);
				break;
			
		}
		ASSERT_EQ(query_data->len, type);
	}
	time_now = get_time_usec();
	printf("filter_compress_query cost:%lld us\n", time_now - time_old);

	filter_compress_release(seg_filter_com);
	filter_index_release(filter_index);
	filter_index = NULL;

	mem_pool_destroy(mem_pool);

}


void test_filter_compress2()
{
	MEM_POOL_PTR mem_pool = mem_pool_init(M_1M);
    char dir_path[] = "/tmp/filter_test";
	mkdirs(dir_path);
	struct filter_index_manager* filter_index = NULL;
	struct filter_compress_manager* filter_compress = NULL;
	uint32 i = 0;

	/*~~~~~~~~~~~~~测试LONGLONG型~~~~~~~~~~~~~~~~`*/
	struct filter_index_config config;
    config.unit_size = 8;
	config.row_limit = 5;
	strcpy(config.work_space,dir_path);
	config.type = HI_TYPE_LONGLONG;
	struct low_data_struct long_data;
	get_low_data(&long_data,HI_TYPE_LONGLONG,mem_pool);
	

	//测试所有值都是一样的
	filter_index = filter_index_init(&config,mem_pool);

	for(i=0;i<config.row_limit;i++)
		filter_index_insert(filter_index,&long_data,i);
	
	filter_compress = filter_compress_load(filter_index->storage,8,mem_pool);
	struct low_data_struct* long_query_ret = NULL;
	for(i=0;i<config.row_limit;i++)
	{
		long_query_ret = filter_compress_query(filter_compress,i,mem_pool);
		ASSERT_EQ(*(uint64*)long_query_ret->data,8888);
	}
	filter_compress_destroy(filter_compress);
	filter_compress = NULL;
	filter_index_destroy(filter_index);
	filter_index = NULL;
	
	//测试所有值有一对一样的
	filter_index = filter_index_init(&config,mem_pool);
	filter_index_insert(filter_index,&long_data,0);
	filter_index_insert(filter_index,&long_data,1);
	for(i=2;i<config.row_limit;i++)
	{
		*(uint64*)long_data.data = i;
		filter_index_insert(filter_index,&long_data,i);
	}

	filter_compress = filter_compress_load(filter_index->storage,8,mem_pool);
	long_query_ret = filter_compress_query(filter_compress,0,mem_pool);
	ASSERT_EQ(*(uint64*)long_query_ret->data,8888);
	long_query_ret = filter_compress_query(filter_compress,1,mem_pool);
	ASSERT_EQ(*(uint64*)long_query_ret->data,8888);
	long_query_ret = filter_compress_query(filter_compress,2,mem_pool);
	ASSERT_EQ(*(uint64*)long_query_ret->data,2);
	long_query_ret = filter_compress_query(filter_compress,3,mem_pool);
	ASSERT_EQ(*(uint64*)long_query_ret->data,3);
	long_query_ret = filter_compress_query(filter_compress,4,mem_pool);
	ASSERT_EQ(*(uint64*)long_query_ret->data,4);

	filter_compress_destroy(filter_compress);
	filter_compress = NULL;
	filter_index_destroy(filter_index);
	filter_index = NULL;

	//测试所有值都不一样
	filter_index = filter_index_init(&config,mem_pool);
	for(i=0;i<config.row_limit;i++)
	{
		*(uint64*)long_data.data = i;
		filter_index_insert(filter_index,&long_data,i);
	}

	filter_compress = filter_compress_load(filter_index->storage,8,mem_pool);
	
	ASSERT_EQ(filter_compress,(struct filter_compress_manager *)0x1);
	filter_index_destroy(filter_index);
	filter_index = NULL;

	//测试有一个空值的情况
	filter_index = filter_index_init(&config,mem_pool);
	long_data.len = 0;
	filter_index_insert(filter_index,&long_data,0);
	long_data.len = 8;
	for(i=1;i<config.row_limit;i++)
	{
		*(uint64*)long_data.data = i;
		filter_index_insert(filter_index,&long_data,i);
	}

	filter_compress = filter_compress_load(filter_index->storage,8,mem_pool);

	long_query_ret =  filter_compress_query(filter_compress,0,mem_pool);
	ASSERT_EQ(long_query_ret->len,0);
	for(i=1;i<config.row_limit;i++)
	{
		long_query_ret =  filter_compress_query(filter_compress,i,mem_pool);
		ASSERT_EQ(*(uint64*)long_query_ret->data,i);
	}

	filter_compress_destroy(filter_compress);
	filter_compress = NULL;
	filter_index_destroy(filter_index);
	filter_index = NULL;

	//测试所有都是空值的情况
	filter_index = filter_index_init(&config,mem_pool);
	long_data.len = 0;
	for(i=1;i<config.row_limit;i++)
	{
		filter_index_insert(filter_index,&long_data,i);
	}

	filter_compress = filter_compress_load(filter_index->storage,8,mem_pool);
	for(i=0;i<config.row_limit;i++)
	{
		long_query_ret =  filter_compress_query(filter_compress,i,mem_pool);
		ASSERT_EQ(long_query_ret->len,0);
	}

	filter_compress_destroy(filter_compress);
	filter_compress = NULL;
	filter_index_destroy(filter_index);
	filter_index = NULL;


	/*~~~~~~~~~~~~~测试LONG型~~~~~~~~~~~~~~~~`*/
	struct low_data_struct short_data;
	struct low_data_struct* short_query_ret = NULL;
	filter_index = filter_index_init(&config,mem_pool);
	get_low_data(&short_data,HI_TYPE_LONG,mem_pool);

	short_data.len = 0;
	filter_index_insert(filter_index,&short_data,0);
	short_data.len = 4;
	for(i=1;i<config.row_limit;i++)
	{
		*(uint32*)short_data.data = i;
		filter_index_insert(filter_index,&short_data,i);
	}

	filter_compress = filter_compress_load(filter_index->storage,4,mem_pool);
	short_query_ret =  filter_compress_query(filter_compress,0,mem_pool);
	ASSERT_EQ(short_query_ret->len,0);
	for(i=1;i<config.row_limit;i++)
	{
		short_query_ret =  filter_compress_query(filter_compress,i,mem_pool);
		ASSERT_EQ(*(uint32*)short_query_ret->data,i);
	}

	filter_compress_destroy(filter_compress);
	filter_compress = NULL;
	filter_index_destroy(filter_index);
	filter_index = NULL;
}




TEST(FILTERCOMPRESS_TEST, HandleNoneZeroInput)  {
	system("rm -rf /tmp/filter_test");
	MEM_POOL_PTR mem_pool = mem_pool_init(M_1M);
    init_profile(1000, mem_pool);

	test_filter_compress1(10000, 4);

	test_filter_compress2();

	mem_pool_destroy(mem_pool);
}

