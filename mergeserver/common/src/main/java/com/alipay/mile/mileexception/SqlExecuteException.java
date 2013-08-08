/**
 * created since 2011-5-16
 */
package com.alipay.mile.mileexception;

import java.sql.SQLException;

/**
 * @author yuzhong.zhao
 * @version $Id: SqlExecuteException.java,v 0.1 2011-5-16 下午03:16:45 yuzhong.zhao Exp $
 */
public class SqlExecuteException extends SQLException {

    private static final long serialVersionUID = -7867037479359179927L;

    /**
     * 
     */
    public SqlExecuteException() {
    }

    /**
     * @param arg0
     */
    public SqlExecuteException(String arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     */
    public SqlExecuteException(Throwable arg0) {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SqlExecuteException(String arg0, String arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public SqlExecuteException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public SqlExecuteException(String arg0, String arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     */
    public SqlExecuteException(String arg0, String arg1, Throwable arg2) {
        super(arg0, arg1, arg2);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     */
    public SqlExecuteException(String arg0, String arg1, int arg2, Throwable arg3) {
        super(arg0, arg1, arg2, arg3);
    }

}
