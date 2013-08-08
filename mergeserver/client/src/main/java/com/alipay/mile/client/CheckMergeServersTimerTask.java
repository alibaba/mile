/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.client;

import org.apache.log4j.Logger;

import com.alipay.mile.communication.ApplationClientService;
import com.alipay.mile.communication.MileClient;

/**
 * @author jin.qian
 * @version $Id: ClientMergeServicesTimerTask.java,v 0.1 2011-4-6 ÏÂÎç05:49:52 jin.qian Exp $
 */
public class CheckMergeServersTimerTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(CheckMergeServersTimerTask.class
                                           .getName());
    private MileClient          mileClient;

    CheckMergeServersTimerTask(MileClient mileClient) {
        this.mileClient = mileClient;
    }

    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checkMergeServer is run");
        }
        ApplationClientService.checkMergeServer(mileClient);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("checkMergeServer is end");
        }
    }

    public MileClient getMileClient() {
        return mileClient;
    }

    public void setMileClient(MileClient mileClient) {
        this.mileClient = mileClient;
    }
}
