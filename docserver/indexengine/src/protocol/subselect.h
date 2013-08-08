// subselect.h : subselect
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-07-22

#ifndef SUBSELECT_H
#define SUBSELECT_H

#include "../common/def.h"
#include "databuffer.h"

#include "../common/list.h"

enum subselect_type {
	SUBSELECT_TYPE_RAW_VALUE  = 1,
	SUBSELECT_TYPE_MAX_FUNC   = 2,
	SUBSELECT_TYPE_MIN_FUNC   = 4,
	SUBSELECT_TYPE_COUNT_FUNC = 5,
	SUBSELECT_TYPE_SUM_FUNC   = 6
};

typedef int32_t (*SUBSELECT_PARSE_FUNC)(void *subselect, MEM_POOL_PTR mem, struct data_buffer *rbuf);
typedef int32_t (*SUBSELECT_INIT_FUNC)(void *subselect, MEM_POOL_PTR mem, uint8_t table_id);
typedef int32_t (*SUBSELECT_EXECUTE_FUNC)(void *subselect, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem);
typedef int32_t (*SUBSELECT_GET_RESULT_FUNC)(void *subselect, MEM_POOL_PTR mem, uint8_t table_id);
typedef uint32_t (*SUBSELECT_SIZEOF_RESULT_FUNC)(void *subselect);
typedef int32_t (*SUBSELECT_TO_STREAM_FUNC)(void *subselect, struct data_buffer *sbuf);

struct subselect_t {
	uint8_t type; // subselect type
	void *select; // subselet: max_func_select/...

	// process functions
	SUBSELECT_PARSE_FUNC parse_func;
	SUBSELECT_INIT_FUNC init_func;
	SUBSELECT_EXECUTE_FUNC execute_func;
	SUBSELECT_GET_RESULT_FUNC get_result_func;
	SUBSELECT_SIZEOF_RESULT_FUNC sizeof_result_func;
	SUBSELECT_TO_STREAM_FUNC to_stream_func;
};

struct max_func_select {
	// input
	uint32_t func_col; // max function column id
	enum field_types func_field_type; // max function column type

	uint32_t select_col_num; // select column number
	uint32_t *select_cols; // select column ids
	enum field_types *select_types; // select column types

	MEM_POOL_PTR mem;

	// result
	uint64_t docid; // doc_id of the value
	struct low_data_struct *value; // value 
	struct low_data_struct **select_values; // select values
};

// min_func_select use the same structure with max_func_select

struct count_func_select {
	// result
	uint32_t value;
};

struct sum_func_select {
	// input
	uint32_t col;
	enum field_types field_type;

	MEM_POOL_PTR mem;
	// result
	struct low_data_struct *value;
};

struct raw_value_select {
	// input
	uint32_t sel_col_n;
	uint32_t *sel_cols;
	enum field_types *sel_types;

	uint32_t ord_col_n;
	uint32_t *ord_cols;
	enum order_types *ord_types;

	uint32_t limit;
	uint32_t offset;

	struct orderby_info *ord_info;
	// result
	struct query_result_dataset *result_database;
};

uint32_t init_subselect(MEM_POOL_PTR mem, struct subselect_t *subselect, enum subselect_type type);

#endif // SUBSELECT_H

