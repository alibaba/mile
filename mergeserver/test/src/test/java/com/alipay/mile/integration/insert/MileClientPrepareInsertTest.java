/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.insert;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.integration.MileClientAbstract;
import com.alipay.mile.integration.function.MileClientCountFuncTest;

/**
 *
 * @author yuzhong.zhao
 * @version $Id: MileClientPrepareInsertTest.java, v 0.1 2011-7-28 下午07:11:24 yuzhong.zhao Exp $
 */
public class MileClientPrepareInsertTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientCountFuncTest.class.getName());

    /**超时*/
    private int                 timeOut = 50000000;

    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (UnsupportedEncodingException e) {
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
    public void testPrepareInsert() {
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "12345";
        params[1] = "ctumile";
        params[2] = "127.0.0.1";
        params[3] = Long.valueOf(6);
        MileInsertResult insertResult;
        try {
            insertResult = applationClientImpl.preInsert(sql, params, timeOut);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("docid: " + insertResult.getDocId());
            }
            assertTrue(insertResult.getDocId() > 0);
        } catch (Exception e) {
            LOGGER.error(e);
            fail();
        }

    }
}
