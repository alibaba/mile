#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/storage/filter_index.h"
}


TEST(FILTER_INDEX_TEST, HandleNoneZeroInput)  {
    int32 ret;
    MEM_POOL_PTR mem_pool = mem_pool_init(M_1M);
	
	system("rm -rf /tmp/filter_test");
    
    struct filter_index_config config;
    char dir_path[] = "/tmp/filter_test";
    config.unit_size = 8;
    config.row_limit = 100;
	strcpy(config.work_space,dir_path);
    config.type = HI_TYPE_LONGLONG;

	mkdirs(dir_path);
	
    init_profile(1000,mem_pool);
	
   //定长 
   struct filter_index_manager* filter_index = filter_index_init(&config,mem_pool);
   uint32 docid = 0;
   
   //插入一个值
   struct low_data_struct insert_data;
   struct low_data_struct* old_data;

   get_low_data(&insert_data,HI_TYPE_LONGLONG,mem_pool);
   
   ret = filter_index_insert(filter_index,&insert_data,docid);
   ASSERT_EQ(ret,0);
   
   //查询一个值
   struct low_data_struct* query_data = NULL;
   query_data = filter_index_query(filter_index,docid,mem_pool);
   ASSERT_EQ(*((uint64*)query_data->data),8888);
   ASSERT_EQ(query_data->len,8);
   
   //更新一个值
   uint64 value = 6666;
   insert_data.data= &value;
   ret = filter_index_update(filter_index,&insert_data,&old_data,docid,mem_pool);
   ASSERT_EQ(ret,0);
   
   query_data = filter_index_query(filter_index,0,mem_pool);
   ASSERT_EQ(*((uint64*)query_data->data),6666);
   ASSERT_EQ(query_data->len,8);


   struct low_data_struct null_data;
   null_data.len = 0;
   null_data.data = NULL;

   docid = 1;
   
   //插入一个空值，更新一个非空值
   ret = filter_index_insert(filter_index,&null_data,docid);
   ASSERT_EQ(ret,0);

   query_data = filter_index_query(filter_index,docid,mem_pool);
   ASSERT_EQ(query_data->len,0);

   ret = filter_index_update(filter_index,&insert_data,&old_data,docid,mem_pool);
   ASSERT_EQ(ret,0);
   
   //验证原始值
   ASSERT_EQ(old_data->len,0);
   
   //查询
   query_data = filter_index_query(filter_index,1,mem_pool);
   query_data = filter_index_query(filter_index,0,mem_pool);
   ASSERT_EQ(*((uint64*)query_data->data),6666);
   ASSERT_EQ(query_data->len,8);

   //插入一个非空值，更新一个空值
   docid = 2;
  
   //插入一个值
   ret = filter_index_insert(filter_index,&insert_data,docid);
   ASSERT_EQ(ret,0);

   //更新值
   ret = filter_index_update(filter_index,&null_data,&old_data,docid,mem_pool);
   ASSERT_EQ(ret,0);
   ASSERT_EQ(*((uint64*)old_data->data),6666);
   ASSERT_EQ(old_data->len,8);

   query_data = filter_index_query(filter_index,docid,mem_pool);
   ASSERT_EQ(query_data->len,0);

   //批量插入，并查询
   for(uint32 i=docid+1;i<config.row_limit;i++)
  { 
       insert_data.data = (void*)&i;
       ret = filter_index_insert(filter_index,&insert_data,i);
       ASSERT_EQ(ret,0);
    
       query_data = filter_index_query(filter_index,i,mem_pool);
       ASSERT_EQ(*((uint64*)query_data->data),i);
  }

   
 filter_index_release(filter_index);
 filter_index = NULL;


 //不定长验证
 system("rm -rf /tmp/filter_test");
 
 mkdirs(dir_path);

 //不定长
 config.type = HI_TYPE_STRING;
 init_profile(1000,mem_pool);
 filter_index = filter_index_init(&config,mem_pool);  
 
 get_low_data(&insert_data,HI_TYPE_STRING,mem_pool);
 
 ret = filter_index_insert(filter_index,&insert_data,0);
 ASSERT_EQ(ret,0);

 //查询
 query_data = filter_index_query(filter_index,0,mem_pool);
 ASSERT_STREQ((char*)query_data->data,"ali");
 ASSERT_EQ(query_data->len,5);

 //更新空值
 insert_data.data = NULL;
 insert_data.len = 0;
 ret = filter_index_update(filter_index,&insert_data,&old_data,0,mem_pool);
 ASSERT_EQ(ret,0);
 query_data = filter_index_query(filter_index,0,mem_pool);
 ASSERT_EQ(query_data->len,0);

 filter_index_release(filter_index);


 mem_pool_destroy(mem_pool);
}

