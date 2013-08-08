// hi_binlog.c : hi_binlog
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-06-29

#include "binlog.h"
#include "../common/log.h"
#include "../common/stat_collect.h"
#include "../common/file_op.h"
#include "../common/ConfigFile.h"
#include "StorageEngine.h"
#include <unistd.h>
#include <fnmatch.h>
#include <dirent.h>
#include <stdio.h>
#include <ctype.h>

#define INVALID_FILE_FD (-1)
#define BINLOG_FILE_NAME "mile-bin.log"
#define BINLOG_FILE_SUFFIX_FORMATTER "%u"

#define errno_saved_log_error(...) do { int errno_saver = errno; \
										log_error(__VA_ARGS__); errno = errno_saver; } while (0)

const char *operation_code_names[] = {
	"CONFIRM_OK",
	"CONFIRM_FAIL",
	"INSERT",
	"UPDATE",
	"DELETE",
	"INDEX",
	"DINDEX",
	"LOAD",
	"UNLOAD",
	"COMPRESS",
	"LDB_DELETE",
	"LDB_UPDATE"
};

struct reader_init_param_t {
	int flag;

	uint64_t offset;
	uint32_t time;
};

enum {
	READER_INIT_BY_OFFSET,
	READER_INIT_BY_TIME
};


static int open_binlog_file(const char *dir, uint32_t suffix, int flags);   // set errno when failed.
static int seek_to_time(BL_READER_PTR reader, uint32_t time);

static ssize_t while_write(int fd, void *buf, size_t count);
static ssize_t while_pread(int fd, void *buf, size_t count, off_t offset);

// add binlog index item to item array
static void add_item_array(struct binlog_index_t *index, const struct binlog_file_item *item);
// add itme to index file
static int add_file_item(struct binlog_index_t *index, const struct binlog_file_item *item);
static int load_index_file(struct binlog_index_t *index);

static int binlog_confirm(BL_WRITER_PTR writer, int op_code, MEM_POOL_PTR mem);

/**
 * initialise binlog reader.
 * @param binlog_dir
 * @param max_size
 * @param writer binlog writer. Set to NULL, if has no writer
 * @param mem
 * @param param init param, indicate init by offset or time
 * @return on error, NULL is returned
 */
static BL_READER_PTR binlog_reader_init(const char *binlog_dir, uint32_t max_size, CONST_WRITER_PTR writer,
		MEM_POOL_PTR mem, struct reader_init_param_t *param);



static void add_item_array(struct binlog_index_t *index, const struct binlog_file_item *item)
{
	// make item array long enough
	void *old_mem = NULL;

	if (index->last == index->item_array_len - 1) {
		uint32_t new_len = index->item_array_len + 1024;
		struct binlog_file_item *items = (struct binlog_file_item *)malloc(sizeof(struct binlog_file_item) * new_len);
		if (index->items != NULL) {
			memcpy(items, index->items, index->item_array_len * sizeof(struct binlog_file_item));
			old_mem = index->items;
		}
		index->items = items;
		index->item_array_len = new_len;
	}
	memcpy(index->items + (index->last + 1), item, sizeof(*item));
	if (old_mem)
		free(old_mem);

	index->last++;
}

static ssize_t while_write(int fd, void *buf, size_t count)
{
	size_t done = 0;

	while (done < count) {
		sc_record_value(STAT_ITEM_BINLOG_WRITE, count - done);
		ssize_t n = write(fd, (char *)buf + done, count - done);
		if (n < 0 && errno != EINTR) {
			errno_saved_log_error("write to %d failed, errno %d", fd, errno);
			return -1;
		}
		else {
			done += n;
		}
	}
	return done;
}

static ssize_t while_pread(int fd, void *buf, size_t count, off_t offset)
{
	size_t done = 0;

	while (done < count) {
		ssize_t n = pread(fd, (char *)buf + done, count - done, offset + done);
		if (n < 0 && errno != EINTR) {
			errno_saved_log_error("write to %d failed, errno %d", fd, errno);
			return -1;
		}
		else if (0 == n) {
			break;
		}
		else {
			done += n;
		}
	}
	return done;
}

static int add_file_item(struct binlog_index_t *index, const struct binlog_file_item *item)
{
	char file[FILENAME_MAX_LENGTH];

	snprintf(file, FILENAME_MAX_LENGTH, "%s/" BINLOG_INDEX_FILE_NAME, index->dir);
	int fd = open_file(file, O_CREAT | O_SYNC | O_WRONLY | O_APPEND);
	if (fd < 0) {
		return -1;
	}

	const static int buf_len = 128;
	char buf[buf_len];
	int n = snprintf(buf, buf_len, "%u\t%llu\t%u\n", item->time, item->abs_offset, item->suffix);
	int rc = while_write(fd, buf, n);
	if (rc < 0) {
		log_error("write %s failed, errno %d", file, errno);
	}
	close(fd);

	add_item_array(index, item);
	return rc < 0 ? -1 : 0;
}

static int load_index_file(struct binlog_index_t *index)
{
	char file[FILENAME_MAX_LENGTH];

	snprintf(file, FILENAME_MAX_LENGTH, "%s/" BINLOG_INDEX_FILE_NAME, index->dir);
	FILE *fp = fopen(file, "a+"); // open for read and append, but never write
	if (NULL == fp) {
		log_error("open %s failed, errno %d", file, errno);
		return -1;
	}

	char *line = NULL;
	size_t line_len = 0;
	ssize_t read;

	int rc = 0;
	int line_number = 1;
	// read and parse binlog_file_item
	for (; (read = getline(&line, &line_len, fp)) != -1; line_number++) {
		char *saveptr = NULL;
		const char *delim = " \f\n\r\t\v";

		const int value_len = 3;
		int i = 0, is_invalid_line = 0;
		int64_t values[value_len];
		for (i = 0; i < value_len; i++) {
			char *p = strtok_r(0 == i ? line : NULL, delim, &saveptr);
			if (0 == i && (0 == p || '#' == *p) ) { // empty line or comment
				is_invalid_line = 1;
				break;
			}
			if (NULL == p) {
				log_error("not enough token in index file line %d", line_number);
				rc = -1; break;
			}
			values[i] = strtoll(p, NULL, 10);
		}
		if (is_invalid_line)
			continue;
		if (rc < 0)
			break;

		struct binlog_file_item item;
		item.time = values[0];
		item.abs_offset = values[1];
		item.suffix = values[2];

		log_debug("get item, time: %u, abs_offset: %llu, suffix: %u", item.time, item.abs_offset, item.suffix);
		add_item_array(index, &item);
	}
	if (NULL != line)
		free(line), line = NULL;
	fclose(fp);

	return rc;
}

struct binlog_index_t *binlog_index_init(const char *binlog_dir, MEM_POOL_PTR mem)
{
	struct binlog_index_t *index = (struct binlog_index_t *)mem_pool_malloc(mem, sizeof(struct binlog_index_t));

	memset(index, 0, sizeof(*index));
	strncpy(index->dir, binlog_dir, sizeof(index->dir) - 1);

	index->last = -1;
	index->item_array_len = 0;
	index->items = NULL;

	if (load_index_file(index) != 0) {
		if (index->items != NULL)
			free(index->items);
		return NULL;
	}

	return index;
}

void binlog_index_destroy(struct binlog_index_t *index)
{
	if (index->items != NULL)
		free(index->items), index->items = NULL;
}


struct binlog_record *create_binlog_record(int data_len, MEM_POOL_PTR mem)
{
	struct binlog_record *record = (struct binlog_record *)mem_pool_malloc(mem, sizeof( struct binlog_record ) + data_len);

	memset(record, 0, sizeof( struct binlog_record ) + data_len);
	record->len = data_len + sizeof( struct binlog_record );
	return record;
}

int binlog_sync(BL_WRITER_PTR writer)
{
	if (fsync(writer->fd) != 0) {
		errno_saved_log_error("fsync log failed, errno %d", errno);
		return -1;
	}
	return 0;
}


BL_WRITER_PTR binlog_writer_init(const char *binlog_dir, uint32_t max_size, MEM_POOL_PTR mem)
{
	BL_WRITER_PTR writer = (BL_WRITER_PTR)mem_pool_malloc(mem, sizeof(BL_WRITER) );

	memset(writer, 0, sizeof( BL_WRITER ) );
	strncpy(writer->dir, binlog_dir, FILENAME_MAX_LENGTH - 1);
	writer->max_size = max_size;

	if ( (writer->index = binlog_index_init(binlog_dir, mem) ) == NULL) {
		log_error("init binlog index file failed");
		return NULL;
	}

	uint32_t suffix = writer->index->last == (uint32_t)-1 ? 0 : writer->index->items[writer->index->last].suffix;

	writer->fd = open_binlog_file(writer->dir, suffix, O_WRONLY | O_CREAT | O_APPEND |
			(writer->sync_immediately ? O_SYNC : 0 ) );
	if (INVALID_FILE_FD == writer->fd) {
		log_error("open binlog file failed, errno %d", errno);
		binlog_index_destroy(writer->index);
		return NULL;
	}

	off_t offset = lseek(writer->fd, 0, SEEK_END);
	if (-1 == offset) {
		log_error("lseek failed , errno %d", errno);
		binlog_index_destroy(writer->index);
		return NULL;
	}
	// current file's offset + absolute offset of the first record of this file
	writer->abs_offset = offset + (writer->index->last == (uint32_t)-1 ? 0 : writer->index->items[writer->index->last].abs_offset);
	pthread_mutex_init(&writer->mutex, NULL);
	return writer;
}

static int writer_goto_next(BL_WRITER_PTR writer, uint32_t time, MEM_POOL_PTR mem)
{
	int is_first = (writer->index->last == (uint32_t)-1); // the first binlog file

	if (!is_first) {
		// sync old binlog file
		if ( (!writer->sync_immediately) && fsync(writer->fd) < 0) {
			log_error("fsync failed, errno %d", errno);
			return -1;
		}
	}

	// add index item
	struct binlog_file_item item;
	item.time = time;
	if (is_first) {
		item.abs_offset = 0;
		item.suffix = 0;
	}
	else {
		item.abs_offset = writer->abs_offset;
		item.suffix = writer->index->items[writer->index->last].suffix + 1;
	}

	if (add_file_item(writer->index, &item) != 0) {
		return -1;
	}

	// open new binlog file
	if (!is_first) {
		int fd = open_binlog_file(writer->dir, item.suffix, O_WRONLY | O_CREAT | O_APPEND |
				(writer->sync_immediately ? O_SYNC : 0 ) );
		if (INVALID_FILE_FD == fd) {
			log_error("open binlog file failed, errno %d", errno);
			return -1;
		}

		int tmp = writer->fd;
		writer->fd = fd;
		close(tmp);
	}

	return 0;
}

int binlog_lock_writer(BL_WRITER_PTR writer)
{
	if (!writer)
		return -1;
	return pthread_mutex_lock(&writer->mutex) == 0 ? 0 : -1;
}

int binlog_unlock_writer(BL_WRITER_PTR writer)
{
	if (!writer)
		return -1;
	return pthread_mutex_unlock(&writer->mutex) == 0 ? 0 : -1;
}

int binlog_write_record(BL_WRITER_PTR writer, struct binlog_record *record, MEM_POOL_PTR mem)
{
	char *data_ptr = NULL;
	uint32_t len = 0;
	if(record == NULL)
		return -1;

	data_ptr = (char *)record;
	len = record->len;
	record->time = time(NULL);

	if (writer->index->last == (uint32_t)-1 ||
			len + writer->abs_offset - writer->index->items[writer->index->last].abs_offset > writer->max_size) {  // write index file
		if (writer_goto_next(writer, record->time, mem) != 0) {
			log_error("switch to next binlog file failed.");
			return -1;
		}
	}
	if (while_write(writer->fd, data_ptr, len) < 0) {
		log_error("writer file failed, errno %d", errno);
		return -1;
	}
	writer->abs_offset += len;
	return 0;
}

static int binlog_confirm(BL_WRITER_PTR writer, int op_code, MEM_POOL_PTR mem)
{
	struct binlog_record *record = create_binlog_record(0, mem);

	record->op_code = op_code;
	return binlog_write_record(writer, record, mem);
}

int binlog_confirm_ok(BL_WRITER_PTR writer, MEM_POOL_PTR mem)
{
	return binlog_confirm(writer, OPERATION_CONFIRM_OK, mem);
}

int binlog_confirm_fail(BL_WRITER_PTR writer, MEM_POOL_PTR mem)
{
	return binlog_confirm(writer, OPERATION_CONFIRM_FAIL, mem);
}

static int open_binlog_file(const char *dir, uint32_t suffix, int flags)
{
	assert(NULL != dir);
	char file[FILENAME_MAX_LENGTH];
	snprintf(file, FILENAME_MAX_LENGTH, "%s/" BINLOG_FILE_NAME "." BINLOG_FILE_SUFFIX_FORMATTER,
			dir, suffix);
	int rc = open(file, flags, 0600);
	if (rc < 0) {
		int errno_bak = errno;
		log_warn("open binlog file failed, path [%s], errno %d", file, errno);
		errno = errno_bak;
	}
	else if (O_WRONLY & flags) {
		log_info("open binlog file [%s] for write", file);
	}
	return rc;
}

void binlog_writer_destroy(BL_WRITER_PTR writer)
{
	if (NULL == writer)
		return;
	close(writer->fd);
	writer->fd = INVALID_FILE_FD;
	if (NULL != writer->index)
		binlog_index_destroy(writer->index);
}

BL_READER_PTR binlog_reader_init(const char *binlog_dir, uint32_t max_size, CONST_WRITER_PTR writer,
		MEM_POOL_PTR mem, struct reader_init_param_t *param)
{
	BL_READER_PTR reader = (BL_READER_PTR)mem_pool_malloc(mem, sizeof(BL_READER) );

	memset(reader, 0, sizeof( BL_READER) );
	strncpy(reader->dir, binlog_dir, FILENAME_MAX_LENGTH - 1);
	reader->max_size = max_size;
	reader->writer = writer;

	if (NULL != writer) {
		reader->index = writer->index;
	}
	else {
		if ( (reader->index = binlog_index_init(binlog_dir, mem) ) == NULL) {
			log_error("init binlog index file failed");
			return NULL;
		}
	}

	reader->suffix = 0;
	reader->offset = 0;
	reader->index_item_pos = 0;

	if (param->flag == READER_INIT_BY_OFFSET) {  // init by offset
		if (reader->index->last != (uint32_t)-1) {
			uint32_t i = 0;
			for (; i <= reader->index->last && reader->index->items[i].abs_offset <= param->offset; i++)
				;
			if (i > 0) i--;
			reader->suffix = reader->index->items[i].suffix;
			reader->offset = param->offset - reader->index->items[i].abs_offset;
			reader->index_item_pos = i;
		}
		else {
			reader->offset = param->offset;
		}
	}
	else if (param->flag == READER_INIT_BY_TIME) {    // init by time, seek to file
		if (reader->index->last != (uint32_t)-1) {
			uint32_t i = 0;
			for (; i <= reader->index->last && reader->index->items[i].time < param->time; i++)
				;
			if (i > 0) i--;
			reader->suffix = reader->index->items[i].suffix;
			reader->index_item_pos = i;
		}
	}

	reader->fd = open_binlog_file(reader->dir, reader->suffix, O_RDONLY | O_CREAT);
	if (INVALID_FILE_FD == reader->fd) {
		log_error("open binlog file failed, errno %d", errno);
		if (NULL == writer) {
			binlog_index_destroy(reader->index);
		}
		return NULL;
	}

	if (param->flag == READER_INIT_BY_TIME) {  // init by time, seek to file's offset
		if (seek_to_time(reader, param->time) != 0) {
			log_error("seek reader to time %u, failed", param->time);
			if (NULL == writer) {
				binlog_index_destroy(reader->index);
			}
			close(reader->fd);
			return NULL;
		}
	}

	return reader;
}

int seek_to_time(BL_READER_PTR reader, uint32_t time)
{
	struct binlog_record *record = NULL;
	MEM_POOL_PTR mem = mem_pool_init(MB_SIZE * 10);   // TODO
	int rc = 0;

	while (1) {
		rc = binlog_read_record(reader, &record, mem);
		if (rc < 0 || rc == 0) {  // error or end
			break;
		}

		if (record->time >= time) {
			assert(reader->offset >= record->len);
			reader->offset -= record->len; // go back one record
			break;
		}
		mem_pool_reset(mem);   // reset mem after befor record read
	}
	mem_pool_destroy(mem);
	return rc < 0 ? -1 : 0;
}

void binlog_reader_destroy(BL_READER_PTR reader)
{
	if (NULL == reader)
		return;

	close(reader->fd);
	reader->fd = INVALID_FILE_FD;
	if (NULL == reader->writer && NULL != reader->index)
		binlog_index_destroy(reader->index);
}


int binlog_read_data(BL_READER_PTR reader, char *data, uint32_t data_len, MEM_POOL_PTR mem)
{
	if (NULL != reader->writer) {
		int64_t rpos = (reader->index->last ==(uint32_t) -1 ? 0 : reader->index->items[reader->index_item_pos].abs_offset) + (int64_t)reader->offset;
		// less than sizeof(record length)
		if (reader->writer->abs_offset - rpos < 4) {
			log_debug("catch the writer");
			return 0;
		}
	}
	int n = while_pread(reader->fd, data, data_len, reader->offset);
	if (n < 0) {
		log_error("read data failed, errno %d", errno);
		return -1;
	}
	if ((uint32_t)n == data_len) {
		reader->offset += n;
		return n;
	}

	// n < data_len
	int pre_read_len = n;
	reader->offset += n;
	if (reader->index_item_pos == reader->index->last || reader->index->last == (uint32_t)-1) {
		log_debug("reader reach the end");
		return n;
	}

	// go to next file.
	data_len -= n;
	data += n;
	int fd = open_binlog_file(reader->dir, reader->suffix + 1, O_RDONLY);
	if (fd < 0) {
		if (ENOENT == errno)
			return n;
		log_error("open file to read failed, errno %d", errno);
		return -1;
	}
	reader->offset = 0;
	reader->suffix++;
	reader->index_item_pos++;
	close(reader->fd);
	reader->fd = fd;

	n = while_pread(reader->fd, data, data_len, reader->offset);
	if (n < 0) {
		log_error("read data failed, errno %d", errno);
		return -1;
	}
	reader->offset += n;
	if ((uint32_t)n < data_len) {
		log_debug("reader reach the end");
	}

	return pre_read_len + n;
}


int binlog_read_record(BL_READER_PTR reader, struct binlog_record **record, MEM_POOL_PTR mem)
{
	uint32_t record_len = 0;
	const int sizeof_rlen = sizeof( (*record)->len );
	int n = binlog_read_data(reader, (char *)&record_len, sizeof_rlen, mem);

	if (n < 0) {
		log_error("read record failed.");
		return -1;
	}
	else if (sizeof_rlen != n) {
		reader->offset -= n; // go back n
		return 0;
	}

	*record = (struct binlog_record *)mem_pool_malloc(mem, record_len);
	(*record)->len = record_len;

	n = binlog_read_data(reader, (char *)*record + sizeof_rlen, record_len - sizeof_rlen, mem);
	if (n < 0) {
		log_error("read record failed.");
		return -1;
	}
	else if ((uint32_t)n != record_len - sizeof_rlen) {
		reader->offset -= sizeof_rlen + n; // go back to record beginning
		return 0;
	}
	return 1;
}


BL_READER_PTR binlog_reader_init_byoffset(const char *binlog_dir, uint32_t max_size, uint64_t absolute_offset, CONST_WRITER_PTR writer, MEM_POOL_PTR mem)
{
	struct reader_init_param_t param;

	param.flag = READER_INIT_BY_OFFSET;
	param.offset = absolute_offset;

	return binlog_reader_init(binlog_dir, max_size, writer, mem, &param);
}

BL_READER_PTR binlog_reader_init_bytime(const char *binlog_dir, uint32_t max_size, uint32_t time, MEM_POOL_PTR mem)
{
	struct reader_init_param_t param;

	param.flag = READER_INIT_BY_TIME;
	param.time = time;

	return binlog_reader_init(binlog_dir, max_size, NULL, mem, &param);
}

int binlog_is_read_all(BL_READER_PTR reader)
{
	struct stat st;

	if (fstat(reader->fd, &st) != 0) {
		log_error("fstat binlog file failed, fd %d, errno %d", reader->fd, errno);
		return -1;
	}

	if ((uint32_t)st.st_size == reader->offset) {
		return 1;
	}
	else {
		log_error("CRITICAL ERROR binlog file contain unknown data. suffix %d, file size %lld, reader offset %u",
				reader->index->last == (uint32_t)-1 ? 0 : reader->index->items[reader->index_item_pos].suffix,
				(int64_t)st.st_size, reader->offset);
		return 0;
	}
}

int64_t binlog_remain_data_len(BL_READER_PTR reader)
{
	if (NULL == reader->writer) {
		log_error("writer should be set.");
		return -1;
	}

	int64_t rpos = binlog_get_position(reader);

	return reader->writer->abs_offset - rpos;
}

int64_t binlog_get_position(BL_READER_PTR reader)
{
	return (reader->index->last == (uint32_t)-1 ? 0 : reader->index->items[reader->index_item_pos].abs_offset) + (int64_t)reader->offset;
}

int binlog_check_position(BL_READER_PTR reader, uint64_t absolute_offset)
{
	if ((uint64_t)binlog_get_position(reader) == absolute_offset)
		return 1;
	else
		return 0;
}

struct slave_sync_res *execute_slave_sync(MEM_POOL_PTR mem, struct slave_sync_req *packet,
		void **bl_reader, MEM_POOL_PTR session_mem)
{
	assert(NULL != bl_reader && NULL != session_mem);
	struct slave_sync_res *res = (struct slave_sync_res *)mem_pool_malloc(mem,
			sizeof(struct slave_sync_res) + MAX_BINLOG_DATA_PER_PACKET);
	memset(res, 0, sizeof(struct slave_sync_res) + MAX_BINLOG_DATA_PER_PACKET);

	// init reader at the first request.
	BL_READER_PTR reader = (BL_READER_PTR)*bl_reader;
	if (NULL == reader || !binlog_check_position(reader, packet->offset) ) {
		if (NULL != reader) {
			log_warn("slave and reader's offset mismatch, slave offset %llu, reader offset %llu",
					packet->offset, binlog_get_position(reader) );
			binlog_reader_destroy(reader);
			mem_pool_reset(session_mem);
			*bl_reader = NULL;
		}

		const char *dir = ConfigFile::GlobalInstance()->GetValue(CONF_SERVER_SESSION, "binlog_dir", DEFAULT_BINLOG_DIR);
		uint32_t max_size = ConfigFile::GlobalInstance()->GetIntValue(CONF_SERVER_SESSION, "binlog_maxsize", DEFAULT_BINLOG_FILESIZE);
		reader = binlog_reader_init_byoffset(dir, max_size, packet->offset,
				StorageEngine::storage->GetBinlogWriter(), session_mem);
		if (NULL == reader) {
			log_error("init binlog reader failed");
			res->return_code = ERROR_BINLOG_FAILED;
			return res;
		}
		assert(binlog_check_position(reader, packet->offset) );
		*bl_reader = reader;
	}

	int n = binlog_read_data(reader, res->data, MAX_BINLOG_DATA_PER_PACKET, mem);
	if (n < 0) {
		log_error("read binlog file failed, errno %d", errno);
		res->return_code = ERROR_BINLOG_FAILED;
		return res;
	}
	res->len = n;
	res->remain_length = binlog_remain_data_len(reader);
	res->offset = binlog_get_position(reader);
	res->return_code = 0;
	return res;
}

