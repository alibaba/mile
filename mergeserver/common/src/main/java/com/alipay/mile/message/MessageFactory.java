/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.alipay.mile.util.ByteConveror;

/**
 * @author jin.qian
 * @version $Id: MessageFactory.java,v 0.1 2011-4-6 下午05:37:22 jin.qian Exp $
 */
public class MessageFactory {

    /**
     * @param args
     * @throws IOException
     */
    private static final Logger LOGGER = Logger.getLogger(MessageFactory.class.getName());

    /**
     * 取得消息id
     *
     * @param data
     * @return
     */
    public static int getMessageId(byte[] data) {
        return ByteConveror.getInt(data, 8, 12);
    }

    /**
     * 取得消息类型
     *
     * @param data
     * @return
     */
    public static short getMessageType(byte[] data) {
        return ByteConveror.getShort(data, 6, 8);
    }

    /**
     * @param data
     * @return 消息解码 转换成消息对象
     */
    public static Message toMessage(byte[] data) {
        try {
            Short messageType = getMessageType(data);
            if (messageType == Message.MT_CM_CONN) {
                ClientConnectMessage message = new ClientConnectMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_CM_RE_CONN) {
                ClientReConnectMessage message = new ClientReConnectMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_CM_SQL || messageType == Message.MT_CM_Q_SQL) {
                SqlExecuteMessage message = new SqlExecuteMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_CM_PRE_SQL
                       || messageType == Message.MT_CM_PRE_Q_SQL) {
                SqlPreExecuteMessage message = new SqlPreExecuteMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_MD_HEART) {
                MergerDocHeartcheck message = new MergerDocHeartcheck();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_STOP_M) {
                MergeStopCommandMessage message = new MergeStopCommandMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_START_M) {
                MergeStartCommandMessage message = new MergeStartCommandMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_STOP_D) {
                DocStopCommandMessage message = new DocStopCommandMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_START_D) {
                DocStartCommandMessage message = new DocStartCommandMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_MC_CONN_RS_OK) {
                ClientConnRespOKMessage message = new ClientConnRespOKMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_MC_CONN_RS_ERR) {
                ClientConnRespError message = new ClientConnRespError();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_MC_SQL_RS) {
                SqlExecuteRsMessage message = new SqlExecuteRsMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_MC_SPEC_SQL_RS) {
                SpecifyQueryExecuteRsMessage message = new SpecifyQueryExecuteRsMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_MC_SQL_EXC_ERR) {
                SqlExectueErrorMessage message = new SqlExectueErrorMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_DM_RS) {
                AccessRsMessage message = new AccessRsMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_DM_SQ_RS) {
                AccessSpecifyQueryRsMessage message = new AccessSpecifyQueryRsMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_DM_SQL_EXC_ERROR) {
                AccessSqlExeErrorMessage message = new AccessSqlExeErrorMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_DM_STATE_RS) {
                AccessStateRsMessage message = new AccessStateRsMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_OK) {
                CommonOkMessage message = new CommonOkMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_ERROR) {
                CommonErrorMessage message = new CommonErrorMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_START_LEAD_QUERY_M) {
                MergeStartLeadQueryMessage message = new MergeStartLeadQueryMessage();
                message.fromBytes(data);
                return message;
            } else if (messageType == Message.MT_COMMON_STOP_LEAD_QUERY_M) {
                MergeStopLeadQueryMessage message = new MergeStopLeadQueryMessage();
                message.fromBytes(data);
                return message;
            }
        } catch (IOException e) {
            LOGGER.error("message Decode error! ", e);
        }
        return null;
    }

    /**
     * 消息编码
     *
     * @param message
     * @return
     * @throws IOException
     */
    public static byte[] toSendMessage(Message message) throws IOException {
        return message.toBytes();
    }

}
