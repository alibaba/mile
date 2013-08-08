/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.alipay.mile.Constants;
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.client.result.MileUpdateResult;
import com.alipay.mile.client.result.MileExportResult;
import com.alipay.mile.communication.ApplationClientService;
import com.alipay.mile.communication.MileClient;
import com.alipay.mile.communication.SendFuture;
import com.alipay.mile.communication.ServerRef;
import com.alipay.mile.message.ClientConnectMessage;
import com.alipay.mile.message.CommonErrorMessage;
import com.alipay.mile.message.CommonOkMessage;
import com.alipay.mile.message.KeyValueData;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.SqlExectueErrorMessage;
import com.alipay.mile.message.SqlExecuteRsMessage;
import com.alipay.mile.message.SqlPreExecuteMessage;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.util.SimpleThreadFactory;

/**
 * 应用Client端
 * 
 * @author jin.qian
 * @version $Id: ApplationClientImpl.java,v 0.1 2011-4-6 下午05:42:28 jin.qian Exp
 */
public class ApplationClientImpl implements SqlClientTemplate {

    private static final Logger            LOGGER              = Logger
                                                                   .getLogger(ApplationClientImpl.class
                                                                       .getName());
    /** mergeServer列表 */
    private List<String>                   mergeServerList;
    /** 可用服务器列表 */
    private MileClient                     mileClient;
    /** 协议版本号 */
    private short                          version             = Constants.VERSION;
    /** 用户名 */
    private String                         userName;
    /** 口令 */
    private byte[]                         passWord;
    /** client参数 */
    private List<KeyValueData>             clientProperty;
    /** 侦听连接线程数 */
    private int                            bossExecutorCount   = 0;
    /** 通讯工作线数 */
    private int                            workerExecutorCount = 0;

    /** 检测服务器状态调度器 */
    private final ScheduledExecutorService scheduler           = Executors
                                                                   .newScheduledThreadPool(
                                                                       2,
                                                                       new SimpleThreadFactory(
                                                                           "CheckMergeServerThreadGroup",
                                                                           true));



    /**
     * 初始化配置
     * 
     * @throws UnsupportedEncodingException
     */
    public void init() throws UnsupportedEncodingException {

        /** 初始mileClient */
        if (mileClient == null) {
            mileClient = new MileClient();
            mileClient.setBossExecutorCount(bossExecutorCount);
            mileClient.setWorkerExecutorCount(workerExecutorCount);
            mileClient.customBootstrap();
        }

        /** 初始mergeServerList */
        if (mergeServerList == null || mergeServerList.isEmpty()) {
            mergeServerList = new ArrayList<String>();
            mergeServerList.add("127.0.0.1:8964");
        }

        /** 初始version */
        if (version == 0) {
            version = 1;
        }
        /** 初始userName */
        if (StringUtils.isBlank(userName)) {
            userName = "test";
        }
        /** 初始passWord */
        if (passWord == null || passWord.length < 1) {
            passWord = "test".getBytes();
        }
        /** 初始clientProperty */
        if (clientProperty == null || clientProperty.size() < 1) {
            KeyValueData kv = new KeyValueData();
            kv.setKey("testKey");
            kv.setValue("testDV");
            clientProperty = new ArrayList<KeyValueData>();
            clientProperty.add(kv);
        }
        /** 初始mergeServersOk、mergeServersFail */
        if (mileClient.getServerRefOk() == null || mileClient.getServerRefOk().size() == 0) {
            // 构造连接消息
            ClientConnectMessage clientConnectMessage = new ClientConnectMessage();
            clientConnectMessage.setUserName(userName);
            clientConnectMessage.setPassWord(passWord);
            clientConnectMessage.setVersion(version);
            clientConnectMessage.setClientProperty(clientProperty);
            // 初始化连接
            ApplationClientService.initMergeServers(mergeServerList, mileClient,
                clientConnectMessage);
        }

        scheduler.scheduleWithFixedDelay(new CheckMergeServersTimerTask(mileClient), 10, 10,
            TimeUnit.SECONDS);
    }

    /**
     * 关闭client, 慎用
     */
    public void close() {
        scheduler.shutdownNow();
        mileClient.close();
    }

    /**
     * @throws SqlExecuteException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @see com.alipay.mile.client.SqlClientTemplate#preExecSql(java.lang.String,
     *      java.lang.String[], int)
     */

    protected Message preExecSql(String sql, Object[] params, int timeOut, boolean isQuery)
                                                                                           throws SqlExecuteException,
                                                                                           IOException,
                                                                                           InterruptedException,
                                                                                           ExecutionException {
        // 随机选择服务器
        ServerRef serverInfo = ApplationClientService.getRodemMergeServer(mileClient);
        // 二次验证 serverInfo
        if (serverInfo == null) {
            return null;
        }
        // 构造 预执行sql 消息
        SqlPreExecuteMessage sqlPreExecuteMessage = new SqlPreExecuteMessage();
        if (isQuery) {
            sqlPreExecuteMessage.setType(Message.MT_CM_PRE_Q_SQL);
        }
        sqlPreExecuteMessage.setExeTimeout(timeOut);
        sqlPreExecuteMessage.setSessionID(serverInfo.getSessionId());
        sqlPreExecuteMessage.setSqlCommand(sql);
        sqlPreExecuteMessage.setVersion(version);
        sqlPreExecuteMessage.setParameters(params);
        // 发送消息
        SendFuture sendFuture = mileClient.futureSendData(serverInfo.getChannel(),
            sqlPreExecuteMessage, timeOut, true);

        // 取得结果集
        return sendFuture.get();
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @see com.alipay.mile.client.SqlClientTemplate#preUpdate(java.lang.String,
     *      java.lang.String[], int)
     */
    @Override
    public MileUpdateResult preUpdate(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException {
        // 执行sql
        Message result = preExecSql(sql, params, timeOut, false);

        if (result instanceof SqlExecuteRsMessage) {
            SqlExecuteRsMessage rsMessage = (SqlExecuteRsMessage) result;
            return new MileUpdateResult(rsMessage.getSqlResultSet());
        } else if (result instanceof SqlExectueErrorMessage) {
            SqlExectueErrorMessage rsError = (SqlExectueErrorMessage) result;
            LOGGER.error("SQL执行失败 ： " + rsError.toErrString());
            throw new SqlExecuteException("SQL执行失败 ： " + rsError.toErrString());
        } else if (result instanceof CommonErrorMessage) {
            CommonErrorMessage rsError = (CommonErrorMessage) result;
            Iterator<ServerRef> it = mileClient.getServerRefOk().iterator();
            ServerRef sr;
            while (it.hasNext()) {
                sr = it.next();
                if (("/" + sr.getServerIp() + ":" + sr.getPort()).equals(rsError
                    .getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                    if (mileClient.getServerRefOk().remove(sr)) {
                        mileClient.getServerRefFail().add(sr);
                    }
                    break;
                }
            }
            it = mileClient.getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if (sr.getServerIp().equals(rsError.getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                }
            }
            LOGGER.warn("SQL执行失败 ： " + rsError.getErrorDescription());
        }

        return new MileUpdateResult();
    }

 

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @see com.alipay.mile.client.SqlClientTemplate#preInsert(java.lang.String,
     *      java.lang.String[], int)
     */
    @Override
    public MileInsertResult preInsert(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException {
        // 执行sql
        Message result = preExecSql(sql, params, timeOut, false);
        if (result instanceof SqlExecuteRsMessage) {
            SqlExecuteRsMessage rsMessage = (SqlExecuteRsMessage) result;
            return new MileInsertResult(rsMessage.getSqlResultSet());
        } else if (result instanceof SqlExectueErrorMessage) {
            SqlExectueErrorMessage rsError = (SqlExectueErrorMessage) result;
            LOGGER.error("SQL执行失败 ： " + rsError.toErrString());
            throw new SqlExecuteException("SQL执行失败 ： " + rsError.toErrString());
        } else if (result instanceof CommonErrorMessage) {
            CommonErrorMessage rsError = (CommonErrorMessage) result;
            Iterator<ServerRef> it = mileClient.getServerRefOk().iterator();
            ServerRef sr;
            while (it.hasNext()) {
                sr = it.next();
                if (("/" + sr.getServerIp() + ":" + sr.getPort()).equals(rsError
                    .getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                    if (mileClient.getServerRefOk().remove(sr)) {
                        mileClient.getServerRefFail().add(sr);
                    }
                    break;
                }
            }
            it = mileClient.getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if (sr.getServerIp().equals(rsError.getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                }
            }
            LOGGER.warn("SQL执行失败 ： " + rsError.getErrorDescription());
        }
        return new MileInsertResult();
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @see com.alipay.mile.client.SqlClientTemplate#preDelete(java.lang.String,
     *      java.lang.String[], int)
     */
    @Override
    public MileDeleteResult preDelete(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException {
        Message result = preExecSql(sql, params, timeOut, false);
        if (result instanceof SqlExecuteRsMessage) {
            SqlExecuteRsMessage rsMessage = (SqlExecuteRsMessage) result;
            return new MileDeleteResult(rsMessage.getSqlResultSet());
        } else if (result instanceof SqlExectueErrorMessage) {
            SqlExectueErrorMessage rsError = (SqlExectueErrorMessage) result;
            LOGGER.error(rsError.toErrString());
            throw new SqlExecuteException(rsError.toErrString());
        } else if (result instanceof CommonErrorMessage) {
            CommonErrorMessage rsError = (CommonErrorMessage) result;
            Iterator<ServerRef> it = mileClient.getServerRefOk().iterator();
            ServerRef sr;
            while (it.hasNext()) {
                sr = it.next();
                if (("/" + sr.getServerIp() + ":" + sr.getPort()).equals(rsError
                    .getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                    if (mileClient.getServerRefOk().remove(sr)) {
                        mileClient.getServerRefFail().add(sr);
                    }
                    break;
                }
            }
            it = mileClient.getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if (sr.getServerIp().equals(rsError.getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                }
            }
            LOGGER.warn("SQL执行失败 ： " + rsError.getErrorDescription());
        }
        return new MileDeleteResult();
    }
	
    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @see com.alipay.mile.client.SqlClientTemplate#preDelete(java.lang.String,
     *      java.lang.String[], int)
     */
    @Override
    public MileExportResult preExport(String sql, Object[] params, int timeOut)
                                                                               throws SqlExecuteException,
                                                                               IOException,
                                                                               InterruptedException,
                                                                               ExecutionException {
        Message result = preExecSql(sql, params, timeOut, false);
        if (result instanceof SqlExecuteRsMessage) {
            SqlExecuteRsMessage rsMessage = (SqlExecuteRsMessage) result;
            return new MileExportResult(rsMessage.getSqlResultSet());
        } else if (result instanceof SqlExectueErrorMessage) {
            SqlExectueErrorMessage rsError = (SqlExectueErrorMessage) result;
            LOGGER.error(rsError.toErrString());
            throw new SqlExecuteException(rsError.toErrString());
        } else if (result instanceof CommonErrorMessage) {
            CommonErrorMessage rsError = (CommonErrorMessage) result;
            Iterator<ServerRef> it = mileClient.getServerRefOk().iterator();
            ServerRef sr;
            while (it.hasNext()) {
                sr = it.next();
                if (("/" + sr.getServerIp() + ":" + sr.getPort()).equals(rsError
                    .getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                    if (mileClient.getServerRefOk().remove(sr)) {
                        mileClient.getServerRefFail().add(sr);
                    }
                    break;
                }
            }
            it = mileClient.getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if (sr.getServerIp().equals(rsError.getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                }
            }
            LOGGER.warn("SQL执行失败 ： " + rsError.getErrorDescription());
        }
        return new MileExportResult();
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @see com.alipay.mile.client.SqlClientTemplate#preQueryForList(java.lang.String,
     *      java.lang.String[], int)
     */
    @Override
    public MileQueryResult preQueryForList(String sql, Object[] params, int timeOut)
                                                                                    throws SqlExecuteException,
                                                                                    IOException,
                                                                                    InterruptedException,
                                                                                    ExecutionException {
        // 执行sql
        Message result = preExecSql(sql, params, timeOut, true);
        if (result instanceof SqlExecuteRsMessage) {
            SqlExecuteRsMessage rsMessage = (SqlExecuteRsMessage) result;
            return new MileQueryResult(rsMessage.getSqlResultSet());
        } else if (result instanceof SqlExectueErrorMessage) {
            SqlExectueErrorMessage rsError = (SqlExectueErrorMessage) result;
            LOGGER.error("SQL执行失败 ： " + rsError.toErrString());
            throw new SqlExecuteException("SQL执行失败 ： " + rsError.toErrString());
        } else if (result instanceof CommonErrorMessage) {
            CommonErrorMessage rsError = (CommonErrorMessage) result;
            Iterator<ServerRef> it = mileClient.getServerRefOk().iterator();
            ServerRef sr;
            while (it.hasNext()) {
                sr = it.next();
                if (("/" + sr.getServerIp() + ":" + sr.getPort()).equals(rsError
                    .getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                    if (mileClient.getServerRefOk().remove(sr)) {
                        mileClient.getServerRefFail().add(sr);
                    }
                    break;
                }
            }
            it = mileClient.getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if (sr.getServerIp().equals(rsError.getErrorParameters().get(0))) {
                    sr.setOnline(false);
                    sr.setAvailable(false);
                }
            }
            LOGGER.warn("SQL执行失败 ： " + rsError.getErrorDescription());
        }
        return new MileQueryResult();
    }

    public String command(String[] mergeServers, Message commandMessage) throws IOException,
                                                                        InterruptedException,
                                                                        ExecutionException {
        StringBuffer sb = new StringBuffer();
        for (String mergeServer : mergeServers) {
            //确认与mergeserver的连接
            String[] merge = mergeServer.split(":");
            Channel channel = null;
            for (ServerRef srf : mileClient.getServerRefOk()) {
                if (srf.getServerIp().equals(merge[0]) && (srf.getPort() + "").equals(merge[1])) {
                    channel = srf.getChannel();
                    if (channel.isConnected()) {
                        break;
                    }
                }
            }
            for (ServerRef srf : mileClient.getServerRefFail()) {
                if (srf.getServerIp().equals(merge[0]) && (srf.getPort() + "").equals(merge[1])) {
                    channel = srf.getChannel();
                    if (channel.isConnected()) {
                        break;
                    }
                }
            }
            if (channel == null) {
                sb.append("处理失败--没有merger连接");
                sb.append(mergeServer);
            } else {
                //发送指令
                Message result = null;
                SendFuture sf = mileClient.futureSendData(channel, commandMessage, 3000, true);
                result = sf.get();
                if (result instanceof CommonOkMessage) {
                    CommonOkMessage co = (CommonOkMessage) result;
                    sb.append(co.getOkDescription());
                } else if (result instanceof CommonErrorMessage) {
                    CommonErrorMessage co = (CommonErrorMessage) result;
                    sb.append(co.getErrorDescription());
                }else{
                    sb.append("无法识别的返回结果" + result);
                }

            }
        }
        return sb.toString();
    }

    private class CtuComparator implements Comparator<Map<String, Object>> {
        private String  orderField;

        private boolean orderType;

        public CtuComparator(String orderField, boolean orderType) {
            this.orderField = orderField;
            this.orderType = orderType;
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            Comparable left = (Comparable) o1.get(orderField);
            Comparable right = (Comparable) o2.get(orderField);
            int result = left.compareTo(right);
            if (!orderType) {
                result = -result;
            }

            return result;
        }

    }

    @SuppressWarnings("unchecked")
    private MileQueryResult preCtuPartialQuery(String tableName, List<String> selectFields,
                                               String condition, String clusterField,
                                               String topField, String orderField,
                                               boolean orderType, Object[] params, int timeOut)
                                                                                               throws SqlExecuteException,
                                                                                               IOException,
                                                                                               InterruptedException,
                                                                                               ExecutionException {

        MileQueryResult queryResult;

        // 检查合法性
        if (StringUtils.contains(clusterField, ",")) {
            throw new SqlExecuteException("cluster列只能包含一列!");
        }
        if (StringUtils.contains(orderField, ",")) {
            throw new SqlExecuteException("排序列只能包含一列");
        }
        if (StringUtils.contains(topField, ",")) {
            throw new SqlExecuteException("top列只能包含一列");
        }

        // 构造sql，查询结果集
        String sql = "select " + selectFields.get(0);
        for (int j = 1; j < selectFields.size(); j++) {
            sql += ", " + selectFields.get(j);
        }
        sql = sql + " from " + tableName + " " + condition + " limit 20000";

        queryResult = preQueryForList(sql, params, timeOut);
        if (null == queryResult || null == queryResult.getQueryResult()) {
            return new MileQueryResult();
        }
        List<Map<String, Object>> rowList = queryResult.getQueryResult();
        if (rowList.size() == 0) {
            return queryResult;
        }

        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

        for (Map<String, Object> row : rowList) {
            Map<String, Object> tmpRow = map.get(row.get(clusterField));
            if (null == tmpRow) {
                map.put((String) row.get(clusterField), row);
            } else {
                Comparable left = (Comparable) row.get(orderField);
                Comparable right = (Comparable) tmpRow.get(orderField);

                if (StringUtils.containsIgnoreCase(topField, "max")) {
                    if (left.compareTo(right) > 0) {
                        map.put((String) row.get(clusterField), row);
                    }
                }
                if (StringUtils.containsIgnoreCase(topField, "min")) {
                    if (left.compareTo(right) < 0) {
                        map.put((String) row.get(clusterField), row);
                    }
                }
            }
        }

        rowList = new ArrayList<Map<String, Object>>(map.values());
        Collections.sort(rowList, new CtuComparator(orderField, orderType));
        queryResult.setQueryResult(rowList);

        return queryResult;
    }

    /**
     * 为ctu后台的查询需求所做的临时接口，不允许其他系统使用，后期会抽象出通用的接口，此接口会逐步废弃掉
     * 
     * @param tableName
     *            表名，例如"t"
     * @param condition
     *            条件字段，例如"indexwhere CP=? where GR>? and GR<?"
     * @param clusterField
     *            要聚集的列，例如"UD"
     * @param topField
     *            挑选根据的列，例如"max(GR)"或者"min(GR)"
     * @param params
     *            参数列表
     * @param timeOut
     *            超时
     * @return
     */
    public int preCtuClusterCountQuery(String tableName, String condition, String clusterField,
                                       String topField, Object[] params, int timeOut)
                                                                                     throws SqlExecuteException,
                                                                                     IOException,
                                                                                     InterruptedException,
                                                                                     ExecutionException {

        MileQueryResult queryResult;
        List<String> selectFields = new ArrayList<String>();
        String orderField = null;

        int beginIndex = topField.indexOf("(");
        int endIndex = topField.indexOf(")");
        orderField = topField.substring(beginIndex + 1, endIndex);

        selectFields.add(clusterField);
        selectFields.add(orderField);
        queryResult = preCtuPartialQuery(tableName, selectFields, condition, clusterField,
            topField, orderField, true, params, timeOut);
        if (null == queryResult || null == queryResult.getQueryResult()) {
            return 0;
        }
        return queryResult.getQueryResult().size();
    }

    /**
     * 为ctu后台的查询需求所做的临时接口，不允许其他系统使用，后期会抽象出通用的接口，此接口会逐步废弃掉
     * 
     * @param tableName
     *            表名
     * @param selectFields
     *            要选择的列
     * @param condition
     *            条件字段，例如"indexwhere CP=? where GR>? and GR<?"
     * @param clusterField
     *            要聚集的列，例如"UD"
     * @param topField
     *            挑选top记录所根据的列，例如"max(GR)"或者"min(GR)"
     * @param orderField
     *            要排序的列，例如"GR"
     * @param orderType
     *            排序类型，顺序/倒序，true表示顺序，false表示倒序
     * @param limit
     * @param offset
     *            limit=100,offset=90时表示第91条记录到第100条记录
     * @param params
     *            参数列表
     * @param timeOut
     *            超时，单位为ms，例如3000表示超时时间为3秒
     * @return 查询结果集
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
                                                                           ExecutionException {

        MileQueryResult queryResult;
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(condition)
            || StringUtils.isBlank(clusterField) || StringUtils.isBlank(topField)
            || StringUtils.isBlank(orderField) || null == selectFields || selectFields.size() == 0) {
            throw new SqlExecuteException("接口的输入参数不正确!");
        }
        if (clusterField.contains(",")) {
            throw new SqlExecuteException("不允许有多个cluster列!");
        }
        if (offset > limit) {
            throw new SqlExecuteException("在查询中offset应该小于limit!");
        }

        queryResult = preCtuPartialQuery(tableName, selectFields, condition, clusterField,
            topField, orderField, orderType, params, timeOut);

        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        int up = limit;
        if (limit > resultList.size()) {
            up = resultList.size();
        }

        if (offset < resultList.size()) {
            queryResult.setQueryResult(resultList.subList(offset, up));
        } else {
            queryResult.setQueryResult(new ArrayList<Map<String, Object>>());
        }
        return queryResult;
    }

    /**
     * 加载配置文件
     * 
     * @param filePath
     */
    public void readProperties(String filePath) {
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            setUserName(props.getProperty("mile.client.user.name"));
            setPassWord(props.getProperty("mile.client.user.password").getBytes());
            bossExecutorCount = Integer.parseInt(props.getProperty(
                "mile.client.boss.executor.thread.count", "0"));
            workerExecutorCount = Integer.parseInt(props.getProperty(
                "mile.client.worker.executor.thread.count", "0"));

            if (mergeServerList == null || mergeServerList.isEmpty()) {
                mergeServerList = new ArrayList<String>();
            }

            String mergeServer = props.getProperty("mile.client.merg.server");
            if (mergeServer != null) {
                Collections.addAll(mergeServerList, mergeServer.split(";"));
            }

        } catch (Exception e) {
            LOGGER.error("加载配置文件失败 ： ", e);
        }
    }

    public List<String> getMergeServerList() {
        return mergeServerList;
    }

    public void setMergeServerList(List<String> mergeServerList) {
        this.mergeServerList = mergeServerList;
    }

    public MileClient getMileClient() {
        return mileClient;
    }

    public void setMileClient(MileClient mileClient) {
        this.mileClient = mileClient;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getPassWord() {
        return passWord;
    }

    public void setPassWord(byte[] passWord) {
        this.passWord = passWord;
    }

    public List<KeyValueData> getClientProperty() {
        return clientProperty;
    }

    public void setClientProperty(List<KeyValueData> clientProperty) {
        this.clientProperty = clientProperty;
    }

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public ScheduledExecutorService getTimer() {
        return scheduler;
    }

    public int getBossExecutorCount() {
        return bossExecutorCount;
    }

    public void setBossExecutorCount(int bossExecutorCount) {
        this.bossExecutorCount = bossExecutorCount;
    }

    public int getWorkerExecutorCount() {
        return workerExecutorCount;
    }

    public void setWorkerExecutorCount(int workerExecutorCount) {
        this.workerExecutorCount = workerExecutorCount;
    }


}
