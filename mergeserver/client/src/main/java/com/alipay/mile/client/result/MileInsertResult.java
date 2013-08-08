package com.alipay.mile.client.result;

import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;

public class MileInsertResult extends MileSqlResult {
    private long docId;

    public MileInsertResult() {
        this.docId = -1;
    }

    public MileInsertResult(SqlResultSet sqlResultSet) {
        this.setDocState(sqlResultSet.docState);
        for (Record record : sqlResultSet.data) {
            this.docId = record.docid;
            break;
        }
    }

    public void setDocId(long docId) {
        this.docId = docId;
    }

    public long getDocId() {
        return docId;
    }

}
