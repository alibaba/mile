/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alipay.mile.Expression;
import com.alipay.mile.util.ByteConveror;

/**
 * 更新sql解析
 * @author jin.qian
 * @version $Id: UpdateStatement.java, v 0.1 2011-5-10 下午05:47:11 jin.qian Exp $
 */
public class UpdateStatement extends Statement {

    /**DocHint  */
    public DocHint        dochint;

    /** TimeHint */
    public TimeHint       hint;

    /**hashWhere  */
    public Expression     hashWhere;

    /**filterWhere  */
    public Expression     filterWhere;

    /**需更新的值 */
    public FieldValuePair updateValue;

    
	@Override
	public void writeToStream(DataOutput os,
			Map<Object, List<Object>> paramBindMap) throws IOException {
        //处理 tableName
        ByteConveror.writeString(os, tableName);

        // 处理 fieldValue
        ByteConveror.writeString(os, updateValue.field.fieldName);
        ByteConveror.outPutData(os, paramBindMap.get(updateValue).get(0));

        // 处理 TimeHint
        if (hint == null) {
            hint = new TimeHint();
        }
        os.writeInt(4);
        os.writeLong(hint.startCreateTime);
        os.writeLong(hint.endCreateTime);
        os.writeLong(hint.startUpdateTime);
        os.writeLong(hint.endUpdateTime);

        // 处理 indexWhere
        if (null == hashWhere) {
            os.writeInt(0);
        } else {
            os.writeInt(hashWhere.size);
            hashWhere.postWriteToStream(os, paramBindMap);
        }

        // 处理 filterWhere
        if (null == filterWhere) {
            os.writeInt(0);
        } else {
            os.writeInt(filterWhere.size);
            filterWhere.postWriteToStream(os, paramBindMap);
        }
		
	}

}
