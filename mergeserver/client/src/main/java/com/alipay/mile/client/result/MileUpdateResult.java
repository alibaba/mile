package com.alipay.mile.client.result;

import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;

/**
 * 
 * @author sicong,shou
 * @version $Id: MileQueryResult.java, v 0.1 2011-9-15 TODO
 */
public class MileUpdateResult extends MileSqlResult {
    private int updateNum;

    public MileUpdateResult(SqlResultSet sqlResultSet) {
        this.setDocState(sqlResultSet.docState);
        for (Record record : sqlResultSet.data) {
            this.updateNum = (Integer) record.data.get(0);
            break;
        }
    }

    public MileUpdateResult() {
        this.updateNum = 0;
    }

    public void setUpdateNum(int updateNum) {
        this.updateNum = updateNum;
    }

    public int getUpdateNum() {
        return updateNum;
    }

}
