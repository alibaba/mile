/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alipay.mile.benchmark.util.FileLine;
import com.alipay.mile.benchmark.util.Props;
import com.alipay.mile.benchmark.util.SqlParamEntry;
import com.alipay.mile.client.ApplationClientImpl;
import com.alipay.mile.client.result.MileSqlResult;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: SqlFileProvider.java, v 0.1 2012-11-9 ÏÂÎç03:47:06 yuzhong.zhao Exp $
 */
public class SqlFileProvider extends FileProvider {
    private static final Logger  logger = Logger.getLogger(SqlFileProvider.class.getName());

    public SqlFileProvider(Props props) throws IOException {
        super(props);
    }

    
    /** 
     * @see com.alipay.mile.benchmark.CmdProvider#execute(com.alipay.mile.client.ApplationClientImpl)
     */
    @Override
    public int execute(ApplationClientImpl client) {
        FileLine line = next();
        MileSqlResult result = null;
        try {
            if(null == line){
                return -1;
            }
            SqlParamEntry entry = MileCmdParser.parseStringParams(line.getText());
            String sql = entry.getSql();
            if (StringUtils.isNotBlank(sql)) {
                if (StringUtils.startsWithIgnoreCase(sql, "insert")) {
                    result = client.preInsert(sql, entry.getParams(), 3000);
                } else if (StringUtils.startsWithIgnoreCase(sql, "delete")) {
                    result = client.preDelete(sql, entry.getParams(), 3000);
                } else if (StringUtils.startsWithIgnoreCase(sql, "update")) {
                    result = client.preUpdate(sql, entry.getParams(), 3000);
                } else if (StringUtils.startsWithIgnoreCase(sql, "select")) {
                    result = client.preQueryForList(sql, entry.getParams(), 3000);
                } else {
                    return -1;
                }
            }
            
            // only log failed SQL.
            if (result == null || !result.isSuccessful()) {
            	logger.error("execute sql [" + sql + "] failed.");
            }
            return 0;
        } catch (Exception e) {
            logger.error(e);
            return -1;
        }
    }


}
