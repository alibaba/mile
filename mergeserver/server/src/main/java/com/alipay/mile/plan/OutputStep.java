/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 输出步骤，把查询结果集转换成给client的输出结构
 * 
 * @author yuzhong.zhao
 * @version $Id: OutputStep.java,v 0.1 2011-5-19 04:37:37 yuzhong.zhao Exp $
 */
public class OutputStep implements ExecuteStep {
	// 选择列
	private List<FieldDesc> selectFields;

	// 选择列在结果列中的位置
	private List<Integer> selectIndex;
	
	// 结果集的offset
	private int offset;

	// 对结果集大小的限制
	private int limit;

	public OutputStep(List<FieldDesc> selectFields, List<FieldDesc> resultFields, int offset, int limit) throws IllegalSqlException {
		this.offset = offset;
		this.limit = limit;
		this.selectFields = selectFields;
		this.selectIndex = new ArrayList<Integer>();
		
		if(offset > limit){
		    throw new IllegalSqlException("在查询中offset应该小于limit!");
		}
		
		for (FieldDesc fieldDesc : selectFields) {
			int index = resultFields.indexOf(fieldDesc);
			if (index < 0) {
				throw new IllegalSqlException("在结果集中找不到列" + fieldDesc);
			} else {
				selectIndex.add(index);
			}
		}
		
	}

	/**
	 * @param input
	 *            输出步的输入为SqlResultSet
	 * @param params
	 *            预编译sql的参数列表
	 * @return 输出步的输出为ResultSet
	 * @throws SqlExecuteException
	 * @throws Exception
	 * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object,
	 *      java.util.List, int)
	 */
	public Object execute(Object input, Map<Object, List<Object>> paramBindMap,
			int timeOut) throws SqlExecuteException {
		// 检查输入参数
		if (null == input) {
			throw new SqlExecuteException("在执行output step时输入为空");
		}
		if (!(input instanceof SqlResultSet)) {
			throw new SqlExecuteException("在执行Output step时输入不为SqlResultSet类型");
		}
		SqlResultSet resultSet = (SqlResultSet) input;

		// 在mergeserver进行查询的过程中，会查询一些多余的列，从mergeserver的查询结果中找出实际选择的列
		SqlResultSet output = new SqlResultSet();
		output.fields = selectFields;
		output.docState = resultSet.docState;		

		// 构造输出结果集，首先判断结果集大小是否够offset，如果不够返回空记录集合
		if (resultSet.data.size() < offset) {
			return output;
		} else {
			int up = limit < resultSet.data.size() ? limit : resultSet.data
					.size();
			if (resultSet.data instanceof List<?>) {
				// 如果结果集是List结构的，那么可以取offset
				List<Record> data = (List<Record>) resultSet.data;
				for (int i = offset; i < up; i++) {
					Record record = data.get(i);
					Record retRecord = new Record();
					retRecord.docid = record.docid;
					retRecord.data = new ArrayList<Object>();
					for (int j = 0; j < selectFields.size(); j++) {
						retRecord.data.add(record.data.get(selectIndex.get(j)));
					}
					output.data.add(retRecord);
				}
			} else {
				// 如果结果集不是List结构的，根据up和offset取前up-offset条记录
				int i = up - offset;
				for (Record record : resultSet.data) {
					if (i <= 0) {
						break;
					}
					for (int j = 0; j < selectFields.size(); j++) {
						Record retRecord = new Record();
						retRecord.docid = record.docid;
						retRecord.data = new ArrayList<Object>();
						retRecord.data.add(record.data.get(selectIndex.get(j)));
						output.data.add(retRecord);
					}
					i--;
				}
			}

		}

		return output;
	}

}
