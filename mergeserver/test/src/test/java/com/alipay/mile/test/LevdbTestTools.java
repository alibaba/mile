package com.alipay.mile.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.ats.ScenarioTest;
import com.alipay.mile.client.ApplationClientImpl;

/**
 * @author xiaoju.luo
 * @version $Id: TestToolsForLevdb.java,v 0.1 2012-11-8 ÏÂÎç05:37:38 xiaoju.luo
 *          Exp $
 */
public class LevdbTestTools extends ScenarioTest {
    protected static ApplationClientImpl applationClientImpl;

    public static synchronized ApplationClientImpl getApplationClientImpl()
                                                                           throws UnsupportedEncodingException {
        if (applationClientImpl == null) {
            applationClientImpl = new ApplationClientImpl();
            List<String> serverList = new ArrayList<String>();
            serverList.add("127.0.0.1 ");
            applationClientImpl.setMergeServerList(serverList);
            applationClientImpl.init();
        }
        return applationClientImpl;
    }
}
