// copy defined const value and declared enum in hyperindex_def.h hi_packet.h

#ifndef FAKE_PROTOCOL_H
#define FAKE_PROTOCOL_H

/* define message type */
#define MT_VG_MD 0x2100 // merge 2 document  operate command type
#define MT_MD_EXE_INSERT  (MT_VG_MD | 0x01)
#define MT_MD_EXE_DELETE  (MT_VG_MD | 0x02)
#define MT_MD_EXE_DELETE_BY_ID  (MT_VG_MD | 0x12)
#define MT_MD_EXE_UPDATE  (MT_VG_MD | 0x03)
#define MT_MD_EXE_UPDATE_BY_ID  (MT_VG_MD | 0x13)
#define MT_MD_EXE_QUERY  (MT_VG_MD | 0x04)
#define MT_MD_EXE_SPEC_QUERY  (MT_VG_MD | 0x05)
#define MT_MD_EXE_GET_KVS  (MT_VG_MD | 0x21)  // get info by ids
#define MT_MD_GET_STATE	(MT_VG_MD | 0x22)    // get the docserver state


#define MT_VG_DM  0x2200  // document 2 merge  maintain command type
#define MT_DM_RS  (MT_VG_DM | 0x01) // exe reponse
#define MT_MD_HEART (MT_VG_DM | 0x31) // heartbeat
#define MT_DM_SQL_EXC_ERROR	(MT_VG_DM | 0x02) //error response
#define MT_DM_STATE_RS	(MT_VG_DM | 0x03) //state response
#define MT_DM_SQ_RS (MT_VG_DM | 0x04) // specify query response

#define MT_VG_MT_S  0x4100 // maintain client message
#define MT_VG_MT_R  0x4200 // maintain server message

#define MT_VG_SM 0x5100 // slaver to master
#define MT_SM_GET_BINLOG (MT_VG_SM | 0x01 ) // get binlog

#define MT_VG_COMMON_R  0x6200 // common response
#define MT_COMMON_OK  (MT_VG_COMMON_R | 0x01) // OK
#define MT_COMMON_ERROR  (MT_VG_COMMON_R | 0x02) // ERROR

#define MT_TEST_REQ_ECHO (0x9800 | 0x00) // echo message for test
#define MT_TEST_RES_ECHO (0x9900 | 0x00) // echo message for test

#define ACCESS_TYPE_DISTINCT 5

enum field_types { 
	HI_TYPE_NULL              = 0,
	HI_TYPE_TINY              = 1,
	HI_TYPE_SHORT             = 2,
	HI_TYPE_UNSIGNED_SHORT    = 3,
	HI_TYPE_LONG              = 4,
	HI_TYPE_UNSIGNED_LONG     = 5,
	HI_TYPE_FLOAT             = 6,
	HI_TYPE_DOUBLE            = 7,
	HI_TYPE_DECIMAL           = 8,   
	HI_TYPE_TIMESTAMP         = 9,
	HI_TYPE_LONGLONG          = 10,
	HI_TYPE_UNSIGNED_LONGLONG = 11,
	HI_TYPE_INT24             = 12,
	HI_TYPE_DATE              = 13,   
	HI_TYPE_TIME              = 14,
	HI_TYPE_DATETIME          = 15,
	HI_TYPE_YEAR              = 16,
	HI_TYPE_NEWDATE           = 17, 
	HI_TYPE_VARCHAR           = 18,
	HI_TYPE_BIT               = 19,	
	HI_TYPE_NUMERIC32         = 20,	
	HI_TYPE_NUMERIC64         = 21,	
	HI_TYPE_NEWDECIMAL        = 246,
	HI_TYPE_ENUM              = 247,
	HI_TYPE_SET               = 248,
	HI_TYPE_TINY_BLOB         = 249,
	HI_TYPE_MEDIUM_BLOB       = 250,
	HI_TYPE_LONG_BLOB         = 251,
	HI_TYPE_BLOB              = 252,
	HI_TYPE_VAR_STRING        = 253,
	HI_TYPE_STRING            = 254,
	HI_TYPE_GEOMETRY          = 255
};

enum order_types{
	ORDER_TYPE_ASC = 101,
	ORDER_TYPE_DESC = 102
};

enum compare_type{
	//等于
	CT_EQ = 7,
	//大于
	CT_GT = 8,
	//大于等于
	CT_GE = 9,
	//小于
	CT_LT = 10,
	//小于等于
	CT_LE = 11,
	//In
	EXP_COMPARE_IN = 13,
	//BETWEEN ()
	EXP_COMPARE_BETWEEN_LG = 15,
	//BETWEEN (]
	EXP_COMPARE_BETWEEN_LGE = 16,
	//BETWEEN [)
	EXP_COMPARE_BETWEEN_LEG = 17,
	//BETWEEN []
	EXP_COMPARE_BETWEEN_LEGE = 18,
	// <>, not equel
	CT_NE = 20
};


enum condition_type{
	//表达式
	CONDITION_EXP = 1,
	//逻辑与
	LOGIC_AND = 2,
	//逻辑或
	LOGIC_OR = 3,
	//集合差集
	HC_SET_MINUS = 123,	
	//联合索引表达式
	HC_UNION_HASH_EXP = 19
};

enum select_field_type {
	VALUE_SELECT    = 0,
	FUNCTION_SELECT = 1,
	STAR_SELECT     = 2
};

enum function_type{
	FUNC_COUNT = 50,
	FUNC_SUM = 51,
	FUNC_MAX = 52,
	FUNC_MIN = 53
};

#endif // FAKE_PROTOCOL_H
