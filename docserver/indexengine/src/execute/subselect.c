// subselect.c : subselect
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-07-22

#if 0
#include "subselect.h"
#include "db.h"
#include "packet_parse.h"

// max_func select
static int32_t parse_max_func_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf );
static int32_t init_max_func_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static int32_t execute_max_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem);
static int32_t get_max_func_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static uint32_t sizeof_max_func_result(void *subselect);
static int32_t max_func_to_stream(void *subselect, struct data_buffer *sbuf);

// min_func select
static int32_t execute_min_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem);

// count_func select
static int32_t parse_count_func_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf );
static int32_t init_count_func_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static int32_t execute_count_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem);
static int32_t get_count_func_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static uint32_t sizeof_count_func_result(void *subselect);
static int32_t count_func_to_stream(void *subselect, struct data_buffer *sbuf);

// sun_func select
static int32_t parse_sum_func_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf );
static int32_t init_sum_func_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static int32_t execute_sum_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem);
static int32_t get_sum_func_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static uint32_t sizeof_sum_func_result(void *subselect);
static int32_t sum_func_to_stream(void *subselect, struct data_buffer *sbuf);

// sun_func select
static int32_t parse_raw_value_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf );
static int32_t init_raw_value_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static int32_t execute_raw_value_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem);
static int32_t get_raw_value_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id);
static uint32_t sizeof_raw_value_result(void *subselect);
static int32_t raw_value_to_stream(void *subselect, struct data_buffer *sbuf);

uint32_t init_subselect(MEM_POOL_PTR mem, struct subselect_t *subselect, enum subselect_type type)
{
	memset( subselect, 0, sizeof( *subselect ) );
	subselect->type = type;

	switch( type ) {
	case SUBSELECT_TYPE_MAX_FUNC:
		subselect->select = mem_pool_malloc(mem, sizeof(struct max_func_select));
		memset(subselect->select, 0, sizeof(struct max_func_select));

		// add process functions here
		subselect->parse_func = parse_max_func_select;
		subselect->init_func = init_max_func_select;
		subselect->execute_func = execute_max_func_select;
		subselect->get_result_func = get_max_func_select_result;
		subselect->sizeof_result_func = sizeof_max_func_result;
		subselect->to_stream_func = max_func_to_stream;
		break;
	case SUBSELECT_TYPE_MIN_FUNC: // only execute_func different with max_func
		subselect->select = mem_pool_malloc(mem, sizeof(struct max_func_select));
		memset(subselect->select, 0, sizeof(struct max_func_select));

		// add process functions here
		subselect->parse_func = parse_max_func_select;
		subselect->init_func = init_max_func_select;
		subselect->execute_func = execute_min_func_select;
		subselect->get_result_func = get_max_func_select_result;
		subselect->sizeof_result_func = sizeof_max_func_result;
		subselect->to_stream_func = max_func_to_stream;
		break;
	case SUBSELECT_TYPE_COUNT_FUNC:
		subselect->select = mem_pool_malloc(mem, sizeof(struct count_func_select));
		memset(subselect->select, 0, sizeof(struct count_func_select));

		// add process functions here
		subselect->parse_func = parse_count_func_select;
		subselect->init_func = init_count_func_select;
		subselect->execute_func = execute_count_func_select;
		subselect->get_result_func = get_count_func_select_result;
		subselect->sizeof_result_func = sizeof_count_func_result;
		subselect->to_stream_func = count_func_to_stream;
		break;
	case SUBSELECT_TYPE_SUM_FUNC:
		subselect->select = mem_pool_malloc(mem, sizeof(struct sum_func_select));
		memset(subselect->select, 0, sizeof(struct sum_func_select));

		// add process functions here
		subselect->parse_func = parse_sum_func_select;
		subselect->init_func = init_sum_func_select;
		subselect->execute_func = execute_sum_func_select;
		subselect->get_result_func = get_sum_func_select_result;
		subselect->sizeof_result_func = sizeof_sum_func_result;
		subselect->to_stream_func = sum_func_to_stream;
		break;
	case SUBSELECT_TYPE_RAW_VALUE:
		subselect->select = mem_pool_malloc(mem, sizeof(struct raw_value_select));
		memset(subselect->select, 0, sizeof(struct raw_value_select));

		// add process functions here
		subselect->parse_func = parse_raw_value_select;
		subselect->init_func = init_raw_value_select;
		subselect->execute_func = execute_raw_value_select;
		subselect->get_result_func = get_raw_value_select_result;
		subselect->sizeof_result_func = sizeof_raw_value_result;
		subselect->to_stream_func = raw_value_to_stream;
		break;
	default:
		log_error( "unknown subselect type: %d", type );
		return -1;
	}
	return 0;
}

static int32_t parse_max_func_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf )
{
	struct max_func_select *select = (struct max_func_select*)arg;
	
	select->func_col = read_int32( rbuf );
	select->select_col_num = read_int32( rbuf );
	if( select->select_col_num > 0 ) {
		select->select_cols = (uint32_t *)mem_pool_malloc(mem, sizeof(uint32_t) * select->select_col_num );
		int i;
		for( i = 0; i < select->select_col_num; i++ ) {
			select->select_cols[i] = read_int32( rbuf );
		}
	}
	return 0;
}

int32_t init_max_func_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	struct max_func_select *select = (struct max_func_select*)arg;

	select->mem = mem;
	select->func_field_type = db_getfield_type( table_id, select->func_col );
	if( select->select_col_num > 0 ) {
		select->select_types = (enum field_types*)mem_pool_malloc(mem, sizeof(enum field_types) * select->select_col_num);
		uint32_t i;
		for( i = 0; i < select->select_col_num; i++ ) {
			select->select_types[i] = db_getfield_type( table_id, select->select_cols[i] );
		}
	}

	select->value = NULL;
	select->select_values = NULL;

	return MILE_RETURN_SUCCESS;
}

int32_t execute_max_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem)
{
	struct max_func_select *select = (struct max_func_select*)arg;

	struct low_data_struct **value = db_query_fields_by_rowid(table_id, segment_id, row_id, 1, &select->func_col, inner_mem);
	if( NULL == value ) {
		log_error("查询以下记录时出错 rowid=%d from table_id=%d, sid=%d!", row_id, table_id, segment_id);
		return ERROR_QUERY_BY_ROWID;
	}

	if( NULL == select->value ) { // first time
		select->value = (struct low_data_struct*)mem_pool_malloc(select->mem, sizeof(struct low_data_struct));
		memset( select->value, 0, sizeof(struct low_data_struct));
		copy_low_data_struct( select->mem, select->value, value[0] );
		select->docid = (((uint64_t)segment_id) << 32) + row_id;
	}
	else {
		int32_t rc = compare_ld(select->value, value[0], select->func_field_type);
		if( -1 == rc ) {
			copy_low_data_struct( select->mem, select->value, value[0] );
			select->docid = (((uint64_t)segment_id) << 32) + row_id;
		}
		if( -2 == rc ) {
			return rc;
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t get_max_func_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	struct max_func_select *select = (struct max_func_select*)arg;

	// no select column
	if( 0 == select->select_col_num )
		return MILE_RETURN_SUCCESS;

	// get select column value from docid
	select->select_values = db_query_fields_by_rowid(table_id, select->docid >> 32, (uint32_t)select->docid,
			select->select_col_num, select->select_cols, mem);
	if( NULL == select->select_values ) {
		log_error("查询以下记录时出错 rowid=%d from table_id=%d, sid=%d!", (uint32_t)select->docid, table_id, (uint32_t)(select->docid>>32));
		return ERROR_QUERY_BY_ROWID;
	}
	return MILE_RETURN_SUCCESS;
}

uint32_t sizeof_max_func_result(void *arg)
{
	struct max_func_select *select = (struct max_func_select*)arg;
	uint32_t size = 0;
	size += 4;
	size += select->value->len;
	uint32_t i;
	for( i = 0; i < select->select_col_num; i++ ) {
		size += 4;
		size += select->select_values[i]->len;
	}
	return size;
}

int32_t max_func_to_stream(void *arg, struct data_buffer *sbuf)
{
	struct max_func_select *select = (struct max_func_select*)arg;
	write_dyn_value( sbuf, select->value, select->func_field_type );
	uint32_t i;
	for( i = 0; i < select->select_col_num; i++ ) {
		write_dyn_value( sbuf, select->select_values[i], select->select_types[i] );
	}
	return 0;
}

int32_t execute_min_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem)
{
	struct max_func_select *select = (struct max_func_select*)arg;

	struct low_data_struct **value = db_query_fields_by_rowid(table_id, segment_id, row_id, 1, &select->func_col, inner_mem);
	if( NULL == value ) {
		log_error("查询以下记录时出错 rowid=%d from table_id=%d, sid=%d!", row_id, table_id, segment_id);
		return ERROR_QUERY_BY_ROWID;
	}

	if( NULL == select->value ) { // first time
		select->value = (struct low_data_struct*)mem_pool_malloc(select->mem, sizeof(struct low_data_struct));
		memset( select->value, 0, sizeof(struct low_data_struct));
		copy_low_data_struct( select->mem, select->value, value[0] );
		select->docid = (((uint64_t)segment_id) << 32) + row_id;
	}
	else {
		int32_t rc = compare_ld(select->value, value[0], select->func_field_type);
		if( 1 == rc ) {
			copy_low_data_struct( select->mem, select->value, value[0] );
			select->docid = (((uint64_t)segment_id) << 32) + row_id;
		}
		if( -2 == rc ) {
			return rc;
		}
	}

	return MILE_RETURN_SUCCESS;
}

static int32_t parse_count_func_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf )
{
	// nothing to do
	return 0;
}

int32_t init_count_func_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	struct count_func_select *select = (struct count_func_select*)arg;
	select->value = 0;
	return MILE_RETURN_SUCCESS;
}

int32_t execute_count_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem)
{
	struct count_func_select *select = (struct count_func_select*)arg;
	select->value++;
	return MILE_RETURN_SUCCESS;
}

int32_t get_count_func_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	// nothing to do
	return MILE_RETURN_SUCCESS;
}

uint32_t sizeof_count_func_result(void *arg)
{
	struct count_func_select *select = (struct count_func_select*)arg;
	return sizeof(select->value);
}

int32_t count_func_to_stream(void *arg, struct data_buffer *sbuf)
{
	struct count_func_select *select = (struct count_func_select*)arg;
	write_int32(select->value, sbuf);
	return 0;
}

int32_t parse_sum_func_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf )
{
	struct sum_func_select *select = (struct sum_func_select*)arg;
	
	select->col = read_int32( rbuf );
	return 0;
}

int32_t init_sum_func_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	struct sum_func_select *select = (struct sum_func_select*)arg;

	select->field_type = db_getfield_type( table_id, select->col );
	select->value = NULL;

	select->mem = mem;

	return MILE_RETURN_SUCCESS;
}

int32_t execute_sum_func_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem)
{
	struct sum_func_select *select = (struct sum_func_select*)arg;

	struct low_data_struct **value = db_query_fields_by_rowid(table_id, segment_id, row_id, 1, &select->col, inner_mem);
	if( NULL == value ) {
		log_error("查询以下记录时出错 rowid=%d from table_id=%d, sid=%d!", row_id, table_id, segment_id);
		return ERROR_QUERY_BY_ROWID;
	}

	if( NULL == select->value ) { // first time
		select->value = (struct low_data_struct*)mem_pool_malloc(select->mem, sizeof(struct low_data_struct));
		memset( select->value, 0, sizeof(struct low_data_struct));
		copy_low_data_struct( select->mem, select->value, value[0] );
	}
	else {
		return add_data(select->value->data, value[0]->data, select->field_type );
	}

	return MILE_RETURN_SUCCESS;
}

int32_t get_sum_func_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	// nothing to do
	return MILE_RETURN_SUCCESS;
}

uint32_t sizeof_sum_func_result(void *arg)
{
	struct sum_func_select *select = (struct sum_func_select*)arg;
	uint32_t size = 0;
	size += 4;
	size += select->value->len;
	return size;
}

int32_t sum_func_to_stream(void *arg, struct data_buffer *sbuf)
{
	struct sum_func_select *select = (struct sum_func_select*)arg;
	write_dyn_value( sbuf, select->value, select->field_type );
	return 0;
}

int32_t parse_raw_value_select( void *arg, MEM_POOL_PTR mem, struct data_buffer *rbuf )
{
	struct raw_value_select *select = (struct raw_value_select*)arg;

	uint32_t n = read_int32( rbuf ); // read select columns number.
	select->sel_col_n = n;

	select->sel_cols = (uint32_t *)mem_pool_malloc( mem, sizeof(uint32_t) * n );
	uint32_t i = 0;
	for( i = 0; i < n; i++ ) {
		select->sel_cols[i] = read_int32( rbuf );
	}

	n = read_int32( rbuf ); // read order columns number.
	select->ord_col_n = n;
	if( n > 0 ) {
		select->ord_cols = (uint32_t *)mem_pool_malloc(mem, sizeof(uint32_t) * n );
		select->ord_types = (enum order_types *)mem_pool_malloc(mem, sizeof(enum order_types) * n );
		for( i = 0; i < n; i++ ) {
			select->ord_cols[i] = read_int32( rbuf );
			select->ord_types[i] = read_int8( rbuf );
		}
	}

	select->limit = read_int32( rbuf );
	select->offset = read_int32( rbuf );
	
	return 0;
}

int32_t init_raw_value_select(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	struct raw_value_select *select = (struct raw_value_select*)arg;

	select->sel_types = (enum field_types *)mem_pool_malloc(mem, sizeof(enum field_types) * select->sel_col_n);
	uint32_t i = 0;
	for( i = 0; i < select->sel_col_n; i++ ) {
		select->sel_types[i] = db_getfield_type( table_id, select->sel_cols[i] );
	}

	// generate orderby_info
	if( select->ord_col_n > 0 ) {
		select->ord_info = (struct orderby_info*) mem_pool_malloc(mem, sizeof(struct orderby_info));
		struct orderby_info *info = select->ord_info;
		memset(info, 0, sizeof(struct orderby_info));

		info->sel_n = select->sel_col_n;
		info->sel_columns = select->sel_cols;
		info->ord_n = select->ord_col_n;
		info->ord_columns = select->ord_cols;
		info->ord_types = select->ord_types;
		info->fd_types = (enum field_types *)mem_pool_malloc(mem, sizeof(enum field_types) * info->ord_n );
		info->ord_index = (uint32_t *)mem_pool_malloc(mem, sizeof(uint32_t) * info->ord_n);

		uint32_t j = 0;
		for( i = 0; i < info->ord_n; i ++ ) {
			for( j = 0; j < info->sel_n; j++ ) {
				if( info->ord_columns[i] == info->sel_columns[j] ) {
					info->ord_index[i] = j;
					info->fd_types[i] = select->sel_types[j]; // info->sel_columns = select->sel_cols;
					break;
				}
			}
			if( j >= info->sel_n ) {
				log_error( "Order columns must in select columns in specify query's raw value select" );
				return ERROR_UNSUPPORTED_SQL_TYPE;
			}
		}

		info->map = NULL; // for distinct select.
	}

	// init result database
	select->result_database = init_query_result_dataset(mem, select->sel_col_n, select->sel_types, select->limit + select->offset);

	return MILE_RETURN_SUCCESS;
}

int32_t execute_raw_value_select(void *arg, uint8_t table_id, uint16_t segment_id, uint32_t row_id, MEM_POOL_PTR inner_mem)
{
	struct raw_value_select *select = (struct raw_value_select*)arg;
	struct result_data_elem *elem;
	int32_t need_sort = select->ord_col_n > 0;

	if( need_sort )
		elem = (struct result_data_elem *)mem_pool_malloc( inner_mem, sizeof(struct result_data_elem));
	else
		elem = (struct result_data_elem *)mem_pool_malloc( select->result_database->pMemPool, sizeof(struct result_data_elem));

	elem->docid = ((uint64_t)segment_id << 32) + row_id;
	elem->col_num = select->sel_col_n;

	if( need_sort )
		elem->cols_data = db_query_fields_by_rowid(table_id, segment_id, row_id, elem->col_num, select->sel_cols, inner_mem);
	else
		elem->cols_data = db_query_fields_by_rowid(table_id, segment_id, row_id, elem->col_num, select->sel_cols, select->result_database->pMemPool);
	if( NULL == elem->cols_data ) {
		log_error("查询以下记录时出错 rowid=%d from table_id=%d, sid=%d!", row_id, table_id, segment_id);
		return ERROR_QUERY_BY_ROWID;
	}

	if( !need_sort )
		return add_result_data_elem(elem, select->result_database, 0 );

	int32_t rc = heap_sort_add( select->result_database, select->ord_info, elem );
	if( rc >= 0 )
		return MILE_RETURN_SUCCESS;
	return rc;
}

int32_t get_raw_value_select_result(void *arg, MEM_POOL_PTR mem, uint8_t table_id)
{
	// nothing to do
	return MILE_RETURN_SUCCESS;
}

uint32_t sizeof_raw_value_result(void *arg)
{
	struct raw_value_select *select = (struct raw_value_select*)arg;
	uint32_t size = 0;
	size += 4;
	uint32_t i = 0, j = 0;
	for( i = 0; i < select->result_database->elems_num; i++ ) {
		size += 8;
		for( j = 0; j < select->result_database->total_cols_num; j++ ) {
			size += 4;
			size += select->result_database->data_array[i]->cols_data[j]->len;
		}
	}

	return size;
}

int32_t raw_value_to_stream(void *arg, struct data_buffer *sbuf)
{
	struct raw_value_select *select = (struct raw_value_select*)arg;

	write_int32( select->result_database->elems_num, sbuf );

	uint32_t i = 0, j = 0;
	for( i = 0; i < select->result_database->elems_num; i++ ) {
		print_result_data_elem(select->result_database->data_array[i], select->result_database->ftype_array);
		write_int64( select->result_database->data_array[i]->docid, sbuf );
		for( j = 0; j < select->result_database->total_cols_num; j++ ) {
			write_dyn_value(sbuf, select->result_database->data_array[i]->cols_data[j],
					select->result_database->ftype_array[j] );
		}
	}

	return MILE_RETURN_SUCCESS;
}

#endif
