/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert SQL to Mile SQL
 * @author bin.lb
 */
public class CommonSqltoSql implements DataToSql {

	@Override
	public String convert(String data, List<Object> params) {
		List<String> strParams = new ArrayList<String>();
		String newSql = SqlParamParser.parseStringParams(data , strParams);
		for (String p: strParams) {
			params.add(SqlParamParser.parseObject(p));
		}
		return newSql;
	}

}
