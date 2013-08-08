/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alipay.mile.FieldDesc;
import com.alipay.mile.mileexception.SqlExecuteException;
import com.alipay.mile.plan.GroupStep;

public class MaxFuncSelectDesc implements SubSelectDesc {
    // calculate column
    public FieldDesc       field        = new FieldDesc();
    public MaxFuncSelect   subSelect;
    public List<FieldDesc> selectFields = new ArrayList<FieldDesc>();
    public MaxFuncResult   docResult;                                // result from docserver

    @Override
    public void rewrite() throws SqlExecuteException {
        List<FieldDesc> allFields = new ArrayList<FieldDesc>(selectFields.size() + 1);
        if (StringUtils.isBlank(field.aliseName)) {
            field.aliseName = field.fieldName;
        }
        allFields.add(field);
        for (FieldDesc f : selectFields) {
            if (StringUtils.isBlank(f.aliseName)) {
                field.aliseName = field.fieldName;
            }
            allFields.add(f);
        }
    }

    @Override
    public void fromSubSelect(SubSelect subSelect) {
        MaxFuncSelect select = (MaxFuncSelect) subSelect;
        this.subSelect = select;
        field.fieldName = select.getColumn();
        for (String s : select.getSelectColumns()) {
            FieldDesc f = new FieldDesc();
            f.fieldName = s;
            selectFields.add(f);
        }
    }

    @Override
    public byte getType() {
        return subSelect.getType();
    }

    @Override
    public void writeToStream(DataOutput os) throws IOException {

        os.writeInt(selectFields.size());
        //		for(FieldDesc f : selectFields) {
        //			;//os.writeInt(f.columnId);
        //		}
    }

    @Override
    public void setDocResult(SubSelectResult result) {
        docResult = (MaxFuncResult) result;
    }

    @Override
    public void mergeDocResult(SubSelectResult result) throws SqlExecuteException {
        MaxFuncResult newResult = (MaxFuncResult) result;
        Object obj = GroupStep.max(newResult.value, docResult.value);
        if (obj == newResult.value) {
            docResult = newResult;
        }
    }

}
