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
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * @author xiaoju.luo
 * @version $Id: SR622160.java,v 0.1 2012-11-9 下午06:44:30 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("where&dochint查询")
public class SR622160 extends LevdbTestTools {

    /** 超时 */
    private int timeOut    = 5000;
    List<Long>  docidsList = new ArrayList<Long>();

    @Before
    public void setUp() {
        stepInfo("插入数据");
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.1";
        params[4] = "rowkey";
        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[0] = String.valueOf(i % 2);
            params[3] = Long.valueOf(i);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                docidsList.add(insertResult.getDocId());
                //Logger.info("docid: " + insertResult.getDocId());
            } catch (Exception e) {
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
    @Subject("where A and B匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622160() {
        stepInfo("执行查询");
        String sql = "select TEST_IP, TEST_ID, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST > ? and TEST_ID = ?";
        Object[] params = new Object[3];
        params[0] = "rowkey";
        params[1] = Long.valueOf(6);
        params[2] = "1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("预期查询结果判定");
        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        Assert.areEqual(2, resultList.size(), "预期结果集大小");
        for (int i = 0; i < 2; i++) {
            Assert.areEqual("127.0.0.1", resultList.get(i).get("TEST_IP"), "预期结果");
            Assert.areEqual("1", resultList.get(i).get("TEST_ID"), "预期结果");
        }
    }

    @Test
    @Subject("where A or B匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622161() {
        stepInfo("执行查询");
        String sql = "select TEST_IP, TEST_ID, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID = ? or TEST_ID = ?";
        Object[] params = new Object[3];
        params[0] = "rowkey";
        params[1] = "0";
        params[2] = "1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("预期查询结果判定");
        List<Map<String, Object>> resultList = queryResult.getQueryResult();
        Assert.areEqual(10, resultList.size(), "预期查询结果");
        for (int i = 0; i < 10; i++) {
            Assert.areEqual("127.0.0.1", resultList.get(i).get("TEST_IP"), "预期查询结果");
        }
    }

    @Test
    @Subject("不支持dochint查询")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622162() {
        stepInfo("执行查询以及结果判定");
        String sql = "select TEST_ID, CIENT_IP, TEST_NAME, GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? DOCHINT mile_doc_id=";
        Object[] params = new Object[1];
        params[0] = "rowkey";

        try {
            for (int i = 0; i < 1; i++) {
                MileQueryResult queryResult = getApplationClientImpl().preQueryForList(
                    sql + docidsList.get(i), params, timeOut);
                List<Map<String, Object>> resultList = queryResult.getQueryResult();
                Assert.areEqual(String.valueOf(i % 2), resultList.get(0).get("TEST_ID"), "预期查询结果");
                Assert.areEqual(Long.valueOf(i), resultList.get(0).get("GMT_TEST"), "预期查询结果");
            }
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.areEqual(true, e.getClass().equals(SqlExecuteException.class), "预期异常");
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
