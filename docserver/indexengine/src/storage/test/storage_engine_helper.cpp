// storage_engine_helper.cpp : storage_engine_helper
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-16

#include "storage_engine_helper.h"
#include "../../common/AggrFunc.h"
#include <boost/algorithm/string.hpp>

static void string2row(row_data &row, const char *sql, MEM_POOL_PTR mem);

typedef std::vector<std::string> STR_VEC;

// use a wrap func, because gtest's ASSERT_ macro can't be used in func if has return value. ??
row_data *string2row(const char *sql, MEM_POOL_PTR mem)
{
	row_data *row = (struct row_data *)mem_pool_malloc(mem, sizeof(row_data));
	string2row(*row, sql, mem);
	return row;
}

void string2row(row_data &row, const char *sql, MEM_POOL_PTR mem)
{
	STR_VEC fields;
	boost::split(fields, sql, boost::is_any_of(";,"), boost::token_compress_on);

	row.field_count = fields.size();
	row.datas = (low_data_struct *)mem_pool_malloc(mem, sizeof(low_data_struct) * fields.size());

	for (size_t i = 0; i < fields.size(); i++) {
		STR_VEC datas;
		// fieldname:type:data
		boost::split(datas, fields.at(i), boost::is_any_of(":"), boost::token_compress_on);
		ASSERT_LE(2U, datas.size());
		
		// fieldname
		row.datas[i].field_name = (char *)mem_pool_malloc(mem, datas.at(0).size() + 1);
		::memcpy(row.datas[i].field_name, datas.at(0).c_str(), datas.at(0).size());
		row.datas[i].field_name[datas.at(0).size()] = '\0';

		// type
		const std::string *value;
		if (datas.size() == 2) {
			row.datas[i].type = HI_TYPE_STRING;
			value = &datas[1];
		}
		else {
			row.datas[i].type = (field_types)atoi(datas.at(1).c_str());
			value = &datas[2];
		}

		// value
		switch (row.datas[i].type) {
		case HI_TYPE_STRING:
			row.datas[i].len = value->size();
			row.datas[i].data = mem_pool_malloc(mem, value->size());
			::memcpy(row.datas[i].data, value->c_str(), value->size());
			break;
		default: // treat as integer
			{
				int64_t v = atoll(value->c_str());
				row.datas[i].len = get_unit_size(row.datas[i].type);
				row.datas[i].data = mem_pool_malloc(mem, row.datas[i].len);
				::memcpy(row.datas[i].data, &v, row.datas[i].len);
			}
		}
	}

}

select_fields_t *string2select_field(const char *sql, MEM_POOL_PTR mem)
{
	select_fields_t *select = (select_fields_t *)mem_pool_malloc(mem, sizeof(select_fields_t));
	STR_VEC fields;
	boost::split(fields, sql, boost::is_any_of(";,"), boost::token_compress_on);
	select->n = fields.size();
	select->fields_name = (char **)mem_pool_malloc(mem, select->n * sizeof(char *));
	select->select_type = (select_types_t *)mem_pool_malloc(mem, select->n * sizeof(select_types_t));
	for (size_t i = 0; i < fields.size(); i++) {
		STR_VEC datas;
		// fieldname:select_type(int)
		boost::split(datas, fields.at(i), boost::is_any_of(":"), boost::token_compress_on);
		const std::string &name = datas[0];
		if (datas.size() == 1) // default is original
			select->select_type[i] = SELECT_TYPE_ORIGINAL;
		else
			select->select_type[i] = (select_types_t)atoi(datas.at(1).c_str());
		select->fields_name[i] = (char *)mem_pool_malloc(mem, name.size() + 1);
		::strcpy(select->fields_name[i], name.c_str());
	}
	
	return select;
}

select_field_array *string2func_select(const char *sql, MEM_POOL_PTR mem)
{
	STR_VEC fields;
	boost::split(fields, sql, boost::is_any_of(";,"), boost::token_compress_on);

	select_field_array *sel = NEW(mem, select_field_array);
	sel->n = fields.size();
	sel->select_fields = (select_field_t *)mem_pool_malloc(mem, sel->n * sizeof(select_field_t));

	for (uint32_t i = 0; i < sel->n; i++) {
		select_field_t &f = sel->select_fields[i];
		f.type = FUNCTION_SELECT;

		STR_VEC parsed_vec;
		boost::split(parsed_vec, fields.at(i), boost::is_any_of("()"), boost::token_compress_on);

		boost::trim(parsed_vec.at(0));
		boost::trim(parsed_vec.at(1));

		f.func_type = (enum function_type)func_name2type(parsed_vec.at(0).c_str());
		char *name = (char *)mem_pool_malloc(mem, parsed_vec.at(1).size() + 1);
		::strcpy(name, parsed_vec.at(1).c_str());
		f.field_name = name;
	}

	return sel;
}

int row_count(MileIterator *iter)
{
	int n = 0;
	iter->First();
	while (!iter->IsDone()) {
		n++;
		iter->Next();
	}
	return n;
}

