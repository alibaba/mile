/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.util.List;
import java.util.Set;

import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: ShardingRule.java, v 0.1 2012-9-20 обнГ09:40:54 yuzhong.zhao Exp $
 */
public interface ShardingRule {
    
    public Set<Integer> equalSharding(Object value) throws SqlExecuteException;

    public Set<Integer> expSharding(byte comparator, List<Object> values) throws SqlExecuteException;
}
