// ldb_storage_test.cpp : ldb_storage_test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-09

#include "storage_engine_helper.h"

#include "../ldb/LdbConfig.h"
#include "../ldb/LdbEngine.h"
#include "../../common/ConfigFile.h"
#include "../../common/mem.h"
#include "../../protocol/packet.h"

#include <gtest/gtest.h>
#include <unistd.h>
#include <sys/stat.h>
#include <string>

class LdbEngineTest : public ::testing::Test
{
protected:
	LdbEngineTest() : dir_("./_ldb_engin_test_"), engine_(NULL) {}

	virtual void SetUp()
	{
		ClearDir();
		mem_ = mem_pool_init( 1 << 20); // MB

		// init config
		ConfigFile &conf = *ConfigFile::GlobalInstance();
		conf.SetValue(CONF_LDB_SESSION, CONF_LDB_TABLES, "t1, t2");

		// use user specified time
		conf.SetValue(CONF_LDB_SESSION, "t1.row_key", "pk");
		conf.SetValue(CONF_LDB_SESSION, "t1.time_key", "t");
		conf.SetValue(CONF_LDB_SESSION, "t1.time_key_scale", "0");
		conf.SetValue(CONF_LDB_SESSION, "t1.time_key_len", "4");

		conf.SetValue(CONF_LDB_SESSION, "t1.opt.use_bloom_filter", "1");

		// use host time
		conf.SetValue(CONF_LDB_SESSION, "t2.row_key", "pk");
		conf.SetValue(CONF_LDB_SESSION, "t2.time_key_scale", "0");
		conf.SetValue(CONF_LDB_SESSION, "t2.time_key_len", "4");

		conf.SetValue(CONF_LDB_SESSION, "max_open_files", "512");

		engine_ = NEW(mem_, LdbEngine)(dir_.c_str(), conf);
		ASSERT_EQ(0, engine_->Init());
	}

	virtual void TearDown()
	{
		DELETE(engine_);
		mem_pool_destroy(mem_);
		ClearDir();
	}

	void ClearDir() { switch(::system(("rm -rf " + dir_).c_str())){}; }

	void Insert()
	{
		TableManager *t1 = engine_->GetTableManager("t1", mem_);
		ASSERT_TRUE(NULL != t1);
		
		/*
		 * TYPE:
		 * 5 : uint32_t
		 * 11 : uint64_t
		 */

		MileHandler *handler = NULL;
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:123,v:value1", mem_), mem_));
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:223,v:value1", mem_), mem_));
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:323,v:value1", mem_), mem_));
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:333,v:value1", mem_), mem_));

		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abd,t:5:333,v:value1", mem_), mem_));

		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:bbc,t:5:433,v:value1", mem_), mem_));
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:cbc,t:5:533,v:value1", mem_), mem_));
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:dbc,t:5:633,v:value1", mem_), mem_));
		ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:ebc,t:5:733,v:value1", mem_), mem_));

		/*** table t2 **/
		
		TableManager *t2 = engine_->GetTableManager("t2", mem_);
		ASSERT_TRUE(NULL != t2);
		ASSERT_EQ(0, t2->InsertRow(handler, string2row("pk:abc,v:value1", mem_), mem_));


		DELETE(t1);
		DELETE(t2);
	}
	std::string dir_;

	LdbEngine *engine_;

	MEM_POOL_PTR mem_;
};

TEST_F(LdbEngineTest, table)
{
	TableManager *t1 = engine_->GetTableManager("t1", mem_);
	TableManager *t2 = engine_->GetTableManager("t2", mem_);
	ASSERT_TRUE(NULL != t1);
	ASSERT_TRUE(NULL != t2);

	DELETE(t1);
	DELETE(t2);

	TableManager *t3 = engine_->GetTableManager("table_not_exist", mem_);
	ASSERT_TRUE(NULL == t3);
}

TEST_F(LdbEngineTest, insert)
{
	Insert();

	TableManager *t1 = engine_->GetTableManager("t1", mem_);

	MileHandler *handler = NULL;
	// insert again
	ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:123,v:value1", mem_), mem_));
	// no row key
	ASSERT_NE(0, t1->InsertRow(handler, string2row("t:5:123,v:value1", mem_), mem_));
	// no time field
	ASSERT_NE(0, t1->InsertRow(handler, string2row("pk:abc,v:value1", mem_), mem_));
	// only row key and time key
	ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:123", mem_), mem_));

	DELETE(t1);
}

TEST_F(LdbEngineTest, iterator)
{
	Insert();

	
	TableManager *t1 = engine_->GetTableManager("t1", mem_);
	MileIterator *iter = t1->UseIndex( ConditionHelper(mem_)
			// [abc, abc]
			.Add("pk", HI_TYPE_STRING, CT_EQ, "abc")
			.Add("pk", HI_TYPE_STRING, EXP_COMPARE_BETWEEN_LEGE, "ab", "abd")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter != NULL);
	ASSERT_TRUE(iter->IsDone());
	ASSERT_EQ(4, row_count(iter));

	DELETE(iter);

	iter = t1->UseIndex( ConditionHelper(mem_)
			// [abc, abc]
			.Add("pk", HI_TYPE_STRING, CT_EQ, "abc")
			.Add("pk", HI_TYPE_STRING, EXP_COMPARE_BETWEEN_LEGE, "ab", "abd")

			// (200, 300)
			.Add("t", HI_TYPE_UNSIGNED_LONG, EXP_COMPARE_BETWEEN_LG, "200", "300")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(1, row_count(iter));

	DELETE(iter);

	iter = t1->UseIndex( ConditionHelper(mem_)
			// [abc, bbc)
			.Add("pk", HI_TYPE_STRING, CT_GE, "abc")
			.Add("pk", HI_TYPE_STRING, CT_LT, "dbc")
			.Add("pk", HI_TYPE_STRING, EXP_COMPARE_BETWEEN_LGE, "abc", "bbc")

			// (223, 3000)
			.Add("t", HI_TYPE_UNSIGNED_LONG, EXP_COMPARE_BETWEEN_LG, "223", "3000")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(3, row_count(iter));

	DELETE(iter);

	iter = t1->UseIndex( ConditionHelper(mem_)
			// (abc, bbc)
			.Add("pk", HI_TYPE_STRING, EXP_COMPARE_BETWEEN_LG, "abc", "bbc")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(1, row_count(iter));

	DELETE(iter);


	iter = t1->UseIndex( ConditionHelper(mem_)
			// invalid range (bbc, abc) 
			.Add("pk", HI_TYPE_STRING, CT_EQ, "abc")
			.Add("pk", HI_TYPE_STRING, EXP_COMPARE_BETWEEN_LG, "bbc", "abc")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(0, row_count(iter));
	DELETE(iter);


	iter = t1->UseIndex( ConditionHelper(mem_)
			// not whole range, not supported
			.Add("pk", HI_TYPE_STRING, CT_GT, "abc")
			.Add("t", HI_TYPE_UNSIGNED_LONG, EXP_COMPARE_BETWEEN_LG, "200", "300")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter == NULL);

	/*** table t2 ***/
	TableManager *t2 = engine_->GetTableManager("t2", mem_);
	iter = t2->UseIndex( ConditionHelper(mem_)
			// [abc, abc]
			.Add("pk", HI_TYPE_STRING, CT_EQ, "abc")
			.ToCondArray(), NULL, mem_);

	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(1, row_count(iter));

	DELETE(iter);



	DELETE(t1);
	DELETE(t2);
}

TEST_F(LdbEngineTest, query)
{
	TableManager *t1 = engine_->GetTableManager("t1", mem_);
	ASSERT_TRUE(NULL != t1);

	MileHandler *handler = NULL;
	ASSERT_EQ(0, t1->InsertRow(handler, string2row("pk:abc,t:5:223,v1:value1,i:6:123456", mem_), mem_));

	MileIterator *iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "abc").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(NULL != iter);
	ASSERT_TRUE(iter->IsDone());
	iter->First();
	ASSERT_FALSE(iter->IsDone());
	ASSERT_TRUE(NULL != iter->CurrentItem());

	select_row_t *result = t1->QueryRow((MileHandler *)iter->CurrentItem(), string2select_field("t,pk,v1,i,vv", mem_), mem_);
	ASSERT_TRUE(NULL != result);
	ASSERT_EQ(result->n, 5U);
	// t
	ASSERT_EQ(223U, *(uint32_t*)result->data[0].data);
	// pk
	std::string str;
	str.assign((char *)result->data[1].data, result->data[1].len);
	ASSERT_STREQ("abc", str.c_str());
	// v1
	str.assign((char *)result->data[2].data, result->data[2].len);
	ASSERT_STREQ("value1", str.c_str());
	// i
	ASSERT_EQ(123456U, *(uint32_t*)result->data[3].data);
	// vv (not exist)
	ASSERT_EQ(0U, result->data[4].len);


	DELETE(iter);
	DELETE(t1);
}

TEST_F(LdbEngineTest, del)
{
	Insert();
	TableManager *t1 = engine_->GetTableManager("t1", mem_);
	ASSERT_TRUE(NULL != t1);

	MileIterator *iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "abc").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(NULL != iter);
	ASSERT_EQ(4, row_count(iter));

	// delete
	iter->First();
	ASSERT_EQ(0, t1->DeleteRow((MileHandler *)iter->CurrentItem(), mem_));

	DELETE(iter);

	// count again
	iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "abc").ToCondArray(), NULL, mem_);
	ASSERT_EQ(3, row_count(iter));
	DELETE(iter);

	DELETE(t1);

}

TEST_F(LdbEngineTest, update)
{
	Insert();
	TableManager *t1 = engine_->GetTableManager("t1", mem_);
	ASSERT_TRUE(t1);

	MileIterator *iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "ebc").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(NULL != iter);
	iter->First();
	ASSERT_FALSE(iter->IsDone());

	// <1> update value, and insert new value
	ASSERT_EQ(0, t1->UpdateRow((MileHandler *)iter->CurrentItem(), string2row("v:nwe_value,vv:new_vv,tt:5:0", mem_), mem_));
	DELETE(iter);
	iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "ebc").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(iter);
	iter->First();
	ASSERT_FALSE(iter->IsDone());

	select_row_t *result = t1->QueryRow((MileHandler *)iter->CurrentItem(), string2select_field("t,pk,v,vv", mem_), mem_);

	// t
	std::string str;
	ASSERT_EQ(733, *(uint32_t*)result->data[0].data);
	// pk
	str.assign((char *)result->data[1].data, result->data[1].len);
	ASSERT_STREQ("ebc", str.c_str());
	// v
	str.assign((char *)result->data[2].data, result->data[2].len);
	ASSERT_STREQ("nwe_value", str.c_str());
	// vv
	str.assign((char *)result->data[3].data, result->data[3].len);
	ASSERT_STREQ("new_vv", str.c_str());

	// <2> update time_key
	ASSERT_EQ(0, t1->UpdateRow((MileHandler *)iter->CurrentItem(), string2row("t:5:833", mem_), mem_));
	DELETE(iter);
	iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "ebc").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(iter);
	iter->First();
	ASSERT_FALSE(iter->IsDone());

	result = t1->QueryRow((MileHandler *)iter->CurrentItem(), string2select_field("t,pk,v", mem_), mem_);
	ASSERT_EQ(1, row_count(iter));
	// t
	iter->First();
	ASSERT_EQ(833, *(uint32_t*)result->data[0].data);

	// <3> update row_key
	ASSERT_EQ(0, t1->UpdateRow((MileHandler *)iter->CurrentItem(), string2row("pk:zzz", mem_), mem_));
	DELETE(iter);
	iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "zzz").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(iter);
	iter->First();
	ASSERT_FALSE(iter->IsDone());

	result = t1->QueryRow((MileHandler *)iter->CurrentItem(), string2select_field("t,pk,v", mem_), mem_);
	// pk
	str.assign((char *)result->data[1].data, result->data[1].len);
	ASSERT_STREQ("zzz", str.c_str());

	DELETE(iter);
	iter = t1->UseIndex(ConditionHelper(mem_).Add("pk", HI_TYPE_STRING, CT_EQ, "ebc").ToCondArray(), NULL, mem_);
	ASSERT_TRUE(iter);
	ASSERT_TRUE(iter->IsDone());
	ASSERT_EQ(0, row_count(iter));

	DELETE(iter);

	DELETE(t1);
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}

