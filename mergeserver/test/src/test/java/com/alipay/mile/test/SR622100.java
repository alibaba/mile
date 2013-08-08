/**
 * 
 */
package com.alipay.mile.test;

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
 * @version $Id: SR622100.java,v 0.1 2012-11-9 下午06:09:11 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("聚合函数与group by，limit")
public class SR622100 extends LevdbTestTools {
    int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("插入数据");
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];

        for (int i = 0; i < 100; i++) {
            MileInsertResult insertResult;
            params[0] = String.valueOf(i / 10);
            params[1] = String.valueOf(i / 10);
            params[2] = "127.0.0.1";
            params[3] = Long.valueOf(i);
            params[4] = "rowkey";
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                //	Logger.info("docid: " + insertResult.getDocId());
            } catch (Exception e) {
                Logger.info("异常为：" + e);
                Assert.isFalse(true, "插入失败");
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "插入失败");
        }
    }

    @Test
    @Subject("聚合函数，count,max,min+as别名+其他列+group by+limit查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622100() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as a, max(GMT_TEST) as v1, min(GMT_TEST) as v2, sum(GMT_TEST) as v3, count(GMT_TEST) as v4, count(distinct TEST_IP) as b, TEST_NAME from TEST_VELOCITY indexwhere ROWKEY=? group by TEST_ID, TEST_NAME limit 1000";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.error("异常" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("对查询结果的预期判定");
        Assert.areEqual(10, queryResult.getQueryResult().size(), "查询结果集大小");
        for (Map<String, Object> record : queryResult.getQueryResult()) {
            int i = Integer.parseInt((String) record.get("TEST_NAME"));
            Assert.areEqual(Long.valueOf(i * 10 + 9), record.get("v1"), "预期查询结果");
            Assert.areEqual(Long.valueOf(i * 10), record.get("v2"), "预期查询结果");
            Assert.areEqual(Double.valueOf(i * 100 + 45), record.get("v3"), "预期查询结果");
            Assert.areEqual(Long.valueOf(10), record.get("v4"), "预期查询结果");
            Assert.areEqual(Long.valueOf(10), record.get("a"), "预期查询结果");
            Assert.areEqual(Long.valueOf(1), record.get("b"), "预期查询结果");
        }
    }

    @Test
    @Subject("聚合函数，count,count(distinct),count(),max,min+group by 其他列+limit查询的英文常规用例")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622101() {
        stepInfo("执行查询");
        String sql = "select count(distinct GMT_TEST) as a, max(GMT_TEST) as v1, min(GMT_TEST) as v2, sum(GMT_TEST) as v3, count(GMT_TEST) as v4, count(distinct TEST_IP) as b from TEST_VELOCITY indexwhere ROWKEY=? group by TEST_ID limit 1000";
        String[] params = new String[1];
        params[0] = "rowkey";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.error("异常为：" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("对查询结果的预期判定");
        Assert.areEqual(10, queryResult.getQueryResult().size(), "预期查询结果集大小");
        for (Map<String, Object> record : queryResult.getQueryResult()) {
            int i = (Integer.parseInt(record.get("v1").toString()) - 9) / 10;
            Assert.areEqual(Long.valueOf(i * 10 + 9), record.get("v1"), "预期查询结果");
            Assert.areEqual(Long.valueOf(i * 10), record.get("v2"), "预期查询结果");
            Assert.areEqual(Double.valueOf(i * 100 + 45), record.get("v3"), "预期查询结果");
            Assert.areEqual(Long.valueOf(10), record.get("v4"), "预期查询结果");
            Assert.areEqual(Long.valueOf(10), record.get("a"), "预期查询结果");
            Assert.areEqual(Long.valueOf(1), record.get("b"), "预期查询结果");
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
            Assert.isFalse(true, "插入失败");
        }
    }
}
