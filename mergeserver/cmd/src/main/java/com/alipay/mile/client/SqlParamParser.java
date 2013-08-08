/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.util.List;

import com.alipay.mile.message.TypeCode;
import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author bin.lb
 */

public class SqlParamParser {
		public static String parseStringParams(String sql, List<String> params) {
		char[] input = sql.toCharArray();
		StringBuilder newSql = new StringBuilder(input.length);

		char begin = '\0';
		StringBuilder param = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			if (begin != '\'' && begin != '"') {
				if (input[i] == '\'' || input[i] == '"') {
					newSql.append('?');
					begin = input[i];
					param.delete(0, param.length());
				} else {
					newSql.append(input[i]);
				}
			} else {
				if ('\\' == input[i]) {
					if (i + 1 < input.length) {
						param.append(input[++i]);
					} else {
						param.append(input[i]);
						begin = '\0';
						params.add(param.toString());
					}
					continue;
				}

				if (input[i] == begin) {
					begin = '\0';
					params.add(param.toString());
				} else {
					param.append(input[i]);
					if (i + 1 >= input.length) {
						params.add(param.toString());
					}
				}
			}
		}

		return newSql.toString();
	}

	public static Object[] stringsToObjects(String[] params) {
		Object[] newParams = new Object[params.length];
		for (int i = 0; i < params.length; i++) {
			newParams[i] = parseObject(params[i]);
		}
		return newParams;
	}

	public static Object parseObject(String param) {
		Object obj = null;
		int index = param.indexOf(':');
		if (-1 == index) {
			if ("null".equalsIgnoreCase(param))
				obj = null;
			else
				obj = ByteConveror.preString2value(
					TypeCode.TC_STRING, param);
		} else {
			Byte tc = TypeCode.getTCByName(param.substring(0, index));
			if(null == tc)
				obj = ByteConveror.preString2value( TypeCode.TC_STRING, param);
			else
				obj = ByteConveror.preString2value( tc, param.substring(index + 1));
		}

		return obj;
	}
}