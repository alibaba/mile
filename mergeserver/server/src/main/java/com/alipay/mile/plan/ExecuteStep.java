/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * sql的执行步骤，每一步的输出就是下一步的输入
 * 
 * @author yuzhong.zhao
 * @version $Id: ExecuteStep.java,v 0.1 2011-5-15 06:53:25 yuzhong.zhao Exp $
 */

public interface ExecuteStep {

	/**
	 * 执行步骤
	 * 
	 * @param input
	 *            执行步骤的输入
	 * @param params
	 *            参数列表，用于处理预编译sql
	 * @param timeOut
	 *            SQL执行超时时长
	 * @return 执行步骤的输出
	 * @throws SqlExecuteException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IllegalSqlException
	 */
	Object execute(Object input, Map<Object, List<Object>> params, int timeOut)
			throws SqlExecuteException, IOException, InterruptedException,
			ExecutionException, IllegalSqlException;
}
