/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.Constants;
import com.alipay.mile.Expression;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.OperatorExp;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.ColumnExp;
import com.alipay.mile.server.query.ColumnSubSelectExp;

/**
 * 
 * having步骤，对聚组后的结果进行条件过滤
 * 
 * @author yuzhong.zhao
 * @version $Id: HavingStep.java, v 0.1 2012-6-26 下午09:02:29 yuzhong.zhao Exp $
 */
public class HavingStep implements ExecuteStep {
    // having条件
    private Expression havingCondition;
    // having列在选择列中的索引位置
    private Map<FieldDesc, Integer> havingIndex;
    
    
    
    public HavingStep(Expression havingCondition, List<FieldDesc> selectFields) throws SqlExecuteException{
        this.havingCondition = havingCondition;
        this.havingIndex = new HashMap<FieldDesc, Integer>();
        computeHavingIndex(havingCondition, selectFields);
    }
    
    
    
    /**
     * 计算having列在选择列中的位置
     *
     * @param havingCondition       having条件
     * @param selectFields          选择列
     * @throws SqlExecuteException
     * @throws Exception
     */
    private void computeHavingIndex(Expression havingCondition, List<FieldDesc> selectFields)
                                                                                             throws SqlExecuteException {
        if (null == havingCondition) {
            return;
        }

        if (havingCondition.isComposition()) {
            // 递归遍历
            computeHavingIndex(havingCondition.getLeft(), selectFields);
            computeHavingIndex(havingCondition.getRight(), selectFields);
        } else {
            FieldDesc field = null;
            if (havingCondition instanceof ColumnExp) {
                field = ((ColumnExp) havingCondition).column;
            } else if (havingCondition instanceof ColumnSubSelectExp) {
                field = ((ColumnSubSelectExp) havingCondition).column;
            } else {
                throw new SqlExecuteException("unknown expression" + havingCondition);
            }
            int index = selectFields.indexOf(field);
            if (index < 0) {
                throw new SqlExecuteException("在选择列中找不到having条件列" + field);
            } else {
                havingIndex.put(field, index);
            }
        }
    }
    
    
    
    /**
     * 比较两个对象，当o1<o2时返回负数，当o1>o2时返回正数，两者相等时返回0
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) throws SqlExecuteException {
        if (null == o1 && null == o2) {
            return 0;
        } else if (null == o1) {
            return -1;
        } else if (null == o2) {
            return 1;
        } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
            Comparable<Object> comp1 = (Comparable<Object>) o1;
            Comparable<Object> comp2 = (Comparable<Object>) o2;
            return comp1.compareTo(comp2);
        } else {
            throw new SqlExecuteException("不能进行比较的对象类型");
        }
    }
    
    
    
    

    /**
     * 进行having条件过滤
     *
     * @param record                需要进行过滤的记录
     * @param havingCondition       having条件表达式
     * @param params                having条件中的动态绑定参数
     * @param timeOut               超时时间
     * @return                      是否满足having条件
     * @throws SqlExecuteException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws IllegalSqlException
     * @throws Exception
     */
    private boolean having(Record record, Expression havingCondition,
            Map<Object, List<Object>> paramBindMap, int timeOut) throws SqlExecuteException,
                                                       IllegalSqlException, IOException,
                                                       InterruptedException, ExecutionException {
        if (havingCondition.isComposition()) {
            // 此节点不是叶子节点
            OperatorExp operatorExp = (OperatorExp) havingCondition;
            switch (operatorExp.getOperator()) {
                case Constants.EXP_LOGIC_AND:
                    return having(record, havingCondition.getLeft(), paramBindMap, timeOut)
                           && having(record, havingCondition.getRight(), paramBindMap, timeOut);
                case Constants.EXP_LOGIC_OR:
                    return having(record, havingCondition.getLeft(), paramBindMap, timeOut)
                           || having(record, havingCondition.getRight(), paramBindMap, timeOut);
                default:
                    throw new SqlExecuteException("不支持的逻辑运算符" + operatorExp.getOperator());
            }
        } else {
            // 叶子节点的having条件处理
            byte comparetor;
            Object left;
            List<Object> right;
            if (havingCondition instanceof ColumnExp) {
                ColumnExp exp = (ColumnExp) havingCondition;
                comparetor = exp.comparetor;
                int index = havingIndex.get(exp.getField());
                left = record.data.get(index);
                right = exp.getBindParams(paramBindMap);
            } else if (havingCondition instanceof ColumnSubSelectExp) {
                ColumnSubSelectExp exp = (ColumnSubSelectExp) havingCondition;
                comparetor = exp.comparetor;
                left = record.data.get(havingIndex.get(exp.column));
                right = exp.getBindParams(paramBindMap);
            } else {
                throw new SqlExecuteException("未知的having条件表达式" + havingCondition);
            }

            switch (comparetor) {
                case Constants.EXP_COMPARE_EQUALS:
                    return left.equals(right.get(0));
                case Constants.EXP_COMPARE_NOT_EQUALS:
                    return !left.equals(right.get(0));
                case Constants.EXP_COMPARE_LT:
                    return compare(left, right.get(0)) < 0;
                case Constants.EXP_COMPARE_GET:
                    return compare(left, right.get(0)) >= 0;
                case Constants.EXP_COMPARE_GT:
                    return compare(left, right.get(0)) > 0;
                case Constants.EXP_COMPARE_LET:
                    return compare(left, right.get(0)) <= 0;
                case Constants.EXP_COMPARE_BETWEEN_LEG:
                    return compare(left, right.get(0)) >= 0 && compare(left, right.get(1)) < 0;
                case Constants.EXP_COMPARE_BETWEEN_LEGE:
                    return compare(left, right.get(0)) >= 0 && compare(left, right.get(1)) <= 0;
                case Constants.EXP_COMPARE_BETWEEN_LG:
                    return compare(left, right.get(0)) > 0 && compare(left, right.get(1)) < 0;
                case Constants.EXP_COMPARE_BETWEEN_LGE:
                    return compare(left, right.get(0)) > 0 && compare(left, right.get(1)) <= 0;
                case Constants.EXP_COMPARE_IN:
                    for (Object rightObj : right) {
                        if (left.equals(rightObj)) {
                            return true;
                        }
                    }
                    return false;
                default:
                    throw new SqlExecuteException("不支持的比较运算符" + comparetor);
            }
        }
    }
    
    
    
    /** 
     * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object, java.util.Map, int)
     */
    @Override
    public Object execute(Object input, Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                      throws SqlExecuteException,
                                                                                      IOException,
                                                                                      InterruptedException,
                                                                                      ExecutionException,
                                                                                      IllegalSqlException {

        SqlResultSet resultSet;
        
        if (null == input) {
            throw new SqlExecuteException("在执行having步时输入为空");
        }
        if (!(input instanceof SqlResultSet)) {
            throw new SqlExecuteException("在执行having步时输入不是SqlResultSet类型");
        }
        resultSet = (SqlResultSet) input;
        
        List<Record> havingResult = new ArrayList<Record>();
        for(Record record : resultSet.data){
            if(having(record, havingCondition, paramBindMap, timeOut)){
                havingResult.add(record);
            }
        }

        resultSet.data = havingResult;
        return resultSet;
    }



    public Expression getHavingCondition() {
        return havingCondition;
    }



    public void setHavingCondition(Expression havingCondition) {
        this.havingCondition = havingCondition;
    }



    public Map<FieldDesc, Integer> getHavingIndex() {
        return havingIndex;
    }



    public void setHavingIndex(Map<FieldDesc, Integer> havingIndex) {
        this.havingIndex = havingIndex;
    }

}
