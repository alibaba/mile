// data_export.cpp : data_export
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-02-09

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <getopt.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <iostream>
#include <string>
#include <vector>
#include <map>

#include <boost/algorithm/string.hpp>
#include <boost/lexical_cast.hpp>

#include "common/def.h"
#include "common/mem.h"
#include "common/log.h"
#include "common/common_util.h"

#include "storage/docdb/bitmark.h"
#include "storage/docdb/vstorage.h"
#include "storage/docdb/data_field.h"

#define DATA_FILTER_NAME "filter_vstore"
#define DATA_BITMARK_NAME "del"

static void usage(void);

typedef std::pair< std::string, std::string > FieldValue;
typedef std::vector< FieldValue > FieldValueArray;

template < typename T >
void StringAppend(std::string *str, const char *fmt, T t)
{
	const int buf_size = 64;
	char buf[buf_size];

	if (NULL != fmt) {
		int n = snprintf(buf, buf_size, fmt, t);
		if (n < 0)
			return;
		str->append(buf, std::min(n, buf_size) );
	}
	else {
		*str += boost::lexical_cast< std::string >(t);
	}
}

struct export_option_t {
	uint64_t row_limit;
	std::string output_file;
	std::string file_group_field;

	std::string fv_sep;
	std::string row_sep;
	std::string col_sep;

	export_option_t()
		: row_limit(10000000LL), output_file("-"), fv_sep(":"), row_sep("#row#"), col_sep("#col#")
	{
	}
};


// read row data from segment
class SegmentReader {
public:
	SegmentReader(uint64_t row_limit, const std::string &segment_dir)
		: cur_row_(0), row_limit_(row_limit), segment_dir_(segment_dir)
		, vstorage_(NULL), bitmark_(NULL)
		, mem_main_(NULL), mem_tmp_(NULL)
	{
	}

	virtual ~SegmentReader()
	{
		if (vstorage_) {
			vstorage_release(vstorage_);
		}
		if (bitmark_) {
			bitmark_release(bitmark_);
		}

		if (mem_tmp_) {
			mem_pool_destroy(mem_tmp_);
		}
		if (mem_main_) {
			mem_pool_destroy(mem_main_);
		}
	}

	int Init()
	{
		const size_t mem_pool_size = 10 * 1024 * 1024LL; // 10MB

		mem_main_ = mem_pool_init(mem_pool_size);
		mem_tmp_ = mem_pool_init(mem_pool_size);

		// check file exists, hard coded.
		std::vector< std::string > files;
		files.push_back(segment_dir_ + "/" + DATA_FILTER_NAME + ".dat");
		//files.push_back(segment_dir_ + "/" + DATA_FILTER_NAME + ".loc");
		files.push_back(segment_dir_ + "/" + DATA_BITMARK_NAME + ".bit");
		for (size_t i = 0; i < files.size(); i++) {
			if (access(files.at(i).c_str(), R_OK) != 0) {
				log_error("access file %s failed, errno %d", files.at(i).c_str(), errno);
				return -1;
			}
		}

		// init vstorage
		struct vstorage_config vc;
		vc.row_limit = row_limit_;
		strcpy(vc.work_space, segment_dir_.c_str() );
		strcpy(vc.vstorage_name, DATA_FILTER_NAME);

		vstorage_ = vstorage_init(&vc, mem_main_);
		if (NULL == vstorage_) {
			log_error("vstorage init failed");
			return -1;
		}

		// init bitmark
		struct bitmark_config bc;
		bc.row_limit = row_limit_;
		strcpy(bc.work_space, segment_dir_.c_str() );
		strcpy(bc.bitmark_name, DATA_BITMARK_NAME);

		bitmark_ = bitmark_init(&bc, mem_main_);
		if (NULL == bitmark_) {
			log_error("bitmark init failed.");
			vstorage_release(vstorage_);
			return -1;
		}

		return 0;
	}

	int GetRow(FieldValueArray *array)
	{
		mem_pool_reset(mem_tmp_);

		// query delete bitmark
		while (cur_row_ < row_limit_ && bitmark_query(bitmark_, cur_row_) != 0)
			++cur_row_;
		if (cur_row_ >= row_limit_)  // read completed
			return -1;

		// query row
		struct low_data_struct *data = vstorage_query(vstorage_, cur_row_, mem_tmp_);
		if (NULL == data) {
			return -1;
		}
		++cur_row_;
		RowToString(data, array);

		return 0;
	}

	// convert row binary data to  filedname -> string value  pair. not thread safe.
	int RowToString(struct low_data_struct *data, FieldValueArray *array)
	{
		static std::string value;

		struct row_data *row = lowdata_to_rowdata(data, mem_tmp_);

		array->clear();
		for (int i = 0; i < row->field_count; i++) {
			struct low_data_struct *low = &row->datas[i];
			value.clear();
			if (low->len != 0) {
				switch (low->type) {
				case HI_TYPE_NULL:
					break;
				case HI_TYPE_TINY:
					StringAppend(&value, "%d", int(*((uint8_t *)(low->data))) );
					break;
				case HI_TYPE_SHORT:
					StringAppend(&value, "%d", int(*((int16_t *)(low->data))) );
					break;
				case HI_TYPE_UNSIGNED_SHORT:
					StringAppend(&value, "%d", int(*((uint16_t *)(low->data))) );
					break;
				case HI_TYPE_LONG:
					StringAppend(&value, "%d", *((int32_t *)(low->data)) );
					break;
				case HI_TYPE_UNSIGNED_LONG:
					StringAppend(&value, "%u", *((uint32_t *)(low->data)) );
					break;
				case HI_TYPE_LONGLONG:
					StringAppend(&value, "%lld", *((int64_t *)(low->data)) );
					break;
				case HI_TYPE_UNSIGNED_LONGLONG:
					StringAppend(&value, "%llu", *((uint64_t *)(low->data)) );
					break;
				case HI_TYPE_FLOAT:
					StringAppend(&value, "%f", *((float *)(low->data)) );
					break;
				case HI_TYPE_DOUBLE:
					StringAppend(&value, "%f", *((double *)(low->data)) );
					break;
				case HI_TYPE_STRING:
					value.assign( (char *)low->data, low->len);
					break;
				default:
					log_error("unknow type: %d", low->type);
					;
				}
			}

			array->push_back(std::make_pair(low->field_name, value) );
		}

		return array->size();
	}

private:
	uint32_t cur_row_;
	uint64_t row_limit_;
	std::string segment_dir_;

	struct vstorage_manager *vstorage_;
	struct bitmark_manager *bitmark_;

	MEM_POOL_PTR mem_main_;
	MEM_POOL_PTR mem_tmp_;
};

// output file by group
class FileGroupBase {
public:
	FileGroupBase(const std::string &filename)
		: base_file_name_(filename)
	{
	}
	virtual ~FileGroupBase() {}
	virtual int GetFD(bool *is_first, const FieldValueArray &array) = 0;

protected:
	bool is_empty(int fd)
	{
		struct stat st;

		if (fstat(fd, &st) == 0 && st.st_size > 0)
			return false;
		return true;
	}
	std::string base_file_name_;
};

// single output file
class SingleFile : public FileGroupBase
{
public:
	SingleFile(const std::string &filename)
		: FileGroupBase(filename), fd_(-1)
	{
	}

	virtual ~SingleFile()
	{
		if (fd_ > 0)
			close(fd_), fd_ = -1;
	}

	virtual int GetFD(bool *is_first, const FieldValueArray &array)
	{
		if (fd_ < 0) {
			if (base_file_name_ == "-") {
				fd_ = STDOUT_FILENO;
				*is_first = false;
			}
			else {
				fd_ = open(base_file_name_.c_str(), O_CREAT | O_WRONLY | O_APPEND, 0600);
				if (fd_ < 0) {
					log_error("open %s failed, errno %d", base_file_name_.c_str(), errno);
					return -1;
				}
				*is_first = is_empty(fd_);
			}
			return fd_;
		}
		*is_first = false;
		return fd_;
	}

private:
	int fd_;
};

// group output file by field values
class FieldGroupFile : public FileGroupBase
{
public:
	FieldGroupFile(const std::string &filename, const std::string &group_fields)
		: FileGroupBase(filename)
	{
		std::vector< std::string > tmp;
		boost::split(tmp, group_fields, boost::is_any_of(","), boost::token_compress_on);
		for (std::vector< std::string >::iterator iter = tmp.begin(); iter != tmp.end(); ++iter) {
			boost::trim(*iter);
			if (!iter->empty() )
				fields_.push_back(*iter);
		}
		assert(!fields_.empty());
	}

	~FieldGroupFile()
	{
		std::map< std::string, int >::iterator iter = fd_map_.begin();
		for (; iter != fd_map_.end(); ++iter) {
			if (iter->second > 0)
				close(iter->second), iter->second = -1;
		}
	}

	virtual int GetFD(bool *is_first, const FieldValueArray &array)
	{
		std::string value = GetGroupString(array);

		std::map< std::string, int >::iterator iter = fd_map_.find(value);
		if (iter == fd_map_.end() ) {
			std::string filename = base_file_name_ + "_" + value;
			int fd = open(filename.c_str(), O_CREAT | O_WRONLY | O_APPEND, 0600);
			if (fd < 0) {
				log_error("open %s failed, errno %d", filename.c_str(), errno);
				return -1;
			}
			fd_map_.insert(std::make_pair(value, fd) );
			*is_first = is_empty(fd);
			return fd;
		}
		else {
			*is_first = false;
			return iter->second;
		}
	}

private:
	std::string GetGroupString(const FieldValueArray &array)
	{
		std::string result;

		for (size_t i = 0; i < fields_.size(); i++) {
			if (0 != i)
				result.append("_");
			for (size_t j = 0; j < array.size(); j++) {
				if (array.at(j).first == fields_.at(i) ) {
					result.append(array.at(j).second);
					break;
				}
			}
		}
		return result;
	}

private:
	std::vector< std::string > fields_;
	std::map< std::string, int > fd_map_;
};

class DataExport {
public:
	DataExport(const struct export_option_t &opt)
		: opt_(opt), file_selector_(NULL), row_count_(0)
	{
	};

	virtual ~DataExport()
	{
		delete file_selector_;
	}

	int Init(void)
	{
		assert(!opt_.output_file.empty());

		if (opt_.file_group_field.empty()) {
			file_selector_ = new SingleFile(opt_.output_file);
		}
		else {
			file_selector_ = new FieldGroupFile(opt_.output_file, opt_.file_group_field);
		}

		return 0;
	}

	int ExportSegment(const std::string &segment_dir)
	{
		log_info("export segment %s", segment_dir.c_str() );
		SegmentReader reader(opt_.row_limit, segment_dir);
		if (reader.Init() != 0) {
			log_error("init segment %s failed", segment_dir.c_str() );
			return -1;
		}

		uint64_t start_count = row_count_;
		FieldValueArray row;
		while (reader.GetRow(&row) == 0) {
			bool is_first = false;
			int fd = file_selector_->GetFD(&is_first, row);
			if (fd < 0) {
				log_error("init write file failed");
				return -1;
			}
			WriteRow(fd, is_first, row);
		}

		log_info("export %"PRIu64"records from %s", row_count_ - start_count, segment_dir.c_str() );

		return 0;
	}

	uint64_t GetRowCount(void) { return row_count_; }

private:
	// write a text row to file, not thread safe.
	int WriteRow(int fd, bool is_first, const FieldValueArray &row)
	{
		static std::string out_str;

		out_str.clear();
		out_str.reserve(2 * 1024);
		out_str.append(is_first ? std::string("") : opt_.row_sep);
		for (FieldValueArray::const_iterator iter = row.begin(); iter != row.end(); ++iter) {
			if (iter != row.begin() )
				out_str.append(opt_.col_sep);
			out_str += iter->first;
			out_str += opt_.fv_sep;
			out_str += iter->second;
		}
		if (write(fd, out_str.c_str(), out_str.size() ) < 0) {
			log_error("write failed, errno %d", errno);
			return -1;
		}

		++row_count_;
		return 0;
	}

private:
	const struct export_option_t opt_;
	FileGroupBase *file_selector_;
	uint64_t row_count_;
};

void usage()
{
	struct export_option_t opt; // get default falue

	std::cerr << "Mile data export tool." << std::endl
			  << "Usage: data_export OPTIONS segment_dir ..." << std::endl
			  << "OPTIONS:" << std::endl
			  << "   -n, --row-limit row_limit     segment row limit, default: " << opt.row_limit << std::endl
			  << "   -o, --output filename         output file, '-' for stdout, default: " << opt.output_file << std::endl
			  << "   -g, --group-field fieldnames  group output file by field value." << std::endl
			  << "                                 fieldnames separated by ','." << std::endl
			  << "   -f, --fv-sep separator        field value separator, default: " << opt.fv_sep << std::endl
			  << "   -r, --row-sep separator       row separator, default: " << opt.row_sep << std::endl
			  << "   -c, --col-sep separator       col separator, default: " << opt.col_sep << std::endl
			  << "   -h, --help                    show this help message. " << std::endl
	// TODO add example
	;
}

int main(int argc, char *argv[])
{
	struct export_option_t opt;

	struct option long_option[]  = {
		{ "help", 0, NULL, 'h' },
		{ "row-limit", 1, NULL, 'n' },
		{ "output", 1, NULL, 'o' },
		{ "group-filed", 1, NULL, 'g' },
		{ "fv-sep", 1, NULL, 'f' },
		{ "row-sep", 1, NULL, 'r' },
		{ "col-sep", 1, NULL, 'c' },
		{ NULL, 0, NULL, 0 }
	};

	int c;
	int index = 0;

	while ( (c = getopt_long(argc, argv, "hn:o:g:f:r:c:", long_option, &index)) != -1) {
		switch (c) {
		case 'n':
			opt.row_limit = atoll(optarg);
			break;
		case 'o':
			opt.output_file = optarg;
			break;
		case 'g':
			opt.file_group_field = optarg;
			break;
		case 'f':
			opt.fv_sep = optarg;
			break;
		case 'r':
			opt.row_sep = optarg;
			break;
		case 'c':
			opt.col_sep = optarg;
			break;
		case 'h':
		case 0:
		default:
			usage();
			exit(EXIT_FAILURE);
		}
	}

	if (argc <= optind) {
		usage();
		exit(EXIT_FAILURE);
	}

	DataExport data_export(opt);
	if (data_export.Init() < 0) {
		log_error("init data export failed.");
		exit(EXIT_FAILURE);
	}

	MEM_POOL_PTR mem_pool = mem_pool_init(1 * 1024 * 1024);   // 1MB
	init_profile(1000, mem_pool);   // init profile to avoid core dump.

	for (int i = optind; i < argc; i++) {
		int rc = data_export.ExportSegment(argv[i]);
		if (rc < 0) {
			log_error("export %s failed, abort.", argv[i]);
			exit(EXIT_FAILURE);
		}
	}

	log_info("total exported records: %"PRIu64, data_export.GetRowCount() );

	mem_pool_destroy(mem_pool);
}

