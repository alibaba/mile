/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark.ctu;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alipay.mile.benchmark.Benchmark;
import com.alipay.mile.benchmark.FileProvider;
import com.alipay.mile.benchmark.util.FileLine;
import com.alipay.mile.benchmark.util.Props;
import com.alipay.mile.benchmark.util.SqlParamEntry;
import com.alipay.mile.client.ApplationClientImpl;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: CtuMiniSearchProvider.java, v 0.1 2012-11-9 下午03:55:42 yuzhong.zhao Exp $
 */
public class CtuMiniSearchProvider extends FileProvider {
    private static final Logger logger = Logger.getLogger(CtuMiniSearchProvider.class.getName());

    private float               threshold;

    private int                 limit;

    private String              table;

    private boolean             containOriginal;

    public CtuMiniSearchProvider(Props props) throws IOException {
        super(props);
        this.threshold = (float) props.getFloat(Benchmark.CTU_SIMILAR_THRESHOLD);
        this.limit = (int) props.getInt(Benchmark.CTU_MINISEARCH_LIMIT);

        String cmd = props.getString(Benchmark.CMD_PROVIDER);
        if (StringUtils.endsWith(cmd, "address")) {
            table = "address";
        } else if (StringUtils.endsWith(cmd, "x")) {
            table = "x";
        } else {
            table = "";
        }

        containOriginal = props.getBoolean(Benchmark.CTU_MINISEARCH_CONTAIN_ORIGINAL, false);

    }

    private SqlParamEntry genQuerySql(String tableName, String colName, String text) {
        StringBuffer sql = new StringBuffer("SELECT " + colName + " FROM " + tableName
                                            + " INDEXWHERE " + colName + " MATCH (");
        Segmenter segmenter = new WordSegment();

        List<String> words = segmenter.split(text);
        if (containOriginal) {
            words.add(text);
        }
        Object[] params = new Object[words.size()];
        SqlParamEntry entry = new SqlParamEntry();

        sql.append("?");
        params[0] = words.get(0);
        for (int i = 1; i < words.size(); i++) {
            sql.append(",?");
            params[i] = words.get(i);
        }
        sql.append(") limit " + limit);

        entry.setSql(sql.toString());
        entry.setParams(params);

        return entry;
    }

    private SqlParamEntry genInsertSql(String tableName, String colName, String text) {
        StringBuffer sql = new StringBuffer("INSERT INTO " + tableName + " " + colName + "=? "
                                            + " WITH WORDSEG(" + colName + ")=(");
        Segmenter segmenter = new WordSegment();

        List<String> words = segmenter.split(text);
        if (containOriginal) {
            words.add(text);
        }
        Object[] params = new Object[words.size() + 1];
        SqlParamEntry entry = new SqlParamEntry();

        sql.append("?");
        params[0] = text;
        params[1] = words.get(0);
        for (int i = 1; i < words.size(); i++) {
            sql.append(",?");
            params[i + 1] = words.get(i);
        }
        sql.append(")");

        entry.setSql(sql.toString());
        entry.setParams(params);

        return entry;
    }

    private int process(ApplationClientImpl client, String tableName, String colName, String text) {
        SqlParamEntry entry;

        Segmenter segmenter = new WordSegment();
        SimilarityEvaluator evaluator = new SimilarityEvaluatorImpl(segmenter);

        try {
            entry = genQuerySql(tableName, colName, text);
            MileQueryResult result = client
                .preQueryForList(entry.getSql(), entry.getParams(), 3000);

            if (result.isSuccessful()) {
                for (Map<String, Object> row : result.getQueryResult()) {
                    String stext = (String) row.get(colName);
                    if (evaluator.evaluate(stext, text) >= threshold) {
                        return 0;
                    }
                }

                entry = genInsertSql(tableName, colName, text);
                client.preInsert(entry.getSql(), entry.getParams(), 3000);

            } else {
                logger.error("在处理文本时出错:" + text);
                return -1;
            }

        } catch (Exception e) {
            logger.error("在处理文本时出现异常:" + text, e);
            return -1;
        }

        return 1;
    }

    /** 
     * @see com.alipay.mile.benchmark.CmdProvider#execute(com.alipay.mile.client.ApplationClientImpl)
     */
    @Override
    public int execute(ApplationClientImpl client) {
        try {

            FileLine line = next();

            if (null == line) {
                return -1;
            }

            String[] words = line.getText().split(",");
            StringBuffer address = new StringBuffer();
            StringBuffer goods = new StringBuffer();

            goods.append(words[1]);
            for (int i = 2; i < words.length; i++) {
                address.append(words[i]);
            }

            if (StringUtils.equals(table, "x")) {
                return process(client, "x", "x", address.toString());
            }

            if (StringUtils.equals(table, "x")) {
                return process(client, "x", "x", goods
                    .toString());
            }

            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

}
