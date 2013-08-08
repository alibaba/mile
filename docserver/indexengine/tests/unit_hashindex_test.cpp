#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/storage/hash_index.h"
}

#include "time.h"
#define BUCKET_NUM 4

TEST(HASHINDEX_TEST, HandleNoneZeroInput)  {
    MEM_POOL* mem_pool = mem_pool_init(M_1M);  
    struct hash_index_config config;

    system("rm -rf /tmp/hashindex_test");
	char dir_path[] = "/tmp/hashindex_test";
	mkdirs(dir_path);
    config.row_limit = BUCKET_NUM;
    strcpy(config.work_space,dir_path);

	init_profile(1000,mem_pool);
	int32 ret;

	//插入一个值
	struct low_data_struct insert_data;
	get_low_data(&insert_data,HI_TYPE_STRING,mem_pool);

	struct hash_index_manager* hash_index = hash_index_init(&config,mem_pool);
	ret = hash_index_insert(hash_index,&insert_data,0);
	ASSERT_EQ(ret,0);

	//查询
	struct rowid_list* rowids;
	uint32 i;
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

