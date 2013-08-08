/*
 * Expression.cpp
 *
 *  Created on: 2012-8-30
 *      Author: yuzhong.zhao
 */

#include "Expression.h"

Expression::Expression(struct condition_t* cond) {
	this->cond = cond;
	this->left = NULL;
	this->right = NULL;
	this->exp_fields = NULL;
}

Expression::~Expression() {
	// TODO Auto-generated destructor stub
}





Expression* Expression::GenExp(condition_array* cond_array, MEM_POOL_PTR mem_pool){
	int i, j;
	Expression* exp;
	MileList* list = new(mem_pool_malloc(mem_pool, sizeof(MileList)))MileList(mem_pool);
	struct select_fields_t* exp_fields = init_select_fields_t(mem_pool, cond_array->n/2+1);
	exp_fields->n = 0;

	for(i= 0; i < cond_array->n; i++) {
		struct condition_t * cond = &cond_array->conditions[i];
		exp = new(mem_pool_malloc(mem_pool, sizeof(Expression)))Expression(cond);

		if(cond->type == LOGIC_AND || cond->type == LOGIC_OR){
			// 碰到运算符，需出栈
			if(list->IsEmpty()) {
				log_warn("filter逆波兰表达式有误");
				return NULL;
			}

			exp->left = (Expression*) list->Pop();

			if(list->IsEmpty()) {
				log_warn("filter逆波兰表达式有误");
				return NULL;
			}
			exp->right = (Expression*) list->Pop();
		}
		if(cond->type == CONDITION_EXP){
			if(cond->comparator == CT_EQ || cond->comparator == CT_NE || cond->comparator == EXP_COMPARE_IN){
				cond->hash_values = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct)*cond->value_num);
				memset(cond->hash_values, 0, sizeof(struct low_data_struct)*cond->value_num);
				for(int k = 0; k < cond->value_num; k++){
					cond->hash_values[k].type = HI_TYPE_LONGLONG;
					cond->hash_values[k].len = sizeof(uint64_t);
					cond->hash_values[k].data = mem_pool_malloc(mem_pool, sizeof(uint64_t));
					*(uint64_t*)cond->hash_values[k].data = get_hash_value(&cond->values[k]);
				}
			}

			for(j = 0; j < exp_fields->n; j++){
				if(strcmp(cond->field_name, exp_fields->fields_name[j]) == 0){
					break;
				}
			}
			if(j == exp_fields->n){
				exp_fields->fields_name[exp_fields->n] = cond->field_name;
				if(cond->comparator == CT_EQ || cond->comparator == CT_NE || cond->comparator == EXP_COMPARE_IN){
					exp_fields->select_type[exp_fields->n] = SELECT_TYPE_HASH;
				}else{
					exp_fields->select_type[exp_fields->n] = SELECT_TYPE_ORIGINAL;
				}
				exp_fields->n++;
			}
		}
		list->Add(exp);
	}

	//如果整个条件都执行完了，栈里只剩下一个元素
	if(list->IsEmpty()) {
		log_warn("filter逆波兰表达式有误");
		return NULL;
	}

	exp = (Expression*)list->Pop();
	exp->exp_fields = exp_fields;
	return exp;
}







int32_t CompareValue(struct low_data_struct* value, enum compare_type comparator, uint32_t cond_value_num, struct low_data_struct* cond_values)
{
	uint32_t i;
	int32_t ret = 0;

	switch(comparator)
	{
		case CT_EQ:
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 0){
				return 1;
			}else{
				return 0;
			}
		case CT_NE:
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 0){
				return 0;
			}else{
				return 1;
			}
		case CT_LT:
			//如果小于，则满足条件
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == -1){
				return 1;
			}else{
				return 0;
			}

		case CT_LE:
			//如果小于等于，则满足条件
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == -1 || ret == 0){
				return 1;
			}else{
				return 0;
			}
		case CT_GE:
			//如果大于等于，则满足条件
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 1 || ret == 0){
				return 1;
			}else{
				return 0;
			}
		case CT_GT:
			//如果大于，则满足条件
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 1){
				return 1;
			}else{
				return 0;
			}
		case EXP_COMPARE_IN:
			for(i = 0; i < cond_value_num; i++)
			{
				ret = compare_ld(value, &cond_values[i]);
				if(ret == -2)
				{
					return 0;
				}
				if(ret == 0)
				{
					return 1;
				}
			}
			ret = 0;
			break;
		case EXP_COMPARE_BETWEEN_LGE:
			//(]
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 1){
				ret = compare_ld(value, &cond_values[1]);
				if(ret == -2)
				{
					return 0;
				}else if(ret == 0 || ret == -1){
					return 1;
				}else{
					return 0;
				}
			}else{
				return 0;
			}
		case EXP_COMPARE_BETWEEN_LEGE:
			//[]
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 0 || ret == 1){
				ret = compare_ld(value, &cond_values[1]);
				if(ret == -2)
				{
					return 0;
				}else if(ret == 0 || ret == -1){
					return 1;
				}else{
					return 0;
				}
			}else{
				return 0;
			}
		case EXP_COMPARE_BETWEEN_LG:
			//()
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 1){
				ret = compare_ld(value, &cond_values[1]);
				if(ret == -2)
				{
					return 0;
				}else if(ret == -1){
					return 1;
				}else{
					return 0;
				}
			}else{
				return 0;
			}
		case EXP_COMPARE_BETWEEN_LEG:
			//[)
			ret = compare_ld(value, &cond_values[0]);
			if(ret == -2)
			{
				return 0;
			}else if(ret == 0 || ret == 1){
				ret = compare_ld(value, &cond_values[1]);
				if(ret == -2)
				{
					return 0;
				}else if(ret == -1){
					return 1;
				}else{
					return 0;
				}
			}else{
				return 0;
			}
		default:
			log_error("不支持的比较类型");
			return -1;
	}

	return ret;
}








int32_t Expression::Execute(struct select_row_t* row){
	uint32_t i;

	if(left == NULL || right == NULL){
		for(i = 0; i < row->n; i++){
			if(strcmp(cond->field_name, row->data[i].field_name) == 0){
				if(row->select_type[i] == SELECT_TYPE_HASH){
					return CompareValue(&row->data[i], cond->comparator, cond->value_num, cond->hash_values);
				}else{
					return CompareValue(&row->data[i], cond->comparator, cond->value_num, cond->values);
				}
			}
		}
		return -1;
	}else{
		switch(cond->type){
		case LOGIC_AND:
			if(left->Execute(row) == 1 && right->Execute(row) == 1){
				return 1;
			}else{
				return 0;
			}
		case LOGIC_OR:
			if(left->Execute(row) == 1 || right->Execute(row) == 1){
				return 1;
			}else{
				return 0;
			}
		default:
			log_error("不支持的filter过滤条件%d", cond->type);
			return -1;
		}
	}
}
