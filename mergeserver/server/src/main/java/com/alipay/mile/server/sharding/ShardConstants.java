/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

/**
 *
 * @author jin.qian
 * @version $Id: ShardConstants.java, v 0.1 2011-5-10 下午03:22:21 jin.qian Exp $
 */
public class ShardConstants {
    /** 单值sharding */
    public static final byte TYPE_SINGLE        = 1;
    /** 值between */
    public static final byte TYPE_BETWEEN       = 2;
    /** 值 Modulo */
    public static final byte TYPE_MODULO        = 3;
    /** 基于当前时间的单值sharding */
    public static final byte TYPE_TIME_SINGLE   = 4;

}
