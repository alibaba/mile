/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.message.m2d;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import com.alipay.mile.Constants;
import com.alipay.mile.message.AbstractMessage;

/**
 * 向docserver发送消息，获取docserver的当前状态
 * 
 * @author yuzhong.zhao
 * @version $Id: AccessStateMessage.java, v 0.1 2011-7-7 下午03:15:41 yuzhong.zhao
 *          Exp $
 */
public class AccessStateMessage extends AbstractMessage {
    /** 超时时间 */
    private int          timeout = Constants.GET_STATE_TIME_OUT;

    private List<String> states;

    /**
     * 构造器
     */
    public AccessStateMessage() {
        super();
        setType(MT_MD_GET_STATE);
    }

    /**
     * @see com.alipay.mile.message.AbstractMessage#writeToStream(java.io.DataOutput)
     */
    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        os.writeInt(timeout);
        if (null != states) {
            os.writeInt(states.size());
            for (String state : states) {
                os.writeInt(state.length());
                os.writeBytes(state);
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(23);
        sb.append("AccessStateMessage: [");
        if (null != states) {
            for (int i = 0; i < states.size() - 1; i++) {
                sb.append(states.get(i)).append(",");
            }
            sb.append(states.get(states.size() - 1));
        }
        sb.append("]");
        return sb.toString();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }
}
