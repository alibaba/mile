#include "def.h"
#include <string>
#include <iostream>
#include <algorithm>
#include <vector>
extern "C"
{
#include "../../indexengine/src/include/hyperindex_databuffer.h"
}










TEST(DATA_BUFFER_TEST, READ_AND_WRITE){

	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	struct data_buffer* buffer = init_data_buffer();
	buffer->data_len = 256;
	buffer->data = (uint8*) mem_pool_malloc(pMemPool, 256);

	uint8 tmp8;
	uint16 tmp16;
	uint32 tmp32;
	uint64 tmp64;

	char* str = (char*) mem_pool_malloc(pMemPool, 100);
	char* result = (char*) mem_pool_malloc(pMemPool, 100);
	strcpy(str, "hello world!");

	
	write_int8(122, buffer);
	write_int16(1111, buffer);
	write_int32(324, buffer);
	write_int64(2321, buffer);
	write_bytes((uint8*)str, strlen(str)+1, buffer);

	

	tmp8 = read_int8(buffer);
	ASSERT_EQ(tmp8, 122);
	tmp16 = read_int16(buffer);
	ASSERT_EQ(tmp16, 1111);
	tmp32 = read_int32(buffer);
	ASSERT_EQ(tmp32, 324);
	tmp64 = read_int64(buffer);
	ASSERT_EQ(tmp64, 2321);

	read_bytes(buffer, (uint8*)result, strlen(str)+1);
	ASSERT_EQ(0, strcmp(str, result));

	destroy_data_buffer(buffer);
	mem_pool_destroy(pMemPool);
}


