/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import com.alipay.mile.client.ApplationClientImpl;

/**
 *
 * @author yunliang.shi
 * @version $Id: MileClientAbstract.java, v 0.1 2011-6-2 ионГ11:26:56 yunliang.shi Exp $
 */
public class MileClientAbstract extends TestCase {
    public ApplationClientImpl applationClientImpl;

    @Override
    public void setUp() throws UnsupportedEncodingException {
        applationClientImpl = TestTools.getApplationClientImpl();
    }
    
    
}
