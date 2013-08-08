/**
 * 
 */
package com.alipay.mile.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.SpecRunner;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * @author xiaoju.luo
 * @version $Id: SR6212230.java,v 0.1 2012-11-22 下午07:14:20 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("多数据类型,且数子相同的count(*)&count()&count(distinct)聚合查询")
public class SR621230 extends DocdbTestTools {

    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult;
        stepInfo("插入数据");
        // long类型
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";

        for (int i = 0; i < 4; i++) {
            // params[3] = Long.valueOf(4);
            params[3] = 4l;
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
        for (int i = 4; i < 6; i++) {
            params[3] = Integer.valueOf(4);
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

        for (int i = 6; i < 8; i++) {
            // params[3] = Float.valueOf(4);
            params[3] = 4f;
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

        for (int i = 8; i < 10; i++) {
            // params[3] = Double.valueOf(4);
            params[3] = 4d;
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
    @Subject("聚合函数，count(distinct)，统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622110() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params = new Object[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，count(*)，统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622111() {
        stepInfo("执行查询");
        String sql = "select count(*) as A from  TEST_DAILY indexwhere TEST_IP=?";
        Object[] params = new Object[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(12), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，count(列)，统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622112() {
        stepInfo("执行查询");
        String sql = "select count(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params = new Object[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(12), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @After
    public void tearDown() {
        try {
            stepInfo("理清数据");
            String sql = "delete from TEST_DAILY indexwhere TEST_IP=?";
            String[] params = new String[1];
            params[0] = "127.0.0.2";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {

            Assert.isFalse(true, "abdc");
        }
    }
}
