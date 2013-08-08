/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alipay.mile.Constants;
import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: SingleShardingRule.java, v 0.1 2012-9-21 下午03:00:00 yuzhong.zhao Exp $
 */
public class SingleShardingRule implements ShardingRule {
    /** 单值比较子 */
    private byte         singleComparator = 0;
    /** 值 */
    private Object       singleValue = null;
    /** 节点列表  */
    private Set<Integer> shardingResult = null;

    public void init(byte columnType, String ruleValue, String ruleResult) {
        String[] strData;
        String valueData;

        //解析单值
        if (ruleValue.startsWith(">=")) {
            singleComparator = Constants.EXP_COMPARE_GET;
            valueData = ruleValue.substring(2);
        } else if (ruleValue.startsWith(">")) {
            singleComparator = Constants.EXP_COMPARE_GT;
            valueData = ruleValue.substring(1);
        } else if (ruleValue.startsWith("<=")) {
            singleComparator = Constants.EXP_COMPARE_LET;
            valueData = ruleValue.substring(2);
        } else if (ruleValue.startsWith("<")) {
            singleComparator = Constants.EXP_COMPARE_LT;
            valueData = ruleValue.substring(1);
        } else {
            singleComparator = Constants.EXP_COMPARE_EQUALS;
            valueData = ruleValue;
        }
        singleValue = ByteConveror.preString2value(columnType, valueData);

        //解析sharding节点集
        strData = ruleResult.split(",");
        shardingResult = new HashSet<Integer>();
        for (String str : strData) {
            shardingResult.add(Integer.parseInt(str));
        }
    }

    @SuppressWarnings("unchecked")
    private boolean interSects(Object value, byte compar) {
        Comparable data;
        if (value instanceof Comparable) {
            data = (Comparable) value;
        } else {
            return false;
        }

        if (compar == Constants.EXP_COMPARE_GET || compar == Constants.EXP_COMPARE_GT) {
            // >= data, >data
            // = singleValue, >= singleVaue...
            switch (singleComparator) {
                case Constants.EXP_COMPARE_EQUALS:
                    return data.compareTo(singleValue) < 0
                           || (compar == Constants.EXP_COMPARE_GET && data.compareTo(singleValue) == 0);
                case Constants.EXP_COMPARE_GET:
                    return true;
                case Constants.EXP_COMPARE_GT:
                    return true;
                case Constants.EXP_COMPARE_LET:
                    return data.compareTo(singleValue) < 0
                           || (compar == Constants.EXP_COMPARE_GET && data.compareTo(singleValue) == 0);
                case Constants.EXP_COMPARE_LT:
                    return data.compareTo(singleValue) < 0;
                default:
                    return false;
            }
        } else if (compar == Constants.EXP_COMPARE_LET || compar == Constants.EXP_COMPARE_LT) {
            // <= data
            switch (singleComparator) {
                case Constants.EXP_COMPARE_EQUALS:
                    return data.compareTo(singleValue) > 0
                           || (compar == Constants.EXP_COMPARE_LET && data.compareTo(singleValue) == 0);
                case Constants.EXP_COMPARE_GET:
                    return data.compareTo(singleValue) > 0
                           || (compar == Constants.EXP_COMPARE_LET && data.compareTo(singleValue) == 0);
                case Constants.EXP_COMPARE_GT:
                    return data.compareTo(singleValue) > 0;
                case Constants.EXP_COMPARE_LET:
                    return true;
                case Constants.EXP_COMPARE_LT:
                    return true;
                default:
                    return false;
            }
        } else if (compar == Constants.EXP_COMPARE_EQUALS) {
            switch (singleComparator) {
                case Constants.EXP_COMPARE_EQUALS:
                    return data.compareTo(singleValue) == 0;
                case Constants.EXP_COMPARE_GET:
                    return data.compareTo(singleValue) >= 0;
                case Constants.EXP_COMPARE_GT:
                    return data.compareTo(singleValue) > 0;
                case Constants.EXP_COMPARE_LET:
                    return data.compareTo(singleValue) <= 0;
                case Constants.EXP_COMPARE_LT:
                    return data.compareTo(singleValue) < 0;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    /** 
     * @see com.alipay.mile.server.sharding.ShardingRule#equalSharding(java.lang.Object)
     */
    @Override
    public Set<Integer> equalSharding(Object value) {
        if (interSects(value, Constants.EXP_COMPARE_EQUALS)) {
            return shardingResult;
        }
        return null;
    }

    /** 
     * @see com.alipay.mile.server.sharding.ShardingRule#expSharding(byte, java.util.List)
     */
    @Override
    public Set<Integer> expSharding(byte comparator, List<Object> values) {
        boolean flag = false;

        switch (comparator) {
            case Constants.EXP_COMPARE_EQUALS:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_EQUALS);
                break;
            case Constants.EXP_COMPARE_GET:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_GET);
                break;
            case Constants.EXP_COMPARE_GT:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_GT);
                break;
            case Constants.EXP_COMPARE_LET:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_LET);
                break;
            case Constants.EXP_COMPARE_LT:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_LT);
                break;
            case Constants.EXP_COMPARE_BETWEEN_LEG:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_GET)
                       && interSects(values.get(1), Constants.EXP_COMPARE_LT);
                break;
            case Constants.EXP_COMPARE_BETWEEN_LEGE:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_GET)
                       && interSects(values.get(1), Constants.EXP_COMPARE_LET);
                break;
            case Constants.EXP_COMPARE_BETWEEN_LG:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_GT)
                       && interSects(values.get(1), Constants.EXP_COMPARE_LT);
                break;
            case Constants.EXP_COMPARE_BETWEEN_LGE:
                flag = interSects(values.get(0), Constants.EXP_COMPARE_GT)
                       && interSects(values.get(1), Constants.EXP_COMPARE_LET);
                break;
            case Constants.EXP_COMPARE_IN:
                for (Object value : values) {
                    if (interSects(value, Constants.EXP_COMPARE_EQUALS)) {
                        flag = true;
                        break;
                    }
                }
                break;
            default:
                return null;
        }
        if (flag) {
            return shardingResult;
        }
        return null;
    }

    public byte getSingleComparator() {
        return singleComparator;
    }

    public void setSingleComparator(byte singleComparator) {
        this.singleComparator = singleComparator;
    }

    public Object getSingleValue() {
        return singleValue;
    }

    public void setSingleValue(Object singleValue) {
        this.singleValue = singleValue;
    }

    public Set<Integer> getShardingResult() {
        return shardingResult;
    }

    public void setShardingResult(Set<Integer> shardingResult) {
        this.shardingResult = shardingResult;
    }

}
