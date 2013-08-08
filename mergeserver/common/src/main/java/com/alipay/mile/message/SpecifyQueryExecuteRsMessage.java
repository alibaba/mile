/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;



/**
 * 
 * @author bin.lb
 *
 */
public class SpecifyQueryExecuteRsMessage extends AbstractMessage {
    // affect rows
    private int    affectRows;
    // value
    private byte[] values;

    public SpecifyQueryExecuteRsMessage() {
        super();
        setType(MT_MC_SPEC_SQL_RS);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);

        os.writeInt(affectRows);

        os.writeInt(values.length);
        if (values.length > 0) {
            os.write(values);
        }

    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);

        affectRows = is.readInt();

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

    public void setValues(byte[] values) {
        this.values = values;
    }

    public byte[] getValues() {
        return values;
    }
}
