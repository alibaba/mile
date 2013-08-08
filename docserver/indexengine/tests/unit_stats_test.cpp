#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/common/store_stat.h"
}


TEST(STORE_STAT_TEST, HandleNoneZeroInput) 
{

   MEM_POOL* mem_pool = mem_pool_init(M_1M);  
   store_stats_init(1000*10,mem_pool);

   uint64 i;
   for(i=0;i<10000;i++)
   	record_time("index_equal",1000*100);


   ASSERT_EQ(100.000000,get_avg_time_inms("index_equal"));
   ASSERT_EQ(10000,get_count("index_equal"));
   ASSERT_EQ(10000,get_total_count("index_equal"));
   sleep(2);
   ASSERT_EQ(5000,get_throughput("index_equal"));
}


