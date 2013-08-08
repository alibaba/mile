#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/common/log.h"
#include "../src/common/profiles.h"
}
TEST(PROFILETEST, HandleNoneZeroInput)  {
  MEM_POOL_PTR pMemPool = mem_pool_init(M_1M);
  init_profile(1000,pMemPool);
  init_log((char*)"WARN",(char *)"/home/admin/mile-zian/indexengine/tests/mile.log");    
  
  PROFILER_START((char *)"test");
  PROFILER_BEGIN((char *)"test_sub1");
  PROFILER_BEGIN((char *)"test_sub2");
  PROFILER_BEGIN((char *)"test_sub3");
  PROFILER_END();
  PROFILER_END();
  PROFILER_END();
  PROFILER_DUMP();
  PROFILER_STOP(); 
  mem_pool_destroy(pMemPool);
}
