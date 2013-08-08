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

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: SingleShardingTest.java, v 0.1 2012-9-23 ÏÂÎç05:44:50 yuzhong.zhao Exp $
 */
public class SingleShardingTest extends TestCase {
    
    private SingleShardingRule shardingRule = new SingleShardingRule();
    
    
    @Test
    public void testEqual(){
        Set<Integer> result;
        
        
        shardingRule.init(TypeCode.TC_INT_32, "0", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(null == result || result.size() == 0);
        
        
        
        shardingRule.init(TypeCode.TC_INT_32, ">0", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(result.size() == 3);
        
       
        
        shardingRule.init(TypeCode.TC_INT_32, ">=0", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(result.size() == 3);
        
        
        
        shardingRule.init(TypeCode.TC_INT_32, "<0", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(-1);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(1);
        assertTrue(null == result || result.size() == 0);
        
        
        
        shardingRule.init(TypeCode.TC_INT_32, "<=0", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(-1);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(1);
        assertTrue(null == result || result.size() == 0);
        
    }

    
    
    @Test
    public void testExp(){
        Set<Integer> result;
        List<Object> values;
        
        
        shardingRule.init(TypeCode.TC_INT_32, "0", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(0));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
        assertTrue(result.size() == 3);
        

        shardingRule.init(TypeCode.TC_INT_32, ">0", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(0));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
        assertTrue(null == result || result.size() == 0);
        
        
        shardingRule.init(TypeCode.TC_INT_32, ">=0", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(0));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
        assertTrue(result.size() == 3);
        
        
        shardingRule.init(TypeCode.TC_INT_32, "<0", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(0));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
        assertTrue(result.size() == 3);
        
        
        shardingRule.init(TypeCode.TC_INT_32, "<=0", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(0));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
        assertTrue(result.size() == 3);
        
    }

}
