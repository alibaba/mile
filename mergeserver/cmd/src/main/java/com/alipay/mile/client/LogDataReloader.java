/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alipay.mile.message.TypeCode;
import com.alipay.mile.util.ByteConveror;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: LogDataReloader.java, v 0.1 2011-11-17 下午02:31:06 yuzhong.zhao Exp $
 */
public class LogDataReloader {
    private static final Logger         LOGGER             = Logger.getLogger("LOGDATA-RELOAD");

    /** 用于查询和插入的client端  */
    private ApplationClientImpl         applationClientImpl;

    /** 数据加载器，每张表对应一个数据加载器  */
    private Map<String, RecordReloader> recordReloaders    = new HashMap<String, RecordReloader>();

    /** 插入sql的模式  */
    private static final Pattern        INSERT_SQL_PAT       = Pattern
                                                               .compile("insert\\s+into\\s+((\\w|_)+)\\s+");

    /** 插入列的模式  */
    private static final Pattern        INSERT_FIELD_PAT     = Pattern
                                                               .compile("\\s+((\\w|_)+)\\s*=\\s*\\?");

    /** 插入参数头的模式 */
    private static final Pattern        INSERT_PARAM_HEAD_PAT = Pattern.compile("#params#");

    /** 插入参数的模式 */
    private static final Pattern        INSERT_PARAM_PAT     = Pattern.compile("(\\w+):(.*)");

    public LogDataReloader(ApplationClientImpl applationClientImpl) {
        this.applationClientImpl = applationClientImpl;
    }

    /**
     * 注册一个记录补录器
     * 
     * @param tableName             表名
     * @param hashIndexes           hash索引列
     * @param filterIndexes         filter索引列
     */
    public void registRecordLoader(String tableName, String[] hashIndexes, String[] filterIndexes) {
        RecordReloader recordReloader = new RecordReloader();
        recordReloader.setApplationClientImpl(applationClientImpl);
        recordReloader.setTableName(tableName);
        recordReloader.setHashIndexes(hashIndexes);
        recordReloader.setFilterIndexes(filterIndexes);
        recordReloaders.put(tableName, recordReloader);
    }

    /**
     * 解析一行日志
     * 
     * @param logLine       一行日志
     */
    private void parseLogLine(String logLine) {
        String tableName;
        List<String> fieldList = new ArrayList<String>();
        List<Object> paramList = new ArrayList<Object>();

        Matcher matcher = INSERT_SQL_PAT.matcher(logLine);
        if (matcher.find()) {
            tableName = matcher.group(1);
            String insertSql = logLine.substring(matcher.start());

            Matcher fieldMatcher = INSERT_FIELD_PAT.matcher(logLine);
            while (fieldMatcher.find()) {
                fieldList.add(fieldMatcher.group(1));
            }

            Matcher paramHeadMatcher = INSERT_PARAM_HEAD_PAT.matcher(logLine);
            if (paramHeadMatcher.find()) {
                String paramValues = logLine.substring(paramHeadMatcher.end());
                String[] paramArray = paramValues.split("#col#");

                for (int i = 1; i < paramArray.length; i++) {
                    if (StringUtils.isBlank(paramArray[i])) {
                        paramList.add(null);
                    } else {
                        Matcher paramMatcher = INSERT_PARAM_PAT.matcher(paramArray[i]);
                        if (paramMatcher.find()) {
                            paramList.add(ByteConveror.preString2value(TypeCode
                                .getTCByName(paramMatcher.group(1)), paramMatcher.group(2)));
                        }
                    }
                }

            }

            if (fieldList.size() == paramList.size()) {
                Map<String, Object> sqlMap = new HashMap<String, Object>();
                for (int i = 0; i < fieldList.size(); i++) {
                    sqlMap.put(fieldList.get(i), paramList.get(i));
                }

                RecordReloader recordReloader = recordReloaders.get(tableName);
                int result = recordReloader.loadData(sqlMap);
                if (result == 1) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("数据补录成功：" + insertSql);
                    }
                } else if (result == 0) {
                    LOGGER.warn("数据已经存在：" + insertSql);
                } else {
                    LOGGER.error("数据补录失败：" + insertSql);
                }
            }
        }

    }

    /**
     * 解析日志文件并进行数据补录
     * 
     * @param logFileNames      日志文件名，可能会有多个
     */
    public void parseLogFile(List<String> logFileNames) {
        FileReader fileReader = null;
        BufferedReader bufReader = null;

        for (String fileName : logFileNames) {
            fileReader = null;
            bufReader = null;
            try {
                fileReader = new FileReader(fileName);
                bufReader = new BufferedReader(fileReader);

                String logLine = bufReader.readLine();
                while (logLine != null) {
                    parseLogLine(logLine);
                    logLine = bufReader.readLine();
                }

                if (bufReader != null) {
                    bufReader.close();
                }
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                LOGGER.error("从日志文件" + fileName + "中补全数据时出错, ", e);
            }
        }

    }

    public void setApplationClientImpl(ApplationClientImpl applationClientImpl) {
        this.applationClientImpl = applationClientImpl;
    }

    public ApplationClientImpl getApplationClientImpl() {
        return applationClientImpl;
    }

}
