package com.alipay.mile.server.query.special;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alipay.mile.util.ByteConveror;

public class MaxFuncResult implements SubSelectResult {

    public Object       value;
    public List<Object> selectValues = new ArrayList<Object>();

    @Override
    public void readFromStream(DataInput is, SubSelectDesc subSelectDesc) throws IOException {
        MaxFuncSelectDesc desc = (MaxFuncSelectDesc) subSelectDesc;
        value = ByteConveror.getData(is);
        for (int i = 0; i < desc.selectFields.size(); i++) {
            selectValues.add(ByteConveror.getData(is));
        }
    }

}
