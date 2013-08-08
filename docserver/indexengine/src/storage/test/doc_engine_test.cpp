// doc_engine_test.cpp : docdb storage engine unit test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-16

#include "../docdb/DocEngine.h"
#include "../docdb/db.h"
#include "../../common/ConfigFile.h"
#include "storage_engine_helper.h"

#include <gtest/gtest.h>

#include <string>
#include <limits>

class DocEngineTest : public ::testing::Test
{
protected:
	DocEngineTest() : dir_("./_docdb_engin_test_"), binlog_dir_("./_binlog_test_dir"), engine_(NULL) {}

	virtual void SetUp()
	{
		ClearDir();
		::mkdir(dir_.c_str(), 0755);
		mem_ = mem_pool_init( 1 << 20); // MB
		hint_.n = 4;
		hint_.hints = (uint64_t *)mem_pool_malloc(mem_, sizeof(uint64_t) * 4);
		::memset(hint_.hints, 0, sizeof(uint64_t) * 4);
		hint_.hints[1] = std::numeric_limits<uint64_t>::max();
		hint_.hints[3] = std::numeric_limits<uint64_t>::max();

		// init config
		conf_.SetValue(CONF_SERVER_SESSION, "binlog_dir", binlog_dir_.c_str());
		conf_.SetValue(CONF_SERVER_SESSION, "binlog_maxsize", "10000000"); // 10MB
		conf_.SetValue(CONF_SERVER_SESSION, "binlog_flag", "1");
		conf_.SetValue(CONF_SERVER_SESSION, "role", "master");
		conf_.SetValue(CONF_SERVER_SESSION, "work_space", dir_.c_str());

		conf_.SetValue(CONF_DOCDB_SESSION, "max_segment_num", "1024");
		conf_.SetValue(CONF_DOCDB_SESSION, "row_limit", "4");

		// init engine
		engine_ = new(mem_pool_malloc(mem_, sizeof(*engine_)))DocEngine(dir_.c_str(), conf_);
		ASSERT_EQ(0, engine_->Init());

		// create index
		db_read_lock();
		char table_name[1024];
		char field_name[1024];
		::strcpy(table_name, "t1");
		::strcpy(field_name, "id");
		ASSERT_EQ(0, db_ensure_index(table_name, field_name, HI_KEY_ALG_HASH, HI_TYPE_STRING, mem_));
		::strcpy(table_name, "t1");
		::strcpy(field_name, "t");
		ASSERT_EQ(0, db_ensure_index(table_name, field_name, HI_KEY_ALG_HASH, HI_TYPE_UNSIGNED_LONG, mem_));
		ASSERT_EQ(0, db_ensure_index(table_name, field_name, HI_KEY_ALG_FILTER, HI_TYPE_UNSIGNED_LONG, mem_));
		::strcpy(table_name, "t1");
		::strcpy(field_name, "v");
		ASSERT_EQ(0, db_ensure_index(table_name, field_name, HI_KEY_ALG_FILTER, HI_TYPE_STRING, mem_));
		db_read_unlock();
	}


	virtual void TearDown()
	{
		if (engine_)
			DELETE(engine_);
		ClearDir();
		mem_pool_destroy(mem_);
	}

	void ClearDir()
	{
		switch(::system(("rm -rf " + dir_).c_str())){};
		switch(::system(("rm -rf " + binlog_dir_).c_str())){};
	}

	void Insert()
	{
		TableManager *t = engine_->GetTableManager("t1", mem_);
		ASSERT_TRUE(NULL != t);

		MileHandler *handler = NULL;

		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:123,v:value,v1:value1", mem_), mem_));
		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:123,v:value,v1:value1", mem_), mem_));

		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:123,v:value,v2:value1", mem_), mem_));
		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:123,v:value,v3:value1", mem_), mem_));

		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:223,v:value,v3:value1", mem_), mem_));
		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:323,v:value,v3:value 2", mem_), mem_));
		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:423,v:value,v3:value 2", mem_), mem_));

		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:bbc,t:5:423,v:value,v3:value 2", mem_), mem_));
		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:cbc,t:5:423,v:value,v3:value 2", mem_), mem_));
		ASSERT_EQ(0, t->InsertRow(handler, string2row("id:dbc,t:5:423,v:value,v3:value 2", mem_), mem_));
		DELETE(t);
	}

protected:
	std::string dir_;
	std::string binlog_dir_;
	ConfigFile conf_;

	DocEngine *engine_;

	MEM_POOL_PTR mem_;
	// default segment hint (hint all segments)
	hint_array hint_;
};

TEST_F(DocEngineTest, insert)
{
	Insert();

	MileHandler *handler = NULL;

	TableManager *t1 = engine_->GetTableManager("t1", mem_);
	// no index
	ASSERT_EQ(0, t1->InsertRow(handler, string2row("id1:id1", mem_), mem_));
	DELETE(t1);

	// new table;
	TableManager *t2 = engine_->GetTableManager("t2", mem_);

	ASSERT_EQ(0, t2->InsertRow(handler, string2row("id1:id1", mem_), mem_));

	DELETE(t2);
}

TEST_F(DocEngineTest, iterator)
{
	Insert();

	TableManager *t = engine_->GetTableManager("t1", mem_);
	// id = abc
	MileIterator *iter = t->UseIndex(ConditionHelper(mem_)
			.Add("id", HI_TYPE_STRING, CT_EQ, "abc")
			.ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(7, row_count(iter));
	DELETE(iter);

	// id = abc and t = 123
	iter = t->UseIndex(ConditionHelper(mem_)
			.Add("id", HI_TYPE_STRING, CT_EQ, "abc")
			.Add("t", HI_TYPE_UNSIGNED_LONG, CT_EQ, "123")
			.Add(LOGIC_AND)
			.ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(4, row_count(iter));
	DELETE(iter);

	// id = kkk or t = 888  (empty result)
	iter = t->UseIndex(ConditionHelper(mem_)
			.Add("id", HI_TYPE_STRING, CT_EQ, "kkk")
			.Add("t", HI_TYPE_UNSIGNED_LONG, CT_EQ, "888")
			.Add(LOGIC_AND)
			.ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(0, row_count(iter));
	DELETE(iter);

	// no condition
	iter = t->UseIndex(ConditionHelper(mem_).ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(10, row_count(iter));
	DELETE(iter);

	DELETE(t);
}

TEST_F(DocEngineTest, query)
{
	TableManager *t = engine_->GetTableManager("t1", mem_);
	MileHandler *handler = NULL;

	ASSERT_EQ(0, t->InsertRow(handler, string2row("id:abc,t:5:423,v:value,v2:value2", mem_), mem_));

	MileIterator *iter = t->UseIndex(ConditionHelper(mem_)
			.Add("id", HI_TYPE_STRING, CT_EQ, "abc")
			.ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	iter->First();
	ASSERT_FALSE(iter->IsDone());

	/*
	 * SELECT_TYPE_ORIGINAL 0
	 * SELECT_TYPE_HASH 1
	 * SELECT_TYPE_DELAY 2
	 */
	// select original
	select_fields_t *sel = string2select_field("id:1,t:1,v:2,v2", mem_);
	t->IdentifyQueryWay(sel, mem_);
	select_row_t *result = t->QueryRow((MileHandler *)iter->CurrentItem(), sel, mem_);
	ASSERT_EQ(4, result->n);
	for (int i = 0; i < 4; i++)
		ASSERT_EQ(SELECT_TYPE_ORIGINAL, result->select_type[i]);
	ASSERT_STREQ("abc", std::string((char *)result->data[0].data, result->data[0].len).c_str());
	ASSERT_EQ(423, *(uint32_t*)result->data[1].data);
	ASSERT_STREQ("value", std::string((char *)result->data[2].data, result->data[2].len).c_str());
	ASSERT_STREQ("value2", std::string((char *)result->data[3].data, result->data[3].len).c_str());

	// select from filter and no delay field
	sel = string2select_field("t:1,v:2", mem_);
	t->IdentifyQueryWay(sel, mem_);
	result = t->QueryRow((MileHandler *)iter->CurrentItem(), sel, mem_);
	ASSERT_EQ(2, result->n);
	ASSERT_EQ(SELECT_TYPE_ORIGINAL, result->select_type[0]);
	ASSERT_EQ(SELECT_TYPE_DELAY, result->select_type[1]);
	ASSERT_EQ(423, *(uint32_t*)result->data[0].data);
	ASSERT_STRNE("value", std::string((char *)result->data[1].data, result->data[1].len).c_str());

	// select original, only one field
	sel = string2select_field("v", mem_);
	t->IdentifyQueryWay(sel, mem_);
	result = t->QueryRow((MileHandler *)iter->CurrentItem(), sel, mem_);
	ASSERT_EQ(1, result->n);
	ASSERT_EQ(SELECT_TYPE_ORIGINAL, result->select_type[0]);
	ASSERT_STREQ("value", std::string((char *)result->data[0].data, result->data[0].len).c_str());

	DELETE(iter);
	DELETE(t);
}

TEST_F(DocEngineTest, del)
{
	Insert();

	TableManager *t = engine_->GetTableManager("t1", mem_);
	// id = abc
	MileIterator *iter = t->UseIndex(ConditionHelper(mem_)
			.Add("id", HI_TYPE_STRING, CT_EQ, "abc")
			.ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(7, row_count(iter));

	// delete the first one
	iter->First();
	t->DeleteRow((MileHandler *)iter->CurrentItem(), mem_);
	DELETE(iter);

	// count again
	iter = t->UseIndex(ConditionHelper(mem_)
			.Add("id", HI_TYPE_STRING, CT_EQ, "abc")
			.ToCondArray(), &hint_, mem_);
	ASSERT_TRUE(iter != NULL);
	ASSERT_EQ(6, row_count(iter));
	DELETE(iter);

	DELETE(t);
}

TEST_F(DocEngineTest, TotalRowCount)
{
	Insert();
	TableManager *t = engine_->GetTableManager("t1", mem_);
	ASSERT_EQ(10, t->TotalRowCount(mem_));
	DELETE(t);
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}

