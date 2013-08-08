#include "def.h"
#include <iostream>
#include <algorithm>
extern "C"
{
#include "../../indexengine/src/include/hyperindex_packet_execute.h"
#include "../../indexengine/src/include/hyperindex_protocol.h"
#include "hi_db_mock.h"
}





uint32 get_field_type_len(enum field_types field_type)
{
	switch(field_type)
	{
		case HI_TYPE_SHORT:
			return sizeof(int16);
		case HI_TYPE_UNSIGNED_SHORT:
			return sizeof(int16);
		case HI_TYPE_LONG:
			return sizeof(int32);
		case HI_TYPE_UNSIGNED_LONG:
			return sizeof(uint32);
		case HI_TYPE_LONGLONG:
			return sizeof(long long);
		case HI_TYPE_UNSIGNED_LONGLONG:
			return sizeof(unsigned long long);
		case HI_TYPE_FLOAT:
			return sizeof(float);
		case HI_TYPE_DOUBLE:
			return sizeof(double);
		default:
			printf("不支持的数据类型!\n");
			exit(1);
	}
}





bool check_low_data_struct_equal(struct low_data_struct* a, struct low_data_struct* b)
{
	int32 i;
	uint8 *p = (uint8*)a->data;
	uint8 *q = (uint8*)b->data;
	
	if(a->len != b->len)
		return false;

	for(i = 0; i < a->len; i++)
	{
		if(*p != *q)
		{
			return false;
		}
		p++;
		q++;
	}

	return true;
}






void gen_low_data_struct(MEM_POOL_PTR pMemPool, struct low_data_struct* ldata, enum field_types field_type)
{
	ldata->len = get_field_type_len(field_type);
	ldata->data = mem_pool_malloc(pMemPool, ldata->len);

	int32 i;
	for(i = 0; i < ldata->len; i++)
	{
		*((uint8*)ldata->data + i) = rand()%256;
	}

}



struct low_data_struct** gen_row_record(MEM_POOL_PTR pMemPool, uint32 column_num, enum field_types* fields_type_array)
{
	uint32 i;
	struct low_data_struct** result = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*) * column_num);

	for(i = 0; i < column_num; i++)
	{
		result[i] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		gen_low_data_struct(pMemPool, result[i], fields_type_array[i]);
	}

	return result;
}







void gen_low_data_struct(MEM_POOL_PTR pMemPool, struct low_data_struct* ldata, enum field_types field_type, uint32 i)
{
	ldata->len = get_field_type_len(field_type);
	ldata->data = mem_pool_malloc(pMemPool, ldata->len);

	switch(field_type)
	{
		case HI_TYPE_SHORT:
			*((int16*)ldata->data) = (int16)i;
			return;
		case HI_TYPE_UNSIGNED_SHORT:
			*((uint16*)ldata->data) = (uint16)i;
			return;
		case HI_TYPE_LONG:
			*((int32*)ldata->data) = (int32)i;
			return;
		case HI_TYPE_UNSIGNED_LONG:
			*((uint32*)ldata->data) = (uint32)i;
			return;
		case HI_TYPE_LONGLONG:
			*((int64*)ldata->data) = (int64)i;
			return;
		case HI_TYPE_UNSIGNED_LONGLONG:
			*((uint64*)ldata->data) = (uint64)i;
			return;
		case HI_TYPE_FLOAT:
			*((float*)ldata->data) = (float)i;
			return;
		case HI_TYPE_DOUBLE:
			*((double*)ldata->data) = (double)i;
			return;
		default:
			printf("不支持的数据类型!\n");
			exit(1);
	}


}



struct low_data_struct* gen_row_record(MEM_POOL_PTR pMemPool, uint32 column_num, enum field_types* fields_type_array, uint32 n)
{
	uint32 i;
	struct low_data_struct* result = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct) * column_num);

	for(i = 0; i < column_num; i++)
	{
		gen_low_data_struct(pMemPool, &result[i], fields_type_array[i], n);
	}

	return result;
}













TEST(PACKET_EXECUTE_TEST, INSERT){
	uint64 docid;
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	init_profile(10, pMemPool);


	
	int32 result_code;
	list_head* list;
	struct insert_packet* packet = (struct insert_packet*) mem_pool_malloc(pMemPool, sizeof(struct insert_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	mock_db_init(4, fields_type_array);
	
	packet->table_id = 0;
	packet->column_num = 4;
	packet->ldata_array = gen_row_record(pMemPool, packet->column_num, fields_type_array);


	docid = execute_insert(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);
	ASSERT_EQ(docid, 0x0001000000000000);


	list = db_query_by_value(0, 0, packet->ldata_array[0], pMemPool);
	ASSERT_EQ(is_seg_rowid_list_empty(list), 0);

	mem_pool_destroy(pMemPool);
}











TEST(PACKET_EXECUTE_TEST, DELETE_BY_ID){
	uint32 i;
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	uint16 segid;
	uint32 rowid;
	uint32 del_num;

	init_profile(10, pMemPool);

	
	int32 result_code;
	struct low_data_struct** record;
	struct delete_by_id_packet* packet = (struct delete_by_id_packet*) mem_pool_malloc(pMemPool, sizeof(struct delete_by_id_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	mock_db_init(4, fields_type_array);
	
	packet->table_id = 0;
	packet->docid_num = 10;
	packet->docid_array = (uint64*) mem_pool_malloc(pMemPool, sizeof(uint64)*packet->docid_num);

	for(i = 0; i < packet->docid_num; i++)
	{
		packet->docid_array[i] = i;
		record = gen_row_record(pMemPool, 4, fields_type_array);
		db_insert(0, &segid, &rowid, record);
	}

	for(i = 0; i < packet->docid_num; i++)
	{
		ASSERT_EQ(db_is_rowid_deleted(0, (uint16)(packet->docid_array[i]>>32), (uint32)packet->docid_array[i]), 0);
	}

	
	del_num = execute_delete_by_id(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);
	ASSERT_EQ(del_num, packet->docid_num);


	for(i = 0; i < packet->docid_num; i++)
	{
		ASSERT_EQ(db_is_rowid_deleted(0, (uint16)(packet->docid_array[i]>>32), (uint32)packet->docid_array[i]), 1);
	}


	mem_pool_destroy(pMemPool);
}









TEST(PACKET_EXECUTE_TEST, DELETE){
	uint32 i;
	uint32 del_num;
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);

	init_profile(10, pMemPool);

	uint16 segid;
	uint32 rowid;

	
	int32 result_code;
	struct low_data_struct* record;	
	struct delete_packet* packet = (struct delete_packet*) mem_pool_malloc(pMemPool, sizeof(struct delete_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	mock_db_init(4, fields_type_array);
	
	packet->table_id = 0;
	packet->hi_array.n = 0;
	packet->filter_cond_array.n = 0;
	packet->hash_cond_array.n = 0;

	del_num = execute_delete(pMemPool, packet, &result_code);
	ASSERT_LT(result_code, 0);


	for(i = 0; i < 1000; i++)
	{
		record = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct) * 4);
		gen_low_data_struct(pMemPool, &record[0], fields_type_array[0], i%5);
		gen_low_data_struct(pMemPool, &record[1], fields_type_array[1], i%7);
		gen_low_data_struct(pMemPool, &record[2], fields_type_array[2], i);
		gen_low_data_struct(pMemPool, &record[3], fields_type_array[3], i);		
		db_insert(0, &segid, &rowid, &record);
	}





	packet->hash_cond_array.n = 3;
	packet->hash_cond_array.hash_cond = (struct hash_condition*) mem_pool_malloc(pMemPool, sizeof(struct hash_condition) * packet->hash_cond_array.n);
	packet->hash_cond_array.hash_cond[0].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[0].column_id = 0;
	packet->hash_cond_array.hash_cond[0].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[0].value, fields_type_array[0], 0);
	packet->hash_cond_array.hash_cond[1].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[1].column_id = 1;
	packet->hash_cond_array.hash_cond[1].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[1].value, fields_type_array[1], 0);
	packet->hash_cond_array.hash_cond[2].type = HC_SET_AND;


	
	packet->filter_cond_array.n = 3;
	packet->filter_cond_array.filter_cond = (struct filter_condition*) mem_pool_malloc(pMemPool, sizeof(struct filter_condition) * packet->filter_cond_array.n);
	packet->filter_cond_array.filter_cond[0].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[0].column_id = 2;
	packet->filter_cond_array.filter_cond[0].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[0].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->filter_cond_array.filter_cond[0].value, fields_type_array[2], 100);	
	packet->filter_cond_array.filter_cond[1].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[1].column_id = 3;
	packet->filter_cond_array.filter_cond[1].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[1].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->filter_cond_array.filter_cond[1].value, fields_type_array[3], 100);	
	packet->filter_cond_array.filter_cond[2].type = FC_LOGIC_AND;
	

	del_num = execute_delete(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);
	


	
	for(i = 0; i < 1000; i++)
	{
		if(i > 100 && i%35 == 0)
		{
			ASSERT_EQ(db_is_rowid_deleted(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM), 1);
		}else{
			ASSERT_EQ(db_is_rowid_deleted(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM), 0);
		}
	}


	//测试没有过滤条件的情况
	packet->filter_cond_array.n = 0;
	del_num = execute_delete(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);


	for(i = 0; i < 100; i++)
	{
		if(i%35 == 0)
		{
			ASSERT_EQ(db_is_rowid_deleted(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM), 1);
		}else{
			ASSERT_EQ(db_is_rowid_deleted(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM), 0);
		}
	}

	


	mem_pool_destroy(pMemPool);
}























TEST(PACKET_EXECUTE_TEST, UPDATE_BY_ID){
	uint32 i;
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	uint16 segid;
	uint32 rowid;
	uint32 update_num;

	init_profile(10, pMemPool);


	
	int32 result_code;
	struct low_data_struct** record;	
	struct low_data_struct* ldata;
	struct update_by_id_packet* packet = (struct update_by_id_packet*) mem_pool_malloc(pMemPool, sizeof(struct update_by_id_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	mock_db_init(4, fields_type_array);
	
	packet->table_id = 0;
	packet->docid_num = 10;
	packet->column_id = 0;
	gen_low_data_struct(pMemPool, &packet->ldata, fields_type_array[0]);
	packet->docid_array = (uint64*) mem_pool_malloc(pMemPool, sizeof(uint64)*packet->docid_num);

	for(i = 0; i < packet->docid_num; i++)
	{
		packet->docid_array[i] = i;
		record = gen_row_record(pMemPool, 4, fields_type_array);
		db_insert(0, &segid, &rowid, record);
	}

	for(i = 0; i < packet->docid_num; i++)
	{
		ASSERT_EQ(db_is_rowid_deleted(0, (uint16)(packet->docid_array[i]>>32), (uint32)packet->docid_array[i]), 0);
	}

	
	update_num = execute_update_by_id(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);


	for(i = 0; i < packet->docid_num; i++)
	{
		ldata = db_query_by_rowid(0, (uint16)(packet->docid_array[i]>>32), (uint32)packet->docid_array[i], 0, pMemPool);
		ASSERT_TRUE(check_low_data_struct_equal(ldata, &packet->ldata));
	}


	mem_pool_destroy(pMemPool);
}

















TEST(PACKET_EXECUTE_TEST, UPDATE){
	uint32 i;
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	uint16 segid;
	uint32 update_num;
	uint32 rowid;

	init_profile(10, pMemPool);

	
	int32 result_code;
	struct low_data_struct** record;	
	struct low_data_struct* ldata;
	struct update_packet* packet = (struct update_packet*) mem_pool_malloc(pMemPool, sizeof(struct update_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	mock_db_init(4, fields_type_array);
	
	packet->table_id = 0;
	packet->column_id = 0;
	packet->hi_array.n = 0;
	packet->filter_cond_array.n = 0;
	packet->hash_cond_array.n = 0;

	update_num = execute_update(pMemPool, packet, &result_code);
	ASSERT_LT(result_code, 0);



	gen_low_data_struct(pMemPool, &packet->ldata, fields_type_array[0], -1);


	for(i = 0; i < 1000; i++)
	{
		record = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*) * 4);
		for(int j = 0; j < 4; j++)
		{
			record[j] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		}
		gen_low_data_struct(pMemPool, record[0], fields_type_array[0], i%5);
		gen_low_data_struct(pMemPool, record[1], fields_type_array[1], i%7);
		gen_low_data_struct(pMemPool, record[2], fields_type_array[2], i);
		gen_low_data_struct(pMemPool, record[3], fields_type_array[3], i);		
		db_insert(0, &segid, &rowid, record);
	}






	packet->hash_cond_array.n = 3;
	packet->hash_cond_array.hash_cond = (struct hash_condition*) mem_pool_malloc(pMemPool, sizeof(struct hash_condition) * packet->hash_cond_array.n);
	packet->hash_cond_array.hash_cond[0].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[0].column_id = 0;
	packet->hash_cond_array.hash_cond[0].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[0].value, fields_type_array[0], 0);
	packet->hash_cond_array.hash_cond[1].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[1].column_id = 1;
	packet->hash_cond_array.hash_cond[1].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[1].value, fields_type_array[1], 0);
	packet->hash_cond_array.hash_cond[2].type = HC_SET_AND;


	
	packet->filter_cond_array.n = 3;
	packet->filter_cond_array.filter_cond = (struct filter_condition*) mem_pool_malloc(pMemPool, sizeof(struct filter_condition) * packet->filter_cond_array.n);
	packet->filter_cond_array.filter_cond[0].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[0].column_id = 2;
	packet->filter_cond_array.filter_cond[0].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[0].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->filter_cond_array.filter_cond[0].value, fields_type_array[2], 100);
	packet->filter_cond_array.filter_cond[1].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[1].column_id = 3;
	packet->filter_cond_array.filter_cond[1].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[1].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->filter_cond_array.filter_cond[1].value, fields_type_array[3], 100);	
	packet->filter_cond_array.filter_cond[2].type = FC_LOGIC_AND;
	

	update_num = execute_update(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);


	for(i = 0; i < 1000; i++)
	{
		if(i > 100 && i%35 == 0)
		{
			ldata = db_query_by_rowid(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM, 0, pMemPool);
			ASSERT_EQ(check_low_data_struct_equal(ldata, &packet->ldata), 1);
		}else{
			ldata = db_query_by_rowid(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM, 0, pMemPool);
			ASSERT_EQ(check_low_data_struct_equal(ldata, &packet->ldata), 0);			
		}
	}


	//测试没有过滤条件的情况
	packet->filter_cond_array.n = 0;

	update_num = execute_update(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);

	for(i = 0; i < 100; i++)
	{
		if(i%35 == 0)
		{
			ldata = db_query_by_rowid(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM, 0, pMemPool);
			ASSERT_EQ(check_low_data_struct_equal(ldata, &packet->ldata), 1);
		}else{
			ldata = db_query_by_rowid(0, i/TEST_MAX_ROW_NUM, i%TEST_MAX_ROW_NUM, 0, pMemPool);
			ASSERT_EQ(check_low_data_struct_equal(ldata, &packet->ldata), 0);			
		}
	}


	mem_pool_destroy(pMemPool);
}














TEST(PACKET_EXECUTE_TEST, QUERY){
	uint32 i;
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);

	uint16 segid;
	uint32 rowid;


	//init_log("DEBUG", "./mile.profile");
	init_profile(10, pMemPool);
	PROFILER_START("start query test!");
	
	int32 result_code;
	struct low_data_struct** record;	
	struct query_packet* packet = (struct query_packet*) mem_pool_malloc(pMemPool, sizeof(struct query_packet));
	memset(packet, 0, sizeof(struct query_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_LONGLONG, HI_TYPE_LONG};//HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	mock_db_init(4, fields_type_array);
	
	packet->table_id = 0;
	packet->hi_array.n = 0;
	packet->filter_cond_array.n = 0;
	packet->hash_cond_array.n = 0;
	packet->select_col_array.n = 0;
	packet->group_array.n = 0;

	execute_query(pMemPool, packet, &result_code);
	ASSERT_LT(result_code, 0);


	printf("============================================================\n");


	for(i = 2; i < 1000; i++)
	{
		record = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*) * 4);
		for(int j = 0; j < 4; j++)
		{
			record[j] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		}

		gen_low_data_struct(pMemPool, record[0], fields_type_array[0], i%5);
		gen_low_data_struct(pMemPool, record[1], fields_type_array[1], i%7);
		gen_low_data_struct(pMemPool, record[2], fields_type_array[2], i);
		gen_low_data_struct(pMemPool, record[3], fields_type_array[3], 32);		
		db_insert(0, &segid, &rowid, record);
	}
/*
	for(i = 2; i < 1000; i++)
	{
		record = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*) * 4);
		for(int j = 0; j < 4; j++)
		{
			record[j] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		}

		gen_low_data_struct(pMemPool, record[0], fields_type_array[0], i%5);
		gen_low_data_struct(pMemPool, record[1], fields_type_array[1], i%7);
		gen_low_data_struct(pMemPool, record[2], fields_type_array[2], i);
		gen_low_data_struct(pMemPool, record[3], fields_type_array[3], i);		
		db_insert(0, &segid, &rowid, record);
	}
*/


	packet->select_col_array.n = 2;
	packet->select_col_array.columns = (uint32*) mem_pool_malloc(pMemPool, sizeof(uint32) * packet->select_col_array.n);
	packet->select_col_array.columns[0] = 0;
	packet->select_col_array.columns[1] = 1;
	//packet->select_col_array.columns[2] = 2;
//	packet->select_col_array.columns[3] = 3;

	//packet->select_col_array.columns[0] = 1000;

	//construct groupby column
	packet->group_array.n = 2;
	packet->group_array.columns = (uint32*) mem_pool_malloc(pMemPool, sizeof(uint32) * packet->group_array.n);
	packet->group_array.columns[0] = 0;
	packet->group_array.columns[1] = 1;
	//packet->group_array.columns[2] = 2;

	//构造虚拟列
	packet->virtual_col_array.n = 4;
	packet->virtual_col_array.virtual_col = (struct virtual_column*)mem_pool_malloc(pMemPool, sizeof(struct virtual_column) * packet->virtual_col_array.n);
	packet->virtual_col_array.virtual_cols[0].virtual_id = 1000;
	packet->virtual_col_array.virtual_cols[0].ref_id = 2;
	packet->virtual_col_array.virtual_cols[0].type = HI_TYPE_UNSIGNED_LONGLONG;//HI_TYPE_DOUBLE;
//	packet->virtual_col_array.virtual_col[0].function = FUNC_COUNT;
//	packet->virtual_col_array.virtual_col[0].function = FUNC_MAX;
//	packet->virtual_col_array.virtual_col[0].function = FUNC_MIN;
	packet->virtual_col_array.virtual_cols[0].function = FUNC_SUM;//FUNC_COUNT;//FUNC_SUM;
	
	packet->virtual_col_array.virtual_cols[1].virtual_id = 1001;
	packet->virtual_col_array.virtual_cols[1].ref_id = 2;
	packet->virtual_col_array.virtual_cols[1].type = HI_TYPE_LONG;
	packet->virtual_col_array.virtual_cols[1].function = FUNC_COUNT;
	
	packet->virtual_col_array.virtual_cols[2].virtual_id = 1002;
	packet->virtual_col_array.virtual_cols[2].ref_id = 2;
	packet->virtual_col_array.virtual_cols[2].type = HI_TYPE_LONG;
	packet->virtual_col_array.virtual_cols[2].function = FUNC_MAX;

	packet->virtual_col_array.virtual_cols[3].virtual_id = 1003;
	packet->virtual_col_array.virtual_cols[3].ref_id = 2;
	packet->virtual_col_array.virtual_cols[3].type = HI_TYPE_LONG;
	packet->virtual_col_array.virtual_cols[3].function = FUNC_MIN;

	//构造hash condition
	packet->hash_cond_array.n = 1;
	packet->hash_cond_array.hash_cond = (struct hash_condition*) mem_pool_malloc(pMemPool, sizeof(struct hash_condition) * packet->hash_cond_array.n);
	packet->hash_cond_array.hash_cond[0].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[0].column_id = 0;
	packet->hash_cond_array.hash_cond[0].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[0].value, fields_type_array[0], 1);
	printf("----------------> 0x%x\n",*(int*)packet->hash_cond_array.hash_cond[0].value->data);
	/*
	packet->hash_cond_array.hash_cond[1].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[1].column_id = 1;
	packet->hash_cond_array.hash_cond[1].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[1].value, fields_type_array[1], 5);
	packet->hash_cond_array.hash_cond[2].type = HC_SET_AND;
	packet->hash_cond_array.hash_cond[2].column_id = 2;
	packet->hash_cond_array.hash_cond[2].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->hash_cond_array.hash_cond[1].value, fields_type_array[1], 4);
	*/
	#if 0
	/* where columnid 0 == 3 */
	packet->hash_cond_array.hash_cond[0].value->len = 2;
	packet->hash_cond_array.hash_cond[0].value->data = (uint16*)mem_pool_malloc(pMemPool,2);
	*(uint16*)(packet->hash_cond_array.hash_cond[0].value->data) = 3;
	//memcpy();
	#endif

	//构造filter condition
	packet->filter_cond_array.n = 3;
	packet->filter_cond_array.filter_cond = (struct filter_condition*) mem_pool_malloc(pMemPool, sizeof(struct filter_condition) * packet->filter_cond_array.n);
	packet->filter_cond_array.filter_cond[0].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[0].column_id = 2;
	packet->filter_cond_array.filter_cond[0].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[0].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->filter_cond_array.filter_cond[0].value, fields_type_array[2], 100);	
	packet->filter_cond_array.filter_cond[1].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[1].column_id = 3;
	packet->filter_cond_array.filter_cond[1].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[1].value = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	gen_low_data_struct(pMemPool, packet->filter_cond_array.filter_cond[1].value, fields_type_array[3], 100);	
	packet->filter_cond_array.filter_cond[2].type = FC_LOGIC_AND;


	packet->order_array.n = 0;

	packet->limit = 1000;

	struct query_result_dataset* q_res = execute_query(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);
	printf("filter query:%d\n", q_res->elems_num);
	for ( i = 0; i < q_res->elems_num; i += 1 )
	{
		/*
		printf("col_num count = %d\n",q_res->data_array[i]->col_num);
		printf("%d\t",*(short*)q_res->data_array[i]->cols_data[0]->data);
		printf("type --> %d  ",q_res->ftype_array[0]);
		printf("%d\n",*(int*)q_res->data_array[i]->cols_data[1]->data);
		printf("type --> %d  ",q_res->ftype_array[1]);
		*/
		print_result_data_elem(q_res->data_array[i], q_res->ftype_array);
		printf("\n");
	}

	printf("============================================================\n");
	goto query_end;

	//对无过滤条件的查询进行测试
	packet->filter_cond_array.n = 0;
	q_res = execute_query(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);

	printf("no filter query:%d\n", q_res->elems_num);
	for ( i = 0; i < q_res->elems_num; i += 1 )
	{
		print_result_data_elem(q_res->data_array[i], q_res->ftype_array);
		printf("\n");
	}





	//对排序进行测试
	//排序没有虚拟列
	packet->virtual_col_array.n = 0;
	packet->select_col_array.columns[0] = 3;

	packet->filter_cond_array.n = 0;
	packet->order_array.n = 1;
	packet->order_array.desc = (struct order_desc*) mem_pool_malloc(pMemPool, sizeof(struct order_desc)*packet->order_array.n);
	packet->order_array.desc[0].column_id = 3;
	packet->order_array.desc[0].order_type = ORDER_TYPE_ASC;
	
	q_res = execute_query(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);
 
	printf("order query:%d\n", q_res->elems_num);
	for ( i = 0; i < q_res->elems_num; i += 1 )
	{
		print_result_data_elem(q_res->data_array[i], q_res->ftype_array);
		printf("\n");
	}






	//对limit较小的情况进行测试
	packet->limit = 5;
	packet->filter_cond_array.n = 0;
	execute_query(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS); 

query_end:
	
	PROFILER_STOP();
	PROFILER_DUMP();

	mem_pool_destroy(pMemPool);
}











