/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

/**
 * 段时间过滤
 * @author jin.qian
 * @version $Id: TimeHint.java, v 0.1 2011-5-10 下午05:44:15 jin.qian Exp $
 */
public class TimeHint {

    /**段创建最小时间  */
    public long startCreateTime = 0;
    /** 段创建最大时间 */
    public long endCreateTime   = Long.MAX_VALUE;
    /**段更新最小时间  */
    public long startUpdateTime = 0;
    /**段更新最大时间  */
    public long endUpdateTime   = Long.MAX_VALUE;

}
