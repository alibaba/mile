/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 
 * 在查询的执行计划中有可能将一条sql拆分为多条sql进行查询，本模块负责将多条sql的查询结果进行整合
 * 
 * @author yuzhong.zhao
 * @version $Id: CombineStep.java, v 0.1 2012-6-28 下午03:57:31 yuzhong.zhao Exp $
 */
public class CombineStep implements ExecuteStep {

    // 要进行关联的列
    private List<FieldDesc> joinFields;
    // 选择列
    private List<FieldDesc> selectFields;
    // 关联列在选择列中的索引位置
    private List<Integer>           joinIndex;
    
    
    
    public CombineStep(List<FieldDesc> joinFields, List<FieldDesc> selectFields) throws SqlExecuteException{
        int i, j;
        this.joinFields = joinFields;
        this.selectFields = selectFields;
        
        this.joinIndex = new ArrayList<Integer>(joinFields.size());
        // 关联列编号
        for (i = 0; i < joinFields.size(); i++) {
            j = selectFields.indexOf(joinFields.get(i));
            if (j < 0) {
                throw new SqlExecuteException("在选择列中找不到关联列" + joinFields.get(i));
            }
            joinIndex.add(j);
        }
    }
    
    
    
    /** 
     * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object, java.util.Map, int)
     */
    @Override
    public Object execute(Object input, Map<Object, List<Object>> params, int timeOut)
                                                                                      throws SqlExecuteException,
                                                                                      IOException,
                                                                                      InterruptedException,
                                                                                      ExecutionException,
                                                                                      IllegalSqlException {
        
        
        SqlResultSet resultSet = new SqlResultSet();
        int i;
        List<Object> splitResultList = params.get(selectFields);
        Map<List<Object>, Object[]> map = new HashMap<List<Object>, Object[]>();
        
        for(Object obj : splitResultList){
            if(obj instanceof SqlResultSet){
                SqlResultSet splitResult = (SqlResultSet) obj;
                List<Integer> splitJoinIndex = new ArrayList<Integer>();
                Map<Integer, Integer> splitIndex = new HashMap<Integer, Integer>();
                
                // 获取关联列在结果集中的位置
                for(FieldDesc fieldDesc : joinFields){
                    if(splitResult.fields.contains(fieldDesc)){
                        splitJoinIndex.add(splitResult.fields.indexOf(fieldDesc));
                    }else{
                        throw new SqlExecuteException("在执行CombineStep时部分结果集中找不到关联列");
                    }
                }
                
                for(i = 0; i < splitResult.fields.size(); i++){
                    if(selectFields.contains(splitResult.fields.get(i))){
                        splitIndex.put(i, selectFields.indexOf(splitResult.fields.get(i)));
                    }
                }
                
                
                
                for(Record record : splitResult.data){
                    List<Object> key = new ArrayList<Object>();
                    for(i = 0; i < joinFields.size(); i++){
                        key.add(record.data.get(splitJoinIndex.get(i)));
                    }
                    
                    Object[] value = map.get(key);
                    if(null == value){
                        value = new Object[selectFields.size()];
                        for(i = 0; i < selectFields.size(); i++){
                            value[i] = null;
                        }
                        for(i = 0; i < key.size(); i++){
                            value[joinIndex.get(i)] = key.get(i);
                        }
                        map.put(key, value);
                    }
                    for(Entry<Integer, Integer> entry : splitIndex.entrySet()){
                        value[entry.getValue()] = record.data.get(entry.getKey()); 
                    }
                }
                
                // 添加docstat信息
                resultSet.docState.addAll(splitResult.docState);
            }else{
                throw new SqlExecuteException("在执行CombineStep时部分结果集不为SqlResultSet类型");
            }
        }
        
        
        // 向最终结果集中添加记录
        resultSet.fields = selectFields;
        for(Object[] value : map.values()){
            Record record = new Record();
            record.docid = 0;
            record.data = Arrays.asList(value);
            resultSet.data.add(record);
        }
        
        return resultSet;
    }

    
    
    public List<FieldDesc> getJoinFields() {
        return joinFields;
    }

    public void setJoinFields(List<FieldDesc> joinFields) {
        this.joinFields = joinFields;
    }

    
    public List<FieldDesc> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<FieldDesc> selectFields) {
        this.selectFields = selectFields;
    }

}
