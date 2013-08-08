/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;

import com.alipay.mile.FieldDesc;
import com.alipay.mile.util.ByteConveror;

/**
 * ≈≈–Ú¡–√Ë ˆ
 * @author jin.qian
 * @version $Id: OrderDesc.java, v 0.1 2011-5-10 œ¬ŒÁ05:35:38 jin.qian Exp $
 */
public class OrderDesc {

    /** ¡–√Ë ˆ */
    public FieldDesc field;

    /** …˝–ÚΩµ–Ú */
    public byte      type; // DESC ASC

    public void writeToStream(DataOutput os) throws IOException {
        ByteConveror.writeString(os, field.fieldName);
        os.writeByte(type);
    }

}
