/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.mileexception;

public class MileException extends Exception {

    /**  */
    private static final long serialVersionUID = -1178742912829999367L;

    /**  */

    public MileException() {
    }

    public MileException(String message) {
        super(message);
    }

    public MileException(Throwable cause) {
        super(cause);
    }

    public MileException(String message, Throwable cause) {
        super(message, cause);
    }

}
