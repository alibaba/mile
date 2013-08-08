/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.alipay.mile.util.ByteConveror;

/**
 * @author jin.qian
 * @version $Id: KeyValueData.java,v 0.1 2011-4-6 下午05:39:45 jin.qian Exp $
 */
public class KeyValueData {

    /** 键值 */
    private String key;

    /**  值*/
    private Object value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 
     * @param os
     * @throws IOException
     */
    public void writeToStream(DataOutput os) throws IOException {
        //key 字符串编码
        byte[] data = key.getBytes("utf-8");
        os.writeShort(data.length);
        os.write(data);
        //值处理
        ByteConveror.outPutData(os, value);
    }

    /**
     * 
     * @param is
     * @throws IOException
     */
    public void readFromStream(DataInput is) throws IOException {
        //处理key
        short strlen = is.readShort();
        byte[] data = new byte[strlen];
        is.readFully(data, 0, strlen);
        String str = new String(data, 0, strlen, "utf-8");
        this.key = str;

        //处理值
        this.value = ByteConveror.getData(is);
    }
}
