/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
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
 * @version $Id: MileClientCountFuncTest.java, v 0.1 2011-7-27 上午09:36:09
 *          yuzhong.zhao Exp $
 */
public class MileClientCutCountFuncTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientCutCountFuncTest.class
                                            .getName());

    /** 超时 */
    private int                 timeOut = 5000;
    private int                 num     = 30000;
    private long                cut     = 20000;

    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (UnsupportedEncodingException e) {
            fail();
        }

        // 插入数据
        String sql = "insert into TEST_DAILY TEST_ID=?  GMT_TEST=?";
        Object[] params = new Object[2];

        params[1] = 100L;

        for (int i = 0; i < num; i++) {
            MileInsertResult insertResult;
            params[0] = i;
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
            String sql = "delete from TEST_DAILY indexwhere GMT_TEST=?";
            Object[] params = new Object[1];
            params[0] = 100L;
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCount() {
        String sql = "select count(*) from TEST_DAILY indexwhere GMT_TEST=?";
        Object[] params = new Object[1];
        params[0] = 100L;
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(queryResult);
            }
        } catch (Exception e) {
            fail();
        }

        assertEquals(cut, queryResult.getQueryResult().get(0).get("COUNT (*)"));

    }
}
