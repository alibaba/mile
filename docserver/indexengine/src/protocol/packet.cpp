/*
 * packet.cpp
 *
 *  Created on: 2012-8-28
 *      Author: yuzhong.zhao
 */

#include "packet.h"

uint32_t sizeof_field_type(enum field_types ftype) {
	//首先根据数据类型确定数据的长度
	switch (ftype) {
	case HI_TYPE_TINY:
		return 1;
	case HI_TYPE_SHORT:
		return 2;
	case HI_TYPE_UNSIGNED_SHORT:
		return 2;
	case HI_TYPE_LONG:
		return 4;
	case HI_TYPE_UNSIGNED_LONG:
		return 4;
	case HI_TYPE_LONGLONG:
		return 8;
	case HI_TYPE_UNSIGNED_LONGLONG:
		return 8;
	case HI_TYPE_FLOAT:
		return 4;
	case HI_TYPE_DOUBLE:
		return 8;
	case HI_TYPE_VARCHAR:
		return 0;
	case HI_TYPE_STRING:
		return 0;
	default:
		log_error("unsupported data type %d!\n", ftype);
		return 0;
	}

}

/**
 *	从rbuf中读取动态数据类型，并放入mile的底层存储类型中
 *
 *	DynVlaue Define:
 *	DynValue = <int8_t tc> <int32_t value_len> <value>
 *	DynValue(String):  <int8_t tc> <int32_t length> <bytes UTF-8 Encoding>
 *	DynValue(NULL): <int8_t tc>
 *
 *	@param 	mem			内存池
 *  @param	rbuf				read buffer
 *	@param 	ldata				mile底层的存储结构
 *	@return
 */
int32_t read_dyn_value(MEM_POOL_PTR mem, struct data_buffer* rbuf,
		struct low_data_struct* ldata) {
	//数据长度
	uint32_t len = 0;

	//为处理不同数据类型时的临时变量
	uint8_t tmp_int8;
	uint16_t tmp_int16;
	uint32_t tmp_int32;
	uint64_t tmp_int64;

	// 读取数据类型
	ldata->type = (enum field_types) read_int8(rbuf);
	if (HI_TYPE_NULL == ldata->type) { // null data
		ldata->len = 0;
		return MILE_RETURN_SUCCESS;
	}
	// 读取数据长度
	len = read_int32(rbuf);

	if (len > rbuf->data_len - rbuf->rpos) {
		log_error("ldata中的数据长度 %d 不正常, 超出了read buffer的剩余大小!", len);
		return ERROR_PACKET_FORMAT;
	}

	if (len > mem->size) {
		// 数据的长度过长，直接返回错误
		log_error("数据长度 %d 过长, 无法从内存池中申请内存", len);
		return ERROR_NOT_ENOUGH_MEMORY;
	}

	//根据数据长度分配相应的存储空间
	ldata->data = mem_pool_malloc(mem, len);
	ldata->len = len;
	if (len == 0) {
		return MILE_RETURN_SUCCESS;
	}

	switch (ldata->type) {
	//对几种整形类型需要进行字节序的转换
	case HI_TYPE_TINY:
		tmp_int8 = read_int8(rbuf);
		memcpy(ldata->data, &tmp_int8, len);
		break;
	case HI_TYPE_SHORT:
		tmp_int16 = read_int16(rbuf);
		memcpy(ldata->data, &tmp_int16, len);
		break;
	case HI_TYPE_UNSIGNED_SHORT:
		tmp_int16 = read_int16(rbuf);
		memcpy(ldata->data, &tmp_int16, len);
		break;
	case HI_TYPE_LONG:
		tmp_int32 = read_int32(rbuf);
		memcpy(ldata->data, &tmp_int32, len);
		break;
	case HI_TYPE_UNSIGNED_LONG:
		tmp_int32 = read_int32(rbuf);
		memcpy(ldata->data, &tmp_int32, len);
		break;
	case HI_TYPE_LONGLONG:
		tmp_int64 = read_int64(rbuf);
		memcpy(ldata->data, &tmp_int64, len);
		break;
	case HI_TYPE_UNSIGNED_LONGLONG:
		tmp_int64 = read_int64(rbuf);
		memcpy(ldata->data, &tmp_int64, len);
		break;
	case HI_TYPE_FLOAT:
		tmp_int32 = read_int32(rbuf);
		memcpy(ldata->data, &tmp_int32, len);
		break;
	case HI_TYPE_DOUBLE:
		tmp_int64 = read_int64(rbuf);
		memcpy(ldata->data, &tmp_int64, len);
		break;
	case HI_TYPE_VARCHAR:
		read_bytes(rbuf, (uint8_t *) ldata->data, ldata->len);
		break;
	case HI_TYPE_STRING:
		read_bytes(rbuf, (uint8_t *) ldata->data, ldata->len);
		break;
	default:
		log_error("不支持的数据类型 %d!\n", ldata->type);
		return ERROR_PACKET_FORMAT;
	}

	return MILE_RETURN_SUCCESS;
}

/**
 * see read_dyn_value
 */
int32_t write_dyn_value(struct data_buffer* sbuf,
		struct low_data_struct* ldata) {
	//为处理不同数据类型时的临时变量
	int32_t result_code;
	int8_t tmp_int8;
	uint16_t tmp_int16;
	uint32_t tmp_int32;
	uint64_t tmp_int64;
	HashSet* set;
	MileArray* array;
	MileIterator* iter;

	if (NULL == ldata || HI_TYPE_NULL == ldata->type
			|| (ldata->len == 0 && ldata->type != HI_TYPE_STRING)) {
		write_int8(HI_TYPE_NULL, sbuf);
		return MILE_RETURN_SUCCESS;
	}
	if (ldata->type == HI_TYPE_NOTCOMP) {
		write_int8(HI_TYPE_NOTCOMP, sbuf);
		return MILE_RETURN_SUCCESS;
	}

	write_int8(ldata->type, sbuf);

	if (ldata->len == 0) {
		write_int32(0, sbuf);
		return MILE_RETURN_SUCCESS;
	}

	switch (ldata->type) {
	case HI_TYPE_TINY:
		tmp_int8 = *((int8_t*) ldata->data);
		write_int32(1, sbuf);
		write_int8(tmp_int8, sbuf);
		break;
	case HI_TYPE_SHORT:
		;
	case HI_TYPE_UNSIGNED_SHORT:
		tmp_int16 = *((unsigned short*) ldata->data);
		write_int32(2, sbuf);
		write_int16(tmp_int16, sbuf);
		break;
	case HI_TYPE_LONG:
		;
	case HI_TYPE_UNSIGNED_LONG:
		tmp_int32 = *((unsigned long*) ldata->data);
		write_int32(4, sbuf);
		write_int32(tmp_int32, sbuf);
		break;
	case HI_TYPE_LONGLONG:
		;
	case HI_TYPE_UNSIGNED_LONGLONG:
		tmp_int64 = *((unsigned long long*) ldata->data);
		write_int32(8, sbuf);
		write_int64(tmp_int64, sbuf);
		break;
	case HI_TYPE_FLOAT:
		tmp_int32 = *((unsigned long*) ldata->data);
		write_int32(4, sbuf);
		write_int32(tmp_int32, sbuf);
		break;
	case HI_TYPE_DOUBLE:
		tmp_int64 = *((unsigned long long*) ldata->data);
		write_int32(8, sbuf);
		write_int64(tmp_int64, sbuf);
		break;
	case HI_TYPE_VARCHAR:
		write_int32(ldata->len, sbuf);
		write_bytes((uint8_t *) ldata->data, ldata->len, sbuf);
		break;
	case HI_TYPE_STRING:
		write_int32(ldata->len, sbuf);
		write_bytes((uint8_t *) ldata->data, ldata->len, sbuf);
		break;
	case HI_TYPE_SET:
		write_int32(ldata->len, sbuf);
		set = (HashSet*) ldata->data;
		iter = set->CreateIterator();
		for (iter->First(); !iter->IsDone(); iter->Next()) {
			result_code = write_dyn_value(sbuf,
					(struct low_data_struct*) iter->CurrentItem());
			if (result_code < 0) {
				return result_code;
			}
		}
		break;
	case HI_TYPE_ARRAY:
		write_int32(ldata->len, sbuf);
		array = (MileArray*) ldata->data;
		iter = array->CreateIterator();
		for (iter->First(); !iter->IsDone(); iter->Next()) {
			result_code = write_dyn_value(sbuf,
					(struct low_data_struct*) iter->CurrentItem());
			if (result_code < 0) {
				return result_code;
			}
		}
		break;
	default:
		log_error("不支持的数据类型 %d!\n", ldata->type);
		return ERROR_PACKET_FORMAT;
	}

	return MILE_RETURN_SUCCESS;
}

int32_t print_ld(struct low_data_struct* ldata, char* buffer, int32_t size) {
	if (ldata->len == 0) {
		return snprintf_buffer(buffer, size, "NULL ");
	}
	switch (ldata->type) {
	case HI_TYPE_NULL:
		return snprintf_buffer(buffer, size, "NULL ");
	case HI_TYPE_TINY:
		return snprintf_buffer(buffer, size, "%d ", *(int8_t*) ldata->data);
	case HI_TYPE_SHORT:
		return snprintf_buffer(buffer, size, "%d ", *(int16_t*) ldata->data);
	case HI_TYPE_UNSIGNED_SHORT:
		return snprintf_buffer(buffer, size, "%d ", *(uint16_t*) ldata->data);
	case HI_TYPE_LONG:
		return snprintf_buffer(buffer, size, "%d ", *(int32_t*) ldata->data);
	case HI_TYPE_UNSIGNED_LONG:
		return snprintf_buffer(buffer, size, "%d ", *(uint32_t*) ldata->data);
	case HI_TYPE_LONGLONG:
		return snprintf_buffer(buffer, size, "%lld ", *(int64_t*) ldata->data);
	case HI_TYPE_UNSIGNED_LONGLONG:
		return snprintf_buffer(buffer, size, "%lld ", *(uint64_t*) ldata->data);
	case HI_TYPE_FLOAT:
		return snprintf_buffer(buffer, size, "%f ", *(float*) ldata->data);
	case HI_TYPE_DOUBLE:
		return snprintf_buffer(buffer, size, "%lf ", *(double*) ldata->data);
	case HI_TYPE_VARCHAR:
	case HI_TYPE_STRING:
		return snprintf_buffer(buffer, size, "%*.*s ", ldata->len, ldata->len,
				(char*) ldata->data);
	default:
		return snprintf_buffer(buffer, size, "不支持的数据类型 %d", ldata->type);
	}

}

int32_t print_condition(struct condition_array* cond_array, char* buffer,
		int32_t size) {
	uint32_t i, j;
	int offset = 0;
	struct condition_t* condition;

	for (i = 0; i < cond_array->n; i++) {
		condition = &cond_array->conditions[i];
		switch (condition->type) {
		case LOGIC_AND:
			offset += snprintf_buffer(buffer + offset, size - offset, "and ");
			break;
		case LOGIC_OR:
			offset += snprintf_buffer(buffer + offset, size - offset, "or ");
			break;
		case HC_SET_MINUS:
			offset += snprintf_buffer(buffer + offset, size - offset, "minus ");
			break;
		case CONDITION_EXP:
			offset += snprintf_buffer(buffer + offset, size - offset, "%s ",
					condition->field_name);
			switch (condition->comparator) {
			case CT_EQ:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" = ");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				break;
			case CT_GT:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" > ");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				break;
			case CT_GE:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" >= ");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				break;
			case CT_LT:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" < ");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				break;
			case CT_LE:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" <= ");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				break;
			case EXP_COMPARE_BETWEEN_LEG:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" between [");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				offset += print_ld(&condition->values[1], buffer + offset,
						size - offset);
				offset += snprintf_buffer(buffer + offset, size - offset, ") ");
				break;
			case EXP_COMPARE_BETWEEN_LEGE:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" between [");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				offset += print_ld(&condition->values[1], buffer + offset,
						size - offset);
				offset += snprintf_buffer(buffer + offset, size - offset, "] ");
				break;
			case EXP_COMPARE_BETWEEN_LG:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" between (");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				offset += print_ld(&condition->values[1], buffer + offset,
						size - offset);
				offset += snprintf_buffer(buffer + offset, size - offset, ") ");
				break;
			case EXP_COMPARE_BETWEEN_LGE:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" between (");
				offset += print_ld(&condition->values[0], buffer + offset,
						size - offset);
				offset += print_ld(&condition->values[1], buffer + offset,
						size - offset);
				offset += snprintf_buffer(buffer + offset, size - offset, "] ");
				break;
			case EXP_COMPARE_IN:
				offset += snprintf_buffer(buffer + offset, size - offset,
						"in (");
				for (j = 0; j < condition->value_num; j++) {
					offset += print_ld(&condition->values[j], buffer + offset,
							size - offset);
				}
				offset += snprintf_buffer(buffer + offset, size - offset, ") ");
				break;
			default:
				offset += snprintf_buffer(buffer + offset, size - offset,
						" 不支持的比较类型 %d", condition->comparator);
				return offset;
			}
			break;
		default:
			offset += snprintf_buffer(buffer + offset, size - offset,
					" 不支持的index过滤类型 %d", condition->type);
			return offset;
		}
	}

	return offset;
}

void print_insert_packet(struct insert_packet* packet) {
	if (!is_info_enabled())
		return;

	uint32_t i;
	int offset = 0;
	int size = 1000;
	char buffer[1000];
	struct low_data_struct* ldata;

	offset += snprintf_buffer(buffer + offset, size - offset,
			"insert into %s values (", packet->table_name);

	for (i = 0; i < packet->column_num; i++) {
		ldata = &packet->datas[i];
		offset += snprintf_buffer(buffer + offset, size - offset, "%s=",
				ldata->field_name);
		offset += print_ld(&packet->datas[i], buffer + offset, size - offset);
		offset += snprintf_buffer(buffer + offset, size - offset, " ");
	}

	offset += snprintf_buffer(buffer + offset, size - offset, ")");

	log_info( "%s", buffer);
}

void print_delete_packet(struct delete_packet* packet) {
	if (!is_info_enabled())
		return;

	uint32_t i;
	int offset = 0;
	int size = 1000;
	char buffer[1000];

	offset += snprintf_buffer(buffer + offset, size - offset,
			"delete from %s\n", packet->table_name);

	offset += snprintf_buffer(buffer + offset, size - offset, "seghint(");
	for (i = 0; i < packet->hi_array.n; i++) {
		offset += snprintf_buffer(buffer + offset, size - offset, "%llu, ",
				packet->hi_array.hints[i]);
	}
	offset += snprintf_buffer(buffer + offset, size - offset, ") ");

	if (packet->index_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset,
				"indexwhere ");
		offset += print_condition(&packet->index_cond, buffer + offset,
				size - offset);
	}

	if (packet->filter_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset, "where ");
		offset += print_condition(&packet->filter_cond, buffer + offset,
				size - offset);
	}

	log_info("%s", buffer);
}

static void print_export_packet(struct export_packet *packet) {
	if (!is_info_enabled())
		return;

	uint32_t i;
	int offset = 0;
	int size = 1000;
	char buffer[1000];

	offset += snprintf_buffer(buffer + offset, size - offset, "export to %s ",
			packet->save_path);

	offset += snprintf_buffer(buffer + offset, size - offset, "from %s ",
			packet->table_name);

	offset += snprintf_buffer(buffer + offset, size - offset, "seghint(");
	for (i = 0; i < packet->hi_array.n; i++) {
		offset += snprintf_buffer(buffer + offset, size - offset, "%llu, ",
				packet->hi_array.hints[i]);
	}
	offset += snprintf_buffer(buffer + offset, size - offset, ") ");

	if (packet->index_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset,
				"indexwhere ");
		offset += print_condition(&packet->index_cond, buffer + offset,
				size - offset);
	}

	if (packet->filter_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset, "where ");
		offset += print_condition(&packet->filter_cond, buffer + offset,
				size - offset);
	}

offset += snprintf_buffer(buffer + offset, size - offset, " limit %" PRIi64, packet->limit);

		log_info("%s", buffer);
}

void print_update_packet(struct update_packet* packet) {
	if (!is_info_enabled())
		return;

	uint32_t i;
	int offset = 0;
	int size = 1000;
	char buffer[1000];

	offset += snprintf_buffer(buffer + offset, size - offset, "update ",
			packet->table_name);

	offset += snprintf_buffer(buffer + offset, size - offset, "set %s=",
			packet->field_name);
	offset += print_ld(&packet->data, buffer + offset, size - offset);

	offset += snprintf_buffer(buffer + offset, size - offset, "seghint( ");
	for (i = 0; i < packet->hi_array.n; i++) {
		offset += snprintf_buffer(buffer + offset, size - offset, "%llu, ",
				packet->hi_array.hints[i]);
	}
	offset += snprintf_buffer(buffer + offset, size - offset, ") ");

	if (packet->index_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset,
				"indexwhere ");
		offset += print_condition(&packet->index_cond, buffer + offset,
				size - offset);
	}

	if (packet->filter_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset, "where ");
		offset += print_condition(&packet->filter_cond, buffer + offset,
				size - offset);
	}

	log_info("%s", buffer);
}

void print_query_packet(struct query_packet* packet) {
	if (!is_info_enabled())
		return;

	uint32_t i;
	int offset = 0;
	int size = 1000;
	char buffer[1000];

	offset += snprintf_buffer(buffer + offset, size - offset, "select ");
	if (packet->access_type == ACCESS_TYPE_DISTINCT) {
		offset += snprintf_buffer(buffer + offset, size - offset, "distinct ");
	}

	for (i = 0; i < packet->select_field.n; i++) {
		offset += snprintf_buffer(buffer + offset, size - offset, "%s ",
				packet->select_field.select_fields[i].field_name);
	}

	offset += snprintf_buffer(buffer + offset, size - offset, "from %s",
			packet->table_name);

	offset += snprintf_buffer(buffer + offset, size - offset, "seghint( ");
	for (i = 0; i < packet->hi_array.n; i++) {
		offset += snprintf_buffer(buffer + offset, size - offset, "%llu, ",
				packet->hi_array.hints[i]);
	}
	offset += snprintf_buffer(buffer + offset, size - offset, ") ");

	if (packet->index_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset,
				"indexwhere ");
		offset += print_condition(&packet->index_cond, buffer + offset,
				size - offset);
	}

	if (packet->filter_cond.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset, "where ");
		offset += print_condition(&packet->filter_cond, buffer + offset,
				size - offset);
	}

	if (packet->group_array.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset, "group by ");
		for (i = 0; i < packet->group_array.n; i++) {
			offset += snprintf_buffer(buffer + offset, size - offset, "%s ",
					packet->group_array.group_fields[i].field_name);
		}
	}

	if (packet->order_array.n > 0) {
		offset += snprintf_buffer(buffer + offset, size - offset, "order by ");
		for (i = 0; i < packet->order_array.n; i++) {
			offset += snprintf_buffer(buffer + offset, size - offset, "%s ",
					packet->order_array.order_fields[i].field_name);
			if (packet->order_array.order_fields[i].order_type
					== ORDER_TYPE_DESC) {
				offset += snprintf_buffer(buffer + offset, size - offset,
						"DESC, ");
			}
		}
	}

	offset += snprintf_buffer(buffer + offset, size - offset, "limit %d",
			packet->limit);

	log_info("%s", buffer);
	return;
}

int32_t parse_condition(MEM_POOL_PTR mem, struct data_buffer *rbuf,
		struct condition_array *cond_array) {
	cond_array->n = read_int32(rbuf);
	if (0 == cond_array->n)
		return MILE_RETURN_SUCCESS;

	cond_array->conditions = (struct condition_t *) mem_pool_malloc(mem,
			cond_array->n * sizeof(struct condition_t));
	memset(cond_array->conditions, 0,
			cond_array->n * sizeof(struct condition_t));

	uint32_t i, j;
	for (i = 0; i < cond_array->n; i++) {
		struct condition_t *cond = &cond_array->conditions[i];
		cond->type = (enum condition_type) read_int8(rbuf);
		switch (cond->type) {
		case CONDITION_EXP:
			cond->field_name = read_cstring(rbuf, mem);
			cond->comparator = (enum compare_type) read_int8(rbuf);
			cond->value_num = read_int32(rbuf);
			cond->values = (struct low_data_struct*) mem_pool_malloc(mem,
					cond->value_num * sizeof(struct low_data_struct));
			memset(cond->values, 0,
					cond->value_num * sizeof(struct low_data_struct));
			for (j = 0; j < cond->value_num; j++) {
				if (read_dyn_value(mem, rbuf, &cond->values[j]) < 0) {
					log_error( "read condition's value failed");
					return ERROR_PACKET_FORMAT;
				}

				// point low_data_struct's field_name to conditon's field_name
				cond->values[j].field_name = cond->field_name;
			}
			break;
		case LOGIC_AND:
		case LOGIC_OR:
			break;
		default:
			log_error( "unknown condition type: %d", cond->type);
			return ERROR_PACKET_FORMAT;
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t parse_hint_array(MEM_POOL_PTR mem, struct data_buffer* rbuf,
		struct hint_array* hi_array) {
	uint32_t i;
	hi_array->n = read_int32(rbuf);
	if (hi_array->n != 4) {
		log_error("提示列数目为 %d, 提示列应该只有 4 列!", hi_array->n);
		return ERROR_PACKET_FORMAT;
	}

	hi_array->hints = (uint64_t*) mem_pool_malloc(mem,
			sizeof(uint64_t) * hi_array->n);
	memset(hi_array->hints, 0, sizeof(uint64_t) * hi_array->n);

	for (i = 0; i < hi_array->n; i++) {
		hi_array->hints[i] = read_int64(rbuf);
	}

	return MILE_RETURN_SUCCESS;
}

int32_t parse_select_field_array(MEM_POOL_PTR mem, struct data_buffer *rbuf,
		struct select_field_array *select_field) {
	select_field->n = read_int32(rbuf);
	uint32_t i;
	select_field->select_fields = (struct select_field_t *) mem_pool_malloc(mem,
			select_field->n * sizeof(struct select_field_t));
	memset(select_field->select_fields, 0,
			select_field->n * sizeof(struct select_field_t));
	uint8_t code;

	for (i = 0; i < select_field->n; i++) {
		struct select_field_t *field = &select_field->select_fields[i];
		field->type = (enum select_field_type) read_int8(rbuf);
		switch (field->type) {
		case VALUE_SELECT:
			field->field_name = read_cstring(rbuf, mem);
			field->alise_name = read_cstring(rbuf, mem);
			break;
		case FUNCTION_SELECT:
			code = read_int8(rbuf);
			field->select_name = read_cstring(rbuf, mem);
			field->alise_name = read_cstring(rbuf, mem);
			field->field_name = read_cstring(rbuf, mem);
			if (code >= 128) {
				field->func_type = (enum function_type) (code - 128);
				field->range_exp = (struct condition_array*) mem_pool_malloc(
						mem, sizeof(struct condition_array));
				memset(field->range_exp, 0, sizeof(struct condition_array));
				parse_condition(mem, rbuf, field->range_exp);
			} else {
				field->func_type = (enum function_type) code;
				field->range_exp = NULL;
			}
			break;
		case STAR_SELECT:
			// read nothing.
			break;
		default:
			return -1;
		}
	}

	return MILE_RETURN_SUCCESS;
}

int32_t parse_group_field(MEM_POOL_PTR mem, struct data_buffer *rbuf,
		struct group_field_array *group) {
	group->n = read_int32(rbuf);
	if (0 == group->n)
		return MILE_RETURN_SUCCESS;

	group->group_fields = (struct group_field_t*) mem_pool_malloc(mem,
			group->n * sizeof(struct group_field_t));
	memset(group->group_fields, 0, group->n * sizeof(struct group_field_t));
	uint32_t i;
	for (i = 0; i < group->n; i++) {
		group->group_fields[i].field_name = read_cstring(rbuf, mem);
	}

	return MILE_RETURN_SUCCESS;
}

int32_t parse_order_field(MEM_POOL_PTR mem, struct data_buffer *rbuf,
		struct order_field_array *order_field) {
	order_field->n = read_int32(rbuf);
	if (0 == order_field->n)
		return MILE_RETURN_SUCCESS;

	order_field->order_fields = (struct order_field_t*) mem_pool_malloc(mem,
			order_field->n * sizeof(struct order_field_t));
	memset(order_field->order_fields, 0,
			order_field->n * sizeof(struct order_field_t));
	for (uint32_t i = 0; i < order_field->n; i++) {
		order_field->order_fields[i].field_name = read_cstring(rbuf, mem);
		order_field->order_fields[i].order_type = (enum order_types) read_int8(
				rbuf);
	}

	return MILE_RETURN_SUCCESS;
}

int8_t parse_hb_packet(struct data_buffer* rbuf) {
	int8_t result_code;

	/* 0 -- failure; 2 -- health */
	result_code = read_int16(rbuf);
	log_debug("mergeserver state --> %d", result_code);
	if (result_code) {
		result_code = MERGESERVER_HEALTH;
	}

	return result_code;
}

struct insert_packet *parse_insert_packet(MEM_POOL_PTR mem,
		struct data_buffer *rbuf) {
	struct insert_packet *packet = (struct insert_packet*) mem_pool_malloc(mem,
			sizeof(struct insert_packet));
	memset(packet, 0, sizeof(struct insert_packet));

	packet->table_name = read_cstring(rbuf, mem);
	packet->column_num = read_int32(rbuf);
	if (packet->column_num > 1024) {
		log_error("插入列数目为 %d, 最多允许1024列!", packet->column_num);
		return NULL;
	}

	packet->datas = (struct low_data_struct*) mem_pool_malloc(mem,
			packet->column_num * sizeof(struct low_data_struct));
	memset(packet->datas, 0,
			packet->column_num * sizeof(struct low_data_struct));

	for (uint32_t i = 0; i < packet->column_num; i++) {
		packet->datas[i].field_name = read_cstring(rbuf, mem);
		if (read_dyn_value(mem, rbuf, &packet->datas[i]) < 0) {
			log_error( "parse insert packet failed");
			return NULL;
		}
	}

	print_insert_packet(packet);

	if (rbuf->array_border != 0) {
		log_error( "parse insert packet failed");
		return NULL;
	}

	return packet;
}

struct delete_packet* parse_delete_packet(MEM_POOL_PTR mem,
		struct data_buffer *rbuf) {
	struct delete_packet* packet = (struct delete_packet*) mem_pool_malloc(mem,
			sizeof(struct delete_packet));
	memset(packet, 0, sizeof(struct delete_packet));

	packet->table_name = read_cstring(rbuf, mem);

	parse_hint_array(mem, rbuf, &packet->hi_array);

	// parse index condition
	if (parse_condition(mem, rbuf, &packet->index_cond) < 0) {
		log_error("parse delete packet's index conditions failed");
		return NULL;
	}

	// parse filter condition
	if (parse_condition(mem, rbuf, &packet->filter_cond) < 0) {
		log_error("parse delete packet's filter conditions failed");
		return NULL;
	}
	print_delete_packet(packet);

	if (0 != rbuf->array_border) {
		log_error( "parse delete packet faield");
		return NULL;
	}
	return packet;
}

struct export_packet *parse_export_packet(MEM_POOL_PTR mem,
		struct data_buffer *rbuf) {
	struct export_packet *packet = (struct export_packet *) mem_pool_malloc(mem,
			sizeof(struct export_packet));
	memset(packet, 0, sizeof(struct export_packet));

	packet->table_name = read_cstring(rbuf, mem);
	packet->save_path = read_cstring(rbuf, mem);

	parse_hint_array(mem, rbuf, &packet->hi_array);

	// parse index condition
	if (parse_condition(mem, rbuf, &packet->index_cond) < 0) {
		log_error("parse export packet's index conditions failed");
		return NULL;
	}

	// parse filter condition
	if (parse_condition(mem, rbuf, &packet->filter_cond) < 0) {
		log_error("parse export packet's filter conditions failed");
		return NULL;
	}

	packet->limit = read_int64(rbuf);

	print_export_packet(packet);

	if (0 != rbuf->array_border) {
		log_error( "parse export packet faield");
		return NULL;
	}
	return packet;
}

struct update_packet *parse_update_packet(MEM_POOL_PTR mem,
		struct data_buffer *rbuf) {
	struct update_packet* packet = (struct update_packet*) mem_pool_malloc(mem,
			sizeof(struct update_packet));
	memset(packet, 0, sizeof(struct update_packet));

	packet->table_name = read_cstring(rbuf, mem);
	packet->field_name = read_cstring(rbuf, mem);
	if (read_dyn_value(mem, rbuf, &packet->data) < 0) {
		log_error("parse update packet's upate value failed");
		return NULL;
	}

	// link low_data_struct::field_name to update_packet:field_name
	packet->data.field_name = packet->field_name;

	parse_hint_array(mem, rbuf, &packet->hi_array);

	// parse index condition
	if (parse_condition(mem, rbuf, &packet->index_cond) < 0) {
		log_error("parse update packet's index conditions failed");
		return NULL;
	}

	// parse filter condition
	if (parse_condition(mem, rbuf, &packet->filter_cond) < 0) {
		log_error("parse update packet's filter conditions failed");
		return NULL;
	}

	print_update_packet(packet);

	if (0 != rbuf->array_border) {
		log_error( "parse update packet failed");
		return NULL;
	}

	return packet;
}

struct query_packet* parse_query_packet(MEM_POOL_PTR mem,
		struct data_buffer* rbuf) {
	struct query_packet *packet = (struct query_packet*) mem_pool_malloc(mem,
			sizeof(struct query_packet));
	memset(packet, 0, sizeof(struct query_packet));

	packet->access_type = read_int16(rbuf);
	packet->table_name = read_cstring(rbuf, mem);

	// parse segment hint
	if (parse_hint_array(mem, rbuf, &packet->hi_array) < 0) {
		log_error( "parse hint failed");
		return NULL;
	}

	// parse select fields
	if (parse_select_field_array(mem, rbuf, &packet->select_field) < 0) {
		log_error( "parse select fields failed");
		return NULL;
	}

	// parse index condition
	if (parse_condition(mem, rbuf, &packet->index_cond) < 0) {
		log_error( "parse index condition failed");
		return NULL;
	}

	// parse filter condition
	if (parse_condition(mem, rbuf, &packet->filter_cond) < 0) {
		log_error( "parse filter condition failed");
		return NULL;
	}

	// parse group by fields
	if (parse_group_field(mem, rbuf, &packet->group_array) < 0) {
		log_error( "parse group fields failed");
		return NULL;
	}

	// parse group order fields
	if (parse_order_field(mem, rbuf, &packet->group_order_array) < 0) {
		log_error( "parse group order field");
		return NULL;
	}

	// gorup limit
	packet->group_limit = read_int32(rbuf);

	// parse order field
	if (parse_order_field(mem, rbuf, &packet->order_array) < 0) {
		log_error( "parse order fields failed");
		return NULL;
	}

	// limit
	packet->limit = read_int32(rbuf);

	if (0 != rbuf->array_border) {
		log_error( "prase query packet failed.");
		return NULL;
	}

	print_query_packet(packet);

	return packet;
}

void gen_query_result_packet(ResultSet* result,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {

	uint32_t i, j;
	MileIterator* iter;
	struct select_row_t* row;
	uint32_t data_pos = 0;

	clear_data_buffer(sbuf);

	//msg的长度
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//结果的行数
	write_int32(result->Size(), sbuf);

	//写入统计信息
	write_int32(0, sbuf);

	//写入数据
	data_pos = sbuf->wpos;
	write_int32(0, sbuf);
	iter = result->CreateIterator();
	for (iter->First(); !iter->IsDone(); iter->Next()) {
		row = (struct select_row_t*) iter->CurrentItem();
		//handler
		if (NULL == row->handler) {
			write_int64(0, sbuf);
		} else {
			write_int64(((MileHandler*) (row->handler))->GetHandlerId(), sbuf);
		}
		for (i = 0; i < row->n; i++) {
			write_dyn_value(sbuf, &row->data[i]);
		}
	}
	fill_int32(sbuf->data_len, sbuf, 0);
	fill_int32(sbuf->data_len - data_pos - 4, sbuf, data_pos);
}

void gen_insert_result_packet(uint64_t docid,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	clear_data_buffer(sbuf);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//result rows
	write_int32(1, sbuf);
	//stat array
	write_int32(0, sbuf);
	//values: bytes
	write_int32(8, sbuf);
	//docid
	write_int64(docid, sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

void gen_update_result_packet(uint32_t update_num,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	clear_data_buffer(sbuf);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//result rows
	write_int32(1, sbuf);
	//stat array
	write_int32(0, sbuf);
	//values: bytes
	write_int32(4, sbuf);
	//docid
	write_int32(update_num, sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

void gen_delete_result_packet(uint32_t delete_num,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	clear_data_buffer(sbuf);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//result rows
	write_int32(1, sbuf);
	//stat array
	write_int32(0, sbuf);
	//values: bytes
	write_int32(4, sbuf);
	//docid
	write_int32(delete_num, sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

void gen_export_result_packet(uint64_t export_num,
		struct mile_message_header *msg_head, struct data_buffer *sbuf) {
	clear_data_buffer(sbuf);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//result rows
	write_int32(1, sbuf);
	//stat array
	write_int32(0, sbuf);
	//values: bytes
	write_int32(8, sbuf);

	// export number
	write_int64(export_num, sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

void gen_error_packet(int32_t result_code, struct mile_message_header* msg_head,
		struct data_buffer* sbuf) {
	char const* err_desc = error_msg(result_code);
	clear_data_buffer(sbuf);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_SQL_EXC_ERROR, sbuf);
	write_int32(msg_head->message_id, sbuf);

	//error code
	write_int16((int16_t) result_code, sbuf);
	//error parameter array
	write_int32(0, sbuf);
	//error description string
	write_int32(strlen(err_desc), sbuf);
	write_bytes((uint8_t*) err_desc, strlen(err_desc), sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

void gen_docserver_client_error_packet(uint32_t rc,
		struct mile_message_header *msg_head, struct data_buffer *sbuf) {
	clear_data_buffer(sbuf);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DC_ERROR, sbuf);
	write_int32(msg_head->message_id, sbuf);

	write_int32((int32_t) rc, sbuf);
	fill_int32(sbuf->data_len, sbuf, 0);
}

struct slave_sync_req *parse_slave_sync_req(MEM_POOL_PTR mem,
		struct data_buffer *rbuf) {
	struct slave_sync_req *packet = (struct slave_sync_req *) mem_pool_malloc(
			mem, sizeof(struct slave_sync_req));
	// use read_bytes instead of read_int64, because offset is big endian
	read_bytes(rbuf, (uint8_t *) &packet->offset, 8);
	log_debug( "get binlog request, offset %llu", packet->offset);
	if (0 != rbuf->array_border) {
		log_error( "parse get binlog request failed");
		return NULL;
	}
	return packet;
}

void gen_slave_sync_res_packet(struct slave_sync_res *binlog_res,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	const uint32_t header_size = 12;
	const uint32_t size = header_size + sizeof(struct slave_sync_res)
			+ binlog_res->len;

	clear_data_buffer(sbuf);
	databuf_resize(sbuf, size);

	//msg length
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	write_bytes((uint8_t*) binlog_res, size - header_size, sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

void gen_slave_sync_req_packet(uint32_t message_id, uint64_t offset,
		struct data_buffer *sbuf) {
	const uint32_t header_size = 16;
	const uint32_t size = header_size + sizeof(offset);

	clear_data_buffer(sbuf);
	databuf_resize(sbuf, size);

	write_int32(0, sbuf);
	write_int8(MILE_DOC_SERVER_MAJOR_VER, sbuf);
	write_int8(MILE_DOC_SERVER_MINOR_VER, sbuf);
	write_int16(MT_SM_GET_BINLOG, sbuf);
	write_int32(message_id, sbuf);
	write_int32(0, sbuf); // timeout

	// write body
	write_bytes((uint8_t*) &offset, sizeof(offset), sbuf);

	fill_int32(sbuf->data_len, sbuf, 0);
}

struct get_state_packet* parse_get_state_packet(MEM_POOL_PTR mem,
		struct data_buffer* rbuf) {
	uint32_t i;
	uint16_t len;
	struct get_state_packet* packet =
			(struct get_state_packet*) mem_pool_malloc(mem,
					sizeof(struct get_state_packet));
	memset(packet, 0, sizeof(struct get_state_packet));

	packet->n = read_int32(rbuf);
	packet->state_names = (uint8_t**) mem_pool_malloc(mem,
			sizeof(uint8_t*) * packet->n);
	for (i = 0; i < packet->n; i++) {
		len = read_int32(rbuf);
		packet->state_names[i] = (uint8_t*) mem_pool_malloc(mem, len + 1);
		memset(packet->state_names[i], 0, len + 1);
		read_bytes(rbuf, packet->state_names[i], len);
	}

	if (rbuf->array_border == 0) {
		return packet;
	} else {
		log_error("解析get state命令时出错, 可能是错误的数据报文.");
		return NULL;
	}
}

void gen_state_result_packet(struct stat_info_array* stat_array,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	uint32_t i;
	struct stat_info* stat;

	clear_data_buffer(sbuf);

	//msg的长度
	write_int32(0, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DM_STATE_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	write_int32(stat_array->n, sbuf);
	for (i = 0; i < stat_array->n; i++) {
		stat = &(stat_array->stat[i]);
		write_int32(stat->name_len, sbuf);
		write_bytes(stat->name, stat->name_len, sbuf);
		write_dyn_value(sbuf, stat->ldata);
	}
	fill_int32(sbuf->data_len, sbuf, 0);
}

struct set_load_threshold_packet *parse_set_load_threshold_packet(
		MEM_POOL_PTR pMemPool, struct data_buffer *rbuf) {
	struct set_load_threshold_packet *packet =
			(struct set_load_threshold_packet*) mem_pool_malloc(pMemPool,
					sizeof(struct set_load_threshold_packet));
	memset(packet, 0, sizeof(struct set_load_threshold_packet));
	read_bytes(rbuf, (uint8_t *) &packet->value, sizeof(packet->value));
	return packet;
}

void gen_dc_get_load_threshold_packet(double load,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	uint32_t size = 12 + sizeof(double);
	sbuf->array_border = 0;
	sbuf->rpos = 0;
	sbuf->wpos = 0;
	sbuf->data_len = size;
	sbuf->data = (uint8_t*) malloc(size);
	memset(sbuf->data, 0, size);

	//msg length
	write_int32(size, sbuf);
	write_int8(msg_head->version_major, sbuf);
	write_int8(msg_head->version_minor, sbuf);
	write_int16(MT_DC_EXE_RS, sbuf);
	write_int32(msg_head->message_id, sbuf);

	// value
	write_bytes((uint8_t *) &load, sizeof(load), sbuf);
}

void gen_dc_set_load_threshold_packet(double load,
		struct mile_message_header* msg_head, struct data_buffer* sbuf) {
	gen_dc_get_load_threshold_packet(load, msg_head, sbuf);
}

