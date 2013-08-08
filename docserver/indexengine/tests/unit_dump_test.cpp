#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/storage/db.h"
#include "../src/storage/config_parser.h"
}



void db_initialize(MEM_POOL* mem_pool)
{
	struct db_conf* config = config_parser("docserver.conf",mem_pool);
	g_running_flag = 1;
	config->row_limit = 10;
	ASSERT_EQ(MILE_RETURN_SUCCESS,db_init(config));
}


//简单测试
void test_single_dump(MEM_POOL* mem_pool)
{
	system("rm -rf /tmp/binlog");
	system("rm -rf /tmp/log");
	system("rm -rf /tmp/storage");
	
	db_initialize(mem_pool);

	//构造数据
    uint16 sid = 0;
    uint32 docid = 0;
    int32 ret;
    
    uint16 field_count = 5;
    enum field_types types[5] = {HI_TYPE_STRING,HI_TYPE_LONG,HI_TYPE_TINY,HI_TYPE_LONGLONG,HI_TYPE_DOUBLE};

	//将第一个段填满
	while(sid == 0)
	{
	    ret = db_insert("hello", &sid, &docid,get_row_data(field_count,types,mem_pool), DOCID_BY_SELF, mem_pool);
	    ASSERT_EQ(MILE_RETURN_SUCCESS,ret);
	}

	sid = 0;
	docid = 10;
	
	//验证是否可查询
	uint32 i;
	for(i=0; i<docid; i++)
		ASSERT_EQ(1,verify_row_data(db_data_query_row("hello",sid,i,mem_pool),field_count,types));

	ret = db_unload_segment("hello",sid,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	//验证是否可查询
	for(i=0; i<docid; i++)
		ASSERT_EQ(0,verify_row_data(db_data_query_row("hello",sid,i,mem_pool),field_count,types));

	char segment_file_name[FILENAME_MAX_LENGTH];
	memset(segment_file_name,0,sizeof(segment_file_name));

	sprintf(segment_file_name,"%s/hello/hello_segment_000000_dump",get_db()->work_space);

	ret = db_load_segment("hello",sid,segment_file_name,mem_pool);

	//验证是否可查询
	for(i=0; i<docid; i++)
		ASSERT_EQ(1,verify_row_data(db_data_query_row("hello",sid,i,mem_pool),field_count,types));

	db_release();
}


void test_with_index_dump(MEM_POOL* mem_pool)
{
	system("rm -rf /tmp/binlog");
	system("rm -rf /tmp/log");
	system("rm -rf /tmp/storage");
	
	db_initialize(mem_pool);

	//构造数据
    uint16 sid = 0;
    uint32 docid = 0;
    int32 ret;
    
    uint16 field_count = 5;
    enum field_types types[5] = {HI_TYPE_STRING,HI_TYPE_LONG,HI_TYPE_TINY,HI_TYPE_LONGLONG,HI_TYPE_DOUBLE};

	//将第一个段填满
	while(sid == 0)
	{
	    ret = db_insert("hello", &sid, &docid,get_row_data(field_count,types,mem_pool), DOCID_BY_SELF, mem_pool);
	    ASSERT_EQ(MILE_RETURN_SUCCESS,ret);
	}

	sid = 0;

	//建hash索引，然后unload load 验证
	ret = db_ensure_index("hello","HI_TYPE_STRING",HI_KEY_ALG_HASH,HI_TYPE_STRING, mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);
	
	struct low_data_struct* data = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
	get_low_data(data,HI_TYPE_STRING,mem_pool);

	struct list_head* index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),data,mem_pool);

	struct segment_query_rowids* node;
	list_for_each_entry(node,index_equal_ret,rowids_list){
	        if(node->sid == 0)
		  	 ASSERT_EQ(node->rowids->rowid_num,10);
		   else if(node->sid == 1)
		   	 ASSERT_EQ(node->rowids->rowid_num,1);
		   else
		   	 FAIL();
	}  

	ret = db_unload_segment("hello",sid,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),data,mem_pool);
	list_for_each_entry(node,index_equal_ret,rowids_list){
		   	 ASSERT_EQ(node->rowids->rowid_num,1);
			 ASSERT_EQ(node->sid,1);
	} 

	
	char segment_file_name[FILENAME_MAX_LENGTH];
	memset(segment_file_name,0,sizeof(segment_file_name));

	sprintf(segment_file_name,"%s/hello/hello_segment_000000_dump",get_db()->work_space);

	ret = db_load_segment("hello",sid,segment_file_name,mem_pool);

	index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),data,mem_pool);

	list_for_each_entry(node,index_equal_ret,rowids_list){
           if(node->sid == 0)
		  	 ASSERT_EQ(node->rowids->rowid_num,10);
		   else if(node->sid == 1)
		   	 ASSERT_EQ(node->rowids->rowid_num,1);
		   else
		   	FAIL();
	}  

	//在当前段多建1个filter索引，然后load，验证加载的段是否会添加新索引
	ret = db_unload_segment("hello",sid,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	//再建filter索引
	ret = db_ensure_index("hello","HI_TYPE_LONG",HI_KEY_ALG_FILTER,HI_TYPE_LONG,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	ret = db_load_segment("hello",sid,segment_file_name,mem_pool);

	index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),data,mem_pool);

	list_for_each_entry(node,index_equal_ret,rowids_list){
            if(node->sid == 0)
		  	 ASSERT_EQ(node->rowids->rowid_num,10);
		   else if(node->sid == 1)
		   	 ASSERT_EQ(node->rowids->rowid_num,1);
		   else
		   	FAIL();
	}  

	struct low_data_struct* index_value_ret = db_index_value_query("hello",sid,"HI_TYPE_LONG",docid,mem_pool);
	ASSERT_EQ(*(uint32*)index_value_ret->data,6666);
	
	//在当前段多建2个filter索引，然后load，验证加载的段是否会添加新索引
	ret = db_unload_segment("hello",sid,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);
	
	ret = db_ensure_index("hello","HI_TYPE_STRING",HI_KEY_ALG_FILTER,HI_TYPE_STRING,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	ret = db_ensure_index("hello","HI_TYPE_TINY",HI_KEY_ALG_FILTER, HI_TYPE_TINY,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	ret = db_load_segment("hello",sid,segment_file_name,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

	index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),data,mem_pool);

	list_for_each_entry(node,index_equal_ret,rowids_list){
           if(node->sid == 0)
		  	 ASSERT_EQ(node->rowids->rowid_num,10);
		   else if(node->sid == 1)
		   	 ASSERT_EQ(node->rowids->rowid_num,1);
		   else
		   	FAIL();
	}  

	index_value_ret = db_index_value_query("hello",sid,"HI_TYPE_STRING",docid,mem_pool);
	ASSERT_EQ(*(uint64*)index_value_ret->data,get_hash_value(data));

	index_value_ret = db_index_value_query("hello",sid,"HI_TYPE_TINY",docid,mem_pool);
	ASSERT_EQ(*(int8*)(index_value_ret->data),1);

	db_release();
}


TEST(DUMP_TEST, HandleNoneZeroInput){
	 MEM_POOL* mem_pool = mem_pool_init(M_1M);

  	//初始化DB
  	init_profile(1000,mem_pool);
  
	test_single_dump(mem_pool);
	test_with_index_dump(mem_pool);

	mem_pool_destroy(mem_pool);
}




