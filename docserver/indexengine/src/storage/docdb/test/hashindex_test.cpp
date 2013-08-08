#include "../../../common/def.h"
#include <gtest/gtest.h>
#include <inttypes.h>
#include "../../../common/mem.h"
#include "../doclist.h"
#include "../hash_index.h"

#include "time.h"
#define BUCKET_NUM 4

#define ROW_LIMIT 20

void get_low_data(struct low_data_struct* data,MEM_POOL* mem_pool)
{
	data->len = 5;
	data->data = mem_pool_malloc(mem_pool,5);
	data->type = HI_TYPE_STRING;
	data->field_name = (char*)mem_pool_malloc(mem_pool,20);
	memset(data->field_name,0,20);
	strcpy(data->field_name,"HI_TYPE_STRING");
	memset(data->data,0,5);
	strcpy((char*)data->data,"ali");

	return;
}


TEST(HASHINDEX_TEST, HandleNoneZeroInput)  {
    MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);  
    struct hash_index_config config;

    system("rm -rf /tmp/hashindex_test");
	char dir_path[] = "/tmp/hashindex_test";
	mkdirs(dir_path);
    config.row_limit = BUCKET_NUM;
    strcpy(config.work_space,dir_path);

	init_profile(1000,mem_pool);
	int32_t ret;

	//插入一个值
	struct low_data_struct insert_data;
	get_low_data(&insert_data,mem_pool);

	struct hash_index_manager* hash_index = hash_index_init(&config,mem_pool);
	ret = hash_index_insert(hash_index,&insert_data,0);
	ASSERT_EQ(ret,0);

	//查询
	struct rowid_list* rowids;
	uint32_t i;
	struct rowid_list_node* p;

	rowids = hash_index_query(hash_index,&insert_data,mem_pool);

	for(i = 0, p = rowids->head; i < rowids->rowid_num; i++)
	{
		if(i != 0 && i%ROWID_ARRAY_SIZE == 0)
		{
			p = p->next;
		}
		ASSERT_EQ(p->rowid_array[i%ROWID_ARRAY_SIZE],0);
		break;
	}

	//再插入一个值
	hash_index_insert(hash_index,&insert_data,1);

	rowids = hash_index_query(hash_index,&insert_data,mem_pool);


	for(i = 0, p = rowids->head; i < rowids->rowid_num; i++)
	{
		if(i != 0 && i%ROWID_ARRAY_SIZE == 0)
		{
			p = p->next;

			if(i==0)
				ASSERT_EQ(p->rowid_array[i%ROWID_ARRAY_SIZE],1);

			if(i==1)
			{
				ASSERT_EQ(p->rowid_array[i%ROWID_ARRAY_SIZE],0);
				break;
			}
		}
	}

	//插入BUCKET_NUM个
	for(i=2; i<BUCKET_NUM+1; i++)
	{
		sprintf((char*)insert_data.data,"ali%u",i);
		ret = hash_index_insert(hash_index,&insert_data,i);
		ASSERT_EQ(ret,0);
	}

	//再插一个报冲突了
	sprintf((char*)insert_data.data,"ali%u",i);
	ret = hash_index_insert(hash_index,&insert_data,i);
	ASSERT_EQ(ret,ERROR_HASH_CONFLICT);
	
	//插入一个空值，OK
	insert_data.len = 0;
	ret = hash_index_insert(hash_index,&insert_data,i);
    ASSERT_EQ(ret,0);

	rowids = hash_index_query(hash_index,&insert_data,mem_pool);

    ASSERT_EQ(rowids->rowid_num,1);
	ASSERT_EQ(rowids->head->rowid_array[0],5);

	hash_index_release(hash_index);
	mem_pool_destroy(mem_pool);
}

TEST(RECOVER_TEST_1, HandleNoneZeroInput)  {
    MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);  
    struct hash_index_config config;

    system("rm -rf /tmp/hashindex_test");
	char dir_path[] = "/tmp/hashindex_test";
	mkdirs(dir_path);
    config.row_limit = ROW_LIMIT;
    strcpy(config.work_space,dir_path);

	init_profile(1000,mem_pool);
	int32_t ret;

	struct hash_index_manager* hash_index = hash_index_init(&config,mem_pool);

	//插入18条数据，只恢复16条
	uint32_t i;
	struct low_data_struct* data = (struct low_data_struct*)mem_pool_malloc(mem_pool, sizeof(struct low_data_struct));
	memset(data, 0, sizeof(struct low_data_struct));

	for(i=0; i<18; i++)
	{
		data->len = 5;
		data->data = mem_pool_malloc(mem_pool,5);
		data->type = HI_TYPE_STRING;
		data->field_name = (char*)mem_pool_malloc(mem_pool,20);
		memset(data->field_name,0,20);
		strcpy(data->field_name,"HI_TYPE_STRING");
		memset(data->data,0,5);
		sprintf((char*)data->data, "ali%u", i);
		ret = hash_index_insert(hash_index,data,i);
		ASSERT_EQ(0, ret);
	}	
	
	struct doc_row_unit* doc = NULL;

	ASSERT_EQ(0, hash_index_recover(hash_index, 16));

	for(i=0; i<ROW_LIMIT; i++)
	{
		doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);
	}
	
	//验证
	for(i=0; i<16; i++)
	{
		doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);
		ASSERT_EQ(i, doc->doc_id);
	}	
	
	for(i=16; i<ROW_LIMIT; i++)
	{
		doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);
		ASSERT_EQ(0, doc->doc_id);
		ASSERT_EQ(0,doc->next);
	}

	struct hash_bucket* hbucket = NULL;
	uint32_t bucket_no = 0;

	for(i=0; i<ROW_LIMIT+1; i++)
	{
		hbucket = hash_index->mem_mmaped + i;
		doc = NEXT_DOC_ROW_STRUCT(hash_index->doclist, hbucket->offset);
	
		if(hbucket->hash_value == 0)
		{
			continue;	
		}

		while(!(doc->next & 0x80000000))
		{
			ASSERT_LT(16, doc->doc_id);
			doc = NEXT_DOC_ROW_STRUCT(hash_index->doclist, doc->next);
		}
		
		bucket_no = doc->next & 0x7fffffff;
		ASSERT_EQ(i,bucket_no);
	}

	hash_index_release(hash_index);
	mem_pool_destroy(mem_pool);
}


TEST(RECOVER_TEST_2, HandleNoneZeroInput)  {
    MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);  
    struct hash_index_config config;

    system("rm -rf /tmp/hashindex_test");
	char dir_path[] = "/tmp/hashindex_test";
	mkdirs(dir_path);
    config.row_limit = ROW_LIMIT;
    strcpy(config.work_space,dir_path);

	init_profile(1000,mem_pool);
	int32_t ret;

	struct hash_index_manager* hash_index = hash_index_init(&config,mem_pool);

	//插入18条数据，只恢复16条
	uint32_t i;
	struct low_data_struct* data = (struct low_data_struct*)mem_pool_malloc(mem_pool, sizeof(struct low_data_struct));
	memset(data, 0, sizeof(struct low_data_struct));
	
    for(i=0; i<18; i++)
    {   
        if(i != 0 && i%2 == 1)
            continue;

        data->len = 5;
        data->data = mem_pool_malloc(mem_pool,5);
        data->type = HI_TYPE_STRING;
        data->field_name = (char*)mem_pool_malloc(mem_pool,20);
        memset(data->field_name,0,20);
        strcpy(data->field_name,"HI_TYPE_STRING");
        memset(data->data,0,5);
		ret = hash_index_insert(hash_index,data,i);
		ASSERT_EQ(0, ret);
		ret = hash_index_insert(hash_index,data,i+1);
		ASSERT_EQ(0, ret);
    }	

	struct doc_row_unit* doc = NULL;

	ASSERT_EQ(0, hash_index_recover(hash_index, 16));

	for(i=0; i<ROW_LIMIT; i++)
	{
		doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);
	}
	
	//验证
	for(i=0; i<16; i++)
	{
		doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);
		ASSERT_EQ(i, doc->doc_id);
	}	
	
	for(i=16; i<ROW_LIMIT; i++)
	{
		doc = GET_DOC_ROW_STRUCT(hash_index->doclist, i);
		ASSERT_EQ(0, doc->doc_id);
		ASSERT_EQ(0,doc->next);
	}

	struct hash_bucket* hbucket = NULL;
	uint32_t bucket_no = 0;

	for(i=0; i<ROW_LIMIT+1; i++)
	{
		hbucket = hash_index->mem_mmaped + i;
		doc = NEXT_DOC_ROW_STRUCT(hash_index->doclist, hbucket->offset);
	
		if(hbucket->hash_value == 0)
		{
			continue;	
		}

		while(!(doc->next & 0x80000000))
		{
			ASSERT_GT(16, doc->doc_id);
			doc = NEXT_DOC_ROW_STRUCT(hash_index->doclist, doc->next);
		}
		
		bucket_no = doc->next & 0x7fffffff;
		ASSERT_EQ(i,bucket_no);
	}

	hash_index_release(hash_index);
	mem_pool_destroy(mem_pool);
}

int main(int argc, char** argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}
