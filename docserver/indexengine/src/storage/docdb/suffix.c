/*
 * =====================================================================================
 *
 *       Filename:  hi_suffix.c
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

#include "suffix.h"

struct fulltext_word_t {
	struct low_data_struct* value;
	double weight;
	int64_t count;
};





static struct list_head* fulltext_match(char *table_name,
		struct list_head* seg_list, struct condition_t* cond,
		MEM_POOL_PTR mem_pool) {
	int32_t i, j;
	struct list_head* ret;
	int64_t total;

	struct fulltext_word_t** fw = (struct fulltext_word_t**) mem_pool_malloc(
			mem_pool, sizeof(struct fulltext_word_t*) * cond->value_num);
	memset(fw, 0, sizeof(struct fulltext_word_t*) * cond->value_num);
	for (i = 0; i < cond->value_num; i++) {
		fw[i] = (struct fulltext_word_t*) mem_pool_malloc(mem_pool,
				sizeof(struct fulltext_word_t));
		memset(fw[i], 0, sizeof(struct fulltext_word_t));
	}

	//get the weight score of each word
	total = db_get_record_num(table_name, seg_list)
			+ db_get_delete_num(table_name, seg_list);
	for (i = 0; i < cond->value_num; i++) {
		fw[i]->value = &cond->values[i];
		fw[i]->count = db_fulltext_index_length_query(table_name, seg_list,
				&cond->values[i], mem_pool);
		if (fw[i]->count == 0) {
			fw[i]->weight = 0;
		} else {
			fw[i]->weight = log(
					(total - fw[i]->count + 0.5) / (fw[i]->count + 0.5));
		}
	}

	//sort by the weight score
	for (i = 0; i < cond->value_num; i++) {
		for (j = i + 1; j < cond->value_num; j++) {
			if (fw[j]->weight > fw[i]->weight) {
				struct fulltext_word_t* tmp;
				tmp = fw[i];
				fw[i] = fw[j];
				fw[j] = tmp;
			}
		}
	}
	double weight_sum = 0;
	for (i = 0; i < cond->value_num; i++) {
		weight_sum += fw[i]->weight;
	}

	//select the docs
	double weight_cur = fw[0]->weight;
	ret = db_fulltext_index_equal_query(table_name, seg_list, fw[0]->value,
			mem_pool);
	seg_rowid_setscore(mem_pool, ret, fw[0]->weight);
	for (i = 1; i < cond->value_num; i++) {
		if (fw[i]->count > 10000) {
			//stop word
			break;
		}

		struct list_head* tmp_ret = db_fulltext_index_equal_query(table_name,
				seg_list, fw[i]->value, mem_pool);
		seg_rowid_setscore(mem_pool, tmp_ret, fw[i]->weight);

		if(seg_rowid_count(ret) < 1000){
			ret = seg_rowid_union(mem_pool, ret, tmp_ret);
		}else{
			ret = seg_rowid_fulltext_hence(mem_pool, ret, tmp_ret);
		}

		//if the weight of the left words is very small, then skip them
		weight_cur += fw[i]->weight;
		if ((weight_sum - weight_cur) < weight_cur / 10) {
			break;
		}
	}


	return ret;
}

static struct list_head* hash_compare_value(char *table_name,
		struct list_head* seg_list, struct condition_t* cond, MEM_POOL_PTR mem) {
	int32_t i;
	struct list_head* ret = NULL;
	struct list_head* tmp;
	struct db_range_query_condition range_condition;
	memset(&range_condition, 0, sizeof(struct db_range_query_condition));

	switch (cond->comparator) {
	case CT_EQ:
		//如果相等，则满足条件
		ret = db_index_equal_query(table_name, seg_list, &cond->values[0], mem);
		break;
	case CT_NE:
		log_error(
				"'NOT EQUAL' comparator not supported in indexwhere expression");
		return NULL ;
	case CT_MATCH:
		ret = fulltext_match(table_name, seg_list, cond, mem);
		break;
	case EXP_COMPARE_IN:
		if (cond->value_num > 0) {
			ret = db_index_equal_query(table_name, seg_list, &cond->values[0],
					mem);
			for (i = 1; i < cond->value_num; i++) {
				tmp = db_index_equal_query(table_name, seg_list,
						&cond->values[i], mem);
				if (NULL == tmp) {
					return NULL ;
				} else {
					ret = seg_rowid_union(mem, ret, tmp);
					if (NULL == ret) {
						return NULL ;
					}
				}
			}
		} else {
			ret = (struct list_head*) mem_pool_malloc(mem,
					sizeof(struct list_head));
			INIT_LIST_HEAD(ret);
		}
		break;
	default:
		log_error("不支持的比较类型");
		break;
	}

	return ret;
}

static int32_t hash_set_operations(struct list_head* node_list_head,
		enum condition_type op_type, MEM_POOL* mem_pool) {
//碰到运算符，需出栈
	struct hash_stack_node* node_left;
	struct hash_stack_node* node_right;

//出栈
	if (list_empty(node_list_head)) {
		log_warn("hash逆波兰表达式有误");
		return -1;
	}
	node_right =
			list_entry(node_list_head->next, typeof(*node_right), node_list);
	list_del(&node_right->node_list);

	if (list_empty(node_list_head)) {
		log_warn("hash逆波兰表达式有误");
		return -1;
	}
	node_left = list_entry(node_list_head->next, typeof(*node_left), node_list);
	list_del(&node_left->node_list);

	switch (op_type) {
	case LOGIC_AND:
		//调用交集运算方法
		node_left->hash_result = seg_rowid_intersection(mem_pool,
				node_left->hash_result, node_right->hash_result);
		if (node_left->hash_result == NULL ) {
			log_warn("交集计算有误");
			return ERROR_SET_OPERATION;
		}
		break;
	case HC_SET_MINUS:
		//调用差集运算方法
		node_left->hash_result = seg_rowid_minus(mem_pool,
				node_left->hash_result, node_right->hash_result);
		if (node_left->hash_result == NULL ) {
			log_warn("差集计算有误");
			return ERROR_SET_OPERATION;
		}
		break;
	case LOGIC_OR:
		//调用并集运算方法
		node_left->hash_result = seg_rowid_union(mem_pool,
				node_left->hash_result, node_right->hash_result);
		if (node_left->hash_result == NULL ) {
			log_warn("并集计算有误");
			return ERROR_SET_OPERATION;
		}
		break;
	default:
		log_warn("不支持的集合运算方法");
		return ERROR_UNSUPPORTED_SQL_TYPE;
	}

//将计算后的结果入栈
	list_add(&node_left->node_list, node_list_head);
	return MILE_RETURN_SUCCESS;
}

struct list_head* query_by_hash_conditions(char *table_name,
		struct condition_array* conditions, struct list_head *seg_list,
		MEM_POOL* mem_pool) {
//初始化栈头
	uint32_t i;
	struct list_head node_list_head;
	struct condition_t *cond;
	struct hash_stack_node* result;
	struct hash_stack_node* node;

	INIT_LIST_HEAD(&node_list_head);

	for (i = 0; i < conditions->n; i++) {
		cond = &conditions->conditions[i];

		switch (cond->type) {
		case LOGIC_AND:
		case LOGIC_OR:
		case HC_SET_MINUS:
			if (hash_set_operations(&node_list_head, cond->type, mem_pool)
					< 0) {
				return NULL ;
			}
			break;
		case CONDITION_EXP:
			//如果是表达式，则需要查询出结果，并将结果压栈
			node = (struct hash_stack_node*) mem_pool_malloc(mem_pool,
					sizeof(struct hash_stack_node));
			memset(node, 0, sizeof(struct hash_stack_node));
			INIT_LIST_HEAD(&node->node_list);
			//调用db的接口查询hash的值
			node->hash_result = hash_compare_value(table_name, seg_list, cond,
					mem_pool);
			//如果返回NULL，则说明值非法
			if (node->hash_result == NULL ) {
				return NULL ;
			}
			//入栈
			list_add(&node->node_list, &node_list_head);
			break;
		default:
			log_error("不支持的hash过滤条件%d", cond->type);
			return NULL ;
		}

	}

//如果整个条件都执行完了，栈里只剩下一个元素
	if (list_empty(&node_list_head)) {
		log_warn("hash逆波兰表达式有误");
		return NULL ;
	}
	result = list_entry(node_list_head.next, typeof(*result), node_list);
	list_del(&result->node_list);

	return result->hash_result;
}

