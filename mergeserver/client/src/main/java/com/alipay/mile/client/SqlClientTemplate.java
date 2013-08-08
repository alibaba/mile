/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileExportResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.client.result.MileUpdateResult;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * @author jin.qian
 * @version $Id: SqlClientTemplate.java,v 0.1 2011-4-6 下午05:50:33 jin.qian Exp $
 */
public interface SqlClientTemplate {

    /**
     * 预编译sql执行
     * @param sql
     * @param params
     * @param timeOut
     * @return
     * @throws Exception
     */
    public MileQueryResult preQueryForList(String sql, Object[] params, int timeOut)
                                                                                    throws SqlExecuteException,
                                                                                    IOException,
                                                                                    InterruptedException,
                                                                                    ExecutionException;

    /**
     * 预编译sql执行
     * @param sql
     * @param params
     * @param timeOut
     * @return
     * @throws Exception
     */
    public MileUpdateResult preUpdate(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException;

    /**
     * 预编译sql执行
     * @param sql
     * @param params
     * @param timeOut
     * @return
     * @throws Exception
     */
    public MileInsertResult preInsert(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException;

    /**
     * 预编译sql执行
     * @param sql
     * @param params
     * @param timeOut
     * @return
     * @throws Exception
     */
    public MileDeleteResult preDelete(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException;

    /**
     * 为ctu后台的查询需求所做的临时接口，不允许其他系统使用，后期会抽象出通用的接口，此接口会逐步废弃掉
     * 统计按用户过滤出来的记录总数
     * 
     * @param tableName         表名，例如"t"
     * @param condition         条件字段，例如"seghint(0, 100, 0, 0) indexwhere CP=? where GR>? and GR<?"
     * @param clusterField      要聚集的列，例如"UD"
     * @param topField          挑选top记录所根据的列，例如"max(GR)"或者"min(GR)"
     * @param params            参数列表
     * @param timeOut           超时
     * @return
     */
    public int preCtuClusterCountQuery(String tableName, String condition, String clusterField,
                                       String topField, Object[] params, int timeOut)
                                                                                     throws SqlExecuteException,
                                                                                     IOException,
                                                                                     InterruptedException,
                                                                                     ExecutionException;

    /**
     * 为ctu后台的查询需求所做的临时接口，不允许其他系统使用，后期会抽象出通用的接口，此接口会逐步废弃掉
     * 分页显示按用户过滤的查询结果
     * 
     * @param tableName         表名
     * @param selectFields      要选择的列
     * @param condition         条件字段，例如"seghint(0, 100, 0, 0) indexwhere CP=? where GR>? and GR<?"
     * @param clusterField      要聚集的列，例如"UD"
     * @param topField          挑选top记录所根据的列，例如"max(GR)"或者"min(GR)"
     * @param orderField        要排序的列，例如"GR"
     * @param orderType         排序类型，顺序/倒序，true表示顺序，false表示倒序
     * @param limit             
     * @param offset            limit=100,offset=90时表示第91条记录到第100条记录
     * @param params            参数列表
     * @param timeOut           超时，单位为ms，例如3000表示超时时间为3秒
     * @return                  查询结果集
     * @throws Exception
     */
    public MileQueryResult preCtuClusterQuery(String tableName, List<String> selectFields,
                                              String condition, String clusterField,
                                              String topField, String orderField,
                                              boolean orderType, int limit, int offset,
                                              Object[] params, int timeOut)
                                                                           throws SqlExecuteException,
                                                                           IOException,
                                                                           InterruptedException,
                                                                           ExecutionException;

    /**
     * 执行预编译export语句
     * @param sql
     * @param params
     * @param timeOut
     * @return
     * @throws SqlExecuteException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     */
	MileExportResult preExport(String sql, Object[] params, int timeOut)
			throws SqlExecuteException, IOException, InterruptedException,
			ExecutionException;

}
