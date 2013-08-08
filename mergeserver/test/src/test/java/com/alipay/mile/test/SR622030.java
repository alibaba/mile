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
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * update单列更新
 * 
 * @author xiaoju.luo
 * @version $Id: SR621030.java,v 0.1 2012-11-9 下午06:11:01 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("单列更新")
public class SR622030 extends LevdbTestTools {

    private int timeOut = 5000;

    @Before
    public void setUp() {

        stepInfo("插入数据");
        String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
        Object[] params = new Object[6];
        params[0] = "12345";
        params[1] = "milemac";
        params[2] = "127.0.0.3";
        params[4] = "rowkey";
        for (int i = 0; i < 10; i++) {
            MileInsertResult insertResult;
            params[3] = Long.valueOf(i);
            params[5] = Long.valueOf(i);
            try {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            } catch (Exception e) {
                Assert.isFalse(true, "插入失败");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行异常");
        }
    }

    @Test
    @Subject("单列更新,indexwhere&where")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622030() {

        stepInfo("进行更新");
        String sqlUpdate = "update TEST_VELOCITY set GMT_TEST=? indexwhere ROWKEY=?";
        Object[] params1 = new Object[2];
        params1[0] = Long.valueOf(111);
        params1[1] = "rowkey";

        try {
            getApplationClientImpl().preUpdate(sqlUpdate, params1, timeOut);

        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "更新执行异常");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assert.isFalse(true, "执行异常");
        }

        stepInfo("进行更新后查询操作");
        String sqlSelect = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params2 = new Object[1];
        params2[0] = "rowkey";
        MileQueryResult queryResult1 = null;
        try {
            queryResult1 = getApplationClientImpl().preQueryForList(sqlSelect, params2, timeOut);

        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "更新执行异常");
        }
        stepInfo("确认更新成功");
        Logger.infoText("结果为" + queryResult1.getQueryResult().size());
        Assert.areEqual(Long.valueOf(111), queryResult1.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
    }

    //	@Test
    //	@Subject("单列更新异常，不满足filter索引且且非hash索引")
    //	@Priority(PriorityLevel.HIGHEST)
    //	@Tester("xiaoju.luo")
    //	public void TC622031() {
    //
    //		stepInfo("进行更新操作");
    //		String sqlUpdate = "update TEST_VELOCITY set TEST_ID=? indexwhere ROWKEY=?";
    //		Object[] params1 = new Object[2];
    //		params1[0] = "123459";
    //		params1[1] = "rowkey";
    //
    //		try {
    //			getApplationClientImpl().preUpdate(sqlUpdate, params1, timeOut);
    //
    //		} catch (Exception e) {
    //			Logger.info("异常" + e);
    //			// Assert.isTrue(false, "更新执行异常");
    //		}
    //
    //		stepInfo("进行更新后查询操作");
    //		String sqlSelect = "select TEST_ID from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
    //		Object[] params2 = new Object[2];
    //		params2[0] = "rowkey";
    //		params2[1] = "123459";
    //		MileQueryResult queryResult1 = null;
    //		try {
    //			queryResult1 = getApplationClientImpl().preQueryForList(sqlSelect,
    //					params2, timeOut);
    //
    //		} catch (Exception e) {
    //			Logger.info("结果为" + e);
    //			Assert.isFalse(true, "更新执行异常");
    //		}
    //		// 判定更新不成功
    //		Assert.areEqual(0, queryResult1.getQueryResult().size(), "更新后预期查询结果");
    //
    //	}

    @After
    public void tearDown() {
        MileDeleteResult deleteNumber = null;
        try {
            stepInfo("删除数据");
            String sql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
            String[] params = new String[1];
            params[0] = "rowkey";
            deleteNumber = getApplationClientImpl().preDelete(sql, params, timeOut);

        } catch (Exception e) {
            Assert.isFalse(true, "清理数据失败");
        }
        // Assert.areEqual(10, deleteNumber.getDeleteNum(), "预期删除数量");
    }
}
