/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.IOException;



/**
 * 
 * @author bin.lb
 *
 */
public class AccessSpecifyQueryRsMessage extends AbstractMessage {
    /** nodeId, 表示这个结果报文来自哪一组 docserver */
    private int    nodeId;
    /** 记录数 */
    private int    affectRows;
    /** 值 */
    private byte[] values;

    public AccessSpecifyQueryRsMessage() {
        super();
        setType(MT_DM_SQ_RS);
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        //处理 resultRows
        this.affectRows = is.readInt();

        //处理 values
        int valuesLen = is.readInt();
        byte[] data = new byte[valuesLen];
        is.readFully(data, 0, valuesLen);
        this.values = data;
    }

    public void setAffectRows(int affectRows) {
        this.affectRows = affectRows;
    }

    public int getAffectRows() {
        return affectRows;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setValues(byte[] values) {
        this.values = values;
    }

    public byte[] getValues() {
        return values;
    }
}
