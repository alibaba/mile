/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

/**
 * @author jin.qian
 * @version $Id: MessageClientPipelineFactory.java,v 0.1 2011-4-6 上午10:34:46 jin.qian Exp $
 * 通讯层 客户端初始化工厂赋值
 * 
 */
public class MessageClientPipelineFactory implements ChannelPipelineFactory {

    private static final Logger      LOGGER = Logger.getLogger(MessageClientPipelineFactory.class
                                                .getName());

    /** SendFuture对象集合用于通信返回时的 结果集查找赋值 */
    private Map<Integer, SendFuture> sendDataHandles;

    /**可用server列表  */
    private List<ServerRef>          serversOk;

    /**不可用状态的server列表  */
    private List<ServerRef>          serversFail;

    /**
     * @param serversOk 可用server列表
     * @param serversFail 不可用server列表
     * @param sendDataHandles SendFuture对象
     */
    MessageClientPipelineFactory(List<ServerRef> serversOk, List<ServerRef> serversFail,
                                 Map<Integer, SendFuture> sendDataHandles) {
        super();
        this.sendDataHandles = sendDataHandles;
        this.serversOk = serversOk;
        this.serversFail = serversFail;
    }

    /** 
     * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
     */
    @Override
    public ChannelPipeline getPipeline() {
        //对请求处理过程实现了过滤器链模式ChannelPipeline
        MileClientChannelPipeline pipeline = new MileClientChannelPipeline();
        //添加解码器
        pipeline.addLast("decoder", new MileMessageDecoder());
        //添加编码器
        pipeline.addLast("encoder", new MileMessageEncoder());
        //添加 事件处理器
        pipeline.addLast("handler", new MileClientHandler());
        //设置不可用服务器列表
        pipeline.setServersFail(serversFail);
        //设置可用服务器列表
        pipeline.setServersOk(serversOk);
        //设置SendFuture对象集合
        pipeline.setSendDataHandles(sendDataHandles);
        if (LOGGER.isInfoEnabled()) {
            LOGGER
                .info("加载 ->MileMessageDecoder,MileMessageEncoder, MileClientHandler,MergeServersFail,MergeServersOk");
        }
        return pipeline;
    }

    public Map<Integer, SendFuture> getSendDataHandles() {
        return sendDataHandles;
    }

    public void setSendDataHandles(Map<Integer, SendFuture> sendDataHandles) {
        this.sendDataHandles = sendDataHandles;
    }

    public List<ServerRef> getServersOk() {
        return serversOk;
    }

    public void setServersOk(List<ServerRef> serversOk) {
        this.serversOk = serversOk;
    }

    public List<ServerRef> getServersFail() {
        return serversFail;
    }

    public void setServersFail(List<ServerRef> serversFail) {
        this.serversFail = serversFail;
    }
}
