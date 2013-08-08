/*
 * =====================================================================================
 *
 *       Filename:  rowid_list.h
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



#ifndef ROWID_LIST_H
#define ROWID_LIST_H
#include "../../common/log.h"
#include "../../common/mem.h"
#include "../../common/list.h"
#include "../../common/def.h"


#define ROWID_ARRAY_SIZE 256


typedef int32_t (*ROWID_LIST_FUNC) (uint32_t, void*);



struct rowid_list_node{
	uint32_t rowid_array[ROWID_ARRAY_SIZE];
	double* score_array;
	struct rowid_list_node* next;
};


struct rowid_list{
	uint32_t rowid_num;
	struct rowid_list_node* head;
	struct rowid_list_node* tail;
};





/**
 * 初始化一个rowid_list
 *
 * @param pMemPool		内存池
 * @retrun				指向rowid_list的指针
 */
struct rowid_list* rowid_list_init(MEM_POOL_PTR pMemPool);





/**
 * 向rowid_list中添加新的rowid，注意数据库中的rowid链表是降序的
 *
 * @param pMemPool		内存池
 * @param id_list		rowid的链表
 * @param rowid
 */
void rowid_list_add(MEM_POOL_PTR pMemPool, struct rowid_list* id_list, uint32_t rowid);






void rowid_list_batch_add(MEM_POOL_PTR pMemPool, struct rowid_list* id_list, uint32_t* rowids,uint32_t num);


/**
 * 遍历整个rowid_list
 *
 * @param id_list		rowid的链表
 * @param func			对每个rowid进行操作的函数
 * @param deadline_time	超时时间
 * @param user_data		func函数还需要的其他参数
 * @return				成功时返回MILE_RETURN_SUCCESS， 异常时返回值<0
 */
int32_t rowid_list_for_each(struct rowid_list* id_list, ROWID_LIST_FUNC func, int64_t deadline_time, void* user_data);





/**
 * 判断两个rowid_list是否相同
 *
 * @param lista			
 * @param listb			
 * @return				两个rowid_list相同时, 返回1; 否则返回0
 */
int32_t rowid_list_equals(struct rowid_list* lista, struct rowid_list* listb);





/* 
 * ===  FUNCTION  ======================================================================
 *         Name:  rowid_qsort
 *  Description:  对一个块状链表rowlist按照rowid做快速排序，从大到小
 *  	@param row_array 存储的是块状链表各个块的指针
 *  	@param start 所有rowid的开始的位置
 *  	@param end   所有rowid结束位置
 * 		@return 
 * =====================================================================================
 */
void rowid_qsort(struct rowid_list_node ** row_array, int start, int end);





/* 
 * ===  FUNCTION  ======================================================================
 *         Name:  print_rowid_list
 *  Description:  输出id_list的所有rowid
 *  	@param 	id_list
 * 		@return 			
 * =====================================================================================
 */
void print_rowid_list(struct rowid_list* id_list);




/* 
 * ===  FUNCTION  ======================================================================
 *         Name:  print_rowid_list_to_buffer
 *  Description:  输出id_list的所有rowid到buffer里面
 *  	@param 	id_list
 *		@param	buffer		字符数组
 *		@param	size		字符数组的大小
 * 		@return 			写入的字符数
 * =====================================================================================
 */
int32_t print_rowid_list_to_buffer(struct rowid_list* id_list, char* buffer, int32_t size);







#endif
