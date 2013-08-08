/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.io.IOException;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ExceptionEvent;

/**
 * @author jin.qian
 * @version $Id: MessageListener.java,v 0.1 2011-4-6 上午10:45:12 jin.qian Exp $
 * 处理异步通知事件
 * 如果需要接受消息，需要实现这个接口，并将实现类注入到MessageManager中。
 */
public interface MessageListener {

    /**
     * 收到消息处理的接口
     * @param data 收到的数据包
     * @param channel 通信用的channel
     * @throws IOException
     */
    public void receiveMessage(byte[] data, Channel channel, long messageStartTime)
                                                                                   throws IOException;

    /**
     * 异常处理
     * @param e Netty 封装的 异常
     */
    public void handleException(ExceptionEvent e);

}
