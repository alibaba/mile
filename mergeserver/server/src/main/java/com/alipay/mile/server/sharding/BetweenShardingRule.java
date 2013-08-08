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
 * @version $Id: BetweenShardingRule.java, v 0.1 2012-9-21 下午04:40:17 yuzhong.zhao Exp $
 */
public class BetweenShardingRule implements ShardingRule {
    /** 左比较子 */
    private byte          leftComparator = 0;
    /** 左值 */
    private Comparable<?> leftValue = null;
    /** 右比较子 */
    private byte          rightComparator = 0;
    /** 右值 */
    private Comparable<?> rightValue = null;
    /** 节点列表  */
    private Set<Integer>  shardingResult = null;

    
    
    
    
    @SuppressWarnings("unchecked")
    private boolean interSects(Object value, byte compar) {
        Comparable data;
        if (value instanceof Comparable) {
            data = (Comparable) value;
        } else {
            return false;
        }

        if (compar == Constants.EXP_COMPARE_GET || compar == Constants.EXP_COMPARE_GT) {
            // y >= data, 需要比较data和rightValue的大小
            if (rightComparator == Constants.EXP_COMPARE_LET
                && compar == Constants.EXP_COMPARE_GET) {
                return data.compareTo(rightValue) <= 0;
            } else {
                return data.compareTo(rightValue) < 0;
            }
        } else if (compar == Constants.EXP_COMPARE_LET || compar == Constants.EXP_COMPARE_LT) {
            // y <= data, 需要比较data和leftValue的大小
            if (leftComparator == Constants.EXP_COMPARE_GET
                && compar == Constants.EXP_COMPARE_LET) {
                return data.compareTo(leftValue) >= 0;
            } else {
                return data.compareTo(leftValue) > 0;
            }
        } else if (compar == Constants.EXP_COMPARE_EQUALS) {
            // y == data, 需要比较data和rightValue, data和leftValue的大小
            if (leftComparator == Constants.EXP_COMPARE_GET
                && rightComparator == Constants.EXP_COMPARE_LET) {
                return data.compareTo(leftValue) >= 0 && data.compareTo(rightValue) <= 0;
            } else if (leftComparator == Constants.EXP_COMPARE_GET
                       && rightComparator == Constants.EXP_COMPARE_LT) {
                return data.compareTo(leftValue) >= 0 && data.compareTo(rightValue) < 0;
            } else if (leftComparator == Constants.EXP_COMPARE_GT
                       && rightComparator == Constants.EXP_COMPARE_LET) {
                return data.compareTo(leftValue) > 0 && data.compareTo(rightValue) <= 0;
            } else if (leftComparator == Constants.EXP_COMPARE_GT
                       && rightComparator == Constants.EXP_COMPARE_LT) {
                return data.compareTo(leftValue) > 0 && data.compareTo(rightValue) < 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    
    public void init(byte columnType, String ruleValue, String ruleResult) {
        //匹配 数值 between () []
        String strTemp = ruleValue.substring(1, ruleValue.length() - 1);
        String[] strData = strTemp.split(",");
        if (ruleValue.startsWith("(")) {
            this.leftComparator = Constants.EXP_COMPARE_GT;
        } else if (ruleValue.startsWith("[")) {
            this.leftComparator = Constants.EXP_COMPARE_GET;
        }
        this.leftValue = (Comparable<?>) ByteConveror.preString2value(columnType, strData[0]);
        if (ruleValue.endsWith(")")) {
            this.rightComparator = Constants.EXP_COMPARE_LT;
        } else if (ruleValue.endsWith("]")) {
            this.rightComparator = Constants.EXP_COMPARE_LET;
        }
        this.rightValue = (Comparable<?>) ByteConveror.preString2value(columnType, strData[1]);
        
        
        strData = ruleResult.split(",");
        shardingResult = new HashSet<Integer>();
        for(String str : strData){
            shardingResult.add(Integer.parseInt(str));
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
                for(Object value : values){
                    if(interSects(value, Constants.EXP_COMPARE_EQUALS)){
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

    
    
    public byte getLeftComparator() {
        return leftComparator;
    }

    public void setLeftComparator(byte leftComparator) {
        this.leftComparator = leftComparator;
    }

    public Comparable<?> getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(Comparable<?> leftValue) {
        this.leftValue = leftValue;
    }

    public byte getRightComparator() {
        return rightComparator;
    }

    public void setRightComparator(byte rightComparator) {
        this.rightComparator = rightComparator;
    }

    public Comparable<?> getRightValue() {
        return rightValue;
    }

    public void setRightValue(Comparable<?> rightValue) {
        this.rightValue = rightValue;
    }

    public Set<Integer> getShardingResult() {
        return shardingResult;
    }

    public void setShardingResult(Set<Integer> shardingResult) {
        this.shardingResult = shardingResult;
    }



}
