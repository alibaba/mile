/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

/**
 * @author jin.qian
 * @version $Id: ReceiveDataHandle.java,v 0.1 2011-4-6 下午05:13:44 jin.qian Exp $
 */
public class ReceiveDataHandle implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ReceiveDataHandle.class.getName());

    private byte[]              data;
    private Channel             channel;
    private MessageListener     messageListener;
    private long                messageStartTime;

    public ReceiveDataHandle(byte[] data, Channel channel, MessageListener messageListener) {
        this.data = data;
        this.channel = channel;
        this.messageListener = messageListener;
        this.messageStartTime = System.currentTimeMillis();
    }

    /**
     * 多线成处理收到消息
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            messageListener.receiveMessage(data, channel, messageStartTime);
        } catch (IOException e) {
            LOGGER.error("消息处理异常", e);
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public long getMessageStartTime() {
        return messageStartTime;
    }

    public void setMessageStartTime(long messageStartTime) {
        this.messageStartTime = messageStartTime;
    }
}
