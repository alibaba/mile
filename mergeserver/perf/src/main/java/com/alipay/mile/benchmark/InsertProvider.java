/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import com.alipay.mile.benchmark.util.Props;
import com.alipay.mile.benchmark.util.SqlParamEntry;
import com.alipay.mile.client.ApplationClientImpl;
import com.alipay.mile.client.result.MileInsertResult;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: InsertProvider.java, v 0.1 2012-11-6 ÏÂÎç09:29:54 yuzhong.zhao Exp $
 */
public class InsertProvider implements CmdProvider {
    private String   sql;

    private Object[] params;

    private int      timeOut;

    public InsertProvider(Props props) {
        SqlParamEntry entry;
        String sql = props.getString(Benchmark.INSERT_CMD, "");
        String strParam = props.getString(Benchmark.INSERT_PARAMS, "");
        if (StringUtils.isBlank(strParam)) {
            entry = MileCmdParser.parseStringParams(sql);
            this.sql = entry.getSql();
            this.params = entry.getParams();
        } else {
            this.sql = sql;
            this.params = MileCmdParser.stringsToObjects(Arrays.asList(strParam.split(",")));
        }
        this.timeOut = 3000;
    }

    /** 
     * @see com.alipay.mile.benchmark.CmdProvider#execute()
     */
    @Override
    public int execute(ApplationClientImpl client) {
        try {
            MileInsertResult result = client.preInsert(sql, params, timeOut);
            if (result.isSuccessful()) {
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    /** 
     * @see com.alipay.mile.benchmark.CmdProvider#getName()
     */
    @Override
    public String getName() {
        return Benchmark.INSERT;
    }

}
