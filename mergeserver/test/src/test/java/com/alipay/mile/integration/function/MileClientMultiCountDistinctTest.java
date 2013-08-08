/**
 * created since 2012-7-16
 */

package com.alipay.mile.integration.function;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 * 各种数据类型的count(distinct),包括string,long,integer,byte,double,short
 * @author xiaoju.luo
 * @version $Id: MileClientMultiCountDistinctTest.java,v 0.1 2012-7-16 下午01:29:47 xiaoju.luo Exp $
 */
public class MileClientMultiCountDistinctTest extends MileClientAbstract {
    private static final Logger LOGGER  = Logger.getLogger(MileClientCountDistinctFuncTest.class
                                            .getName());

    /** 超时 */
    private int                 timeOut = 5000;

    @Override
    public void setUp() {
        try {
            super.setUp();
        } catch (UnsupportedEncodingException e) {
            fail();
        }

        String sql = "insert into TEST_DAILY TEST_ID=? col1=? uuuid=? col2=? col3=? col4=? col5=? col6=? col7=? col8=? col9=? col10=? col11=? eeeid=? seeid=? mmmid=? eep=? gg=?";
        Object[] params = new Object[18];
        params[0] = "Winsertfacade";
        params[1] = "2345";
        params[2] = Long.valueOf(20);
        params[3] = "adef";
        params[4] = "addd";
        params[5] = "c1234";
        params[6] = "d1234";
        params[7] = "e1234";
        params[8] = "f1234";
        params[9] = "h1234";
        params[10] = "jj234";
        params[11] = "jj234";
        params[12] = Double.valueOf(2.0);
        params[13] = Double.valueOf(3.2);
        params[14] = Byte.valueOf((byte) 23);
        //params[15] = Integer.valueOf(11);  comment for one succ uncomment for the other 
        params[16] = Short.valueOf((short) 1);
        params[17] = Long.valueOf(22);

        for (int i = 0; i < 10; i++) {

            MileInsertResult insertResult;
            params[15] = Integer.valueOf(i);
            try {
                insertResult = applationClientImpl.preInsert(sql, params, timeOut);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("docid: " + insertResult.getDocId());
                }
            } catch (Exception e) {
                fail();
            }
        }

    }

    @Override
    public void tearDown() {
        try {
            // 删除数据
            String sql = "delete from TEST_DAILY indexwhere TEST_ID=?";
            String[] params = new String[1];
            params[0] = "Winsertfacade";
            applationClientImpl.preDelete(sql, params, timeOut);
            super.tearDown();
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testMutliCountDistinct() {
        String sql = "select count(distinct TEST_ID) as a, count(distinct uuuid) as b,count(distinct mmmid) as c, count(distinct seeid) as d,count(distinct eeeid) as e, count(distinct eep) as f from TEST_DAILY indexwhere TEST_ID=?";
        String[] params = new String[1];
        params[0] = "Winsertfacade";
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(queryResult);
            }
        } catch (Exception e) {
            fail();
        }

        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("a"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("b"));
        assertEquals(Long.valueOf(10), queryResult.getQueryResult().get(0).get("c"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("d"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("e"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("f"));
    }

    @Test
    public void testMutliCountDistinctMixGroupby() {
        String sql = "select count(distinct TEST_ID) as a, count(distinct uuuid) as b,count(distinct mmmid) as c, count(distinct seeid) as d,count(distinct eeeid) as e, count(distinct eep) as f from TEST_DAILY indexwhere TEST_ID=? group by TEST_ID having TEST_ID !=? order by mmmid asc";
        String[] params = new String[2];
        params[0] = "Winsertfacade";
        params[1] = "abc";
        MileQueryResult queryResult = null;
        try {
            queryResult = applationClientImpl.preQueryForList(sql, params, timeOut);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(queryResult);
            }
        } catch (Exception e) {
            fail();
        }

        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("a"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("b"));
        assertEquals(Long.valueOf(10), queryResult.getQueryResult().get(0).get("c"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("d"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("e"));
        assertEquals(Long.valueOf(1), queryResult.getQueryResult().get(0).get("f"));
    }
}
