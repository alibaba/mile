/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.log;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: RequestDigest.java, v 0.1 2012-5-24 ÉÏÎç09:40:07 yuzhong.zhao Exp $
 */
public class RequestDigest {
    private int  count;
    private int  timeOutCount;
    private int  errCount;
    private long totalExcTime;

    public RequestDigest(int count, int timeOutCount, int errCount, long totalExcTime) {
        this.count = count;
        this.timeOutCount = timeOutCount;
        this.errCount = errCount;
        this.totalExcTime = totalExcTime;
    }

    public double getAverageTime() {
        return count > 0 ? 1f * totalExcTime / count : -0f;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(count).append(",");
        sb.append(getAverageTime()).append(",");
        sb.append(timeOutCount).append(",");
        sb.append(errCount);
        return sb.toString();
    }
    
    
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTimeOutCount() {
        return timeOutCount;
    }

    public void setTimeOutCount(int timeOutCount) {
        this.timeOutCount = timeOutCount;
    }

    public int getErrCount() {
        return errCount;
    }

    public void setErrCount(int errCount) {
        this.errCount = errCount;
    }

    public long getTotalExcTime() {
        return totalExcTime;
    }

    public void setTotalExcTime(long totalExcTime) {
        this.totalExcTime = totalExcTime;
    }
    
    
}
