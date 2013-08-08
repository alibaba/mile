/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MergeLeadQueryMessage.java, v 0.1 2012-9-17 下午09:29:32 yuzhong.zhao Exp $
 */
public class MergeStartLeadQueryMessage extends AbstractMessage {
    /** 要将查询流量导入到哪里  */
    private List<String> mergeServers;

    public MergeStartLeadQueryMessage() {
        super();
        setType(MT_COMMON_START_LEAD_QUERY_M);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        byte[] data;
        
        os.writeShort(mergeServers.size());
        for (String server : mergeServers) {
            data = server.getBytes("utf-8");
            os.writeShort(data.length);
            os.write(data);
        }
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        short strlen;
        byte[] data;

        short size = is.readShort();
        this.mergeServers = new ArrayList<String>(size);
        for (short i = 0; i < size; i++) {
            strlen = is.readShort();
            data = new byte[strlen];
            is.readFully(data, 0, strlen);
            mergeServers.add(new String(data, 0, strlen, "utf-8"));
        }
    }


    /**
     * Setter method for property <tt>mergeServers</tt>.
     * 
     * @param mergeServers value to be assigned to property mergeServers
     */
    public void setMergeServers(List<String> mergeServers) {
        this.mergeServers = mergeServers;
    }

    /**
     * Getter method for property <tt>mergeServers</tt>.
     * 
     * @return property value of mergeServers
     */
    public List<String> getMergeServers() {
        return mergeServers;
    }

}
