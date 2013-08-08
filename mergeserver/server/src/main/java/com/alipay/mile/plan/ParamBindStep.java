/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * @author yuzhong.zhao
 * 
 */
public class ParamBindStep implements ExecuteStep {

	private Object bindingKey;
	
	
	public ParamBindStep(Object bindingKey){
		this.bindingKey = bindingKey;
	}
	
	/* (non-Javadoc)
	 * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object, java.util.Map, int)
	 */
	@Override
	public Object execute(Object input, Map<Object, List<Object>> params,
			int timeOut) throws SqlExecuteException, IOException,
			InterruptedException, ExecutionException, IllegalSqlException {
		
		List<Object> bindingValues = params.get(bindingKey);
		if(null == bindingValues){
			bindingValues = new ArrayList<Object>();
		}
		bindingValues.add(input);
		params.put(bindingKey, bindingValues);
		return null;
	}

	public void setBindingKey(Object bindingKey) {
		this.bindingKey = bindingKey;
	}

	public Object getBindingKey() {
		return bindingKey;
	}

}
