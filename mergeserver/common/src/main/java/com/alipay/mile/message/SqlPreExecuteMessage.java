/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.alipay.mile.Constants;
import com.alipay.mile.util.ByteConveror;

/**
 * @author jin.qian
 * @version $Id: SqlExecuteMessage.java,v 0.1 2011-4-6 下午05:40:15 jin.qian Exp $
 */
public class SqlPreExecuteMessage extends AbstractMessage {
    /** sessionID */
    private int      sessionID;
    /** 执行时间 */
    private int      exeTimeout;
    /** sqlCommand */
    private String   sqlCommand;
    /** 参数 */
    private Object[] parameters;

    /**
     * 构造器
     */
    public SqlPreExecuteMessage() {
        super();
        setType(MT_CM_PRE_SQL);
    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#writeToStream(java.io.DataOutput)
     */
    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        // 处理sessionID
        os.writeInt(sessionID);

        // 处理exeTimeout
        os.writeInt(exeTimeout);
        // 处理sqlCommand
        byte[] data = sqlCommand.getBytes("utf-8");
        if (getVersion() == Constants.VERSION) {
            os.writeInt(data.length);
        } else {
            os.writeShort(data.length);
        }
        os.write(data);
        // 处理parameters
        if (parameters == null) {
            os.writeShort(0);
        } else {
            os.writeShort(parameters.length);
            for (int i = 0; i < parameters.length; i++) {
                ByteConveror.outPutData(os, parameters[i]);
            }
        }
    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#readFromStream(java.io.DataInput)
     */
    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        // 处理sessionID
        this.sessionID = is.readInt();
        // 处理exeTimeout
        this.exeTimeout = is.readInt();
        // 处理sqlCommand
        int strlen = 0;
        if (getVersion() == Constants.VERSION) {
            strlen = is.readInt();
        } else {
            strlen = is.readShort();
        }
        byte[] data = new byte[strlen];
        is.readFully(data, 0, strlen);
        String str = new String(data, 0, strlen, "utf-8");
        this.sqlCommand = str;
        // 处理parameters
        short paramslen = is.readShort();
        this.parameters = new Object[paramslen];
        for (int i = 0; i < paramslen; i++) {
            this.parameters[i] = ByteConveror.getData(is);
        }
    }

    @Override
    protected void writeToString(StringBuffer sb) {
        super.writeToString(sb);
        sb.append("执行超时时间 [").append(exeTimeout).append("]sqlCommand[").append(sqlCommand).append(
            "]parameters[");
        for (int i = 0; i < parameters.length; i++) {
            sb.append(parameters[i]).append(",");
        }
        sb.append("]");
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getExeTimeout() {
        return exeTimeout;
    }

    public void setExeTimeout(int exeTimeout) {
        this.exeTimeout = exeTimeout;
    }

    public String getSqlCommand() {
        return sqlCommand;
    }

    public void setSqlCommand(String sqlCommand) {
        this.sqlCommand = sqlCommand;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

}
