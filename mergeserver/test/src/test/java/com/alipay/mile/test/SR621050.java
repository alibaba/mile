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
 * docdb的sharding,注释的用例不能去掉，当开启配置后是需要用的
 * 
 * @author xiaoju.luo
 * @version $Id: SR621050.java,v 0.1 2012-11-5 下午05:27:12 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("shariding规则用例")
public class SR621050 extends DocdbTestTools {
    int timeOut = 5000;

    @Test
    @Subject("sharding规则，sharding1正常: GMT_TEST >20")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621050() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(30);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
            }
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");

        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? ";
        Object[] params = new Object[1];
        params[0] = "127.0.0.2";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";

        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(30), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    // @Test
    // @Subject("sharding规则，sharding1异常 GMT_TEST=20")
    // @Priority(PriorityLevel.HIGHEST)
    // @Tester("xiaoju.luo")
    // public void TC621051() {
    // stepInfo("插入数据");
    // try {
    // String sql =
    // "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
    // Object[] params = new Object[4];
    // params[0] = "12345";
    // params[1] = "milemac";
    // params[2] = "127.0.0.2";
    // for (int i = 0; i < 9; i++) {
    // params[3] = Long.valueOf(20);
    // MileInsertResult insertResult = getApplationClientImpl()
    // .preInsert(sql, params, timeOut);
    // // Logger.info("docid: " + insertResult.getDocId());
    // }
    // } catch (Exception e) {
    // // 插入异常，获取异常，进行预期判断
    // Logger.info("TC621051插入异常：" + e);
    // Assert.areEqual(true, e.getMessage().contains(
    // "在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    // //Assert.isFalse(true, "插入失败");
    // }
    //
    // }

    // @Test
    // @Subject("sharding规则，sharding1异常 GMT_TEST<20")
    // @Priority(PriorityLevel.HIGHEST)
    // @Tester("xiaoju.luo")
    // public void TC621052() {
    // stepInfo("插入数据");
    // try {
    // String sql =
    // "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
    // Object[] params = new Object[4];
    // params[0] = "12345";
    // params[1] = "milemac";
    // params[2] = "127.0.0.2";
    // for (int i = 0; i < 9; i++) {
    // params[3] = Long.valueOf(10);
    // MileInsertResult insertResult = getApplationClientImpl()
    // .preInsert(sql, params, timeOut);
    // // Logger.info("docid: " + insertResult.getDocId());
    // }
    // } catch (Exception e) {
    // // 插入异常，获取异常，进行预期判断
    // Logger.info("TC621052插入异常：" + e);
    // Assert.areEqual(true, e.getMessage().contains(
    // "在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    // }
    //
    // }

    // sharding2: TEST_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,GMT_TEST=10")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621053() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(10);
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
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";

        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // sharding2: TEST_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,GMT_TEST=20")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621054() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(20);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("等待1秒");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");

        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(20), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    // sharding2: TEST_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,GMT_TEST=10")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621055() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(10);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("等待1秒");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");

        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(10), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    // sharding2: TEST_DAILY GMT_TEST longlong 2 [10,20] 1,2
    @Test
    @Subject("sharding规则，sharding2正常,10小于GMT_TEST小于20")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621056() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(15);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("等待1秒");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");

        }
        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(15), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // // sharding2: TEST_DAILY GMT_TEST longlong 2 [10,20] 1,2
    // @Test
    // @Subject("sharding规则，sharding2异常,GMT_TEST>20")
    // @Priority(PriorityLevel.HIGHEST)
    // @Tester("xiaoju.luo")
    // public void TC621057() {
    // stepInfo("插入数据");
    // try {
    // String sql =
    // "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
    // Object[] params = new Object[4];
    // params[0] = "12345";
    // params[1] = "milemac";
    // params[2] = "127.0.0.2";
    // for (int i = 0; i < 9; i++) {
    // params[3] = Long.valueOf(21);
    // MileInsertResult insertResult = getApplationClientImpl()
    // .preInsert(sql, params, timeOut);
    // }
    // } catch (Exception e) {
    //
    // // 抓取异常的预期判断，插入失败
    // Logger.info("TC621057异常" + e);
    // Assert.areEqual(true, e.getMessage().contains(
    // "在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    // }
    // }

    // // sharding2: TEST_DAILY GMT_TEST longlong 2 [10,20] 1,2
    // @Test
    // @Subject("sharding规则，sharding2异常,GMT_TEST<10")
    // @Priority(PriorityLevel.HIGHEST)
    // @Tester("xiaoju.luo")
    // public void TC621058() {
    // stepInfo("插入数据");
    // try {
    // String sql =
    // "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
    // Object[] params = new Object[4];
    // params[0] = "12345";
    // params[1] = "milemac";
    // params[2] = "127.0.0.2";
    // for (int i = 0; i < 9; i++) {
    // params[3] = Long.valueOf(6);
    // MileInsertResult insertResult = getApplationClientImpl()
    // .preInsert(sql, params, timeOut);
    // Logger.info("docid: " + insertResult.getDocId());
    // }
    // } catch (Exception e) {
    //
    // // 抓取异常的预期判断，插入失败
    // Assert.areEqual(true, e.getMessage().contains(
    // "在执行插入命令时sharding出错，没有匹配sharding规则的节点!"), "预期异常");
    // // Assert.isFalse(true, "插入失败");
    // }
    // }

    // TEST_DAILY TEST_ID string 3:-3,-2 2 1,2
    @Test
    @Subject("sharding规则，sharding3正常,TEST_ID=12345,即34%2")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621059() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }

        stepInfo("等待1秒");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");

        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12345";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }
}