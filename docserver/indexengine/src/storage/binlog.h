// hi_binlog.h : hi_binlog
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-06-29

#ifndef BINLOG_H
#define BINLOG_H

#include "../common/def.h"
#include "../common/mem.h"
#include <pthread.h>

#define BINLOG_INDEX_FILE_NAME "mile-bin.index"
#define IS_CONFIRM_RECORD(x) ((x)->op_code == OPERATION_CONFIRM_OK || (x)->op_code == OPERATION_CONFIRM_FAIL)

#define DEFAULT_BINLOG_FILESIZE (200 * 1024 * 1024L)  // 200MB
#define DEFAULT_BINLOG_DIR "./binlog"

enum binlog_record_flag {
	BINLOG_FLAG_COMPRESSED = 1
};

enum binlog_record_operation_code {
	OPERATION_CONFIRM_OK,
	OPERATION_CONFIRM_FAIL,
	OPERATION_INSERT,
	OPERATION_UPDATE,
	OPERATION_DELETE,
	OPERATION_INDEX,
	OPERATION_DINDEX,
	OPERATION_LOAD,
	OPERATION_UNLOAD,
	OPERATION_COMPRESS,
	OPERATION_LDB_DELETE,
	OPERATION_LDB_UPDATE,
	OPERATION_MAX
};

extern const char *operation_code_names[];

// slave get binlog request
struct slave_sync_req {
	uint64_t offset; // binlog offset
};

// slave get binlog response
struct slave_sync_res {
	uint32_t len; // binlog data length
	int32_t return_code; // return code
	uint64_t offset; //current offset
	uint64_t remain_length; // remain binlog lenght
	char data[]; // binlog data
} __attribute__((packed));

struct binlog_record {
	uint32_t len; // record length
	uint32_t time;    // time stamp
	uint16_t sid; // segment id
	uint32_t docid;   // doc id
	uint8_t flag;     // reserved for future use
	uint8_t op_code;  // operation code
	int8_t data[0];   // data
} __attribute__ ((packed));

struct binlog_file_item {
	uint32_t time; // first record time
	uint64_t abs_offset; // first record's absolute offset
	uint32_t suffix; // binlog file suffix
};

struct binlog_index_t {
	char dir[FILENAME_MAX_LENGTH]; // binlog save directory
	uint32_t last; // last item
	struct binlog_file_item *items; // binlog file array
	uint32_t item_array_len;
};

typedef struct binlog_writer {
	char dir[FILENAME_MAX_LENGTH];      // binlog save directory
	uint32_t max_size;    // max log file size
	int fd;     // current log fd.
	uint8_t sync_immediately; // sync immediately or not
	uint64_t abs_offset; // absolute offset
	struct binlog_index_t *index; // binlog file index ptr
	pthread_mutex_t mutex;
} BL_WRITER, *BL_WRITER_PTR;

typedef const BL_WRITER *CONST_WRITER_PTR;

typedef struct binlog_reader {
	char dir[FILENAME_MAX_LENGTH];
	uint32_t max_size;
	int fd;
	uint32_t suffix;
	uint32_t offset;

	// binlog file index ptr
	// if writer not NULL, refer to writer's index
	struct binlog_index_t *index;

	uint32_t index_item_pos; // current binlog index item array's pos

	CONST_WRITER_PTR writer; // check EOF through writer if not null
} BL_READER, *BL_READER_PTR;

/**
 *
 * create a binlog record
 * @param data_len data length, not the record length.
 * @param mem
 * @return
 */
struct binlog_record *create_binlog_record(int data_len, MEM_POOL_PTR mem);

/**
 * initialise binlog writer.
 * @param binlog_dir
 * @param max_size
 * @param mem
 * @return
 */
BL_WRITER_PTR binlog_writer_init(const char *binlog_dir, uint32_t max_size,
		MEM_POOL_PTR mem);

/**
 * destory binlog writer.
 */
void binlog_writer_destroy(BL_WRITER_PTR writer);

/*
 * lock writer
 */
int binlog_lock_writer(BL_WRITER_PTR writer);

/*
 * unlock writer
 */
int binlog_unlock_writer(BL_WRITER_PTR writer);

/**
 * write a record to binlog
 * @param writer
 * @param record
 * @param mem
 * @return On success, return zero, and update record's time field. On error, -1 is returned
 */
int binlog_write_record(BL_WRITER_PTR writer, struct binlog_record *record, MEM_POOL_PTR mem);

/**
 * write confirm ok record to binlog
 * @param writer
 * @param mem
 */
int binlog_confirm_ok(BL_WRITER_PTR writer, MEM_POOL_PTR mem);

/**
 * write confirm fail record to binlog
 * @param writer
 * @param mem
 */
int binlog_confirm_fail(BL_WRITER_PTR writer, MEM_POOL_PTR mem);

/**
 * sync binlog data to disk.
 * @param writer
 * @return On success, return zero. On error, -1 is returned
 */
int binlog_sync(BL_WRITER_PTR writer);

/**
 * initialise binlog reade by absolute offset.
 * @param binlog_dir
 * @param max_size
 * @param absolute_offset offset of all binlog data.
 * @param writer
 * @param mem
 * @return On error, NULL is returned
 */
BL_READER_PTR binlog_reader_init_byoffset(const char *binlog_dir, uint32_t max_size, uint64_t absolute_offset,
		CONST_WRITER_PTR writer, MEM_POOL_PTR mem);

/**
 * initialise binlog reader
 * seek the reader from the end to the point where the previous record is the newest record that before the given time.
 * @param binlog_dir
 * @param max_size
 * @param time
 * @param mem
 * @return On error, NULL is returned
 */
BL_READER_PTR binlog_reader_init_bytime(const char *binlog_dir, uint32_t max_size, uint32_t time, MEM_POOL_PTR mem);

/**
 * destroy binlog reader.
 * @param reader
 */
void binlog_reader_destroy(BL_READER_PTR reader);

/**
 * read a record
 * @param reader
 * @param record
 * @return read record nubmer. On success read, return 1. On binlog end, return 0. On error, return -1;
 */
int binlog_read_record(BL_READER_PTR reader, struct binlog_record **record, MEM_POOL_PTR mem);

/**
 * read data_len length data.
 * @param reader
 * @param data
 * @param data_len
 * @return read data length. On success read, return read data length ( 0 indicates end of binlog). On error, return -1;
 */
int binlog_read_data(BL_READER_PTR reader, char *data, uint32_t data_len, MEM_POOL_PTR mem);

/**
 * get reader's remain data length
 * @param reader
 * @return reamin data lenght. On error -1 is returned.
 */
int64_t binlog_remain_data_len(BL_READER_PTR reader);

/**
 * get reader position
 * @return absolute_offset
 */
int64_t binlog_get_position(BL_READER_PTR reader);

/**
 * check reader position
 * @return On match 1. Mismatch 0.
 */
int binlog_check_position(BL_READER_PTR reader, uint64_t absolute_offset);

/**
 * check whether binlog reader read all data of current file
 * @param reader
 * @return reader read all data return 1, has data left return 0, on error return -1
 */
int binlog_is_read_all(BL_READER_PTR reader);

/**
 * Execute slave sync request. Read binlog data from bl_reader to slave_sync_res
 */
struct slave_sync_res *execute_slave_sync(MEM_POOL_PTR mem, struct slave_sync_req *packet,
		void **bl_reader, MEM_POOL_PTR session_mem);

struct binlog_index_t *binlog_index_init(const char *binlog_dir, MEM_POOL_PTR mem);

#endif // BINLOG_H

