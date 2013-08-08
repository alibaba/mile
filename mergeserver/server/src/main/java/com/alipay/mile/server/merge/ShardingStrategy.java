/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.server.merge;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.CollectionUtils;

import com.alipay.mile.Constants;
import com.alipay.mile.Expression;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.OperatorExp;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.ColumnExp;
import com.alipay.mile.server.query.ColumnSubSelectExp;
import com.alipay.mile.server.query.DeleteStatement;
import com.alipay.mile.server.query.InsertStatement;
import com.alipay.mile.server.query.QueryStatement;
import com.alipay.mile.server.query.UpdateStatement;
import com.alipay.mile.server.query.ExportStatement;
import com.alipay.mile.server.sharding.DefaultShardConfig;

/**
 * 
 * @author yunliang.shi, yuzhong.zhao
 * @version $Id: ShardingStrategy.java, v 0.1 2011-7-6 下午02:09:47 yunliang.shi
 *          Exp $
 */
public class ShardingStrategy {

    /** sharding配置 */
    private final DefaultShardConfig  shard;

    /** 默认的节点列表 */
    private final Collection<Integer> nodeIDs;

    public ShardingStrategy(DefaultShardConfig shard, Collection<Integer> nodeIDs) {
        this.shard = shard;
        this.nodeIDs = nodeIDs;
    }

    @SuppressWarnings("unchecked")
    private Collection<Integer> expressionSharding(String tableName, Expression expression,
                                                   Map<Object, List<Object>> paramBindMap)
                                                                                          throws IllegalSqlException,
                                                                                          SqlExecuteException,
                                                                                          IOException,
                                                                                          InterruptedException,
                                                                                          ExecutionException {
        if (null == expression) {
            return nodeIDs;
        } else if (expression.isComposition()) {
            OperatorExp operatorExp = (OperatorExp) expression;
            switch (operatorExp.getOperator()) {
                case Constants.EXP_LOGIC_AND:
                    return (Collection<Integer>) CollectionUtils.intersection(expressionSharding(
                        tableName, expression.getLeft(), paramBindMap), expressionSharding(
                        tableName, expression.getRight(), paramBindMap));
                case Constants.EXP_LOGIC_OR:
                    return (Collection<Integer>) CollectionUtils.union(expressionSharding(
                        tableName, expression.getLeft(), paramBindMap), expressionSharding(
                        tableName, expression.getRight(), paramBindMap));
                default:
                    throw new IllegalSqlException("在进行sharding时出现异常，不支持的条件表达式!");
            }
        } else if (expression instanceof ColumnExp) {
            ColumnExp columnExp = (ColumnExp) expression;
            return doSharding(tableName, columnExp.column, columnExp.comparetor, columnExp
                .getBindParams(paramBindMap));
        } else if (expression instanceof ColumnSubSelectExp) {
            ColumnSubSelectExp subSelectExp = (ColumnSubSelectExp) expression;
            return doSharding(tableName, subSelectExp.column, subSelectExp.comparetor, subSelectExp
                .getBindParams(paramBindMap));
        } else {
            throw new IllegalSqlException("在进行sharding时出现异常，不支持的条件表达式!");
        }

    }

    /**
     * 
     * sharding运算返回 结果集
     * 
     * @param tableName
     * @param field
     * @param comparetor
     * @param paramList
     * @return
     * @throws SqlExecuteException 
     */
    private Collection<Integer> doSharding(String tableName, FieldDesc field, byte comparetor,
                                           List<Object> paramList) throws SqlExecuteException {
        Collection<Integer> resultSet = nodeIDs;

        if (field.isComputeField()) {
            // 对于计算列不做sharding
            return nodeIDs;
        } else if (!shard.isShardingColumn(tableName, field.fieldName)) {
            // 对应列不是sharding列, 不做sharding
            return nodeIDs;
        } else {
            resultSet = shard.expSharding(tableName, field.fieldName, comparetor, paramList);
        }

        return resultSet;
    }

    /**
     * 
     * 
     * @param tableName
     * @param documentValue
     * @return
     * @throws SqlExecuteException 
     */
    @SuppressWarnings("unchecked")
    public Collection<Integer> insertSharding(InsertStatement stmt,
                                              Map<Object, List<Object>> paramBindMap) throws SqlExecuteException {
        Collection<Integer> nodeIds = nodeIDs;
        Collection<Integer> resultSet = null;
        String tableName = stmt.tableName;
        List<Object> valueList;

        valueList = paramBindMap.get(stmt.documentValue);
        for (int i = 0; i < stmt.documentValue.size(); i++) {
            // 处理sharding列
            FieldDesc fieldDesc = stmt.documentValue.get(i).field;
            if (shard.isShardingColumn(tableName, fieldDesc.fieldName)) {
                resultSet = shard.equalSharding(tableName, fieldDesc.fieldName, valueList.get(i));
                if (null != resultSet) {
                    nodeIds = (Collection<Integer>) CollectionUtils.intersection(nodeIds, resultSet);
                }
            }
        }

        return nodeIds;
    }

    @SuppressWarnings("unchecked")
    public Collection<Integer> deleteSharding(DeleteStatement stmt,
                                              Map<Object, List<Object>> paramBindMap)
                                                                                     throws IllegalSqlException,
                                                                                     SqlExecuteException,
                                                                                     IOException,
                                                                                     InterruptedException,
                                                                                     ExecutionException {
        return (Collection<Integer>) CollectionUtils.intersection(expressionSharding(
            stmt.tableName, stmt.hashWhere, paramBindMap), expressionSharding(stmt.tableName,
            stmt.filterWhere, paramBindMap));
    }

	@SuppressWarnings("unchecked")
	public Collection<Integer> exportSharding(ExportStatement stmt,
											  Map<Object, List<Object>> paramBindMap)
                                                                                     throws IllegalSqlException,
                                                                                     SqlExecuteException,
                                                                                     IOException,
                                                                                     InterruptedException,
                                                                                     ExecutionException {
		return (Collection<Integer>) CollectionUtils.intersection(
			expressionSharding(stmt.tableName, stmt.hashWhere, paramBindMap),
			expressionSharding(stmt.tableName, stmt.filterWhere, paramBindMap));
	}	

    @SuppressWarnings("unchecked")
    public Collection<Integer> updateSharding(UpdateStatement stmt,
                                              Map<Object, List<Object>> paramBindMap)
                                                                                     throws IllegalSqlException,
                                                                                     SqlExecuteException,
                                                                                     IOException,
                                                                                     InterruptedException,
                                                                                     ExecutionException {
        return (Collection<Integer>) CollectionUtils.intersection(expressionSharding(
            stmt.tableName, stmt.hashWhere, paramBindMap), expressionSharding(stmt.tableName,
            stmt.filterWhere, paramBindMap));
    }

    @SuppressWarnings("unchecked")
    public Collection<Integer> querySharding(QueryStatement stmt,
                                             Map<Object, List<Object>> paramBindMap)
                                                                                    throws IllegalSqlException,
                                                                                    SqlExecuteException,
                                                                                    IOException,
                                                                                    InterruptedException,
                                                                                    ExecutionException {
        return (Collection<Integer>) CollectionUtils.intersection(expressionSharding(
            stmt.tableName, stmt.hashWhere, paramBindMap), expressionSharding(stmt.tableName,
            stmt.filterWhere, paramBindMap));
    }
}
