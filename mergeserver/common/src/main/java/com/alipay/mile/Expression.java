/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author jin.qian
 * @version $Id: Expression.java, v 0.1 2011-5-10 下午05:03:53 jin.qian Exp $
 */
public abstract class Expression {
    /** 表达式树中包含节点的个数 */
    public int        size     = 1;

    /** 左节点 */
    public Expression leftExp  = null;

    /** 右节点 */
    public Expression rightExp = null;

    /**
     * 添加and 节点
     * 
     * @param another
     * @return
     */
    public Expression andExp(Expression another) {
        OperatorExp parent = new OperatorExp();
        parent.leftExp = this;
        parent.operator = Constants.EXP_LOGIC_AND;
        parent.rightExp = another;
        parent.size = 1 + this.size + another.size;
        return parent;
    }

    /**
     * 添加 or 节点
     * 
     * @param another
     * @return
     */
    public Expression orExp(Expression another) {
        OperatorExp parent = new OperatorExp();
        parent.leftExp = this;
        parent.operator = Constants.EXP_LOGIC_OR;
        parent.rightExp = another;
        parent.size = 1 + this.size + another.size;
        return parent;
    }

    /**
     * 是否是中间节点
     * 
     * @return
     */
    public boolean isComposition() {
        return leftExp != null || rightExp != null;
    }

    /**
     * 得到左子树
     * 
     * @return
     */
    public Expression getLeft() {
        return this.leftExp;
    }

    /**
     * 得到右子树
     * 
     * @return
     */
    public Expression getRight() {
        return this.rightExp;
    }

    /**
     * 将表达式树通过后序遍历的方式写入到输出流中
     * 
     * @param os		输出流
     * @throws IOException
     * @throws ExecutionException 
     * @throws InterruptedException 
     * @throws IllegalSqlException 
     * @throws SqlExecuteException 
     */
    public void postWriteToStream(DataOutput os, Map<Object, List<Object>> paramBindMap) throws IOException {
        if (isComposition()) {
            leftExp.postWriteToStream(os, paramBindMap);
            rightExp.postWriteToStream(os, paramBindMap);
        }
        writeToStream(os, paramBindMap);
    }

    /**
     * 将表达式写入到输出流中
     * 
     * @param os
     * @throws ExecutionException 
     * @throws InterruptedException 
     * @throws IllegalSqlException 
     * @throws SqlExecuteException 
     */
    public abstract void writeToStream(DataOutput os, Map<Object, List<Object>> params) throws IOException;

}
