/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.communication;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.alipay.mile.log.DigestLogUtil;
import com.alipay.mile.util.SimpleThreadFactory;

/**
 * @author jin.qian
 * @version $Id: MessageManager.java,v 0.1 2011-4-6 上午10:51:34 jin.qian Exp $
 * listener 管理类，
 * exec 是异步处理 消息队列
 */
public class MessageManager {

    private static final Logger   DIGESTLOGGER = Logger.getLogger("COMMON-DIGEST");

    /** 消息listenters 的注册容器 */
    private List<MessageListener> messagelisteners;

    /**消息处理线程池 任务对列我限制 、线程数cpu个数  */
    public final ThreadPoolExecutor  exec;
    public final ThreadPoolExecutor  queryExec;

    
    private class QueueDigestPrint implements Runnable{
        @Override
        public void run() {
            if(DIGESTLOGGER.isInfoEnabled()){
                DIGESTLOGGER.info("当前插入队列长度:" + exec.getQueue().size());
                DIGESTLOGGER.info("当前查询队列长度:" + queryExec.getQueue().size());
            }
        }
    }
    
    public MessageManager(int execMin, int execMax, int queryExecMin, int queryExecMax,
                          int keepAliveTime, int blockingQueueCount) {
        exec = new ThreadPoolExecutor(execMin, execMax, keepAliveTime, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(blockingQueueCount), new SimpleThreadFactory(
                "MessageManager-exec-core", false), new ThreadPoolExecutor.CallerRunsPolicy());
        queryExec = new ThreadPoolExecutor(queryExecMin, queryExecMax, keepAliveTime,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(blockingQueueCount),
            new SimpleThreadFactory("MessageManager-queryexec-core", false),
            new ThreadPoolExecutor.CallerRunsPolicy());
        messagelisteners = new CopyOnWriteArrayList<MessageListener>();
        
        // 注册摘要日志打印任务, 每隔60s打印一次
        DigestLogUtil.registDigestTask(new QueueDigestPrint(), 30, 60, TimeUnit.SECONDS);
    }

    /**
     * 注册listener
     * @param MessageListener 接收消息的listener
     */
    public void addMessageListener(MessageListener listener) {
        messagelisteners.add(listener);
    }

    /**
     * 返回MessageListeners 列表
     * @return List<MessageListener>
     */
    public List<MessageListener> getMessagelisteners() {
        return messagelisteners;
    }

    /**
     * 删除Listener
     * @param MessageListener
     */
    public void rmoveMessageListener(MessageListener listener) {
        messagelisteners.remove(listener);
    }

    /**
     * 设置Messagelisteners
     * @param messagelisteners
     */
    public void setMessagelisteners(List<MessageListener> messagelisteners) {
        this.messagelisteners = messagelisteners;
    }

}
