/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.orderby;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 *
 * @author yuzhong.zhao
 * @version $Id: MileClientDistinctOrderTest.java, v 0.1 2011-7-27 上午11:12:41 yuzhong.zhao Exp $
 */
public class MileClientDistinctOrderTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientDistinctOrderTest.class
                                            .getName());
    //超时
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

        for (int i = 0; i < 100; i++) {
            MileInsertResult insertResult;
            params[0] = String.valueOf(i % 10);
            params[1] = String.valueOf(i);
            params[2] = "127.0.0.1";
            params[3] = Long.valueOf(i % 10);
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

    public void testDistinctOrderbySimple() {
        try {
            String sql = "select distinct GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            assertEquals(queryResult.getQueryResult().size(), 10);
            for (int i = 0; i < 10; i++) {
                Map<String, Object> record = queryResult.getQueryResult().get(i);
                assertEquals(Long.valueOf(i), record.get("GMT_TEST"));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDistinctOrderby() {
        try {
            String sql = "select distinct TEST_ID, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            assertEquals(queryResult.getQueryResult().size(), 10);
            for (int i = 0; i < 10; i++) {
                Map<String, Object> record = queryResult.getQueryResult().get(i);
                assertEquals(Long.valueOf(i), record.get("GMT_TEST"));
                assertEquals(String.valueOf(i), record.get("TEST_ID"));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testDistinctOrderbyWithoutIndex() {
        try {
            String sql = "select distinct TEST_IP, TEST_ID, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            assertEquals(queryResult.getQueryResult().size(), 10);
            for (int i = 0; i < 10; i++) {
                Map<String, Object> record = queryResult.getQueryResult().get(i);
                assertEquals(Long.valueOf(i), record.get("GMT_TEST"));
                assertEquals(String.valueOf(i), record.get("TEST_ID"));
                assertEquals("127.0.0.1", record.get("TEST_IP"));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

}
