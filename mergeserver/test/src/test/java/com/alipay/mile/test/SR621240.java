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
 * @version $Id: SR621240.java,v 0.1 2012-11-27 下午04:48:51 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("各种数据类型查询的支持")
public class SR621240 extends DocdbTestTools {
    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult = null;
        stepInfo("插入数据");
        // 一般正常数据
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? BYE=? SH=? INE=? DU=? FL=? LO=?";
        Object[] params = new Object[10];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.2";
        params[4] = Byte.valueOf((byte) 000);
        params[5] = Short.valueOf((short) 10);
        params[6] = Integer.valueOf(111);
        params[7] = Double.valueOf(222);
        params[8] = Float.valueOf(333);
        params[9] = Long.valueOf(444);
        for (int i = 0; i < 10; i++) {
            params[3] = Long.valueOf(i);

            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
                Logger.info("插入ID：" + insertResult.getDocId());
            } catch (Exception e) {
                Logger.info("异常" + e);
                Assert.isFalse(true, "插入数据异常");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "等待异常");
        }
    }

    @Test
    @Subject("各种数据类型值的查询,统计字符串列")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621240() {
        stepInfo("执行查询");
        String sql = "select TEST_ID,BYE,SH,INE,DU,FL,LO from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params = new Object[1];
        params[0] = "127.0.0.2";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        Assert.areEqual(10, queryResult.getQueryResult().size(), "预期结果集大小");
        stepInfo("查询结果判定");
        Assert.areEqual("12345", queryResult.getQueryResult().get(0).get("TEST_ID"), "预期结果");
        Assert.areEqual(Byte.valueOf((byte) 000), queryResult.getQueryResult().get(0).get("BYE"),
            "预期结果");
        Assert.areEqual(Short.valueOf((short) 10), queryResult.getQueryResult().get(0).get("SH"),
            "预期结果");
        Assert.areEqual(Integer.valueOf(111), queryResult.getQueryResult().get(0).get("INE"),
            "预期结果");
        Assert.areEqual(Double.valueOf(222), queryResult.getQueryResult().get(0).get("DU"), "预期结果");
        Assert.areEqual(Float.valueOf(333), queryResult.getQueryResult().get(0).get("FL"), "预期结果");
        Assert.areEqual(Long.valueOf(444), queryResult.getQueryResult().get(0).get("LO"), "预期结果");
    }

    @After
    public void tearDown() {
        try {
            stepInfo("理清数据");
            String sql = "delete from TEST_DAILY indexwhere TEST_IP=?";
            String[] params = new String[1];
            params[0] = "127.0.0.2";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "abdc");
        }
    }
}
