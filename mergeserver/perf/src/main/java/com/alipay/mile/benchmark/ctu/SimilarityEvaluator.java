/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark.ctu;

import java.util.List;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: SimilarityEvaluator.java, v 0.1 2012-11-5 下午05:30:47 yuzhong.zhao Exp $
 */
public interface SimilarityEvaluator {

    /**
     * 对两个字符串进行相似度评测, 返回值区间在[0,1]，越靠近1表示越相似，越靠近0表示越不相似
     * 
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 相似度评测结果
     */
    public float evaluate(String str1, String str2);

    /**
     * 对分好词的两个字符串进行相似度评测, 返回值区间在[0,1]，越靠近1表示越相似，越靠近0表示越不相似
     * @param words1 字符串1的分词列表
     * @param words2 字符串2的分词列表
     * @return 相似度评测结果
     */
    public float evaluate(List<String> words1, List<String> words2);
}
