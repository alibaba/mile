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
 * @version $Id: SR621180.java,v 0.1 2012-11-7 下午07:27:05 xiaoju.luo Exp $
 */

@RunWith(SpecRunner.class)
@Feature("in嵌套 &!= 查询")
public class SR621180 extends DocdbTestTools {
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
            MileInsertResult insertResult = null;
            params[3] = Long.valueOf(i);
            params[4] = "a" + String.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);

                // Logger.info("docid: " + insertResult.getDocId());
            } catch (Exception e) {
                Assert.isFalse(true, "插入异常");
            }

            Assert.areEqual(true, insertResult.isSuccessful(), "预期插入结果成功");
            int a = insertResult.hashCode();
            System.out.println("ww" + a);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "插入异常");
        }
    }

    @Test
    @Subject("in（空值) 查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621180() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, 11 from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST in (select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? )";
        String[] params = new String[2];
        params[0] = "127.0.0.1";
        params[1] = "127.0.0.9";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(0, queryResult.getQueryResult().size(), "预期查询结果集大小");
    }

    @Test
    @Subject("in（null), abc为null 查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621181() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, 11 from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST in (select abc from TEST_DAILY indexwhere TEST_IP=?)";
        String[] params = new String[2];
        params[0] = "127.0.0.1";
        params[1] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(0, queryResult.getQueryResult().size(), "预期查询结果集大小");
    }

    @Test
    @Subject("单个in(?,?) 查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621182() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST in (?,?) order by GMT_TEST";
        Object[] params = new Object[3];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(2);
        params[2] = Long.valueOf(8);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(2, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(2), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(1).get("GMT_TEST"),
            "预期查询结果");
    }

    @Test
    @Subject("多个in条件的or&and查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621183() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST,11 from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST in (?,?) or 11 in(?,?) order by GMT_TEST";
        Object[] params = new Object[5];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(2);
        params[2] = Long.valueOf(8);
        params[3] = "a1";
        params[4] = "a9";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(4, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(1), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual("a1", queryResult.getQueryResult().get(0).get("11"), "预期查询结果");
        Assert.areEqual(Long.valueOf(2), queryResult.getQueryResult().get(1).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual("a2", queryResult.getQueryResult().get(1).get("11"), "预期查询结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(2).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual("a8", queryResult.getQueryResult().get(2).get("11"), "预期查询结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(3).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual("a9", queryResult.getQueryResult().get(3).get("11"), "预期查询结果");
    }

    @Test
    @Subject("in(嵌套条件正常)查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621184() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST,11 from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST in(select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST=? )";
        Object[] params = new Object[3];
        params[0] = "127.0.0.1";
        params[1] = "127.0.0.1";
        params[2] = Long.valueOf(3);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(3), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果");

    }

    @Test
    @Subject("数值列的!=查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621185() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST,11 from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST !=? order by GMT_TEST";
        Object[] params = new Object[2];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(4);

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(5), queryResult.getQueryResult().get(4).get("GMT_TEST"),
            "预期查询结果");
    }

    @Test
    @Subject("字符串的!=查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621186() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST,11 from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST !=? and 11 !=? order by GMT_TEST";
        Object[] params = new Object[3];
        params[0] = "127.0.0.1";
        params[1] = Long.valueOf(4);
        params[2] = "a6";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(8, queryResult.getQueryResult().size(), "预期查询结果集大小");
        Assert.areEqual(Long.valueOf(0), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(1), queryResult.getQueryResult().get(1).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(2), queryResult.getQueryResult().get(2).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(3), queryResult.getQueryResult().get(3).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(5), queryResult.getQueryResult().get(4).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(7), queryResult.getQueryResult().get(5).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(8), queryResult.getQueryResult().get(6).get("GMT_TEST"),
            "预期查询结果");
        Assert.areEqual(Long.valueOf(9), queryResult.getQueryResult().get(7).get("GMT_TEST"),
            "预期查询结果");

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
