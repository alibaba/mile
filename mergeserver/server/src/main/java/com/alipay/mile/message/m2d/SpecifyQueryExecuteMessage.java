/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message.m2d;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.alipay.mile.message.AbstractMessage;
import com.alipay.mile.server.query.special.SpecifyQuery;

/**
 * 
 * @author bin.lb
 * 
 */
public class SpecifyQueryExecuteMessage extends AbstractMessage {
    private int          sessionID;   // session id
    private int          exeTimeout;  // execute timeout
    private SpecifyQuery specifyQuery; // specify query

    public SpecifyQueryExecuteMessage() {
        super();
        setType(MT_CM_SPEC_Q_SQL);
    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#writeToStream(java.io.DataOutput)
     */
    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        // 处理sessionID
        os.writeInt(sessionID);

        // 处理exeTimeout
        os.writeInt(exeTimeout);

        // write specify query
        specifyQuery.writeToStream(os);
    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#readFromStream(java.io.DataInput)
     */
    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        // 处理sessionID
        this.sessionID = is.readInt();

        // 处理exeTimeout
        this.exeTimeout = is.readInt();

        specifyQuery = new SpecifyQuery();
        // read specifyQuery
        specifyQuery.readFromStream(is);
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setExeTimeout(int exeTimeout) {
        this.exeTimeout = exeTimeout;
    }

    public int getExeTimeout() {
        return exeTimeout;
    }

    public void setSpecifyQuery(SpecifyQuery specifyQuery) {
        this.specifyQuery = specifyQuery;
    }

    public SpecifyQuery getSpecifyQuery() {
        return specifyQuery;
    }
}
