/*
 * docdb_packet.c
 *
 *  Created on: 2012-8-28
 *      Author: yuzhong.zhao
 */


#include "docdb_packet.h"



struct delete_docid_packet *parse_delete_docid_packet(MEM_POOL_PTR mem, struct data_buffer *rbuf)
{
	struct delete_docid_packet *packet = (struct delete_docid_packet*)mem_pool_malloc(mem, sizeof(struct delete_docid_packet));
	memset(packet, 0, sizeof(struct delete_docid_packet));

	packet->table_name = read_cstring(rbuf, mem);
	packet->docid_num = read_int32(rbuf);

	packet->docids = (uint64_t *)mem_pool_malloc(mem, packet->docid_num * sizeof(uint64_t));

	for(uint32_t i = 0; i < packet->docid_num; i++) {
		packet->docids[i] = read_int64(rbuf);
	}

	if( 0 != rbuf->array_border ) {
		log_error( "parse delete docid packet failed" );
		return NULL;
	}

	return packet;
}






struct update_docid_packet *parse_update_docid_packet(MEM_POOL_PTR mem, struct data_buffer *rbuf)
{
	struct update_docid_packet* packet = (struct update_docid_packet*) mem_pool_malloc(mem, sizeof(struct update_docid_packet));
	memset(packet, 0, sizeof(struct update_docid_packet));

	packet->table_name = read_cstring(rbuf, mem);
	packet->field_name = read_cstring(rbuf, mem);

	if( read_dyn_value(mem, rbuf, &packet->data) < 0 ) {
		log_error( "parse update docid packet's data failed" );
		return NULL;
	}
	packet->docid_num = read_int32(rbuf);
	packet->docids = (uint64_t *)mem_pool_malloc(mem, packet->docid_num * sizeof(struct update_docid_packet));

	for(uint32_t i = 0; i < packet->docid_num; i++) {
		packet->docids[i] = read_int64(rbuf);
	}

	if( 0 != rbuf->array_border ) {
		log_error( "parse update docid packet failed" );
		return NULL;
	}

	return packet;
}









struct get_kvs_packet* parse_get_kvs_packet(MEM_POOL_PTR mem, struct data_buffer* rbuf)
{
	struct get_kvs_packet* packet = (struct get_kvs_packet*) mem_pool_malloc(mem, sizeof(struct get_kvs_packet));
	memset(packet, 0, sizeof(struct get_kvs_packet));

	packet->table_name = read_cstring(rbuf, mem);

	if( parse_select_field_array(mem, rbuf,&packet->select_field) < 0 ) {
		log_error( "parse get kvs packet's select field failed" );
		return NULL;
	}

	packet->docid_num = read_int32(rbuf);

	packet->docids = (uint64_t*) mem_pool_malloc(mem, sizeof(uint64_t) * packet->docid_num);
	for(uint32_t i = 0; i < packet->docid_num; i++) {
		packet->docids[i] = read_int64(rbuf);
	}

	// print_get_kvs_packet(packet);
	if( 0 != rbuf->array_border ) {
		log_error( "parse get kvs packet failed" );
		return NULL;
	}

	return packet;
}



struct load_segment_packet* parse_load_segment_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct load_segment_packet* packet = (struct load_segment_packet*)mem_pool_malloc(pMemPool,sizeof(struct load_segment_packet));
	memset(packet,0,sizeof(struct load_segment_packet));


	packet->table_name = read_cstring(rbuf,pMemPool);
	packet->sid = read_int16(rbuf);
	packet->segment_dir = read_cstring(rbuf,pMemPool);

	return packet;
}

struct replace_segment_packet* parse_replace_segment_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct replace_segment_packet* packet = (struct replace_segment_packet*)mem_pool_malloc(pMemPool,sizeof(struct replace_segment_packet));
	memset(packet,0,sizeof(struct replace_segment_packet));


	packet->table_name = read_cstring(rbuf,pMemPool);
	packet->segment_dir = read_cstring(rbuf,pMemPool);

	return packet;
}




struct unload_segment_packet* parse_unload_segment_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct unload_segment_packet* packet = (struct unload_segment_packet*)mem_pool_malloc(pMemPool, sizeof(struct unload_segment_packet));
	memset(packet,0,sizeof(struct unload_segment_packet));

	packet->table_name = read_cstring(rbuf,pMemPool);
	packet->sid = read_int16(rbuf);

	return packet;
}



struct ensure_index_packet* parse_ensure_index_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct ensure_index_packet* packet = (struct ensure_index_packet*)mem_pool_malloc(pMemPool, sizeof(struct ensure_index_packet));
	memset(packet,0,sizeof(struct ensure_index_packet));

	packet->table_name = read_cstring(rbuf,pMemPool);
	packet->field_name = read_cstring(rbuf,pMemPool);
	packet->index_type = (enum index_key_alg)read_int8(rbuf);
	packet->data_type = (enum field_types)read_int8(rbuf);

	return packet;
}



struct del_index_packet* parse_del_index_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct del_index_packet* packet = (struct del_index_packet*)mem_pool_malloc(pMemPool, sizeof(struct del_index_packet));
	memset(packet,0,sizeof(struct del_index_packet));

	packet->table_name = read_cstring(rbuf,pMemPool);
	packet->field_name = read_cstring(rbuf,pMemPool);
	packet->index_type = (enum index_key_alg)read_int8(rbuf);

	return packet;
}








struct compress_packet* parse_compress_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct compress_packet* packet = (struct compress_packet*)mem_pool_malloc(pMemPool, sizeof(struct compress_packet));
	memset(packet,0,sizeof(struct compress_packet));

	packet->table_name = read_cstring(rbuf,pMemPool);

	return packet;
}





struct doc_stat_packet* parse_doc_stat_packet(MEM_POOL_PTR pMemPool, struct data_buffer* rbuf)
{
	struct doc_stat_packet* packet = (struct doc_stat_packet*)mem_pool_malloc(pMemPool, sizeof(struct doc_stat_packet));
	memset(packet,0,sizeof(struct doc_stat_packet));

	packet->table_name = read_cstring(rbuf,pMemPool);
	packet->type = read_int8(rbuf);

	return packet;
}







void gen_dc_response_packet(int32_t result_code, struct mile_message_header* msg_head, struct data_buffer* sbuf)
{
	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DC_EXE_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//error code
	write_int32((int32_t)result_code, sbuf);
	fill_int32(sbuf->data_len, sbuf, 0);
}



void gen_dc_segment_stat_packet(struct segment_meta_data* meta_data,uint16_t max_segment_num,struct mile_message_header* msg_head, struct data_buffer* sbuf)
{
	//计算结果的长度
	uint16_t i;
	uint16_t segment_count = 0;
	struct segment_meta_data* meta = meta_data;
	for(i=0;i<max_segment_num;i++,meta++)
	{
		if(meta->flag & SEGMENT_INIT)
		{
			segment_count++;
		}
	}

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DC_STAT_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	write_int16(segment_count,sbuf);

	meta = meta_data;
	for(i=0;i<max_segment_num;i++,meta++)
	{
		if(meta->flag & SEGMENT_INIT)
		{
			write_int16(i,sbuf);
			write_int64(meta->create_time,sbuf);
			write_int64(meta->modify_time,sbuf);
			write_int64(meta->checkpoint_time,sbuf);

			write_int8(meta->flag,sbuf);
			write_int32(meta->row_count,sbuf);
			write_int32(meta->del_count,sbuf);
		}
	}

	fill_int32(sbuf->data_len, sbuf, 0);
}


void gen_dc_index_stat_packet(struct index_field_meta* meta_data,uint16_t index_field_count,struct mile_message_header* msg_head, struct data_buffer* sbuf)
{
	//计算结果的长度
	uint16_t i;
	struct index_field_meta* meta = meta_data;

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DC_STAT_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	write_int16(index_field_count,sbuf);

	meta = meta_data;

	uint8_t j;
	struct index_field_stat* stat;
	for(i=0;i<index_field_count;i++,meta++)
	{
		write_int32(strlen(meta->field_name),sbuf);
		write_bytes((uint8_t*)meta->field_name,(uint32_t)strlen(meta->field_name),sbuf);
		write_int8(meta->index_count,sbuf);

		stat = meta->indexs;
		for(j=0;j<meta->index_count;j++,stat++)
		{
			write_int8(stat->index_type,sbuf);
			write_int8(stat->data_type,sbuf);
			write_int8(stat->flag,sbuf);
		}
	}
	fill_int32(sbuf->data_len, sbuf, 0);
}





