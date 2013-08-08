/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import org.jboss.netty.channel.DefaultChannelPipeline;

/**
 * @author jin.qian
 * @version $Id: MileServerChannelPipeline.java,v 0.1 2011-4-6 ÏÂÎç05:10:08 jin.qian Exp $
 */
public class MileServerChannelPipeline extends DefaultChannelPipeline {

    private MessageManager messageManager;

    /**
     * @param messageManager
     */
    public MileServerChannelPipeline(MessageManager messageManager) {
        super();
        this.messageManager = messageManager;
    }

    /**
     * @param messageManager
     */
    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /**
     * @return
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }
}
