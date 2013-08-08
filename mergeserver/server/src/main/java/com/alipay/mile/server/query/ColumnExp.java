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

import com.alipay.mile.Constants;
import com.alipay.mile.Expression;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.util.ByteConveror;

/**
 * 后缀表达式描述 where列 用于存储 优先数
 *
 * @author huabing.du
 * @version $Id: ColumnExp.java, v 0.1 2011-5-10 下午03:34:44 huabing.du Exp $
 */
public class ColumnExp extends Expression {

    /** 列 */
    public FieldDesc       column;
    /** 值域 =, >, >=, <=, < Between In*/
    public byte            comparetor;
    /** 值 */
    public List<ValueDesc> values;

    public ColumnExp() {
        super();
        values = new ArrayList<ValueDesc>();
    }

    public FieldDesc getField() {
        return this.column;
    }

    public byte getComparetor() {
        return this.comparetor;
    }

    public List<ValueDesc> getValue() {
        return this.values;
    }

	public List<Object> getBindParams(Map<Object, List<Object>> params) {
		return params.get(this);
	}
    
    
    @Override
    public void writeToStream(DataOutput os, Map<Object, List<Object>> params)
                                                                                                     throws IOException {
        os.writeByte(Constants.EXP_CONDITION_EXP);
        ByteConveror.writeString(os, column.fieldName);
        os.writeByte(this.comparetor);
        os.writeInt(values.size());
        for(Object obj : getBindParams(params)){
        	ByteConveror.outPutData(os, obj);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(22);
        sb.append("表达式: [列");
        sb.append(column.toString());
        sb.append(", 比较子: ");
        sb.append(comparetor);
        sb.append(", 值: ");
        for (ValueDesc valueDesc : values) {
            sb.append(valueDesc.toString()).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
