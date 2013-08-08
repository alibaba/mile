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
 * function函数测试类
 * 
 * @author xiaoju.luo
 * @version $Id: SR621080.java,v 0.1 2012-11-6 下午01:13:22 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("聚合函数")
public class SR621080 extends DocdbTestTools {
    private int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("插入数据");
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.1";

        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            } catch (Exception e) {
                Assert.isFalse(true, "插入异常失败");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行失败");
        }
    }

    @Test
    @Subject("聚合函数，count(*)&count(distinct)英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621080() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as a, count(distinct TEST_NAME) as b from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行失败");
        }
        stepInfo("对查询结果的预期的判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("a"), "预期查询结果");
        Assert.areEqual(Long.valueOf(1), queryResult.getQueryResult().get(0).get("b"), "预期查询结果");
    }

    @Test
    @Subject("聚合函数，count*查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621081() {
        stepInfo("执行查询");
        String sql = "select count(*) from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
            Logger.info("结果集为：" + queryResult);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("COUNT (*)"),
            "预期查询结果");

    }

    @Test
    @Subject("聚合函数，max查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621082() {
        stepInfo("执行查询");
        String sql = "select max(GMT_TEST) from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        Logger.info("查询结果为" + queryResult);
        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(0).get("MAX (GMT_TEST)"),
            "预期查询结果");
    }

    @Test
    @Subject("聚合函数，min查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621083() {
        stepInfo("执行查询");
        String sql = "select MIN(GMT_TEST) from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("MIN (GMT_TEST)"),
            "预期查询结果");
    }

    @Test
    @Subject("聚合函数，2个sum重复查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621084() {
        stepInfo("执行查询");
        String sql = "select sum(GMT_TEST), sum(GMT_TEST) from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get(
            "SUM (GMT_TEST)"), "预期查询结果");
    }

    @Test
    @Subject("聚合函数，1个sum重复查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621085() {
        String sql = "select sum(GMT_TEST) from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get(
            "SUM (GMT_TEST)"), "预期查询结果");
    }

    @Test
    @Subject("聚合函数，max,min,sum与count混合查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621086() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as a, max(GMT_TEST) as c, min(GMT_TEST) as d, sum(GMT_TEST) as e, count(*) as f, count(distinct TEST_NAME) as b from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
            Logger.info("查询结果为" + queryResult);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("a"), "预期查询结果");
        Assert.areEqual(Long.valueOf(1), queryResult.getQueryResult().get(0).get("b"), "预期查询结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(0).get("c"), "预期查询结果");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("d"), "预期查询结果");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get("e"), "预期查询结果");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("f"), "预期查询结果");
    }

    @Test
    @Subject("聚合函数，sum,count,min,max,avg,variance,stddev等函数混合查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621087() {
        stepInfo("执行查询");
        String sql = "select sum(GMT_TEST), count(GMT_TEST), min(GMT_TEST), max(GMT_TEST), avg(GMT_TEST), variance(GMT_TEST), stddev(GMT_TEST), squaresum(GMT_TEST) from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("对查询结果的预期判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "结果集大小预期");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get(
            "SUM (GMT_TEST)"), "预期查询结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(0).get("MAX (GMT_TEST)"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("MIN (GMT_TEST)"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get(
            "COUNT (GMT_TEST)"), "预期查询结果");

        Assert.areEqual(Double.valueOf(4.5), queryResult.getQueryResult().get(0).get(
            "AVG (GMT_TEST)"), "预期查询结果");
        Assert.areEqual(Double.valueOf(2.8722813232690143), queryResult.getQueryResult().get(0)
            .get("STDDEV (GMT_TEST)"), "预期查询结果");
        Assert.areEqual(Double.valueOf(8.25), queryResult.getQueryResult().get(0).get(
            "VARIANCE (GMT_TEST)"), "预期查询结果");
        Assert.areEqual(Double.valueOf(285), queryResult.getQueryResult().get(0).get(
            "SQUARESUM (GMT_TEST)"), "预期查询结果");
    }

    @After
    public void tearDown() {
        try {
            stepInfo("删除数据");
            String sql = "delete from TEST_DAILY indexwhere TEST_IP=?";
            String[] params = new String[1];
            params[0] = "127.0.0.1";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "清除数据失败");
        }
    }
}
