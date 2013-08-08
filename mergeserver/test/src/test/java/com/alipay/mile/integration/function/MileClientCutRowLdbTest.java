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
public class MileClientCutRowLdbTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientCutRowLdbTest.class.getName());

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
        String sql = "insert into t id=?  gmt=? n=?";
        Object[] params = new Object[3];
        params[0] = "1";
        params[2] = 2L;

        for (long i = 0; i < num; i++) {
            MileInsertResult insertResult;
            params[1] = (Long) i;
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
            String sql = "delete from t indexwhere id=?";
            Object[] params = new Object[1];
            params[0] = "1";
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testSelect() {
        String sql = "select n from t indexwhere id =?";
        Object[] params = new Object[1];
        params[0] = "1";
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            List<Map<String, Object>> l = queryResult.getQueryResult();
            int i = 0;
            for (Map<String, Object> m : l) {
                if (i++ > cut)
                    break;
                assertEquals(m.get("n"), 2L);
            }
        } catch (Exception e) {
            fail();
        }

    }
}
