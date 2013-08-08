/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.alipay.mile.Config;
import com.alipay.mile.communication.MileClient;
import com.alipay.mile.communication.SendFuture;
import com.alipay.mile.communication.ServerRef;
import com.alipay.mile.log.DigestLogUtil;
import com.alipay.mile.log.RequestCounter;
import com.alipay.mile.log.RequestDigest;
import com.alipay.mile.message.AccessRsMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.m2d.AccessStatementMessage;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.merge.DocumentChooseStrategy;
import com.alipay.mile.server.merge.RandomStrategy;
import com.alipay.mile.server.merge.ShardingStrategy;
import com.alipay.mile.server.query.DeleteStatement;
import com.alipay.mile.server.query.ExportStatement;
import com.alipay.mile.server.query.GetkvStatement;
import com.alipay.mile.server.query.InsertStatement;
import com.alipay.mile.server.query.QueryStatement;
import com.alipay.mile.server.query.UpdateStatement;
import com.alipay.mile.server.sharding.DefaultShardConfig;

/**
 * 
 * 消息处理器，负责mergeserver和docserver之间的通讯
 * 
 * @author yuzhong.zhao
 * @version $Id: MessageProcessor.java, v 0.1 2011-5-30 下午07:57:41 yuzhong.zhao
 *          Exp $
 */
public class MessageProcessor {
    private static final Logger    LOGGER             = Logger.getLogger(MessageProcessor.class
                                                          .getName());

    private static final Logger    INSERTDIGESTLOGGER = Logger.getLogger("DIGEST-DOC-INSERT");

    private static final Logger    QUERYDIGESTLOGGER  = Logger.getLogger("DIGEST-DOC-QUERY");

    /** 发送端 */
    private final MileClient       mileClient;

    /** sharding策略 */
    private ShardingStrategy       shardingStrategy;

    /** 用于选择哪台docserver进行发送 */
    private DocumentChooseStrategy documentChooseStrategy;

    /** 统计每台docserver上插入请求的执行情况  */
    private final RequestCounter   insertCounter;

    /** 统计每台docserver上查询请求的执行情况  */
    private final RequestCounter   queryCounter;

    private int                    logTimeThreshold   = 0;
    private Level                  logTimeLevel       = Level.WARN;

    public ShardingStrategy getShardingStrategy() {
        return shardingStrategy;
    }

    public void setShardingStrategy(ShardingStrategy shardingStrategy) {
        this.shardingStrategy = shardingStrategy;
    }

    public DocumentChooseStrategy getDocumentChooseStrategy() {
        return documentChooseStrategy;
    }

    public void setDocumentChooseStrategy(DocumentChooseStrategy documentChooseStrategy) {
        this.documentChooseStrategy = documentChooseStrategy;
    }

    public MileClient getMileClient() {
        return mileClient;
    }

    public MessageProcessor(MileClient mileClient, DefaultShardConfig shard) {
        this.mileClient = mileClient;
        this.shardingStrategy = new ShardingStrategy(shard, mileClient.getNodes().keySet());
        this.documentChooseStrategy = new RandomStrategy(mileClient);
        this.insertCounter = new RequestCounter();
        this.queryCounter = new RequestCounter();
        DigestLogUtil.registDigestTask(new DigestLogPrint(), 30, 60, TimeUnit.SECONDS);
        this.logTimeLevel = Level.toLevel(Config.getLogTimeLevel());
        this.logTimeThreshold = Config.getLogTimeThreshold();
        LOGGER.warn("log threshold = " + logTimeThreshold);
    }

    private class DigestLogPrint implements Runnable {

        @Override
        public void run() {
            try {

                for (Entry<String, RequestDigest> entry : insertCounter.getRequests().entrySet()) {
                    String address[] = entry.getKey().split(":");
                    if (INSERTDIGESTLOGGER.isInfoEnabled()) {
                        INSERTDIGESTLOGGER.info(address[0] + "," + address[1] + ","
                                                + entry.getValue().toString());
                    }
                }

                insertCounter.reset();
                for (Entry<String, RequestDigest> entry : queryCounter.getRequests().entrySet()) {
                    String address[] = entry.getKey().split(":");
                    if (QUERYDIGESTLOGGER.isInfoEnabled()) {
                        QUERYDIGESTLOGGER.info(address[0] + "," + address[1] + ","
                                               + entry.getValue().toString());
                    }
                }

                queryCounter.reset();
            } catch (Exception e) {
                LOGGER.error("在打印docserver的摘要日志时出错, ", e);
            }
        }

    }

    /**
     * 发送消息
     * 
     * @param dsrs
     *            要发送的docserver列表
     * @param reqMessage
     *            要发送的消息
     * @param timeOut
     *            超时时间
     * @return 返回结果集
     * @throws IOException
     * @throws SqlExecuteException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private List<Message> sendMessage(List<ServerRef> dsrs, Message reqMessage, int timeOut)
                                                                                            throws IOException,
                                                                                            SqlExecuteException,
                                                                                            InterruptedException,
                                                                                            ExecutionException {
        List<Map<String, Object>> futures = new ArrayList<Map<String, Object>>(dsrs.size());
        List<Message> result = new ArrayList<Message>(dsrs.size());

        long startTime = System.currentTimeMillis();

        // 向每个docserver发送查询请求
        for (ServerRef dsf : dsrs) {
            Channel dsChannel = dsf.getChannel();
            SendFuture sf = null;
            if (dsChannel != null) {
                sf = mileClient.futureSendData(dsChannel, reqMessage, timeOut, true);
            }
            Map<String, Object> tmpMap = new HashMap<String, Object>();
            tmpMap.put("sf", sf);
            tmpMap.put("ds", dsf);
            futures.add(tmpMap);
        }

        // 遍历接收结果、处理结果
        for (int i = 0; i < futures.size(); i++) {
            Map<String, Object> tmpMap = futures.get(i);
            SendFuture sf = (SendFuture) tmpMap.get("sf");
            ServerRef ds = (ServerRef) tmpMap.get("ds");

            if (sf == null) {
                AccessRsMessage resultMessage = new AccessRsMessage();
                resultMessage.setId(ds.getServerId());
                resultMessage.setExcTime(0);
                resultMessage.setValues(null);
                result.add(resultMessage);
                continue;
            }

            Message resMessage = sf.get();
            Channel dsChannel = ds.getChannel();

            if (resMessage == null) {
                LOGGER.error("在执行sql时docserver[" + dsChannel.getRemoteAddress().toString()
                             + "超时，发送等待时间" + (sf.getWriteTime() - sf.getSendTime())
                             + "ms，docserver执行时间"
                             + (System.currentTimeMillis() - sf.getWriteTime()) + "ms");
                AccessRsMessage accessRsMessage = new AccessRsMessage();
                accessRsMessage.setNodeId(ds.getServerId());
                accessRsMessage.setExcTime(sf.getResultTime() - startTime);
                accessRsMessage.setValues(null);
                accessRsMessage.setResultCode(1);
                result.add(accessRsMessage);
            } else if (resMessage instanceof AccessRsMessage) {
                AccessRsMessage resultMessage = (AccessRsMessage) resMessage;
                resultMessage.setNodeId(ds.getServerId());
                resultMessage.setExcTime(sf.getResultTime() - startTime);
                resultMessage.setResultCode(0);
                result.add(resultMessage);
                if (logTimeThreshold > 0)
                    logTimes(resultMessage.getExcTime(), dsChannel.getRemoteAddress().toString(),
                        reqMessage.getId());
            } else {
                LOGGER.error("在执行sql时docserver[" + dsChannel.getRemoteAddress().toString()
                             + "]返回错误包，" + resMessage);
                AccessRsMessage accessRsMessage = new AccessRsMessage();
                accessRsMessage.setNodeId(ds.getServerId());
                accessRsMessage.setExcTime(sf.getResultTime() - startTime);
                accessRsMessage.setValues(null);
                accessRsMessage.setResultCode(-1);
                result.add(accessRsMessage);
            }
        }
        return result;
    }

    private void logTimes(long excTime, String string, int reqId) {
        if (excTime > logTimeThreshold) {
            if (LOGGER.isEnabledFor(logTimeLevel)) {
                LOGGER.log(logTimeLevel, " docserver " + string + " 耗时 " + excTime + " ms"
                                         + " reqID " + reqId);
            }
        }
    }

    /**
     * 
     * @param stmt
     * @param paramBindMap
     * @param timeOut
     * @return
     * @throws SqlExecuteException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IllegalSqlException
     */
    public Message getKVMessage(GetkvStatement stmt, Map<Object, List<Object>> paramBindMap,
                                int timeOut) throws SqlExecuteException, IOException,
                                            InterruptedException, ExecutionException,
                                            IllegalSqlException {
        List<Message> result;
        Long docid = stmt.dochint.docId;
        int serverId = (int) (docid >> 48);

        AccessStatementMessage sendMessage = new AccessStatementMessage(stmt, paramBindMap, timeOut);

        ServerRef server = documentChooseStrategy.chooseReadDocumentServerById(serverId);
        List<ServerRef> servers = new ArrayList<ServerRef>();
        servers.add(server);
        result = sendMessage(servers, sendMessage, timeOut);

        return result.get(0);
    }

    /**
     * 
     * @param stmt
     * @param paramBindMap
     * @param timeOut
     * @return
     * @throws SqlExecuteException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IllegalSqlException
     */
    public List<Message> queryMessage(QueryStatement stmt, Map<Object, List<Object>> paramBindMap,
                                      int timeOut) throws SqlExecuteException, IOException,
                                                  InterruptedException, ExecutionException,
                                                  IllegalSqlException {
        List<ServerRef> servers;
        List<Message> accessRsMessage;
        AccessStatementMessage sendMessage = new AccessStatementMessage(stmt, paramBindMap, timeOut);

        // 根据sharding以及其他策略选择合适的docserver列表
        Collection<Integer> nodeIds = shardingStrategy.querySharding(stmt, paramBindMap);
        if (null == nodeIds || nodeIds.isEmpty()) {
            throw new SqlExecuteException("在执行查询命令时sharding出错，没有匹配sharding规则的节点!");
        }

        // 根据docserver的id选择docserver
        servers = documentChooseStrategy.chooseQueryDocumentServer(nodeIds);
        accessRsMessage = sendMessage(servers, sendMessage, timeOut);

        for (int i = 0; i < servers.size(); i++) {
            AccessRsMessage rsMessage = (AccessRsMessage) accessRsMessage.get(i);
            queryCounter.addRequest(servers.get(i).getServerIp() + ":" + servers.get(i).getPort(),
                rsMessage.getExcTime(), rsMessage.getResultCode());
        }

        return accessRsMessage;
    }

    /**
     * 处理构造插入消息
     * 
     * @param sessionId
     *            session号
     * @param stmt
     *            插入语句
     * @param paramList
     *            动态绑定的参数列表
     * @param timeOut
     *            超时时间
     * @return docserver的应答消息
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @throws IllegalSqlException
     * @throws Exception
     */
    public Message insertMessage(InsertStatement stmt, Map<Object, List<Object>> paramBindMap,
                                 int timeOut) throws SqlExecuteException, IOException,
                                             InterruptedException, ExecutionException,
                                             IllegalSqlException {

        AccessStatementMessage sendMessage = new AccessStatementMessage(stmt, paramBindMap, timeOut);
        ServerRef server;
        List<ServerRef> servers = new ArrayList<ServerRef>();
        List<Message> resultMsg = new ArrayList<Message>();
        AccessRsMessage rsMessage;

        // 根据sharding以及其他策略选择合适的docserver列表
        Collection<Integer> nodeIds = shardingStrategy.insertSharding(stmt, paramBindMap);
        if (null == nodeIds || nodeIds.isEmpty()) {
            throw new SqlExecuteException("在执行插入命令时sharding出错，没有匹配sharding规则的节点!");
        }

        // 根据docserver的id选择docserver
        server = documentChooseStrategy.chooseInsertDocumentServer(nodeIds);
        servers.add(server);

        // 增加相应serverRef的插入计数
        try {
            server.addInsertMsgCount();
            resultMsg = sendMessage(servers, sendMessage, timeOut);
        } catch (Exception e) {
            LOGGER.error("在向docserver发送消息时出现异常, ", e);
        } finally {
            // 减少相应serverRef的插入计数
            server.subInsertMsgCount();
        }

        rsMessage = (AccessRsMessage) resultMsg.get(0);
        insertCounter.addRequest(server.getServerIp() + ":" + server.getPort(), rsMessage
            .getExcTime(), rsMessage.getResultCode());
        return rsMessage;
    }

    /**
     * 处理构造删除消息
     * 
     * @param stmt
     *            删除语句
     * @param paramList
     *            动态绑定的参数列表
     * @param timeOut
     *            超时时间
     * @return docserver的应答消息
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws SqlExecuteException
     * @throws IllegalSqlException
     * @throws Exception
     */
    public List<Message> deleteMessage(DeleteStatement stmt,
                                       Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                           throws SqlExecuteException,
                                                                                           IOException,
                                                                                           InterruptedException,
                                                                                           ExecutionException,
                                                                                           IllegalSqlException {
        AccessStatementMessage sendMessage = new AccessStatementMessage(stmt, paramBindMap, timeOut);
        List<ServerRef> servers;

        // 根据sharding以及其他策略选择合适的docserver列表
        Collection<Integer> nodeIds = shardingStrategy.deleteSharding(stmt, paramBindMap);
        if (null == nodeIds || nodeIds.isEmpty()) {
            throw new SqlExecuteException("在执行删除命令时sharding出错，没有匹配sharding规则的节点!");
        }
        // 根据docserver的id选择docserver
        servers = documentChooseStrategy.chooseChangeDocumentServer(nodeIds);
        return sendMessage(servers, sendMessage, timeOut);
    }

    public List<Message> exportMessage(ExportStatement stmt,
                                       Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                           throws SqlExecuteException,
                                                                                           IOException,
                                                                                           InterruptedException,
                                                                                           ExecutionException,
                                                                                           IllegalSqlException {
        AccessStatementMessage sendMessage = new AccessStatementMessage(stmt, paramBindMap, timeOut);
        List<ServerRef> servers;

        // select docserver list
        Collection<Integer> nodeIds = shardingStrategy.exportSharding(stmt, paramBindMap);
        if (nodeIds == null || nodeIds.isEmpty()) {
            throw new SqlExecuteException("在执行export命令时sharding出错，没有匹配sharding规则的节点!");
        }
        servers = documentChooseStrategy.chooseChangeDocumentServer(nodeIds);
        return sendMessage(servers, sendMessage, timeOut);
    }

    /**
     * 处理构造更新消息
     * 
     * @param sessionId
     *            session号
     * @param stmt
     *            更新语句
     * @param paramList
     *            动态绑定的参数列表
     * @param timeOut
     *            超时时间
     * @return docserver的应答消息
     * @throws SqlExecuteException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IllegalSqlException
     */
    public List<Message> updateMessage(UpdateStatement stmt,
                                       Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                           throws SqlExecuteException,
                                                                                           IOException,
                                                                                           InterruptedException,
                                                                                           ExecutionException,
                                                                                           IllegalSqlException {

        List<ServerRef> servers;
        AccessStatementMessage sendMessage = new AccessStatementMessage(stmt, paramBindMap, timeOut);

        // 根据sharding以及其他策略选择合适的docserver列表
        Collection<Integer> nodeIds = shardingStrategy.updateSharding(stmt, paramBindMap);
        if (null == nodeIds || nodeIds.isEmpty()) {
            throw new SqlExecuteException("在执行更新命令时sharding出错，没有匹配sharding规则的节点!");
        }
        // 根据docserver的id选择docserver
        servers = documentChooseStrategy.chooseChangeDocumentServer(nodeIds);
        return sendMessage(servers, sendMessage, timeOut);
    }
}
