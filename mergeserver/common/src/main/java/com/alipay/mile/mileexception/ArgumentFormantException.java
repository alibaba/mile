/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.mileexception;

/**
 * 处理参数异常
 * @author huabing.du
 * @version $Id: ArgumentFormantException.java, v 0.1 2011-5-10 上午11:37:58 huabing.du Exp $
 */
public class ArgumentFormantException extends Exception {

    private static final long serialVersionUID = 1L;

    public ArgumentFormantException() {
        super();
    }

    public ArgumentFormantException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentFormantException(String message) {
        super(message);
    }

    public ArgumentFormantException(Throwable cause) {
        super(cause);
    }

}
