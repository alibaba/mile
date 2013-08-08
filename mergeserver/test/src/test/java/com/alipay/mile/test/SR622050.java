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
 * @version $Id: SR622050.java,v 0.1 2012-11-9 下午02:29:10 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("sharding规则")
public class SR622050 extends LevdbTestTools {
    /** 超时 */
    private int timeOut = 5000;

    @Test
    @Subject("sharding规则，sharding1正常: GMT_TEST >20")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622050() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(30);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12345";
        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(30), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params3 = new Object[2];
        params3[0] = "rowkey";
        params3[1] = "12345";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "删除执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    //	@Test
    //	@Subject("sharding规则，sharding1异常 GMT_TEST=20")
    //	@Priority(PriorityLevel.HIGHEST)
    //	@Tester("xiaoju.luo")
    //	public void TC622051() {
    //		stepInfo("插入数据");
    //		try {
    //			String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
    //			Object[] params = new Object[6];
    //			params[0] = "12345";
    //			params[1] = "milemac";
    //			params[2] = "127.0.0.2";
    //			params[4] = "rowkey";
    //			for (int i = 0; i < 9; i++) {
    //				params[3] = Long.valueOf(20);
    //				params[5] = Long.valueOf(i);
    //				MileInsertResult insertResult = getApplationClientImpl()
    //						.preInsert(sql, params, timeOut);
    //				// Logger.info("docid: " + insertResult.getDocId());
    //			}
    //		} catch (Exception e) {
    //			// 插入异常，获取异常，进行预期判断
    //			Logger.info("TC622051异常" + e);
    //			Assert.areEqual(true, e.getMessage().contains(
    //					"在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    //		}
    //
    //	}

    //	@Test
    //	@Subject("sharding规则，sharding1异常 GMT_TEST<20")
    //	@Priority(PriorityLevel.HIGHEST)
    //	@Tester("xiaoju.luo")
    //	public void TC622052() {
    //		stepInfo("插入数据");
    //		try {
    //			String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
    //			Object[] params = new Object[6];
    //			params[0] = "12345";
    //			params[1] = "milemac";
    //			params[2] = "127.0.0.2";
    //			params[4] = "rowkey";
    //			for (int i = 0; i < 9; i++) {
    //				params[3] = Long.valueOf(10);
    //				params[5] = Long.valueOf(i);
    //				MileInsertResult insertResult = getApplationClientImpl()
    //						.preInsert(sql, params, timeOut);
    //				// Logger.info("docid: " + insertResult.getDocId());
    //			}
    //		} catch (Exception e) {
    //			// 插入异常，获取异常，进行预期判断
    //			Assert.areEqual(true, e.getMessage().contains(
    //					"在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    //		}
    //
    //	}

    // sharding2: CTU_EVENT_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,GMT_TEST=10")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622053() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(10);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";

        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // sharding2: CTU_EVENT_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,GMT_TEST=20")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622054() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(20);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(20), queryResult.getQueryResult().get(0).get("GMT_TEST"),
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

    // sharding2: CTU_EVENT_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,GMT_TEST=10")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622055() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(10);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    // sharding2: CTU_EVENT_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,10<GMT_TEST<20")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622056() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(15);
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
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(15), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
        Object[] params3 = new Object[1];
        params3[0] = "rowkey";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    ////	// sharding2: CTU_EVENT_DAILY GMT_TEST longlong 2 [10,20] 1,2
    ////	@Test
    ////	@Subject("sharding规则，sharding2异常,GMT_TEST>20")
    ////	@Priority(PriorityLevel.HIGHEST)
    ////	@Tester("xiaoju.luo")
    ////	public void TC622057() {
    ////		stepInfo("插入数据");
    ////		try {
    ////			String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
    ////			Object[] params = new Object[6];
    ////			params[0] = "12345";
    ////			params[1] = "milemac";
    ////			params[2] = "127.0.0.2";
    ////			params[4] = "rowkey";
    ////			for (int i = 0; i < 9; i++) {
    ////				params[3] = Long.valueOf(21);
    ////				params[5] = Long.valueOf(i);
    ////				MileInsertResult insertResult = getApplationClientImpl()
    ////						.preInsert(sql, params, timeOut);
    ////				Logger.info("docid: " + insertResult.getDocId());
    ////			}
    ////		} catch (Exception e) {
    ////
    ////			// 抓取异常的预期判断，插入失败
    ////			Logger.info("异常为：" + e);
    ////			Assert.areEqual(true, e.getMessage().contains(
    ////					"在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    ////		}
    ////	}
    ////
    ////	// sharding2: CTU_EVENT_DAILY GMT_TEST longlong 2 [10,20] 1,2
    ////	@Test
    ////	@Subject("sharding规则，sharding2异常,GMT_TEST<10")
    ////	@Priority(PriorityLevel.HIGHEST)
    ////	@Tester("xiaoju.luo")
    ////	public void TC622058() {
    ////		stepInfo("插入数据");
    ////		try {
    ////			String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
    ////			Object[] params = new Object[6];
    ////			params[0] = "12345";
    ////			params[1] = "milemac";
    ////			params[2] = "127.0.0.2";
    ////			params[4] = "rowkey";
    ////			for (int i = 0; i < 9; i++) {
    ////				params[3] = Long.valueOf(6);
    ////				params[5] = Long.valueOf(i);
    ////				MileInsertResult insertResult = getApplationClientImpl()
    ////						.preInsert(sql, params, timeOut);
    ////			}
    ////		} catch (Exception e) {
    ////			Logger.info("异常为：" + e);
    ////			// 抓取异常的预期判断，插入失败
    ////			Assert.areEqual(true, e.getMessage().contains(
    ////					"在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    ////			// Assert.isFalse(true, "插入失败");
    ////		}
    ////	}

    // CTU_EVENT_DAILY TEST_ID string 3:-3,-2 2 1,2
    @Test
    @Subject("sharding规则，sharding3正常,TEST_ID=12345,即34%2")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622059() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[4] = "rowkey";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                params[5] = Long.valueOf(i);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
            }
            Thread.sleep(2000);
        } catch (Exception e) {
            Logger.info("异常为：" + e);
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_VELOCITY indexwhere ROWKEY=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "rowkey";
        params[1] = "12345";

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
}
