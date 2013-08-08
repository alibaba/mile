/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.client.result;

import java.util.List;

import com.alipay.mile.DocDigestData;

/**
 *
 * @author yuzhong.zhao
 * @version $Id: MileSqlResult.java, v 0.1 2011-9-8 下午01:46:23 yuzhong.zhao Exp
 *          $
 */
public class MileSqlResult {

    private List<DocDigestData> docState;

    public List<DocDigestData> getDocState() {
        return docState;
    }

    public void setDocState(List<DocDigestData> docState) {
        this.docState = docState;
    }

    /**
     * sql执行是否成功
     *
     * @return true表示执行成功，false表示失败
     */
    public boolean isSuccessful() {
        if (docState == null || docState.isEmpty()) {
            return false;
        }

        for (DocDigestData docDigestData : docState) {
            if (!docDigestData.isSuccess()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 由于sql可能分发到多台docserver上执行，当部分docserver宕机时，我们仍然会返回部分的结果集，通过此接口来表明结果集的完整性
     *
     * @return 返回值是一个0到1之间的浮点数，0表示所有docserver均无返回，1表示结果集是完整的
     */
    public double getCompleteness() {
        double totalDoc = docState.size();
        double sucessDoc = 0;

        if (docState == null || docState.isEmpty()) {
            return 0;
        }

        for (DocDigestData docDigestData : docState) {
            if (docDigestData.isSuccess()) {
                sucessDoc += 1;
            }
        }

        if (totalDoc == 0) {
            return 0;
        } else {
            return sucessDoc / totalDoc;
        }
    }
}
