/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark;

import java.util.ArrayList;
import java.util.List;

import com.alipay.mile.benchmark.util.SqlParamEntry;
import com.alipay.mile.message.TypeCode;
import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: MileCmdParser.java, v 0.1 2012-11-8 ÏÂÎç07:42:46 yuzhong.zhao Exp $
 */
public class MileCmdParser {


    static public SqlParamEntry parseStringParams(String sql) {
        char[] input = sql.toCharArray();
        SqlParamEntry entry = new SqlParamEntry();
        StringBuilder newSql = new StringBuilder(input.length);
        List<String> params = new ArrayList<String>();

        char begin = '\0';
        StringBuilder param = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (begin != '\'' && begin != '"') {
                if (input[i] == '\'' || input[i] == '"') {
                    newSql.append('?');
                    begin = input[i];
                    param.delete(0, param.length());
                } else {
                    newSql.append(input[i]);
                }
            } else {
                if ('\\' == input[i]) {
                    if (i + 1 < input.length) {
                        param.append(input[++i]);
                    } else {
                        param.append(input[i]);
                        begin = '\0';
                        params.add(param.toString());
                    }
                    continue;
                }

                if (input[i] == begin) {
                    begin = '\0';
                    params.add(param.toString());
                } else {
                    param.append(input[i]);
                    if (i + 1 >= input.length) {
                        params.add(param.toString());
                    }
                }
            }
        }


        entry.setParams(MileCmdParser.stringsToObjects(params));
        entry.setSql(newSql.toString());
        return entry;
    }

    
    
    
    public static Object[] stringsToObjects(List<String> params) {
        Object[] newParams = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            int index = params.get(i).indexOf(':');
            if (-1 == index) {
                if ("null".equalsIgnoreCase(params.get(i)))
                    newParams[i] = null;
                else
                    newParams[i] = ByteConveror.preString2value(TypeCode.TC_STRING, params.get(i));
            } else {
                Byte tc = TypeCode.getTCByName(params.get(i).substring(0, index));
                if (null == tc)
                    newParams[i] = ByteConveror.preString2value(TypeCode.TC_STRING, params.get(i));
                else
                    newParams[i] = ByteConveror.preString2value(tc, params.get(i).substring(index + 1));
            }
        }
        return newParams;
    }

}
