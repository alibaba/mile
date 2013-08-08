/**
 * 
 */
package com.alipay.mile.test;

import java.util.Date;

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
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * @author xiaoju.luo
 * @version $Id: SR621120.java,v 0.1 2012-11-7 下午01:20:32 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("CTU特殊查询接口：PreCtuClusterCountQuery")
public class SR621120 extends DocdbTestTools {
    private int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("执行预编译插入");
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_IP=? TEST_NAME=? EXTERNAL_ID=? CLIENT_ID=? EVENT_NAME=? g1=? U=? time=? GMT_TEST=?";
        Object[] params = new Object[10];
        params[1] = "bb";
        params[2] = "cc";
        params[3] = "dd";
        params[4] = "ee";
        params[5] = "ff";
        params[6] = "gg";
        params[7] = "name";
        try {
            for (int i = 0; i < 10; i++) {
                params[0] = new String("aa" + i);
                params[8] = new Date().getTime();
                params[9] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.infoText("docid为：" + insertResult.getDocId());
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入异常");
        }
    }

    @Test
    @Subject("测试传入正常的参数，接口的功能正常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621120() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388889343,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);
        int queryResult = 0;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterCountQuery(tableName, condition,
                clusterField, topField, params1, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("预期查询结果判定");
        Assert.areEqual(7, queryResult, "预期查询结果集大小");
    }

    @Test
    @Subject("测试传入多列cluster的参数异常用例，需要抛出异常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621121() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1338889343,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID,TEST_IP,TEST_NAME,EXTERNAL_ID,CLIENT_ID,EVENT_NAME";

        String topField = "max(GMT_TEST)";
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);
        int queryResult = 0;
        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterCountQuery(tableName, condition,
                clusterField, topField, params1, timeOut);

        } catch (SqlExecuteException e) {
            Logger.info("SqlExecuteException" + e);
            Assert.areEqual("cluster列只能包含一列!", e.getMessage(), "预期异常");

        } catch (Exception e) {
            Logger.info("其他异常" + e);
            Assert.isFalse(true, "查询异常");
        }
        // stepInfo("预期查询结果判定");
        // Assert.areEqual(0, queryResult, "预期查询结果集大小");
    }

    @Test
    @Subject("测试传入的参数内容为空时，结果集大小为0")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621122() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388885390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        Object[] params1 = new Object[3];
        // params1[0] = "cc";
        // params1[1] = Long.valueOf(1);
        // params1[2] = Long.valueOf(9);
        int queryResult = 0;
        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterCountQuery(tableName, condition,
                clusterField, topField, params1, timeOut);

        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询异常");
        }
        stepInfo("预期查询结果判定");
        Assert.areEqual(0, queryResult, "预期查询结果集大小");
    }

    @Test
    @Subject("测试传入的参数内容为null时，接口报异常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621123() {
        stepInfo("设置系列参数");
        String tableName = "";
        String condition = "seghint(0,1388815390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";

        stepInfo("执行查询");
        int queryResult = 0;
        try {
            queryResult = getApplationClientImpl().preCtuClusterCountQuery(tableName, condition,
                clusterField, topField, null, timeOut);

        } catch (Exception e) {
            // Logger.info("异常" + e);
            Assert.areEqual(true, e.getClass().equals(SqlExecuteException.class), "预期异常");
        }
        stepInfo("预期查询结果判定");
        Assert.areEqual(0, queryResult, "预期查询结果集大小");
    }

    @Test
    @Subject("测试传入的参数string为空格时，造成 mile的语法错误，接口报异常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621124() {
        stepInfo("设置系列参数");
        String condition = "";
        String clusterField = "";
        String topField = "";
        int queryResult = 0;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterCountQuery(null, condition,
                clusterField, topField, null, timeOut);
        } catch (Exception e) {
            // Logger.error("SqlExecuteException" + e);
            Assert.areEqual(true, e.getMessage().contains("String index out of range: -1"), "预期异常");
        }
    }

    @Test
    @Subject("测试传入的参数的数据类型不是插入时的数据类型，接口返回结果为0的异常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621125() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388815390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = new Double(1);
        params1[2] = new Double(9);

        stepInfo("执行查询");
        int queryResult = 0;
        try {
            queryResult = getApplationClientImpl().preCtuClusterCountQuery(tableName, condition,
                clusterField, topField, params1, timeOut);

        } catch (Exception e) {
            Logger.error("SqlExecuteException" + e);
            Assert.isFalse(true, "查询失败");
        }
        stepInfo("预期查询结果判定");
        Assert.areEqual(0, queryResult, "预期查询结果集大小");
    }

    @After
    public void tearDown() {
        stepInfo("执行数据删除");
        String sql = "delete from TEST_DAILY indexwhere TEST_NAME=?";
        Object[] params2 = new Object[1];
        params2[0] = "cc";
        MileDeleteResult dResult = null;
        try {
            dResult = getApplationClientImpl().preDelete(sql, params2, timeOut);
        } catch (Exception e) {

            Assert.isFalse(true, "删除异常");
        }
        Assert.isTrue(dResult.getDeleteNum() > 0, "删除结果大于0");

    }

}
