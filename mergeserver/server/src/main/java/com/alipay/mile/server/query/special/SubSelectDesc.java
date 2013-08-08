/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataOutput;
import java.io.IOException;

import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 
 * @author bin.lb
 *
 */
public interface SubSelectDesc {
    // rewrite 
    public void rewrite() throws SqlExecuteException;

    // from sub select
    public void fromSubSelect(SubSelect subSelect);

    // get type
    public byte getType();

    // write to stream
    public void writeToStream(DataOutput os) throws IOException;

    // set doc server result
    public void setDocResult(SubSelectResult result);

    // merge doc server result
    public void mergeDocResult(SubSelectResult result) throws SqlExecuteException;
}
