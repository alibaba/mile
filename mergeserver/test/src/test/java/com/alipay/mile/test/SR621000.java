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
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * 模糊查询的中文支持用例
 * 
 * @author xiaoju.luo
 * @version $Id: SR621000.java,v 0.1 2012-10-30 下午03:44:29 xiaoju.luo Exp $
 */

@RunWith(SpecRunner.class)
@Feature("模糊查询")
public class SR621000 extends DocdbTestTools {

    int timeOut = 5000;

    @Before
    public void setUp() {

        stepInfo("插入数据");
        MileInsertResult insertResult;
        String sql = "insert into TEST_ADDRESS TEST_VALUE=? with wordseg(TEST_VALUE)=(?,?)";
        Object[] params = new Object[3];

        try {
            // 数据1
            params[0] = "北京天安门";
            params[1] = "北京";
            params[2] = "天安门";
            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                Assert.isFalse(true, "插入失败");
            }
            // 数据2
            params[0] = "北京广场";
            params[1] = "北京";
            params[2] = "广场";
            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                Assert.isFalse(true, "插入失败");
            }
            // 数据3
            params[0] = "北京饭店";
            params[1] = "北京";
            params[2] = "饭店";

            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                Assert.isFalse(true, "插入失败");
            }
            // 数据4
            params[0] = "北京王府井";
            params[1] = "北京";
            params[2] = "王府井";

            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            if (!insertResult.isSuccessful()) {
                Assert.isFalse(true, "插入失败");
            }

            // 数据5
            String sql1 = "insert into TEST_ADDRESS TEST_VALUE=? with wordseg(TEST_VALUE)=(?,?,?,?)";
            Object[] params1 = new Object[5];
            params1[0] = "北京王府井全聚德饭店";
            params1[1] = "北京";
            params1[2] = "王府井";
            params1[3] = "全聚德";
            params1[4] = "饭店";

            insertResult = getApplationClientImpl().preInsert(sql1, params1, timeOut);
            if (!insertResult.isSuccessful()) {
                Assert.isFalse(true, "插入失败");
            }

            // 数据6
            String sql2 = "insert into TEST_ADDRESS TEST_VALUE=? with wordseg(TEST_VALUE)=(?,?,?,?,?)";
            Object[] params2 = new Object[6];
            params2[0] = "北京王府井全聚德饭店甲8号";
            params2[1] = "北京";
            params2[2] = "王府井";
            params2[3] = "全聚德";
            params2[4] = "饭店";
            params2[5] = "甲8号";
            insertResult = getApplationClientImpl().preInsert(sql2, params2, timeOut);
            if (!insertResult.isSuccessful()) {
                Assert.isFalse(true, "插入失败");
            }
        } catch (Exception e) {
            Logger.info("异常" + e);
            Assert.isFalse(true, "插入异常");

        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "插入失败");

        }
    }

    @Test
    // 结果不正确，没有按照匹配度高低计算排序
    @Subject("模糊查询,3个分词都能匹配，且有2个分词能同时匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621000() {
        stepInfo("1.执行查询");
        String sql = "select TEST_VALUE from TEST_ADDRESS indexwhere TEST_VALUE match (?,?,?,?,?)";
        String[] params = new String[5];

        try {
            params[0] = "北京";
            params[1] = "饭店";
            params[2] = "王府井";
            params[3] = "全聚德";
            params[4] = "甲8号";
            MileQueryResult queryResult = getApplationClientImpl().preQueryForList(sql, params,
                timeOut);

            stepInfo("2.对查询结果集中的值进行判定");
            //打印日志
            System.out.println("结果集"+queryResult.getSqlResultSet());
            Assert.areEqual("北京王府井全聚德饭店甲8号", queryResult.getQueryResult().get(0).get("TEST_VALUE"),
                "预期结果");
        } catch (Exception e) {
            Logger.warn("用例0异常:" + e);
            Assert.isFalse(true, "查询失败");
        }

    }

    @Test
    @Subject("模糊查询,3个分词只有1个分词能匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621001() {
        stepInfo("1.执行查询");
        String sql = "select TEST_VALUE from TEST_ADDRESS indexwhere TEST_VALUE match (?,?,?)";
        String[] params = new String[3];

        try {
            params[0] = "南京";
            params[1] = "饭店";
            params[2] = "王府井";
            MileQueryResult queryResult = getApplationClientImpl().preQueryForList(sql, params,
                timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();

            stepInfo("2.对查询结果集判定");
            int sizeValue = resultList.size();
            Assert.areEqual(4, sizeValue, "大小正常");
        } catch (Exception e) {

            Assert.isFalse(true, "查询失败");
        }

    }

    @Test
    @Subject("模糊查询,3个分词只有1个分词能匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621002() {
        stepInfo("1.执行查询");
        String sql = "select TEST_VALUE from TEST_ADDRESS indexwhere TEST_VALUE match (?,?,?)";
        String[] params = new String[3];

        try {
            params[0] = "南京";
            params[1] = "饭店";
            params[2] = "湖北";
            MileQueryResult queryResult = getApplationClientImpl().preQueryForList(sql, params,
                timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();

            stepInfo("2.对查询结果集判定");
            int sizeValue = resultList.size();
            Assert.areEqual(3, sizeValue, "结果集大小正常");
        } catch (Exception e) {
            Assert.isFalse(true, "查询失败");
        }

    }

    @Test
    @Subject("模糊查询,3个分词2个同时匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621003() {
        stepInfo("1.执行查询");
        String sql = "select TEST_VALUE from TEST_ADDRESS indexwhere TEST_VALUE match (?,?,?)";
        String[] params = new String[3];

        try {
            params[0] = "北京";
            params[1] = "饭店";
            params[2] = "武汉";
            MileQueryResult queryResult = getApplationClientImpl().preQueryForList(sql, params,
                timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();

            stepInfo("2.对查询结果集判定");
            int sizeValue = resultList.size();
            Assert.areEqual(3, sizeValue, "结果集大小正常");

        } catch (Exception e) {
            Assert.isFalse(true, "查询失败");
        }

    }

    @Test
    @Subject("模糊查询,3个分词没有有1个分词能匹配")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621004() {
        stepInfo("1.执行查询");
        String sql = "select TEST_VALUE from TEST_ADDRESS indexwhere TEST_VALUE match (?,?,?)";
        String[] params = new String[3];
        List<Map<String, Object>> resultList = null;
        try {
            params[0] = "南京";
            params[1] = "大厦";
            params[2] = "天空";
            MileQueryResult queryResult = getApplationClientImpl().preQueryForList(sql, params,
                timeOut);
            resultList = queryResult.getQueryResult();

        } catch (Exception e) {
            Assert.isFalse(true, "查询失败");
        }

        stepInfo("2.对查询结果集判定");
        int sizeValue = resultList.size();
        Assert.areEqual(0, sizeValue, "结果集大小正常");

    }

    @After
    public void tearDown() {
        MileDeleteResult delResult = null;
        try {
             String sql = "delete from TEST_ADDRESS";
             Object[] params = new Object[1];
             params[0] = null;
             delResult = getApplationClientImpl().preDelete(sql, params, timeOut);
             //deleteNumber = getApplationClientImpl().preDelete(sql, params, timeOut);
        	
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
        Assert.areEqual(6, delResult.getDeleteNum(), "删除预期数量");
    }
}
