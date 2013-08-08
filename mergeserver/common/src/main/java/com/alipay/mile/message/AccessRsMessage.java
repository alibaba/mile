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
 * 
 * @author jin.qian
 * @version $Id: AccessRsMessage.java, v 0.1 2011-5-10 上午08:50:55 jin.qian Exp $
 */
public class AccessRsMessage extends AbstractMessage {

    /** nodeId, 表示这个结果报文来自哪一组docserver */
    private int                nodeId;
    /** 记录数 */
    private int                resultRows;
    /** 状态 */
    private List<KeyValueData> stat = new ArrayList<KeyValueData>();
    /** 值 */
    private byte[]             values;
    /** 执行时间   DigestLog*/
    private long               excTime;
    /** 结果码, 0表示成功，1表示超时，-1表示错误 */
    private int                 resultCode;
    

    public AccessRsMessage() {
        super();
        setType(MT_DM_RS);
    }

    @Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        os.writeInt(resultRows);
        // 处理 stat
        if (stat == null || stat.isEmpty()) {
            os.writeInt(0);
        } else {
            // 处理 stat
            os.writeInt(stat.size());
            // 处理 fieldValues
            for (KeyValueData keyValueData : stat) {
                keyValueData.writeToStream(os);
            }
        }
        if (values == null) {
            os.writeInt(0);
        } else {
            // 处理 values
            os.writeInt(values.length);
            os.write(values);
        }
    }

    @Override
    protected void readFromStream(DataInput is) throws IOException {
        super.readFromStream(is);
        //处理 resultRows
        this.resultRows = is.readInt();
        //处理 stat
        int arrayLength = is.readInt();
        for (int i = 0; i < arrayLength; i++) {
            KeyValueData keyValueData = new KeyValueData();
            keyValueData.readFromStream(is);
            this.stat.add(keyValueData);
        }
        //处理 values
        int valuesLen = is.readInt();
        byte[] data = new byte[valuesLen];
        is.readFully(data, 0, valuesLen);
        this.values = data;
    }

    public int getResultRows() {
        return resultRows;
    }

    public void setResultRows(int resultRows) {
        this.resultRows = resultRows;
    }

    public List<KeyValueData> getStat() {
        return stat;
    }

    public void setStat(List<KeyValueData> stat) {
        this.stat = stat;
    }

    public byte[] getValues() {
        return values;
    }

    public void setValues(byte[] values) {
        this.values = values;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodeId() {
        return nodeId;
    }

    public long getExcTime() {
        return excTime;
    }

    public void setExcTime(long excTime) {
        this.excTime = excTime;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

}
