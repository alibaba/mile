/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_set_operator.h
 *
 *    Description:  对rowid集合的交集、并集、差集运算，用于hash条件计算
 *
 *        Version:  1.0
 *        Created:  2011/05/06 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yuzhong.zhao
 *        Company:  alipay
 *
 * =====================================================================================
 */

#ifndef SET_OPERATOR_H
#define SET_OPERATOR_H
#include "rowid_list.h"

struct segment_query_rowids {
	/*segment id号*/
	uint16_t sid;

	/*该段最大的docid号*/
	uint32_t max_docid;

	/*doc id list*/
	struct rowid_list * rowids;

	/*列表*/
	struct list_head rowids_list;
};

/**
 *	对所有segment求对应的rowid集合的并集，注意集合运算会改变链表中的数据
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct list_head* seg_rowid_union(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb);

/**
 *	对所有segment求对应的rowid集合的交集，注意集合运算会改变链表中的数据
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct list_head* seg_rowid_intersection(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb);

/**
 *	对所有segment求对应的rowid集合的差集，注意集合运算会改变链表中的数据
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct list_head* seg_rowid_minus(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb);



struct list_head* seg_rowid_fulltext_hence(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb);


int32_t seg_rowid_count(struct list_head* seglist);


void seg_rowid_setscore(MEM_POOL_PTR pMemPool, struct list_head* seglist,
		double score);

void print_seg_rowid_list(struct list_head* list);

/**
 *	求两个rowid集合的并集，注意集合运算会改变链表中的数据，结果集放在lista中
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct rowid_list* rowid_union(MEM_POOL_PTR pMemPool, struct rowid_list* lista,
		struct rowid_list* listb);

/**
 *	求两个rowid集合的交集，注意集合运算会改变链表中的数据，结果集放在lista中
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct rowid_list* rowid_intersection(MEM_POOL_PTR pMemPool,
		struct rowid_list* lista, struct rowid_list* listb);

/**
 *	求两个rowid集合的差集，注意集合运算会改变链表中的数据，结果集放在lista中
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct rowid_list* rowid_minus(MEM_POOL_PTR pMemPool, struct rowid_list* lista,
		struct rowid_list* listb);


struct rowid_list* rowid_fulltext_hence(MEM_POOL_PTR pMemPool,
		struct rowid_list* lista, struct rowid_list* listb);


void rowid_list_setscore(MEM_POOL_PTR mem_pool, struct rowid_list* id_list,
		double score);

#endif
