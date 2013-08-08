/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alipay.mile.Constants;
import com.alipay.mile.mileexception.ArgumentFormantException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: ModShardingRule.java, v 0.1 2012-9-21 下午03:00:41 yuzhong.zhao Exp $
 */
public class ModShardingRule implements ShardingRule {
    /** 模参数 */
    private long               modValue       = 0;
    /** 取模位置 */
    private int[]              modPosition    = null;
    /** 节点列表  */
    private List<Set<Integer>> shardingResult = null;

    private int stringHash(String str) throws UnsupportedEncodingException {
        int hashCode = 1315423911;
        byte[] strBytes = str.getBytes("utf-8");

        for (int i = 0; i < strBytes.length; i++) {
            hashCode ^= ((hashCode << 5) + (strBytes[i]) + (hashCode >> 2));
        }

        return (hashCode & 0x7FFFFFFF);
    }

    private int calcModIndex(Integer value) throws UnsupportedEncodingException {
        return calcModIndex(value.longValue());
    }

    private int calcModIndex(Long value) throws UnsupportedEncodingException {
        if (modPosition.length == 0) {
            return (int) (value % modValue);
        } else {
            return calcModIndex(value.toString());
        }
    }

    private int calcModIndex(String str) throws UnsupportedEncodingException {
        long value = 0;
        if (modPosition.length == 0) {
            value = stringHash(str);
        } else {
            for (int i = 0; i < modPosition.length; i++) {
                int index = modPosition[i];
                if (index < 0) {
                    index += str.length();
                }
                if (index >= str.length() || index < 0) {
                    throw new StringIndexOutOfBoundsException("index: " + index + ", string: "
                                                              + str);
                }
                char c = str.charAt(index);
                if (!Character.isDigit(c)) {
                    throw new NumberFormatException("Character: " + c + ", string: " + str);
                }
                value = value * 10 + Character.digit(c, 10);
            }
        }

        return (int) (value % modValue);
    }

    public void init(byte columnType, String position, String ruleValue, String ruleResult)
                                                                                           throws ArgumentFormantException {
        modValue = Long.parseLong(ruleValue);

        if (modValue <= 0) {
            throw new ArgumentFormantException("取模sharding时，所模的数值要大于0!");
        }

        if (null != position) {
            String[] pos = position.split(",");
            modPosition = new int[pos.length];
            for (int i = 0; i < pos.length; i++) {
                modPosition[i] = Integer.parseInt(pos[i]);
            }
        } else {
            modPosition = new int[0];
        }

        shardingResult = new ArrayList<Set<Integer>>();
        for (String ss : ruleResult.split(",")) {
            String[] nodes = ss.split(":");
            Set<Integer> nodeGroup = new HashSet<Integer>();
            for (String node : nodes) {
                nodeGroup.add(Integer.parseInt(node));
            }
            shardingResult.add(nodeGroup);
        }
    }

    /** 
     * @throws SqlExecuteException 
     * @see com.alipay.mile.server.sharding.ShardingRule#equalSharding(java.lang.Object)
     */
    @Override
    public Set<Integer> equalSharding(Object value) throws SqlExecuteException {
        Set<Integer> resultSet = new HashSet<Integer>();
        int index = -1;

        try {
            if (value instanceof Long) {
                index = calcModIndex((Long) value);
            } else if (value instanceof Integer) {
                index = calcModIndex((Integer) value);
            } else if (value instanceof String) {
                index = calcModIndex((String) value);
            }
        } catch (Exception e) {
            throw new SqlExecuteException("在执行等值sharding时出错", e);
        }

        if (index >= 0) {
            resultSet.addAll(shardingResult.get(index));
        }

        return resultSet;
    }

    /** 
     * @throws SqlExecuteException 
     * @see com.alipay.mile.server.sharding.ShardingRule#expSharding(byte, java.util.List)
     */
    @Override
    public Set<Integer> expSharding(byte comparator, List<Object> values)
                                                                         throws SqlExecuteException {
        Set<Integer> resultSet = new HashSet<Integer>();

        try {
            switch (comparator) {
                case Constants.EXP_COMPARE_EQUALS:
                    return equalSharding(values.get(0));
                case Constants.EXP_COMPARE_IN:
                    for (Object value : values) {
                        Set<Integer> ruleResult = equalSharding(value);
                        if (null != ruleResult) {
                            resultSet.addAll(ruleResult);
                        }
                    }
                    break;
                default:
                    for (Set<Integer> ruleResult : shardingResult) {
                        resultSet.addAll(ruleResult);
                    }
            }
        } catch (Exception e) {
            throw new SqlExecuteException("在执行表达式值sharding时出错", e);
        }

        return resultSet;
    }

    public long getModValue() {
        return modValue;
    }

    public void setModValue(long modValue) {
        this.modValue = modValue;
    }

    public int[] getModPosition() {
        return modPosition;
    }

    public void setModPosition(int[] modPosition) {
        this.modPosition = modPosition;
    }
}
