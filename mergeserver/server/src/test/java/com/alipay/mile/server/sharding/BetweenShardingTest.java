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
 * @version $Id: BetweenShardingTest.java, v 0.1 2012-9-23 ÏÂÎç07:34:16 yuzhong.zhao Exp $
 */
public class BetweenShardingTest extends TestCase {

    private BetweenShardingRule shardingRule = new BetweenShardingRule();
    
    
    @Test
    public void testEqual(){
        Set<Integer> result;
        
        
        shardingRule.init(TypeCode.TC_INT_32, "(0,1)", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(null == result || result.size() == 0);
        
        
        shardingRule.init(TypeCode.TC_INT_32, "[0,1)", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(null == result || result.size() == 0);
        
       
        
        shardingRule.init(TypeCode.TC_INT_32, "(0,1]", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(result.size() == 3);
        
        
        
        shardingRule.init(TypeCode.TC_INT_32, "[0,1]", "1,2,3");
        result = shardingRule.equalSharding(0);
        assertTrue(result.size() == 3);
        result = shardingRule.equalSharding(-1);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.equalSharding(1);
        assertTrue(result.size() == 3);
    }

    
    
    @Test
    public void testExp(){
        Set<Integer> result;
        List<Object> values;
        
        
        shardingRule.init(TypeCode.TC_INT_32, "(0,1)", "1,2,3");
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
        

        shardingRule.init(TypeCode.TC_INT_32, "[0,1)", "1,2,3");
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
        
        
        shardingRule.init(TypeCode.TC_INT_32, "(0,1]", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(1));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GT, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_GET, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LT, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_LET, values);
        assertTrue(result.size() == 3);
        
        
        shardingRule.init(TypeCode.TC_INT_32, "[0,1]", "1,2,3");
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
        
        
        
        shardingRule.init(TypeCode.TC_INT_32, "[0,1]", "1,2,3");
        values = new ArrayList<Object>();
        values.add(new Integer(1));
        values.add(new Integer(2));
        result = shardingRule.expSharding(Constants.EXP_COMPARE_BETWEEN_LEG, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_BETWEEN_LEGE, values);
        assertTrue(result.size() == 3);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_BETWEEN_LG, values);
        assertTrue(null == result || result.size() == 0);
        result = shardingRule.expSharding(Constants.EXP_COMPARE_BETWEEN_LGE, values);
        assertTrue(null == result || result.size() == 0);
        

    }

    
}
