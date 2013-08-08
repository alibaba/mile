/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Query by specify sub selects, and condition.
 *
 * @author bin.lb
 */
public class SpecifyQuery {
    private String                 table;                                          // table name
    private String                 condition;                                      // condition in SQL format
    private List<SubSelect>        subSelects   = new ArrayList<SubSelect>();      // sub
    // select
    private Map<String, SubSelect> subSelectMap = new HashMap<String, SubSelect>();

    public SpecifyQuery() {
    }

    public SpecifyQuery(String table) {
        this.table = table;
    }

    public void addQuery(String name, SubSelect subSelect) {
        this.subSelects.add(subSelect);
        subSelectMap.put(name, subSelect);
    }

    public SubSelect getQueryByName(String name) {
        return subSelectMap.get(name);
    }

    public List<SubSelect> getSubSelect() {
        return subSelects;
    }

    public void writeToStream(DataOutput os) throws IOException {
        // write table name
        byte[] data = table.getBytes("utf-8");
        os.writeShort(data.length);
        os.write(data);

        // write condition
        data = condition.getBytes("utf-8");
        os.writeShort(data.length);
        os.write(data);

        // write sub selects
        os.writeShort(subSelects.size());
        for (SubSelect s : subSelects) {
            os.writeByte(s.getType());
            s.writeRequestToStream(os);
        }
    }

    public void readFromStream(DataInput is) throws IOException {
        // read table
        int len = is.readShort();
        byte[] data = new byte[len];
        is.readFully(data, 0, len);
        table = new String(data, 0, len, "utf-8");

        // read condition
        len = is.readShort();
        data = new byte[len];
        is.readFully(data, 0, len);
        condition = new String(data, 0, len, "utf-8");

        // read sub selects
        int subSelectCount = is.readShort();
        for (int i = 0; i < subSelectCount; i++) {
            subSelects.add(SubSelectFactory.createFromRequestStream(is));
        }
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public List<SubSelect> getSubSelects() {
        return subSelects;
    }

    public void setSubSelects(List<SubSelect> subSelects) {
        this.subSelects = subSelects;
    }

    public Map<String, SubSelect> getSubSelectMap() {
        return subSelectMap;
    }

    public void setSubSelectMap(Map<String, SubSelect> subSelectMap) {
        this.subSelectMap = subSelectMap;
    }
}
