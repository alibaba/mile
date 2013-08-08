// milebinlog.cpp : milebinlog
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-07-04

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <getopt.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <inttypes.h>

#include <string>
#include <iostream>
#include <set>
#include <vector>
#include <boost/algorithm/string.hpp>
#include <boost/lexical_cast.hpp>

#include "common/def.h"
#include "common/mem.h"
#include "common/log.h"
#include "storage/binlog.h"
#include "storage/docdb/db.h"

static int string_append(std::string &str, const char *fmt, ...) __attribute__((format(printf, 2, 3)));
static int string_append_data(std::string &str, enum field_types type, void *data, uint32_t len);

struct dump_opt_t {
	uint64_t dump_count; // dump record count
	bool only_header; // only dump binlog record header
	bool ignore_confirm_rec; // ignore confirm record
	bool go_on; // dump specify file and the following files
	bool type_name; // print type name, before value
	uint16_t sid; // segment id

	std::string docid_range; // docid range
	std::string time_range; // timestamp range

	std::string type; // binlog type (op_code), separate by ','

	std::string log_level;

	// only set default value
	dump_opt_t() : dump_count(-1), only_header(false), ignore_confirm_rec(true), go_on(false), type_name(false), sid(-1)
	{
		log_level = "WARN";
	}
};

struct record_filter_t {
public:
	record_filter_t(const struct dump_opt_t &opt) : sid_(opt.sid), ignore_confirm_rec_(opt.ignore_confirm_rec)
	{
		// set docid range
		std::vector< std::string > tmp;
		docid_range_.first = 0;
		docid_range_.second = -1;
		if (!opt.docid_range.empty()) {
			boost::split(tmp, opt.docid_range, boost::is_any_of(",-~"), boost::token_compress_on);
			docid_range_.first = boost::lexical_cast< uint32_t >(tmp.at(0));
			if (tmp.size() > 1)
				docid_range_.second = boost::lexical_cast< uint32_t >(tmp.at(1));
		}

		// set time range
		time_range_.first = 0;
		time_range_.second = -1;
		if (!opt.time_range.empty()) {
			tmp.clear();
			boost::split(tmp, opt.time_range, boost::is_any_of(",-~"), boost::token_compress_on);
			time_range_.first = boost::lexical_cast< uint32_t >(tmp.at(0));
			if (tmp.size() > 1)
				time_range_.second = boost::lexical_cast< uint32_t >(tmp.at(1));
		}

		// set types (operation codes)
		if (!opt.type.empty()) {
			tmp.clear();
			boost::split(tmp, opt.type, boost::is_any_of(",;"), boost::token_compress_on);
			for (std::vector< std::string >::const_iterator it = tmp.begin(); it != tmp.end(); ++it) {
				for (int i = 0; i < OPERATION_MAX; i++) {
					if (boost::iequals(*it, operation_code_names[i])) {
						types_.insert(i);
						break;
					}
					if (i + 1 == OPERATION_MAX) // op_code not defined
						log_warn("operation code %s not defined", it->c_str());
				}
			}
		}
	}

	bool operator () (const struct binlog_record &rec)
	{
		if (sid_ != (uint16_t)-1 && rec.sid != sid_)
			return false;

		if (rec.docid < docid_range_.first || rec.docid > docid_range_.second)
			return false;

		if (rec.time < time_range_.first || rec.time > time_range_.second)
			return false;

		if (!types_.empty() && types_.find(rec.op_code) == types_.end())
			return false;

		if (ignore_confirm_rec_ &&
				(rec.op_code == OPERATION_CONFIRM_OK || rec.op_code == OPERATION_CONFIRM_FAIL))
			return false;

		return true;
	}
private:
	uint16_t sid_;
	bool ignore_confirm_rec_;
	std::pair< uint32_t, uint32_t > docid_range_;
	std::pair< uint32_t, uint32_t > time_range_;
	std::set< int > types_;
};

class BinlogDumper {
public:
	BinlogDumper(const char *dir, uint32_t size, uint32_t index, const dump_opt_t &opt)
		: opt_(opt), filter_(opt), dir_(dir), binlog_file_size_(size), binlog_index_(index)
	{
		mem_ = mem_pool_init(MB_SIZE);
		init_profile(1000, mem_); // init profile to avoid core dump.

		int seperator_size = 80;
		struct winsize win;
		if (ioctl(STDOUT_FILENO, TIOCGWINSZ, (char *)&win) == 0)
			seperator_size = win.ws_col > 0 ? win.ws_col - 1 : 0;

		seperator_.append(seperator_size, '-');
	}

	int Dump()
	{
		BL_READER_PTR reader = get_reader();

		if (NULL == reader) {
			log_error("get binlog reader failed");
			return -1;
		}

		MEM_POOL_PTR tempmem = mem_pool_init(MB_SIZE * 10);
		std::string tempbuf;

		binlog_record *rec;
		uint64_t count = 0;
		while (count < opt_.dump_count) {
			mem_pool_reset(tempmem);
			if (!opt_.go_on && reader->suffix != binlog_index_)
				break;

			uint64_t pos = binlog_get_position(reader);
			int rc = binlog_read_record(reader, &rec, tempmem);
			if (-1 == rc) {
				log_error("read binlog record failed");
				return -1;
			}
			if (0 == rc)
				break;

			if (!filter_(*rec))
				continue;

			count++;

			// print record
			printf("%s\n", seperator_.c_str());
			dump_binlog_header(pos, *rec);

			if (!opt_.only_header) {
				rc = dump_binlog_data(*rec, tempbuf, tempmem);
			}

		}

		return 0;
	}
private:
	int dump_binlog_header(uint64_t offset, const binlog_record &rec)
	{
		printf("Absolute Offset: %" PRIu64 ", Data Length %" PRIu32 ", Time: %" PRIu32
				", Operation: %s, sid %" PRIu16 ", docid %" PRIu32 ", flag %x\n",
				offset, (uint32_t)(rec.len - sizeof(rec)), rec.time, operation_code_names[rec.op_code],
				rec.sid, rec.docid, rec.flag);
		return 0;
	}

	int dump_binlog_data(binlog_record &rec, std::string &buf, MEM_POOL_PTR mem)
	{
		buf.clear();

		char *table_name = NULL;
		char *field_name = NULL;
		uint64_t docid = (((uint64_t)rec.sid) << 32) + rec.docid;

		switch (rec.op_code) {
		case OPERATION_DELETE:
		{
			buf.append("DELETE FROM ");
			buf.append((char *)rec.data + 1, *(uint8_t *)(rec.data + 1));
			string_append(buf, " DOCHINT mile_doc_id = '%" PRIu64 "'", docid);
		}
		break;
		case OPERATION_INSERT:
		{
			struct row_data *rdata;
			rdata = db_binrecord_to_insert(&rec, &table_name, mem);
			string_append(buf, "INSERT INTO %s", table_name);
			for (uint16_t i = 0; i < rdata->field_count; i++) {
				struct low_data_struct *data = &rdata->datas[i];
				string_append(buf, " %s", data->field_name);
				buf.append(" = '");
				if (opt_.type_name)
					string_append(buf, "%s:", get_type_name(data->type));
				string_append_data(buf, data->type, data->data, data->len);
				buf.append("'");
			}
		}
		break;
		case OPERATION_UPDATE:
		{
			struct low_data_struct *data = db_binrecord_to_update(&rec, &table_name, &field_name, mem);
			string_append(buf, "UPDATE %s SET %s = '", table_name, field_name);
			if (opt_.type_name)
				string_append(buf, "%s:", get_type_name(data->type));
			string_append_data(buf, data->type, data->data, data->len);
			string_append(buf, "' DOCHINT mile_doc_id = '%" PRIu64 "'", docid);
		}
		break;
		case OPERATION_INDEX:
		{
			enum index_key_alg index_type;
			enum field_types data_type;
			index_type = db_binrecord_to_index(&rec, &table_name, &field_name, &data_type, mem);
			string_append(buf, "ensure_index %s %s %d %d", table_name, field_name, index_type, data_type);
		}
		break;
		case OPERATION_DINDEX:
		{
			enum index_key_alg index_type;
			index_type = db_binrecord_to_dindex(&rec, &table_name, &field_name, mem);
			string_append(buf, "del_index %s %s %d", table_name, field_name, index_type);
		}
		break;
		case OPERATION_COMPRESS:
		{
			db_binrecord_to_compress(&rec, &table_name, mem);
			string_append(buf, "compress %s", table_name);
		}
		break;

		case OPERATION_CONFIRM_OK:
		case OPERATION_CONFIRM_FAIL:
		break;

		default:
			log_error("dump operation %s not supported", operation_code_names[rec.op_code]);
		}
		;

		if (!buf.empty())
			printf("%s\n", buf.c_str());

		return 0;
	}

	const char *get_type_name(enum field_types type)
	{
		static const char *name_map[255 + 1];
		static bool name_map_inited = false;

		if (!name_map_inited) {
			for (int i = 0; g_field_type_kvs[i].key != NULL; i++)
				name_map[g_field_type_kvs[i].value] = g_field_type_kvs[i].key;
			name_map_inited = true;
		}

		return name_map[type];
	}

	BL_READER_PTR get_reader(void)
	{
		std::string index_file = dir_ + "/" + BINLOG_INDEX_FILE_NAME;

		if (access(index_file.c_str(), R_OK) != 0) {
			log_error("binlog index file %s not exist or have no read permission", index_file.c_str());
			return NULL;
		}
		binlog_index_t *index = binlog_index_init(dir_.c_str(), mem_);
		if (NULL == index) {
			log_error("load binlog index failed, dir %s", dir_.c_str());
			return NULL;
		}

		uint64_t abs_offset = -1;
		if (index->last == (uint32_t)-1) {
			abs_offset = 0;
		}
		else {
			for (uint32_t i = 0; i <= index->last; i++) {
				if (index->items[i].suffix == binlog_index_) {
					abs_offset = index->items[i].abs_offset;
					break;
				}
			}
		}
		if (abs_offset == (uint64_t)-1) {
			log_error("binlog file %u not found", binlog_index_);
			return NULL;
		}

		return binlog_reader_init_byoffset(dir_.c_str(), binlog_file_size_, abs_offset, NULL, mem_);
	}

private:
	dump_opt_t opt_;
	record_filter_t filter_;
	std::string dir_;
	uint32_t binlog_file_size_;
	uint32_t binlog_index_;

	MEM_POOL_PTR mem_;

	std::string seperator_;
};

static void usage(void)
{
	struct dump_opt_t opt; // get default falue

	std::cerr << "Mile binlog dump tool." << std::endl
			  << "Usage: milebinlog OPTIONS binlog_dir binlog_maxsize binlog_file_suffix" << std::endl
			  << "OPTIONS:" << std::endl
			  << "   -n, --count count             dump record count, default unlimited" << std::endl
			  << "   -H, --only-header             only dump record header" << std::endl
			  << "   -c, --confirm-rec             don't ignore confirm record" << std::endl
			  << "   -g, --go-on                   continue dump after specify file finished" << std::endl
			  << "   -s, --sid sid                 dump record of this sid" << std::endl
			  << "   -d, --docid-range docid-range dump record in the docid range, value seperated by '-' or ',' or '~'" << std::endl
			  << "   -t, --time-range time-range   dump record in the time range, value seperated by '-' or ',' or '~'" << std::endl
			  << "   -T, --type types              dump record int specified types, value seperated  ',' or ';'" << std::endl
			  << "   --type-name                   print value type before value" << std::endl
			  << "   --log-level log-level         log level, default " << opt.log_level << std::endl
			  << "   -h, --help                    show this help message. " << std::endl
	;
}

static int string_append(std::string &str, const char *fmt, ...)
{
	const int buf_size = 1024 * 16;

	char buf[buf_size];

	va_list ap;

	va_start(ap, fmt);
	int len = vsnprintf(buf, buf_size, fmt, ap);
	va_end(ap);
	if (len < 0)
		return -1;

	if (len <= buf_size) {
		str.append(buf, len);
		return len;
	}

	char *large_buf = new char[len + 1];
	va_start(ap, fmt);
	len = vsnprintf(large_buf, len, fmt, ap);
	va_end(ap);
	if (len < 0)
		return -1;

	str.append(large_buf, len);
	return len;
}

static int string_append_data(std::string &str, enum field_types type, void *data, uint32_t len)
{
	if (len == 0)
		return 0;
	int rc = 0;
	switch (type) {
	case HI_TYPE_NULL:
		break;
	case HI_TYPE_TINY:
		rc = string_append(str, "%" PRIu8, int(*((uint8_t *)(data))));
		break;
	case HI_TYPE_SHORT:
		rc = string_append(str, "%" PRIi16, int(*((int16_t *)(data))));
		break;
	case HI_TYPE_UNSIGNED_SHORT:
		rc = string_append(str, "%" PRIu16, int(*((uint16_t *)(data))));
		break;
	case HI_TYPE_LONG:
		rc = string_append(str, "%" PRIi32, *((int32_t *)(data)));
		break;
	case HI_TYPE_UNSIGNED_LONG:
		rc = string_append(str, "%" PRIu32, *((uint32_t *)(data)));
		break;
	case HI_TYPE_LONGLONG:
		rc = string_append(str, "%" PRIi64, *((int64_t *)(data)));
		break;
	case HI_TYPE_UNSIGNED_LONGLONG:
		rc = string_append(str, "%" PRIu64, *((uint64_t *)(data)));
		break;
	case HI_TYPE_FLOAT:
		rc = string_append(str, "%f", *((float *)(data)));
		break;
	case HI_TYPE_DOUBLE:
		rc = string_append(str, "%f", *((double *)(data)));
		break;
	case HI_TYPE_STRING:
		str.append((char *)data, len);
		rc = len;
		break;
	default:
		log_error("unknow type: %d", type);
		rc = -1;
	}
	return rc;
}

int main(int argc, char *argv[])
{
	struct dump_opt_t opt;
	struct option long_option[]  = {
		{ "help", 0, NULL, 'h' },
		{ "count", 1, NULL, 'n' },
		{ "only-header", 0, NULL, 'H' },
		{ "confirm-rec", 0, NULL, 'c' },
		{ "go-on", 0, NULL, 'g' },
		{ "sid", 1, NULL, 's' },
		{ "docid-range", 1, NULL, 'd' },
		{ "time-range", 1, NULL, 't' },
		{ "type", 1, NULL, 'T' },
		{ "type-name", 0, NULL, 0 },
		{ "log-level", 1, NULL, 0 },
		{ NULL, 0, NULL, 0 }
	};

	int c;
	int index = 0;

	while ((c = getopt_long(argc, argv, "hn:Hcgs:d:t:T:", long_option, &index)) != -1) {
		switch (c) {
		case 'n':
			opt.dump_count = atoll(optarg);
			break;
		case 'H':
			opt.only_header = true;
			break;
		case 'c':
			opt.ignore_confirm_rec = false;
			break;
		case 'g':
			opt.go_on = true;
			break;
		case 's':
			opt.sid = atoi(optarg);
			break;
		case 'd':
			opt.docid_range = optarg;
			break;
		case 't':
			opt.time_range = optarg;
			break;
		case 'T':
			opt.type = optarg;
			break;
		case 0:
			if (strcmp(long_option[index].name, "type-name") == 0)
				opt.type_name = true;
			else if (strcmp(long_option[index].name, "log-level") == 0)
				opt.log_level = optarg;
			break;
		case 'h':
		default:
			usage();
			exit(EXIT_FAILURE);
		}
	}

	if (optind + 2 >= argc) {
		usage();
		exit(EXIT_FAILURE);
	}

	set_log_level(opt.log_level.c_str());

	BinlogDumper dumper(argv[optind], atoi(argv[optind + 1]), atoi(argv[optind + 2]), opt);
	int rc = dumper.Dump();
	if (rc != 0)
		log_error("dump failed");

	return rc == 0 ? 0 : 1;
}

