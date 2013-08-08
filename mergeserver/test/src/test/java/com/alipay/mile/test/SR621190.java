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
 * @version $Id: SR621190.java,v 0.1 2012-11-7 下午08:03:26 xiaoju.luo Exp $
 */

@RunWith(SpecRunner.class)
@Feature("intersection&unions查询")
public class SR621190 extends DocdbTestTools {
    private int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("插入数据");
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? 11=?";
        Object[] params = new Object[5];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.1";

        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            params[4] = "a" + String.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            } catch (Exception e) {
                Assert.isFalse(true, "插入异常");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "插入异常");
        }
    }

    @Test
    @Subject("A intersection B查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621190() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST>? order by GMT_TEST intersection select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST>? order by GMT_TEST";
        Object[] params = new Object[4];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(5);
        params[2] = "127.0.0.1";
        params[3] = Long.valueOf(7);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("执行异常" + e);
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(2, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(1).get("GMT_TEST"),
            "预期查询结果集大小");
    }

    @Test
    @Subject("A unions B查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621191() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST unions select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST>? order by GMT_TEST";
        Object[] params = new Object[5];
        params[0] = "127.0.0.1";
        params[1] = "127.0.0.1";
        params[2] = Long.valueOf(5);

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("执行异常" + e);
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(10, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(9).get("GMT_TEST"),
            "预期查询结果集大小");
    }

    @Test
    @Subject("A intersect B unions C查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621192() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST>? order by GMT_TEST  intersection select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST>? order by GMT_TEST unions select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST>? order by GMT_TEST";
        Object[] params = new Object[6];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(5);
        params[2] = "127.0.0.1";
        params[3] = Long.valueOf(7);
        params[4] = "127.0.0.1";
        params[5] = Long.valueOf(5);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("执行异常" + e);
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(4, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(7), queryResult.getQueryResult().get(1).get("GMT_TEST"),
            "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(2).get("GMT_TEST"),
            "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(3).get("GMT_TEST"),
            "预期查询结果集大小");
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
            Assert.isFalse(true, "删除异常");
        }
    }

}
