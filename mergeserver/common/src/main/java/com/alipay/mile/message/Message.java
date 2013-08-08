/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.message;

import java.io.IOException;

/**
 * 消息接口
 * @author jin.qian
 * @version $Id: Message.java, v 0.1 2011-5-9 下午05:06:57 jin.qian Exp $
 */
public interface Message {
    //client到mergeserver之间的包类型
    public static final short MT_VG_CM               = 0x1100;             // client 2 merge
    public static final short MT_CM_CONN             = MT_VG_CM | 0x01;    // client conn
    public static final short MT_CM_RE_CONN          = MT_VG_CM | 0x02;
    public static final short MT_CM_SQL              = MT_VG_CM | 0x11;    // sql request
    public static final short MT_CM_PRE_SQL          = MT_VG_CM | 0x12;    // sql request
    public static final short MT_CM_Q_SQL            = MT_VG_CM | 0x13;    // sql request
    public static final short MT_CM_PRE_Q_SQL        = MT_VG_CM | 0x14;    // sql request
    public static final short MT_CM_SPEC_Q_SQL       = MT_VG_CM | 0x15;    // specify SQL request

    //mergeserver到client之间的包类型
    public static final short MT_VG_MC               = 0x1200;             // merge 2 client
    public static final short MT_MC_CONN_RS          = MT_VG_MC | 0x01;    // response to
    public static final short MT_MC_CONN_RS_OK       = MT_VG_MC | 0x02;
    public static final short MT_MC_CONN_RS_ERR      = MT_VG_MC | 0x03;
    public static final short MT_MC_SQL_RS           = MT_VG_MC | 0x11;    // response to sql
    public static final short MT_MC_SQL_EXC_ERR      = MT_VG_MC | 0x12;
    public static final short MT_MC_SPEC_SQL_RS      = MT_VG_MC | 0x13;    // specify SQL result

    //mergeserver到docserver之间的包类型
    public static final short MT_VG_MD               = 0x2100;             // merge 2 document
    public static final short MT_MD_EXE_INSERT       = MT_VG_MD | 0x01;
    public static final short MT_MD_EXE_DELETE       = MT_VG_MD | 0x02;
    public static final short MT_MD_EXE_DELETE_BY_ID = MT_VG_MD | 0x12;
    public static final short MT_MD_EXE_UPDATE       = MT_VG_MD | 0x03;
    public static final short MT_MD_EXE_UPDATE_BY_ID = MT_VG_MD | 0x13;
    public static final short MT_MD_EXE_QUERY        = MT_VG_MD | 0x04;
    public static final short MT_MD_EXE_SPEC_QUERY   = MT_VG_MD | 0x05;
    public static final short MT_MD_EXE_EXPORT       = MT_VG_MD | 0x06;
    public static final short MT_MD_EXE_GET_KVS      = MT_VG_MD | 0x21;    // get info by ids
    public static final short MT_MD_GET_STATE        = MT_VG_MD | 0x22;    // get the docserver state
    public static final short MT_MD_HEART            = MT_VG_MD | 0x31;

    //docserver到mergeserver之间的包类型
    public static final short MT_VG_DM               = 0x2200;             // document 2 merge
    public static final short MT_DM_RS               = MT_VG_DM | 0x01;    // exe response
    public static final short MT_DM_SQL_EXC_ERROR    = MT_VG_DM | 0x02;
    public static final short MT_DM_STATE_RS         = MT_VG_DM | 0x03;    // state response
    public static final short MT_DM_SQ_RS            = MT_VG_DM | 0x04;    // specify query response

    public static final short MT_VG_MT_S             = 0x4100;             // maintain client message
    public static final short MT_VG_MT_R             = 0x4200;             // maintain server message

    public static final short MT_VG_COMMON           = 0x6200;             // common response
    public static final short MT_COMMON_OK           = MT_VG_COMMON | 0x01; // OK
    public static final short MT_COMMON_ERROR        = MT_VG_COMMON | 0x02; // ERROR
    public static final short MT_COMMON_STOP_M       = MT_VG_COMMON | 0x21; // stop mergerServer
    public static final short MT_COMMON_START_M      = MT_VG_COMMON | 0x22; // start mergerServer
    public static final short MT_COMMON_STOP_D       = MT_VG_COMMON | 0x23; // stop docServer
    public static final short MT_COMMON_START_D      = MT_VG_COMMON | 0x24; // start docServer
    public static final short MT_COMMON_START_LEAD_QUERY_M = MT_VG_COMMON | 0x25; // lead query
    public static final short MT_COMMON_STOP_LEAD_QUERY_M = MT_VG_COMMON | 0x26; // lead query

    // ---------------- methods

    public int getId();

    public void setId(int id);

    public short getVersion();

    public void setVersion(short version);

    public short getType();

    public void setType(short type);

    public int getLength();

    public void setLength(int length);

    public byte[] toBytes() throws IOException;

    public void fromBytes(byte[] bytes) throws IOException;

    public String toString();
    // ---------------- methods
}
