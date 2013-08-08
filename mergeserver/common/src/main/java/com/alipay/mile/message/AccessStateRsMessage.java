/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: AccessStateRsMessage.java, v 0.1 2011-7-7 下午08:49:03 yuzhong.zhao Exp $
 */
public class AccessStateRsMessage extends AbstractMessage {
    //docserver的状态信息
    private Map<String, Object> states;

    /**
     * 构造器
     */
    public AccessStateRsMessage() {
        super();
        states = new HashMap<String, Object>();
        setType(MT_DM_STATE_RS);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        os.writeInt(1);
        Iterator<String> it = states.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            ByteConveror.outPutData(os, key);
            ByteConveror.outPutData(os, states.get(key));
        }
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        int stateNum;
        String stateName;
        Object value;
        stateNum = is.readInt();
        for (int i = 0; i < stateNum; i++) {
            stateName = ByteConveror.readString(is);
            value = ByteConveror.getData(is);
            states.put(stateName, value);
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(22);
        sb.append("AccessStateMessage: [");
        if (null != states) {
            sb.append(states);
        }
        sb.append("]");
        return sb.toString();
    }

    public Map<String, Object> getStates() {
        return states;
    }

    public void setStates(Map<String, Object> states) {
        this.states = states;
    }

}
