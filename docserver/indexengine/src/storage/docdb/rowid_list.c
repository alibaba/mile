/*
 * =====================================================================================
 *
 *       Filename:  rowid_list.c
 *
 *    Description:  rowid的集合，采用块状链表的数据结构。由于采用倒排索引，rowid中的rowid是有序的，排序方式为降序
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

#include "rowid_list.h"

struct rowid_list* rowid_list_init(MEM_POOL_PTR pMemPool) {
	struct rowid_list* id_list = (struct rowid_list*) mem_pool_malloc(pMemPool,
			sizeof(struct rowid_list));
	id_list->rowid_num = 0;
	id_list->head = NULL;
	id_list->tail = NULL;
	return id_list;
}

int32_t rowid_list_equals(struct rowid_list* lista, struct rowid_list* listb) {
	struct rowid_list_node *p, *q;
	uint32_t i, j;

	if (lista == NULL && listb == NULL ) {
		return 1;
	}
	if (lista == NULL || listb == NULL ) {
		return 0;
	}

	if (lista->rowid_num != listb->rowid_num) {
		return 0;
	}

	for (i = 0, j = 0, p = lista->head, q = listb->head;
			i < lista->rowid_num && j < listb->rowid_num; i++, j++) {
		if (p->rowid_array[i % ROWID_ARRAY_SIZE]
				!= q->rowid_array[j % ROWID_ARRAY_SIZE]) {
			return 0;
		}
		if (i != 0 && i % ROWID_ARRAY_SIZE == 0) {
			p = p->next;
		}
		if (j != 0 && j % ROWID_ARRAY_SIZE == 0) {
			q = q->next;
		}
	}

	if (p != lista->tail || q != listb->tail) {
		return 0;
	}

	return 1;
}

int32_t rowid_list_for_each(struct rowid_list* id_list, ROWID_LIST_FUNC func,
		int64_t deadline_time, void* user_data) {
	uint32_t i;
	//指向rowid链表结点的指针
	struct rowid_list_node* p;
	uint32_t rowid;
	int32_t result_code;

	if (id_list == NULL ) {
		return MILE_RETURN_SUCCESS;
	}
	for (p = id_list->head, i = 0; i < id_list->rowid_num;) {
		rowid = p->rowid_array[i % ROWID_ARRAY_SIZE];

		i++;
		if (i % ROWID_ARRAY_SIZE == 0) {
			p = p->next;
		}

		result_code = (*func)(rowid, user_data);
		if (result_code < 0) {
			return result_code;
		}

		if (i % 100 == 0) {
			//每隔100次检查下超时
			if (get_time_msec() > (uint64_t) deadline_time) {
				return ERROR_TIMEOUT;
			}
		}
	}
	return MILE_RETURN_SUCCESS;
}

/**
 * 向rowid_list中添加新的rowid，注意数据库中的rowid链表是降序的
 *
 * @param pMemPool		内存池
 * @param id_list		rowid的链表
 * @param rowid
 */
void rowid_list_add(MEM_POOL_PTR pMemPool, struct rowid_list* id_list,
		uint32_t rowid) {
	if (id_list == NULL ) {
		return;
	}

	if (id_list->head == NULL || id_list->rowid_num % ROWID_ARRAY_SIZE == 0) {
		struct rowid_list_node* tmp = (struct rowid_list_node*) mem_pool_malloc(
				pMemPool, sizeof(struct rowid_list_node));
		memset(tmp, 0, sizeof(struct rowid_list_node));

		tmp->rowid_array[0] = rowid;
		tmp->next = NULL;
		id_list->rowid_num++;
		if (id_list->head == NULL ) {
			id_list->head = tmp;
			id_list->tail = tmp;
		} else {
			id_list->tail->next = tmp;
			id_list->tail = tmp;
		}
	} else {
		id_list->tail->rowid_array[id_list->rowid_num % ROWID_ARRAY_SIZE] =
				rowid;
		id_list->rowid_num++;
	}

}






/**
 * 向rowid_list中添加新的rowid，注意数据库中的rowid链表是降序的
 *
 * @param pMemPool		内存池
 * @param id_list		rowid的链表
 * @param rowid
 */
void rowid_list_batch_add(MEM_POOL_PTR pMemPool, struct rowid_list* id_list,
		uint32_t* rowids, uint32_t num) {
	uint32_t insert_num = 0;
	uint32_t total = num;
	if (id_list == NULL ) {
		return;
	}

	while (num != 0) {
		insert_num = num / ROWID_ARRAY_SIZE > 0 ? ROWID_ARRAY_SIZE : num;
		struct rowid_list_node* tmp = (struct rowid_list_node*) mem_pool_malloc(
				pMemPool, sizeof(struct rowid_list_node));
		memset(tmp, 0, sizeof(struct rowid_list_node));


		memcpy(tmp->rowid_array, rowids + total - num,
				insert_num * sizeof(uint32_t));

		tmp->next = NULL;
		if (id_list->head == NULL ) {
			id_list->head = tmp;
			id_list->tail = tmp;
		} else {
			id_list->tail->next = tmp;
			id_list->tail = tmp;
		}
		num -= insert_num;
		id_list->rowid_num += insert_num;
	}
}

/*-----------------------------------------------------------------------------
 *  辅助函数，通过num定位到块状链表rowid_array的位置
 *-----------------------------------------------------------------------------*/
static inline uint32_t rowid_get(struct rowid_list_node ** rowid_array, int num) {
	return rowid_array[num / ROWID_ARRAY_SIZE]->rowid_array[num
			% ROWID_ARRAY_SIZE];
}

/*-----------------------------------------------------------------------------
 *  辅助函数，通过num定位到块状链表rowid_array的位置
 *-----------------------------------------------------------------------------*/
static inline void rowid_set(struct rowid_list_node ** rowid_array, int i,
		uint32_t key) {
	rowid_array[i / ROWID_ARRAY_SIZE]->rowid_array[i % ROWID_ARRAY_SIZE] = key;
}

/*-----------------------------------------------------------------------------
 *  对一个块状链表rowlist按照rowid做快速排序，从大到小
 *-----------------------------------------------------------------------------*/
void rowid_qsort(struct rowid_list_node ** rowid_array, int start, int end) {
	uint32_t key = rowid_get(rowid_array, start);
	int left = start, right = end;
	if (left >= right)
		return;

	while (left < right) {
		while (left < right && rowid_get(rowid_array, right) <= key)
			right--;
		rowid_set(rowid_array, left, rowid_get(rowid_array, right));

		while (left < right && rowid_get(rowid_array, left) >= key)
			left++;
		rowid_set(rowid_array, right, rowid_get(rowid_array, left));
	}
	rowid_set(rowid_array, right, key);

	rowid_qsort(rowid_array, start, right - 1);
	rowid_qsort(rowid_array, right + 1, end);
}

void print_rowid_list(struct rowid_list* id_list) {
	uint32_t i;
	struct rowid_list_node* p;

	if (id_list == NULL ) {
		printf("rowid list 为空 ");
	} else {
		for (i = 0, p = id_list->head; i < id_list->rowid_num; i++) {
			if (i != 0 && i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			printf("%d ", p->rowid_array[i % ROWID_ARRAY_SIZE]);
		}
	}

	printf("\n");
}

int32_t print_rowid_list_to_buffer(struct rowid_list* id_list, char* buffer,
		int32_t size) {
	uint32_t i;
	struct rowid_list_node* p;
	int32_t offset = 0;

	if (id_list == NULL ) {
		offset += snprintf_buffer(buffer + offset, size - offset,
				"rowid list 为空 ");
	} else {
		for (i = 0, p = id_list->head; i < id_list->rowid_num; i++) {
			if (i != 0 && i % ROWID_ARRAY_SIZE == 0) {
				p = p->next;
			}
			offset += snprintf_buffer(buffer + offset, size - offset, "%d ",
					p->rowid_array[i % ROWID_ARRAY_SIZE]);
		}
	}

	return offset;
}

