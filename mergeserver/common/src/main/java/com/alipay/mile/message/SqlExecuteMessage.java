/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author jin.qian
 * @version $Id: SqlExecuteMessage.java,v 0.1 2011-4-6 下午05:40:15 jin.qian Exp $
 */
public class SqlExecuteMessage extends AbstractMessage {
    /**sessionID  */
    private int    sessionID;
    /** exeTimeout */
    private int    exeTimeout;
    /**sqlCommand */
    private String sqlCommand;

    public SqlExecuteMessage() {
        super();
        setType(MT_CM_SQL);
    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#writeToStream(java.io.DataOutput)
     */
    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        //处理sessionID
        os.writeInt(sessionID);

        //处理exeTimeout
        os.writeInt(exeTimeout);

        //处理sqlCommand
        byte[] data = sqlCommand.getBytes("utf-8");
        os.writeShort(data.length);

        os.write(data);

    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#readFromStream(java.io.DataInput)
     */
    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        //处理sessionID
        this.sessionID = is.readInt();

        //处理exeTimeout
        this.exeTimeout = is.readInt();

        //处理sqlCommand
        short strlen = is.readShort();

        byte[] data = new byte[strlen];
        is.readFully(data, 0, strlen);
        String str = new String(data, 0, strlen, "utf-8");
        this.sqlCommand = str;
    }

    @Override
    protected void writeToString(StringBuffer sb) {
        super.writeToString(sb);
        sb.append("执行超时时间 [").append(exeTimeout).append("]执行超时时间 [").append(exeTimeout).append(
            "]sqlCommand[").append(sqlCommand).append("]");
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
}
