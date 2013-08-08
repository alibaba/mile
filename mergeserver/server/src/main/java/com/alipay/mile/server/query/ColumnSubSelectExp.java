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
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.util.ByteConveror;

/**
 * sub select expression
 * 
 * @author jin.qian, bin.lb, yuzhong.zhao
 */
public class ColumnSubSelectExp extends Expression {
	/** column */
	public FieldDesc column;
	/** comparetor in */
	public byte comparetor;
	/** sub query statement */
	public QueryStatement subQueryStatement;

	public List<Object> getBindParams(Map<Object, List<Object>> params) {
		List<Object> paramList = new ArrayList<Object>();
		List<Object> values = params.get(this);

		for (Object value : values) {
			SqlResultSet resultSet = (SqlResultSet) value;
			for (Record record : resultSet.data) {
				paramList.add(record.data.get(0));
			}
		}
		return paramList;
	}

	@Override
	public void writeToStream(DataOutput os, Map<Object, List<Object>> params)
			throws IOException {
		os.writeByte(Constants.EXP_CONDITION_EXP);
		ByteConveror.writeString(os, column.fieldName);
		os.writeByte(comparetor);
		List<Object> values = getBindParams(params);
		os.writeInt(values.size());
		// 将参数列表写入流
		for (Object value : values) {
			ByteConveror.outPutData(os, value);
		}
	}

	@Override
	public String toString() {
		// TODO : to be continue
		return null;
	}

    public FieldDesc getColumn() {
        return column;
    }

    public void setColumn(FieldDesc column) {
        this.column = column;
    }

    public byte getComparetor() {
        return comparetor;
    }

    public void setComparetor(byte comparetor) {
        this.comparetor = comparetor;
    }

    public QueryStatement getSubQueryStatement() {
        return subQueryStatement;
    }

    public void setSubQueryStatement(QueryStatement subQueryStatement) {
        this.subQueryStatement = subQueryStatement;
    }
}
