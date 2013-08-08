#include "../../../common/def.h"
#include <gtest/gtest.h>
#include "../../../common/mem.h"
#include "../dynamic_hash_index.h"

struct dyhash_index_manager* init(struct dyhash_index_config* config,char * dir_path){
	char cmd[1024];
	memset(cmd, 0, sizeof(cmd));
	sprintf(cmd, "rm -rf %s", dir_path);
	system(cmd);
	
	mkdirs(dir_path);

	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);
    init_profile(1000,mem_pool);
	return dyhash_index_init(config,mem_pool);
}



TEST(DYHASH_RECOVER1, dyhashindex){
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct dyhash_index_config config;
	
	char dir_path[] = "/tmp/dyhash_index4";

	config.is_full = 0;
	config.row_limit = 8;
	strcpy(config.work_space, dir_path);
	
	
	struct dyhash_index_manager* dyhash_index = init(&config,dir_path);
	
	struct low_data_struct data;
	data.data = mem_pool_malloc(mem_pool, strlen("hello1world"));
	memset(data.data, 0, strlen("hello1world"));
	data.len = strlen("hello world");
	data.type = HI_TYPE_STRING;

	int32_t ret = 0;
	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,0);
	ASSERT_EQ(0, ret);
	
	
	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,1);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,2);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,3);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello2world");
    ret = dyhash_index_insert(dyhash_index,&data,4);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello2world");
    ret = dyhash_index_insert(dyhash_index,&data,5);
	ASSERT_EQ(0, ret);

	dyhash_index_release(dyhash_index);
}



TEST(DYHASH_RECOVER2, dyhashindex){
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);
	struct dyhash_index_config config;
	
	char dir_path[] = "/tmp/dyhash_index4";

	config.is_full = 1;
	config.row_limit = 8;
	strcpy(config.work_space, dir_path);
	
	struct low_data_struct data;
	data.data = malloc(strlen("hello1world") + 1);
	memset(data.data, 0, strlen("hello1world") + 1);
	data.len = strlen("hello1world");
	data.type = HI_TYPE_STRING;
	
	struct dyhash_index_manager* dyhash_index = dyhash_index_init(&config, mem_pool);
	int32_t ret;

	dyhash_index_recover(dyhash_index, 3);

	strcpy((char*)data.data, "hello1world");
	uint32_t count = dyhash_index_count_query(dyhash_index, &data, mem_pool);
	ASSERT_EQ(3, count);

	strcpy((char*)data.data, "hello2world");
	count = dyhash_index_count_query(dyhash_index, &data, mem_pool);
	ASSERT_EQ(0, count);

	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,3);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello2world");
    ret = dyhash_index_insert(dyhash_index,&data,4);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello2world");
    ret = dyhash_index_insert(dyhash_index,&data,5);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello1world");
	count = dyhash_index_count_query(dyhash_index, &data, mem_pool);
	ASSERT_EQ(4, count);

	strcpy((char*)data.data, "hello2world");
	count = dyhash_index_count_query(dyhash_index, &data, mem_pool);
	ASSERT_EQ(2, count);
	
}

TEST(DYHASH_CHECKPOINT1, dyhashindex){
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct dyhash_index_config config;
	
	char dir_path[] = "/tmp/dyhash_index3";

	config.is_full = 0;
	config.row_limit = 2;
	strcpy(config.work_space, dir_path);
	
	
	struct dyhash_index_manager* dyhash_index = init(&config,dir_path);

	int32_t ret = 0;
	uint32_t docid = 0;
	
	struct low_data_struct data;
	data.data = mem_pool_malloc(mem_pool, strlen("hello world"));
	memset(data.data, 0, strlen("hello world"));
	strcpy((char*)data.data, "hello world");
	data.len = strlen("hello world");
	data.type = HI_TYPE_STRING;

	uint32_t i;
	for(i=0; i<10; i++)
	{
		ret = dyhash_index_insert(dyhash_index,&data,docid);
		ASSERT_EQ(0, ret);

	}
	
	dyhash_index_checkpoint(dyhash_index);


	struct rowid_list* query_ret = dyhash_index_query(dyhash_index, &data, mem_pool);
	print_rowid_list(query_ret);
}


TEST(DYHASH_CHECKPOINT2, dyhashindex){
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct dyhash_index_config config;
	
	char dir_path[] = "/tmp/dyhash_index3";

	config.is_full = 0;
	config.row_limit = 2;
	config.is_full = 1;
	strcpy(config.work_space, dir_path);
	

    init_profile(1000,mem_pool);
	
	struct dyhash_index_manager* dyhash_index = dyhash_index_init(&config,mem_pool);

	struct low_data_struct data;
	data.data = mem_pool_malloc(mem_pool, strlen("hello world"));
	memset(data.data, 0, strlen("hello world"));
	strcpy((char*)data.data, "hello world");
	data.len = strlen("hello world");
	data.type = HI_TYPE_STRING;


	uint32_t count = dyhash_index_count_query(dyhash_index, &data, mem_pool);
	ASSERT_EQ(1, count);

	
	struct rowid_list* query_ret = dyhash_index_query(dyhash_index, &data, mem_pool);
	print_rowid_list(query_ret);
}



//²âÊÔhash³åÍ»
TEST(DYHASH_INDEX_TEST1, dyhashindex){
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct dyhash_index_config config;
	
	char dir_path[] = "/tmp/dyhash_index1";

	config.is_full = 0;
	config.row_limit = 2;
	strcpy(config.work_space, dir_path);
	
	
	struct dyhash_index_manager* dyhash_index = init(&config,dir_path);
	
	struct low_data_struct data;
	data.data = mem_pool_malloc(mem_pool, strlen("hello1world"));
	memset(data.data, 0, strlen("hello1world"));
	data.len = strlen("hello world");
	data.type = HI_TYPE_STRING;

	int32_t ret = 0;
	uint32_t docid = 0;
	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);
	
	
	docid++;
	strcpy((char*)data.data, "hello2world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	docid++;
	strcpy((char*)data.data, "hello3world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	docid++;
	strcpy((char*)data.data, "hello4world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);
	
	
	docid++;
	strcpy((char*)data.data, "hello5world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	
	docid++;
	strcpy((char*)data.data, "hello6world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	
	docid++;
	strcpy((char*)data.data, "hello7world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	docid++;
	strcpy((char*)data.data, "hello8world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	docid++;
	strcpy((char*)data.data, "hello9world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);
}


TEST(DYHASH_INDEX_TEST2, dyhashindex)  {
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct dyhash_index_config config;
	
	char dir_path[] = "/tmp/dyhash_index2";

	config.is_full = 0;
	config.row_limit = 2;
	strcpy(config.work_space, dir_path);
	
	
	struct dyhash_index_manager* dyhash_index = init(&config,dir_path);

	int32_t ret = 0;
	uint32_t docid = 0;
	
	struct low_data_struct data;
	data.data = mem_pool_malloc(mem_pool, strlen("hello world"));
	memset(data.data, 0, strlen("hello world"));
	strcpy((char*)data.data, "hello world");
	data.len = strlen("hello world");
	data.type = HI_TYPE_STRING;
	
	strcpy((char*)data.data, "hello1world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);
	
	
	strcpy((char*)data.data, "hello2world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello3world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello4world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);
	
	
	strcpy((char*)data.data, "hello5world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	
	strcpy((char*)data.data, "hello6world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	
	strcpy((char*)data.data, "hello7world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello8world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);

	strcpy((char*)data.data, "hello9world");
    ret = dyhash_index_insert(dyhash_index,&data,docid);
	ASSERT_EQ(0, ret);
	
	uint32_t count = dyhash_index_count_query(dyhash_index, &data, mem_pool);
	ASSERT_EQ(1,count);
}

int main(int argc, char** argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
