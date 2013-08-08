/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.client.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;

/**
 *
 * @author yuzhong.zhao
 * @version $Id: MileQueryResult.java, v 0.1 2011-9-13 ÏÂÎç01:38:53 yuzhong.zhao
 *          Exp $
 */
public class MileQueryResult extends MileSqlResult {

    private SqlResultSet sqlResultSet = null;

    private List<Map<String, Object>> queryResult = null;

    @Override
    public String toString() {
        return sqlResultSet.toString();
    }

    public MileQueryResult() {

    }

    public MileQueryResult(SqlResultSet sqlResultSet) {
        this.sqlResultSet = sqlResultSet;
        this.queryResult = new ArrayList<Map<String, Object>>();
        for (Record record : sqlResultSet.data) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < sqlResultSet.fields.size(); i++) {
                map.put(sqlResultSet.fields.get(i).aliseName, record.data.get(i));
            }
            queryResult.add(map);
        }
        this.setDocState(sqlResultSet.docState);
    }

    public void setQueryResult(List<Map<String, Object>> queryResult) {
        this.queryResult = queryResult;
    }

    public List<Map<String, Object>> getQueryResult() {
        return queryResult;
    }
    
    public SqlResultSet getSqlResultSet() {
        return sqlResultSet;
    }

    public void setSqlResultSet(SqlResultSet sqlResultSet) {
        this.sqlResultSet = sqlResultSet;
    }

}
