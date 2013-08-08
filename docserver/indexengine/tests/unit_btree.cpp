/*
 * =====================================================================================
 *
 *       Filename:  btreeTest.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  2011年03月10日 11时46分36秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  shuai.li (algol), shuai.li@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */

#include "def.h"
extern "C"
{
#include "../src/include/hi_block.h"
#include "../../indexengine/src/include/hi_btree.h"
#include "../../indexengine/src/include/mem.h"
}

#include "time.h"
#define MAX 100000

TEST(BTREETEST, HandleNoneZeroInput)  {
	MEM_POOL_PTR pMemPool = mem_pool_init(M_1M); 
	BTREE_PTR pBtree = btree_init(pMemPool, "zbtree_index", "/tmp/", 3*MAX, HI_TYPE_LONGLONG);
//	BTREE_PTR pBtree = btree_init(pMemPool, "zbtree_index", "/tmp/", 3*MAX, HI_TYPE_LONG);
	BTREE_PTR pBtree2 = btree_init(pMemPool, "zbtree_index2", "/tmp/", 3*MAX, HI_TYPE_DOUBLE);

	init_profile(1000, pMemPool);
	/*
	struct timeval start, end, end2;               
	double interval; 
	gettimeofday(&start, NULL);

	srand((unsigned int)time(NULL)); 
	int res,i;
	uint64 num;
	//插入10万数据
	for ( i = 0; i < MAX; i += 1 )
	{
		num = rand()%MAX;
		res = btree_insert(pMemPool, pBtree, num, i);
		ASSERT_EQ(res, 0);
	}
	gettimeofday(&end, NULL);
	interval = 1000000*(end.tv_sec - start.tv_sec) + (end.tv_usec - start.tv_usec);
	printf("insert %d data, cost %.2f ms\n", MAX, interval /1000);

	//范围查询10万次
	BTREE_ENTRY_PTR pBtreeEntry;
	uint32 num2;
//	struct rowid_list *pRowidList;
	for ( i = 0; i < MAX; i += 1 )
	{
		num = rand()%MAX+100;
		pBtreeEntry = btree_search(pBtree, num, CT_LE);
//		num2 = rand()%MAX+100;
//		struct rowid_list * pRowidList = btree_range_query(pBtree, num, 0, num2, 0, pMemPool);
//		if(pRowidList!=NULL)
//		{
//			printf("%d:", i);
//			print_rowid_list(pRowidList);
//		}
		//		ASSERT_EQ((pBtreeEntry != NULL), 1);
	}
	gettimeofday(&end2, NULL);
	interval = 1000000*(end2.tv_sec - end.tv_sec) + (end2.tv_usec - end.tv_usec);
	printf("search %d data, cost %.2f ms\n", MAX, interval /1000);
	*/
	
	//插入3 7 7 9 11 2 4 5 6 8
	int res = btree_insert(pMemPool, pBtree, 3, 1);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 7, 2);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 7, 10);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 9, 3);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 11, 4);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 2, 5);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 4, 6);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 5, 7);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 6, 8);
	ASSERT_EQ(res, 0);
	res = btree_insert(pMemPool, pBtree, 8, 9);
	ASSERT_EQ(res, 0);

	//等值查找3 9 8
//	long long value = 3;
	BTREE_ENTRY_PTR pBtreeEntry = btree_search(pBtree, 3, CT_EQ);
	ASSERT_EQ((pBtreeEntry != NULL), 1);
	ASSERT_EQ(pBtreeEntry->key, 3);
	pBtreeEntry = btree_search(pBtree, 9, CT_EQ);
	ASSERT_EQ((pBtreeEntry != NULL), 1);
	ASSERT_EQ(pBtreeEntry->key, 9);
	pBtreeEntry = btree_search(pBtree, 1, CT_EQ);
	ASSERT_EQ((pBtreeEntry == NULL), 1);

	//查找<7 <6 
	pBtreeEntry = btree_search(pBtree, 7, CT_LT);
	ASSERT_EQ((pBtreeEntry != NULL), 1);
	ASSERT_EQ(pBtreeEntry->key, 6);
	pBtreeEntry = btree_search(pBtree, 6, CT_LT);
	ASSERT_EQ((pBtreeEntry != NULL), 1);
	ASSERT_EQ(pBtreeEntry->key, 5);

	//查找>7 >6 
	pBtreeEntry = btree_search(pBtree, 7, CT_GT);
	ASSERT_EQ((pBtreeEntry != NULL), 1);
	ASSERT_EQ(pBtreeEntry->key, 8);
	pBtreeEntry = btree_search(pBtree, 6, CT_GT);
	ASSERT_EQ((pBtreeEntry != NULL), 1);
	ASSERT_EQ(pBtreeEntry->key, 7);

	//测试btree next
	//BTREE_ENTRY_PTR pEntry = btree_search(pBtree, 2, 0);
	BTREE_ENTRY_PTR pEntry = btree_get_start_entry(pBtree);
	struct doc_row_unit *pUnit;
	while(pEntry != NULL)
	{
		std::cout << "key = " << pEntry->key << " docID: ";
		//输出doclist
		pUnit = doclist_next(pBtree->pDoclist, pEntry->doclist);
		while(pUnit != NULL)
		{
			std::cout << pUnit->doc_id << " ";
			pUnit = doclist_next(pBtree->pDoclist, pUnit->next);
		}
		pEntry = btree_next(pBtree, pEntry);
		std::cout << std::endl;
	}
	std::cout << std::endl;

	struct rowid_list * pRowidList = btree_range_query(pBtree, 0, 0, 10, 0, pMemPool);
	print_rowid_list(pRowidList);

	BTREE_KEY key;
	double value;
	//insert 3.1 11.11 111.111 1111.1 11111.1 11.11
	value =3.1;
	memcpy(&key, &value, 8);
	res = btree_insert(pMemPool, pBtree2, key, 1);
	ASSERT_EQ(res, 0);

	value =11.11;
	memcpy(&key, &value, 8);
	res = btree_insert(pMemPool, pBtree2, key, 2);
	ASSERT_EQ(res, 0);

	value =111.111;
	memcpy(&key, &value, 8);
	res = btree_insert(pMemPool, pBtree2, key, 3);
	ASSERT_EQ(res, 0);

	value =1111.1;
	memcpy(&key, &value, 8);
	res = btree_insert(pMemPool, pBtree2, key, 4);
	ASSERT_EQ(res, 0);

	value =11111.1;
	memcpy(&key, &value, 8);
	res = btree_insert(pMemPool, pBtree2, key, 5);
	ASSERT_EQ(res, 0);

	value =11.11;
	memcpy(&key, &value, 8);
	res = btree_insert(pMemPool, pBtree2, key, 6);
	ASSERT_EQ(res, 0);

	value =1111;
	memcpy(&key, &value, 8);
	pRowidList = btree_range_query(pBtree2, 0, 0, key, 0, pMemPool);
	print_rowid_list(pRowidList);

	pEntry = btree_get_start_entry(pBtree2);
	while(pEntry != NULL)
	{
		std::cout << "key = " << *(double*)&pEntry->key << " docID: ";
		//输出doclist
		pUnit = doclist_next(pBtree2->pDoclist, pEntry->doclist);
		while(pUnit != NULL)
		{
			std::cout << pUnit->doc_id << " ";
			pUnit = doclist_next(pBtree2->pDoclist, pUnit->next);
		}
		pEntry = btree_next(pBtree2, pEntry);
		std::cout << std::endl;
	}
	std::cout << std::endl;
	//测试dump
	//btree_dump(pBtree);
	btree_destroy(pBtree);
	btree_destroy(pBtree2);
}

