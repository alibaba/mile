#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/storage/bitmark.h"
}
TEST(BITMARK_TEST, HandleNoneZeroInput)  {
    int32 ret;
    int32 value;
    MEM_POOL* mem_pool = mem_pool_init(M_1M);  
    struct bitmark_config config; 
	strcpy(config.bitmark_name,"null");
	strcpy(config.work_space,"/tmp/bitmark_test");
	config.row_limit = 10;

	system("rm -rf /tmp/bitmark_test");
	init_profile(1000,mem_pool);
	mkdirs(config.work_space);

    struct bitmark_manager* bitmark = bitmark_init(&config, mem_pool);

    //全部置零
    bitmark_reset(bitmark);

    //置位
    //表示6行已插入该行值
    ret = bitmark_set(bitmark,6);
    ASSERT_EQ(ret,0);
    
    //查询
    value = bitmark_query(bitmark,6);
    ASSERT_EQ(value,0); 
	
    value = bitmark_query(bitmark,8);
    ASSERT_EQ(value,1);

    //删除6行的值
    ret = bitmark_clear(bitmark,6);
    ASSERT_EQ(ret,0);

    mem_pool_destroy(mem_pool);
}

