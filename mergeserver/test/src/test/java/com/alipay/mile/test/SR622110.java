/**
 * 
 */
package com.alipay.mile.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/** 多数据类型，且数值一样的count(distinct)
 * @author xiaoju.luo
 * @version $Id: SR622110.java,v 0.1 2012-11-9 下午06:19:27 xiaoju.luo Exp $
 */

public class SR622110 extends LevdbTestTools {

    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult;
        stepInfo("插入数据");
        // long类型
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = "rowkey";
        for (int i = 0; i < 4; i++) {
            params[3] = Long.valueOf(4);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }
        // Integer类型
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = "rowkey";
        for (int i = 4; i < 6; i++) {
            params[3] = Integer.valueOf(4);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        // Float类型		
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = "rowkey";
        for (int i = 6; i < 8; i++) {
            params[3] = Float.valueOf(4);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        // Double类型		
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = "rowkey";
        for (int i = 8; i < 10; i++) {
            params[3] = Double.valueOf(4);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        // Byte类型		
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[3] = Byte.valueOf((byte) 4);
        params[4] = "rowkey";
        params[5] = Long.valueOf(10);
        try {
            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入数据异常");
        }

        // Byte类型		
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[3] = Short.valueOf((short) 4);
        params[4] = "rowkey";
        params[5] = Long.valueOf(11);
        try {
            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入数据异常");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "等待异常");
        }
    }

    @Test
    @Subject("聚合函数，count(distinct)")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622110() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，count(*)")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622111() {
        stepInfo("执行查询");
        String sql = "select count(*) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(12), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，count(列)")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622112() {
        stepInfo("执行查询");
        String sql = "select count(GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(12), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @After
    public void tearDown() {
        try {
            stepInfo("理清数据");
            String sql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
            String[] params = new String[1];
            params[0] = "rowkey";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "abdc");
        }
    }
}
