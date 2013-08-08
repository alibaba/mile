/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_suffix.h
 *
 *    Description:  对hash条件、filter条件的逆波兰式计算
 *
 *        Version:  1.0
 *        Created:  2011/05/06 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yuzhong.zhao
 *        Company:  alipay
 *
 * =====================================================================================
 */



#include "../../common/def.h"
#include "../../common/list.h"
#include "../../protocol/packet.h"
#include "set_operator.h"
#include "db.h"

#ifndef SUFFIX_H
#define SUFFIX_H


//hash栈结点信息
struct hash_stack_node{
	struct list_head  node_list;
	struct list_head* hash_result;
};





/**
  * 根据hash逆波兰表达式进行运算
  * @param  table_name
  * @param  conditions 哈希逆波兰表达式
  * @param  mem_pool 内存池
  * @return 成功返回segnment_query_rowids的结果集
  **/ 
struct list_head* query_by_hash_conditions(char *table_name, struct condition_array* conditions, struct list_head *seg_list, MEM_POOL* mem_pool);




#endif
