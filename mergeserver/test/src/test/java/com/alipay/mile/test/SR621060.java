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
 * sharding 3.4正常、异常用例
 * 注释掉的用例不能去掉，当开启配置后需要用
 * @author xiaoju.luo
 * @version $Id: SR621060.java,v 0.1 2012-11-5 下午07:05:16 xiaoju.luo Exp $
 */
@RunWith(SpecRunner.class)
@Feature("sharding规则")
public class SR621060 extends DocdbTestTools {
    int timeOut = 5000;

    // TEST_DAILY TEST_ID string 3:-3,-2 2 1,2
    @Test
    @Subject("sharding规则，sharding3正常,TEST_ID=12355,即35%2")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621060() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12355";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isTrue(true, "插入失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12355";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }

        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isTrue(true, "查询执行异常");
        }

        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // TEST_DAILY TEST_ID string 3 2 1,2
    @Test
    @Subject("sharding规则，sharding3对列的string取hashcode然后对2取模,落在节点1（docNo2）上")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621061() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12355";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "等待失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12355";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // TEST_DAILY TEST_ID string 3 2 1,2
    @Test
    @Subject("sharding规则，sharding3,对列的string取hashcode然后对2取模,落在节点0（docNo1）上")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621062() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12354";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = Long.valueOf(6);
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Logger.info("等待异常：" + e);
            Assert.isFalse(true, "执行失败");
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12354";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert.areEqual(Long.valueOf(6), queryResult.getQueryResult().get(0).get("GMT_TEST"),
            "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");

    }

    // @Test
    // public void setUp(){
    // Long cTime=System.currentTimeMillis();
    // System.out.println("当前时间："+cTime);
    // Long dTime=cTime-100000000;
    // System.out.println("结果时间："+dTime);
    // }
    //	
    // TEST_DAILY GMT_TEST longlong 4 >-100000000 1,2
    @Test
    @Subject("sharding规则，sharding4正常,GMT_TEST")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621063() {
        stepInfo("插入数据");
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12355";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            for (int i = 0; i < 9; i++) {
                params[3] = 1363649761971L;
                MileInsertResult insertResult = getApplationClientImpl().preInsert(sql, params,
                    timeOut);
                // Logger.info("docid: " + insertResult.getDocId());
            }
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "插入失败");
        }

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Logger.info("等待异常为：" + e);
        }

        stepInfo("执行查询");
        String sql = "select GMT_TEST from TEST_DAILY indexwhere TEST_IP=? where TEST_ID=?";
        Object[] params = new Object[2];
        params[0] = "127.0.0.2";
        params[1] = "12355";

        MileQueryResult queryResult = null;
        try {
            queryResult = getApplationClientImpl().preQueryForList(sql, params, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        stepInfo("查询结果判定");
        Assert.areEqual(9, queryResult.getQueryResult().size(), "预期结果大小");
        Assert
            .areEqual(1363649761971L, queryResult.getQueryResult().get(0).get("GMT_TEST"), "预期结果");

        stepInfo("执行数据清除");
        String deleteSql = "delete from TEST_DAILY indexwhere TEST_IP=?";
        Object[] params3 = new Object[1];
        params3[0] = "127.0.0.2";
        MileDeleteResult deleteResult = null;
        try {
            deleteResult = getApplationClientImpl().preDelete(deleteSql, params3, timeOut);
        } catch (Exception e) {
            Logger.info("异常：" + e);
            Assert.isFalse(true, "查询执行异常");
        }
        Assert.areEqual(9, deleteResult.getDeleteNum(), "清理数据预期数量");
    }

    // // TEST_DAILY GMT_TEST longlong 4 >-100000000 1,2
    // @Test
    // @Subject("sharding规则，sharding4异常,GMT_TEST=")
    // @Priority(PriorityLevel.HIGHEST)
    // @Tester("xiaoju.luo")
    // public void TC621064() {
    // stepInfo("插入数据");
    // try {
    // String sql =
    // "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
    // Object[] params = new Object[4];
    // params[0] = "12355";
    // params[1] = "milemac";
    // params[2] = "127.0.0.2";
    // for (int i = 0; i < 9; i++) {
    // params[3] = 1353550397609L;
    // MileInsertResult insertResult = getApplationClientImpl()
    // .preInsert(sql, params, timeOut);
    // }
    // } catch (Exception e) {
    // Logger.info("异常：" + e);
    // Assert.areEqual(true,
    // e.getMessage().contains("在执行插入命令时sharding出错，没有匹配sharding规则的节点!"),
    // "预期异常");
    // }
    // }
}
