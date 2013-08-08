/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.alipay.mile.Config;
import com.alipay.mile.Constants;
import com.alipay.mile.communication.MileClient;
import com.alipay.mile.communication.SendFuture;
import com.alipay.mile.communication.ServerRef;
import com.alipay.mile.message.AccessStateRsMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.m2d.AccessStateMessage;

/**
 * @author jin.qian
 * @version $Id: ClientMergeServicesTimerTask.java,v 0.1 2011-4-6 下午05:49:52
 *          jin.qian Exp $
 */
public class CheckDocServersTimerTask implements Runnable {

    private static final Logger   LOGGER = Logger.getLogger(CheckDocServersTimerTask.class);

    private final List<ServerRef> serverRefOk;
    private final List<ServerRef> serverRefFail;
    private final MileClient      engineConnector;

    public CheckDocServersTimerTask(MileClient engineConnector) {
        this.serverRefOk = engineConnector.getServerRefOk();
        this.serverRefFail = engineConnector.getServerRefFail();
        this.engineConnector = engineConnector;
    }

    /**
     * 向docserver发送状态获取报文，获取docserver的状态
     * @param channel	向docserver发送报文的channel
     * @return			范围值为true时表明docserver可用，为false时表明docserver不可用
     */
    public static boolean getDocServerState(Channel channel, MileClient mileClient) {
        //如果开关关闭直接返回true
        if (!Config.getAllowMergerServerCheckDocServer()) {
            return true;
        }
        AccessStateMessage accessStateMessage = new AccessStateMessage();
        List<String> states = new ArrayList<String>();
        states.add(Constants.DOCSERVER_STATE_READABLE);
        accessStateMessage.setStates(states);
        boolean state = false;

        try {
            SendFuture sendFuture = mileClient.futureSendData(channel, accessStateMessage,
                Constants.GET_STATE_TIME_OUT, true);
            Message message = sendFuture.get();
            if (message instanceof AccessStateRsMessage) {
                AccessStateRsMessage stateRsMessage = (AccessStateRsMessage) message;
                byte readable = (Byte) stateRsMessage.getStates().get(
                    Constants.DOCSERVER_STATE_READABLE);
                if (readable == 1) {
                    state = true;
                }
            } else {
                LOGGER.error("向docserver查询状态时, docserver返回错误数据包 " + message);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } catch (ExecutionException e) {
            LOGGER.error(e);
        }

        return state;
    }

    /**
     * 定时检测服务器状态
     *
     */
    @Override
    public void run() {
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("定时检测服务器状态 开始");
        }
        Iterator<ServerRef> itok = serverRefOk.iterator();
        ServerRef ms;
        // 检测可用连接可用状态
        while (itok.hasNext()) {
            ms = itok.next();
            if (ms.getChannel() == null || !ms.getChannel().isConnected() || !ms.isAvailable()
                || !ms.isOnline() || !getDocServerState(ms.getChannel(), engineConnector)) {
                ms.setAvailable(false);
                if (serverRefOk.remove(ms)) {
                    serverRefFail.add(ms);
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("检测到失败连接转移到失败DocServer列表 ->" + ms.getServerIp() + ":"
                                + ms.getPort());
                }
            }
        }
        // 检测非可用连接的可用状态，重新连接非可用连接使其变成可用，维护可用连接列表
        Iterator<ServerRef> itfail = serverRefFail.iterator();
        while (itfail.hasNext()) {
            ms = itfail.next();
            if ((ms.getChannel() == null || !ms.getChannel().isConnected()) && ms.isOnline()) {
                ms.setAvailable(false);
                Channel channel = engineConnector.getConnectedChannel(ms.getServerIp(), ms
                    .getPort());
                if (channel.isConnected() && getDocServerState(channel, engineConnector)) {
                    ms.setAvailable(true);
                    ms.setChannel(channel);
                    if (serverRefFail.remove(ms)) {
                        serverRefOk.add(ms);
                    }
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("成功连接DocServer ->" + ms.getChannel().getLocalAddress() + "->"
                                    + ms.getChannel().getRemoteAddress());
                    }
                } else {
                    ms.setAvailable(false);
                }
            }
            else if (ms.getChannel() != null && ms.getChannel().isConnected() && ms.isOnline()
                       && getDocServerState(ms.getChannel(), engineConnector)) {
                ms.setAvailable(true);
                if (serverRefFail.remove(ms)) {
                    serverRefOk.add(ms);
                }
            }
        }
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("定时检测服务器状态 结束");
        }
    }

}
