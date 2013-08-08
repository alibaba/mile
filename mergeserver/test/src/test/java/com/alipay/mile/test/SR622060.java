/**
 * 
 */
package com.alipay.mile.test;

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
 * @author xiaoju.luo
 * @version $Id: SR622060.java,v 0.1 2012-11-9 下午03:18:45 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("sharding规则2")
public class SR622060 extends LevdbTestTools {
    int timeOut = 5000;

    // CTU_EVENT_DAILY TEST_ID string 3:-3,-2 2 1,2
    @Test
    @Subject("sharding规则，sharding3正常,TEST_ID=12355,即35%2,落在节点1（docN2）")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622060() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12355";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            Assert.isTrue(true, "插入失败");
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Logger.info("等待异常：" + e);
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12355";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isTrue(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // CTU_EVENT_DAILY TEST_ID string 3 2 1,2
    @Test
    @Subject("sharding规则，sharding3正常,TEST_ID=12355,即35%2,落在节点1（docN2）")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622061() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12355";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12355";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // CTU_EVENT_DAILY TEST_ID string 3 2 1,2
    @Test
    @Subject("sharding规则，sharding3正常,TEST_ID=12354,即35%2,落在节点0（docN1）")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622062() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12354";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12354";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "删除执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    //	@Test
    //	public void setUp() {
    //		Long cTime = System.currentTimeMillis();
    //		System.out.println("当前时间：" + cTime);
    //		Long dTime = cTime - 100000000;
    //		System.out.println("结果时间：" + dTime);
    //	}
    //
    // CTU_EVENT_DAILY GMT_TEST longlong 4 >-100000000 1,2,用例修订
    @Test
    @Subject("sharding规则，sharding4正常,GMT_TEST")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622063() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12355";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = 1363559763923L;
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12355";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert
            .areEqual(1363559763923L, queryResult.getQueryResult().get(0).get("GMT_TEST"), "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "删除执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    // CTU_EVENT_DAILY GMT_TEST longlong 4 >-100000000 1,2, 用例修订
    //	@Test
    //	@Subject("sharding规则，sharding4异常,GMT_TEST=")
    //	@Priority(PriorityLevel.HIGHEST)
    //	@Tester("xiaoju.luo")
    //	public void TC622064() {
    //		stepInfo("插入数据");
    //		try {
    //			String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
    //			Object[] params = new Object[6];
    //			params[0] = "12355";
    //			params[1] = "milemac";
    //			params[2] = "127.0.0.2";
    //			params[4] = "rowkey";
    //			for (int i = 0; i < 9; i++) {
    //				params[3] = 1343559853389L;
    //				params[5] = Long.valueOf(i);
    //				MileInsertResult insertResult = getApplationClientImpl()
    //						.preInsert(sql, params, timeOut);
    //				Logger.info("docid: " + insertResult.getDocId());
    //			}
    //		} catch (Exception e) {
    //			Logger.info("异常为：" + e);
    //			Assert.areEqual(true, e.getMessage().contains(
    //					"在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    //		}
    //	}

}
