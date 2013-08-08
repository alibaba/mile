/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.util;

import java.io.File;
import java.io.IOException;

/**
 * 自动创建文件目录的appender。
 * 
 * @author liang.chenl
 * @version $Id: DailyRollingFileAppender.java, v 0.1 2011-5-31 上午11:50:15 liang.chenl Exp $
 */
public class DailyRollingFileAppender extends org.apache.log4j.DailyRollingFileAppender {
    public void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
                                                                                            throws IOException {
        synchronized (this) {
            File logfile = new File(fileName);
            logfile.getParentFile().mkdirs();
            super.setFile(fileName, append, bufferedIO, bufferSize);
        }
    }
}
