/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author jin.qian
 * @version $Id: ChannelPipelLineTimerTask.java,v 0.1 2011-4-6 上午10:19:18 jin.qian Exp $
 * ChannelPipelLineTimerTask 是定时调度任务 主要是清理由于务返回结果而照成的 SendFuture 垃圾对象
 *
 */
public class SendFutureCleanTimerTask implements Runnable {

    private static final Logger      LOGGER = Logger.getLogger(SendFutureCleanTimerTask.class
                                                .getName());
    private Map<Integer, SendFuture> sendDataHandles;

    SendFutureCleanTimerTask(Map<Integer, SendFuture> sendDataHandles) {
        this.sendDataHandles = sendDataHandles;
    }

    /**
     * 定时清理超时的 sendFuture
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + " 调度开始");
        }
        Iterator<Integer> it = sendDataHandles.keySet().iterator();
        int key;
        while (it.hasNext()) {
            key = it.next();
            SendFuture sf = sendDataHandles.get(key);
            if (sf != null && isTimeOut(sf)) {
                sendDataHandles.remove(key);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("清理过期的sendFuture对象 ：" + sf.getMessageId() + " 发送时间: "
                                 + sf.getSendTime());
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + " 调度结束");
        }
    }

    /**
     * @param sendFuture
     *
     * @return
     * 超时是根据SendFuture 对象创建时间判断。
     */
    private boolean isTimeOut(SendFuture sendFuture) {
        if ((sendFuture.getSendTime() + sendFuture.getTimeOut()) < System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    public Map<Integer, SendFuture> getSendDataHandles() {
        return sendDataHandles;
    }

    public void setSendDataHandles(Map<Integer, SendFuture> sendDataHandles) {
        this.sendDataHandles = sendDataHandles;
    }

}
