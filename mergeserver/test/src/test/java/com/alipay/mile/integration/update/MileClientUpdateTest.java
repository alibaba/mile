/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.integration.update;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.client.result.MileUpdateResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MileClientUpdateTest.java, v 0.1 2012-7-12 下午03:50:32 yuzhong.zhao Exp $
 */
public class MileClientUpdateTest extends MileClientAbstract {
    private static final Logger LOGGER     = Logger.getLogger(MileClientUpdateTest.class.getName());

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
            params[0] = String.valueOf(i % 2);
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
            String sql = "delete from TEST_DAILY where TEST_ID=? or TEST_ID=?";
            String[] params = new String[2];
            params[0] = "0";
            params[1] = "1";
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testPrepareUpdate() {
        String sql = "update TEST_DAILY set GMT_TEST = ? where TEST_ID = ? or TEST_ID = ?";
        Object[] params = new Object[3];
        params[0] = Long.valueOf(100);
        params[1] = "0";
        params[2] = "1";

        try {
            MileUpdateResult updateResult = applationClientImpl.preUpdate(sql, params, timeOut);
            assertEquals(10, updateResult.getUpdateNum());

        } catch (Exception e) {
            fail();
        }

        sql = "select TEST_IP, TEST_ID, GMT_TEST from TEST_DAILY where TEST_ID = ? or TEST_ID = ?";
        params = new Object[3];
        params[0] = "0";
        params[1] = "1";

        try {
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();
            assertEquals(10, resultList.size());
            for (int i = 0; i < 10; i++) {
                assertEquals(Long.valueOf(100), resultList.get(i).get("GMT_TEST"));
            }

        } catch (Exception e) {
            fail();
        }
    }
}
