/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.server.sharding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.alipay.mile.Constants;
import com.alipay.mile.message.TypeCode;
import com.alipay.mile.mileexception.ArgumentFormantException;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: ModShardingTest.java, v 0.1 2012-9-23 ÏÂÎç07:59:04 yuzhong.zhao Exp $
 */
public class ModShardingTest extends TestCase {
    private ModShardingRule shardingRule = new ModShardingRule();

    @Test
    public void testEqual() {
        Set<Integer> result;

        try {
            shardingRule.init(TypeCode.TC_INT_32, null, "10",
                "1:2,3:4,5:6,7:8,9:10,11:12,13:14,15:16,17:18,19:20");
        } catch (ArgumentFormantException e) {
            assertTrue(false);
        }
        try {
            result = shardingRule.equalSharding(0);
            assertTrue(result.contains(1) && result.size() == 2);
            result = shardingRule.equalSharding(1);
            assertTrue(result.contains(3) && result.size() == 2);
            result = shardingRule.equalSharding(10);
            assertTrue(result.contains(1) && result.size() == 2);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testExp() {
        Set<Integer> result;
        List<Object> values;

        try {
            shardingRule.init(TypeCode.TC_INT_32, null, "10",
                "1:2,3:4,5:6,7:8,9:10,11:12,13:14,15:16,17:18,19:20");
        } catch (ArgumentFormantException e) {
            assertTrue(false);
        }

        values = new ArrayList<Object>();
        values.add(new Integer(0));
        try {
            result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
            assertTrue(result.size() == 20);
            result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
            assertTrue(result.size() == 20);
            result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
            assertTrue(result.size() == 20);
            result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
            assertTrue(result.size() == 20);
        } catch (Exception e) {
            fail();
        }
        
    }
}
