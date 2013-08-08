/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark.ctu;

import java.util.List;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: Segmenter.java, v 0.1 2012-11-5 下午08:23:35 yuzhong.zhao Exp $
 */
public interface Segmenter {
    /**
     * 对指定的字符串进行分词操作
     * 
     * @param source 源字符串
     * @return 切割后的词列表
     */
    public List<String> split(String source);
}
