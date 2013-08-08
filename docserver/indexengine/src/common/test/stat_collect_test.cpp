// stat_collect_test.cpp : stat_collect_test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-05-25

#include <gtest/gtest.h>
// #include <thread>

#include "../stat_collect.h"
#include "../mem.h"

TEST(stat_collect, dump_and_reset)
{
	stat_collect_init(1000);

	/* g++ 4.6 or higher
	std::thread t(
			[]{
			while (true)
			{ 
				usleep(1300000);
				sc_record_value(STAT_ITEM_INDEX_VALUE, 1);
			}
			});
	*/

	while (true) {
		usleep(5);
		sc_record_value(random() % 2 ? STAT_ITEM_INSERT : STAT_ITEM_UPDATE, random());
	}
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}

