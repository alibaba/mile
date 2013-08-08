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

/**
 * 
 * @author bin.lb
 */
public class MileClientSubSelectTest extends MileClientAbstract {
    private static final Logger LOGGER     = Logger.getLogger(MileClientSubSelectTest.class
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
        String sql = "insert into TEST_DAILY TEST_IP=? TEST_NAME=?";
        Object[][] params = { { "user1", "mac1" }, { "user2", "mac1" } };

        for (int i = 0; i < params.length; i++) {
            try {
                MileInsertResult insertResult = applationClientImpl.preInsert(sql, params[i],
                    timeOut);
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
            String sql = "delete from TEST_DAILY indexwhere TEST_IP in ( ?, ? )";
            String[] params = { "user1", "user2" };
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPrepareSelect() {
        String sql = "select TEST_IP, TEST_NAME from TEST_DAILY indexwhere TEST_NAME in (select TEST_NAME from TEST_DAILY indexwhere TEST_IP = ?)";
        String[] params = { "user1" };

        try {

            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();
            assertEquals(2, resultList.size());

        } catch (Exception e) {
            fail();
        }
    }

}
