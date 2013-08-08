/*
 * docdb_packet.h
 *
 *  Created on: 2012-8-28
 *      Author: yuzhong.zhao
 */

#ifndef DOCDB_PACKET_H_
#define DOCDB_PACKET_H_

#include "../../protocol/packet.h"
#include "segment.h"

struct load_segment_packet{
	char* table_name;
	uint16_t sid;
	char* segment_dir;
};

struct replace_segment_packet{
	char* table_name;
	char* segment_dir;
};


struct unload_segment_packet{
	char* table_name;
	uint16_t sid;
};

struct compress_packet{
	char* table_name;
};

struct ensure_index_packet{
	char* table_name;
	char* field_name;
	enum index_key_alg index_type;
	enum field_types data_type;
};

struct del_index_packet{
	char* table_name;
	char* field_name;
	enum index_key_alg index_type;
};

struct doc_stat_packet{
	char* table_name;
	uint8_t type; /*1 段的状态 2索引的状态*/
};



// delete by docids packet
struct delete_docid_packet {
	// table name
	char *table_name;
	// docid number
	uint32_t docid_num;
	// docids
	uint64_t *docids;
};


// update by docid
struct update_docid_packet {
	// table name
	char *table_name;
	// update field
	char *field_name;
	// update filed data
	struct low_data_struct data;
	// docid number
	uint32_t docid_num;
	// docids
	uint64_t *docids;
};



// get kvs packet
struct get_kvs_packet {
	// table name
	char *table_name;
	// docid number
	uint32_t docid_num;
	// docids
	uint64_t *docids;
	// select fields
	struct select_field_array select_field;
};




/**
 *	解析delete_by_id命令
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						delete_by_id命令, 解析出错时返回null
 */
struct delete_docid_packet* parse_delete_docid_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);




/**
 *	解析update_by_id包
 *  @param	mem   	 			内存池
 *	@param 	rbuf				read buffer
 *	@return						update_by_id命令, 解析出错时返回null
 */
struct update_docid_packet *parse_update_docid_packet(MEM_POOL_PTR mem, struct data_buffer *rbuf);




/**
 *	解析get_kvs包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						get_kvs命令, 解析出错时返回null
 */
struct get_kvs_packet* parse_get_kvs_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析load_segment_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						load segment命令, 解析出错时返回null
 */
struct load_segment_packet* parse_load_segment_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析replace_segment_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						load segment命令, 解析出错时返回null
 */
struct replace_segment_packet* parse_replace_segment_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析unload_segment_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						unload segment命令, 解析出错时返回null
 */
struct unload_segment_packet* parse_unload_segment_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析ensure_index_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						ensure index命令, 解析出错时返回null
 */
struct ensure_index_packet* parse_ensure_index_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析del_index_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						del index命令, 解析出错时返回null
 */
struct del_index_packet* parse_del_index_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析doc_stat_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						doc stat命令, 解析出错时返回null
 */
struct doc_stat_packet* parse_doc_stat_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);


/**
 *	解析compress_packet包
 *  @param	pMemPool			内存池
 *	@param 	rbuf				read buffer
 *	@return						compress命令, 解析出错时返回null
 */
struct compress_packet* parse_compress_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf);



/**
 *	构造docserver客户端查询结果数据包，向sbuf中写入相应数据
 *	@param	result_code			查询结果集
 *	@param	msg_head			数据包头
 *	@param 	sbuf				sender buffer
 *	@return
 */
void gen_dc_response_packet(int32_t result_code, struct mile_message_header* msg_head, struct data_buffer* sbuf);


/**
 *	构造docserver客户端查询段状态数据包，向sbuf中写入相应数据
 *  @param  meta_data           段的元数据信息
 *  @param  max_segment_num     最大段数量
 *	@param	msg_head			数据包头
 *	@param 	sbuf				sender buffer
 *	@return
 */
void gen_dc_segment_stat_packet(struct segment_meta_data* meta_data,uint16_t max_segment_num,struct mile_message_header* msg_head, struct data_buffer* sbuf);



/**
 *	构造docserver客户端查询索引状态数据包，向sbuf中写入相应数据
 *  @param  meta_data           索引的元数据信息
 *  @param  index_field_count     最大段数量
 *	@param	msg_head			数据包头
 *	@param 	sbuf				sender buffer
 *	@return
 */
void gen_dc_index_stat_packet(struct index_field_meta* meta_data,uint16_t index_field_count,struct mile_message_header* msg_head, struct data_buffer* sbuf);





#endif /* DOCDB_PACKET_H_ */
