/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.merge;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;

import com.alipay.mile.Config;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.communication.MileClient;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.plan.ExecutePlan;
import com.alipay.mile.plan.MessageProcessor;
import com.alipay.mile.plan.SqlAnalyzer;
import com.alipay.mile.server.T2.mileSqlConditionLexer;
import com.alipay.mile.server.T2.mileSqlConditionParser;
import com.alipay.mile.server.T2.milesqlLexer;
import com.alipay.mile.server.T2.milesqlParser;
import com.alipay.mile.server.query.Statement;
import com.alipay.mile.server.query.special.SpecifyQuery;
import com.alipay.mile.server.query.special.SpecifyQueryStatement;
import com.alipay.mile.server.sharding.DefaultShardConfig;

/**
 * Merge Server : receive sql string parse sql to Query analyze Query with
 * schema info to Access dispatch access invoke to Docuemnet Server merge all
 * return result set get document addition info return result to client
 *
 * @author huabing.du
 *
 */
public class Merger {

    private static final Logger                           LOGGER = Logger.getLogger(Merger.class
                                                                     .getName());

    /** sql 缓存 */
    private BoundedConcurrentHashMap<String, ExecutePlan> sqlCache;

    private SqlAnalyzer                                   sqlAnalyzer;

    public Merger(MileClient mileClient, DefaultShardConfig shard) {
        MessageProcessor messageProcessor = new MessageProcessor(mileClient, shard);
        this.sqlCache = new BoundedConcurrentHashMap<String, ExecutePlan>(Config.getSqlCacheCount());
        this.sqlAnalyzer = new SqlAnalyzer(messageProcessor);
    }

    /**
     *
     * @param sql
     * @return
     * @throws RecognitionException
     * @throws Exception
     * anrlr 解析sql 语句转换成statement
     */
    private Statement parse(String sql) throws RecognitionException {
        ANTLRStringStream in = new ANTLRStringStream(sql);
        milesqlLexer lexer = new milesqlLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        milesqlParser parser = new milesqlParser(tokens);
        milesqlParser.sql_stmt_return ret = parser.sql_stmt();

        return ret.statement;
    }

    /**
     * parse SQL condition.
     * @param condition
     * @return
     * @throws RecognitionException
     */
    private Statement parseCondition(String condition) throws RecognitionException {
        ANTLRStringStream in = new ANTLRStringStream(condition);
        mileSqlConditionLexer lexer = new mileSqlConditionLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        mileSqlConditionParser parser = new mileSqlConditionParser(tokens);
        parser.sql_stmt();

        return parser.statement;
    }

    /**
     *
     * @param sql
     * @return
     * @throws RecognitionException
     * @throws SqlExecuteException
     * @throws IllegalSqlException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws Throwable
     * sql执行器
     */
    public SqlResultSet execute(String sql, int timeOut)
                                                                        throws IllegalSqlException,
                                                                        SqlExecuteException,
                                                                        IOException,
                                                                        InterruptedException,
                                                                        ExecutionException,
                                                                        RecognitionException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("开始执行sql: " + sql);
        }
        return (SqlResultSet) sqlAnalyzer.analyze(parse(sql)).execute(timeOut, null);

    }

    /**
     * Execute specify query.
     * @param query
     * @param timeOut
     * @return
     * @throws IllegalSqlException
     * @throws SqlExecuteException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws RecognitionException
     */
    public Integer executeSpecifyQuery(long sessionId, SpecifyQuery query, int timeOut)
                                                                                       throws IllegalSqlException,
                                                                                       SqlExecuteException,
                                                                                       IOException,
                                                                                       InterruptedException,
                                                                                       ExecutionException,
                                                                                       RecognitionException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("开始执行specify: " + query.getTable() + " " + query.getCondition());
        }
        Statement stmt = parseCondition(query.getCondition());
        stmt.tableName = query.getTable();
        if (stmt instanceof SpecifyQueryStatement) {
            ((SpecifyQueryStatement) stmt).subSelects = query.getSubSelect();
        }
        ExecutePlan executePlan = sqlAnalyzer.analyze(stmt);
        Integer result = (Integer) executePlan.execute(timeOut, null);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("执行sql完成!");
        }
        return result;
    }

    /**
     * 带?号的预编译sql 执行器
     * @param sql
     * @param params
     * @return
     * @throws RecognitionException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @throws IllegalSqlException
     * @throws Throwable
     */
    public SqlResultSet execute(String sql, Object[] params, int timeOut)
                                                                                         throws RecognitionException,
                                                                                         SqlExecuteException,
                                                                                         IOException,
                                                                                         InterruptedException,
                                                                                         ExecutionException,
                                                                                         IllegalSqlException {
        //在sql缓存中检索sql
        ExecutePlan executePlan = sqlCache.get(sql);

        if (null == executePlan) {
            executePlan = sqlAnalyzer.analyze(parse(sql));
            sqlCache.put(sql, executePlan);
        }
        return (SqlResultSet) executePlan.execute(timeOut, params);
    }

    public SqlAnalyzer getSqlAnalyzer() {
        return sqlAnalyzer;
    }

    public void setSqlAnalyzer(SqlAnalyzer sqlAnalyzer) {
        this.sqlAnalyzer = sqlAnalyzer;
    }

    public BoundedConcurrentHashMap<String, ExecutePlan> getsqlCache() {
        return sqlCache;
    }

    public void setsqlCache(BoundedConcurrentHashMap<String, ExecutePlan> sqlCache) {
        this.sqlCache = sqlCache;
    }

}
