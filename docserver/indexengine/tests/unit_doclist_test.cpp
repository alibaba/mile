#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/storage/doclist.h"
}

TEST(DOCLIST_TEST, HandleNoneZeroInput)  {
   MEM_POOL* mem_pool = mem_pool_init(M_1M);  
   
   struct doclist_config config;
   
   system("rm -rf /tmp/doclist_test");
   
   char dir_path[] = "/tmp/doclist_teset";
   mkdirs(dir_path);
   
   init_log("DEBUG","/tmp/doclist_teset/mile.log");
   init_profile(1000,mem_pool);
   strcpy(config.work_space,"/tmp/doclist_teset");
   config.row_limit = 100;
   
   struct doclist_manager* doclist = doclist_init(&config,mem_pool);
   struct doc_row_unit* head = NULL;
   
   //²âÊÔ²åÈë
   int64 a = 128;
   struct low_data_struct data;
   data.data = &a;
   data.len = 8;
   uint32 offset = doclist_insert(doclist,1,0);
   head = GET_DOC_ROW_STRUCT(doclist,offset);
   ASSERT_EQ(head->doc_id,1);

   offset = doclist_insert(doclist,2,offset);
   head = GET_DOC_ROW_STRUCT(doclist,offset);
   int i = 2;
   while(head!=NULL)
   {
      ASSERT_EQ(head->doc_id,i--);
      if(head->next == 0)
		break;
      head = GET_DOC_ROW_STRUCT(doclist,head->next);
   }
   doclist_destroy(doclist);

   mem_pool_destroy(mem_pool);
}

