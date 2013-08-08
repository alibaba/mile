/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.integration.fulltext;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MileClientFulltextSelectTest.java, v 0.1 2012-10-19 下午04:09:49 yuzhong.zhao Exp $
 */
public class MileClientFulltextSelectTest extends MileClientAbstract {

    /** 超时 */
    private int timeOut = 5000;

    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (UnsupportedEncodingException e) {
            fail();
        }

        // 插入数据
        MileInsertResult insertResult;
        String sql = "insert into table A=? C=? with wordseg(A)=(?,?,?) wordseg(C)=(?,?,?)";
        Object[] params = new Object[10];

        try {
            params[0] = "hang zhou shi";
            params[1] = "lian yi qun";
            params[2] = "hang";
            params[3] = "zhou";
            params[4] = "shi";
            params[5] = "lian";
            params[6] = "yi";
            params[7] = "qun";
            insertResult = applationClientImpl.preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                fail();
            }

            params[0] = "shang hai shi";
            params[1] = "niu zai ku";
            params[2] = "shang";
            params[3] = "hai";
            params[4] = "shi";
            params[5] = "niu";
            params[6] = "zai";
            params[7] = "ku";
            insertResult = applationClientImpl.preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                fail();
            }

            params[0] = "bei jing shi";
            params[1] = "xi zhang ku";
            params[2] = "bei";
            params[3] = "jing";
            params[4] = "shi";
            params[5] = "xi";
            params[6] = "zhuang";
            params[7] = "ku";
            insertResult = applationClientImpl.preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                fail();
            }
        } catch (Exception e) {
            fail();
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
            String sql = "delete from table indexwhere A match (?)";
            String[] params = new String[1];
            params[0] = "shi";
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testFulltextSearch() {
        String sql = "select C, A from table indexwhere A match (?, ?)";
        String[] params = new String[3];

        try {
            params[0] = "hai";
            params[1] = "shi";
            MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();

            
            for (int i = 0; i < 1; i++) {
                assertEquals("shang hai shi", resultList.get(i).get("A"));
            }
            
        } catch (Exception e) {
            fail();
        }
    }
}
