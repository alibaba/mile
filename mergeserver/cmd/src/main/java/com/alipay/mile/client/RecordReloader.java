/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: LogDataLoader.java, v 0.1 2011-11-15 上午09:37:11 yuzhong.zhao Exp $
 */
public class RecordReloader {
    //用于查询和插入的client端
    private ApplationClientImpl applationClientImpl;

    //表名
    private String              tableName;

    //hash索引列
    private String[]        hashIndexes;

    //filter索引列
    private String[]        filterIndexes;

    private class SqlParamPair {
        // sql语句
        public String   sql;
        // param参数
        public Object[] params;
    }

    /**
     * 判断两行记录是否相等
     * 
     * @param record1       记录1
     * @param record2       记录2
     * @return
     */
    private boolean isRecordEqual(Map<String, Object> record1, Map<String, Object> record2) {
        if (null == record1 && null == record2) {
            return true;
        } else if (null == record1 || null == record2) {
            return false;
        } else if (record1.size() != record2.size()) {
            return false;
        } else {
            for (Entry<String, Object> entry : record1.entrySet()) {
                Object o1 = entry.getValue();
                Object o2 = record2.get(entry.getKey());
                if (null == o1 && null == o2) {
                    continue;
                } else if (null == o1 || null == o2) {
                    return false;
                } else if (o1.equals(o2)) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 构造插入sql
     * 
     * @param sqlMap                每列对应的参数
     * @return                      插入sql语句及对应的参数列表
     */
    private SqlParamPair genInsertSql(Map<String, Object> sqlMap) {
        SqlParamPair insertSql = new SqlParamPair();
        int i = 0;

        insertSql.sql = "insert into " + tableName + " ";
        insertSql.params = new Object[sqlMap.size()];

        for (Entry<String, Object> entry : sqlMap.entrySet()) {
            insertSql.sql = insertSql.sql + entry.getKey() + "=? ";
            insertSql.params[i++] = entry.getValue();
        }

        return insertSql;
    }

    /**
     * 构造查询sql
     * 
     * @param sqlMap                每列对应的参数
     * @return                      查询sql语句及对应的参数列表
     */
    private SqlParamPair genQuerySql(Map<String, Object> sqlMap) {
        int i;
        SqlParamPair querySql = new SqlParamPair();
        querySql.sql = "select ";
        querySql.params = new Object[hashIndexes.length + filterIndexes.length];

        i = 1;
        for (String field : sqlMap.keySet()) {
            if (i < sqlMap.keySet().size()) {
                querySql.sql = querySql.sql + field + ",";
            } else {
                querySql.sql = querySql.sql + field;
            }
            i++;
        }
        querySql.sql = querySql.sql + " from " + tableName + " indexwhere ";

        if (hashIndexes == null || hashIndexes.length == 0) {
            return null;
        }

        for (i = 0; i < hashIndexes.length; i++) {
            if (i == 0) {
                querySql.sql = querySql.sql + hashIndexes[i] + "=? ";
            } else {
                querySql.sql = querySql.sql + " and " + hashIndexes[i] + "=? ";
            }
            querySql.params[i] = sqlMap.get(hashIndexes[i]);
        }

        for (i = 0; i < filterIndexes.length; i++) {
            if (i == 0) {
                querySql.sql = querySql.sql + " where " + filterIndexes[i] + "=? ";
            } else {
                querySql.sql = querySql.sql + " and " + filterIndexes[i] + "=? ";
            }
            querySql.params[i + hashIndexes.length] = sqlMap.get(filterIndexes[i]);
        }

        return querySql;
    }

    /**
     * 向docserver中补录数据
     * 
     * @param sqlMap                    每列及对应的参数
     * @return                          返回1，表示数据补录成功；返回-1，表示数据补录失败；返回0，表示数据已经写入，不进行补录
     */
    protected int loadData(Map<String, Object> sqlMap) {

        SqlParamPair insertSql = genInsertSql(sqlMap);
        SqlParamPair querySql = genQuerySql(sqlMap);
        MileQueryResult mileQueryResult = null;
        MileInsertResult mileInsertResult = null;

        try {
            // 控制压力
            Thread.sleep(100);
            // 先进行查询，判断mile中是否已经存储了相应的记录
            mileQueryResult = applationClientImpl.preQueryForList(querySql.sql, querySql.params,
                3000);
            if (mileQueryResult.isSuccessful()) {
                List<Map<String, Object>> queryResult = mileQueryResult.getQueryResult();

                for (Map<String, Object> map : queryResult) {
                    if (isRecordEqual(map, sqlMap)) {
                        // 已经有记录了，不再进行补录
                        return 0;
                    }
                }
            }else{
                return -1;
            }

            // mile中没有对应的记录，进行补录
            mileInsertResult = applationClientImpl.preInsert(insertSql.sql, insertSql.params, 1000);
            if(!mileInsertResult.isSuccessful()){
                return -1;
            }

        } catch (Exception e) {
            return -1;
        }

        return 1;
    }

    public void setApplationClientImpl(ApplationClientImpl applationClientImpl) {
        this.applationClientImpl = applationClientImpl;
    }

    public ApplationClientImpl getApplationClientImpl() {
        return applationClientImpl;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String[] getHashIndexes() {
        return hashIndexes;
    }

    public void setHashIndexes(String[] hashIndexes) {
        this.hashIndexes = hashIndexes;
    }

    public String[] getFilterIndexes() {
        return filterIndexes;
    }

    public void setFilterIndexes(String[] filterIndexes) {
        this.filterIndexes = filterIndexes;
    }

}
