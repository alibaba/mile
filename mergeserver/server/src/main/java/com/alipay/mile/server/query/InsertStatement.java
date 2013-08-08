/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alipay.mile.util.ByteConveror;

/**
 * 插入描述
 * @author jin.qian
 * @version $Id: InsertStatement.java, v 0.1 2011-5-10 下午05:31:27 jin.qian Exp $
 */
public class InsertStatement extends Statement {

    /** 插入列信息描述 */
    public List<FieldValuePair> documentValue = new ArrayList<FieldValuePair>();

	@Override
	public void writeToStream(DataOutput os,
			Map<Object, List<Object>> paramBindMap) throws IOException {
        //处理 tableName
        ByteConveror.writeString(os, tableName);
        // 处理 fieldValues
        if (documentValue == null || documentValue.isEmpty()) {
            os.writeInt(0);
        } else {
            // 处理 fieldValues
            os.writeInt(documentValue.size());
            // 处理 fieldValues
            List<Object> paramList = paramBindMap.get(documentValue);
            for (int i = 0; i < documentValue.size(); i++) {
            	FieldValuePair fvPair = documentValue.get(i);
                ByteConveror.writeString(os, fvPair.field.fieldName);
                ByteConveror.outPutData(os, paramList.get(i));
            }
        }
	}

}
