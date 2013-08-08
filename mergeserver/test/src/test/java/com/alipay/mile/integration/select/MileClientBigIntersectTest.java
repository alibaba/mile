/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.integration.select;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;

/**
 * 
 * @author sicong.shou
 */
public class MileClientBigIntersectTest extends MileClientAbstract {
	private static final Logger LOGGER = Logger.getLogger(MileClientSubSelectTest.class.getName());
	
	private int timeOut = 5000;

	List<Long> docidsList = new ArrayList<Long>();

	private String bigStr()
	{
		int size = 1000000;
		byte[] x=new byte[size];
		
		for(int i=0;i<size;++i)
		{
			x[i]=65;
		}
		return new String(x);
	}
	@Override
	public void setUp() {
		LOGGER.setLevel(Level.INFO);
		try {
			super.setUp();
		} catch (UnsupportedEncodingException e) {
			fail();
		} 

		// insert records
		String sql = "insert into ssc a=? id=?";
		Object[] params = {bigStr(),"123"};

		
			try {
                MileInsertResult insertResult = applationClientImpl.preInsert(sql, params, timeOut);
                if(LOGGER.isInfoEnabled()){
                    LOGGER.info("docid: " + insertResult.getDocId());
                    System.err.println(insertResult.getDocId());
                }
			} catch (Exception e) {
				fail();
			}
		
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			fail();
		}
	}

	
	@Override
	public void tearDown() {
		try {
			// delete records
			String sql = "delete from ssc indexwhere a=  ?";
			String[] params = { bigStr() };
			applationClientImpl.preDelete(sql, params, timeOut);
			super.tearDown();
		} catch (Exception e) {
			fail();
		}
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void testPrepareSelect() {
		try {
			String sql = "select id,a from ssc indexwhere a = ? ";
			String[] params = { bigStr() };

			MileQueryResult queryResult = applationClientImpl.preQueryForList(sql, params,
					timeOut);
            List<Map<String, Object>> resultList = queryResult.getQueryResult();
			assertEquals(1, resultList.size());
			Map<String, Object> re=resultList.get(0);
			Iterator it = re.entrySet().iterator(); 
			while (it.hasNext()) { 
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next(); 
				Object key = entry.getKey(); 
				String value = (String) entry.getValue();
				System.err.println(key+"  =  "+value.length());
			} 
			
		} catch (Exception e) {
			fail();
		}
		System.err.println("select ok");
	}
	
	
}
