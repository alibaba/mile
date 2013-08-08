/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_databuffer.h
 *
 *    Description:  data buffer，处理网络字节序和机器字节序的转换
 *
 *        Version:  1.0
 *        Created:  2011/05/06 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yuzhong.zhao
 *        Company:  alipay
 *
 * =====================================================================================
 */

#ifndef DATABUFFER_H
#define DATABUFFER_H
#include "../common/def.h"
#include "../common/mem.h"





struct data_buffer{
	//数据长度
	uint32_t data_len;
	//数据
	uint8_t* data;
	//当前正在写的元素的位置
	uint32_t wpos;
	//当前正在读的元素的位置
	uint32_t rpos;
	//size
	uint32_t size;
	//标记是否越界, 0表示没有越界, 1表示越界
	int8_t array_border;
};



struct data_buffer* init_data_buffer();


void destroy_data_buffer(struct data_buffer* buffer);

// make sure allocated storage space at least n.
// if current storage space less than n, will expand to 2 * n.
void databuf_reserve(struct data_buffer *buf, size_t n);

// resize the allocated storage space to exact n.
void databuf_resize(struct data_buffer *buf, size_t n);

void print_data_buffer(struct data_buffer * buffer);



void clear_data_buffer(struct data_buffer* buffer);



uint8_t read_int8(struct data_buffer* buffer);



uint16_t read_int16(struct data_buffer* buffer);



uint32_t read_int32(struct data_buffer* buffer);



uint64_t read_int64(struct data_buffer* buffer);




uint8_t read_pos_int8(struct data_buffer* buffer, uint32_t pos);



uint16_t read_pos_int16(struct data_buffer* buffer, uint32_t pos);



uint32_t read_pos_int32(struct data_buffer* buffer, uint32_t pos);



uint64_t read_pos_int64(struct data_buffer* buffer, uint32_t pos);


// read len:data as cstring
char *read_cstring(struct data_buffer *buf, MEM_POOL_PTR mem);


void read_bytes(struct data_buffer* buffer, uint8_t* data, uint32_t len);



void write_int8(uint8_t n, struct data_buffer* buffer);



void write_int16(uint16_t n, struct data_buffer* buffer);



void write_int32(uint32_t n, struct data_buffer* buffer);



void write_int64(uint64_t n, struct data_buffer* buffer);



void write_bytes(uint8_t* bytes_data, uint32_t len, struct data_buffer* buffer);



void fill_int8(uint8_t n, struct data_buffer* buffer, uint32_t fpos);



void fill_int32(uint32_t n, struct data_buffer* buffer, uint32_t fpos);



uint16_t convert_int16(uint16_t n);



uint32_t convert_int32(uint32_t n);



uint64_t convert_int64(uint64_t n);


#endif
