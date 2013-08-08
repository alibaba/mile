#include "def.h"
#include <iostream>
#include <algorithm>
extern "C"
{
#include <stdarg.h>
#include <stddef.h>
#include <setjmp.h>
#include "google/cmockery.h"
#include "../../indexengine/src/include/hyperindex_packet_execute.h"
}





uint32 get_field_type_len(enum field_types field_type)
{
	switch(field_type)
	{
		case HI_TYPE_TINY:
			return sizeof(int8);
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
		case HI_TYPE_TINY:
			*((int8*)ldata->data) = (int8)i;
			return;
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



uint32 get_unit_size(enum field_types field_type)
{
	return *(uint32*)mock();
}





int32 db_insert(uint8 tid,uint16* segment_id,uint32* row_id,struct low_data_struct** data)
{
	*segment_id = *(uint16*)mock();
	*row_id = *(uint32*)mock();
	int32 result =  *(int32*)mock();
	return result;
}




int32 db_update(uint8 tid,uint16 sid,struct low_data_struct* new_rdata,uint32 row_id,uint16 field_id)
{
	return *(int32*)mock();
}



struct list_head* db_query_range(uint8 tid,is_matched condition,uint16 field_id,MEM_POOL* mem_pool)
{
	return (struct list_head*)mock();
}






struct low_data_struct* db_query_by_rowid(uint8 tid,uint16 sid,uint32 row_id,uint16 field_id,MEM_POOL* mem_pool)
{
	return (struct low_data_struct*)mock();
}



struct list_head* db_query_by_value(uint8 tid, uint16 field_id,	struct hint_array* time_cond, struct low_data_struct* data, MEM_POOL* mem_pool)
{
	return (struct list_head*)mock();
}	




int32 db_del_rowid(uint8 tid,uint16 sid,uint32 row_id)
{
	return *(int32*)mock();
}



int32 db_is_rowid_deleted(uint8 tid,uint16 sid,uint32 row_id)
{
	return *(int32*)mock();
}



enum field_types db_getfield_type(uint8 tid,uint16 field_id)
{
	return *(enum field_types*)mock();
}



enum index_key_alg db_getindex_type(uint8 tid, uint16 field_id)
{
	return *(enum index_key_alg*)mock();
}





struct list_head * db_btree_query_range(uint8 tid,	uint16 field_id, struct hint_array* time_cond, struct db_range_query_condition * range_condition, MEM_POOL *pMemPool)
{
	return (struct list_head*)mock();
}






uint16 get_node_id()
{
	return *(uint16*)mock();
}




struct list_head* query_by_hash_conditions(uint8 tid,struct hash_condition_array* conditions, struct hint_array* hi_array, MEM_POOL* mem_pool)
{
	return (struct list_head*)mock();
}




int32 query_by_filter_conditions(uint8 tid,uint16 sid,uint32 rowid,struct filter_condition_array* conditions,MEM_POOL* mem_pool)
{
	return *(int32*)mock();
}












void test_execute_insert_success(void** state)
{
	uint64 docid;
	uint16 segment_id;
	uint32 row_id;
	uint16 node_id;
	int32 result_code;
	int32 success_rc;
	
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	init_profile(10, pMemPool);
	struct insert_packet* packet = (struct insert_packet*) mem_pool_malloc(pMemPool, sizeof(struct insert_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	
	packet->table_id = 0;
	packet->column_num = 4;
	packet->ldata_array = gen_row_record(pMemPool, packet->column_num, fields_type_array);


	segment_id = 1;
	row_id = 1;
	success_rc = MILE_RETURN_SUCCESS;
	node_id = 1;
	will_return(db_insert, &segment_id);
	will_return(db_insert, &row_id);
	will_return(db_insert, &success_rc);
	will_return(get_node_id, &node_id);
	docid = execute_insert(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, success_rc);
	ASSERT_EQ(docid, 0x0001000100000001);


	mem_pool_destroy(pMemPool);
}









void test_execute_insert_failure(void** state)
{
	uint64 docid;
	uint16 segment_id;
	uint32 row_id;
	uint16 node_id;
	int32 result_code;
	int32 failure_rc;
	
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	init_profile(10, pMemPool);
	struct insert_packet* packet = (struct insert_packet*) mem_pool_malloc(pMemPool, sizeof(struct insert_packet));
	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	
	packet->table_id = 0;
	packet->column_num = 4;
	packet->ldata_array = gen_row_record(pMemPool, packet->column_num, fields_type_array);




	segment_id = 1;
	row_id = 1;
	failure_rc = -1;
	node_id = 1;
	will_return(db_insert, &segment_id);
	will_return(db_insert, &row_id);
	will_return(db_insert, &failure_rc);
	docid = execute_insert(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, failure_rc);


	mem_pool_destroy(pMemPool);
}










TEST(PACKET_EXECUTE_TEST, INSERT){
	const UnitTest tests[] ={unit_test(test_execute_insert_success), 
		unit_test(test_execute_insert_failure)};
	run_tests(tests);
}







void test_execute_delete_by_id_success(void** state)
{
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);

	uint32 i;
	uint32 del_num;
	int32 result_code;
	int32 success_rc = MILE_RETURN_SUCCESS;
	int32 row_del = 1;
	int32 row_not_del = 0;
	
	struct delete_by_id_packet* packet = (struct delete_by_id_packet*) mem_pool_malloc(pMemPool, sizeof(struct delete_by_id_packet));
	packet->docid_num = 10;
	packet->table_id = 0;
	packet->docid_array = (uint64*) mem_pool_malloc(pMemPool, sizeof(uint64)*packet->docid_num);

	for(i = 0; i < packet->docid_num; i++)
	{
		packet->docid_array[i] = i;
		will_return(db_is_rowid_deleted, &row_not_del);
		will_return(db_del_rowid, &success_rc);
	}

	del_num = execute_delete_by_id(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, success_rc);
	ASSERT_EQ(del_num, packet->docid_num);

	mem_pool_destroy(pMemPool);
}










void test_execute_delete_by_id_failure(void** state)
{
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);

	uint32 i;
	uint32 del_num;
	int32 result_code;
	int32 success_rc = MILE_RETURN_SUCCESS;	
	int32 failure_rc = -1;
	int32 row_del = 1;
	int32 row_not_del = 0;
	
	struct delete_by_id_packet* packet = (struct delete_by_id_packet*) mem_pool_malloc(pMemPool, sizeof(struct delete_by_id_packet));
	packet->docid_num = 10;
	packet->table_id = 0;
	packet->docid_array = (uint64*) mem_pool_malloc(pMemPool, sizeof(uint64)*packet->docid_num);

	for(i = 0; i < packet->docid_num; i++)
	{
		packet->docid_array[i] = i;
	}

	will_return(db_is_rowid_deleted, &failure_rc);
	del_num = execute_delete_by_id(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, failure_rc);


	will_return(db_is_rowid_deleted, &row_not_del);
	will_return(db_del_rowid, &failure_rc);	
	del_num = execute_delete_by_id(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, failure_rc);

	mem_pool_destroy(pMemPool);
}










TEST(PACKET_EXECUTE_TEST, DELETE_BY_ID){
	const UnitTest tests[] ={unit_test(test_execute_delete_by_id_success),
		unit_test(test_execute_delete_by_id_failure)};
	run_tests(tests);
}








void test_execute_delete(void** state)
{
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);

	uint32 i;
	uint32 del_num;
	int32 result_code;
	int32 success_rc = MILE_RETURN_SUCCESS;
	int32 failure_rc = -1;
	int32 row_del = 1;
	int32 row_not_del = 0;
	struct low_data_struct** record;

	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_FLOAT, HI_TYPE_DOUBLE};
	struct delete_packet* packet = (struct delete_packet*) mem_pool_malloc(pMemPool, sizeof(struct delete_packet));
	packet->table_id = 0;
	packet->hash_cond_array.n = 3;
	packet->filter_cond_array.n = 3;

	//错误的情形
	will_return(query_by_hash_conditions, NULL);
	del_num = execute_delete(pMemPool, packet, &result_code);
	ASSERT_TRUE(result_code < 0);


	
	mem_pool_destroy(pMemPool);
}






TEST(PACKET_EXECUTE_TEST, DELETE){
	const UnitTest tests[] ={unit_test(test_execute_delete)};
	run_tests(tests);
}





