/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */

package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alipay.mile.Expression;
import com.alipay.mile.util.ByteConveror;

/**
 * @author bin.lb
 * Statement for export sql:
 *     export to path from table [indexwhere ...] [where ...] [limit ...]
 */
public class ExportStatement extends Statement {

 	public ValueDesc path; // result file save path
	public TimeHint hint;
	public Expression hashWhere;
	public Expression filterWhere;
	public long limit = -1; // default -1 for unlimited
	
	@Override
	public void writeToStream(DataOutput os,
			Map<Object, List<Object>> paramBindMap) throws IOException {
		
		ByteConveror.writeString(os, tableName);
		// convert to String is safe here, already checked in AccessStatementMessage().
		ByteConveror.writeString(os, (String)paramBindMap.get(path).get(0));

		// time hint
		TimeHint h = hint;
		if (h == null) {
			h = new TimeHint();
		}

		os.writeInt(4);
		os.writeLong(h.startCreateTime);
		os.writeLong(h.endCreateTime);
		os.writeLong(h.startUpdateTime);
		os.writeLong(h.endUpdateTime);

		// indexwhere
		if (hashWhere == null) {
			os.writeInt(0);
		} else {
			os.writeInt(hashWhere.size);
			hashWhere.postWriteToStream(os, paramBindMap);
		}

		// where
		if (filterWhere == null) {
			os.writeInt(0);
		} else {
			os.writeInt(filterWhere.size);
			filterWhere.postWriteToStream(os, paramBindMap);
		}

		os.writeLong(limit);
	}
}
