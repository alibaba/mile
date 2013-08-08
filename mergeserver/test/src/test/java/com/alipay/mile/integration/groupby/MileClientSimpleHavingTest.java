/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.groupby;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 *
 * @author yuzhong.zhao
 * @version $Id: MileClientSimpleHavingTest.java, v 0.1 2011-7-27 上午11:06:55 yuzhong.zhao Exp $
 */
public class MileClientSimpleHavingTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientSimpleHavingTest.class
                                            .getName());
    //超时
    private int                 timeOut = 5000;

    @Before
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
            params[0] = String.valueOf(i % 10);
            params[1] = String.valueOf(i);
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

    public void testSimpleHavingGroupby() {
        try {
            String sql = "select max(GMT_TEST) as v1, min(GMT_TEST) as v2, sum(GMT_TEST) as v3, count(GMT_TEST) as v4, TEST_ID from TEST_DAILY indexwhere TEST_IP=? group by TEST_ID having max(GMT_TEST) >= ?";
            Object[] params = new Object[2];
            params[0] = "127.0.0.1";
            params[1] = Long.valueOf(95);
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            assertEquals(queryResult.getQueryResult().size(), 5);
            for (Map<String, Object> record : queryResult.getQueryResult()) {
                assertEquals(record.size(), 5);
                int i = Integer.parseInt((String) record.get("TEST_ID"));
                assertTrue(i >= 5 && i < 10);
                assertEquals(Long.valueOf(90 + i), record.get("v1"));
                assertEquals(Long.valueOf(i), record.get("v2"));
                assertEquals(Double.valueOf(450 + i * 10), record.get("v3"));
                assertEquals(Long.valueOf(10), record.get("v4"));
            }
        } catch (Exception e) {
            LOGGER.error(e);
            assertTrue(false);
        }
    }

}
