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
 * 聚合函数包括跨string数据统计测试类，现在测试无法通过
 * 
 * @author xiaoju.luo
 * @version $Id: SR621001.java,v 0.1 2012-10-31 下午08:44:24 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("字符串&数字混合聚合函数")
public class SR621010 extends DocdbTestTools {
    int timeOut = 2000;

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

        for (int i = 0; i < 10; i++) {
            params[3] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                // Logger.info("docid: " + insertResult.getDocId());

            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        // 统计列的文本输入
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[3] = "文本输入";
        try {
            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "等待异常");
        }
    }

    @Test
    @Subject("聚合函数，方差且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621010() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，平方和且有别名查询，统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621011() {
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
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("B"), "预期结果");
    }

    @Test
    @Subject("聚合函数,标准差且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621012() {
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
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("C"), "预期结果");

    }

    @Test
    @Subject("聚合函数,所有聚合函数支持且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621014() {
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
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("A"), "预期结果");
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("B"), "预期结果");
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("C"), "预期结果");
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("D"), "预期结果");
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("E"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("F"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("G"), "预期结果");
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("H"), "预期结果");
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("I"), "预期结果");
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("J"), "预期结果");

    }

    @Test
    @Subject("聚合函数,平均数且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621013() {
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
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("D"), "预期结果");

    }

    // 这个查询语法需要确认
    @Test
    @Subject("聚合函数,所有聚合函数支持且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621015() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST, variance(GMT_TEST) as A,squaresum(GMT_TEST) as B,stddev(GMT_TEST) as C,avg(GMT_TEST) as D,sum(GMT_TEST) as E,min(GMT_TEST) as F, max(GMT_TEST) as G,count(GMT_TEST) as H, count(*) as I,count(distinct GMT_TEST) as J from TEST_DAILY indexwhere TEST_IP=? group by GMT_TEST having sum(GMT_TEST)<? limit 20";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = Double.valueOf(1000);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.infoText("结果" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        int sizeNumber = queryResult.getQueryResult().size();
        Assert.areEqual(10, sizeNumber, "预期结果集大小");
        Logger.info("结果：" + queryResult.getQueryResult());

    }

    // 补一个快速查询跨文本的情况
    @Test
    @Subject("聚合快速函数，所有支持的聚合函数且带别名,统计满足符合过滤条件的")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621016() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) within(GMT_TEST > ?) as A,squaresum(GMT_TEST) within(GMT_TEST>?) as B,stddev(GMT_TEST) within(GMT_TEST>=?) as C,avg(GMT_TEST) within(GMT_TEST>?) as D,sum(GMT_TEST) within(GMT_TEST>?)as E,min(GMT_TEST) within(GMT_TEST>?) as F, max(GMT_TEST) within(GMT_TEST>?) as G,count(GMT_TEST) within(GMT_TEST>?) as H, count(*) within(GMT_TEST>?) as I,count(distinct GMT_TEST) within(GMT_TEST>?) as J from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[12];
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
        params[10] = "127.0.0.2";
        params[11] = "12345";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
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
    }

    @Test
    @Subject("聚合函数，count*且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621017() {
        stepInfo("执行查询");
        String sql = "select count(*) as A from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    // 需要跟陈群确认，有文本的过滤条件count(distinct)怎么处理的
    @Test
    @Subject("聚合函数，count(distinct)且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621018() {
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
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("A"), "预期结果");
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
