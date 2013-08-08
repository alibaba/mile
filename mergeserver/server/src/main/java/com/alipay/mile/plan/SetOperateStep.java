/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.Constants;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.SetOperatorExpression;

/**
 *
 * @author bin.lb, yuzhong.zhao
 */
public class SetOperateStep implements ExecuteStep {
    // set operate expression
    private SetOperatorExpression setExpression;

    public SetOperateStep(SetOperatorExpression setExpression) {
        this.setExpression = setExpression;
    }

    @Override
    public Object execute(Object input, Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                     throws SqlExecuteException,
                                                                                     IOException,
                                                                                     InterruptedException,
                                                                                     ExecutionException,
                                                                                     IllegalSqlException {
        SqlResultSet leftResult = (SqlResultSet) paramBindMap.get(setExpression.getLeft()).get(0);
        SqlResultSet rightResult = (SqlResultSet) paramBindMap.get(setExpression.getRight()).get(0);
        
        if (leftResult.fields.size() != rightResult.fields.size()) {
            throw new SqlExecuteException(
                "set operation's left column number differ with the right column");
        }

        switch (setExpression.operator) {
            case Constants.EXP_UNIONSET:
                leftResult.data.removeAll(rightResult.data);
                leftResult.data.addAll(rightResult.data);
                break;
            case Constants.EXP_INTERSECTION:
                leftResult.data.retainAll(rightResult.data);
                break;
            default:
                throw (new SqlExecuteException("unknown set operator: " + setExpression.operator));
        }

        return leftResult;
    }

    public SetOperatorExpression getSetExpression() {
        return setExpression;
    }

    public void setSetExpression(SetOperatorExpression setExpression) {
        this.setExpression = setExpression;
    }
}