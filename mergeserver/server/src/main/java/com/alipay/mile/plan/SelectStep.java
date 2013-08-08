/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.Constants;
import com.alipay.mile.DocDigestData;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.message.AccessRsMessage;
import com.alipay.mile.message.Message;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.server.query.QueryStatement;
import com.alipay.mile.util.ByteConveror;

/**
 * 选择步骤，从docserver获取相应的列
 * 
 * @author yuzhong.zhao
 * @version $Id: SelectStep.java,v 0.1 2011-5-15 06:54:46 yuzhong.zhao Exp $
 */
public class SelectStep implements ExecuteStep {
    // 查询语句
    private QueryStatement   queryStatement;
    // 消息处理器
    private MessageProcessor messageProcessor;

    public SelectStep(QueryStatement queryStatement, MessageProcessor messageProcessor) {
        this.queryStatement = queryStatement;
        this.messageProcessor = messageProcessor;
    }

    /**
     * 对docserver的返回结果包进行解析，构造查询结果集
     * 
     * @param rsMessage
     *            docserver的查询结果包
     * @param data
     *            构造的查询结果集
     * @throws IOException
     */
    private void parseResultMessage(AccessRsMessage rsMessage, Collection<Record> data)
                                                                                       throws IOException {

        DataInput input = new DataInputStream(new ByteArrayInputStream(rsMessage.getValues()));
        for (int i = 0; i < rsMessage.getResultRows(); i++) {

            Record record = new Record();
            record.docid = rsMessage.getNodeId();
            record.docid = (record.docid << 48) + input.readLong();
            record.data = new ArrayList<Object>(queryStatement.selectFields.size());

            for (int j = 0; j < queryStatement.selectFields.size(); j++) {
                record.data.add(ByteConveror.getData(input));
            }

            data.add(record);
        }
    }

    public Object execute(Object input, Map<Object, List<Object>> params, int timeOut)
                                                                                      throws SqlExecuteException,
                                                                                      IOException,
                                                                                      InterruptedException,
                                                                                      ExecutionException,
                                                                                      IllegalSqlException {
        SqlResultSet result = new SqlResultSet();
        result.fields = queryStatement.selectFields;
        if (queryStatement.accessType == Constants.QT_COMMON_DISTINCT
            || queryStatement.accessType == Constants.QT_COMMON_DISTINCT_COUNT) {
            result.data = new HashSet<Record>();
        }

        List<Message> rsMessageList = messageProcessor
            .queryMessage(queryStatement, params, timeOut);
        for (Message message : rsMessageList) {
            AccessRsMessage rsMessage = (AccessRsMessage) message;
            DocDigestData docDigestData = new DocDigestData();
            docDigestData.setNodeId(rsMessage.getNodeId());
            docDigestData.setRowCount(rsMessage.getResultRows());
            // 返回结果集完整情况判断
            if (rsMessage.getValues() == null) {
                docDigestData.setSuccess(false);
            } else {
                parseResultMessage(rsMessage, result.data);
                docDigestData.setSuccess(true);
            }
            result.docState.add(docDigestData);
        }

        return result;
    }

    public QueryStatement getQueryStatement() {
        return queryStatement;
    }

    public void setQueryStatement(QueryStatement queryStatement) {
        this.queryStatement = queryStatement;
    }

    public MessageProcessor getMessageProcessor() {
        return messageProcessor;
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

}
