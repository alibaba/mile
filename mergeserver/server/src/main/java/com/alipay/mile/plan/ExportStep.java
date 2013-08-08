/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.message.AccessRsMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.DocDigestData;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.server.query.ExportStatement;
import com.alipay.mile.Constants;

/**
 * Export step, return epxort record number.
 * @author bin.lb
 */
public class ExportStep implements ExecuteStep {

	private ExportStatement exportStatement;
	private MessageProcessor messageProcessor;

	public ExportStep(ExportStatement stmt, MessageProcessor processor) {
		this.exportStatement = stmt;
		this.messageProcessor = processor;
	}

	/**
	 * Execute export step.
	 */
	@Override
	public Object execute(Object input, Map<Object, List<Object>> params,
			int timeOut) throws SqlExecuteException, IOException,
			InterruptedException, ExecutionException, IllegalSqlException {

		// get result from docserver
		List<Message> rsMessageList = messageProcessor.exportMessage(exportStatement, params, timeOut);

		SqlResultSet result = new SqlResultSet();
		Long exportNum = 0L;

		// merge result
		for (Message msg : rsMessageList) {
			AccessRsMessage rsMsg = (AccessRsMessage) msg;

			DocDigestData docDigestData = new DocDigestData();
			docDigestData.setExcTime(rsMsg.getExcTime());
			docDigestData.setRowCount(rsMsg.getResultRows());
			docDigestData.setNodeId(rsMsg.getNodeId());

			if (rsMsg.getValues() == null) {
				docDigestData.setSuccess(false);
			} else {
				docDigestData.setSuccess(true);
				DataInput inputStream = new DataInputStream(new ByteArrayInputStream(rsMsg.getValues()));

				// parse result
				exportNum += inputStream.readLong();
			}

			result.docState.add(docDigestData);
		}
		// add to result set
		FieldDesc field = new FieldDesc();
		field.fieldName = Constants.EXPORT_RETURN_COLUMN_NAME;
		field.aliseName = field.fieldName;
		
		result.fields.add(field);
		Record rec = new Record();
		rec.data.add(exportNum);
		result.data.add(rec);
		return result;
	}
}
