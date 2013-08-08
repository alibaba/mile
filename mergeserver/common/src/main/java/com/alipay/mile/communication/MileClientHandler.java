/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.alipay.mile.message.MergerDocHeartcheck;
import com.alipay.mile.message.Message;
import com.alipay.mile.message.MessageFactory;

/**
 * @author jin.qian
 * @version $Id: MileClientHandler.java,v 0.1 2011-4-6 下午04:49:59 jin.qian Exp $
 * 收到消息处理器
 */
public class MileClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger LOGGER = Logger.getLogger(MileClientHandler.class.getName());

    /**
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("创建连接: " + e.getChannel().getLocalAddress() + "-->"
                        + e.getChannel().getRemoteAddress());
        }
    }

    /**
     * @param ctx
     * @param e
     * @throws Exception
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     * 断开连接后处理 服务器列表 维护服务器状态
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //取出可用服务器列表、非可用服务器列表
        MileClientChannelPipeline mileChannelPipeline = (MileClientChannelPipeline) e.getChannel()
            .getPipeline();
        List<ServerRef> serversOk = mileChannelPipeline.getServersOk();
        List<ServerRef> serversFail = mileChannelPipeline.getServersFail();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("断开连接: " + e.getChannel().getLocalAddress() + "-->"
                        + e.getChannel().getRemoteAddress());
        }
        //标识服务器状态
        for (ServerRef ms : serversOk) {
            if (ms.getChannel().getId().equals(e.getChannel().getId())) {
                ms.setAvailable(false);
                //将当前服务器从可用列表移到非可用列表
                if (serversOk.remove(ms)) {
                    serversFail.add(ms);
                }
            }
        }
        //断开的连接有可能是服务不可用的状态。需遍历不可用服务器列表
        for (ServerRef ms : serversFail) {
            if (ms.getChannel().getId().equals(e.getChannel().getId())) {
                ms.setAvailable(false);
            }
        }

        //事件上传
        ctx.sendUpstream(e);

    }

    /**
     * @param ctx
     * @param e
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
     * 收到消息 处理 Future 对象
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        byte[] data = (byte[]) e.getMessage();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("收到消息  ID:" + MessageFactory.getMessageId(data));
        }
        if (MessageFactory.getMessageType(data) == Message.MT_MD_HEART) {
            MergerDocHeartcheck mergerDocHeartcheck = (MergerDocHeartcheck) MessageFactory
                .toMessage(data);
            if (mergerDocHeartcheck.getState() == 1) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("收到心跳包");
                }
                mergerDocHeartcheck.setState((short) 2);
                try {
                    e.getChannel().write(mergerDocHeartcheck.toBytes());
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Doc心跳检测应答");
                    }
                } catch (IOException e1) {
                    LOGGER.error("Doc心跳检测出错", e1);
                }
            }
        } else {
            MileClientChannelPipeline mileChannelPipeline = (MileClientChannelPipeline) e
                .getChannel().getPipeline();
            //取得发送handle
            SendFuture sendDataHandle = mileChannelPipeline.getSendDataHandles().get(
                MessageFactory.getMessageId(data));
            if (sendDataHandle != null) {
                //转化数据成message对象 赋值结果集
                sendDataHandle.setResult(MessageFactory.toMessage(data));
                sendDataHandle.setResultTime(System.currentTimeMillis());
                synchronized (sendDataHandle) {
                    //激活异步线程
                    sendDataHandle.notifyAll();
                }
                //清除 sendFuture
                mileChannelPipeline.getSendDataHandles().remove(MessageFactory.getMessageId(data));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("remove sendDataHandle:" + MessageFactory.getMessageId(data));
                }
            } else {
                LOGGER.error("sendDataHandle is Null--messageID: "
                             + MessageFactory.getMessageId(data));
            }
        }
    }

    /**
     * @param ctx
     * @param e
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        LOGGER.error("通信异常远端地址" + ctx.getChannel().getRemoteAddress(), e.getCause());
        ctx.sendUpstream(e);
    }
}
