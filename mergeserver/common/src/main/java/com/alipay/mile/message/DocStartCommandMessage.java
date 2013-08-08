/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.alipay.mile.Constants;

/**
 * @author jin.qian
 * @version $Id: ClientConnectMessage.java,v 0.1 2011-4-6 œ¬ŒÁ05:38:54 jin.qian
 *          Exp $
 */
public class DocStartCommandMessage extends AbstractMessage {

    /** √¸¡Ó */
    private String command;
    private String docServerIp;

    public DocStartCommandMessage(){
        super();
        setType(MT_COMMON_START_D);
    }
    
    public DocStartCommandMessage(String docServerIp) {
        super();
        this.docServerIp = docServerIp;
        this.command = Constants.DOCSERVER_START;
        setType(MT_COMMON_START_D);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        // ¥¶¿Ì√¸¡Ó
        byte[] commandData = command.getBytes("utf-8");
        os.writeShort(commandData.length);
        os.write(commandData);
        byte[] docServerIpData = docServerIp.getBytes("utf-8");
        os.writeShort(docServerIpData.length);
        os.write(docServerIpData);
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        // ∂¡»°√¸¡Ó
        short commandlen = is.readShort();
        byte[] commandData = new byte[commandlen];
        is.readFully(commandData, 0, commandlen);
        String commandstr = new String(commandData, 0, commandlen, "utf-8");
        this.command = commandstr;
        // ∂¡»°ip
        short docServerIplen = is.readShort();
        byte[] docServerIpData = new byte[docServerIplen];
        is.readFully(docServerIpData, 0, docServerIplen);
        String docServerIpstr = new String(docServerIpData, 0, docServerIplen, "utf-8");
        this.docServerIp = docServerIpstr;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDocServerIp() {
        return docServerIp;
    }

    public void setDocServerIp(String docServerIp) {
        this.docServerIp = docServerIp;
    }
}
