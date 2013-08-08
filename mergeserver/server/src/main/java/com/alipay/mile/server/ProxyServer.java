/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.alipay.mile.Config;
import com.alipay.mile.Constants;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.communication.ApplationClientService;
import com.alipay.mile.communication.MessageManager;
import com.alipay.mile.communication.MileClient;
import com.alipay.mile.communication.MileServer;
import com.alipay.mile.communication.ServerRef;
import com.alipay.mile.log.DigestLogUtil;
import com.alipay.mile.log.RequestCounter;
import com.alipay.mile.log.RequestDigest;
import com.alipay.mile.message.ClientConnRespError;
import com.alipay.mile.message.ClientConnRespOKMessage;
import com.alipay.mile.message.ClientConnectMessage;
import com.alipay.mile.message.ClientReConnectMessage;
import com.alipay.mile.message.CommonErrorMessage;
import com.alipay.mile.message.CommonOkMessage;
import com.alipay.mile.message.MergeStartLeadQueryMessage;
import com.alipay.mile.message.MergeStopLeadQueryMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.SpecifyQueryExecuteRsMessage;
import com.alipay.mile.message.SqlExectueErrorMessage;
import com.alipay.mile.message.SqlExecuteMessage;
import com.alipay.mile.message.SqlExecuteRsMessage;
import com.alipay.mile.message.SqlPreExecuteMessage;
import com.alipay.mile.message.m2d.SpecifyQueryExecuteMessage;
import com.alipay.mile.mileexception.ArgumentFormantException;
import com.alipay.mile.server.merge.DocumentServerPolicy;
import com.alipay.mile.server.merge.Merger;
import com.alipay.mile.server.merge.Policy;
import com.alipay.mile.server.query.special.SubSelect;
import com.alipay.mile.server.sharding.DefaultShardConfig;

/**
 * 代理服务器
 * @author huabing.du
 * @version $Id: ProxyServer.java, v 0.1 2011-5-10 下午01:41:08 huabing.du Exp $
 */
public class ProxyServer {

    private static final Logger  LOGGER             = Logger.getLogger(ProxyServer.class.getName());

    private static final Logger  INSERTDIGESTLOGGER = Logger.getLogger("DIGEST-MERGE-INSERT");

    private static final Logger  QUERYDIGESTLOGGER  = Logger.getLogger("DIGEST-MERGE-QUERY");

    private final RequestCounter insertCounter      = new RequestCounter();

    private final RequestCounter queryCounter       = new RequestCounter();

    /** 业务处理类 */
    private Merger               bizServer;

    /** 通信服务端 mergerServer */
    private MileServer           server;

    /** 与DocServer通信端 MileClient */
    private MileClient           client;

    /** DocServer 管理配置类 */
    private Policy               policy;

    /** 会话 */
    private volatile int         sessIdCounter      = 0;

    /** 查询流量导出开关  */
    private AtomicBoolean        leadQuery          = new AtomicBoolean(false);

    /** 与查询流量导入服务器的通信端 */
    private MileClient           leadQueryClient;
    private int                  logTimeThreshold   = 0;
    private Level                logTimeLevel       = Level.WARN;

    private class DigestLogPrint implements Runnable {

        @Override
        public void run() {
            try {

                for (Entry<String, RequestDigest> entry : insertCounter.getRequests().entrySet()) {
                    if (INSERTDIGESTLOGGER.isInfoEnabled()) {
                        INSERTDIGESTLOGGER.info(System.getProperty("HOST_NAME") + ","
                                                + entry.getKey() + ","
                                                + entry.getValue().toString());
                    }
                }
                insertCounter.reset();

                for (Entry<String, RequestDigest> entry : queryCounter.getRequests().entrySet()) {
                    if (QUERYDIGESTLOGGER.isInfoEnabled()) {
                        QUERYDIGESTLOGGER
                            .info(System.getProperty("HOST_NAME") + "," + entry.getKey() + ","
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
     *
     * @throws ArgumentFormantException
     * @throws IOException
     * @throws Exception
     */
    public void init() throws ArgumentFormantException, IOException {
        client = new MileClient();
        server = new MileServer(Config.getServerIOPort());
        File proxyfilePath = Config.getProxyConfig();
        server.readProperties(proxyfilePath.getPath());
        MergeServerMessageListener messageDispatcher = new MergeServerMessageListener(this);
        // 注册 listner to mileserver
        MessageManager mManager = new MessageManager(server.getExecMin(), server.getExecMax(),
            server.getQueryExecMin(), server.getQueryExecMax(), server.getKeepAliveTime(), server
                .getBlockingQueueCount());
        mManager.addMessageListener(messageDispatcher);
        server.setMessageManager(mManager);

        /**
         * 建立docserver连接, 并启动定期检查器
         */
        if (policy == null) {
            policy = new DocumentServerPolicy(Config.getDocumentServerConfig(), client);
        }
        policy.checkDocServerHealth();

        //加载sharding
        DefaultShardConfig shard = new DefaultShardConfig(Config.getShardingConfig());
        Constants.queryResultLimit = Config.getQueryLimit();
        //启动 merger
        bizServer = new Merger(client, shard);

        DigestLogUtil.registDigestTask(new DigestLogPrint(), 30, 60, TimeUnit.SECONDS);

        logTimeLevel = Level.toLevel(Config.getLogTimeLevel());
        logTimeThreshold = Config.getLogTimeThreshold();
    }

    /**
     * mergerServer 通信启动
     */
    public void start() {
        server.startCustomBootstrap();
        server.setOnline(true);
    }

    /**
     * 处理client端连接请求
     * @param reqMessage
     * @return
     */
    public Message processClientConnectMessage(ClientConnectMessage reqMessage) {
        // get message info
        String username = reqMessage.getUserName();
        byte[] password = reqMessage.getPassWord();
        try {
            // LOOKME密码验证 分发session
            if (username.getBytes("UTF-8").length == password.length) {
                ClientConnRespOKMessage resMessage = new ClientConnRespOKMessage();
                resMessage.setSessionID(sessIdCounter++);
                resMessage.setServerDescription("mile merge server");
                resMessage.setServerproperties(null);
                return resMessage;
            } else {
                ClientConnRespError resMessage = new ClientConnRespError();
                resMessage.setConnErrCode((short) 1);
                resMessage.setErrParameter(null);
                resMessage.setErrDescription("username/password invalid.");
                return resMessage;
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(": ", e);
        }
        //拒绝连接
        return null;
    }

    /**
     * 处理通信重连
     * @param reqMessage
     * @return
     */
    public Message processClientReConnectMessage(ClientReConnectMessage reqMessage) {
        ClientConnRespOKMessage resMessage = new ClientConnRespOKMessage();
        resMessage.setSessionID(reqMessage.getSessionID());
        resMessage.setServerDescription("mile merge server");
        resMessage.setServerproperties(null);
        return resMessage;
    }

    private void estimateRequest(String source, String sql, long time, int resultCode) {
        if (sql.startsWith("insert")) {
            insertCounter.addRequest(source, time, resultCode);
        } else if (sql.startsWith("select")) {
            queryCounter.addRequest(source, time, resultCode);
        }
    }

    /**
     * 处理sql消息
     * @param reqMessage
     * @return
     */
    public Message processSqlExecuteMessage(SqlExecuteMessage request, String sourceIp) {
        String sql = request.getSqlCommand();
        long executeTime = 0;
        try {
            long startTime = System.currentTimeMillis();
            SqlResultSet rs = bizServer.execute(sql, request.getExeTimeout());
            long endTime = System.currentTimeMillis();
            executeTime = endTime - startTime;
            if (logTimeThreshold > 0 && executeTime > logTimeThreshold) {
                if (LOGGER.isEnabledFor(logTimeLevel)) {
                    LOGGER.log(logTimeLevel, "sql " + request.getSqlCommand() + " reqID "
                                             + request.getId() + " 耗时 " + executeTime + " ms");
                }
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("处理的sql命令: " + request.getSqlCommand());

            }

            if (rs != null) {
                SqlExecuteRsMessage resMessage = new SqlExecuteRsMessage();
                resMessage.setSqlResultSet(rs);
                estimateRequest(sourceIp, request.getSqlCommand(), executeTime, 0);
                return resMessage;
            } else {
                //空返回
                LOGGER.error("处理sql消息异常 ：DocServr没有返回、响应超时，请求信息： " + request.toString());
                SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
                resMessage.setId(request.getId());
                resMessage.setVersion(request.getVersion());
                resMessage.setErrDescription("DocServr没有返回、响应超时，请求信息: " + request.toString());
                estimateRequest(sourceIp, request.getSqlCommand(), executeTime, 1);
                return resMessage;
            }
        } catch (Exception e) {
            LOGGER.error("处理sql消息异常，sql信息: " + request.toString(), e);
            //返回sql异常消息
            SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
            resMessage.setId(request.getId());
            resMessage.setVersion(request.getVersion());
            resMessage.setErrDescription(e.toString());
            estimateRequest(sourceIp, request.getSqlCommand(), executeTime, -1);
            return resMessage;
        }
    }

    /**
     * process specify query message.
     * @param request
     * @return
     */
    public Message processSpecifyQueryExecuteMessage(SpecifyQueryExecuteMessage request) {
        try {
            long startTime = 0, endTime = 0;
            startTime = System.currentTimeMillis();
            Integer rs = bizServer.executeSpecifyQuery(request.getId(), request.getSpecifyQuery(),
                request.getExeTimeout());
            endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            if (logTimeThreshold > 0 && executeTime > logTimeThreshold) {
                if (LOGGER.isEnabledFor(logTimeLevel)) {
                    LOGGER.log(logTimeLevel, "sql SpecifyQuery" + " reqID " + request.getId()
                                             + " 耗时 " + executeTime + " ms");
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("处理的specify query命令: " + request.getSpecifyQuery().getCondition());
            }

            if (rs != null) {
                SpecifyQueryExecuteRsMessage resMessage = new SpecifyQueryExecuteRsMessage();
                resMessage.setAffectRows(rs);
                byte values[] = new byte[0];
                if (rs > 0) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutput os = new DataOutputStream(baos);
                    for (SubSelect s : request.getSpecifyQuery().getSubSelect()) {
                        s.writeResultToStream(os);
                    }
                    values = baos.toByteArray();
                }
                resMessage.setValues(values);
                return resMessage;
            } else {
                //空返回
                LOGGER.error("处理sql消息异常 ：DocServr没有返回、响应超时--MessageID: " + request.getId());
                SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
                resMessage.setId(request.getId());
                resMessage.setVersion(request.getVersion());
                resMessage.setErrDescription("DocServr没有返回、响应超时--MessageID: " + request.getId());
                return resMessage;
            }
        } catch (Exception e) {
            LOGGER.error("处理sql消息异常--MessageID: " + request.getId(), e);
            //返回sql异常消息
            SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
            resMessage.setId(request.getId());
            resMessage.setVersion(request.getVersion());
            resMessage.setErrDescription(e.toString());
            return resMessage;
        }
    }

    /**
     * 处理预编译sql消息
     * @param reqMessage
     * @return
     */
    public Message processSqlPreExecuteMessage(SqlPreExecuteMessage reqMessage, String sourceIp) {
        long executeTime = 0;

        //如果开启了了查询流量导出，那么需要将查询流量导出到某台mergeserver上
        if (leadQuery.get()
            && StringUtils.startsWithIgnoreCase(reqMessage.getSqlCommand().trim(), "select")) {
            try {
                for (ServerRef leadServer : leadQueryClient.getServerRefOk()) {
                    SqlPreExecuteMessage cpMessage = new SqlPreExecuteMessage();
                    cpMessage.setType(reqMessage.getType());
                    cpMessage.setExeTimeout(reqMessage.getExeTimeout());
                    cpMessage.setSessionID(leadServer.getSessionId());
                    cpMessage.setSqlCommand(reqMessage.getSqlCommand());
                    cpMessage.setVersion(reqMessage.getVersion());
                    cpMessage.setParameters(reqMessage.getParameters());

                    leadQueryClient.futureSendData(leadServer.getChannel(), cpMessage, cpMessage
                        .getExeTimeout(), true);
                }
            } catch (Exception e) {
                //出现异常不应该影响正常的sql执行
                LOGGER.warn("执行查询流量导出时出现异常, ", e);
            }
        }

        //执行sql
        try {
            long startTime = 0, endTime = 0;
            startTime = System.currentTimeMillis();
            SqlResultSet rs = bizServer.execute(reqMessage.getSqlCommand(), reqMessage
                .getParameters(), reqMessage.getExeTimeout());
            endTime = System.currentTimeMillis();
            executeTime = endTime - startTime;
            if (logTimeThreshold > 0 && executeTime > logTimeThreshold) {
                if (LOGGER.isEnabledFor(logTimeLevel)) {
                    LOGGER.log(logTimeLevel, " req " + reqMessage.toString() + " reqID "
                                             + reqMessage.getId() + " 耗时 " + executeTime + " ms");
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("处理的sql命令: " + reqMessage.toString());
            }
            if (rs != null) {
                SqlExecuteRsMessage resMessage = new SqlExecuteRsMessage();
                resMessage.setSqlResultSet(rs);
                if (!rs.isSuccessful()) {
                    LOGGER.error("处理sql命令失败：" + reqMessage.toString());
                }

                estimateRequest(sourceIp, reqMessage.getSqlCommand(), executeTime, 0);

                return resMessage;
            } else {
                LOGGER.error("处理sql消息异常 ：DocServr没有返回，响应超时，请求信息：" + reqMessage.toString());
                SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
                resMessage.setId(reqMessage.getId());
                resMessage.setVersion(reqMessage.getVersion());
                resMessage.setErrDescription("DocServr没有返回，响应超时，请求信息：" + reqMessage.toString());
                estimateRequest(sourceIp, reqMessage.getSqlCommand(), executeTime, 1);
                return resMessage;
            }
        } catch (Exception e) {
            LOGGER.error("处理预编译sql消息异常--MessageID: " + reqMessage.getId() + ", sql: "
                         + reqMessage.toString(), e);
            //返回sql异常消息
            SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
            resMessage.setId(reqMessage.getId());
            resMessage.setVersion(reqMessage.getVersion());
            resMessage.setErrDescription(e.toString());
            estimateRequest(sourceIp, reqMessage.getSqlCommand(), executeTime, -1);
            return resMessage;
        }
    }

    synchronized public Message processStartLeadQueryMessage(MergeStartLeadQueryMessage message) {
        List<String> copyServers = message.getMergeServers();
        Message response;

        for (String copyServer : copyServers) {
            String[] address = copyServer.split(":");
            if (address.length != 2) {
                response = new CommonErrorMessage();
                ((CommonErrorMessage) response).setErrorDescription("无效的mergeserver地址" + copyServer
                                                                    + "!\n");
                return response;
            } else {
                try {
                    if (InetAddress.getByName(address[0]).isLinkLocalAddress()
                        || StringUtils.equals(address[0], InetAddress.getLocalHost()
                            .getHostAddress()) || StringUtils.equals(address[0], "127.0.0.1")) {
                        if (Config.getServerIOPort() == Integer.valueOf(address[1])) {
                            response = new CommonErrorMessage();
                            ((CommonErrorMessage) response)
                                .setErrorDescription("mergeserver地址不能为本机地址!\n");
                            return response;
                        }
                    }
                } catch (Exception e) {
                    response = new CommonErrorMessage();
                    ((CommonErrorMessage) response).setErrorDescription("无效的mergeserver地址"
                                                                        + copyServer + "!\n");
                    return response;
                }
            }

        }

        if (leadQuery.get()) {
            leadQuery.set(false);
            leadQueryClient.close();
        }

        leadQueryClient = new MileClient();
        ClientConnectMessage clientConnectMessage = new ClientConnectMessage();
        clientConnectMessage.setUserName("test");
        clientConnectMessage.setPassWord("test".getBytes());
        clientConnectMessage.setVersion(Short.valueOf("1"));
        // 初始化连接
        ApplationClientService.initMergeServers(message.getMergeServers(), leadQueryClient,
            clientConnectMessage);
        leadQuery.set(true);

        response = new CommonOkMessage();
        ((CommonOkMessage) response).setOkDescription("mergeserver的查询流量导出已经 开启!\n");

        return response;
    }

    synchronized public Message processStopLeadQueryMessage(MergeStopLeadQueryMessage message) {
        if (leadQuery.get()) {
            leadQuery.set(false);
            leadQueryClient.close();
        }
        CommonOkMessage commonOkMessage = new CommonOkMessage();
        commonOkMessage.setOkDescription("mergeserver的查询流量导出已经关闭!\n");

        return commonOkMessage;
    }

    public Merger getBizServer() {
        return bizServer;
    }

    public void setBizServer(Merger bizServer) {
        this.bizServer = bizServer;
    }

    public MileServer getServer() {
        return server;
    }

    public void setServer(MileServer server) {
        this.server = server;
    }

    public int getSessIdCounter() {
        return sessIdCounter;
    }

    public void setSessIdCounter(int sessIdCounter) {
        this.sessIdCounter = sessIdCounter;
    }

    public MileClient getClient() {
        return client;
    }

    public void setClient(MileClient client) {
        this.client = client;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

}
