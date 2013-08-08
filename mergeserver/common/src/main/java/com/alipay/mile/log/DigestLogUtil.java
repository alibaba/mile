/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.alipay.mile.util.SimpleThreadFactory;

/**
 * 摘要日志输出器，定时打印各种摘要日志
 * 
 * @author yuzhong.zhao
 * @version $Id: DigestLogUtil.java, v 0.1 2012-5-23 下午08:24:44 yuzhong.zhao Exp $
 */
public class DigestLogUtil {

    /** 日志打印调度器 */
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1,
                                                         new SimpleThreadFactory(
                                                             "DigestLogPrintSchedule", true));

    /**
     * 注册日志定时打印任务
     * 
     * @param command
     * @param initialDelay
     * @param delay
     * @param unit
     */
    public synchronized static void registDigestTask(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        SCHEDULER.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }
}
