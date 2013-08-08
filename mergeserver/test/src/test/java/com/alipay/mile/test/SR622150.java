/**
 * 
 */
package com.alipay.mile.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * @version $Id: SR622150.java,v 0.1 2012-11-9 下午06:35:19 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("between查询")
public class SR622150 extends LevdbTestTools {
    /** 超时 */
    private int timeOut    = 5000;

    List<Long>  docidsList = new ArrayList<Long>();

    @Before
    public void setUp() {
        stepInfo("执行插入");
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.1";
        params[4] = "rowkey";

        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                //	Logger.info("docid: " + insertResult.getDocId());
            } catch (Exception e) {
                Logger.info("docid: " + e);
                Assert.isFalse(true, "执行异常");
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("between []的正常测试")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622150() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST between [?, ?] ";
        Object[] params = new Object[3];
        params[0] = "rowkey";
        params[1] = Long.valueOf(0);
        params[2] = Long.valueOf(3);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("对查询结果的判定");
        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        Assert.areEqual(4, resultList.size(), "预期结果集大小");
        for (int i = 0; i < 4; i++) {
            Assert.areEqual("12345", resultList.get(i).get("TEST_ID"), "预期查询结果");
            Assert.areEqual("milemac", resultList.get(i).get("TEST_NAME"), "预期查询结果");
            Assert.areEqual("127.0.0.1", resultList.get(i).get("TEST_IP"), "预期查询结果");
            boolean b = (Long) resultList.get(i).get("GMT_TEST") >= 0;
            boolean c = (Long) resultList.get(i).get("GMT_TEST") <= 3;
            Assert.areEqual(true, b, "预期查询结果");
            Assert.areEqual(true, c, "预期查询结果");
        }
    }

    @Test
    @Subject("between (]的正常测试")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622151() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST between (?, ?] ";
        Object[] params = new Object[3];
        params[0] = "rowkey";
        params[1] = Long.valueOf(0);
        params[2] = Long.valueOf(3);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("对查询结果的判定");
        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        Assert.areEqual(3, resultList.size(), "预期结果集大小");
        for (int i = 0; i < 3; i++) {
            Assert.areEqual("12345", resultList.get(i).get("TEST_ID"), "预期查询结果");
            Assert.areEqual("milemac", resultList.get(i).get("TEST_NAME"), "预期查询结果");
            Assert.areEqual("127.0.0.1", resultList.get(i).get("TEST_IP"), "预期查询结果");
            boolean b = (Long) resultList.get(i).get("GMT_TEST") > 0;
            boolean c = (Long) resultList.get(i).get("GMT_TEST") <= 3;
            Assert.areEqual(true, b, "预期查询结果");
            Assert.areEqual(true, c, "预期查询结果");
        }
    }

    @Test
    @Subject("between ()的正常测试")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622152() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST between (?, ?) ";
        Object[] params = new Object[3];
        params[0] = "rowkey";
        params[1] = Long.valueOf(0);
        params[2] = Long.valueOf(3);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("对查询结果的判定");
        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        Assert.areEqual(2, resultList.size(), "预期结果集大小");
        for (int i = 0; i < 2; i++) {
            Assert.areEqual("12345", resultList.get(i).get("TEST_ID"), "预期查询结果");
            Assert.areEqual("milemac", resultList.get(i).get("TEST_NAME"), "预期查询结果");
            Assert.areEqual("127.0.0.1", resultList.get(i).get("TEST_IP"), "预期查询结果");
            boolean b = (Long) resultList.get(i).get("GMT_TEST") > 0;
            boolean c = (Long) resultList.get(i).get("GMT_TEST") < 3;
            Assert.areEqual(true, b, "预期查询结果");
            Assert.areEqual(true, c, "预期查询结果");
        }
    }

    @Test
    @Subject("between [)的正常测试")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622153() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, TEST_NAME, TEST_IP, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST between [?, ?) ";
        Object[] params = new Object[3];
        params[0] = "rowkey";
        params[1] = Long.valueOf(0);
        params[2] = Long.valueOf(3);
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("对查询结果的判定");
        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        Assert.areEqual(3, resultList.size(), "预期结果集大小");
        for (int i = 0; i < 3; i++) {
            Assert.areEqual("12345", resultList.get(i).get("TEST_ID"), "预期查询结果");
            Assert.areEqual("milemac", resultList.get(i).get("TEST_NAME"), "预期查询结果");
            Assert.areEqual("127.0.0.1", resultList.get(i).get("TEST_IP"), "预期查询结果");
            boolean b = (Long) resultList.get(i).get("GMT_TEST") >= 0;
            boolean c = (Long) resultList.get(i).get("GMT_TEST") < 3;
            Assert.areEqual(true, b, "预期查询结果");
            Assert.areEqual(true, c, "预期查询结果");
        }
    }

    @After
    public void tearDown() {
        try {
            stepInfo("删除数据");
            String sql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
            String[] params = new String[1];
            params[0] = "rowkey";
            getApplationClientImpl().preDelete(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
    }
}
