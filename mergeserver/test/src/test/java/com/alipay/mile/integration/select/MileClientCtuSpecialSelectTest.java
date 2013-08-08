/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.integration.select;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MileClientCtuSpecialSelectTest.java, v 0.1 2012-3-8 上午10:49:49 yuzhong.zhao Exp $
 */
public class MileClientCtuSpecialSelectTest extends MileClientAbstract {
    private static final Logger LOGGER     = Logger.getLogger(MileClientCtuSpecialSelectTest.class
                                               .getName());

    /** 超时 */
    private int                 timeOut    = 5000;

    List<Long>                  docidsList = new ArrayList<Long>();

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
            params[0] = String.valueOf(i % 10);
            params[1] = "127.0.0.1";
            params[2] = "127.253.34.1";
            params[3] = Long.valueOf(i);
            try {
                MileInsertResult insertResult = applationClientImpl.preInsert(sql, params, timeOut);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("docid: " + insertResult.getDocId());
                }
            } catch (Exception e) {
                fail();
            }
        }

        try {
            Thread.sleep(100);
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
            params[0] = "127.253.34.1";
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPreCtuClusterCountQuery() {
        Object[] params = new Object[3];
        params[0] = "127.253.34.1";
        params[1] = Long.valueOf(10);
        params[2] = Long.valueOf(20);
        int count = 0;
        try {
            count = applationClientImpl.preCtuClusterCountQuery("TEST_DAILY",
                "indexwhere TEST_IP=? where GMT_TEST > ? and GMT_TEST < ?", "TEST_ID",
                "MAX(GMT_TEST)", params, 3000);
            assertTrue(count == 9);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPreCtuClusterQuery() {
        Object[] params = new Object[3];
        params[0] = "127.253.34.1";
        params[1] = Long.valueOf(10);
        params[2] = Long.valueOf(20);
        MileQueryResult result;
        List<String> selectFields = new ArrayList<String>();
        selectFields.add("TEST_ID");
        selectFields.add("GMT_TEST");
        try {
            result = applationClientImpl.preCtuClusterQuery("TEST_DAILY", selectFields,
                "indexwhere TEST_IP=?", "TEST_ID", "MAX(GMT_TEST)", "GMT_TEST", false, 10, 0,
                params, 3000);
            assertTrue(result.getQueryResult().size() == 10);
        } catch (Exception e) {
            fail();
        }
    }
}
