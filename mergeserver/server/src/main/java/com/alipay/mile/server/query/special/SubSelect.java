/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * SubSelect interface.
 * 
 * @author bin.lb
 * 
 */
public interface SubSelect {
    // sub select type
    public static final byte SUB_SELECT_TYPE_RAW_VALUE  = 1;
    public static final byte SUB_SELECT_TYPE_MAX_FUNC   = 2;
    public static final byte SUB_SELECT_TYPE_MIN_FUNC   = 4;
    public static final byte SUB_SELECT_TYPE_COUNT_FUNC = 5;
    public static final byte SUB_SELECT_TYPE_SUM_FUNC   = 6;

    public byte getType();

    public void setType(byte type);

    public void writeRequestToStream(DataOutput os) throws IOException;

    public void readRequestFromStream(DataInput is) throws IOException;

    public void writeResultToStream(DataOutput os) throws IOException;

    public void readResultFromStream(DataInput is) throws IOException;

    public void setResult(SubSelectDesc desc);
}
