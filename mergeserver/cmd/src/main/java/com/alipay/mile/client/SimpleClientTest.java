/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.alipay.mile.Config;
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.client.result.MileUpdateResult;

/**
 * 
 * @author bin.lb
 */
public class SimpleClientTest {
	private static final Logger LOGGER = Logger
			.getLogger(SimpleClientTest.class.getName());
	
	private static final byte INSERT_SQL = 0;
	private static final byte QUERY_SQL = 1;
	private static final byte DELETE_SQL = 2;
	private static final byte UPDATE_SQL = 3;
	
	private ApplationClientImpl client;
	private int timeOut;
	
	SimpleClientTest(ApplationClientImpl client, int timeOut) {
		this.client = client;
		this.timeOut = timeOut;
	}
	
	private byte detectSqlType(String sql) {
		byte type = -1;
		int minIndex = -1;
		int index = sql.toLowerCase().indexOf("insert");
		if( index > minIndex ) {
			minIndex = index;
			type = INSERT_SQL;
		}
		index = sql.toLowerCase().indexOf("select");
		if( index > minIndex ) {
			minIndex = index;
			type = QUERY_SQL;
		}
		index = sql.toLowerCase().indexOf("delete");
		if( index > minIndex ) {
			minIndex = index;
			type = DELETE_SQL;
		}
		index = sql.toLowerCase().indexOf("update");
		if( index > minIndex ) {
			minIndex = index;
			type = UPDATE_SQL;
		}
		return type;
	}

	public void executeSql(String sql) {
		List<String> strParams = new ArrayList<String>();
		String newSql = SqlParamParser.parseStringParams(sql, strParams);
		Object params[] = SqlParamParser.stringsToObjects((String[])strParams.toArray(new String[0]));
		if(LOGGER.isInfoEnabled()) {
			LOGGER.info("execute SQL: " + sql);
			LOGGER.info("converted SQL: " + newSql + " | " + ArrayUtils.toString(params));
		}
		try {
		switch(detectSqlType(newSql)) {
		case INSERT_SQL:
		{
			MileInsertResult insertRs = client.preInsert(newSql, params, timeOut);
			if( LOGGER.isInfoEnabled() ) {
				LOGGER.info("DOCID: " + insertRs.getDocId());
			}
			break;
		}
		case QUERY_SQL:
		{
			MileQueryResult queryRs = client.preQueryForList(newSql, params, timeOut);
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info("ROW: " + queryRs.getQueryResult().size());
				for(Map<String, Object> map: queryRs.getQueryResult()) {
					if(LOGGER.isInfoEnabled()) {
						LOGGER.info(map);
					}
				}
			}
			break;
		}
		case DELETE_SQL:
		{
			MileDeleteResult deleteRs = client.preDelete(newSql, params, timeOut);
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info("DELETE ROW: " + deleteRs.getDeleteNum());
			}
			break;
		}
		case UPDATE_SQL:
		{
			MileUpdateResult updateRs = client.preUpdate(newSql, params, timeOut);
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info("UPDATE ROW: " + updateRs.getUpdateNum());
			}
			break;
		}
		default:
			LOGGER.error("unrecognited SQL: " + newSql);
		}
		}
		catch (Exception e) {
			LOGGER.error("execute SQL [" + sql + "]", e);
		}
	}
	

	public static void main(String[] args) {
		BasicConfigurator.configure();
		PropertyConfigurator.configure("etc" + File.separator + Config.CONFIG_DEFAULT_MILE_LOG_PROPERTIES);

		if(args.length == 0) {
			LOGGER.error("TODO");
			System.exit(1);
		}
		
		ApplationClientImpl appClient = new ApplationClientImpl();
		appClient.readProperties("etc" + File.separator + "mileCliClent.properties.prod");
		try {
			appClient.init();
		} catch (Exception e) {
			LOGGER.error("init failed", e);
		}

		SimpleClientTest client = new SimpleClientTest(appClient, 10 * 1000);

		for(String s : args) {
			client.executeSql(s);
		}
		System.exit(0);
	}
}
