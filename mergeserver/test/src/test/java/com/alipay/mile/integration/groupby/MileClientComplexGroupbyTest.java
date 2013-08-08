/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.groupby;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 *
 * @author yuzhong.zhao
 * @version $Id: MileClientComplexGroupbyTest.java, v 0.1 2011-7-27 上午11:05:37 yuzhong.zhao Exp $
 */
public class MileClientComplexGroupbyTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientComplexGroupbyTest.class
                                            .getName());
    //超时
    private int                 timeOut = 5000000;

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

        for (int i = 0; i < 100; i++) {
            MileInsertResult insertResult;
            params[0] = String.valueOf(i / 10);
            params[1] = String.valueOf(i / 10);
            params[2] = "127.0.0.1";
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

    public void tesComplexGroupby() {
        try {
            String sql = "select count(distinct GMT_TEST) as a, max(GMT_TEST) as v1, min(GMT_TEST) as v2, sum(GMT_TEST) as v3, count(GMT_TEST) as v4, count(distinct TEST_IP) as b, TEST_NAME from TEST_DAILY indexwhere TEST_IP=? group by TEST_ID, TEST_NAME limit 1000";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            assertEquals(queryResult.getQueryResult().size(), 10);
            for (Map<String, Object> record : queryResult.getQueryResult()) {
                int i = Integer.parseInt((String) record.get("TEST_NAME"));
                assertEquals(Long.valueOf(i * 10 + 9), record.get("v1"));
                assertEquals(Long.valueOf(i * 10), record.get("v2"));
                assertEquals(Long.valueOf(i * 100 + 45), record.get("v3"));
                assertEquals(Long.valueOf(10), record.get("v4"));
                assertEquals(Long.valueOf(10), record.get("a"));
                assertEquals(Long.valueOf(1), record.get("b"));
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

    public void testComplexGroupbyUseIndex() {
        try {
            String sql = "select count(distinct GMT_TEST) as a, max(GMT_TEST) as v1, min(GMT_TEST) as v2, sum(GMT_TEST) as v3, count(GMT_TEST) as v4, count(distinct TEST_IP) as b from TEST_DAILY indexwhere TEST_IP=? group by TEST_ID limit 1000";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            assertEquals(queryResult.getQueryResult().size(), 10);
            for (Map<String, Object> record : queryResult.getQueryResult()) {
                int i = (Integer.parseInt(record.get("v1").toString()) - 9) / 10;
                assertEquals(Long.valueOf(i * 10 + 9), record.get("v1"));
                assertEquals(Long.valueOf(i * 10), record.get("v2"));
                assertEquals(Double.valueOf(i * 100 + 45), record.get("v3"));
                assertEquals(Long.valueOf(10), record.get("v4"));
                assertEquals(Long.valueOf(10), record.get("a"));
                assertEquals(Long.valueOf(1), record.get("b"));
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

}
