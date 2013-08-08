/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.alipay.mile.message.ClientConnRespError;
import com.alipay.mile.message.ClientConnRespOKMessage;
import com.alipay.mile.message.ClientConnectMessage;
import com.alipay.mile.message.ClientReConnectMessage;
import com.alipay.mile.message.CommonErrorMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * @author jin.qian
 * @version $Id: ApplationClientService.java,v 0.1 2011-4-6 下午05:48:00 jin.qian
 *          Exp $
 */
public class ApplationClientService {

    private static final Logger LOGGER = Logger.getLogger(ApplationClientService.class.getName());

    /**
     * @param mergeServerList
     * @param mileClient
     * @param clientConnectMessage
     *            启动时根据server列表创建连接
     */
    public static void initMergeServers(List<String> mergeServerList, MileClient mileClient,
                                        ClientConnectMessage clientConnectMessage) {
        if (null == mergeServerList || mergeServerList.size() == 0) {
            return;
        }
        List<ServerRef> mergerOk = new CopyOnWriteArrayList<ServerRef>();
        List<ServerRef> mergerFail = new CopyOnWriteArrayList<ServerRef>();
        for (String hostUrl : mergeServerList) {
            String[] hostTemp = hostUrl.split(":");
            Channel channel = null;
            if (hostTemp.length == 2) {
                // 创建连接
                channel = mileClient
                    .getConnectedChannel(hostTemp[0], Integer.parseInt(hostTemp[1]));
                // 构造服务器描述
                ServerRef ms = new ServerRef();
                ms.setChannel(channel);
                ms.setPort(Integer.parseInt(hostTemp[1]));
                ms.setServerIp(hostTemp[0]);
                ms.setClientUserName(clientConnectMessage.getUserName());
                ms.setClientPassWord(clientConnectMessage.getPassWord());
                ms.setClientProperty(clientConnectMessage.getClientProperty());
                ms.setVersion(clientConnectMessage.getVersion());

                if (channel != null && channel.isConnected()) {
                    // 发送连接消息
                    Message message = clientConnet(clientConnectMessage, ms, mileClient);
                    // 连接验证成功
                    if (message instanceof ClientConnRespOKMessage) {
                        ClientConnRespOKMessage reslutMessage = (ClientConnRespOKMessage) message;
                        ms.setAvailable(true);
                        ms.setOnline(true);
                        ms.setSessionId(reslutMessage.getSessionID());
                        ms.setServerDescription(reslutMessage.getServerDescription());
                        ms.setServerproperties(reslutMessage.getServerproperties());
                        mergerOk.add(ms);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("成功连接MergerServer ->" + channel.getLocalAddress() + "->"
                                        + channel.getRemoteAddress());
                        }
                    } else if (message instanceof ClientConnRespError) {
                        // 连接验证失败
                        ClientConnRespError reslutMessage = (ClientConnRespError) message;
                        ms.setAvailable(false);
                        ms.setConnErrCode(reslutMessage.getConnErrCode());
                        ms.setErrParameter(reslutMessage.getErrParameter());
                        ms.setErrDescription(reslutMessage.getErrDescription());
                        mergerFail.add(ms);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("失败连接MergerServer ->" + channel.getLocalAddress() + "->"
                                        + channel.getRemoteAddress());
                        }
                    } else if (message instanceof CommonErrorMessage) {
                        // merger处于手动离线
                        ms.setAvailable(false);
                        ms.setOnline(false);
                        mergerFail.add(ms);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("MergerServer处于手动离线 ->" + channel.getLocalAddress() + "->"
                                        + channel.getRemoteAddress());
                        }
                    } else if (message == null) {
                        ms.setAvailable(false);
                        mergerFail.add(ms);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("失败的Session验证连接MergerServer ->" + channel.getLocalAddress()
                                        + "->" + channel.getRemoteAddress());
                        }
                    }
                } else {
                    ms.setAvailable(false);
                    mergerFail.add(ms);
                    if(LOGGER.isInfoEnabled()){
                        LOGGER.info("失败的Session验证连接MergerServer");
                    }
                }
            }

        }//for end
        mileClient.getServerRefOk().clear();
        mileClient.getServerRefOk().addAll(mergerOk);
        mileClient.getServerRefFail().clear();
        mileClient.getServerRefFail().addAll(mergerFail);
    }

    /**
     * @param message
     * @param mergeServerInfo
     * @param mileClient
     * @return 发送 连接交互消息
     */
    public static Message clientConnet(ClientConnectMessage message, ServerRef mergeServerInfo,
                                       MileClient mileClient) {
        try {
            SendFuture sendFuture = mileClient.futureSendData(mergeServerInfo.getChannel(),
                message, 10000, true);
            return sendFuture.get();
        } catch (Exception e) {
            LOGGER.warn("连接失败", e);
        }
        return null;

    }

    /**
     * @param message
     * @param mergeServerInfo
     * @param mileClient
     * @return 发送重连交互消息
     */
    public static Message clientReConnet(ClientReConnectMessage message, ServerRef mergeServerInfo,
                                         MileClient mileClient) {
        try {
            SendFuture sendFuture = mileClient.futureSendData(mergeServerInfo.getChannel(),
                message, 10000, true);
            return sendFuture.get();
        } catch (Exception e) {
            LOGGER.warn("发送重连交互消息", e);
        }
        return null;
    }

    /**
     * @param mergeServersOk
     * @param mergeServersFail
     * @param mileClient
     * @return 随机获得服务器
     * @throws SqlExecuteException
     */
    public static ServerRef getRodemMergeServer(MileClient mileClient) throws SqlExecuteException {

        if (mileClient.getServerRefOk().isEmpty()) {
            StringBuffer sb = new StringBuffer(64);
            sb.append("没有可用的MergeServer. MergeServerList:");
            sb.append(mileClient.getServerRefOk().size());
            LOGGER.warn(sb.toString());
            // throw new NullPointerException("没有可用的MergeServerOK");
            return null;
        }

        ServerRef mi = getMergeSI(mileClient.getServerRefOk());

        if (mi == null) {
            checkMergeServer(mileClient);
            mi = getMergeSI(mileClient.getServerRefOk());
        }
        if (mi == null) {
            LOGGER.warn("二次重试后没有可用的MergeServer");
            throw new SqlExecuteException("二次重试后没有可用的MergeServer");
        }
        return mi;
    }

    /**
     * @param mergeServers
     * @return
     */
    private static ServerRef getMergeSI(List<ServerRef> mergeServers) {
        ServerRef mi = null;
        int index = (int) (Math.random() * mergeServers.size());

        mi = mergeServers.get(index);
        if (mi.isAvailable() && mi.getChannel().isConnected()) {
            return mi;
        }
        LOGGER.warn("mergeServers.size()" + mergeServers.size());
        LOGGER.warn("mergeServers.size()" + mergeServers.size());
        LOGGER.warn("没有可用的MergeServer");
        return null;
    }

    /**
     * @param mergeServersOk
     * @param mergeServersFail
     * @param mileClient
     *            维护服务器状态
     */
    public static void checkMergeServer(MileClient mileClient) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + ": checkMergeServer is run");
        }
        Iterator<ServerRef> itok = mileClient.getServerRefOk().iterator();
        ServerRef ms;
        while (itok.hasNext()) {
            ms = itok.next();
            if (ms.getChannel() == null || !ms.getChannel().isConnected() || !ms.isAvailable()
                || !ms.isOnline()) {
                ms.setAvailable(false);
                if (mileClient.getServerRefOk().remove(ms)) {
                    mileClient.getServerRefFail().add(ms);
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("连接MergerServer失败 ->" + ms.getChannel().getLocalAddress() + "->"
                                + ms.getChannel().getRemoteAddress());
                }
            }
        }
        Iterator<ServerRef> itfail = mileClient.getServerRefFail().iterator();
        while (itfail.hasNext()) {
            ms = itfail.next();
            if (ms.getChannel() == null || !ms.getChannel().isConnected()) {
                ms.setAvailable(false);
                Channel channel = mileClient.getConnectedChannel(ms.getServerIp(), ms.getPort());
                ms.setChannel(channel);
            }
            // 发送重连信息
            if (ms.getChannel().isConnected() && !ms.isAvailable()) {
                if (ms.getSessionId() != 0) {
                    // 构造重连消息
                    ClientReConnectMessage clientReConnectMessage = new ClientReConnectMessage();
                    clientReConnectMessage.setClientProperty(ms.getClientProperty());
                    clientReConnectMessage.setPassWord(ms.getClientPassWord());
                    clientReConnectMessage.setUserName(ms.getClientUserName());
                    clientReConnectMessage.setSessionID(ms.getSessionId());
                    clientReConnectMessage.setVersion(ms.getVersion());
                    Message message = ApplationClientService.clientReConnet(clientReConnectMessage,
                        ms, mileClient);
                    // 处理重连结果
                    if (message instanceof ClientConnRespOKMessage) {
                        ClientConnRespOKMessage reslutMessage = (ClientConnRespOKMessage) message;
                        ms.setAvailable(true);
                        ms.setOnline(true);
                        ms.setSessionId(reslutMessage.getSessionID());
                        ms.setServerDescription(reslutMessage.getServerDescription());
                        ms.setServerproperties(reslutMessage.getServerproperties());
                        if (mileClient.getServerRefFail().remove(ms)) {
                            mileClient.getServerRefOk().add(ms);
                        }
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("成功连接MergerServer ->" + ms.getChannel().getLocalAddress()
                                        + "->" + ms.getChannel().getRemoteAddress());
                        }
                    } else if (message instanceof ClientConnRespError) {
                        // 处理重连失败结果
                        ClientConnRespError reslutMessage = (ClientConnRespError) message;
                        ms.setAvailable(false);
                        ms.setConnErrCode(reslutMessage.getConnErrCode());
                        ms.setErrParameter(reslutMessage.getErrParameter());
                        ms.setErrDescription(reslutMessage.getErrDescription());
                    } else if (message instanceof CommonErrorMessage) {
                        // merger处于手动离线
                        ms.setAvailable(false);
                        ms.setOnline(false);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("MergerServer处于手动离线 ->" + ms.getChannel().getLocalAddress()
                                        + "->" + ms.getChannel().getRemoteAddress());
                        }
                    } else if (LOGGER.isInfoEnabled() && message == null) {
                        LOGGER.info("失败的Session验证连接MergerServer ->"
                                    + ms.getChannel().getLocalAddress() + "->"
                                    + ms.getChannel().getRemoteAddress());
                    }
                } else {
                    // 发送连接信息
                    ClientConnectMessage clientConnectMessage = new ClientConnectMessage();
                    clientConnectMessage.setClientProperty(ms.getClientProperty());
                    clientConnectMessage.setPassWord(ms.getClientPassWord());
                    clientConnectMessage.setUserName(ms.getClientUserName());
                    clientConnectMessage.setVersion(ms.getVersion());
                    Message message = ApplationClientService.clientConnet(clientConnectMessage, ms,
                        mileClient);
                    // 处理发挥结果
                    if (message instanceof ClientConnRespOKMessage) {
                        ClientConnRespOKMessage reslutMessage = (ClientConnRespOKMessage) message;
                        ms.setAvailable(true);
                        ms.setSessionId(reslutMessage.getSessionID());
                        ms.setServerDescription(reslutMessage.getServerDescription());
                        ms.setServerproperties(reslutMessage.getServerproperties());
                        mileClient.getServerRefFail().remove(ms);
                        mileClient.getServerRefOk().add(ms);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("成功连接MergerServer ->" + ms.getChannel().getLocalAddress()
                                        + "->" + ms.getChannel().getRemoteAddress());
                        }
                    } else if (message instanceof ClientConnRespError) {
                        // 处理重连失败结果
                        ClientConnRespError reslutMessage = (ClientConnRespError) message;
                        ms.setAvailable(false);
                        ms.setConnErrCode(reslutMessage.getConnErrCode());
                        ms.setErrParameter(reslutMessage.getErrParameter());
                        ms.setErrDescription(reslutMessage.getErrDescription());
                    } else if (message instanceof CommonErrorMessage) {
                        // merger处于手动离线
                        ms.setAvailable(false);
                        ms.setOnline(false);
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("MergerServer处于手动离线 ->" + ms.getChannel().getLocalAddress()
                                        + "->" + ms.getChannel().getRemoteAddress());
                        }
                    } else if (LOGGER.isInfoEnabled() && message == null) {
                        LOGGER.info("失败的Session验证连接MergerServer ->"
                                    + ms.getChannel().getLocalAddress() + "->"
                                    + ms.getChannel().getRemoteAddress());
                    }
                }
            }
            if (!ms.getChannel().isConnected()) {
                ms.setAvailable(false);
                LOGGER.warn("修复失败连接失败" + ms.getServerIp() + ":" + ms.getPort());
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + ": checkMergeServer is end");
        }
    }
}
