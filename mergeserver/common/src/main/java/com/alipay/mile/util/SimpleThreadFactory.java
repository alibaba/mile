package com.alipay.mile.util;

import java.util.concurrent.ThreadFactory;

/**
 * simple thread factory
 * 
 * @author liangjie.li
 * @version $Id: SimpleThreadFactory.java, v 0.1 2011-6-7 ÉÏÎç11:42:50 liangjie.li Exp $
 */
public class SimpleThreadFactory implements ThreadFactory {

    private String       threadName;
    private boolean      isDaemon;
    private volatile int count = 0;

    /**
     * 
     * @param threadName
     * @param isDaemon
     */
    public SimpleThreadFactory(String threadName, boolean isDaemon) {
        this.threadName = threadName;
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(isDaemon);
        t.setName(threadName + count);
        t.setPriority(Thread.NORM_PRIORITY);
        count++;
        return t;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isDaemon() {
        return isDaemon;
    }

    public void setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
    }

}
