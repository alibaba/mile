/*
 * =====================================================================================
 *
 *       Filename:  hi_mem.c
 *
 *    Description:  simple memroy manager
 *
 *        Version:  1.0
 *        Created:  2011/02/16 16时44分01秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  liang.chenl 
 *        Company:  
 *
 * =====================================================================================
 */

#include "mem.h"

#ifndef VALGRIND_MEM_TEST


MEM_POOL_PTR mem_pool_init(size_t size) {
    MEM_POOL_PTR pMemPool = (MEM_POOL_PTR)malloc(sizeof(MEM_POOL));
    pMemPool->address = malloc(size);
    pMemPool->size = size;
    pMemPool->pos = 0;
    pMemPool->next = NULL;
	pMemPool->thread_safe = 0;

	pthread_mutex_init(&pMemPool->thread_safe_locker,NULL);
    return pMemPool;
}

void* mem_pool_malloc(MEM_POOL_PTR pMemPool, size_t size) {
    void* pAddr = NULL;

	if(pMemPool->thread_safe)
		pthread_mutex_lock(&pMemPool->thread_safe_locker);
    //assert(size <= pMemPool->size);
	if(size > pMemPool->size)
	{
		log_error("size ",size," exceeds in malloc");
		return NULL;
	}
    if (size + pMemPool->pos > pMemPool->size) {
        //扩展内存, 将链表形成
        MEM_POOL_PTR pNewPool = mem_pool_init(pMemPool->size);
        void* pOldAddress = pMemPool->address;
        pMemPool->address = pNewPool->address;
        pMemPool->pos = 0;
		pNewPool->next = pMemPool->next;
        pMemPool->next = pNewPool; 
        pNewPool->address = pOldAddress;
    }
    pAddr = (char*)pMemPool->address + pMemPool->pos;
    pMemPool->pos +=  size;

	if(pMemPool->thread_safe)
		pthread_mutex_unlock(&pMemPool->thread_safe_locker);
    return pAddr;
}

void mem_pool_reset(MEM_POOL_PTR pMemPool) {
	if(pMemPool->thread_safe)
		pthread_mutex_lock(&pMemPool->thread_safe_locker);
	
    pMemPool->pos = 0;
    MEM_POOL_PTR pCurPool = pMemPool->next;
    while (pCurPool != NULL) {
        free(pCurPool->address);
        MEM_POOL_PTR tmp = pCurPool; 
        pCurPool = pCurPool->next;
        free(tmp);
    }
	
    pMemPool->next = NULL;

	if(pMemPool->thread_safe)
		pthread_mutex_unlock(&pMemPool->thread_safe_locker);
}

void mem_pool_set_threadsafe(MEM_POOL_PTR pMemPool)
{
	pMemPool->thread_safe = 1;
}


void mem_pool_destroy(MEM_POOL_PTR pMemPool) {
    mem_pool_reset(pMemPool);
	pthread_mutex_destroy(&pMemPool->thread_safe_locker);
    free(pMemPool->address);
    free(pMemPool);
}

#else

MEM_POOL_PTR mem_pool_init(size_t size)
{
    MEM_POOL_PTR pMemPool = (MEM_POOL_PTR)malloc(sizeof(MEM_POOL));
    pMemPool->size = size;
	INIT_LIST_HEAD( &pMemPool->mem_list_h );
    return pMemPool;
}

void* mem_pool_malloc(MEM_POOL_PTR pMemPool, size_t size)
{
	//assert( size < pMemPool->size );
	if(size > pMemPool->size)
        {
                log_error("size ",size," exceeds in malloc");
                return NULL;
        }

	void *p = malloc( size );
	struct mem_ptr *mp = (struct mem_ptr *)malloc( sizeof( struct mem_ptr ) );
	mp->ptr = p;
	list_add( &mp->mem_list, &pMemPool->mem_list_h );
	return  p;
}

void mem_pool_reset(MEM_POOL_PTR pMemPool)
{
	struct mem_ptr *mp = NULL;
	struct mem_ptr tmp;
	list_for_each_entry( mp, &pMemPool->mem_list_h, mem_list ) {
		free( mp->ptr );
		tmp.mem_list.next = mp->mem_list.next;
		list_del( &mp->mem_list );
		free( mp );
		mp = &tmp;
	}
}

void mem_pool_destroy(MEM_POOL_PTR pMemPool)
{
	mem_pool_reset( pMemPool );
	free( pMemPool );
}

#endif // VALGRIND_MEM_TEST

