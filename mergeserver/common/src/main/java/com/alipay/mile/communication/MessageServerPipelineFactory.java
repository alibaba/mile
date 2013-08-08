/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

/**
 * @author jin.qian
 * @version $Id: MessageServerPipelineFactory.java,v 0.1 2011-4-6 上午10:56:28 jin.qian Exp $
 * 通讯层 服务端初始化工厂赋值
 */
public class MessageServerPipelineFactory implements ChannelPipelineFactory {

    private static final Logger LOGGER = Logger.getLogger(MessageServerPipelineFactory.class
                                           .getName());

    /** 消息管理器 */
    private MessageManager      messageManager;

    /**
     * @param messageManager 消息管理器
     */
    public MessageServerPipelineFactory(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /**
     * @return 过滤器pipeline
     * @throws Exception
     * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
     */
    @Override
    public ChannelPipeline getPipeline() {
        //对请求处理过程实现了过滤器链模式ChannelPipeline
        ChannelPipeline pipeline = new MileServerChannelPipeline(messageManager);
        //添加解码器
        pipeline.addLast("decoder", new MileMessageDecoder());
        //添加编码器
        pipeline.addLast("encoder", new MileMessageEncoder());
        //添加 事件处理器
        pipeline.addLast("handler", new MileServerHandler());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("加载 ->MileMessageDecoder,MileMessageEncoder,MileServerHandler");
        }
        return pipeline;
    }

    /**
     * 设置消息管理器
     * @param messageManager
     */
    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /**
     * 取得消息管理器
     * @return MessageManager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

}
