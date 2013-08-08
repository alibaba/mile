/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alipay.mile.message.TypeCode;
import com.alipay.mile.mileexception.ArgumentFormantException;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: TimeSingleShardingRule.java, v 0.1 2012-9-23 下午08:16:20 yuzhong.zhao Exp $
 */
public class TimeSingleShardingRule implements ShardingRule {
    private SingleShardingRule singleSharding;

    public void init(byte columnType, String ruleValue, String ruleResult)
                                                                          throws ArgumentFormantException {
        if (columnType != TypeCode.TC_INT_64) {
            throw new ArgumentFormantException("基于时间的单边sharding中，数据类型一定要是long!");
        }
        singleSharding = new SingleShardingRule();
        singleSharding.init(columnType, ruleValue, ruleResult);
    }

    /** 
     * @see com.alipay.mile.server.sharding.ShardingRule#equalSharding(java.lang.Object)
     */
    @Override
    public Set<Integer> equalSharding(Object value) {
        if (value instanceof Long) {
            Long time = (Long) value;
            return singleSharding.equalSharding(time - System.currentTimeMillis());
        }
        return null;
    }

    /** 
     * @see com.alipay.mile.server.sharding.ShardingRule#expSharding(byte, java.util.List)
     */
    @Override
    public Set<Integer> expSharding(byte comparator, List<Object> values) {
        List<Object> shiftValues = new ArrayList<Object>();
        for (Object value : values) {
            if (value instanceof Long) {
                Long shiftValue = Long.valueOf((Long) value - System.currentTimeMillis());
                shiftValues.add(shiftValue);
            } else {
                return null;
            }
        }
        return singleSharding.expSharding(comparator, shiftValues);
    }

    /**
     * Setter method for property <tt>singleSharding</tt>.
     * 
     * @param singleSharding value to be assigned to property singleSharding
     */
    public void setSingleSharding(SingleShardingRule singleSharding) {
        this.singleSharding = singleSharding;
    }

    /**
     * Getter method for property <tt>singleSharding</tt>.
     * 
     * @return property value of singleSharding
     */
    public SingleShardingRule getSingleSharding() {
        return singleSharding;
    }

}
