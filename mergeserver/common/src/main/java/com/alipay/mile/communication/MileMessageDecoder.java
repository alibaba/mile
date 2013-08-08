/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * @author jin.qian
 * @version $Id: MileMessageDecoder.java,v 0.1 2011-4-6 下午04:57:49 jin.qian Exp $
 * 编码器
 */
public class MileMessageDecoder extends FrameDecoder {

    private static final Logger LOGGER = Logger.getLogger(MileMessageDecoder.class.getName());

    /** 
     * @see org.jboss.netty.handler.codec.frame.FrameDecoder#decode(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.Channel, org.jboss.netty.buffer.ChannelBuffer)
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {
        //消息长度不足 返回
        if (buffer.readableBytes() < 4) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("消息长度不足 ");
            }
            return null;
        }
        //消息长度
        int dataLength = buffer.getInt(buffer.readerIndex());
        //消息没收完整 返回继续接收
        if (buffer.readableBytes() < dataLength) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("消息没收完整 返回继续接收 ");
            }
            return null;
        }
        //获取完整消息
        byte[] decoded = new byte[dataLength];
        buffer.readBytes(decoded);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("获取完整消息 ");
        }
        return decoded;
    }

}
