/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.message.m2d;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alipay.mile.message.AbstractMessage;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.server.query.DeleteStatement;
import com.alipay.mile.server.query.GetkvStatement;
import com.alipay.mile.server.query.InsertStatement;
import com.alipay.mile.server.query.QueryStatement;
import com.alipay.mile.server.query.Statement;
import com.alipay.mile.server.query.UpdateStatement;
import com.alipay.mile.server.query.ExportStatement;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: AccessStatementMessage.java, v 0.1 2012-5-15 下午03:10:36
 *          yuzhong.zhao Exp $
 */
public class AccessStatementMessage extends AbstractMessage {
	
	/** 要向docserver发送的sql语句 */
	private Statement stmt;
	
	/** 绑定变量的map */
	private Map<Object, List<Object>> paramBindMap;
	
	/** sql执行的超时时间 */
	private int timeOut;
	
	public AccessStatementMessage(Statement stmt, Map<Object, List<Object>> paramBindMap, int timeOut) throws IllegalSqlException{
		super();
		if(stmt instanceof InsertStatement){
			this.setType(MT_MD_EXE_INSERT);
		}else if(stmt instanceof DeleteStatement){
			this.setType(MT_MD_EXE_DELETE);
		}else if(stmt instanceof UpdateStatement){
			this.setType(MT_MD_EXE_UPDATE);
		}else if(stmt instanceof QueryStatement){
			this.setType(MT_MD_EXE_QUERY);
		}else if(stmt instanceof GetkvStatement){
			this.setType(MT_MD_EXE_GET_KVS);
		} else if (stmt instanceof ExportStatement) {
			this.setType(MT_MD_EXE_EXPORT);
			// check path type
			if (!(paramBindMap.get(((ExportStatement)stmt).path).get(0) instanceof String)) {
				throw new IllegalSqlException("path must be string type in export sql");
			}
		}else{
			throw new IllegalSqlException("无法进行识别编码的statement类型");
		}
		this.stmt = stmt;
		this.paramBindMap = paramBindMap;
		this.timeOut = timeOut;
	}

	
	
	@Override
    protected void writeToStream(DataOutput os) throws IOException {
        super.writeToStream(os);
        // 处理 timeout
        os.writeInt(timeOut);
        stmt.writeToStream(os, paramBindMap);
    }
	
	
	@Override
	public String toString(){
	    return stmt.toString();
	}
	
	
	/**
	 * @param paramBindMap the paramBindMap to set
	 */
	public void setParamBindMap(Map<Object, List<Object>> paramBindMap) {
		this.paramBindMap = paramBindMap;
	}

	/**
	 * @return the paramBindMap
	 */
	public Map<Object, List<Object>> getParamBindMap() {
		return paramBindMap;
	}

	/**
	 * @param timeOut the timeOut to set
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	/**
	 * @return the timeOut
	 */
	public int getTimeOut() {
		return timeOut;
	}



	/**
	 * @param stmt the stmt to set
	 */
	public void setStmt(Statement stmt) {
		this.stmt = stmt;
	}



	/**
	 * @return the stmt
	 */
	public Statement getStmt() {
		return stmt;
	}
}
