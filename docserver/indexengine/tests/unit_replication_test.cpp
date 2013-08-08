#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/common/log.h"
#include "../src/storage/db.h"
#include "../src/storage/config_parser.h"
}

void db_initialize(MEM_POOL* mem_pool)
{
	struct db_conf* config = config_parser("docserver.conf",mem_pool);
	g_running_flag = 1;
	config->binlog_maxsize = 64*MB_SIZE;
	config->binlog_flag = 1;
	config->binlog_threshold = 1;
	config->row_limit = 10;
	init_log(config->log_level,config->log_dir);
	ASSERT_EQ(MILE_RETURN_SUCCESS,db_init(config));
}


TEST(REPLICATIONTEST, HandleNoneZeroInput)  {
  MEM_POOL* mem_pool = mem_pool_init(M_1M);
  
  init_profile(1000,mem_pool);

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

  //插入数据
  uint32 i;
  struct row_data* insert_row_data = get_row_data(field_count,types,mem_pool);
  for(i=0; i<1000; i++)
  {
	ret = db_insert("hello",&sid,&docid,insert_row_data,DOCID_BY_SELF,mem_pool);
	ASSERT_EQ(MILE_RETURN_SUCCESS,ret);
  }

  //删除第1个
  ret = db_del_docid("hello",sid,0,mem_pool);

  //更新第2行第1列
  struct low_data_struct new_data;
  struct low_data_struct* old_data;
  get_low_data(&new_data,HI_TYPE_LONG,mem_pool);
  memset(new_data.field_name,0,20);
  strcpy(new_data.field_name,"HI_TYPE_STRING");
  
  ret = db_update("hello",sid,1,&new_data,&old_data,mem_pool);
  ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

  //对第一列建hash索引和filter索引
  ret = db_ensure_index("hello","HI_TYPE_STRING",HI_KEY_ALG_HASH, HI_TYPE_STRING,mem_pool);
  ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

  ret = db_ensure_index("hello","HI_TYPE_LONGLONG",HI_KEY_ALG_FILTER, HI_TYPE_LONGLONG,mem_pool);
  ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

  struct segment_query_rowids* node;
  struct list_head* index_equal_ret;
  uint16 verify_sid = 99;
  get_low_data(&new_data,HI_TYPE_STRING,mem_pool);
  index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),&new_data,mem_pool);
  list_for_each_entry(node,index_equal_ret,rowids_list){
       ASSERT_EQ(node->sid,verify_sid);
	   if(verify_sid == 99)
	   	//删掉一个，以及改掉一个
	   	 ASSERT_EQ(8,node->rowids->rowid_num);
	   else
	     ASSERT_EQ(10,node->rowids->rowid_num);

	   verify_sid--;
  } 

  struct low_data_struct* index_value_ret = NULL;
  for(i=0; i<1000; i++)
  {
  	//有一个已经删除
  	if(i/10 == 99 && i%10 == 0)
		continue;
	index_value_ret = db_index_value_query("hello",i/10,"HI_TYPE_LONGLONG",i%10,mem_pool);
  	ASSERT_EQ(8888,*(uint64*)(index_value_ret->data));
  }


  for(i=0; i<1000; i++)
  {
  	//被更新了
  	if(i==991)
  	{
		ASSERT_EQ(0,verify_row_data(db_data_query_row("hello",i/10,i%10,mem_pool),field_count,types));
		continue;
  	}
  	ASSERT_EQ(1,verify_row_data(db_data_query_row("hello",i/10,i%10,mem_pool),field_count,types));
  }



  //保存工作目录
  char segment_dir[FILENAME_MAX_LENGTH];
  char binlog_filename[FILENAME_MAX_LENGTH];
  memset(segment_dir,0,sizeof(segment_dir));
  memset(binlog_filename,0,sizeof(binlog_filename));
  sprintf(segment_dir,"%s/hello/hello_segment_000000",get_db()->work_space);
  sprintf(binlog_filename,"%s/binlog.meta",get_db()->work_space);

  //释放db
  db_release();

  char segment_dir_bak[FILENAME_MAX_LENGTH];
  memset(segment_dir_bak,0,sizeof(segment_dir_bak));
  sprintf(segment_dir_bak,"%s_bak",segment_dir);
  ret = rename(segment_dir,segment_dir_bak);
  ASSERT_EQ(ret,0);  

  char binlog_filename_bak[FILENAME_MAX_LENGTH];
  memset(binlog_filename_bak,0,sizeof(binlog_filename_bak));
  sprintf(binlog_filename_bak,"%s_bak",binlog_filename);
  ret = rename(binlog_filename,binlog_filename_bak);
  ASSERT_EQ(ret,0); 

  db_initialize(mem_pool);

  //验证恢复结果
  //原始数据验证
  for(i=0; i<1000; i++)
  {
  	ASSERT_EQ(1,verify_row_data(db_data_query_row("hello",i/10,i%10,mem_pool),field_count,types));
  }
	

  //索引验证
  index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),&new_data,mem_pool);
  
  verify_sid = 99;
  get_low_data(&new_data,HI_TYPE_STRING,mem_pool);
  list_for_each_entry(node,index_equal_ret,rowids_list){
       ASSERT_EQ(node->sid,verify_sid);
	   if(verify_sid == 99)
	   	 ASSERT_EQ(8,node->rowids->rowid_num);
	   else
	     ASSERT_EQ(10,node->rowids->rowid_num);

	   verify_sid--;
  } 


  for(i=0; i<1000; i++)
  {
  	 if(i/10 == 99 && i%10 == 0)
		continue;
	index_value_ret = db_index_value_query("hello",i/10,"HI_TYPE_LONGLONG",i%10,mem_pool);
  	ASSERT_EQ(*(uint64*)(index_value_ret->data),8888);
  }


  db_release();

  //验证数据复制 
  system("rm -rf /tmp/binlog_bak");
  system("rm -rf /tmp/log");
  system("rm -rf /tmp/storage_replica");
  
  struct db_conf* config = config_parser("docserver.conf",mem_pool);
  g_running_flag = 1;
  config->binlog_maxsize = 64*MB_SIZE;
  config->binlog_flag = 0;
  config->binlog_threshold = 1;
  config->row_limit = 10;
  init_log(config->log_level,config->log_dir);
  
  char binlog_dir[FILENAME_MAX_LENGTH];
  memset(binlog_dir,0,sizeof(binlog_dir));
  strcpy(binlog_dir,config->binlog_dir);
  
  strcat(config->binlog_dir,"_bak");
  
  //更改db的工作目录
  strcat(config->work_space,"_replica");

  ret = db_init(config);
  ASSERT_EQ(MILE_RETURN_SUCCESS,ret);

  //开始同步
  uint64 offset;
  BL_READER_PTR binlog_reader;
  offset = db_start_catch_up();

  uint32 binlog_count;
  binlog_reader = binlog_reader_init_bytime(binlog_dir,config->binlog_maxsize,0,&binlog_count,mem_pool);

  struct binlog_record* binlog_record;
  do
  {
  	ret = binlog_read_record(binlog_reader,&binlog_record,mem_pool);
	ASSERT_EQ(db_execute_binrecord(binlog_record,mem_pool),0);
  }	
  while(ret == 1);

  sleep(15);
 
  //原始数据验证
  for(i=0; i<1000; i++)
  {
  	//被更新过
	if(i == 991)
		continue;
  	ASSERT_EQ(1,verify_row_data(db_data_query_row("hello",i/10,i%10,mem_pool),field_count,types));
  }
  
  
  //索引验证
  index_equal_ret = db_index_equal_query("hello",get_time_hint(mem_pool),&new_data,mem_pool);

  verify_sid = 99;
  list_for_each_entry(node,index_equal_ret,rowids_list){
       ASSERT_EQ(node->sid,verify_sid);
	   if(verify_sid == 99)
	   	 ASSERT_EQ(8,node->rowids->rowid_num);
	   else
	     ASSERT_EQ(10,node->rowids->rowid_num);

	   verify_sid--;
  } 

  for(i=0; i<1000; i++)
  {
  	 if(i/10 == 99 && i%10 == 0)
		continue;
	  index_value_ret = db_index_value_query("hello",i/10,"HI_TYPE_LONGLONG",i%10,mem_pool);
	  ASSERT_EQ(*(uint64*)(index_value_ret->data),8888);
  }

  db_release();

  mem_pool_destroy(mem_pool);
}
