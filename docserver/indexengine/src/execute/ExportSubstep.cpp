// ExportSubstep.cpp : ExportSubstep
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-30

#include "ExportSubstep.h"

#include "../common/file_op.h"

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <inttypes.h>

#include <string>

static int append_row(std::string *str, const struct row_data *row);

template <typename T>
void str_fmt_append(std::string *str, const char *fmt, T t)
{
	const int buf_size = 128;
	char buf[buf_size];

	int n = snprintf(buf, buf_size, fmt, t);
	if (n < 0)
		return;
	str->append(buf, std::min(n, buf_size) );
}

static int append_value(std::string *str, low_data_struct *low);
static void append_escape_string(std::string *str, const char *data, size_t len);

ExportSubstep::ExportSubstep(char *table, char *path, int64_t limit)
	: table_name_(table), path_(path), fd_(-1), limit_(limit) {}
ExportSubstep::~ExportSubstep()
{
	if (fd_ >= 0)
		close(fd_);
}

int ExportSubstep::Init(void)
{
	if (!path_) {
		log_error("no path");
		return -1;
	}

	// dangerous, sql injection
	std::string cmd = std::string("mkdir -p \"$(dirname '") + path_ + "')\"";
	system(cmd.c_str()); // ignore return code, the following open_file will report error if exist.

	fd_ = open_file(path_, O_CREAT | O_TRUNC | O_APPEND | O_WRONLY);

	return fd_ >= 0 ? 0 : -1;
}

int32_t ExportSubstep::Execute(TableManager *table, MileHandler *handler, void *output, MEM_POOL_PTR mem_pool)
{
	int64_t *export_num = (int64_t *)output;
	if (limit_ > 0 && *export_num >= limit_) {
		// break MainStep's loop.
		return WARN_EXCEED_QUERY_LIMIT;
	}

	out_buf_.clear();
	out_buf_.append("INSERT INTO ");
	out_buf_.append(table_name_);

	struct row_data *row = table->GetRowData(handler, mem_pool);
	if (!row) {
		log_error("No row value");
		return -1;
	}
	int rc = append_row(&out_buf_, row);
	if (rc)
		return -1;
	out_buf_.append("\n");

	rc = unintr_write(fd_, out_buf_.data(), out_buf_.size());
	if (rc < 0 || (size_t)rc != out_buf_.size()) {
		log_error("write to %s failed, errno %d", path_, errno);
		return -1;
	}

	(*export_num)++;

	return 0;
}

static int append_row(std::string *str, const struct row_data *row)
{
	for (uint16_t i = 0; i <  row->field_count; i++) {
		low_data_struct *low = &row->datas[i];

		str->append(" ");
		str->append(low->field_name);
		str->append("=\"");
		const char *type_name = g_type_name_map.get(low->type);
		if (!type_name) {
			log_error("unknown type %d", low->type);
			return -1;
		}
		str->append(type_name);
		str->append(":");
		append_value(str, low);
		str->append("\"");
	}

	return 0;
}

static int append_value(std::string *str, low_data_struct *low)
{
	switch (low->type) {
	case HI_TYPE_NULL:
		break;
	case HI_TYPE_TINY:
		str_fmt_append(str, "%d", int(*((uint8_t *)(low->data))) );
		break;
	case HI_TYPE_SHORT:
		str_fmt_append(str, "%d", int(*((int16_t *)(low->data))) );
		break;
	case HI_TYPE_UNSIGNED_SHORT:
		str_fmt_append(str, "%d", int(*((uint16_t *)(low->data))) );
		break;
	case HI_TYPE_LONG:
		str_fmt_append(str, "%d", *((int32_t *)(low->data)) );
		break;
	case HI_TYPE_UNSIGNED_LONG:
		str_fmt_append(str, "%u", *((uint32_t *)(low->data)) );
	case HI_TYPE_LONGLONG:
		str_fmt_append(str, "%" PRIi64, *((int64_t *)(low->data)) );
		break;
	case HI_TYPE_UNSIGNED_LONGLONG:
		str_fmt_append(str, "%" PRIu64, *((uint64_t *)(low->data)) );
		break;
	case HI_TYPE_FLOAT:
		str_fmt_append(str, "%f", *((float *)(low->data)) );
		break;
	case HI_TYPE_DOUBLE:
		str_fmt_append(str, "%f", *((double *)(low->data)) );
		break;
	case HI_TYPE_STRING:
		append_escape_string(str, (const char *)low->data, low->len);
		break;
	default:
		log_error("unknown type %d", low->type);
		return -1;
	}

	return 0;
}

static void append_escape_string(std::string *str, const char *data, size_t len)
{
	for (size_t i = 0; i < len; i++) {
		if (data[i] == '\"' || data[i] == '\\')
			str->append(1, '\\');
		str->append(1, data[i]);
	}
}

