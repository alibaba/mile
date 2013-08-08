package com.alipay.mile.integration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.alipay.mile.integration.function.MileClientCountDistinctFuncTest;
import com.alipay.mile.integration.function.MileClientCountFuncTest;
import com.alipay.mile.integration.function.MileClientMaxFuncTest;
import com.alipay.mile.integration.function.MileClientMinFuncTest;
import com.alipay.mile.integration.function.MileClientMixFuncTest;
import com.alipay.mile.integration.function.MileClientRepeatFuncTest;
import com.alipay.mile.integration.function.MileClientSumFuncTest;
import com.alipay.mile.integration.groupby.MileClientComplexGroupbyTest;
import com.alipay.mile.integration.groupby.MileClientComplexHavingTest;
import com.alipay.mile.integration.groupby.MileClientSimpleGroupbyTest;
import com.alipay.mile.integration.groupby.MileClientSimpleHavingTest;
import com.alipay.mile.integration.insert.MileClientPrepareInsertTest;
import com.alipay.mile.integration.orderby.MileClientAscOrderTest;
import com.alipay.mile.integration.orderby.MileClientDescOrderTest;
import com.alipay.mile.integration.orderby.MileClientDistinctOrderTest;
import com.alipay.mile.integration.orderby.MileClientGroupOrderTest;
import com.alipay.mile.integration.orderby.MileClientLimitOrderTest;
import com.alipay.mile.integration.orderby.MileClientMultiOrderTest;
import com.alipay.mile.integration.select.MileClientAllCountSelectTest;
import com.alipay.mile.integration.select.MileClientBetweenSelectTest;
import com.alipay.mile.integration.select.MileClientBigIntersectTest;
import com.alipay.mile.integration.select.MileClientComplexSelectTest;
import com.alipay.mile.integration.select.MileClientDistinctSelectTest;
import com.alipay.mile.integration.select.MileClientIntersectTest;
import com.alipay.mile.integration.select.MileClientLimitSelectTest;
import com.alipay.mile.integration.select.MileClientSimpleSelectTest;
import com.alipay.mile.integration.select.MileClientSubSelectTest;
import com.alipay.mile.integration.select.MileClientTimeHintSelectTest;
import com.alipay.mile.integration.select.MileClientWhereSelectTest;
import com.alipay.mile.integration.update.MileClientUpdateTest;

public class TestAll extends TestCase {
    /**
     * 集成测试总类
     * @author jin.qian
     * @version $Id: TestAll.java,v 0.1 2010-4-27 下午05:36:40 liansuo Exp $
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        //function
        suite.addTestSuite(MileClientCountFuncTest.class);
        suite.addTestSuite(MileClientCountDistinctFuncTest.class);
        suite.addTestSuite(MileClientMaxFuncTest.class);
        suite.addTestSuite(MileClientMinFuncTest.class);
        suite.addTestSuite(MileClientMixFuncTest.class);
        suite.addTestSuite(MileClientRepeatFuncTest.class);
        suite.addTestSuite(MileClientSumFuncTest.class);
        //groupby
        suite.addTestSuite(MileClientSimpleGroupbyTest.class);
        suite.addTestSuite(MileClientComplexGroupbyTest.class);
        suite.addTestSuite(MileClientSimpleHavingTest.class);
        suite.addTestSuite(MileClientComplexHavingTest.class);
        //insert
        suite.addTestSuite(MileClientPrepareInsertTest.class);
        //ordery
        suite.addTestSuite(MileClientAscOrderTest.class);
        suite.addTestSuite(MileClientDescOrderTest.class);
        suite.addTestSuite(MileClientDistinctOrderTest.class);
        suite.addTestSuite(MileClientGroupOrderTest.class);
        suite.addTestSuite(MileClientLimitOrderTest.class);
        suite.addTestSuite(MileClientMultiOrderTest.class);
        //select
        suite.addTestSuite(MileClientAllCountSelectTest.class);
        suite.addTestSuite(MileClientBetweenSelectTest.class);
        suite.addTestSuite(MileClientComplexSelectTest.class);
        suite.addTestSuite(MileClientDistinctSelectTest.class);
        //suite.addTestSuite(MileClientDochintSelectTest.class);
        suite.addTestSuite(MileClientLimitSelectTest.class);
        suite.addTestSuite(MileClientSimpleSelectTest.class);
        suite.addTestSuite(MileClientTimeHintSelectTest.class);
        suite.addTestSuite(MileClientIntersectTest.class);
        suite.addTestSuite(MileClientSubSelectTest.class);
        suite.addTestSuite(MileClientWhereSelectTest.class);
        suite.addTestSuite(MileClientBigIntersectTest.class);
        //
        //suite.addTestSuite(MileClientCtuSpecialSelectTest.class);

        //update
        suite.addTestSuite(MileClientUpdateTest.class);
        
        return suite;
    }
}
