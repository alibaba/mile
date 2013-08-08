/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * sql结果集
 * 
 * @author yuzhong.zhao
 * @version $Id: SqlResultSet.java,v 0.1 2011-5-16 涓嬪崍03:01:38 yuzhong.zhao Exp
 *          $
 */
public class SqlResultSet {
    // 各个docserver具体的返回状况
    public List<DocDigestData> docState;
    // 结果集列
    public List<FieldDesc>     fields;
    // 结果集数据
    public Collection<Record>  data;

    public SqlResultSet() {
        this.docState = new ArrayList<DocDigestData>();
        this.fields = new ArrayList<FieldDesc>();
        this.data = new ArrayList<Record>();
    }

    
    /**
     * sql执行是否成功
     *
     * @return true表示执行成功，false表示失败
     */
    public boolean isSuccessful() {
        if (docState == null || docState.isEmpty()) {
            return false;
        }

        for (DocDigestData docDigestData : docState) {
            if (!docDigestData.isSuccess()) {
                return false;
            }
        }

        return true;
    }
    
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("\n");
        for (FieldDesc fieldDesc : fields) {
            sb.append("[" + fieldDesc.aliseName + "] ");
        }
        sb.append("\n");

        if (data == null) {
            sb.append(data);
        } else {
            for (Record record : data) {
                for (Object obj : record.data) {
                    sb.append(obj).append("  ");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public void writeToStream(DataOutput os) throws IOException {
        os.writeInt(docState.size());
        for (DocDigestData docDigestData : docState) {
            docDigestData.writeToStream(os);
        }
        os.writeInt(fields.size());
        for (FieldDesc fieldDesc : fields) {
            fieldDesc.commonPartToStream(os);
        }
        os.writeInt(data.size());
        for (Record record : data) {
            record.writeToStream(os);
        }
    }

    public void readFromStream(DataInput is) throws IOException {
        int size;

        // 解析docserver状态
        size = is.readInt();
        for (int i = 0; i < size; i++) {
            DocDigestData docDigestData = new DocDigestData();
            docDigestData.readFromStream(is);
            docState.add(docDigestData);
        }
        // 解析列名
        size = is.readInt();
        for (int i = 0; i < size; i++) {
            FieldDesc fieldDesc = new FieldDesc();
            fieldDesc.commonPartFromStream(is);
            fields.add(fieldDesc);
        }
        // 解析记录
        size = is.readInt();
        for (int i = 0; i < size; i++) {
            Record record = new Record();
            record.readFromStream(is);
            data.add(record);
        }
    }
}
