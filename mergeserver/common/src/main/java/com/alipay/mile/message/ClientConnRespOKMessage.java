/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jin.qian
 * @version $Id: ClientConnRespOKMessage.java,v 0.1 2011-4-6 下午05:39:15 jin.qian Exp $
 */
public class ClientConnRespOKMessage extends AbstractMessage {

    /** sessionID  */
    private int                sessionID;
    /**服务端描述  */
    private String             serverDescription;
    /** 服务端参数 */
    private List<KeyValueData> serverproperties = new ArrayList<KeyValueData>();

    /**
     * 构造器
     */
    public ClientConnRespOKMessage() {
        super();
        setType(MT_MC_CONN_RS_OK);
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public String getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(String serverDescription) {
        this.serverDescription = serverDescription;
    }

    public List<KeyValueData> getServerproperties() {
        return serverproperties;
    }

    public void setServerproperties(List<KeyValueData> serverproperties) {
        this.serverproperties = serverproperties;
    }

    /** 
     * @see com.alipay.mile.message.AbstractMessage#writeToStream(java.io.DataOutput)
     */
    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        //处理 sessionID
        os.writeInt(sessionID);
        //处理 serverDescription
        byte[] data = serverDescription.getBytes("utf-8");
        os.writeShort(data.length);
        os.write(data);
        //处理 serverproperties
        if (serverproperties == null || serverproperties.isEmpty()) {
            os.writeInt(0);
        } else {
            os.writeInt(serverproperties.size());
            for (KeyValueData keyValueData : serverproperties) {
                keyValueData.writeToStream(os);
            }
        }
    }

    /** 
     * @see com.alipay.mile.message.AbstractMessage#readFromStream(java.io.DataInput)
     */
    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        //处理 sessionID
        this.sessionID = is.readInt();
        //处理 serverDescription
        short strlen = is.readShort();
        byte[] data = new byte[strlen];
        is.readFully(data, 0, strlen);
        String str = new String(data, 0, strlen, "utf-8");
        this.serverDescription = str;
        //处理 serverproperties
        int j = is.readInt();
        for (int i = 0; i < j; i++) {
            KeyValueData keyValueData = new KeyValueData();
            keyValueData.readFromStream(is);
            serverproperties.add(keyValueData);
        }
    }

}
