#include "def.h"
extern "C"
{
#include "../../indexengine/src/include/mem.h"
#include "../../indexengine/src/include/log.h"
#include "../../indexengine/src/include/hyperindex_suffix.h"
#include "./seg_rowid_list_test.h"
}
TEST(SUFFIXTEST, HandleNoneZeroInput) {
  	int32 ret;
	MEM_POOL_PTR pMemPool = mem_pool_init(M_1M);
	char work_space[100];

	//初始化schema信息
	struct schema_info schema;
	schema.row_limit = 100;
	schema.field_count = 4;

	//设置时间条件
	struct hint_array time_cond;
	time_cond.n = 4;
	time_cond.hints = (uint64*)mem_pool_malloc(pMemPool,sizeof(uint64)*time_cond.n);
	*time_cond.hints = 0;
	*(time_cond.hints+1) = 0xFFFFFFFFFFFFFFFF;
	*(time_cond.hints+2) = 0;
	*(time_cond.hints+3) = 0xFFFFFFFFFFFFFFFF;

	//一列为hash，一列为filter
	schema.types[0] = HI_TYPE_LONG;
	schema.types[1] = HI_TYPE_LONG;
	schema.types[2] = HI_TYPE_LONGLONG;
	schema.types[3] = HI_TYPE_LONGLONG;
	schema.index_types[0] = HI_KEY_ALG_HASH;
	schema.index_types[1] = HI_KEY_ALG_HASH;
	schema.index_types[2] = HI_KEY_ALG_FILTER;
	schema.index_types[3] = HI_KEY_ALG_FILTER;
	schema.hashmods[0] = 100;
	schema.hashmods[1] = 100;
	schema.field_names[0] = (char*)mem_pool_malloc(pMemPool,100);
	schema.field_names[1] = (char*)mem_pool_malloc(pMemPool,100);
	schema.field_names[2] = (char*)mem_pool_malloc(pMemPool,100);
	schema.field_names[3] = (char*)mem_pool_malloc(pMemPool,100);

	memset(schema.field_names[0],0,100);
	memset(schema.field_names[1],0,100);
	memset(schema.field_names[2],0,100);
	memset(schema.field_names[3],0,100);
	strcpy(schema.field_names[0],"hashindex1");
	strcpy(schema.field_names[1],"hashindex2");
	strcpy(schema.field_names[2],"filter1");
	strcpy(schema.field_names[3],"filter2");

	schema.store_value[0] = 1;
	schema.store_value[1] = 1;
	schema.store_value[2] = 1;
	schema.store_value[3] = 1;
	

	memset(work_space,0,sizeof(work_space));
	parse_config("workspace",work_space);

	//初始化db
	struct db_conf dconf;
	struct table_conf tconf;
	memset(&dconf,0,sizeof(struct db_conf));
	memset(&tconf,0,sizeof(struct table_conf));

	tconf.schema = &schema;
	strcpy(tconf.table_name,"suffix");
	tconf.tid = 0;

	strcpy(dconf.work_space,work_space);
	strcpy(dconf.log_level,"DEBUG");
	dconf.table_count = 1;

	char hour_range_param[] = "2-4";
	dconf.table_configs[0] = &tconf;

	//初始化DB
	ret = db_init(&dconf);
	ASSERT_EQ(ret,MILE_RETURN_SUCCESS);

	//生成数据
	struct low_data_struct** row_data = (struct low_data_struct**)mem_pool_malloc(pMemPool,4*sizeof(struct low_data_struct*));
	*(row_data) = (struct low_data_struct*)mem_pool_malloc(pMemPool,sizeof(struct low_data_struct));
	*(row_data+1) = (struct low_data_struct*)mem_pool_malloc(pMemPool,sizeof(struct low_data_struct));
	*(row_data+2) = (struct low_data_struct*)mem_pool_malloc(pMemPool,sizeof(struct low_data_struct));
	*(row_data+3) = (struct low_data_struct*)mem_pool_malloc(pMemPool,sizeof(struct low_data_struct));
	
	memset(*(row_data),0,sizeof(struct low_data_struct));
	memset(*(row_data+1),0,sizeof(struct low_data_struct));
	memset(*(row_data+2),0,sizeof(struct low_data_struct));
	memset(*(row_data+3),0,sizeof(struct low_data_struct));
	
	uint32 hash_value1 = 6666;
        uint32 hash_value2 = 8888;
        uint64 filter_value1 = 66666;
        uint64 filter_value2 = 88888;
	uint16 segment_id;
	uint32 row_id;
	
	for(int i=0;i<5;i++)
	{
		(*row_data)->data = &hash_value1;
		(*row_data)->len = 4;
		(*row_data+1)->data = &hash_value1;
		(*row_data+1)->len = 4;
		(*row_data+2)->data = &filter_value1;
		(*row_data+2)->len = 8;
		(*row_data+3)->data = &filter_value2;
		(*row_data+3)->len = 8;

		//插入一行数据
		db_insert(0,&segment_id,&row_id,row_data);
	}
	
	for(int i=0;i<5;i++)
        {
                (*row_data)->data = &hash_value2;
                (*row_data)->len = 4;
                (*row_data+1)->data = &hash_value2;
                (*row_data+1)->len = 4;
                (*row_data+2)->data = &filter_value1;
                (*row_data+2)->len = 8;
                (*row_data+3)->data = &filter_value2;
                (*row_data+3)->len = 8;

                //插入一行数据
                db_insert(0,&segment_id,&row_id,row_data);
        }

	//构造hash逆波兰表达式
	struct low_data_struct hash_data_1;
	struct low_data_struct hash_data_2;
	struct hash_condition_array* hash_conds = (struct hash_condition_array*)mem_pool_malloc(pMemPool,sizeof(struct hash_condition_array));
	memset(hash_conds,0,sizeof(struct hash_condition_array));

	struct hash_condition* hash_cond = NULL;
	hash_conds->hash_cond = (struct hash_condition*)mem_pool_malloc(pMemPool,3*sizeof(struct hash_condition));
	hash_conds->n=3;

	//表达式
	hash_cond = hash_conds->hash_cond;
	hash_cond->column_id = 0;
	hash_cond->type = HC_HASH_EXP;
	hash_cond->compare_id = CT_EQ;
	hash_data_1.len = 4;
	hash_data_1.data = &hash_value1;
	hash_cond->value = &hash_data_1;
	hash_cond->value_num = 1;

	//表达式
	hash_cond = hash_conds->hash_cond+1;
	hash_cond->column_id = 1;
	hash_cond->type = HC_HASH_EXP;
	hash_cond->compare_id = CT_EQ;
	hash_data_2.len = 4;
	hash_data_2.data = &hash_value2;
	hash_cond->value = &hash_data_2;
	hash_cond->value_num = 1;

	//运算符
	hash_cond = hash_conds->hash_cond+2;
	hash_cond->type = HC_SET_OR;

	//并集
	struct list_head* hash_result = NULL;
	hash_result = query_by_hash_conditions(0,hash_conds,&time_cond,pMemPool);
	
	print_query_rowids(hash_result);

	uint32* rowid_array_a = (uint32*) mem_pool_malloc(pMemPool, sizeof(uint32)*ROWID_ARRAY_SIZE*10);
	struct list_head *lista = (struct list_head*) mem_pool_malloc(pMemPool, sizeof(struct list_head));

	INIT_LIST_HEAD(lista);

	set_array(rowid_array_a, "9, 8, 7, 6, 5, 4, 3, 2, 1, 0");
	gen_seg_rowid_list(pMemPool, lista, 0, rowid_array_a, 10);
	ASSERT_TRUE(check_seg_rowid_list_equal(lista,hash_result));


	//构造filter逆波兰表达式
	struct filter_condition_array* filter_conds = (struct filter_condition_array*)mem_pool_malloc(pMemPool,sizeof(struct filter_condition_array));
	memset(filter_conds,0,sizeof(struct filter_condition_array));

	struct filter_condition* filter_cond;
	struct low_data_struct filter_data_1;
	struct low_data_struct filter_data_2;
	filter_conds->n = 3;
	filter_conds->filter_cond = (struct filter_condition*)mem_pool_malloc(pMemPool,3*sizeof(struct filter_condition));

	//filter表达式
	filter_cond = filter_conds->filter_cond;
	filter_cond->column_id = 2;
	filter_cond->compare_id = CT_EQ;
	filter_cond->type = FC_FILTER_EXP;
	filter_data_1.len = 8;
	filter_data_1.data = &filter_value1;
	filter_cond->value = &filter_data_1;
	hash_cond->value_num = 1;

	//filter表达式
	filter_cond = filter_conds->filter_cond+1;
	filter_cond->column_id = 3;
	filter_cond->compare_id = CT_EQ;
	filter_cond->type = FC_FILTER_EXP;
	filter_data_2.len = 8;
	filter_data_2.data = &filter_value2;
	filter_cond->value = &filter_data_2;
	hash_cond->value_num = 1;

	//filter运算符
	filter_cond = filter_conds->filter_cond+2;
	filter_cond->type = FC_LOGIC_AND;
	
	int32 filter_ret = query_by_filter_conditions(0,0,0,filter_conds,pMemPool);
	ASSERT_EQ(filter_ret,1);
	
}

