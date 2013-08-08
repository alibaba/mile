#include <signal.h>
#include <unistd.h>
#include <getopt.h>
#include <string.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include "common/def.h"
#include "storage/docdb/db.h"
#include "protocol/databuffer.h"
#include "protocol/packet.h"
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <readline/readline.h>
#include <readline/history.h>


#define MESSAGE_HEAD_LEN 8
int global_start = 0;

void print_usage(char *prog_name)
{
   fprintf(stdout, "%s -c,  --config        docserver:port\n"
		           "	 -h, --help           print this message\n"
		           "	 -l, --cmd_line       exec cmd\n"
		   ,prog_name);
}



void print_help(const char *cmd)
{
	if (cmd == NULL || strcmp(cmd, "ensure_index") == 0) {
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : ensure_index table_name field_name index_type data_type\n"
                 "DESCRIPTION: add one index field\n"
                 "             index_type:btree(1) hash(2) filter(3) fulltext(4)\n"
                 "             data_type:string(254) longlong(10) double(7) int(4) short(2) tiny(1)\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "del_index") == 0) {
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : del_index table_name field_name index_type\n"
                 "DESCRIPTION: del one index field\n"
                 "             index_type:btree(1) hash(2) filter(3)\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "load") == 0) {
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : load table_name sid segment_dir\n"
                 "DESCRIPTION: load one segment to table\n"
                 "             segment_dir:the dir locate of the segment which to load into engine\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "unload") == 0) {
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : unload table_name sid\n"
                 "DESCRIPTION: unload one segment from table\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "replace") == 0) {
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : replace table_name segments_dir\n"
                 "DESCRIPTION: replace all the segments with the segments_dir\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "compress") == 0) {
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : compress table_name\n"
                 "DESCRIPTION: compress all the segments which set the compress flag\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "stat") == 0) {
		
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : stat table_name type\n"
                 "DESCRIPTION: print the stat of the table\n"
                 "             type:1 the stat of the segment\n"
                 "                  2 the index info of the table\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "checkpoint") == 0) {
		
         fprintf(stdout,
                 "------------------------------------------------\n"
                 "SYNOPSIS   : checkpoint\n"
                 "DESCRIPTION: start checkpoint\n"
         );
     }

	if (cmd == NULL || strcmp(cmd, "get_load_threshold") == 0) {

		fprintf(stdout,
				"------------------------------------------------\n"
				"SYNOPSIS   : get_load_threshold\n"
				"DESCRIPTION: get overload threshold value\n"
			   );
	}

	if (cmd == NULL || strcmp(cmd, "set_load_threshold") == 0) {

		fprintf(stdout,
				"------------------------------------------------\n"
				"SYNOPSIS   : set_load_threshold load_threshold\n"
				"DESCRIPTION: set overload threshold value\n"
			   );
	}

	if (cmd == NULL || strcmp(cmd, "ldb") == 0) {
		fprintf(stdout,
				"------------------------------------------------\n"
				"SYNOPSIS   : ldb stat table [stat-name] ...\n"
				"             ldb stat-all [stat-name] ...\n"
				"             ldb create table table-name table-config-line ...\n"
				"             ldb delete table table-name [force]\n"
				"DESCRIPTION:\n"
				"    ldb stat table [stat-name]\n"
				"           show stat of table, stat-name can be: ref-count, memtables, block-cache, stats, files-per-level, sstables\n"
				"    ldb stat-all [stat-name]\n"
				"           show all tables' status\n"
				"    ldb create table table-name table-config-line ...\n"
				"           create table, table-config-line can be:\n"
				"                  table-name.row_key=...\n"
				"                  table.time_key=...\n"
				"                  table.time_key_len=...\n"
				"                  table.time_key_scale=...\n"
				"                  table.expire_time=...\n"
				"    ldb delete table table-name [force]\n"
				"           delete table. remove table files if force used.\n"
			   );
	}


 
}

static uint16_t message_type(const struct data_buffer *buf)
{
	// make a copy
	struct data_buffer copy = *buf;
	// no length in header
	copy.rpos = 2;
	uint16_t type = read_int16(&copy);
	return type;
}

struct data_buffer* gen_ensure_index_packet(char* cmd,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* field_name = NULL;
	uint32_t tblen;
	uint32_t fdlen;
	char* token;
	enum index_key_alg index_type;
	enum field_types data_type;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	
	if((field_name = strtok(NULL," ")) == NULL) 
		return NULL;

	if((token = strtok(NULL," ")) == NULL)
		return NULL;
	index_type = (index_key_alg)atoi(token);
	if(index_type > 0xff)
		return NULL;

	if((token = strtok(NULL," ")) == NULL)
		return NULL;
	data_type = (enum field_types)atoi(token);
	if(data_type > 0xff)
		return NULL;

	tblen = strlen(table_name);
	fdlen = strlen(field_name);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen + fdlen + 10 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);

	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_INDEX, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);
	
	write_int32(fdlen,dbuf);
	write_bytes((uint8_t*)field_name,fdlen,dbuf);

	write_int8(index_type,dbuf);
	write_int8(data_type,dbuf);

	return dbuf;
}


struct data_buffer* gen_del_index_packet(char* cmd,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* field_name = NULL;
	char* token = NULL;
	uint32_t tblen;
	uint32_t fdlen;
	enum index_key_alg index_type;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	
	if((field_name = strtok(NULL," ")) == NULL) 
		return NULL;

	if((token = strtok(NULL," ")) == NULL)
		return NULL;
	index_type = (index_key_alg)atoi(token);

	tblen = strlen(table_name);
	fdlen = strlen(field_name);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen + fdlen + 9 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);

	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_UNINDEX, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);
	
	write_int32(fdlen,dbuf);
	write_bytes((uint8_t*)field_name,fdlen,dbuf);

	write_int8(index_type,dbuf);

	return dbuf;
}


struct data_buffer* gen_load_packet(char* cmd,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* segment_dir = NULL;
	char* token = NULL;
	uint32_t tblen;
	uint32_t dirlen;
	uint16_t sid;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	tblen = strlen(table_name);

	if((token = strtok(NULL," ")) == NULL)
		return NULL;
	sid = atoi(token);

	if((segment_dir = strtok(NULL," ")) == NULL) 
		return NULL;

	dirlen = strlen(segment_dir);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen + dirlen + 10 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_LOAD, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);
	
	write_int16(sid,dbuf);
	write_int32(dirlen,dbuf);
	write_bytes((uint8_t*)segment_dir,dirlen,dbuf);

	return dbuf;
}


struct data_buffer* gen_replace_packet(char* cmd,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* segment_dir = NULL;
	uint32_t tblen;
	uint32_t dirlen;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	
	tblen = strlen(table_name);

	if((segment_dir = strtok(NULL," ")) == NULL) 
		return NULL;

	dirlen = strlen(segment_dir);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen + dirlen + 8 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_REPLACE, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);
	
	write_int32(dirlen,dbuf);
	write_bytes((uint8_t*)segment_dir,dirlen,dbuf);

	return dbuf;
}



struct data_buffer* gen_unload_packet(char* cmd,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* token = NULL;
	uint32_t tblen;
	uint16_t sid;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	tblen = strlen(table_name);

	if((token = strtok(NULL," ")) == NULL)
		return NULL;
	sid = atoi(token);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen +  6 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_UNLOAD, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);
	
	write_int16(sid,dbuf);

	return dbuf;
}

struct data_buffer* gen_checkpoint_packet(char* cmd,MEM_POOL* mem_pool)
{
	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_CP, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	return dbuf;
}

struct data_buffer* gen_get_load_threshold_packet(char* cmd,MEM_POOL* mem_pool)
{
	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_GET_LOAD_THRESHOLD, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	return dbuf;
}

struct data_buffer* gen_set_load_threshold_packet(char* cmd,MEM_POOL* mem_pool)
{
	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = MESSAGE_HEAD_LEN + 2*sizeof(uint32_t) + sizeof(double);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_SET_LOAD_THRESHOLD, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	char *value_str = strtok(NULL, " ");
	if (!value_str)
		return NULL;

	double value = atof(value_str);
	write_bytes((uint8_t *)&value, sizeof(value), dbuf);

	return dbuf;
}

struct data_buffer *gen_ldb_control_packet(char *cmd, MEM_POOL *mem_pool)
{
	// make cmd to full command
	while (strtok(NULL, " "));

	struct data_buffer *dbuf = init_data_buffer();

	// header
	write_int32(0, dbuf);
	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_LDB_CONTROL, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	write_int32(strlen(cmd), dbuf);
	write_bytes((uint8_t *)cmd, strlen(cmd), dbuf);

	fill_int32(dbuf->data_len, dbuf, 0);

	return dbuf;
}


struct data_buffer* gen_compress_packet(char* cmd,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	uint32_t tblen;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	tblen = strlen(table_name);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen +  4 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_EXE_COMPRESS, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);

	return dbuf;
}


struct data_buffer* gen_stat_packet(char* cmd,uint8_t* type,MEM_POOL* mem_pool)
{
	char* table_name = NULL;
	char* token = NULL;
	uint32_t tblen;
	
	if((table_name = strtok(NULL," ")) == NULL) 
		return NULL;
	tblen = strlen(table_name);

	if((token = strtok(NULL," ")) == NULL)
		return NULL;
	*type = atoi(token);

	struct data_buffer *dbuf = init_data_buffer();
	size_t data_len = tblen +  5 * sizeof(uint8_t) + MESSAGE_HEAD_LEN + 2*sizeof(uint32_t);
	databuf_resize(dbuf, data_len);
	
	//写入数据长度
	write_int32(data_len,dbuf);

	write_int8(1, dbuf);
	write_int8(1, dbuf);
	write_int16(MT_CD_STAT, dbuf);
	write_int32(0, dbuf);
	//timeout
	write_int32(0, dbuf);

	//拼接协议包
	write_int32(tblen,dbuf);
	write_bytes((uint8_t*)table_name,tblen,dbuf);

	write_int8(*type,dbuf);
	return dbuf;
}


int socket_write(int sfd,const void *data, int len) 
{
    int res;
    do {
        res = write(sfd, data, len);
    } while (res < 0 && errno == EINTR);
	
    return res;
}


int socket_read (int sfd,void *data, int len) {
    int res;
    do {
        res = read(sfd, data, len);
    } while (res < 0 && errno == EINTR);
    return res;
}


int32_t execute_common_handle(struct data_buffer* dbuf,int socket_fd,MEM_POOL* mem_pool)
{
	struct data_buffer rbuf;
	
	//发数据包
	if(socket_write(socket_fd,dbuf->data,dbuf->data_len) <= 0)
		return -1;

	//接受命名
	memset(&rbuf,0,sizeof(struct data_buffer));
	rbuf.data_len = sizeof(uint32_t);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,sizeof(uint32_t));
	rbuf.size = rbuf.data_len;
	
	if(socket_read(socket_fd,rbuf.data,sizeof(uint32_t)) <= 0)
		return -1;

	rbuf.data_len = read_int32(&rbuf);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,rbuf.data_len);
	rbuf.rpos = 0;
	rbuf.size = rbuf.data_len;

	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;
	rbuf.rpos += MESSAGE_HEAD_LEN;

	uint16_t type = message_type(&rbuf);
	if (type != MT_DC_EXE_RS)
		return -1;

	return read_int32(&rbuf);
}

int32_t execute_segment_stat_handle(struct data_buffer* dbuf,int socket_fd,MEM_POOL* mem_pool)
{
	struct data_buffer rbuf;
	
	//发数据包
	if(socket_write(socket_fd,dbuf->data,dbuf->data_len) <= 0)
		return -1;

	//接受命名
	memset(&rbuf,0,sizeof(struct data_buffer));
	rbuf.data_len = sizeof(uint32_t);
	rbuf.size = rbuf.data_len;
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,sizeof(uint32_t));
	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;

	rbuf.data_len = read_int32(&rbuf);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,rbuf.data_len);
	rbuf.rpos = 0;
	rbuf.size = rbuf.data_len;

	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;
	
	rbuf.rpos += MESSAGE_HEAD_LEN;
	uint16_t type = message_type(&rbuf);
	if (type != MT_DC_STAT_RS)
		return -1;

	uint16_t segment_count = read_int16(&rbuf);

	uint16_t i;
	fprintf(stderr,"%-20s%-20s%-20s%-20s%-20s%-20s%-20s\n","SEGMENT_ID","CREATE_TIME","MODIFY_TIME","CHECKPOINT_TIME","FLAG","ROW_COUNT","DEL_COUNT");
	for(i=0; i<segment_count; i++)
	{
		fprintf(stderr,"%-20u",read_int16(&rbuf));
		fprintf(stderr,"%-20llu",read_int64(&rbuf));
		fprintf(stderr,"%-20llu",read_int64(&rbuf));
		fprintf(stderr,"%-20llu",read_int64(&rbuf));
		fprintf(stderr,"%-20u",read_int8(&rbuf));
		fprintf(stderr,"%-20u",read_int32(&rbuf));
		fprintf(stderr,"%-20u\n",read_int32(&rbuf));
	}
	
	return 0;
}


int32_t execute_index_stat_handle(struct data_buffer* dbuf,int socket_fd,MEM_POOL* mem_pool)
{	
	struct data_buffer rbuf;
	
	//发一个数据包长度
	if(socket_write(socket_fd,dbuf->data,dbuf->data_len) <= 0)
		return -1;

	//接受命名
	memset(&rbuf,0,sizeof(struct data_buffer));
	rbuf.data_len = sizeof(uint32_t);
	rbuf.size = rbuf.data_len;
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,sizeof(uint32_t));
	
	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;
	
	rbuf.data_len = read_int32(&rbuf);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,rbuf.data_len);
	rbuf.rpos = 0;
	rbuf.size = rbuf.data_len;
	
	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0) 
		return -1;

	rbuf.rpos += MESSAGE_HEAD_LEN;
	uint16_t type = message_type(&rbuf);
	if (type != MT_DC_STAT_RS)
		return -1;

	uint16_t field_count = read_int16(&rbuf);

	uint16_t i;
	char* field_name = NULL;
	fprintf(stderr,"%-20s%-20s%-20s%-20s\n","FIELD_NAME","INDEX_TYPE","DATA_TYPE","FLAG");
	uint8_t j;
	uint8_t index_num;
	for(i=0; i<field_count; i++)
	{
		field_name = read_cstring(&rbuf,mem_pool);
		index_num = read_int8(&rbuf);
		for(j=0;j < index_num; j++)
		{
			fprintf(stderr,"%-20s",field_name);
			fprintf(stderr,"%-20u",read_int8(&rbuf));
			fprintf(stderr,"%-20u",read_int8(&rbuf));
			fprintf(stderr,"%-20u\n",read_int8(&rbuf));
		}
	}

	return 0;
}

int execute_get_load_threshold_handle(struct data_buffer *dbuf, int socket_fd, MEM_POOL_PTR mem_pool)
{
	struct data_buffer rbuf;
	
	//发数据包
	if(socket_write(socket_fd,dbuf->data,dbuf->data_len) <= 0)
		return -1;

	//接受命名
	memset(&rbuf,0,sizeof(struct data_buffer));
	rbuf.data_len = sizeof(uint32_t);
	rbuf.size = rbuf.data_len;
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,sizeof(uint32_t));
	
	if(socket_read(socket_fd,rbuf.data,sizeof(uint32_t)) <= 0)
		return -1;

	rbuf.data_len = read_int32(&rbuf);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,rbuf.data_len);
	rbuf.rpos = 0;
	rbuf.size = rbuf.data_len;

	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;
	rbuf.rpos += MESSAGE_HEAD_LEN;

	uint16_t type = message_type(&rbuf);
	if (type != MT_DC_EXE_RS)
		return -1;

	double load = 0;
	read_bytes(&rbuf, (uint8_t *)&load, sizeof(load));

	fprintf(stderr, "%g\n", load);
	return 0;
}

int execute_set_load_threshold_handle(struct data_buffer *dbuf, int socket_fd, MEM_POOL_PTR mem_pool)
{
	struct data_buffer rbuf;
	
	//发数据包
	if(socket_write(socket_fd,dbuf->data,dbuf->data_len) <= 0)
		return -1;

	//接受命名
	memset(&rbuf,0,sizeof(struct data_buffer));
	rbuf.data_len = sizeof(uint32_t);
	rbuf.size = rbuf.data_len;
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,sizeof(uint32_t));
	
	if(socket_read(socket_fd,rbuf.data,sizeof(uint32_t)) <= 0)
		return -1;

	rbuf.data_len = read_int32(&rbuf);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,rbuf.data_len);
	rbuf.rpos = 0;
	rbuf.size = rbuf.data_len;

	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;
	rbuf.rpos += MESSAGE_HEAD_LEN;

	uint16_t type = message_type(&rbuf);
	if (type != MT_DC_EXE_RS)
		return -1;

	double load = 0;
	read_bytes(&rbuf, (uint8_t *)&load, sizeof(load));

	fprintf(stderr, "OLD VALUE: %g\n", load);
	return 0;
}

int execute_ldb_control_handle(struct data_buffer *dbuf, int socket_fd, MEM_POOL_PTR mem_pool)
{
	struct data_buffer rbuf;

	if (socket_write(socket_fd, dbuf->data, dbuf->data_len) <= 0)
		return -1;

	memset(&rbuf,0,sizeof(struct data_buffer));
	rbuf.data_len = sizeof(uint32_t);
	rbuf.size = rbuf.data_len;
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,sizeof(uint32_t));
	
	if(socket_read(socket_fd,rbuf.data,sizeof(uint32_t)) <= 0)
		return -1;

	rbuf.data_len = read_int32(&rbuf);
	rbuf.data = (uint8_t *)mem_pool_malloc(mem_pool,rbuf.data_len);
	rbuf.rpos = 0;
	rbuf.size = rbuf.data_len;

	if(socket_read(socket_fd,rbuf.data,rbuf.data_len) <= 0)
		return -1;
	rbuf.rpos += MESSAGE_HEAD_LEN;

	uint16_t type = message_type(&rbuf);
	if (type != MT_DC_LDB_CONTROL)
		return -1;

	// content
	char *content = read_cstring(&rbuf, mem_pool);
	if (content) {
		fprintf(stderr, "%s\n", content);
		return 0;
	}
	else 
		return -1;
}


void parse_cmd(char* cmd, int socket_fd, MEM_POOL * mem_pool)
{
	char* token = NULL;
	struct data_buffer* dbuf = NULL;
	
	if(cmd == NULL)
	{
		print_help(NULL);
		return;
	}

	token = strtok(cmd,"\r\n");

	if(token != NULL)
	{	
		char *full_command = strdup(token);
		token = strtok(token," ");
		if(strcmp(token,"ensure_index") == 0)
		{
			dbuf = gen_ensure_index_packet(cmd,mem_pool);
			if(dbuf != NULL)
				fprintf(stderr,"ensure_index命令，执行完毕，返回码:%d\n",execute_common_handle(dbuf,socket_fd,mem_pool));
			else
				print_help("ensure_index");
		}
		else if(strcmp(token,"del_index")==0)
		{
			dbuf = gen_del_index_packet(cmd,mem_pool);
			if(dbuf != NULL)
				fprintf(stderr,"del_index命令，执行完毕，返回码:%d\n",execute_common_handle(dbuf,socket_fd,mem_pool));
			else
				print_help("del_index");
		}
		else if(strcmp(token,"load")==0)
		{
			dbuf = gen_load_packet(cmd,mem_pool);
			if(dbuf != NULL)
				fprintf(stderr,"load命令，执行完毕，返回码:%d\n",execute_common_handle(dbuf,socket_fd,mem_pool));
			else 
				print_help("load");
		}
		else if(strcmp(token,"replace") == 0)
		{
			dbuf = gen_replace_packet(cmd,mem_pool);
			if(dbuf != NULL)
				fprintf(stderr,"replace命令，执行完毕，返回码:%d\n",execute_common_handle(dbuf,socket_fd,mem_pool));
			else
				print_help("replace");
		}
		else if(strcmp(token,"unload")==0)
		{
			dbuf = gen_unload_packet(cmd,mem_pool);
			if(dbuf != NULL)
				fprintf(stderr,"unload命令，执行完毕，返回码:%d\n",execute_common_handle(dbuf,socket_fd,mem_pool));
			else 
				print_help("unload");
		}
		else if(strcmp(token,"compress")==0)
		{
			dbuf = gen_compress_packet(cmd,mem_pool);
			if(dbuf != NULL)
				fprintf(stderr,"compress命令，执行完毕，返回码:%d\n",execute_common_handle(dbuf,socket_fd,mem_pool));
			else
				print_help("compress");
		}
		else if(strcmp(token,"stat")==0)
		{
			uint8_t type = 0;	
			int32_t ret = 0;
			dbuf = gen_stat_packet(cmd,&type,mem_pool);
			
			if(dbuf != NULL)
			{
				if(type == 1)
					ret = execute_segment_stat_handle(dbuf,socket_fd,mem_pool);
				else if(type == 2)
					ret = execute_index_stat_handle(dbuf,socket_fd,mem_pool);
				else
					fprintf(stderr,"stat执行失败，不支持的type类型\n");

				if(ret < 0)
					fprintf(stderr,"stat执行失败 %d\n",ret);
			}
			else
				print_help("stat");
		}	
		else if(strcmp(token, "checkpoint") == 0)
		{
			dbuf = gen_checkpoint_packet(cmd, mem_pool);
			if(dbuf != NULL) {
				execute_common_handle(dbuf, socket_fd, mem_pool);
				fprintf( stderr, "checkpoint complete.\n" );
			}
			else {
				print_help("checkpoint");
			}
		}
		else if(strcmp(token, "get_load_threshold") == 0)
		{
			dbuf = gen_get_load_threshold_packet(cmd, mem_pool);
			if(dbuf != NULL) {
				if (execute_get_load_threshold_handle(dbuf, socket_fd, mem_pool) != 0) {
					fprintf(stderr, "get_load_threshold failed.\n");
				}
			}
			else
				print_help("get_load_threshold");
		}
		else if(strcmp(token, "set_load_threshold") == 0)
		{
			dbuf = gen_set_load_threshold_packet(cmd, mem_pool);
			if(dbuf != NULL) {
				if (execute_set_load_threshold_handle(dbuf, socket_fd, mem_pool) != 0) {
					fprintf(stderr, "set_load_threshold failed.\n");
				}
			}
			else
				print_help("set_load_threshold");
		}
		else if (strcmp(token, "ldb") == 0)
		{
			dbuf = gen_ldb_control_packet(full_command, mem_pool);
			if (dbuf != NULL) {
				if (execute_ldb_control_handle(dbuf, socket_fd, mem_pool) != 0) {
					fprintf(stderr, "ldb control failed");
				}
			}
			else
				print_help("ldb");
		}
		else if(strcmp(token,"quit")==0)
		{
			global_start = 0;
		}
		else
		{
			print_help(NULL);
		}
		free(full_command);
	}

	destroy_data_buffer(dbuf);
	return;
}




int connect_docserver(char* addr)
{
	struct sockaddr_in ha_addr;
	char* ip; 
	int32_t port;
	int socket_fd;
	char* token = NULL;

	if(addr == NULL)
		return -1;
	
	token = strtok(addr,":");
	if(token == NULL)
		return -1;
	
	ip = token;
	
	token = strtok(NULL,":");
	if(token == NULL)
		return -1;
	
	port = atoi(token);
	
	if(!inet_aton(ip, (struct in_addr *)&ha_addr.sin_addr.s_addr)){
		log_error("ip address");
		return -1;
	}
	
	ha_addr.sin_family=AF_INET;
	ha_addr.sin_port=htons(port);
	bzero(&(ha_addr.sin_zero),8);
	
	if((socket_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		log_error("socket failure.");
		return -1;
	 }

	
	if(connect(socket_fd, (struct sockaddr *)&ha_addr, sizeof(struct sockaddr)) == -1) {
		log_error("connect failure, errno %d", errno);
		return -1;
	}

	return socket_fd;
}


int parse_cmd_line(int argc, char * argv[],MEM_POOL* mem_pool)
{
	int opt;
	int socket_fd = -1;
	const char *optstring = "c:l:h";
	struct option longopts[] = {
		{"config", 1, NULL, 'c'},
		{"help", 0, NULL, 'h'},
		{"line", 0, NULL, 'l'},
		{0, 0, 0, 0}
	};

	if(argc < 2)
	{
		print_usage(argv[0]);
	}

	opterr = 0;
	while ((opt = getopt_long(argc, argv, optstring, longopts, NULL)) != -1) {
		switch (opt) {
			case 'c':
				{
					if((socket_fd = connect_docserver(optarg)) < 0)
					{
						fprintf(stderr,"docserver 地址输入有误\n");
						return -1;
					}
					global_start = 1;
				}
				break;
			case 'l':
				{
					if(global_start == 1)
						parse_cmd(optarg,socket_fd,mem_pool);
					else
						print_usage(argv[0]);
					return 0;
				}
			case 'h':
				print_usage(argv[0]);
				return 0;
			default:
				print_usage(argv[0]);
				return 0;
		}
	}

	return socket_fd;
}


void sign_handler(int sig)
{
	switch (sig) {
		case SIGTERM:
		case SIGINT:
			global_start=0;
	}
}


int main(int argc, char* argv[])
{
	char* cmd = NULL;
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);
	int socket_fd;

	signal(SIGPIPE, SIG_IGN);
	signal(SIGHUP, SIG_IGN);
	signal(SIGINT, sign_handler);
	signal(SIGTERM, sign_handler);

	if((socket_fd = parse_cmd_line(argc, argv, mem_pool)) <= 0)
	{
		mem_pool_destroy(mem_pool);
		return 0;
	}
	
	while (global_start) {
		if ((cmd = readline("MILE>")) == NULL) {
			break;
		}
		parse_cmd(cmd,socket_fd,mem_pool);
		mem_pool_reset(mem_pool);
		if(cmd != NULL)
		{
			free(cmd);
			cmd = NULL;
		}
	}

	mem_pool_destroy(mem_pool);
	return 0;
}	

