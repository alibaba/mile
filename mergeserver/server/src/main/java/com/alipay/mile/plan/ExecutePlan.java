/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 执行计划类，根据sql的执行计划进行执行，返回结果
 * @author yuzhong.zhao
 * @version $Id: Agent.java, v 0.1 2011-5-9 11:10:39 yuzhong.zhao Exp $
 */
public class ExecutePlan {

	//执行步骤的链表
    private List<ExecuteStep> executeSteps;
    //用于辅助变量绑定的map
    private Map<Object, List<Integer>> paramBindingMap;

    public ExecutePlan() {
        this.executeSteps = new ArrayList<ExecuteStep>();
        this.paramBindingMap = new HashMap<Object, List<Integer>>();
    }

    /**
     * 添加执行步骤
     * @param executeStep
     */
    public void addExecuteStep(ExecuteStep executeStep) {
        executeSteps.add(executeStep);
    }

    
    private Map<Object, List<Object>> bindParams(Object[] params){
    	Map<Object, List<Object>> paramMap = new HashMap<Object, List<Object>>();
    	
    	for(Entry<Object, List<Integer>> entry : paramBindingMap.entrySet()){
    		List<Integer> locList = entry.getValue();
    		List<Object> paramValueList = new ArrayList<Object>();
    		for(Integer i : locList){
    			paramValueList.add(params[i]);
    		}
    		paramMap.put(entry.getKey(), paramValueList);
    	}
    	return paramMap;
    }
    
    
    
    public void mergePlan(ExecutePlan executePlan){
    	List<ExecuteStep> subSteps = executePlan.getExecuteSteps();
    	Map<Object, List<Integer>> subParams = executePlan.getParamBindingMap();
    	executeSteps.addAll(subSteps);
    	paramBindingMap.putAll(subParams);
    }
    
    
    /**
     * 执行执行计划
     * @param timeOut					超时
     * @param params					参数列表
     * @return
     * @throws SqlExecuteException
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IllegalSqlException
     */
    public Object execute(int timeOut, Object[] params) throws SqlExecuteException, IOException, InterruptedException, ExecutionException, IllegalSqlException {
        Object result = null;
        Map<Object, List<Object>> paramMap = bindParams(params);
        
        //前一个执行步骤的输出就是后一个执行步骤的输入
        for (ExecuteStep executeStep : executeSteps) {
            result = executeStep.execute(result, paramMap, timeOut);
        }
        return result;
    }

    public List<ExecuteStep> getExecuteSteps() {
        return executeSteps;
    }

    public void setExecuteSteps(List<ExecuteStep> executeSteps) {
        this.executeSteps = executeSteps;
    }

	public void setParamBindingMap(Map<Object, List<Integer>> paramBindingMap) {
		this.paramBindingMap = paramBindingMap;
	}

	public Map<Object, List<Integer>> getParamBindingMap() {
		return paramBindingMap;
	}


}
