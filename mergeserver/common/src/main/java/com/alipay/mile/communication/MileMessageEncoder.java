/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * @author jin.qian
 * @version $Id: MileMessageEncoder.java,v 0.1 2011-4-6 下午04:58:25 jin.qian Exp $
 * 解码器
 */
public class MileMessageEncoder extends OneToOneEncoder {

    //    private static final Logger logger = Logger.getLogger(MileMessageEncoder.class.getName());

    /** 
     * @see org.jboss.netty.handler.codec.oneone.OneToOneEncoder#encode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, java.lang.Object)
     */
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) {
        //处理byte[]消息
        if (!(msg instanceof byte[])) {
            return msg;// (1)
        }
        byte[] data = (byte[]) msg;
        //消息放入发送缓冲区
        return ChannelBuffers.wrappedBuffer(data);// (3)
    }

}
