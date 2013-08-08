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
 * @version $Id: SR622020.java,v 0.1 2012-11-8 下午07:07:29 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("一般条件下的聚合快速查询")
public class SR622020 extends LevdbTestTools {

    /**超时*/
    private int timeOut = 5000;

    @Before
    public void setUp() {

        stepInfo("插入数据");
       // String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CREATE=?";
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.3";
        params[4] = "rowkey";
        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入失败");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("聚合快速函数，部分聚合函数带别名")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622020() {
        stepInfo("执行查询");
        String sql = "select count(distinct TEST_ID) within (GMT_TEST > ?)  as a1, count(*) within(GMT_TEST > ?) as a2, sum(GMT_TEST) within(GMT_TEST>?) as a3 from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[4];
        params[0] = Long.valueOf(1);
        params[1] = Long.valueOf(0);
        params[2] = Long.valueOf(0);
        params[3] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(1), queryResult.getQueryResult().get(0).get("a1"), "预期结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(0).get("a2"), "预期结果");
        Assert.areEqual(Double.valueOf(45), queryResult.getQueryResult().get(0).get("a3"), "预期结果");

    }

    //新增用例

    @Test
    @Subject("聚合快速函数，所有支持的聚合函数且带别名")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622021() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) within(GMT_TEST > ?) as A,squaresum(GMT_TEST) within(GMT_TEST>?) as B,stddev(GMT_TEST) within(GMT_TEST>=?) as C,avg(GMT_TEST) within(GMT_TEST>?) as D,sum(GMT_TEST) within(GMT_TEST>?)as E,min(GMT_TEST) within(GMT_TEST>?) as F, max(GMT_TEST) within(GMT_TEST>?) as G,count(GMT_TEST) within(GMT_TEST>?) as H, count(*) within(GMT_TEST>?) as I,count(distinct GMT_TEST) within(GMT_TEST>?) as J from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[11];
        params[0] = Long.valueOf(1);
        params[1] = Long.valueOf(1);
        params[2] = Long.valueOf(0);
        params[3] = Long.valueOf(1);
        params[4] = Long.valueOf(1);
        params[5] = Long.valueOf(1);
        params[6] = Long.valueOf(1);
        params[7] = Long.valueOf(1);
        params[8] = Long.valueOf(1);
        params[9] = Long.valueOf(1);
        params[10] = "rowkey";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("执行异常" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(5.25), queryResult.getQueryResult().get(0).get("A"), "预期结果");
        Assert.areEqual(Double.valueOf(284), queryResult.getQueryResult().get(0).get("B"), "预期结果");
        Assert.areEqual(Double.valueOf(2.8722813232690143), queryResult.getQueryResult().get(0)
            .get("C"), "预期结果");
        Assert.areEqual(Double.valueOf(5.5), queryResult.getQueryResult().get(0).get("D"), "预期结果");
        Assert.areEqual(Double.valueOf(44), queryResult.getQueryResult().get(0).get("E"), "预期结果");
        Assert.areEqual(Long.valueOf(2), queryResult.getQueryResult().get(0).get("F"), "预期结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(0).get("G"), "预期结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0).get("H"), "预期结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0).get("I"), "预期结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0).get("J"), "预期结果");
    }

    //需要确定是否支持不带别名
    @Test
    @Subject("聚合快速函数，所有支持的聚合函数且不带别名")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622022() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) within(GMT_TEST > ?) ,squaresum(GMT_TEST) within(GMT_TEST>?) ,stddev(GMT_TEST) within(GMT_TEST>=?) ,avg(GMT_TEST) within(GMT_TEST>?) ,sum(GMT_TEST) within(GMT_TEST>?),min(GMT_TEST) within(GMT_TEST>?), max(GMT_TEST) within(GMT_TEST>?),count(GMT_TEST) within(GMT_TEST>?), count(*) within(GMT_TEST>?) ,count(distinct GMT_TEST) within(GMT_TEST>?) from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[11];
        params[0] = Long.valueOf(1);
        params[1] = Long.valueOf(1);
        params[2] = Long.valueOf(0);
        params[3] = Long.valueOf(1);
        params[4] = Long.valueOf(1);
        params[5] = Long.valueOf(1);
        params[6] = Long.valueOf(1);
        params[7] = Long.valueOf(1);
        params[8] = Long.valueOf(1);
        params[9] = Long.valueOf(1);
        params[10] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("执行异常" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.valueOf(5.25), queryResult.getQueryResult().get(0).get(
            "VARIANCE (GMT_TEST)"), "预期结果");
        Assert.areEqual(Double.valueOf(284), queryResult.getQueryResult().get(0).get(
            "SQUARESUM (GMT_TEST)"), "预期结果");
        Assert.areEqual(Double.valueOf(2.8722813232690143), queryResult.getQueryResult().get(0)
            .get("STDDEV (GMT_TEST)"), "预期结果");
        Assert.areEqual(Double.valueOf(5.5), queryResult.getQueryResult().get(0).get(
            "AVG (GMT_TEST)"), "预期结果");
        Assert.areEqual(Double.valueOf(44), queryResult.getQueryResult().get(0).get(
            "SUM (GMT_TEST)"), "预期结果");
        Assert.areEqual(Long.valueOf(2), queryResult.getQueryResult().get(0).get("MIN (GMT_TEST)"),
            "预期结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(0).get("MAX (GMT_TEST)"),
            "预期结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0)
            .get("COUNT (GMT_TEST)"), "预期结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0).get("COUNT (*)"),
            "预期结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0).get(
            "COUNT (DISTINCT GMT_TEST)"), "预期结果");
    }

    @After
    public void tearDown() {
        try {
            stepInfo("清理数据");
            String sql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
            String[] params = new String[1];
            params[0] = "rowkey";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "数据清理失败");
        }
    }
}
