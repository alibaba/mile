/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark;

import com.alipay.mile.client.ApplationClientImpl;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: CmdProvider.java, v 0.1 2012-11-6 обнГ07:52:42 yuzhong.zhao Exp $
 */
public interface CmdProvider {
    public String getName();
    
    public int execute(ApplationClientImpl client);
}
