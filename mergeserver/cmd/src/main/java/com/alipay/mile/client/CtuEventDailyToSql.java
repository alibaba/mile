/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Convert CTU_EVENT_DAILY data to Mile insert SQL
 * @author bin.lb
 */
public class CtuEventDailyToSql extends DBDataToSql {
	private static final Logger LOGGER = Logger.getLogger(DBDataToSql.class.getName());
	private final ObjectMapper mapper = new ObjectMapper();

	private int propertyColumnIndex = 0; // EVENT_PROPERTY index

	private Map<String, EnumDataParser> enumConvertor = new HashMap<String, EnumDataParser>();
	
	@SuppressWarnings("serial")
	public CtuEventDailyToSql(String separator, List<String> columnOrder, List<String> enumPropertyList) {
		super(separator);

		columnDef = new ArrayList<ColumnData>(){
			{
				add(new StringColumn("ID"));
				add(new StringColumn("USER_ID"));
				add(new StringColumn("OPERATOR_ID"));
				add(new StringColumn("EXTERNAL_ID"));
				add(new StringColumn("CLIENT_IP"));
				add(new StringColumn("CLIENT_MAC"));
				add(new StringColumn("CLIENT_ID"));
				add(new StringColumn("SERVER_ID"));
				add(new StringColumn("MODULE_ID"));
				add(new TimeColumn("GMT_OCCUR"));
				add(new StringColumn("EVENT_TYPE"));
				add(new StringColumn("EVENT_NAME"));
				add(new StringColumn("EVENT_MAIN_TARGET"));
				add(new StringColumn("EVENT_SUPP_TARGET"));
				add(new StringColumn("EVENT_PROPERTY"));
				add(new StringColumn("EVENT_MAIN_TARGET_TYPE"));
				add(new StringColumn("SESSION_ID"));
				add(new StringColumn("EVENT_MAIN_TARGET_TYPE"));
				add(new StringColumn("USER_CLIENT_ID"));
			}
		};

		calColumnIndex(columnOrder);

		// get property column data index
		for (int i = 0; i < columnDef.size(); i++) {
			if (StringUtils.equalsIgnoreCase(columnDef.get(i).getName(), "EVENT_PROPERTY")) {
				propertyColumnIndex = columnIndex[i];
			}
		}

		HashMap<String, EnumDataParser> specialEnumMap = new HashMap<String, EnumDataParser>() {
			{
				put("2", new DoubleDataParser());
				put("4", new DoubleDataParser());
				put("14", new DoubleDataParser());
				put("22", new DoubleDataParser());
				put("46", new DoubleDataParser());
				put("71", new DoubleDataParser());
				put("72", new DoubleDataParser());
				put("73", new DoubleDataParser());
				put("74", new DoubleDataParser());
				put("90", new DoubleDataParser());
				put("91", new DoubleDataParser());
				put("93", new DoubleDataParser());
				put("94", new DoubleDataParser());
				put("97", new DoubleDataParser());
				put("124", new DoubleDataParser());
				put("125", new DoubleDataParser());
				put("137", new DoubleDataParser());
				put("138", new DoubleDataParser());
				put("139", new DoubleDataParser());
				put("debitExpressFee", new DoubleDataParser());
				put("140", new DoubleDataParser());
				put("142", new DoubleDataParser());
				put("realAmount", new DoubleDataParser());

				put("56", new TimeDataParser("EEE MMM d HH:mm:ss Z yyyy"));
				put("133", new TimeDataParser("EEE MMM d HH:mm:ss Z yyyy"));
				put("57", new TimeDataParser("yyyyMMddHHmmss"));
				put("58", new TimeDataParser("yyyyMMddHHmmss"));
				put("85", new Time2DataParser("EEE MMM d HH:mm:ss zzz yyyy"));
				put("time", new TimeDataParser("yyyy-MM-dd HH:mm:ss"));
			}
		};

		for (String key: enumPropertyList) {
			if (specialEnumMap.containsKey(key)) {
				enumConvertor.put(key, specialEnumMap.get(key));
			}
			else {
				enumConvertor.put(key, new EnumDataParser()); // string type
			}
		}
	}

	@Override
	public String convert(String data, List<Object> params) throws Exception {
		String []columns = data.split(columnSep);
		StringBuffer sb = new StringBuffer("INSERT INTO CTU_EVENT_DAILY");
		commonConvert(sb, params, columns);

		// EVENT_PROPERTY
		String propertyStr = columns[propertyColumnIndex];
		if (propertyStr.isEmpty())
			return sb.toString();

		Properties properties = new Properties();
		try {
			properties = mapper.readValue(propertyStr, Properties.class);
		} catch (Exception e) {
			LOGGER.error("parse property failed, json [" + propertyStr + "]");
			throw e;
		}

		for (Object key: properties.keySet()) {
			getEnumMap(key, properties.get(key), sb, params);
		}
				
		return sb.toString();
	}

	public class EnumDataParser {
		public Object parse(String str) throws ParseException {
			return str;
		}
	}

	public class DoubleDataParser extends EnumDataParser {
		public Object parse(String str) throws ParseException {
			return Double.parseDouble(str);
		}
	}

	public class TimeDataParser extends EnumDataParser {
		private SimpleDateFormat sdf = null;
		public TimeDataParser(String fmt) {
			super();
			sdf = new SimpleDateFormat(fmt);
		}
		public Object parser(String str) throws ParseException {
			return sdf.parse(str).getTime();
		}
	}
	
	public class Time2DataParser extends EnumDataParser {
		private SimpleDateFormat sdf = null;
		public Time2DataParser(String fmt) {
			super();
			sdf = new SimpleDateFormat(fmt);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		}
		public Object parser(String str) throws ParseException {
			return sdf.parse(str).getTime();
		}
	}

	public void getEnumMap(Object key, Object value,
			StringBuffer sb, List<Object> params) throws ParseException {
		if (key == null || value == null)
			return;
		String k = key.toString();
		if (k == null)
			return;
		k = k.trim();

		if (enumConvertor.containsKey(k)) {
			sb.append(" ").append(k).append("=?");
			params.add(enumConvertor.get(k).parse(value.toString()));
		}
	}
}
