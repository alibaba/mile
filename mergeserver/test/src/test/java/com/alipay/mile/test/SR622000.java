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
 * @version $Id: SR622000.java,v 0.1 2012-11-8 下午07:05:58 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("null聚合函数查询")
public class SR622000 extends LevdbTestTools {
    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult;
        stepInfo("插入数据");
        //一般正常数据
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=? ABC=?";
        Object[] params = new Object[7];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = "rowkey";
        params[6] = null;
        for (int i = 0; i < 10; i++) {
            params[3] = Long.valueOf(i);
            params[5] = Long.valueOf(i);
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
    @Subject("聚合函数，方差且带别名,统计null列")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622010() {
        stepInfo("执行查询");
        String sql = "select variance(ABC) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params = new Object[1];
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
    @Subject("聚合函数，平方和且有别名查询，统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622011() {
        stepInfo("执行查询");
        String sql = "select squaresum(ABC) as B from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("B"), "预期结果");
    }

    @Test
    @Subject("聚合函数,标准差且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622012() {
        stepInfo("执行查询");
        String sql = "select stddev(ABC) as C from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("C"), "预期结果");

    }

    @Test
    @Subject("聚合函数,所有聚合函数支持且有别名查询,统计null")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622014() {
        stepInfo("执行查询");
        String sql = "select variance(ABC) as A,squaresum(ABC) as B,stddev(ABC) as C,avg(ABC) as D,sum(ABC) as E,min(ABC) as F, max(ABC) as G,count(ABC) as H, count(*) as I,count(distinct ABC) as J from TEST_VELOCITY indexwhere ROWKEY=?";
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
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("B"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("C"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("D"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("E"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("F"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("G"), "预期结果");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("H"), "预期结果");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("I"), "预期结果");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("J"), "预期结果");

    }

    @Test
    @Subject("聚合函数,平均数且有别名查询,统计列含文本")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622013() {
        stepInfo("执行查询");
        String sql = "select avg(ABC) as D from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(null, queryResult.getQueryResult().get(0).get("D"), "预期结果");

    }

    @Test
    @Subject("聚合函数，count且带别名")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622015() {
        stepInfo("执行查询");
        String sql = "select count(ABC) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
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
    @Subject("聚合函数，count(distinct)且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622016() {
        stepInfo("执行查询");
        String sql = "select count(distinct ABC) as A from TEST_VELOCITY indexwhere ROWKEY=?";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("A"), "预期结果");
    }

    @Test
    @Subject("聚合函数，max且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622017() {
        stepInfo("执行查询");
        String sql = "select max(ABC) as A from TEST_VELOCITY indexwhere ROWKEY=?";
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
    @Subject("聚合函数，min且带别名,统计列含有本文跳过")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622018() {
        stepInfo("执行查询");
        String sql = "select min(ABC) as A from TEST_VELOCITY indexwhere ROWKEY=?";
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
    public void TC622019() {
        stepInfo("执行查询");
        String sql = "select sum(ABC) as A from TEST_VELOCITY indexwhere ROWKEY=?";
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
