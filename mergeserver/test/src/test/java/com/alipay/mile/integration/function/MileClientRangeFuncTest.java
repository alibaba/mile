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
 * need level db
 * @author yuzhong.zhao
 * @version $Id: MileClientRangeFuncTest.java, v 0.1 2012-9-3 下午09:15:39 yuzhong.zhao Exp $
 */
public class MileClientRangeFuncTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientRangeFuncTest.class.getName());

    /**超时*/
    private int                 timeOut = 5000;

    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (UnsupportedEncodingException e) {
            fail();
        }

        // 插入数据
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? VALUE=?";
        Object[] params = new Object[5];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.1";

        for (int i = 0; i < 100; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            params[4] = Long.valueOf(i);
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
    public void testMix() {
        String sql = "select sum(VALUE) within GMT_TEST > ? as a, count(VALUE) within GMT_TEST > ? as b, min(VALUE) within GMT_TEST > ? as c, max(VALUE) within GMT_TEST > ? as d from TEST_DAILY indexwhere TEST_IP=? and GMT_TEST>=?";
        Object[] params = new Object[6];
        params[0] = Long.valueOf("0");
        params[1] = Long.valueOf("20");
        params[2] = Long.valueOf("30");
        params[3] = Long.valueOf("40");
        params[4] = "127.0.0.1";
        params[5] = Long.valueOf("0");
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            fail();
        }

        assertEquals(Long.valueOf(4950), queryResult.getQueryResult().get(0).get("a"));
        assertEquals(Long.valueOf(99), queryResult.getQueryResult().get(0).get("d"));
        assertEquals(Long.valueOf(31), queryResult.getQueryResult().get(0).get("c"));
        assertEquals(Long.valueOf(79), queryResult.getQueryResult().get(0).get("b"));
    }
}
