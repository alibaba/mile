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
 * @version $Id: ClientConnectMessage.java,v 0.1 2011-4-6 下午05:38:54 jin.qian
 *          Exp $
 */
public class MergeStopCommandMessage extends AbstractMessage {

    /** 命令 */
    private String command;

    public MergeStopCommandMessage() {
        super();
        this.command = Constants.MERGESERVER_STOP;
        setType(MT_COMMON_STOP_M);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        // 处理命令
        byte[] data = command.getBytes("utf-8");
        os.writeShort(data.length);
        os.write(data);
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        // 读取用户名
        short strlen = is.readShort();
        byte[] data = new byte[strlen];
        is.readFully(data, 0, strlen);
        String str = new String(data, 0, strlen, "utf-8");
        this.command = str;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
