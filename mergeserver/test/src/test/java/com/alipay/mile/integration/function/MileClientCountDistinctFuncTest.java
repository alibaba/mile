/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.integration.function;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MileClientCountDistinctFuncTest.java, v 0.1 2012-7-10 下午08:30:56 yuzhong.zhao Exp $
 */
public class MileClientCountDistinctFuncTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientCountDistinctFuncTest.class
                                            .getName());

    /** 超时 */
    private int                 timeOut = 5000;

    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (UnsupportedEncodingException e) {
            fail();
        }

        // 插入数据
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.1";

        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            try {
                insertResult = applationClientImpl.preInsert(sql, params, timeOut);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("docid: " + insertResult.getDocId());
                }
            } catch (Exception e) {
                fail();
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Override
    public void tearDown() {
        try {
            // 删除数据
            String sql = "delete from TEST_DAILY indexwhere TEST_IP=?";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCountDistinct() {
        String sql = "select count(distinct GMT_TEST) as a, count(distinct TEST_NAME) as b from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(queryResult);
            }
        } catch (Exception e) {
            fail();
        }

        assertEquals(Long.valueOf(10), queryResult.getQueryResult().get(0).get("a"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("b"));
    }

    @Test
    public void testMixCountDistinct() {
        String sql = "select count(distinct GMT_TEST) as a, max(GMT_TEST) as c, min(GMT_TEST) as d, sum(GMT_TEST) as e, count(*) as f, count(distinct TEST_NAME) as b from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(queryResult);
            }
        } catch (Exception e) {
            fail();
        }

        assertEquals(Long.valueOf(10), queryResult.getQueryResult().get(0).get("a"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("b"));
        assertEquals(Long.valueOf(9), queryResult.getQueryResult().get(0).get("c"));
        assertEquals(Long.valueOf(0), queryResult.getQueryResult().get(0).get("d"));
        assertEquals(Double.valueOf(45), queryResult.getQueryResult().get(0).get("e"));
        assertEquals(Long.valueOf(10), queryResult.getQueryResult().get(0).get("f"));
    }

}
