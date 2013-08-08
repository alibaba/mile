/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark;

import com.alipay.mile.benchmark.util.Time;
import com.alipay.mile.client.ApplationClientImpl;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: ClientWrapper.java, v 0.1 2012-11-6 ÏÂÎç09:12:02 yuzhong.zhao Exp $
 */
public class ClientWrapper {
    private ApplationClientImpl client;

    private Metrics             measurement;

    public ClientWrapper(ApplationClientImpl client) {
        this.client = client;
        this.measurement = Metrics.getInstance();
    }

    public boolean execute(CmdProvider cmdProvider) {
        long startNs = System.nanoTime();
        int result = cmdProvider.execute(client);
        long endNs = System.nanoTime();
        measurement.measure(cmdProvider.getName(), (int) ((endNs - startNs) / Time.NS_PER_MS));
        measurement.reportReturnCode(cmdProvider.getName(), result);
        if (result < 0) {
            return false;
        } else {
            return true;
        }
    }

    public Metrics getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Metrics measurement) {
        this.measurement = measurement;
    }


}
