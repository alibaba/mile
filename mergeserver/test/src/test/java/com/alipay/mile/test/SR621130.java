/**
 * 
 */
package com.alipay.mile.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * @version $Id: SR621130.java,v 0.1 2012-11-7 下午01:59:03 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("CTU特殊查询接口：preCtuClusterQuery")
public class SR621130 extends DocdbTestTools {

    private int timeOut = 5000;

    @Before
    public void setUp() {
        stepInfo("执行插入");
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
                params[0] = new String("a" + i);
                params[8] = new Date().getTime();
                params[9] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Assert.isTrue(insertResult.isSuccessful(), "查询执行成功");
            }
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("测试传入正常的参数，接口的功能正常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621130() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388986728,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        List<String> selectFields = new ArrayList<String>();
        selectFields.add(0, "TEST_ID");
        selectFields.add(1, "TEST_IP");
        selectFields.add(2, "GMT_TEST");
        selectFields.add(3, "TEST_NAME");
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 9;
        int offset = 0;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);
        MileQueryResult queryResult = null;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterQuery(tableName, selectFields,
                condition, clusterField, topField, orderField, orderType, limit, offset, params1,
                timeOut);

            // Assert.assertNotNull(queryResult);
            // boolean b = queryResult.getQueryResult().isEmpty();
            // String str = "";
            // for (int i = 0; i < 7; i++) {
            // for (String key : queryResult.getQueryResult().get(i).keySet()) {
            // str += queryResult.getQueryResult().get(i).get(key).toString();
            // }
            // }
            // Assert.assertEquals("bba88ccbba77ccbba66ccbba55ccbba44ccbba33ccbba22cc",
            // str);
        } catch (Exception e) {
            Logger.info("异常为" + e);
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("预期查询结果判定");
        Assert.areEqual(7, queryResult.getQueryResult().size(), "预期查询结果集大小");
        String str = "";
        for (int i = 0; i < 7; i++) {
            for (String key : queryResult.getQueryResult().get(i).keySet()) {
               // str += queryResult.getQueryResult().get(i).get(key).toString();
            	str += queryResult.getQueryResult().get(i);
            }
        }

      //  System.out.println("结果"+str);
        Assert.areEqual("{TEST_ID=a8, TEST_NAME=cc, GMT_TEST=8, TEST_IP=bb}{TEST_ID=a8, TEST_NAME=cc, GMT_TEST=8, TEST_IP=bb}{TEST_ID=a8, TEST_NAME=cc, GMT_TEST=8, TEST_IP=bb}{TEST_ID=a8, TEST_NAME=cc, GMT_TEST=8, TEST_IP=bb}{TEST_ID=a7, TEST_NAME=cc, GMT_TEST=7, TEST_IP=bb}{TEST_ID=a7, TEST_NAME=cc, GMT_TEST=7, TEST_IP=bb}{TEST_ID=a7, TEST_NAME=cc, GMT_TEST=7, TEST_IP=bb}{TEST_ID=a7, TEST_NAME=cc, GMT_TEST=7, TEST_IP=bb}{TEST_ID=a6, TEST_NAME=cc, GMT_TEST=6, TEST_IP=bb}{TEST_ID=a6, TEST_NAME=cc, GMT_TEST=6, TEST_IP=bb}{TEST_ID=a6, TEST_NAME=cc, GMT_TEST=6, TEST_IP=bb}{TEST_ID=a6, TEST_NAME=cc, GMT_TEST=6, TEST_IP=bb}{TEST_ID=a5, TEST_NAME=cc, GMT_TEST=5, TEST_IP=bb}{TEST_ID=a5, TEST_NAME=cc, GMT_TEST=5, TEST_IP=bb}{TEST_ID=a5, TEST_NAME=cc, GMT_TEST=5, TEST_IP=bb}{TEST_ID=a5, TEST_NAME=cc, GMT_TEST=5, TEST_IP=bb}{TEST_ID=a4, TEST_NAME=cc, GMT_TEST=4, TEST_IP=bb}{TEST_ID=a4, TEST_NAME=cc, GMT_TEST=4, TEST_IP=bb}{TEST_ID=a4, TEST_NAME=cc, GMT_TEST=4, TEST_IP=bb}{TEST_ID=a4, TEST_NAME=cc, GMT_TEST=4, TEST_IP=bb}{TEST_ID=a3, TEST_NAME=cc, GMT_TEST=3, TEST_IP=bb}{TEST_ID=a3, TEST_NAME=cc, GMT_TEST=3, TEST_IP=bb}{TEST_ID=a3, TEST_NAME=cc, GMT_TEST=3, TEST_IP=bb}{TEST_ID=a3, TEST_NAME=cc, GMT_TEST=3, TEST_IP=bb}{TEST_ID=a2, TEST_NAME=cc, GMT_TEST=2, TEST_IP=bb}{TEST_ID=a2, TEST_NAME=cc, GMT_TEST=2, TEST_IP=bb}{TEST_ID=a2, TEST_NAME=cc, GMT_TEST=2, TEST_IP=bb}{TEST_ID=a2, TEST_NAME=cc, GMT_TEST=2, TEST_IP=bb}", str, "查询预期结果");
    }

    @Test
    @Subject("测试传入正常的参数clusterField为多列,检查不允许有多个cluster列报错出现")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621131() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388815390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID,TEST_IP,TEST_NAME,EXTERNAL_ID,CLIENT_ID,EVENT_NAME";
        String topField = "max(GMT_TEST)";
        List<String> selectFields = new ArrayList<String>();
        selectFields.add(0, "TEST_ID");
        selectFields.add(1, "TEST_IP");
        selectFields.add(2, "GMT_TEST");
        selectFields.add(3, "TEST_NAME");
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 9;
        int offset = 0;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);

        stepInfo("执行查询");
        try {
            getApplationClientImpl().preCtuClusterQuery(tableName, selectFields, condition,
                clusterField, topField, orderField, orderType, limit, offset, params1, timeOut);
        } catch (Exception e) {
            stepInfo("预期查询异常判定");
            Logger.info("执行异常" + e);
            Assert.areEqual("不允许有多个cluster列!", e.getMessage(), "预期异常信息");
        }
    }

    @Test
    @Subject("如果limit-offset<=0，查询异常")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621132() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388885390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        List<String> selectFields = new ArrayList<String>();
        selectFields.add(0, "TEST_ID");
        selectFields.add(1, "TEST_IP");
        selectFields.add(2, "GMT_TEST");
        selectFields.add(3, "TEST_NAME");
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 1;
        int offset = 5;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);

        stepInfo("执行查询");
        try {
            getApplationClientImpl().preCtuClusterQuery(tableName, selectFields, condition,
                clusterField, topField, orderField, orderType, limit, offset, params1, timeOut);

        } catch (SqlExecuteException e) {
            stepInfo("预期查询异常判定");
            Assert.areEqual("在查询中offset应该小于limit!", e.getMessage(), "预期查询");
        } catch (Exception e) {
            Logger.info("执行异常" + e);
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("如果时间范围交集为空，返回结果集为0")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621133() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388885390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        List<String> selectFields = new ArrayList<String>();
        selectFields.add(0, "TEST_ID");
        selectFields.add(1, "TEST_IP");
        selectFields.add(2, "GMT_TEST");
        selectFields.add(3, "TEST_NAME");
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 9;
        int offset = 0;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(9);
        params1[2] = Long.valueOf(1);
        MileQueryResult queryResult = null;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterQuery(tableName, selectFields,
                condition, clusterField, topField, orderField, orderType, limit, offset, params1,
                timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("预期查询结果集判定");
        boolean b = queryResult.getQueryResult().isEmpty();
        Assert.isTrue(b, "预期结果为true");
    }

    @Test
    @Subject("测试传入List内容为空值异常的测试")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621134() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        List<String> selectFields = new ArrayList<String>();
        String condition = "indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 9;
        int offset = 0;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);
        MileQueryResult queryResult = null;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterQuery(tableName, selectFields,
                condition, clusterField, topField, orderField, orderType, limit, offset, params1,
                timeOut);
        } catch (Exception e) {
            stepInfo("预期查询结果集判定");
            Assert.areEqual(null, queryResult, "预期异常的结果集为NULL");
        }
    }

    @Test
    @Subject("测试传入参数为null，接口的异常测试")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621135() {
        stepInfo("设置系列参数");
        String condition = "indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID";
        String topField = "max(GMT_TEST)";
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 9;
        int offset = 0;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = Long.valueOf(1);
        params1[2] = Long.valueOf(9);
        MileQueryResult queryResult = null;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterQuery(null, null, condition,
                clusterField, topField, orderField, orderType, limit, offset, params1, timeOut);
        } catch (Exception e) {
            stepInfo("预期查询结果集判定");
            Assert.areEqual(null, queryResult, "预期异常的结果集为NULL");
        }

    }

    @Test
    @Subject("测试查询的字段的数据类型跟插入的数据类型不一样，接口的异常测试，判断返回的查询结果列表为空")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621136() {
        stepInfo("设置系列参数");
        String tableName = "TEST_DAILY";
        List<String> selectFields = new ArrayList<String>();
        selectFields.add(0, "a1");
        selectFields.add(1, "b1");
        selectFields.add(2, "value");
        selectFields.add(3, "c1");
        String condition = "indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "a1";
        String topField = "max(GMT_TEST)";
        String orderField = "GMT_TEST";
        boolean orderType = false;
        int limit = 9;
        int offset = 0;
        Object[] params1 = new Object[3];
        params1[0] = "cc";
        params1[1] = new Double(1);
        params1[2] = new Double(9);
        MileQueryResult queryResult = null;

        stepInfo("执行查询");
        try {
            queryResult = getApplationClientImpl().preCtuClusterQuery(tableName, selectFields,
                condition, clusterField, topField, orderField, orderType, limit, offset, params1,
                timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "异常失败");
        }

        stepInfo("预期查询结果集判定");
        boolean result = queryResult.getQueryResult().isEmpty();
        Assert.areEqual(true, result, "预期结果为真");
    }

    @After
    public void tearDown() {
        stepInfo("执行删除数据");
        try {
            String sql = "delete from TEST_DAILY indexwhere TEST_NAME=?";
            Object[] params2 = new Object[1];
            params2[0] = "cc";
            getApplationClientImpl().preDelete(sql, params2, timeOut);
        } catch (Exception e) {

            Assert.isFalse(true, "执行异常");
        }
    }
}
