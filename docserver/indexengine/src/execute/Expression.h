/*
 * Expression.h
 *
 *  Created on: 2012-8-30
 *      Author: yuzhong.zhao
 */

#ifndef EXPRESSION_H_
#define EXPRESSION_H_

#include "../protocol/packet.h"
#include "../common/hash.h"


class Expression {
private:
	struct condition_t* cond;
public:
	Expression* left;
	Expression* right;
	struct select_fields_t* exp_fields;
	Expression(struct condition_t* cond);
	virtual ~Expression();
	static Expression* GenExp(struct condition_array* cond_array, MEM_POOL_PTR mem_pool);
	int32_t Execute(struct select_row_t* row);
};

#endif /* EXPRESSION_H_ */
