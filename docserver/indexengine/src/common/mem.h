/*
 * =====================================================================================
 *
 *       Filename:  mem.h
 *
 *    Description:  simple mem manager
 *
 *        Version:  1.0
 *        Created:  2011/02/16 16时44分36秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  liang.chenl 
 *        Company:  
 *
 * =====================================================================================
 //内存的地址*/


#ifndef MEM_H
#define MEM_H

#include "def.h"
#include "list.h"

#ifndef VALGRIND_MEM_TEST

typedef struct mem_pool {
    void* address; 
    uint32_t  size;      //内存最大大小
    int32_t pos;        //当前内存的位置
    struct mem_pool* next;    //已经用过的内存列表
    uint8_t thread_safe;
	pthread_mutex_t thread_safe_locker;
} MEM_POOL, *MEM_POOL_PTR;

#else

typedef struct mem_pool {
    uint32_t  size;      //内存最大大小
    struct list_head mem_list_h;
} MEM_POOL, *MEM_POOL_PTR;

struct mem_ptr {
	void * ptr;
	struct list_head mem_list;
};

#endif // VALGRIND_MEM_TEST

/* *
 *内存池初始化
 * @param size : 内存大小单位(byte)
 * @return : 申请的内存池指针
 * */
MEM_POOL_PTR mem_pool_init(size_t size);  
/* *
 * 将内存池设置为线程安全
 * @param 内存池
 * 
 * */
void mem_pool_set_threadsafe(MEM_POOL_PTR pMemPool);
/* *
 * 从一个内存池分配一块小内存 ,当前块不够时，内存池会扩展
 * @param size : 要分配的内存大小(byte)
 * 
 * */
void* mem_pool_malloc(MEM_POOL_PTR pMemPool, size_t size);
/* *
 * 将内存池，恢复到初始状态
 * (1) 使用位置重置0
 * (2) 释放链表的内存
 * */
void mem_pool_reset(MEM_POOL_PTR pMemPool);

/* *
 * 将内存池完全销毁
 * */
void mem_pool_destroy(MEM_POOL_PTR pMemPool);                                                                                      

#ifdef __cplusplus
// for c++ placement new
#define NEW(pool, CLASS) new(mem_pool_malloc((pool), sizeof(CLASS))) CLASS

// call destructor
template <typename T>
inline void DELETE(T *p) { p->~T(); }

#endif // __cplusplus

#endif //MEM_H
