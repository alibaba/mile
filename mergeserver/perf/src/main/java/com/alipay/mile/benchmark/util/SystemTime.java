/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package com.alipay.mile.benchmark.util;

import java.util.Date;

public class SystemTime implements Time {

    public static final SystemTime INSTANCE = new SystemTime();

    public Date getCurrentDate() {
        return new Date();
    }

    public long getMilliseconds() {
        return System.currentTimeMillis();
    }

    public long getNanoseconds() {
        return System.nanoTime();
    }

    public int getSeconds() {
        return (int) (getMilliseconds() / MS_PER_SECOND);
    }

    public void sleep(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

}
