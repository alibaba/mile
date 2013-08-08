/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.alipay.mile.message.Message;
import com.alipay.mile.message.MessageFactory;

/**
 * @author jin.qian
 * @version $Id: MileServerHandler.java,v 0.1 2011-4-6 下午05:10:46 jin.qian Exp $
 * 服务端接收消息处理
 */
public class MileServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger LOGGER = Logger.getLogger(MileServerHandler.class.getName());

    /**
     * @param ctx
     * @param e
     * @throws Exception
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("创建连接: " + e.getChannel().getLocalAddress() + "-->"
                        + e.getChannel().getRemoteAddress());
        }
        ctx.sendUpstream(e);
    }

    /**
     * @param ctx
     * @param e
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
     * 接收消息处理 并发处理 messageListener
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        MileServerChannelPipeline ChannelPipeline = (MileServerChannelPipeline) e.getChannel()
            .getPipeline();
        List<MessageListener> messageListeners = ChannelPipeline.getMessageManager()
            .getMessagelisteners();
        Iterator<MessageListener> it = messageListeners.iterator();
        //遍历消息管理器 将消息通知到每个侦听器中
        while (it.hasNext()) {
            MessageListener messageListener = it.next();
            //消息管理器并发处理线程池队列处理消息
            byte[] data = (byte[]) e.getMessage();
            Short messageType = MessageFactory.getMessageType(data);
            if (messageType == Message.MT_CM_Q_SQL || messageType == Message.MT_CM_PRE_Q_SQL) {
                ChannelPipeline.getMessageManager().queryExec.submit(new ReceiveDataHandle(data, e
                    .getChannel(), messageListener));
            } else {
                ChannelPipeline.getMessageManager().exec.submit(new ReceiveDataHandle(data, e
                    .getChannel(), messageListener));
            }
        }

    }

    /**
     * @param ctx
     * @param e
     * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
     * 异常分发到各个MessageListener
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        MileServerChannelPipeline ChannelPipeline = (MileServerChannelPipeline) e.getChannel()
            .getPipeline();
        List<MessageListener> messageListeners = ChannelPipeline.getMessageManager()
            .getMessagelisteners();
        //异常事件通知到 messageListeners
        for (Iterator<MessageListener> messageListenerIterator = messageListeners.iterator(); messageListenerIterator
            .hasNext();) {
            MessageListener messageListener = messageListenerIterator.next();
            messageListener.handleException(e);
        }
        LOGGER.warn("Merge服务端通信异常" + ctx.getChannel().getRemoteAddress(), e.getCause());
        ctx.sendUpstream(e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("断开连接: " + e.getChannel().getLocalAddress() + "-->"
                        + e.getChannel().getRemoteAddress());
        }
        //断开连接
        e.getChannel().close();
        //事件上传
        ctx.sendUpstream(e);
    }
}
