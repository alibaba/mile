package com.alipay.mile.integration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.ats.ScenarioTest;
import com.alipay.mile.client.ApplationClientImpl;

public class TestTools extends ScenarioTest {
    private static ApplationClientImpl applationClientImpl;

    public static synchronized ApplationClientImpl getApplationClientImpl()
                                                                           throws UnsupportedEncodingException {
        if (applationClientImpl == null) {
            applationClientImpl = new ApplationClientImpl();
            List<String> serverList = new ArrayList<String>();

            serverList.add("127.0.0.1:8964");
            applationClientImpl.setMergeServerList(serverList);
            applationClientImpl.init();
            byte[] a = applationClientImpl.getPassWord();
            applationClientImpl.getClientProperty();
            applationClientImpl.getMergeServerList();
            applationClientImpl.getUserName();
            applationClientImpl.getVersion();
            applationClientImpl.getWorkerExecutorCount();

        }
        return applationClientImpl;
    }

}
