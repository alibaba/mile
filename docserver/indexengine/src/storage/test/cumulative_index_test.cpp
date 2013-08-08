// ldb_storage_test.cpp : ldb_storage_test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-09

#include "storage_engine_helper.h"

#include "../ldb/LdbConfig.h"
#include "../ldb/LdbEngine.h"
#include "../ldb/LdbTableMgr.h"
#include "../../common/ConfigFile.h"
#include "../../common/mem.h"
#include "../../protocol/packet.h"

#include <gtest/gtest.h>
#include <unistd.h>
#include <sys/stat.h>
#include <string>

class CumulativeIndexTest: public ::testing::Test
{
protected:
	CumulativeIndexTest() : dir_("./_cumulative_index_test_"), engine_(NULL) {}

	virtual void SetUp()
	{
		ClearDir();
		::mkdir(dir_.c_str(), 0755);
		mem_ = mem_pool_init( 1 << 20); // MB

		// init config
		ConfigFile &conf = *ConfigFile::GlobalInstance();
		conf.SetValue(CONF_LDB_SESSION, CONF_LDB_TABLES, "t");

		// use user specified time
		conf.SetValue(CONF_LDB_SESSION, "t.row_key", "k");
		conf.SetValue(CONF_LDB_SESSION, "t.time_key", "t");
		conf.SetValue(CONF_LDB_SESSION, "t.time_key_scale", "0");
		conf.SetValue(CONF_LDB_SESSION, "t.time_key_len", "4");

		conf.SetValue(CONF_LDB_SESSION, "t.cumulative_step", "10,100,200");
		conf.SetValue(CONF_LDB_SESSION, "t.aggregate_desc", "count(*),max(value),min(value),sum(value)");

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

	std::string dir_;
	LdbEngine *engine_;
	MEM_POOL_PTR mem_;
};

TEST_F(CumulativeIndexTest, insert_query)
{
	MileHandler *handler = NULL;

	TableManager *t = engine_->GetTableManager("t", mem_);
	ASSERT_TRUE(NULL != t);

	// insert
	char buf[512];
	for (int i = 0; i <= 533; i++) {
		snprintf(buf, sizeof(buf), "k:abc,t:5:%d,value:5:%d", i, i);
		ASSERT_EQ(0, t->InsertRow(handler, string2row(buf, mem_), mem_));
	}

	LdbTableMgr *l = dynamic_cast<LdbTableMgr *>(t);
	EXPECT_TRUE(l);

	// query
	const char *sql = "sum(value), min(value), max(value), count(*)";
	low_data_struct res[4];

	int rc = l->CumulativeQuery(res, string2func_select(sql, mem_), ConditionHelper(mem_)
			.Add("k", HI_TYPE_STRING, CT_EQ, "abc")
			.Add("t", HI_TYPE_UNSIGNED_LONG, EXP_COMPARE_BETWEEN_LEGE, "2", "466") // valid range [11, 460]
			.ToCondArray()
			,mem_);

	EXPECT_EQ(0, rc);

	// sum
	EXPECT_NE(0, res[0].len);
	EXPECT_EQ((11 + 460) * (460 - 11 + 1) / 2, (int)*(double*)res[0].data);

	// min
	EXPECT_NE(0, res[1].len);
	EXPECT_EQ(11, *(uint32_t *)res[1].data);

	// max
	EXPECT_NE(0, res[2].len);
	EXPECT_EQ(460, *(uint32_t *)res[2].data);

	// count
	EXPECT_NE(0, res[3].len);
	EXPECT_EQ(460 - 11 + 1, *(uint64_t *)res[3].data);

	// query all
	rc = l->CumulativeQuery(res, string2func_select(sql, mem_), ConditionHelper(mem_)
			.Add("k", HI_TYPE_STRING, CT_EQ, "abc")
			.ToCondArray()
			,mem_);

	EXPECT_EQ(0, rc);

	// sum
	EXPECT_EQ(533 * (533 + 1) / 2, (int)*(double*)res[0].data);
	// min
	EXPECT_EQ(0, *(uint32_t *)res[1].data);
	// max
	EXPECT_EQ(533, *(uint32_t *)res[2].data);
	// count
	EXPECT_EQ(534, *(uint64_t *)res[3].data);

	DELETE(t);
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}

