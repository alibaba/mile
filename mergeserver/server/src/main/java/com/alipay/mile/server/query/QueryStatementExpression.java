/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import com.alipay.mile.Constants;

public abstract class QueryStatementExpression extends Statement {
    public int                      size = 1;
    // left expression
    public QueryStatementExpression leftExpression;
    // right expression
    public QueryStatementExpression rightExpression;

    /**
     * 添加IntersectSet 节点
     *
     * @param another
     * @return
     */
    public QueryStatementExpression intersectSetExp(QueryStatementExpression another) {
        SetOperatorExpression parent = new SetOperatorExpression();
        parent.leftExpression = this;
        parent.operator = Constants.EXP_INTERSECTION;
        parent.rightExpression = another;
        parent.size = 1 + this.size + another.size;
        return parent;
    }

    /**
     * 添加 UnionSet 节点
     *
     * @param another
     * @return
     */
    public QueryStatementExpression unionSetExp(QueryStatementExpression another) {
        SetOperatorExpression parent = new SetOperatorExpression();
        parent.leftExpression = this;
        parent.operator = Constants.EXP_UNIONSET;
        parent.rightExpression = another;
        parent.size = 1 + this.size + another.size;
        return parent;
    }

    /**
     * 是否是中间节点
     *
     * @return
     */
    public boolean isComposition() {
        return leftExpression != null || rightExpression != null;
    }

    /**
     * 得到左子树
     *
     * @return
     */
    public Statement getLeft() {
        return this.leftExpression;
    }

    /**
     * 得到右子树
     *
     * @return
     */
    public Statement getRight() {
        return this.rightExpression;
    }


}
