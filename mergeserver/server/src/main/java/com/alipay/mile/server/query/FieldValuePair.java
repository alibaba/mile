/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import com.alipay.mile.FieldDesc;

public class FieldValuePair {
    /** 列描述 */
    public FieldDesc field;
    /**  值*/
    public ValueDesc value;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("列描述 [" + field.toString() + "], " + "列值 [" + value.toString() + "]\n");
        return sb.toString();
    }
}
