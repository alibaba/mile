#include "../../../common/def.h"
#include <gtest/gtest.h>
#include "../../../common/mem.h"
#include "../doclist.h"

TEST(DOCLIST_TEST, doclist)  {
   MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);  
   
   struct doclist_config config;
   
   system("rm -rf /tmp/doclist_test");
   
   char dir_path[] = "/tmp/doclist_teset";
   mkdirs(dir_path);
   
   init_profile(1000,mem_pool);
   strcpy(config.work_space,"/tmp/doclist_teset");
   config.row_limit = 100;
   
   struct doclist_manager* doclist = doclist_init(&config,mem_pool);
   struct doc_row_unit* head = NULL;
   
   //²âÊÔ²åÈë
   int64_t a = 128;
   struct low_data_struct data;
   data.data = &a;
   data.len = 8;

   uint32_t docid = 1;
   uint32_t bucket_no = 10;

   data.data = &a;
   data.len = 8;
   uint32_t offset = doclist_insert(doclist,docid,0,bucket_no);
   ASSERT_EQ(sizeof(uint32_t) + sizeof(struct doc_row_unit), offset);

   head = GET_DOC_ROW_STRUCT(doclist,docid);
   ASSERT_EQ(1, head->doc_id);
   ASSERT_EQ(0x8000000a, head->next);

   docid = 2;
   offset = doclist_insert(doclist, docid ,offset, bucket_no);
   head = GET_DOC_ROW_STRUCT(doclist,docid);
   int i = 2;
   while(head!=NULL)
   {
      ASSERT_EQ(head->doc_id,i--);
      if(head->next & 0x80000000)
		break;
      head = NEXT_DOC_ROW_STRUCT(doclist,head->next);
   }
   doclist_destroy(doclist);

   mem_pool_destroy(mem_pool);
}

int main(int argc, char** argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
