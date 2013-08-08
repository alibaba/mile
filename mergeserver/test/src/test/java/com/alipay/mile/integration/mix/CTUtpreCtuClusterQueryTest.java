/**
 * created since 2012-3-16
 */
package com.alipay.mile.integration.mix;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.mile.client.SqlClientTemplate;
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * @author xiaoju.luo
 * @version $Id: SqlClientTemplateTest.java,v 0.1 2012-3-16 上午06:38:20
 *          xiaoju.luo Exp $
 */
public class CTUtpreCtuClusterQueryTest extends MileClientAbstract {

    private static final Logger LOGGER  = Logger.getLogger(CTUtpreCtuClusterQueryTest.class
                                            .getName());
    SqlClientTemplate           sqlClient;
    private int                 timeOut = 5000;

    @Before
    public void setUp() {
        try {
            super.setUp();
            sqlClient = (SqlClientTemplate) applationClientImpl;
        } catch (UnsupportedEncodingException e1) {
            Assert.fail();
        }
        String sql = "insert into TEST_DAILY TEST_ID=? TEST_IP=? TEST_NAME=? exid=? clid=? eename=? g1=? U=? time=? GMT_TEST=?";
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
                MileInsertResult insertResult = sqlClient.preInsert(sql, params, timeOut);
                Assert.assertNotNull(insertResult);
            }
        } catch (Exception e) {
            // LOGGER.error("异常报错", e);
            Assert.fail();
        }

    }

    @Test
    /*
     * 测试传入正常的参数，接口返回结果正常
     */
    public void testPreCtuClusterQuery() {
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

        try {
            MileQueryResult queryResult = sqlClient.preCtuClusterQuery(tableName, selectFields,
                condition, clusterField, topField, orderField, orderType, limit, offset, params1,
                timeOut);

            // Assert.assertNotNull(queryResult);
            // boolean b = queryResult.getQueryResult().isEmpty();
            String str = "";
            for (int i = 0; i < 7; i++) {
                for (String key : queryResult.getQueryResult().get(i).keySet()) {
                    str += queryResult.getQueryResult().get(i).get(key).toString();
                }
            }
            Assert.assertEquals("bba88ccbba77ccbba66ccbba55ccbba44ccbba33ccbba22cc", str);
        } catch (Exception e) {
            LOGGER.error("执行异常", e);
            Assert.fail();
        }
    }

    @Test
    /*
     * 测试传入正常的参数clusterField为多列,检查"不允许有多个cluster列!"报错出现
     */
    public void testPreCtuClusterQueryMulCluster() {
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388815390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        String clusterField = "TEST_ID,TEST_IP,TEST_NAME,exid,clid,eename";
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

        try {
            sqlClient.preCtuClusterQuery(tableName, selectFields, condition, clusterField,
                topField, orderField, orderType, limit, offset, params1, timeOut);
            Assert.fail();
        } catch (Exception e) {
            LOGGER.error("执行异常", e);
            Assert.assertEquals(e.getMessage(), "不允许有多个cluster列!");
        }
    }

    @Test
    /*
     * 如果limit-offset<=0，返回结果集为空
     */
    public void testPreCtuClusterQuery1() {
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

        try {
            sqlClient.preCtuClusterQuery(tableName, selectFields, condition, clusterField,
                topField, orderField, orderType, limit, offset, params1, timeOut);
            Assert.fail();

        } catch (SqlExecuteException e) {
            Assert.assertEquals("在查询中offset应该小于limit!", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("执行异常", e);
            Assert.fail();
        }
    }

    @Test
    /*
     * 如果时间范围交集为空，返回结果集为0
     */
    public void testPreCtuClusterTimeQuery() {
        String tableName = "TEST_DAILY";
        String condition = "seghint(0,1388885390,0,0) indexwhere TEST_NAME=? where GMT_TEST>? and GMT_TEST<?";
        // String clusterField =
        // "TEST_ID,TEST_IP,TEST_NAME,exid,clid,eename";
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

        try {
            MileQueryResult queryResult = sqlClient.preCtuClusterQuery(tableName, selectFields,
                condition, clusterField, topField, orderField, orderType, limit, offset, params1,
                timeOut);
            // Assert.assertNotNull(queryResult);
            boolean b = queryResult.getQueryResult().isEmpty();
            Assert.assertTrue(b);
        } catch (Exception e) {
            LOGGER.error("执行异常", e);
            Assert.fail();
        }
    }

    @Test
    /*
     * 测试传入List内容为空值，接口的测试
     */
    public void testPreCtuClusterQueryEmptyException() {
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
        try {
            queryResult = sqlClient.preCtuClusterQuery(tableName, selectFields, condition,
                clusterField, topField, orderField, orderType, limit, offset, params1, timeOut);
            // Assert.assertNotNull(queryResult);
            // System.out.println(queryResult);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertNull(queryResult);
        }
    }

    @Test
    /*
     * 测试传入参数为null，接口的异常测试
     */
    public void testPreCtuClusterQueryNullException() {
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
        try {
            queryResult = sqlClient.preCtuClusterQuery(null, null, condition, clusterField,
                topField, orderField, orderType, limit, offset, params1, timeOut);
            // Assert.assertNotNull(queryResult);
            Assert.fail();
        } catch (Exception e) {
            // LOGGER.error("异常", e);
            Assert.assertNull(queryResult);
        }
    }

    @Test
    /*
     * 测试查询的字段的数据类型跟插入的数据类型不一样，接口的异常测试，判断返回的查询结果列表为空
     */
    public void testPreCtuClusterQueryTypeException() {
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
        try {
            queryResult = sqlClient.preCtuClusterQuery(tableName, selectFields, condition,
                clusterField, topField, orderField, orderType, limit, offset, params1, timeOut);
            // 判断返回的查询结果列表为空
            boolean result = queryResult.getQueryResult().isEmpty();
            Assert.assertTrue(result);
        } catch (Exception e) {
            // LOGGER.error("异常", e);
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
        try {
            String sql = "delete from TEST_DAILY indexwhere TEST_NAME=?";
            Object[] params2 = new Object[1];
            params2[0] = "cc";
            MileDeleteResult dResult = sqlClient.preDelete(sql, params2, timeOut);
            Assert.assertNotNull(dResult);

        } catch (Exception e) {
            // LOGGER.error("", e);
            Assert.fail();
        }
    }
}
