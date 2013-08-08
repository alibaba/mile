/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.Constants;
import com.alipay.mile.DocDigestData;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.message.AccessRsMessage;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.InsertStatement;
import com.alipay.mile.util.ByteConveror;

/**
 * 
 * 插入步骤，向docserver发送插入sql
 * 
 * @author yuzhong.zhao
 * @version $Id: InsertStep.java,v 0.1 2011-5-20 04:24:06 yuzhong.zhao Exp $
 */
public class InsertStep implements ExecuteStep {

	/** 插入语句 */
	private InsertStatement insertStatement;

	/** 消息处理器 */
	private MessageProcessor messageProcessor;

	/** 插入语句会返回docid， docid列的列描述 */
	private FieldDesc docidColumn;

	public InsertStep(InsertStatement stmt, MessageProcessor messageProcessor) {
		this.insertStatement = stmt;
		this.messageProcessor = messageProcessor;
		this.docidColumn = new FieldDesc();
		this.docidColumn.fieldName = Constants.INSERT_RETURN_COLUMN_NAME;
		this.docidColumn.aliseName = this.docidColumn.fieldName;
	}

	/**
	 * @param input
	 *            输入
	 * @param params
	 *            参数
	 * @param timeOut
	 *            超时时间
	 * @return 封装后的sql结果集，记录插入记录的docid
	 * @throws SqlExecuteException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws IllegalSqlException 
	 * @throws Exception
	 * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object,
	 *      java.util.List, int)
	 */
	public Object execute(Object input, Map<Object, List<Object>> paramBindMap,
			int timeOut) throws SqlExecuteException, IOException,
			InterruptedException, ExecutionException, IllegalSqlException {
		// 向docserver发送插入指令
		AccessRsMessage rsMessage = (AccessRsMessage) messageProcessor
				.insertMessage(insertStatement, paramBindMap, timeOut);

		// 处理docserver的返回结果
		SqlResultSet result = new SqlResultSet();
		long docid = -1;
		DocDigestData docDigestData = new DocDigestData();
		docDigestData.setNodeId(rsMessage.getNodeId());
		docDigestData.setExcTime(rsMessage.getExcTime());
		if (rsMessage.getValues() == null) {
			docDigestData.setSuccess(false);
		} else {
			docDigestData.setSuccess(true);
			docid = rsMessage.getNodeId();
			docid = (docid << 48)
					+ ByteConveror.getLong(rsMessage.getValues(), 0, 8);
		}

		result.fields.add(docidColumn);
		Record record = new Record();
		record.docid = docid;
		result.data.add(record);
		result.docState.add(docDigestData);

		return result;
	}

	public InsertStatement getInsertStatement() {
		return insertStatement;
	}

	public void setInsertStatement(InsertStatement insertStatement) {
		this.insertStatement = insertStatement;
	}

	public MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public FieldDesc getDocidColumn() {
		return docidColumn;
	}

	public void setDocidColumn(FieldDesc docidColumn) {
		this.docidColumn = docidColumn;
	}

}
