/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.function;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

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
public class MileClientCutRowTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientCutRowTest.class.getName());

    /** 超时 */
    private int                 timeOut = 5000;
    private int                 num     = 300;
    private long                cut     = 200;

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
        params[0] = 1;
        params[1] = 100L;

        for (int i = 0; i < num; i++) {
            MileInsertResult insertResult;

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
    public void testSelect() {
        String sql = "select TEST_ID from TEST_DAILY indexwhere GMT_TEST=?";
        Object[] params = new Object[1];
        params[0] = 100L;
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            List<Map<String, Object>> l = queryResult.getQueryResult();
            int i = 0;
            for (Map<String, Object> m : l) {
                if (i++ > cut)
                    break;
                assertEquals(m.get("TEST_ID"), 1);
            }

        } catch (Exception e) {
            fail();
        }

    }
}
