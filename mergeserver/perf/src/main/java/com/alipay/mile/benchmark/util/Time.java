/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package com.alipay.mile.benchmark.util;

import java.util.Date;

/**
 * The interface to time itself. Oh wow, your head totally just exploded.
 * 
 * 
 */
public interface Time {

    public final static long US_PER_MS        = 1000;
    public final static long NS_PER_US        = 1000;
    public final static long NS_PER_MS        = US_PER_MS * NS_PER_US;
    public final static long MS_PER_SECOND    = 1000;
    public final static long US_PER_SECOND    = US_PER_MS * MS_PER_SECOND;
    public final static long NS_PER_SECOND    = NS_PER_US * US_PER_SECOND;
    public final static long SECONDS_PER_HOUR = 60 * 60;
    public final static long SECONDS_PER_DAY  = 24 * SECONDS_PER_HOUR;
    public final static long MS_PER_HOUR      = SECONDS_PER_HOUR * MS_PER_SECOND;
    public final static long MS_PER_DAY       = SECONDS_PER_DAY * MS_PER_SECOND;

    public long getMilliseconds();

    public long getNanoseconds();

    public int getSeconds();

    public Date getCurrentDate();

    public void sleep(long ms) throws InterruptedException;

}
