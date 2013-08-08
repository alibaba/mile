/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alipay.mile.message.TypeCode;
import com.alipay.mile.mileexception.ArgumentFormantException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 *
 * @author jin.qian
 * @version $Id: DefaultShardConfig.java, v 0.1 2011-4-20 下午04:38:37 jin.qian
 *          Exp $
 */
public class DefaultShardConfig {

    /** 规则匹配 */
    private final Map<String, TableRule> shardingMapping = new HashMap<String, TableRule>();
    /** 配置文件 */
    private final File                   filePath;

    public DefaultShardConfig(File filePath) throws IOException, ArgumentFormantException {
        this.filePath = filePath;
        init();
    }

    private void init() throws IOException, ArgumentFormantException {
        initShardMap(filePath);
    }

    /**
     *
     * @param filePath
     * @throws IOException
     *             加载配置规则文件
     * @throws SqlExecuteException 
     */
    private void initShardMap(File file) throws IOException, ArgumentFormantException {
        // 读文件
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(
            new FileInputStream(file)), "UTF8"));
        String line = null;
        TableRule tableRule = null;
        ShardingRule shardingRule = null;

        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#")) {
                String[] tokens = line.split("\\s+");
                if (tokens.length != 6) {
                    continue;
                }
                String tableName = tokens[0];
                String columnName = tokens[1];
                String columnTypeDec = tokens[2];
                String ruleType = tokens[3];
                String ruleValue = tokens[4];
                String ruleResultNode = tokens[5];
                tableRule = shardingMapping.get(tableName);
                byte columnType = TypeCode.getTCByName(columnTypeDec);
                if (tableRule == null) {
                    tableRule = new TableRule();
                    shardingMapping.put(tableName, tableRule);
                }

                switch (Integer.parseInt(ruleType.split(":")[0])) {
                    case ShardConstants.TYPE_SINGLE:
                        shardingRule = new SingleShardingRule();
                        ((SingleShardingRule) shardingRule).init(columnType, ruleValue,
                            ruleResultNode);
                        break;
                    case ShardConstants.TYPE_BETWEEN:
                        shardingRule = new BetweenShardingRule();
                        ((BetweenShardingRule) shardingRule).init(columnType, ruleValue,
                            ruleResultNode);
                        break;
                    case ShardConstants.TYPE_MODULO:
                        shardingRule = new ModShardingRule();
                        if (ruleType.split(":").length == 2) {
                            ((ModShardingRule) shardingRule).init(columnType,
                                ruleType.split(":")[1], ruleValue, ruleResultNode);
                        }else{
                            ((ModShardingRule) shardingRule).init(columnType,
                                null, ruleValue, ruleResultNode);
                        }
                        break;
                    case ShardConstants.TYPE_TIME_SINGLE:
                        shardingRule = new TimeSingleShardingRule();
                        ((TimeSingleShardingRule) shardingRule).init(columnType, ruleValue,
                            ruleResultNode);
                        break;
                    default:
                        throw new ArgumentFormantException("无法识别的sharding规则");
                }

                List<ShardingRule> ls = tableRule.getColumnRules().get(columnName);
                if (ls == null) {
                    ls = new ArrayList<ShardingRule>();
                    ls.add(shardingRule);
                    tableRule.getColumnRules().put(columnName, ls);
                } else {
                    ls.add(shardingRule);
                }
            }
        }
    }

    public boolean isShardingColumn(String tableName, String columnName) {
        if (shardingMapping.get(tableName) == null) {
            return false;
        }

        List<ShardingRule> rules = shardingMapping.get(tableName).getColumnRules().get(columnName);

        if (null != rules && rules.size() > 0) {
            return true;
        }
        return false;
    }

    public Set<Integer> equalSharding(String tableName, String columnName, Object value) throws SqlExecuteException {
        TableRule tableRule = shardingMapping.get(tableName);
        Set<Integer> result = new HashSet<Integer>();

        if (null == tableRule) {
            return result;
        } else {
            List<ShardingRule> columnRules = tableRule.getColumnRules().get(columnName);
            if (null == columnRules) {
                return result;
            } else {
                for (ShardingRule rule : columnRules) {
                    Set<Integer> ruleResult = rule.equalSharding(value);
                    if (null != ruleResult && !ruleResult.isEmpty()) {
                        result.addAll(ruleResult);
                    }
                }
            }
        }
        return result;
    }

    public Set<Integer> expSharding(String tableName, String columnName, byte comparator,
                                    List<Object> values) throws SqlExecuteException {
        TableRule tableRule = shardingMapping.get(tableName);
        Set<Integer> result = new HashSet<Integer>();

        if (null == tableRule) {
            return result;
        } else {
            List<ShardingRule> columnRules = tableRule.getColumnRules().get(columnName);
            if (null == columnRules) {
                return result;
            } else {
                for (ShardingRule rule : columnRules) {
                    Set<Integer> ruleResult = rule.expSharding(comparator, values);
                    if (null != ruleResult && !ruleResult.isEmpty()) {
                        result.addAll(ruleResult);
                    }
                }
            }
        }
        return result;
    }

}
