/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark.util;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: FileLine.java, v 0.1 2012-11-12 下午04:59:51 yuzhong.zhao Exp $
 */
public class FileLine {
    /** 文件行记录 */
    private String text;
    /** 文件名 */
    private String file;
    /** 文件行号 */
    private long lineNum;
    
    
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public void setLineNum(long lineNum) {
        this.lineNum = lineNum;
    }
    public long getLineNum() {
        return lineNum;
    }

    
    
}
