/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataInput;
import java.io.IOException;

/**
 * 
 * @author bin.lb
 *
 */
public interface SubSelectResult {
    public void readFromStream(DataInput is, SubSelectDesc subSelectDesc) throws IOException;
}
