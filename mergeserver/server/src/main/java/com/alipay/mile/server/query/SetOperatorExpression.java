/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class SetOperatorExpression extends QueryStatementExpression {

    /** ¹ØÏµIntersectSet UnionSet */
    public byte operator;

    public byte getOperator() {
        return this.operator;
    }


    @Override
    public String toString() {
        return null;
    }



	@Override
	public void writeToStream(DataOutput os,
			Map<Object, List<Object>> paramBindMap) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
