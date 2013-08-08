package com.alipay.mile.test;

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
 * @version $Id: SR621140.java,v 0.1 2012-11-7 下午03:10:06 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("一般条件下的聚合查询+order by")
public class SR621140 extends DocdbTestTools {
    private int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("执行插入");
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];

        for (int i = 0; i < 100; i++) {
            MileInsertResult insertResult;
            params[0] = String.valueOf(i % 10);
            params[1] = String.valueOf(i);
            params[2] = "127.0.0.1";
            params[3] = Long.valueOf(i);
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
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("order by asc+limit正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621140() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST limit 1000";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(100, queryResult.getQueryResult().size(), "预期结果集大小");
        for (int i = 0; i < 100; i++) {
            Map<String, Object> record = queryResult.getQueryResult().get(i);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期查询结果");
        }
    }

    @Test
    @Subject("order by dessc+limit正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621141() {
        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST desc limit 1000";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(100, queryResult.getQueryResult().size(), "预期结果集大小");
        for (int i = 0; i < 100; i++) {
            Map<String, Object> record = queryResult.getQueryResult().get(i);
            Assert.areEqual(Long.valueOf(99 - i), record.get("GMT_TEST"), "预期结果集大小");
        }
    }

    @Test
    @Subject("distinct+order by asc正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621142() {
        stepInfo("执行查询");
        String sql = "select distinct GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(100, queryResult.getQueryResult().size(), "预期结果集大小");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = queryResult.getQueryResult().get(i);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期结果");
        }
    }

    @Test
    @Subject("distinct,单列+order by asc正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621143() {
        stepInfo("执行查询");
        String sql = "select distinct TEST_ID, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {

            Assert.isFalse(true, "执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(100, queryResult.getQueryResult().size(), "预期结果集大小");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = queryResult.getQueryResult().get(i);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期结果集大小");
            Assert.areEqual(String.valueOf(i), record.get("TEST_ID"), "预期结果集大小");
        }
    }

    @Test
    @Subject("distinct,单列+order by asc 不带indexwhere正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621144() {
        stepInfo("执行查询");
        String sql = "select distinct TEST_IP, TEST_ID, GMT_TEST from TEST_DAILY order by GMT_TEST";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(100, queryResult.getQueryResult().size(), "预期结果集大小");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = queryResult.getQueryResult().get(i);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期查询结果");
            Assert.areEqual(String.valueOf(i), record.get("TEST_ID"), "预期查询结果");
            Assert.areEqual("127.0.0.1", record.get("TEST_IP"), "预期查询结果");
        }
    }

    @Test
    @Subject("sum as 别名+group by+order by正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621145() {
        stepInfo("执行查询");
        String sql = "select sum(GMT_TEST) as v1 from TEST_DAILY indexwhere TEST_IP=? group by TEST_ID order by v1";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("查询结果判定");
        List<Map<String, Object>> result = queryResult.getQueryResult();
        Assert.areEqual(10, result.size(), "预期结果集大小");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = result.get(i);
            Assert.areEqual(1, record.size(), "预期查询结果");
            Assert.areEqual(Double.valueOf(450 + 10 * i), record.get("v1"), "预期查询结果");
        }
    }

    @Test
    @Subject("order by+limit正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621146() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST limit 10";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
        stepInfo("查询结果判定");
        List<Map<String, Object>> result = queryResult.getQueryResult();
        Assert.areEqual(10, result.size(), "预期结果集大小");
        for (int i = 0; i < 10; i++) {
            Map<String, Object> record = result.get(i);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期查询结果");
        }
    }

    @Test
    @Subject("order by+limit+offset正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621147() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST limit 100 offset 10";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("预期结果判定");
        List<Map<String, Object>> result = queryResult.getQueryResult();
        Assert.areEqual(90, result.size(), "预期结果集大小");
        for (int i = 10; i < 100; i++) {
            Map<String, Object> record = result.get(i - 10);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期查询结果");
        }
    }

    @Test
    @Subject("order by+limit A+offsetA,结果集为0正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621148() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, GMT_TEST from TEST_DAILY indexwhere TEST_IP=? order by GMT_TEST limit 100 offset 100";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
        stepInfo("预期结果判定");
        List<Map<String, Object>> result = queryResult.getQueryResult();
        Assert.areEqual(0, result.size(), "预期结果集大小");
    }

    @Test
    @Subject("order by A,B正常用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621149() {
        stepInfo("执行查询");
        String sql = "select TEST_ID, GMT_TEST, VALUE from TEST_DAILY indexwhere TEST_IP=? order by VALUE, GMT_TEST";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {

            Assert.isFalse(true, "执行异常");
        }

        stepInfo("预期结果判定");
        List<Map<String, Object>> result = queryResult.getQueryResult();
        Assert.areEqual(100, result.size(), "预期结果集大小");
        for (int i = 0; i < 100; i++) {
            Map<String, Object> record = result.get(i);
            Assert.areEqual(Long.valueOf(i), record.get("GMT_TEST"), "预期查询结果");
            Assert.areEqual(null, record.get("VALUE"), "预期查询结果");
        }
    }

    @After
    public void tearDown() {

        stepInfo("执行删除数据");
        String sql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        String[] params = new String[1];
        params[0] = "127.0.0.1";
        try {
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
    }
}
