/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 线程安全的类，用于统计一段时间内的服务器请求处理情况
 * 
 * @author yuzhong.zhao
 * @version $Id: RequestCounter.java, v 0.1 2012-5-23 下午02:47:06 yuzhong.zhao Exp $
 */
public class RequestCounter {
    private final ConcurrentHashMap<String, RequestDigest> requests;

    public RequestCounter() {
        requests = new ConcurrentHashMap<String, RequestDigest>();
    }

    /**
     * 统计一次请求, 对超时和错误的时间不计入统计
     * 
     * @param source        请求来源
     * @param time          请求的执行时间              
     * @param resultCode    0表示成功，1表示超时，-1表示错误
     */
    public void addRequest(String source, long time, int resultCode) {
        RequestDigest request = requests.get(source);
        int count = 0;
        int timeOutCount = 0;
        int errCount = 0;
        long totalExcTime = 0;

        if (null != request) {
            count = request.getCount();
            timeOutCount = request.getTimeOutCount();
            errCount = request.getErrCount();
            totalExcTime = request.getTotalExcTime();
        }

        if (resultCode == 0) {
            count++;
            totalExcTime += time;
        } else if (resultCode == 1) {
            timeOutCount++;
        } else {
            errCount++;
        }

        requests.put(source, new RequestDigest(count, timeOutCount, errCount, totalExcTime));
    }

    public void reset() {
        requests.clear();
    }
    
    
    public Map<String, RequestDigest> getRequests(){
        return this.requests;
    }

   
}
