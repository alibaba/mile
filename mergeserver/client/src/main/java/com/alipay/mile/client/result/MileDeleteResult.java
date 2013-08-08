package com.alipay.mile.client.result;

import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;

/**
 *
 * @author sicong,shou
 * @version $Id: MileQueryResult.java, v 0.1 2011-9-15 TODO
 */
public class MileDeleteResult extends MileSqlResult {
    private int deleteNum;

    public MileDeleteResult() {
        this.deleteNum = 0;
    }

    public MileDeleteResult(SqlResultSet sqlResultSet) {
        this.setDocState(sqlResultSet.docState);

        for (Record record : sqlResultSet.data) {
            this.deleteNum = (Integer) record.data.get(0);
            break;
        }
    }

    public void setDeleteNum(int deleteNum) {
        this.deleteNum = deleteNum;
    }

    public int getDeleteNum() {
        return deleteNum;
    }
}
