/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2011 All Rights Reserved.
 */
package com.alipay.mile.server.query;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alipay.mile.Constants;
import com.alipay.mile.Expression;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.util.ByteConveror;

/**
 *
 * @author huabing.du
 * @version $Id: QueryStatement.java, v 0.1 2011-4-18 上午09:59:50 Exp huabing.du $
 */
public class QueryStatement extends QueryStatementExpression {
    /** 查询类型  */
    public short           accessType       = Constants.QT_COMMON_QUERY;
    /** 查询返回哪些字段 */
    public List<FieldDesc> selectFields     = new ArrayList<FieldDesc>();
    /** segment时间过滤，可以缩小查询的segment数，提高性能 */
    public TimeHint        hint;
    /** docid可以由用户直接指定查询 */
    public DocHint         dochint          = null;
    /** 走hash或btree索引的条件 */
    public Expression      hashWhere;
    /** 走filter索引的条件 */
    public Expression      filterWhere;
    /** 需要groupBy(分组)的字段 */
    public List<FieldDesc> groupByFields    = new ArrayList<FieldDesc>();
    /** groupBy(分组)中排序的字段 */
    public List<OrderDesc> groupOrderFields = new ArrayList<OrderDesc>();
    /** groupBy中需要限制返回的行数 */
    public int             groupLimit       = 100;
    /** groupBy中从第几行开始 */
    public int             groupOffset      = 0;
    /** having条件 */
    public Expression      having;
    /** 需要orderBy(排序)的字段 */
    public List<OrderDesc> orderFields      = new ArrayList<OrderDesc>();
    /** 限制返回多少行 */
    public int             limit            = Constants.queryResultLimit;
    /** 从第几行开始 */
    public int             offset           = 0;

    @Override
    public void writeToStream(DataOutput os, Map<Object, List<Object>> paramBindMap)
                                                                                    throws IOException {
        // 处理 accessType
        os.writeShort(accessType);
        // 处理 tableName
        ByteConveror.writeString(os, tableName);
        // 处理 TimeHint
        if (hint == null) {
            hint = new TimeHint();
        }
        os.writeInt(4);
        os.writeLong(hint.startCreateTime);
        os.writeLong(hint.endCreateTime);
        os.writeLong(hint.startUpdateTime);
        os.writeLong(hint.endUpdateTime);

        // 处理 选择列
        if (selectFields == null || selectFields.isEmpty()) {
            os.writeInt(0);
        } else {
            os.writeInt(selectFields.size());
            for (FieldDesc field : selectFields) {
                field.writeToStream(os, paramBindMap);
            }
        }

        // 处理 indexWhere
        if (null == hashWhere) {
            os.writeInt(0);
        } else {
            os.writeInt(hashWhere.size);
            hashWhere.postWriteToStream(os, paramBindMap);
        }

        // 处理 filterWhere
        if (null == filterWhere) {
            os.writeInt(0);
        } else {
            os.writeInt(filterWhere.size);
            filterWhere.postWriteToStream(os, paramBindMap);
        }

        // 处理 groupFields
        if (groupByFields == null || groupByFields.isEmpty()) {
            os.writeInt(0);
        } else {
            os.writeInt(groupByFields.size());
            for (FieldDesc field : groupByFields) {
                ByteConveror.writeString(os, field.fieldName);
            }
        }

        // 处理 groupOrderFields
        if (groupOrderFields == null || groupOrderFields.isEmpty()) {
            os.writeInt(0);
        } else {
            os.writeInt(groupOrderFields.size());
            for (OrderDesc od : groupOrderFields) {
                od.writeToStream(os);
            }
        }

        // 处理 groupLimit
        os.writeInt(groupLimit);

        // 处理 orderFields
        if (orderFields == null || orderFields.isEmpty()) {
            os.writeInt(0);
        } else {
            os.writeInt(orderFields.size());
            for (OrderDesc od : orderFields) {
                od.writeToStream(os);
            }
        }
        // 处理 limit
        os.writeInt(limit);

    }
}
