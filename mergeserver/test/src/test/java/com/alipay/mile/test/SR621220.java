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
 * @version $Id: SR621220.java,v 0.1 2012-11-16 下午06:13:12 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("多数据类型的数值列的聚合查询")
public class SR621220 extends DocdbTestTools {

    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult;
        stepInfo("插入数据");
        // 一般正常数据
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";

        for (int i = 0; i < 4; i++) {
            params[3] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";

        for (int i = 4; i < 6; i++) {
            params[3] = Long.valueOf(i);

            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";

        for (int i = 6; i < 8; i++) {
            params[3] = Float.valueOf(i);

            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";

        for (int i = 8; i < 10; i++) {
            params[3] = Double.valueOf(i);

            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "等待异常");
        }
    }

    @Test
    @Subject("聚合函数，方差且带别名,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622130() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params = new Object[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(8.25), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，平方和且有别名查询，统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622011() {
        stepInfo("执行查询");
        String sql = "select squaresum(GMT_TEST) as B from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(285), queryResult.getQueryResult().get(0).get("B"), "预期结果");
    }

    @Test
    @Subject("聚合函数,标准差且有别名查询,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622012() {
        stepInfo("执行查询");
        String sql = "select stddev(GMT_TEST) as C from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(2.8722813232690143), queryResult.getQueryResult().get(0)
            .get("C"), "预期结果");

    }

    @Test
    @Subject("聚合函数,所有聚合函数支持且有别名查询,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622014() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) as A,squaresum(GMT_TEST) as B,stddev(GMT_TEST) as C,avg(GMT_TEST) as D,sum(GMT_TEST) as E,min(GMT_TEST) as F, max(GMT_TEST) as G,count(GMT_TEST) as H, count(*) as I,count(distinct GMT_TEST) as J from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(8.25), queryResult.getQueryResult().get(0).get("A"), "预期结果");
        Assert.areEqual(Double.valueOf(285), queryResult.getQueryResult().get(0).get("B"), "预期结果");
        Assert.areEqual(Double.valueOf(2.8722813232690143), queryResult.getQueryResult().get(0)
            .get("C"), "预期结果");
        Assert.areEqual(Double.valueOf(4.5), queryResult.getQueryResult().get(0).get("D"), "预期结果");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get("E"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("G"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("F"), "预期结果");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("H"), "预期结果");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("I"), "预期结果");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("J"), "预期结果");

    }

    @Test
    @Subject("聚合函数,平均数且有别名查询,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622013() {
        stepInfo("执行查询");
        String sql = "select avg(GMT_TEST) as D from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(4.5), queryResult.getQueryResult().get(0).get("D"), "预期结果");

    }

    @Test
    @Subject("聚合函数，count且带别名，统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622015() {
        stepInfo("执行查询");
        String sql = "select count(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，count(distinct)且带别名,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622016() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，max且带别名,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622017() {
        stepInfo("执行查询");
        String sql = "select max(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，min且带别名,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622018() {
        stepInfo("执行查询");
        String sql = "select min(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，sum且带别名,统计列为多数据类型")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622019() {
        stepInfo("执行查询");
        String sql = "select sum(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get("A"), "预期结果");
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
