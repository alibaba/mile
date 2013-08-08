/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query.special;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alipay.mile.Expression;
import com.alipay.mile.server.query.Statement;
import com.alipay.mile.server.query.TimeHint;


/**
 * condition statement for specify query.
 * @author bin.lb
 *
 */
public class SpecifyQueryStatement extends Statement {
    /** table id*/
    public int                 tableId;
    /** segment时间过滤，可以缩小查询的segment数，提高性能 */
    public TimeHint            hint;
    /** 走Hash或BTree索引的条件 */
    public Expression          hashWhere;
    /** 走filter索引的条件 */
    public Expression          filterWhere;
    /**  sub select */
    public List<SubSelect>     subSelects;
    /** sub select desc */
    public List<SubSelectDesc> subSelectDesc = new ArrayList<SubSelectDesc>();
	
    @Override
	public void writeToStream(DataOutput os,
			Map<Object, List<Object>> paramBindMap) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
