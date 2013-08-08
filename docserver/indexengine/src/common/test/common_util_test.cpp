// doc_data_test.cpp : doc_data_test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-06-18

#include <gtest/gtest.h>
#include <string>

#include "../common_util.h"

TEST(common_util, all_str_append)
{
	struct str_array_t sa;
	sa.n = 3;
	MEM_POOL_PTR mem = mem_pool_init(1024 * 1024);
	sa.strs = (char **)mem_pool_malloc(mem, sa.n * sizeof(char *));

	char tmp[] = {'a', '\0', 'b', 'c', '\0', 'd', 'e', 'f', '\0'};
	sa.strs[0] = tmp;
	sa.strs[1] = tmp + 2;
	sa.strs[2] = tmp + 5;

	struct str_array_t * s = all_str_append(&sa, mem, "%s", "");
	ASSERT_STREQ("a", s->strs[0]);
	ASSERT_STREQ("bc", s->strs[1]);
	ASSERT_STREQ("def", s->strs[2]);

	s = all_str_append(s, mem, "%s%s", "/", "tablename");
	ASSERT_STREQ("a/tablename", s->strs[0]);
	ASSERT_STREQ("bc/tablename", s->strs[1]);
	ASSERT_STREQ("def/tablename", s->strs[2]);
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}

