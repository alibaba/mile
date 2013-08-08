/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alipay.mile.util.ByteConveror;

/**
 * 列描述
 * 
 * @author huabing.du
 * @version $Id: FieldDesc.java, v 0.1 2011-5-10 下午05:09:59 huabing.du Exp $
 */
public class FieldDesc {

    /** 列名功能函数时有转换成sum(fieldName) */
    public String     fieldName;
    /** 列别名 */
    public String     aliseName;
    /** 功能名 */
    public String     functionName;
    /** 功能ID */
    public byte       functionId;
    /** 参考列名 */
    public String     refColumnName;
    /** WITHIN 条件 */
    public Expression withinExpr;

    // with out within expression
    public void commonPartToStream(DataOutput os) throws IOException {
        if (isComputeField()) {
            // 写入列类型
            os.writeByte(Constants.FUNC_FIELD);
            // 写入函数类型
            byte id = functionId;
            // the highest bit is within expression mask.
            if (null != withinExpr) {
                os.writeByte(id + 128);
            } else {
                os.writeByte(id);
            }
            // 写入列名
            ByteConveror.writeString(os, fieldName);
            // 写入列别名
            ByteConveror.writeString(os, aliseName);
            // 写入ref列名
            ByteConveror.writeString(os, refColumnName);
            // no within expression
        } else {
            // 写入列类型
            os.writeByte(Constants.GENERAL_FIELD);
            // 写入列名
            ByteConveror.writeString(os, fieldName);
            // 写入列别名
            ByteConveror.writeString(os, aliseName);
        }
    }

    // with within expression
    public void writeToStream(DataOutput os, Map<Object, List<Object>> paramBindMap)
                                                                                    throws IOException {
        commonPartToStream(os);
        if (null != withinExpr) {
            os.writeInt(withinExpr.size);
            withinExpr.postWriteToStream(os, paramBindMap);
        }
    }

    // no within expression
    public void commonPartFromStream(DataInput is) throws IOException {
        byte type = is.readByte();
        if (type == Constants.FUNC_FIELD) {
            functionId = is.readByte();
            if ((functionId & 0x80) != 0)
                functionId &= ~0x80;
            this.fieldName = ByteConveror.readString(is);
            this.aliseName = ByteConveror.readString(is);
            this.refColumnName = ByteConveror.readString(is);
        } else {
            this.fieldName = ByteConveror.readString(is);
            this.aliseName = ByteConveror.readString(is);
        }
    }

    public boolean isComputeField() {
        return StringUtils.isNotBlank(functionName);
    }

    @Override
    public int hashCode() {
        if (null == withinExpr) {
            return fieldName.hashCode();
        } else {
            return fieldName.hashCode() + withinExpr.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof FieldDesc) {
            FieldDesc another = (FieldDesc) obj;
            if (withinExpr == another.withinExpr) {
                return fieldName.equals(another.fieldName);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("列名 [" + this.fieldName + "]");
        return sb.toString();
    }
}
