/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark.util;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: SqlParamEntry.java, v 0.1 2012-11-6 обнГ02:28:05 yuzhong.zhao Exp $
 */
public class SqlParamEntry {
    private String sql;
    
    private Object[] params;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
    
    
}
