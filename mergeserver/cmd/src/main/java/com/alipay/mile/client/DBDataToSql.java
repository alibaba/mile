/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */

package com.alipay.mile.client;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alipay.mile.message.TypeCode;
import com.alipay.mile.util.ByteConveror;

/**
 * Convert DB exported text data (no column name only data separated separator) to Mile insert SQL
 * @author bin.lb
 */
 
public abstract class DBDataToSql implements DataToSql {
	private static final Logger LOGGER = Logger.getLogger(DBDataToSql.class.getName());

	// column separator
	protected String columnSep;
	protected List<ColumnData> columnDef; // column define
	protected int []columnIndex; // column order

	public abstract class ColumnData {
		private String name;
		public ColumnData(String name) { this.name = name;}
		public Object convert(String data) { return null; }
		public String getName() { return name; }
	}

	public class StringColumn extends ColumnData {
		public StringColumn(String name) { super(name);}
		
		@Override
		public Object convert(String data) {
			return ByteConveror.preString2value(TypeCode.TC_STRING, data);
		}
	}
	
	public class LongColumn extends ColumnData {
		public LongColumn(String name) { super(name);}
		
		@Override
		public Object convert(String data) {
			return ByteConveror.preString2value(TypeCode.TC_INT_64, data);
		}
	}

	public class TimeColumn extends ColumnData {
		public TimeColumn(String name) { super(name); }

		@Override
		public Object convert(String data) {
			Object obj = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				obj = sdf.parse(data).getTime();
			} catch (Exception e) {
				LOGGER.error("parse time " + data + " failed", e);
			}
			return obj;
		}
	}

	public DBDataToSql(String separator) {
		columnSep = separator;
	}

	protected void calColumnIndex(List<String> columnOrder) {
		columnIndex = new int[columnDef.size()];
		for (int i = 0; i < columnDef.size(); i++) {
			for (int j = 0; j < columnOrder.size(); j++) {
				if (StringUtils.equalsIgnoreCase(columnDef.get(i).getName(), columnOrder.get(j))) {
					columnIndex[i] = j;
					break;
				}
			}
		}
	}

	protected void commonConvert(StringBuffer sqlBuf, List<Object> params, String []columns) {
		for (int i = 0; i < columnDef.size(); i++) {
			ColumnData coldef = columnDef.get(i);
			String value = null;
			try {
				value = columns[columnIndex[i]];
			} catch (Exception e) {
				LOGGER.error(coldef + "'s value not found", e);
			}
			if (value == null || value.isEmpty())
				continue;
			params.add(coldef.convert(value));
			sqlBuf.append(" " + coldef.getName() + "=?");
		}
	}
}
