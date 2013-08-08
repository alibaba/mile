/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: OperatorExp.java, v 0.1 2011-7-10 下午12:23:11 yuzhong.zhao Exp $
 */
public class OperatorExp extends Expression {

    /** 关系and or */
    public byte operator;

    public byte getOperator() {
        return this.operator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.alipay.mile.server.query.Expression#writeToStream(java.io.DataOutput)
     */
    @Override
    public void writeToStream(DataOutput os, Map<Object, List<Object>> params)                                                                                                     throws IOException {
        os.writeByte(operator);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("运算符:");
        switch (operator) {
            case Constants.EXP_LOGIC_AND:
                sb.append(" and ");
                break;
            case Constants.EXP_LOGIC_OR:
                sb.append(" or ");
                break;
            default:
                sb.append(" 未知运算符").append(operator);
        }
        sb.append(",");
        return sb.toString();
    }

}
