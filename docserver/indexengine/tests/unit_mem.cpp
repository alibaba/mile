/*
 * =====================================================================================
 *
 *       Filename:  mem.cpp
 *
 *    Description:  test memory pool
 *
 *        Version:  1.0
 *        Created:  2011/02/18 10Ê±18·Ö36Ãë
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  liang.chenl, 
 *        Company:  
 *
 * =====================================================================================
 */

#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
}
TEST(MemTestNomal, HandleNoneZeroInput)  {
    MEM_POOL_PTR pMemPool = mem_pool_init(M_1M);     
    void* pAddr = mem_pool_malloc(pMemPool, 512);     
    EXPECT_EQ(pAddr, pMemPool->address); 
    pAddr = mem_pool_malloc(pMemPool, M_1M);     
    EXPECT_EQ(pAddr, pMemPool->address); 
    mem_pool_destroy(pMemPool);
}

