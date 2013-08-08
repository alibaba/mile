/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark.ctu;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: WordSegment.java, v 0.1 2012-11-5 下午02:32:21 yuzhong.zhao Exp $
 */
public class WordSegment implements Segmenter {
    /** 日志记录器  */
    private static final Logger logger = Logger.getLogger(WordSegment.class);

    /** 
     * @see com.alipay.mile.benchmark.ctu.securitydata.core.service.normalization.Segmenter#split(java.lang.String)
     */
    public List<String> split(String source) {
        if (StringUtils.isBlank(source)) {
            return Collections.emptyList();
        }
        List<String> words = new ArrayList<String>(30);
        StringReader reader = new StringReader(source);

        IKSegmentation ik = new IKSegmentation(reader, false);
        try {
            Lexeme lexeme = null;
            while ((lexeme = ik.next()) != null) {
                words.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            logger.error("split words error : " + source, e);
        } finally {
            reader.close();
        }
        printDebugInfo(source, words);
        return words;
    }

    /**
     * 打印调试信息
     * @param source
     * @param words
     */
    private void printDebugInfo(String source, List<String> words) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("split,Y,");
            sb.append("[").append(source).append("],");
            sb.append("result[");
            String result = ToStringBuilder.reflectionToString(words);
            sb.append(result).append("]");
            logger.debug(sb.toString());
        }
    }
}
