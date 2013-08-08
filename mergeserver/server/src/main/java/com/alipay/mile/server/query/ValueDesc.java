/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

/**
 * 
 * @author jin.qian
 * @version $Id: ValueDesc.java, v 0.1 2011-5-10 下午05:49:00 jin.qian Exp $
 */
public class ValueDesc {

    /** 数据值 */
    public Object value;

    /** sql解析 字符串值 */
    public String valueDesc;

    /** ?的位置 */
    public int    parmIndex;

    @Override
    public String toString() {
        if (value == null) {
            return "NULL";
        }
        return value.toString();
    }

}
