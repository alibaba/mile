/*
 * =====================================================================================
 *
 *       Filename:  hi_set_operator.c
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

#include "set_operator.h"

void print_seg_rowid_list(struct list_head* list) {
	if (list == NULL ) {
		return;
	}

	struct segment_query_rowids* qresult;

	list_for_each_entry(qresult, list, rowids_list) {
		printf("seg id: %d\n", qresult->sid);
		print_rowid_list(qresult->rowids);
	}
}

/**
 *	对所有segment求对应的rowid集合的并集，注意集合运算会改变链表中的数据
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct list_head* seg_rowid_union(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb) {
	if (lista == NULL || listb == NULL ) {
		log_error("error when processing rowid set union, the input is null!");
		return NULL ;
	}

	struct segment_query_rowids* qresult;
	struct segment_query_rowids* qresulta;
	struct segment_query_rowids* qresultb;

	struct list_head* rlist = (struct list_head*) mem_pool_malloc(pMemPool,
			sizeof(struct list_head));
	INIT_LIST_HEAD(rlist);

	double_list_for_each_entry(qresulta, lista, qresultb, listb, rowids_list) {
		if (qresulta->sid != qresultb->sid) {
			log_error(
					"error when processing rowid set union, the segid %d and %d are different!", qresulta->sid, qresultb->sid);
			return NULL ;
		}
		qresult = (struct segment_query_rowids*) mem_pool_malloc(pMemPool,
				sizeof(struct segment_query_rowids));
		memset(qresult, 0, sizeof(struct segment_query_rowids));
		qresult->sid = qresulta->sid;
		qresult->rowids = rowid_union(pMemPool, qresulta->rowids,
				qresultb->rowids);
		list_add_tail(&qresult->rowids_list, rlist);
	}

	return rlist;
}

/**
 *	对所有segment求对应的rowid集合的交集，注意集合运算会改变链表中的数据
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct list_head* seg_rowid_intersection(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb) {
	if (lista == NULL || listb == NULL ) {
		log_error(
				"error when processing rowid set intersection, the input is null!");
		return NULL ;
	}

	struct segment_query_rowids* qresult;
	struct segment_query_rowids* qresulta;
	struct segment_query_rowids* qresultb;
	struct list_head* rlist = (struct list_head*) mem_pool_malloc(pMemPool,
			sizeof(struct list_head));
	INIT_LIST_HEAD(rlist);

	double_list_for_each_entry(qresulta, lista, qresultb, listb, rowids_list) {
		if (qresulta->sid != qresultb->sid) {
			log_error(
					"error when processing rowid set intersection, the segid %d and %d are different!", qresulta->sid, qresultb->sid);
			return NULL ;
		}
		qresult = (struct segment_query_rowids*) mem_pool_malloc(pMemPool,
				sizeof(struct segment_query_rowids));
		memset(qresult, 0, sizeof(struct segment_query_rowids));
		qresult->sid = qresulta->sid;
		qresult->rowids = rowid_intersection(pMemPool, qresulta->rowids,
				qresultb->rowids);
		list_add_tail(&qresult->rowids_list, rlist);
	}

	return rlist;
}

/**
 *	对所有segment求对应的rowid集合的差集，注意集合运算会改变链表中的数据
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct list_head* seg_rowid_minus(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb) {
	if (lista == NULL || listb == NULL ) {
		log_error("error when processing rowid set minus, the input is null!");
		return NULL ;
	}

	struct segment_query_rowids* qresult;
	struct segment_query_rowids* qresulta;
	struct segment_query_rowids* qresultb;
	struct list_head* rlist = (struct list_head*) mem_pool_malloc(pMemPool,
			sizeof(struct list_head));
	INIT_LIST_HEAD(rlist);

	double_list_for_each_entry(qresulta, lista, qresultb, listb, rowids_list) {
		if (qresulta->sid != qresultb->sid) {
			log_error(
					"error when processing rowid set minus, the segid %d and %d are different!", qresulta->sid, qresultb->sid);
			return NULL ;
		}
		qresult = (struct segment_query_rowids*) mem_pool_malloc(pMemPool,
				sizeof(struct segment_query_rowids));
		memset(qresult, 0, sizeof(struct segment_query_rowids));
		qresult->sid = qresulta->sid;
		qresult->rowids = rowid_minus(pMemPool, qresulta->rowids,
				qresultb->rowids);
		list_add_tail(&qresult->rowids_list, rlist);
	}

	return rlist;
}

struct list_head* seg_rowid_fulltext_hence(MEM_POOL_PTR pMemPool,
		struct list_head* lista, struct list_head* listb) {
	if (lista == NULL || listb == NULL ) {
		log_error(
				"error when processing rowid set fulltext hence, the input is null!");
		return NULL ;
	}

	struct segment_query_rowids* qresult;
	struct segment_query_rowids* qresulta;
	struct segment_query_rowids* qresultb;
	struct list_head* rlist = (struct list_head*) mem_pool_malloc(pMemPool,
			sizeof(struct list_head));
	INIT_LIST_HEAD(rlist);

	double_list_for_each_entry(qresulta, lista, qresultb, listb, rowids_list) {
		if (qresulta->sid != qresultb->sid) {
			log_error(
					"error when processing rowid set fulltext hence, the segid %d and %d are different!", qresulta->sid, qresultb->sid);
			return NULL ;
		}
		qresult = (struct segment_query_rowids*) mem_pool_malloc(pMemPool,
				sizeof(struct segment_query_rowids));
		memset(qresult, 0, sizeof(struct segment_query_rowids));
		qresult->sid = qresulta->sid;
		qresult->rowids = rowid_fulltext_hence(pMemPool, qresulta->rowids,
				qresultb->rowids);
		list_add_tail(&qresult->rowids_list, rlist);
	}

	return rlist;
}

int32_t seg_rowid_count(struct list_head* seglist) {
	int32_t count = 0;

	if (seglist == NULL ) {
		return 0;
	}

	struct segment_query_rowids* qresult;
	list_for_each_entry(qresult, seglist, rowids_list) {
		if (qresult->rowids != NULL ) {
			count += qresult->rowids->rowid_num;
		}
	}

	return count;
}

void seg_rowid_setscore(MEM_POOL_PTR mem_pool, struct list_head* seglist,
		double score) {
	if (seglist == NULL ) {
		return;
	}

	struct segment_query_rowids* qresult;

	list_for_each_entry(qresult, seglist, rowids_list) {
		rowid_list_setscore(mem_pool, qresult->rowids, score);
	}
}

/**
 *	求两个rowid集合的并集，注意集合运算会改变链表中的数据，结果集放在lista中，rowid的链表是有序的，排序方式为降序
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct rowid_list* rowid_union(MEM_POOL_PTR pMemPool, struct rowid_list* lista,
		struct rowid_list* listb) {
	//第一个rowid集合为空
	if (lista == NULL || lista->rowid_num == 0) {
		return listb;
	}
	//第二个rowid集合为空
	if (listb == NULL || listb->rowid_num == 0) {
		return lista;
	}

	//并集结果
	struct rowid_list* result = rowid_list_init(pMemPool);

	//p标记当前正在处理的lista中的链表块
	struct rowid_list_node* p = lista->head;
	//q标记当前正在处理的listb中的链表块	
	struct rowid_list_node* q = listb->head;
	//r标记并集结果当前链表块
	struct rowid_list_node* r = (struct rowid_list_node*) mem_pool_malloc(
			pMemPool, sizeof(struct rowid_list_node));
	memset(r, 0, sizeof(struct rowid_list_node));
	result->head = r;

	//i标记当前正在处理的lista中的元素编号
	//j标记当前正在处理的listb中的元素编号
	//k标记当前正在处理的结果集合中的元素编号
	uint32_t i = 0, j = 0, k = 0;

	while (i < lista->rowid_num && j < listb->rowid_num) {
		if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				> q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			r->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
					% ROWID_ARRAY_SIZE];
			if (p->score_array != NULL ) {
				if (r->score_array == NULL ) {
					r->score_array = (double*) mem_pool_malloc(pMemPool,
							sizeof(double) * ROWID_ARRAY_SIZE);
					memset(r->score_array, 0,
							sizeof(double) * ROWID_ARRAY_SIZE);
				}
				r->score_array[k % ROWID_ARRAY_SIZE] = p->score_array[i
						% ROWID_ARRAY_SIZE];
			}

			i++;
			k++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}

		} else if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				< q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			r->rowid_array[k % ROWID_ARRAY_SIZE] = q->rowid_array[j
					% ROWID_ARRAY_SIZE];
			if (q->score_array != NULL ) {
				if (r->score_array == NULL ) {
					r->score_array = (double*) mem_pool_malloc(pMemPool,
							sizeof(double) * ROWID_ARRAY_SIZE);
					memset(r->score_array, 0,
							sizeof(double) * ROWID_ARRAY_SIZE);
				}
				r->score_array[k % ROWID_ARRAY_SIZE] = q->score_array[j
						% ROWID_ARRAY_SIZE];
			}

			j++;
			k++;
			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
		} else {
			r->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
					% ROWID_ARRAY_SIZE];
			if (p->score_array != NULL || q->score_array != NULL ) {
				if (r->score_array == NULL ) {
					r->score_array = (double*) mem_pool_malloc(pMemPool,
							sizeof(double) * ROWID_ARRAY_SIZE);
					memset(r->score_array, 0,
							sizeof(double) * ROWID_ARRAY_SIZE);
				}

				if (p->score_array != NULL ) {
					r->score_array[k % ROWID_ARRAY_SIZE] += p->score_array[i
							% ROWID_ARRAY_SIZE];
				}
				if (q->score_array != NULL ) {
					r->score_array[k % ROWID_ARRAY_SIZE] += q->score_array[j
							% ROWID_ARRAY_SIZE];
				}
			}

			i++;
			j++;
			k++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
		}

		if (k % ROWID_ARRAY_SIZE == 0) {
			r->next = (struct rowid_list_node*) mem_pool_malloc(pMemPool,
					sizeof(struct rowid_list_node));
			memset(r->next, 0, sizeof(struct rowid_list_node));
			r = r->next;
		}

	}

	//第二个集合已经遍历完
	if (j == listb->rowid_num) {
		j = i;
		q = p;
		listb = lista;
	}

	//将剩余的元素依次加入到结果集合中
	while (j < listb->rowid_num) {
		r->rowid_array[k % ROWID_ARRAY_SIZE] = q->rowid_array[j
				% ROWID_ARRAY_SIZE];
		if (q->score_array != NULL ) {
			if (r->score_array == NULL ) {
				r->score_array = (double*) mem_pool_malloc(pMemPool,
						sizeof(double) * ROWID_ARRAY_SIZE);
				memset(r->score_array, 0, sizeof(double) * ROWID_ARRAY_SIZE);
			}
			r->score_array[k % ROWID_ARRAY_SIZE] = q->score_array[j
					% ROWID_ARRAY_SIZE];
		}

		j++;
		k++;

		if (j % ROWID_ARRAY_SIZE == 0) {
			q = q->next;
		}

		if (k % ROWID_ARRAY_SIZE == 0) {
			r->next = (struct rowid_list_node*) mem_pool_malloc(pMemPool,
					sizeof(struct rowid_list_node));
			memset(r->next, 0, sizeof(struct rowid_list_node));
			r = r->next;
		}

	}

	result->rowid_num = k;
	result->tail = r;
	return result;

}

/**
 *	求两个rowid集合的交集，注意集合运算会改变链表中的数据，结果集放在lista中，rowid的链表是有序的，排序方式为降序
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct rowid_list* rowid_intersection(MEM_POOL_PTR pMemPool,
		struct rowid_list* lista, struct rowid_list* listb) {
	//第一个rowid集合为空
	if (lista == NULL || lista->rowid_num == 0) {
		return lista;
	}
	//第二个rowid集合为空	
	if (listb == NULL || listb->rowid_num == 0) {
		return listb;
	}

	//交集结果
	struct rowid_list* result = rowid_list_init(pMemPool);

	//p标记当前正在处理的lista中的链表块
	struct rowid_list_node* p = lista->head;
	//q标记当前正在处理的listb中的链表块	
	struct rowid_list_node* q = listb->head;
	//r标记并集结果当前链表块
	struct rowid_list_node* r = (struct rowid_list_node*) mem_pool_malloc(
			pMemPool, sizeof(struct rowid_list_node));
	memset(r, 0, sizeof(struct rowid_list_node));
	result->head = r;

	//i标记当前正在处理的lista中的元素编号
	//j标记当前正在处理的listb中的元素编号
	//k标记当前正在处理的结果集合中的元素编号
	uint32_t i = 0, j = 0, k = 0;

	while (i < lista->rowid_num && j < listb->rowid_num) {
		if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				> q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			i++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
		} else if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				< q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			j++;

			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
		} else {
			r->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
					% ROWID_ARRAY_SIZE];
			if (p->score_array != NULL || q->score_array != NULL ) {
				if (r->score_array == NULL ) {
					r->score_array = (double*) mem_pool_malloc(pMemPool,
							sizeof(double) * ROWID_ARRAY_SIZE);
					memset(r->score_array, 0,
							sizeof(double) * ROWID_ARRAY_SIZE);
				}

				if (p->score_array != NULL ) {
					r->score_array[k % ROWID_ARRAY_SIZE] += p->score_array[i
							% ROWID_ARRAY_SIZE];
				}
				if (q->score_array != NULL ) {
					r->score_array[k % ROWID_ARRAY_SIZE] += q->score_array[j
							% ROWID_ARRAY_SIZE];
				}
			}

			i++;
			j++;
			k++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
			if (k % ROWID_ARRAY_SIZE == 0) {
				r->next = (struct rowid_list_node*) mem_pool_malloc(pMemPool,
						sizeof(struct rowid_list_node));
				memset(r->next, 0, sizeof(struct rowid_list_node));
				r = r->next;
			}
		}
	}

	if (k == 0) {
		lista->rowid_num = 0;
		lista->head = NULL;
		lista->tail = NULL;
		return lista;
	}

	result->rowid_num = k;
	result->tail = r;
	return result;
}

struct rowid_list* rowid_fulltext_hence(MEM_POOL_PTR pMemPool,
		struct rowid_list* lista, struct rowid_list* listb) {
	//第一个rowid集合为空
	if (lista == NULL || lista->rowid_num == 0) {
		return lista;
	}
	//第二个rowid集合为空
	if (listb == NULL || listb->rowid_num == 0) {
		return lista;
	}

	//结果
	struct rowid_list* result = lista;

	//p标记当前正在处理的lista中的链表块
	struct rowid_list_node* p = lista->head;
	//q标记当前正在处理的listb中的链表块
	struct rowid_list_node* q = listb->head;
	//r标记并集结果当前链表块
	struct rowid_list_node* r = p;

	//i标记当前正在处理的lista中的元素编号
	//j标记当前正在处理的listb中的元素编号
	//k标记当前正在处理的结果集合中的元素编号
	uint32_t i = 0, j = 0, k = 0;

	while (i < lista->rowid_num && j < listb->rowid_num) {
		if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				> q->rowid_array[j % ROWID_ARRAY_SIZE]) {

			r->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
					% ROWID_ARRAY_SIZE];
			r->score_array[k % ROWID_ARRAY_SIZE] = p->score_array[i
					% ROWID_ARRAY_SIZE];
			i++;
			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			k++;
			if (k % ROWID_ARRAY_SIZE == 0) {
				r = r->next;
			}
		} else if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				< q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			j++;

			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
		} else {
			r->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
					% ROWID_ARRAY_SIZE];
			r->score_array[k % ROWID_ARRAY_SIZE] = p->score_array[i
					% ROWID_ARRAY_SIZE] + q->score_array[j % ROWID_ARRAY_SIZE];

			i++;
			j++;
			k++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
			if (k % ROWID_ARRAY_SIZE == 0) {
				r = r->next;
			}
		}
	}

	if (k == 0) {
		lista->rowid_num = 0;
		lista->head = NULL;
		lista->tail = NULL;
		return lista;
	}

	result->rowid_num = k;
	result->tail = r;
	return result;
}




/**
 *	求两个rowid集合的差集，注意集合运算会改变链表中的数据，结果集放在lista中，rowid的链表是有序的，排序方式为降序
 *	@param		pMemPool	内存池，在目前的实现中不会通过内存池申请额外的内存，在以后的优化过程中可能会从中申请额外内存 
 *	@param 		lista
 *	@param 		listb
 *	@return		
 */
struct rowid_list* rowid_minus(MEM_POOL_PTR pMemPool, struct rowid_list* lista,
		struct rowid_list* listb) {
	//两个集合中有一个集合为空
	if (lista == NULL || listb == NULL || lista->rowid_num == 0
			|| listb->rowid_num == 0) {
		return lista;
	}

	//p标记当前正在处理的lista中的链表块
	struct rowid_list_node* p = lista->head;
	//q标记当前正在处理的listb中的链表块	
	struct rowid_list_node* q = listb->head;
	//result标记当前正在处理的差集结果中的链表块
	struct rowid_list_node* result = lista->head;
	//pre_result标记result的前一个链表块
	struct rowid_list_node* pre_result = NULL;

	//i标记当前正在处理的lista中的元素编号
	//j标记当前正在处理的listb中的元素编号
	//k标记当前正在处理的结果集合中的元素编号
	uint32_t i = 0, j = 0, k = 0;

	while (i < lista->rowid_num && j < listb->rowid_num) {
		if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				> q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			result->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
					% ROWID_ARRAY_SIZE];
			i++;
			k++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			if (k % ROWID_ARRAY_SIZE == 0) {
				pre_result = result;
				result = result->next;
			}

		} else if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				< q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			j++;

			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
		} else {
			i++;
			j++;

			if (i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			if (j % ROWID_ARRAY_SIZE == 0) {
				q = q->next;
			}
		}

	}

	//如果第一个集合还有剩余的元素，将它们依次加入到结果集合中
	while (i < lista->rowid_num) {
		result->rowid_array[k % ROWID_ARRAY_SIZE] = p->rowid_array[i
				% ROWID_ARRAY_SIZE];
		i++;
		k++;

		if (i % ROWID_ARRAY_SIZE == 0) {
			p = p->next;
		}
		if (k % ROWID_ARRAY_SIZE == 0) {
			pre_result = result;
			result = result->next;
		}
	}

	if (k == 0) {
		lista->rowid_num = 0;
		lista->head = NULL;
		lista->tail = NULL;
		return lista;
	}

	if (k % ROWID_ARRAY_SIZE == 0) {
		result = pre_result;
	}

	result->next = NULL;
	lista->rowid_num = k;
	lista->tail = result;

	return lista;

}

void rowid_list_setscore(MEM_POOL_PTR mem_pool, struct rowid_list* id_list,
		double score) {
	uint32_t i;
	//指向rowid链表结点的指针
	struct rowid_list_node* p;

	if (id_list == NULL ) {
		return;
	}
	for (p = id_list->head, i = 0; i < id_list->rowid_num;) {
		if (NULL == p->score_array) {
			p->score_array = (double*) mem_pool_malloc(mem_pool,
					sizeof(double) * ROWID_ARRAY_SIZE);
			memset(p->score_array, 0, sizeof(double) * ROWID_ARRAY_SIZE);
		}
		p->score_array[i % ROWID_ARRAY_SIZE] = score;

		i++;
		if (i % ROWID_ARRAY_SIZE == 0) {
			p = p->next;
		}
	}
}
