/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.OrderDesc;

/**
 * 聚组步骤，对查询结果进行排序
 *
 * @author yuzhong.zhao
 * @version $Id: OrderStep.java,v 0.1 2011-5-15 06:59:10 yunliang.shi Exp $
 */
public class OrderStep implements ExecuteStep {
    // 要进行排序的列
    private List<OrderDesc> orderFeilds;
    // 排序比较子
    private OrderComparator orderComparator;

    public OrderStep(List<OrderDesc> orderFields, List<FieldDesc> selectFields)
                                                                               throws SqlExecuteException {
        this.orderFeilds = orderFields;
        this.orderComparator = new OrderComparator(orderFeilds, selectFields);
    }

    /**
     * @param sessionId    session号
     * @param input        排序步的输入是SqlResultSet
     * @return             排序步的输出是排序后的SqlResultSet
     * 
     * @throws SqlExecuteException
     * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object)
     */
    public Object execute(Object input, Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                     throws SqlExecuteException {
        // 检查输入合法性
        if (null == input) {
            throw new SqlExecuteException("执行Order step时输入为空");
        }
        if (!(input instanceof SqlResultSet)) {
            throw new SqlExecuteException("执行Order step时输入不是SqlResultSet类型");
        }

        SqlResultSet resultSet = (SqlResultSet) input;

        List<Record> data = new ArrayList<Record>(resultSet.data);
        Collections.sort(data, orderComparator);
        resultSet.data = data;

        return resultSet;
    }

    public List<OrderDesc> getOrderFeilds() {
        return orderFeilds;
    }

    public void setOrderFeilds(List<OrderDesc> orderFeilds) {
        this.orderFeilds = orderFeilds;
    }

    public OrderComparator getOrderComparator() {
        return orderComparator;
    }

    public void setOrderComparator(OrderComparator orderComparator) {
        this.orderComparator = orderComparator;
    }

}
