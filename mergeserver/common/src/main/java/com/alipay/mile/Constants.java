/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile;

import java.util.HashMap;
import java.util.Map;

import com.alipay.mile.message.TypeCode;

/**
 * Constant used for Server and Engine
 *
 * @author huabing.du
 *
 */
public final class Constants {

    public static final short             VERSION                   = 0x0002;
    public static final short             QT_COMMON_QUERY           = 0;
    public static final short             QT_COMMON_DISTINCT        = 5;
    public static final short             QT_COMMON_DISTINCT_COUNT  = 6;

    // FieldDesc
    public static final byte              GENERAL_FIELD             = 0;
    public static final byte              FUNC_FIELD                = 1;

    // ----- Function
    public static final byte              FUNC_COUNT                = 50;
    public static final byte              FUNC_SUM                  = 51;
    public static final byte              FUNC_MAX                  = 52;
    public static final byte              FUNC_MIN                  = 53;
    public static final byte              FUNC_DISTINCT_COUNT       = 54;
    public static final byte              FUNC_SQUARE_SUM           = 55;
    public static final byte              FUNC_AVG                  = 56;
    public static final byte              FUNC_VAR                  = 57;
    public static final byte              FUNC_STD                  = 58;

    public static final int               FUNC_COLUMN_ID_BEGINE     = 1024 * 1024 + 3;

    public static final int               COL_DOC_ID                = 1024 * 1024 - 3;
    public static final int               COL_INSERT_ST_ID          = 1024 * 1024 - 2;
    public static final int               COL_UPDATE_TS_ID          = 1024 * 1024 - 1;
    public static final String            COL_DOC_NAME              = "mile_doc_id";
    public static final String            COL_INSERT_ST_NAME        = "mile_create_time";
    public static final String            COL_UPDATE_TS_NAME        = "mile_update_time";

    // ------ Order
    public static final byte              ORDER_TYPE_ASC            = 101;
    public static final byte              ORDER_TYPE_DESC           = 102;

    // where express type
    public static final byte              EXP_CONDITION_EXP         = 1;
    public static final byte              EXP_SET_AND               = 2;
    public static final byte              EXP_SET_OR                = 3;
    public static final byte              EXP_FILTER_EXP            = 4;
    public static final byte              EXP_INTERSECTION          = 5;                          //INTERSECTION
    public static final byte              EXP_UNIONSET              = 6;                          //UnionSet
    public static final byte              EXP_LOGIC_AND             = EXP_SET_AND;
    public static final byte              EXP_LOGIC_OR              = EXP_SET_OR;
    public static final byte              EXP_COMPARE_EQUALS        = 7;                          // =
    public static final byte              EXP_COMPARE_GT            = 8;                          // >
    public static final byte              EXP_COMPARE_GET           = 9;                          // >=
    public static final byte              EXP_COMPARE_LT            = 10;                         // <
    public static final byte              EXP_COMPARE_LET           = 11;                         // <=
    public static final byte              EXP_COMPARE_IN            = 13;                         // in
    public static final byte              EXP_COMPARE_BETWEEN       = 14;                         // v
    public static final byte              EXP_COMPARE_BETWEEN_LG    = 15;                         // ()
    public static final byte              EXP_COMPARE_BETWEEN_LGE   = 16;                         // (]
    public static final byte              EXP_COMPARE_BETWEEN_LEG   = 17;                         // [)
    public static final byte              EXP_COMPARE_BETWEEN_LEGE  = 18;                         // []
    public static final byte              EXP_UNION_HASH_EXP        = 19;                         // union hash
    public static final byte              EXP_COMPARE_NOT_EQUALS    = 20;                         // <>
    public static final byte              EXP_COMPARE_MATCH         = 21;                         // match

    // ----- Column id type
    public static final int               COLUMN_CLASS_INNER        = 20001;
    public static final int               COLUMN_CLASS_FIELD        = 20002;
    public static final int               COLUMN_CLASS_CACULATE     = 20003;

    //执行超时时间
    public static final int               DELETE_TIME_OUT           = 10000;
    public static final int               UPDATE_TIME_OUT           = 10000;
    public static final int               INSERT_TIME_OUT           = 10000;
    public static final int               QUERY_TIME_OUT            = 10000;
    public static final int               GET_STATE_TIME_OUT        = 1000;

    //docserver的身份
    public static final int               MASTER                    = 1;
    public static final int               SLAVE                     = 0;

    //查询结果的最大条数
    public static int                     queryResultLimit          = 50;

    // my host
    public static final String            HOST                      = "localhost";

    // command
    public static final String            MERGESERVER_START         = "mergerServer start";
    public static final String            MERGESERVER_STOP          = "mergerServer stop";
    public static final String            DOCSERVER_START           = "docServer start";
    public static final String            DOCSERVER_STOP            = "docServer stop";
    public static final String            MERGESERVER_LEAD_QUERY    = "mergeserver lead query";

    // 插入命令的返回列
    public static final String            INSERT_RETURN_COLUMN_NAME = "docid";
    // 更新命令的返回列
    public static final String            UPDATE_RETURN_COLUMN_NAME = "updateNum";
    // 删除命令的返回列
    public static final String            DELETE_RETURN_COLUMN_NAME = "deleteNum";
	// export step return column anme
	public static final String            EXPORT_RETURN_COLUMN_NAME = "exportNum";

    // 向docserver的状态查询
    public static final String            DOCSERVER_STATE_READABLE  = "readable";

    public static final Map<String, Byte> STATEMAP                  = new HashMap<String, Byte>();
    static {
        STATEMAP.put(DOCSERVER_STATE_READABLE, TypeCode.TC_BYTE);
    }

}
