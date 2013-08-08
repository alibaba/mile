/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: Record.java,v 0.1 2011-5-16  02:53:05 yuzhong.zhao Exp $
 */
public class Record {

    public long         docid;

    public List<Object> data;

    public Record() {
        this.docid = 0;
        this.data = new ArrayList<Object>();
    }

    public void writeToStream(DataOutput os) throws IOException {
        os.writeLong(docid);
        os.writeInt(data.size());
        for (Object obj : data) {
            ByteConveror.outPutData(os, obj);
        }
    }

    public void readFromStream(DataInput is) throws IOException {
        docid = is.readLong();
        int size = is.readInt();
        for (int i = 0; i < size; i++) {
            Object obj = ByteConveror.getData(is);
            data.add(obj);
        }
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Record) {
            Record another = (Record) obj;
            return data.equals(another.data);
        } else {
            return false;
        }
    }

}
