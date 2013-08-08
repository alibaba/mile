/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.mile.util.ByteConveror;

/**
 * @author bin.lb
 */
public class MaxFuncSelect extends AbstractSubSelect {

    // request
    private String       column;
    private List<String> selectColumns   = new ArrayList<String>();

    // result
    private byte         dataType;
    private Object       value;
    private List<Byte>   selectDataTypes = new ArrayList<Byte>();
    private List<Object> selectValues    = new ArrayList<Object>();

    public MaxFuncSelect() {
        super(SUB_SELECT_TYPE_MAX_FUNC);
    }

    public MaxFuncSelect(String column) {
        super(SUB_SELECT_TYPE_MAX_FUNC);
        this.column = column;
    }

    public void addSelectColumn(String column) {
        selectColumns.add(column);
    }

    @Override
    public void writeRequestToStream(DataOutput os) throws IOException {

        // write column
        byte[] data = column.getBytes("utf-8");
        os.writeShort(data.length);
        os.write(data);

        // write selectColumns
        os.writeShort(selectColumns.size());
        for (String s : selectColumns) {
            data = s.getBytes("utf-8");
            os.writeShort(data.length);
            os.write(data);
        }
    }

    @Override
    public void readRequestFromStream(DataInput is) throws IOException {
        // read column
        int len = is.readShort();
        byte[] data = new byte[len];
        is.readFully(data, 0, len);
        column = new String(data, 0, len, "utf-8");

        // read selectColumns
        int selectColumnCount = is.readShort();
        for (int i = 0; i < selectColumnCount; i++) {
            len = is.readShort();
            data = new byte[len];
            is.readFully(data, 0, len);
            selectColumns.add(new String(data, 0, len, "utf-8"));
        }
    }

    @Override
    public void writeResultToStream(DataOutput os) throws IOException {
        // write max value
        os.writeByte(dataType);
        ByteConveror.outPutData(os, value);

        // write select values
        for (int i = 0; i < selectDataTypes.size(); i++) {
            os.writeByte(selectDataTypes.get(i));
            ByteConveror.outPutData(os, selectValues.get(i));
        }
    }

    @Override
    public void readResultFromStream(DataInput is) throws IOException {
        // read max value
        byte dataType = is.readByte();
        this.dataType = dataType;
        value = ByteConveror.getData(is);
        // read select values
        for (int i = 0; i < selectColumns.size(); i++) {
            dataType = is.readByte();
            selectDataTypes.add(dataType);
            selectValues.add(ByteConveror.getData(is));
        }
    }

    @Override
    public void setResult(SubSelectDesc subSelectDesc) {
        MaxFuncSelectDesc desc = (MaxFuncSelectDesc) subSelectDesc;
        value = desc.docResult.value;

        for (int i = 0; i < desc.selectFields.size(); i++) {
            selectValues.add(desc.docResult.selectValues.get(i));
        }
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public Object getValue() {
        return value;
    }

    public List<Object> getSelectValues() {
        return selectValues;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public List<Byte> getSelectDataTypes() {
        return selectDataTypes;
    }

    public void setSelectDataTypes(List<Byte> selectDataTypes) {
        this.selectDataTypes = selectDataTypes;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setSelectValues(List<Object> selectValues) {
        this.selectValues = selectValues;
    }
}
