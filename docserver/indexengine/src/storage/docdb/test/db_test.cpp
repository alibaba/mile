/*
 * =====================================================================================
 *
 *       Filename:  db_test.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  2012年09月27日 19时37分35秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi (zian), yunliang.shi@alipay.com
 *   Organization:  
 *
 * =====================================================================================
 */

#include "../../../common/def.h"
#include <gtest/gtest.h>
#include "../../../common/mem.h"
#include "../db.h"



void get_string_low_data(struct low_data_struct* data, char* value,MEM_POOL* mem_pool)
{
	data->len = strlen(value) + 1;
	data->data = mem_pool_malloc(mem_pool, data->len);
	data->type = HI_TYPE_STRING;
	data->field_name = (char*)mem_pool_malloc(mem_pool,strlen("$address") + 1);

	memset(data->field_name,0,strlen("$address")+1);
	strcpy(data->field_name,"$address");


	memset(data->data,0,data->len);
	strcpy((char*)data->data,value);
}


struct hint_array * get_time_hint(MEM_POOL* mem_pool)
{
	struct hint_array* time_hint = (struct hint_array*)mem_pool_malloc(mem_pool,sizeof(struct hint_array));
	memset(time_hint,0,sizeof(time_hint));

	time_hint->n = 2;

	time_hint->hints = (uint64_t*)mem_pool_malloc(mem_pool,2*sizeof(uint64_t));
	time_hint->hints[0] = 0;
	time_hint->hints[1] = 0xFFFFFFFFFFFFFFFF;
	
	return time_hint;
}


TEST(DOCLIST_TEST, doclist)  {
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	struct db_conf config;
	memset(&config, 0, sizeof(struct db_conf));
	config.row_limit = 10;
	config.store_raw = 1;
	config.storage_dirs.n = 1;
	config.max_segment_num = 10;
	config.storage_dirs.strs = (char**)mem_pool_malloc(mem_pool, sizeof(char*));
	*config.storage_dirs.strs = "/tmp/newdb_index";
	

	int32_t ret = db_init(&config);
	ASSERT_EQ(0, ret);
	

	uint16_t sid;
	uint32_t docid;
	struct row_data rdata;
	rdata.datas = (struct low_data_struct*)mem_pool_malloc(mem_pool, 3 * sizeof(struct low_data_struct));
	memset(rdata.datas, 0,3 * sizeof(struct low_data_struct));

	rdata.field_count = 3;
	
	get_string_low_data(rdata.datas,"浙江", mem_pool);
	get_string_low_data(rdata.datas + 1,"杭州", mem_pool);
	get_string_low_data(rdata.datas + 2, "万塘路", mem_pool);
	
	for(int i = 0; i<30; i++)
	{
		db_read_lock();
		db_lock_table("address");
		ret = db_insert("address", &sid, &docid, &rdata, 1, mem_pool);
		ASSERT_EQ(0,ret);
		db_unlock_table("address");
		db_read_unlock();
	}


	ret = db_ensure_index("address", "address" , HI_KEY_ALG_FULLTEXT, HI_TYPE_STRING, mem_pool);
	ASSERT_EQ(0, ret);

	
	for(int i = 0; i<30; i++)
	{
		db_read_lock();
		db_lock_table("address");
		ret = db_insert("address", &sid, &docid, &rdata, 1, mem_pool);
		ASSERT_EQ(0,ret);
		db_unlock_table("address");
		db_read_unlock();
	}

	struct list_head* seg_list = db_seghint_query("address", get_time_hint(mem_pool), mem_pool);	

	ret =  db_fulltext_index_length_query("address", seg_list, rdata.datas, mem_pool);
	ASSERT_EQ(60,ret);
	
}


int main(int argc, char** argv) {
    ::testing::InitGoogleTest(&argc, argv);
    return RUN_ALL_TESTS();
}

