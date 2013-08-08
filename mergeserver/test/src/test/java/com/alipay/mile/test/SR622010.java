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

/**聚合函数包括跨string&数字混合，现在测试无法通过
 * @author xiaoju.luo
 * @version $Id: SR622000.java,v 0.1 2012-11-8 下午05:36:08 xiaoju.luo Exp $
 */

@RunWith(SpecRunner.class)
@Feature("字符串&数字混合聚合函数查询")
public class SR622010 extends LevdbTestTools {
    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult;
        stepInfo("插入数据");
        //一般正常数据
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = "rowkey";

        for (int i = 0; i < 10; i++) {
            params[3] = Long.valueOf(i);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入数据异常");
            }
        }

        // 统计列的文本输入
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[3] = "文本输入";
        params[4] = "rowkey";
        params[5] = Long.valueOf(10);
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
    public void TC622010() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[1];
        params[0] = "rowkey";
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
    public void TC622011() {
        stepInfo("执行查询");
        String sql = "select squaresum(GMT_TEST) as B from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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
    public void TC622012() {
        stepInfo("执行查询");
        String sql = "select stddev(GMT_TEST) as C from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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
    @Subject("聚合函数,平均数且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622013() {
        stepInfo("执行查询");
        String sql = "select avg(GMT_TEST) as D from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Double.NaN, queryResult.getQueryResult().get(0).get("D"), "预期结果");

    }

    @Test
    @Subject("聚合函数，count列且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622014() {
        stepInfo("执行查询");
        String sql = "select count(GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，count(distinct)且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622015() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(11), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，min且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622016() {
        stepInfo("执行查询");
        String sql = "select min(GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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
    @Subject("聚合函数，max且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622017() {
        stepInfo("执行查询");
        String sql = "select max(GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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
    @Subject("聚合函数，sum且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622018() {
        stepInfo("执行查询");
        String sql = "select sum(GMT_TEST) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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
    @Subject("聚合函数,所有聚合函数支持且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622019() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) as A,squaresum(GMT_TEST) as B,stddev(GMT_TEST) as C,avg(GMT_TEST) as D,sum(GMT_TEST) as E,min(GMT_TEST) as F, max(GMT_TEST) as G,count(GMT_TEST) as H, count(*) as I,count(distinct GMT_TEST) as J from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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

    // 这个查询语法需要确认
    @Test
    @Subject("聚合函数,所有聚合函数支持且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC6220100() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST, variance(GMT_TEST) as A,squaresum(GMT_TEST) as B,stddev(GMT_TEST) as C,avg(GMT_TEST) as D,sum(GMT_TEST) as E,min(GMT_TEST) as F, max(GMT_TEST) as G,count(GMT_TEST) as H, count(*) as I,count(distinct GMT_TEST) as J from TEST_VELOCITY indexwhere ROWKEY=? group by GMT_TEST having sum(GMT_TEST)<? order by GMT_TEST limit 20";
        Object[] params = new Object[2];
        params[0] = "rowkey";
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
        Assert.areEqual(10, sizeNumber, "预发结果集");
        Logger.info("结果列表" + queryResult.getQueryResult());
    }

    // 补一个快速查询跨文本的情况
    @Test
    @Subject("聚合快速函数，所有支持的聚合函数且带别名,且对条件进行过滤")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC6220110() {
        stepInfo("执行查询");
        String sql = "select variance(GMT_TEST) within(GMT_TEST > ?) as A,squaresum(GMT_TEST) within(GMT_TEST>?) as B,stddev(GMT_TEST) within(GMT_TEST>=?) as C,avg(GMT_TEST) within(GMT_TEST>?) as D,sum(GMT_TEST) within(GMT_TEST>?)as E,min(GMT_TEST) within(GMT_TEST>?) as F, max(GMT_TEST) within(GMT_TEST>?) as G,count(GMT_TEST) within(GMT_TEST>?) as H, count(*) within(GMT_TEST>?) as I,count(distinct GMT_TEST) within(GMT_TEST>?) as J  from TEST_VELOCITY indexwhere ROWKEY=?";
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
            Logger.info("异常：" + e);
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
