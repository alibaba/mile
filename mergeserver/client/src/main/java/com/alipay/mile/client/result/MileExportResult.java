/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client.result;

import java.util.Iterator;

import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;

/**
 * Mile export result.
 * @author bin.lb
 */
public class MileExportResult extends MileSqlResult {

	private Long exportNum = 0L;

	public MileExportResult() {}
	
	public MileExportResult(SqlResultSet result) {
		this.setDocState(result.docState);

		Iterator<Record> iter = result.data.iterator();
		if (iter.hasNext()) {
			setExportNum((Long) ((Record)iter.next()).data.get(0));
		}
				
	}

	public Long getExportNum() {
		return exportNum;
	}

	public void setExportNum(Long exportNum) {
		this.exportNum = exportNum;
	}
}
