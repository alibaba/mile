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
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.client.result.MileUpdateResult;

/**
 * update单列更新
 * 
 * @author xiaoju.luo
 * @version $Id: SR621030.java,v 0.1 2012-11-2 下午06:11:01 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("单列更新")
public class SR621030 extends DocdbTestTools {

    private int timeOut = 5000;

    @Before
    public void setUp() {

        stepInfo("插入数据");
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.3";
        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                // Logger.info("docid: " + insertResult.getDocId());

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
    @Subject("单列更新,indexwhere&where")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621030() {

        stepInfo("进行更新");
        String sqlUpdate = "update TEST_DAILY set GMT_TEST=? indexwhere TEST_IP=?";
        Object[] params1 = new Object[2];
        params1[0] = Long.valueOf(111);
        params1[1] = "127.0.0.3";

        try {
            getApplationClientImpl().preUpdate(sqlUpdate, params1, timeOut);
            Thread.sleep(1000);
        } catch (Exception e) {
            Assert.isFalse(true, "更新执行异常");
        }

        stepInfo("进行更新后查询操作");
        String sqlSelect = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? limit 1";
        Object[] params2 = new Object[1];
        params2[0] = "127.0.0.3";
        MileQueryResult queryResult1 = null;
        try {
            queryResult1 = getApplationClientImpl().preQueryForList(sqlSelect, params2, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "更新执行异常");
        }
        stepInfo("确认更新成功");
        Logger.infoText("结果为" + queryResult1.getQueryResult());
        Assert.areEqual(Long.valueOf(111), queryResult1.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
    }

    @Test
    @Subject("单列更新,没有indexwhere&where")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621031() {

        stepInfo("进行更新操作");
        String sqlUpdate = "update TEST_DAILY set GMT_TEST=?";
        Object[] params1 = new Object[1];
        params1[0] = Long.valueOf(222);

        MileUpdateResult updateResult = null;

        try {
            updateResult = getApplationClientImpl().preUpdate(sqlUpdate, params1, timeOut);
            Thread.sleep(1000);
        } catch (Exception e) {
            Assert.isFalse(true, "更新执行异常");
        }
        Assert.areEqual(true, updateResult.isSuccessful(), "预期结果");

        stepInfo("进行更新后查询操作");
        String sqlSelect = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params2 = new Object[1];
        params2[0] = "127.0.0.3";
        MileQueryResult queryResult1 = null;
        try {
            queryResult1 = getApplationClientImpl().preQueryForList(sqlSelect, params2, timeOut);

        } catch (Exception e) {
            Logger.info("用例TC621031异常为：" + e);
            Assert.isFalse(true, "更新执行异常");
        }

        Assert.areEqual(10, queryResult1.getQueryResult().size(), "结果集大小判定");
        stepInfo("确认更新成功");
        Assert.areEqual(Long.valueOf(222), queryResult1.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

    }

    @Test
    @Subject("单列更新异常，不满足filter索引且且非hash索引")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621032() {

        stepInfo("进行更新操作");
        String sqlUpdate = "update TEST_DAILY set TEST_ID=? indexwhere TEST_IP=?";
        Object[] params1 = new Object[2];
        params1[0] = "123459";
        params1[1] = "127.0.0.3";
        MileUpdateResult updateResult = null;
        try {
            updateResult = getApplationClientImpl().preUpdate(sqlUpdate, params1, timeOut);
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常" + e);
            Assert.isTrue(false, "更新执行异常");
        }

        Assert.areEqual(false, updateResult.isSuccessful(), "预期异常");

    }

    @After
    public void tearDown() {
        MileDeleteResult deleteNumber = null;
        try {
            stepInfo("删除数据不带indexwhere");
            String sql = "delete from TEST_DAILY";
            Object[] params = new Object[1];
            params[0] = null;
            deleteNumber = getApplationClientImpl().preDelete(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "清理数据失败");
        }
        Assert.areEqual(10, deleteNumber.getDeleteNum(), "预期删除数量");
    }
}
