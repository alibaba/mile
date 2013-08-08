/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.select;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;
import com.alipay.mile.integration.orderby.MileClientLimitOrderTest;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MileClientBetweenSelectTest.java, v 0.1 2011-8-3 上午09:08:45
 *          yuzhong.zhao Exp $
 */
public class MileClientBetweenSelectTest extends MileClientAbstract {
    private static final Logger LOGGER     = Logger.getLogger(MileClientLimitOrderTest.class
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
        Object[] params = new Object[5];
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
    public void testBetweenSelect() {
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST between [?, ?] ";
        Object[] params = new Object[3];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(0);
        params[2] = Long.valueOf(3);

        try {
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();
            assertEquals(4, resultList.size());
            for (int i = 0; i < 4; i++) {
                assertEquals("12345", resultList.get(i).get("TEST_ID"));
                assertEquals("milemac", resultList.get(i).get("TEST_NAME"));
                assertEquals("127.0.0.1", resultList.get(i).get("TEST_IP"));
                assertTrue((Long) resultList.get(i).get("GMT_TEST") >= 0);
                assertTrue((Long) resultList.get(i).get("GMT_TEST") <= 3);
            }

        } catch (Exception e) {
            fail();
        }
    }

}
