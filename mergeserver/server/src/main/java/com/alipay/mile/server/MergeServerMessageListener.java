/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ExceptionEvent;

import com.alipay.mile.communication.MessageListener;
import com.alipay.mile.communication.ServerRef;
import com.alipay.mile.message.ClientConnectMessage;
import com.alipay.mile.message.ClientReConnectMessage;
import com.alipay.mile.message.CommonErrorMessage;
import com.alipay.mile.message.CommonOkMessage;
import com.alipay.mile.message.DocStartCommandMessage;
import com.alipay.mile.message.DocStopCommandMessage;
import com.alipay.mile.message.MergeStartCommandMessage;
import com.alipay.mile.message.MergeStartLeadQueryMessage;
import com.alipay.mile.message.MergeStopCommandMessage;
import com.alipay.mile.message.MergeStopLeadQueryMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.MessageFactory;
import com.alipay.mile.message.SqlExectueErrorMessage;
import com.alipay.mile.message.SqlExecuteMessage;
import com.alipay.mile.message.SqlPreExecuteMessage;
import com.alipay.mile.message.m2d.SpecifyQueryExecuteMessage;

/**
 * 通信消息接收侦听器
 *
 * @author jin.qian
 * @version $Id: MergeServerMessageListener.java, v 0.1 2011-5-10 下午01:37:26
 *          jin.qian Exp $
 */
public class MergeServerMessageListener implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(MergeServerMessageListener.class
                                           .getName());
    private final ProxyServer   server;

    public MergeServerMessageListener(ProxyServer server) {
        this.server = server;
    }

    /**
     * @see com.alipay.mile.communication.MessageListener#receiveMessage(byte[],
     *      org.jboss.netty.channel.Channel)
     */
    @Override
    public void receiveMessage(byte[] data, Channel channel, long messageStartTime)
                                                                                   throws IOException {
        long startTime = System.currentTimeMillis();
        Message request = MessageFactory.toMessage(data);
        Message response = null;
        if (request instanceof SqlPreExecuteMessage) {// SqlPreExecuteMessage
            SqlPreExecuteMessage requestMessage = (SqlPreExecuteMessage) request;
            if ((startTime - messageStartTime) >= requestMessage.getExeTimeout()) {
                LOGGER.error("处理sql消息在队列里等待超时 --MessageID: " + requestMessage.getId());
                SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
                resMessage.setId(requestMessage.getId());
                resMessage.setVersion(requestMessage.getVersion());
                resMessage.setErrDescription("处理sql消息在队列里等待超时--MessageID: "
                                             + requestMessage.getId());
                response = resMessage;
            } else if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                // 处理预编译消息
                requestMessage
                    .setExeTimeout((int) (requestMessage.getExeTimeout() - (startTime - messageStartTime)));
                response = server.processSqlPreExecuteMessage(requestMessage, channel
                    .getRemoteAddress().toString());
            }
        } else if (request instanceof SqlExecuteMessage) {// SqlExecuteMessage
            SqlExecuteMessage requestMessage = (SqlExecuteMessage) request;
            if ((startTime - messageStartTime) >= requestMessage.getExeTimeout()) {
                LOGGER.error("处理sql消息在队列里等待超时 --MessageID: " + requestMessage.getId());
                SqlExectueErrorMessage resMessage = new SqlExectueErrorMessage();
                resMessage.setId(requestMessage.getId());
                resMessage.setVersion(requestMessage.getVersion());
                resMessage.setErrDescription("处理sql消息在队列里等待超时--MessageID: "
                                             + requestMessage.getId());
                response = resMessage;

            } else if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                // 处理sql 消息
                requestMessage
                    .setExeTimeout((int) (requestMessage.getExeTimeout() - (startTime - messageStartTime)));
                response = server.processSqlExecuteMessage(requestMessage, channel
                    .getRemoteAddress().toString());
            }
        } else if (request instanceof SpecifyQueryExecuteMessage) {// SqlExecuteMessage
            if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                // 处理sql 消息
                response = server
                    .processSpecifyQueryExecuteMessage((SpecifyQueryExecuteMessage) request);
            }
        } else if (request instanceof ClientConnectMessage) {// ClientConnectMessage
            // 处理连接消息
            if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                response = server.processClientConnectMessage((ClientConnectMessage) request);
            }
        } else if (request instanceof ClientReConnectMessage) {// ClientReConnectMessage
            // 处理重连消息
            if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                response = server.processClientReConnectMessage((ClientReConnectMessage) request);
            }
        } else if (request instanceof MergeStartCommandMessage) {// MergeStartCommandMessage
            // 处理手动上线消息
            server.getServer().setOnline(true);
            CommonOkMessage commonOkMessage = new CommonOkMessage();
            commonOkMessage.setOkDescription("mergerServer 处于手动上线状态");
            response = commonOkMessage;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("mergerServer 处于手动上线状态");
            }
        } else if (request instanceof MergeStopCommandMessage) {// MergeStopCommandMessage
            // 处理手动离线消息
            server.getServer().setOnline(false);
            CommonOkMessage commonOkMessage = new CommonOkMessage();
            commonOkMessage.setOkDescription("mergerServer 处于手动离线状态");
            response = commonOkMessage;
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("mergerServer 处于手动离线状态 ");
            }
        } else if (request instanceof DocStartCommandMessage) {// DocStartCommandMessage
            // 处理重连消息
            DocStartCommandMessage dsc = (DocStartCommandMessage) request;
            ServerRef sr;
            Iterator<ServerRef> it = server.getClient().getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if ((sr.getServerIp() + ":" + sr.getPort()).equals(dsc.getDocServerIp())) {
                    sr.setOnline(true);
                    CommonOkMessage commonOkMessage = new CommonOkMessage();
                    commonOkMessage.setOkDescription("DocServer " + dsc.getDocServerIp()
                                                     + " 处于手动上线状态");
                    response = commonOkMessage;
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("DocServer " + dsc.getDocServerIp() + " 处于手动上线状态");
                    }
                    break;
                }
            }
            if (response == null) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("没有找到需手动上线的DocServer "
                                                       + dsc.getDocServerIp());
                response = commonErrorMessage;
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("没有找到需手动上线的DocServe " + dsc.getDocServerIp());
                }
            }
        } else if (request instanceof DocStopCommandMessage) {// DocStopCommandMessage
            // 处理Doc离线消息
            DocStopCommandMessage dsc = (DocStopCommandMessage) request;
            Iterator<ServerRef> it = server.getClient().getServerRefOk().iterator();
            ServerRef sr;
            while (it.hasNext()) {
                sr = it.next();
                if ((sr.getServerIp() + ":" + sr.getPort()).equals(dsc.getDocServerIp())) {
                    sr.setOnline(false);
                    if (server.getClient().getServerRefOk().remove(sr)) {
                        server.getClient().getServerRefFail().add(sr);
                    }
                    break;
                }
            }
            it = server.getClient().getServerRefFail().iterator();
            while (it.hasNext()) {
                sr = it.next();
                if ((sr.getServerIp() + ":" + sr.getPort()).equals(dsc.getDocServerIp())) {
                    sr.setOnline(false);
                    CommonOkMessage commonOkMessage = new CommonOkMessage();
                    commonOkMessage.setOkDescription("DocServer " + dsc.getDocServerIp()
                                                     + " 处于手动离线状态");
                    response = commonOkMessage;
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("DocServer " + dsc.getDocServerIp() + " 处于手动离线状态 ");
                    }
                    break;
                }
            }
            if (response == null) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("没有找到需手动离线的DocServer "
                                                       + dsc.getDocServerIp());
                response = commonErrorMessage;
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("没有找到需手动离线的DocServer " + dsc.getDocServerIp());
                }
            }
        } else if (request instanceof MergeStartLeadQueryMessage) {
            if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                response = server.processStartLeadQueryMessage((MergeStartLeadQueryMessage) request);
            }
            
        } else if (request instanceof MergeStopLeadQueryMessage) {
            if (!server.getServer().isOnline()) {
                CommonErrorMessage commonErrorMessage = new CommonErrorMessage();
                commonErrorMessage.setErrorDescription("mergerServer 处于手动离线状态");
                commonErrorMessage.getErrorParameters().add(channel.getLocalAddress().toString());
                response = commonErrorMessage;
            } else {
                response = server.processStopLeadQueryMessage((MergeStopLeadQueryMessage) request);
            }
        }

        // return
        assert response != null : "Process request message NO response message";
        response.setId(request.getId());
        // 返回结果
        channel.write(MessageFactory.toSendMessage(response));
    }

    /**
     * @see com.alipay.mile.communication.MessageListener#handleException(org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void handleException(ExceptionEvent e) {
        LOGGER.error("handelException" + e.getCause());
    }

}
