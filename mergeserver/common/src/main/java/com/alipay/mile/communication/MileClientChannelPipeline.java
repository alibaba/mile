/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.DefaultChannelPipeline;

/**
 * @author jin.qian
 * @version $Id: MileClientChannelPipeline.java,v 0.1 2011-4-6 下午02:44:58 jin.qian Exp $
 * Client 初始化相关类
 */
public class MileClientChannelPipeline extends DefaultChannelPipeline {

    /** SendFuture对象集合用于通信返回时的 结果集查找赋值 */
    private Map<Integer, SendFuture> sendDataHandles;
    /**可用server列表  */
    private List<ServerRef>          serversOk;
    /**不可用状态的server列表  */
    private List<ServerRef>          serversFail;

    /**
     * @param sendDataHandles
     */
    public void setSendDataHandles(Map<Integer, SendFuture> sendDataHandles) {
        this.sendDataHandles = sendDataHandles;
    }

    /**
     * @return
     */
    public Map<Integer, SendFuture> getSendDataHandles() {
        return sendDataHandles;
    }

    /**
     * @return
     */
    public List<ServerRef> getServersOk() {
        return serversOk;
    }

    /**
     * @param mergeServersOk
     */
    public void setServersOk(List<ServerRef> serversOk) {
        this.serversOk = serversOk;
    }

    /**
     * @return
     */
    public List<ServerRef> getServersFail() {
        return serversFail;
    }

    /**
     * @param mergeServersFail
     */
    public void setServersFail(List<ServerRef> serversFail) {
        this.serversFail = serversFail;
    }
}
