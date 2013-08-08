/**
 * 
 */
package com.alipay.mile.test;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * 方便性能测试的用例
 * 
 * @author xiaoju.luo
 * @version $Id: SR621110.java,v 0.1 2012-11-7 上午10:27:04 xiaoju.luo Exp $
 */

public class SR621110 extends DocdbTestTools {
    private int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("执行预编译插入");
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
        Object[] params = new Object[4];
        params[0] = "abce";
        params[1] = "ctumile";
        params[2] = "127.0.0.1";
        params[3] = Long.valueOf(6);
        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "执行异常");
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("count(*)")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621110() {
        stepInfo("执行count(*)查询");
        String sql = "select count(*) as a from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params = new Object[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);

        } catch (Exception e) {
            Logger.error("异常为：" + e);
            Assert.isFalse(true, "执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(1, queryResult.getQueryResult().size(), "预期结果集大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("a"), "预期结果");
    }

    @Test
    @Subject("单列+count(*),sum+别名+group by简单性能统计")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621111() {
        stepInfo("执行查询");
        String sql = "select TEST_IP,count(*) as a,min(GMT_TEST) as b from TEST_DAILY indexwhere TEST_IP=? group by TEST_IP";

        Object[] params = new Object[1];
        params[0] = "127.0.0.1";
        MileQueryResult queryResult = null;
        try {
            long startTime = System.currentTimeMillis();
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
            long endTime = System.currentTimeMillis();
            long spend = endTime - startTime;
            Logger.info("花费时间为：" + spend + "ms");
        } catch (Exception e) {
            Logger.error("异常为：" + e);
            Assert.isFalse(true, "执行异常");
        }
        stepInfo("执行查询结果判定");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("a"), "预期结果");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("b"), "预期结果");
    }

    @Test
    @Subject("count(*)别名 withount indexwhere")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621112() {
        String sql = "select count(*) as v1 from TEST_DAILY";
        Object[] params = null;

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
        List<Map<String, Object>> result = queryResult.getQueryResult();
        Assert.areEqual(1, result.size(), "预期结果集大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("v1"), "预期结果");

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
            Assert.isFalse(true, "插入失败");
        }
    }
}
