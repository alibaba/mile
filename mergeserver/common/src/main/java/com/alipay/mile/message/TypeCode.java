/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jin.qian
 * @version $Id: TypeCode.java, v 0.1 2011-5-9 下午05:36:35 jin.qian Exp $
 */
public class TypeCode {

    // data type code used for Dyn Value encoding
    public static final byte TC_NULL    = 0;
    public static final byte TC_INT_8   = 1;
    public static final byte TC_INT_16  = 2;
    public static final byte TC_INT_32  = 4;
    public static final byte TC_INT_64  = 10;
    public static final byte TC_BYTE    = TC_INT_8;
    public static final byte TC_FLOAT   = 6;
    public static final byte TC_DOUBLE  = 7;

    public static final byte TC_SET     = 19;
    public static final byte TC_ARRAY   = 50;
    public static final byte TC_BYTES   = 51;
    public static final byte TC_NOTCOMP = 52;
    public static final byte TC_STRING  = (byte) 254;

    /**
     * 返回tc类型对应的数据长度
     * @param tc
     * @return
     */
    public static int tcWidth(byte tc) {
        if (tc == TC_INT_8) {
            return 1;
        } else if (tc == TC_INT_16) {
            return 2;
        } else if (tc == TC_INT_32) {
            return 4;
        } else if (tc == TC_FLOAT) {
            return 4;
        } else if (tc == TC_INT_64) {
            return 8;
        } else if (tc == TC_DOUBLE) {
            return 8;
        }
        return 0;
    }

    /**
     * Get type code by type name.
     * @param name
     * @return
     */
    public static Byte getTCByName(String name) {
        return TCMAP.get(name);
    }

    private static final Map<String, Byte> TCMAP = new TreeMap<String, Byte>();

    static {
        TCMAP.put("int8", TC_INT_8);
        TCMAP.put("int16", TC_INT_16);
        TCMAP.put("int32", TC_INT_32);
        TCMAP.put("int64", TC_INT_64);
        TCMAP.put("byte", TC_INT_8);
        TCMAP.put("float", TC_FLOAT);
        TCMAP.put("double", TC_DOUBLE);
        TCMAP.put("array", TC_ARRAY);
        TCMAP.put("bytes", TC_BYTES);
        TCMAP.put("string", TC_STRING);

        // type strings used in docserver
        TCMAP.put("tiny", TC_INT_8);
        TCMAP.put("short", TC_INT_16);
        TCMAP.put("long", TC_INT_32);
        TCMAP.put("longlong", TC_INT_64);
    }

}
