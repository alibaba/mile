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
 * @version $Id: ClientConnectMessage.java,v 0.1 2011-4-6 下午05:38:54 jin.qian
 *          Exp $
 */
public class MergerDocHeartcheck extends AbstractMessage {

    /** 命令 */
    private String mergerServerip;
    private String docServerIp;
    private short  state;
    private int    timeout;        //docServer需要  ，可不付值

    public MergerDocHeartcheck() {
        super();
        setType(MT_MD_HEART);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        // 处理命令
        os.writeInt(1000);
        os.writeShort(0);
        byte[] mergerServeripdata = mergerServerip.getBytes("utf-8");
        os.writeShort(mergerServeripdata.length);
        os.write(mergerServeripdata);
        byte[] docServerIpdata = docServerIp.getBytes("utf-8");
        os.writeShort(docServerIpdata.length);
        os.write(docServerIpdata);
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        timeout = is.readInt();
        state = is.readShort();
        short mergerServeriplen = is.readShort();
        byte[] mergerServeripdata = new byte[mergerServeriplen];
        is.readFully(mergerServeripdata, 0, mergerServeriplen);
        String mergerServeripstr = new String(mergerServeripdata, 0, mergerServeriplen, "utf-8");
        this.mergerServerip = mergerServeripstr;
        short docServerIplen = is.readShort();
        byte[] docServerIpdata = new byte[docServerIplen];
        is.readFully(docServerIpdata, 0, docServerIplen);
        String docServerIpstr = new String(docServerIpdata, 0, docServerIplen, "utf-8");
        this.docServerIp = docServerIpstr;
    }

    public String getMergerServerip() {
        return mergerServerip;
    }

    public void setMergerServerip(String mergerServerip) {
        this.mergerServerip = mergerServerip;
    }

    public String getDocServerIp() {
        return docServerIp;
    }

    public void setDocServerIp(String docServerIp) {
        this.docServerIp = docServerIp;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
