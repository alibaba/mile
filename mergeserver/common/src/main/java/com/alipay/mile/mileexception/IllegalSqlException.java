/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.mileexception;

import java.sql.SQLException;

/**
 * 非法sql语句异常
 * @author yuzhong.zhao
 * @version $Id: IllegalSqlException.java, v 0.1 2011-5-5 下午05:33:54 yuzhong.zhao Exp $
 */
public class IllegalSqlException extends SQLException {

    private static final long serialVersionUID = 1331939111562733269L;

    /**
     * 
     */
    public IllegalSqlException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public IllegalSqlException(String arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public IllegalSqlException(Throwable arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public IllegalSqlException(String arg0, Throwable arg1) {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
