/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.util.List;

/**
 * Convert data (string) to Mile SQL (SQL string and parameter array)
 * @author bin.lb
 */
public interface DataToSql {
	public String convert(String data, List<Object> params) throws Exception;
}
