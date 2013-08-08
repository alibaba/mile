/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.mileexception;

/**
 * 
 * @author jin.qian
 * @version $Id: SQLException.java, v 0.1 2011-5-10 ионГ10:37:06 jin.qian Exp $
 */
public class SQLException extends RuntimeException {

    private static final long serialVersionUID = -1235632825971443865L;

    public SQLException() {
    }

    public SQLException(String message) {
        super(message);
    }

    public SQLException(Throwable cause) {
        super(cause);
    }

    public SQLException(String message, Throwable cause) {
        super(message, cause);
    }

}
