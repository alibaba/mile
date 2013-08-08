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

import com.alipay.mile.Constants;
import com.alipay.mile.DocDigestData;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.message.AccessRsMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.UpdateStatement;

/**
 * 
 * 向docserver发送更新指令
 * 
 * @author yuzhong.zhao
 * @version $Id: UpdateStep.java,v 0.1 2011-5-20 04:23:41 yuzhong.zhao Exp $
 */
public class UpdateStep implements ExecuteStep {
	// 更新语句
	private UpdateStatement updateStatement;
	// 消息处理器
	private MessageProcessor messageProcessor;
	// 列描述，记录更新记录数
	private FieldDesc updateNumColumn;

	public UpdateStep(UpdateStatement updateStatement,
			MessageProcessor messageProcessor) {
		this.updateStatement = updateStatement;
		this.messageProcessor = messageProcessor;
		this.updateNumColumn = new FieldDesc();
		this.updateNumColumn.aliseName = Constants.UPDATE_RETURN_COLUMN_NAME;
		this.updateNumColumn.fieldName = this.updateNumColumn.aliseName;
	}

	/**
	 * @param sessionId
	 *            session号
	 * @param input
	 *            输入
	 * @param params
	 *            动态绑定的参数列表
	 * @param timeOut
	 *            超时时间
	 * @return 封装后的sql结果集，记录更新记录数
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

		List<Message> rsMessageList = messageProcessor.updateMessage(
				updateStatement, paramBindMap, timeOut);
		SqlResultSet result = new SqlResultSet();
		int updateNum = 0;

		for (Message message : rsMessageList) {
			AccessRsMessage rsMessage = (AccessRsMessage) message;

			DocDigestData docDigestData = new DocDigestData();
			docDigestData.setNodeId(rsMessage.getNodeId());
			if (rsMessage.getValues() == null) {
				docDigestData.setSuccess(false);
			} else {
				docDigestData.setSuccess(true);
				DataInput inputStream = new DataInputStream(
						new ByteArrayInputStream(rsMessage.getValues()));
				updateNum += inputStream.readInt();
			}
			result.docState.add(docDigestData);
		}

		result.fields.add(updateNumColumn);
		Record record = new Record();
		record.data.add(updateNum);
		result.data.add(record);
		return result;
	}

	public UpdateStatement getUpdateStatement() {
		return updateStatement;
	}

	public void setUpdateStatement(UpdateStatement updateStatement) {
		this.updateStatement = updateStatement;
	}

	public MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(MessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	public FieldDesc getUpdateNumColumn() {
		return updateNumColumn;
	}

	public void setUpdateNumColumn(FieldDesc updateNumColumn) {
		this.updateNumColumn = updateNumColumn;
	}

}
