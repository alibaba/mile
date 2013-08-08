/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alipay.mile.FieldDesc;
import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: GetkvStatement.java, v 0.1 2012-5-15 下午21:54:27 yuzhong.zhao Exp $
 */
public class GetkvStatement extends Statement {
    /** 查询返回哪些字段 */
    public List<FieldDesc> selectFields     = new ArrayList<FieldDesc>();
    /** docid可以由用户直接指定查询 */
    public DocHint         dochint;
	
	/* (non-Javadoc)
	 * @see com.alipay.mile.server.query.Statement#writeToStream(java.io.DataOutput, java.util.Map)
	 */
	@Override
	public void writeToStream(DataOutput os,
			Map<Object, List<Object>> paramBindMap) throws IOException {
        //处理 tableName
        ByteConveror.writeString(os, tableName);
        //处理 selectColumns
        if (selectFields == null || selectFields.isEmpty()) {
            os.writeInt(0);
        } else {
            os.writeInt(selectFields.size());
            //处理 selectColumns
            for (FieldDesc field : selectFields) {
                field.writeToStream(os, paramBindMap);
            }
        }
        //处理 docIdList
        if (dochint == null) {
            os.writeInt(0);
        } else {
            os.writeInt(1);
            os.writeLong(dochint.docId);
        }

	}

}
