package com.alipay.mile.log;

import java.util.ArrayList;
import java.util.List;

import com.alipay.mile.DocDigestData;

public class TopSqlData {
    private String              ip;
    private String              port;
    private long                messageId;
    private long                excTime;
    private long                rowCount;
    private String              sql;
    private Object[]            params;
    private int                 docCount;
    private List<DocDigestData> docDigestDatas;

    public TopSqlData(int docCount) {
        this.docCount = docCount;
        docDigestDatas = new ArrayList<DocDigestData>();
    }

    public String printClientDigestLog() {
        StringBuffer sb = new StringBuffer();
        sb.append(System.getProperty("HOST_NAME")).append(",");
        sb.append(ip).append(",");
        sb.append(messageId).append(",");
        sb.append(excTime).append(",");

        //需要把问号替换掉

        if (null != params && params.length > 0) {
            int i = 0;
            while (sql.indexOf('?') > 0) {
                sql = sql.replaceFirst("[?]", params[i].toString());
                i++;
            }
            sql = sql.replace(',', ' ');
            sb.append(sql).append(",");
        } else {
            sql = sql.replace(',', ' ');
            sb.append(sql).append(",");
        }
        for (int j = 1; j <= docCount; j++) {
            boolean isPrint = false;
            for (DocDigestData ddd : docDigestDatas) {
                if (ddd.getNodeId() == j) {
                    ddd.printClientDigestLog(sb);
                    isPrint = true;
                }
            }
            if (!isPrint) {
                sb.append("0,0");
            }
            if (j != docCount) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getExcTime() {
        return excTime;
    }

    public void setExcTime(long excTime) {
        this.excTime = excTime;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public List<DocDigestData> getDocDigestDatas() {
        return docDigestDatas;
    }

    public void setDocDigestDatas(List<DocDigestData> docDigestDatas) {
        this.docDigestDatas = docDigestDatas;
    }

    public long getRowCount() {
        return rowCount;
    }

    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    public int getDocCount() {
        return docCount;
    }

    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }

}
