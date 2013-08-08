#include "def.h"
#include <iostream>
#include <algorithm>
extern "C"
{
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include "../../indexengine/src/include/hyperindex_packet_execute.h"
}


#define DEBUG_FILTER 	0
#define DEBUG_ORDER	1

class SqlExecuteTest: public testing::Test
{
	public:
		struct schema_info * schema;
		char field_names[4][100];
		virtual void SetUp()
		{
			system("rm -rf /tmp/storage/mile_maoyan_test");

			char work_space[100] = "/tmp/storage";
			strcpy(field_names[0], "hash1");
			strcpy(field_names[1], "hash2");
			strcpy(field_names[2], "filter1");
			strcpy(field_names[3], "filter2");

			struct db_conf dconf;
			struct table_conf tconf;
			schema = (struct schema_info *)malloc(sizeof(struct schema_info));


			//初始化schema信息
			schema->row_limit = 100;
			schema->field_count = 4;

			//一列为hash，一列为filter
			schema->types[0] = HI_TYPE_SHORT;
			schema->types[1] = HI_TYPE_LONG;
			schema->types[2] = HI_TYPE_LONGLONG;
			schema->types[3] = HI_TYPE_DOUBLE;
			schema->index_types[0] = HI_KEY_ALG_HASH;
			schema->index_types[1] = HI_KEY_ALG_HASH;
			schema->index_types[2] = HI_KEY_ALG_FILTER;
			schema->index_types[3] = HI_KEY_ALG_FILTER;
			schema->hashmods[0] = 100;
			schema->hashmods[1] = 100;
			schema->field_names[0] = field_names[0];
			schema->field_names[1] = field_names[1];
			schema->field_names[2] = field_names[2];
			schema->field_names[3] = field_names[3];
			schema->store_value[0] = 1;
			schema->store_value[1] = 1;
			schema->store_value[2] = 1;
			schema->store_value[3] = 1;


			//初始化db			
			tconf.schema = schema;
			strcpy(tconf.table_name,"mile_maoyan_test");
			tconf.tid = 0;

			strcpy(dconf.work_space, work_space);
			strcpy(dconf.log_level, "DEBUG");
			dconf.table_count = 1;

			char hour_range_param[] = "2-4";
			dconf.table_configs[0] = &tconf;
			dconf.nid = 0;

			//初始化DB
			db_init(&dconf);

		}
		virtual void TearDown()
		{
			db_destroy();
			free(schema);
			system("rm -rf /tmp/storage/mile_maoyan_test");
		}
};






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









struct low_data_struct* gen_low_data_struct(MEM_POOL_PTR pMemPool, enum field_types field_type, uint i)
{
	struct low_data_struct* ldata = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	ldata->len = get_field_type_len(field_type);
	ldata->data = mem_pool_malloc(pMemPool, ldata->len);

	switch(field_type)
	{
		case HI_TYPE_SHORT:
			*((int16*)ldata->data) = (int16)i;
			break;
		case HI_TYPE_UNSIGNED_SHORT:
			*((uint16*)ldata->data) = (uint16)i;
			break;
		case HI_TYPE_LONG:
			*((int32*)ldata->data) = (int32)i;
			break;
		case HI_TYPE_UNSIGNED_LONG:
			*((uint32*)ldata->data) = (uint32)i;
			break;
		case HI_TYPE_LONGLONG:
			*((int64*)ldata->data) = (int64)i;
			break;
		case HI_TYPE_UNSIGNED_LONGLONG:
			*((uint64*)ldata->data) = (uint64)i;
			break;
		case HI_TYPE_FLOAT:
			*((float*)ldata->data) = (float)i;
			break;
		case HI_TYPE_DOUBLE:
			*((double*)ldata->data) = (double)i;
			break;
		default:
			printf("不支持的数据类型!\n");
			exit(1);
	}

	return ldata;
}







struct insert_packet* gen_row_record(MEM_POOL_PTR pMemPool, uint16 table_id)
{
	uint32 i;
	struct insert_packet* packet = (struct insert_packet*) mem_pool_malloc(pMemPool, sizeof(struct insert_packet));
	packet->table_id = table_id;
	packet->column_num = 4;
	packet->ldata_array = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*)*packet->column_num);

	for(i = 0; i < packet->column_num; i++)
	{
		//	gen_low_data_struct(pMemPool, , );
	}

	return packet;
}



//测试query
TEST_F(SqlExecuteTest, QUERY){
	MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
	uint32 i;

	init_profile(10, pMemPool);
	PROFILER_START("start query test!");

	int32 result_code;
	uint16 segid;
	uint32 rowid;

	enum field_types fields_type_array[4] = {HI_TYPE_SHORT, HI_TYPE_LONG, HI_TYPE_LONGLONG, HI_TYPE_DOUBLE};

	struct low_data_struct** record;	
	struct query_packet* packet = (struct query_packet*) mem_pool_malloc(pMemPool, sizeof(struct query_packet));
	memset(packet, 0, sizeof(struct query_packet));

	packet->limit = 1000;

	packet->table_id = 0;
	packet->hi_array.n = 0;
	packet->filter_cond_array.n = 0;
	packet->hash_cond_array.n = 0;
	packet->select_col_array.n = 0;
	packet->group_array.n = 0;

	//插入数据
	for(i = 0; i < 1000; i++)
	{
		record = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*) * 4);
		for(int j = 0; j < 4; j++)
		{
			record[j] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		}

		record[0] = gen_low_data_struct(pMemPool, fields_type_array[0], i%5);
		record[1] = gen_low_data_struct(pMemPool, fields_type_array[1], i%7);
		record[2] = gen_low_data_struct(pMemPool, fields_type_array[2], i);
		record[3] = gen_low_data_struct(pMemPool, fields_type_array[3], i);		
		db_insert(0, &segid, &rowid, record);
	}

	packet->hi_array.n = 4;
	packet->hi_array.hints = (uint64*)mem_pool_malloc(pMemPool,sizeof(uint64)*packet->hi_array.n);
	*(packet->hi_array.hints) = 0;
	*(packet->hi_array.hints+1) = 0xFFFFFFFFFFFFFFFF; 
	*(packet->hi_array.hints+2) = 0;
	*(packet->hi_array.hints+3) = 0xFFFFFFFFFFFFFFFF;


	packet->select_col_array.n = 4;
	packet->select_col_array.columns = (uint32*) mem_pool_malloc(pMemPool, sizeof(uint32) * packet->select_col_array.n);
	packet->select_col_array.columns[0] = 0;
	packet->select_col_array.columns[1] = 1;
	packet->select_col_array.columns[2] = 2;
	packet->select_col_array.columns[3] = 3;

	packet->group_array.n = 0;	
	packet->virtual_col_array.n = 0;

	//构造hash condition
	packet->hash_cond_array.n = 3;
	packet->hash_cond_array.hash_cond = (struct hash_condition*) mem_pool_malloc(pMemPool, sizeof(struct hash_condition) * packet->hash_cond_array.n);
	packet->hash_cond_array.hash_cond[0].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[0].column_id = 0;
	packet->hash_cond_array.hash_cond[0].compare_id = CT_EQ;
	packet->hash_cond_array.hash_cond[0].value = gen_low_data_struct(pMemPool, fields_type_array[0], 1);
	packet->hash_cond_array.hash_cond[1].type = HC_HASH_EXP;
	packet->hash_cond_array.hash_cond[1].column_id = 1;
	packet->hash_cond_array.hash_cond[1].compare_id = CT_EQ;
	packet->hash_cond_array.hash_cond[1].value = gen_low_data_struct(pMemPool, fields_type_array[1], 5);
	packet->hash_cond_array.hash_cond[2].type = HC_SET_AND;

	//对无过滤条件的查询进行测试
	packet->filter_cond_array.n = 0;
	struct query_result_dataset* q_res = execute_query(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);

	printf("no filter query:%d\n", q_res->elems_num);
	#if DEBUG_FILTER
	for ( i = 0; i < q_res->elems_num; i += 1 )
	{
		print_result_data_elem(q_res->data_array[i], q_res->ftype_array);
		printf("\n");
	}
	#endif

	//构造filter condition
	packet->filter_cond_array.n = 3;
	packet->filter_cond_array.filter_cond = (struct filter_condition*) mem_pool_malloc(pMemPool, sizeof(struct filter_condition) * packet->filter_cond_array.n);
	packet->filter_cond_array.filter_cond[0].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[0].column_id = 2;
	packet->filter_cond_array.filter_cond[0].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[0].value = gen_low_data_struct(pMemPool, fields_type_array[2], 100);	
	packet->filter_cond_array.filter_cond[1].type = FC_FILTER_EXP;
	packet->filter_cond_array.filter_cond[1].column_id = 3;
	packet->filter_cond_array.filter_cond[1].compare_id = CT_GT;
	packet->filter_cond_array.filter_cond[1].value = gen_low_data_struct(pMemPool, fields_type_array[3], 100);	
	packet->filter_cond_array.filter_cond[2].type = FC_LOGIC_AND;


	packet->order_array.n = 0;

	packet->limit = 1000;

	q_res = execute_query(pMemPool, packet, &result_code);
	ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);
	printf("filter query:%d\n", q_res->elems_num);
	#if DEBUG_FILTER
	for ( i = 0; i < q_res->elems_num; i += 1 )
	{
		print_result_data_elem(q_res->data_array[i], q_res->ftype_array);
		printf("\n");
	}
	#endif

	//test order
        packet->virtual_col_array.n = 0;
        //packet->select_col_array.columns[0] = 3;

        //packet->filter_cond_array.n = 0;
        packet->order_array.n = 1;
        packet->order_array.columns = (uint32 *) mem_pool_malloc(pMemPool, sizeof(uint32)*packet->order_array.n);
        packet->order_array.ord_types = (enum order_types *) mem_pool_malloc(pMemPool, sizeof(enum order_types)*packet->order_array.n);
        packet->order_array.columns[0] = 3;
        packet->order_array.ord_types[0] = ORDER_TYPE_ASC;

        q_res = execute_query(pMemPool, packet, &result_code);
        ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);

	#if DEBUG_ORDER
        printf("order query:%d\n", q_res->elems_num);
        for ( i = 0; i < q_res->elems_num; i += 1 )
        {
                print_result_data_elem(q_res->data_array[i], q_res->ftype_array);
                printf("\n");
        }
	#endif

	mem_pool_destroy(pMemPool);
}










/*
   TEST_F(SqlExecuteTest, INSERT_NORMAL){
   uint64 docid;
   MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
   int32 result_code;
   struct insert_packet* insert_pack;
   struct get_kvs_packet* kvs_pack;


   insert_pack = gen_row_record(pMemPool, 0);
   docid = execute_insert(pMemPool, insert_pack, &result_code);
   ASSERT_EQ(result_code, MILE_RETURN_SUCCESS);


   kvs_pack = (struct get_kvs_packet*) mem_pool_malloc(pMemPool, sizeof(struct get_kvs_packet));
   kvs_pack->table_id = 0;
   kvs_pack->docid_num = 1;

   mem_pool_destroy(pMemPool);
   }









   TEST_F(SqlExecuteTest, INSERT_FAILURE){
   uint64 docid;
   int32 result_code;
   MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);	
   struct insert_packet* packet;


   docid = execute_insert(pMemPool, NULL, &result_code);
   ASSERT_LT(result_code, 0);


   packet = gen_row_record(pMemPool, 0);
   packet->table_id = 10;
   docid = execute_insert(pMemPool, NULL, &result_code);
   ASSERT_LT(result_code, 0);

   mem_pool_destroy(pMemPool);
   }










   TEST_F(SqlExecuteTest, DELETE){
   uint64 docid;
   MEM_POOL_PTR pMemPool = mem_pool_init(MB_SIZE);
   init_profile(10, pMemPool);



   int32 result_code;
   list_head* list;
   struct insert_packet* packet = (struct insert_packet*) mem_pool_malloc(pMemPool, sizeof(struct insert_packet));

   packet->table_id = 0;
   packet->column_num = 4;
   packet->ldata_array = NULL;

docid = execute_insert(pMemPool, NULL, &result_code);
ASSERT_LT(result_code, 0);



mem_pool_destroy(pMemPool);
}

*/


