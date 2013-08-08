// doc_data_test.cpp : doc_data_test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-06-18

#include "../ConfigFile.h"
#include <gtest/gtest.h>
#include <unistd.h>
#include <string>
#include <iostream>
#include <fstream>


TEST(ConfigFile, all)
{
	const char *test_file_path = "./config_file_test.conf";
	unlink(test_file_path);

	ConfigFile conf;
	ASSERT_EQ(-1, conf.LoadFile(test_file_path));

	std::ofstream of(test_file_path);
	of << "# comment" << std::endl;
	of << "[]" << std::endl;
	of << "key1 = 123" << std::endl;
	of << " key2= \t-123 " << std::endl;
	of << "key4 = 123" << std::endl;
	of << "   [ ts ] " << std::endl;
	of << "key1 = abc" << std::endl;
	of << "key2 =" << std::endl;
	of << "[ttt]" << std::endl;
	of << "key1 = key1" << std::endl;
	of << "key2 =1024" << std::endl;
	of << "key3 =1024 # 1M" << std::endl;
	of << "[]" << std::endl;
	of << "key4 = 1233" << std::endl;
	of.close();

	ASSERT_EQ(0, conf.LoadFile(test_file_path));

	ASSERT_EQ(123, conf.GetIntValue(CONFIG_DEFAULT_SESSION, "key1"));
	ASSERT_EQ(-123, conf.GetIntValue(CONFIG_DEFAULT_SESSION, "key2"));

	ASSERT_STREQ("abc", conf.GetValue("ts", "key1", ""));
	ASSERT_STREQ("", conf.GetValue("ts", "key2", "abc"));

	ASSERT_STREQ("key1", conf.GetValue("ttt", "key1", ""));
	ASSERT_EQ(1024, conf.GetIntValue("ttt", "key2", 111));
	ASSERT_EQ(1024, conf.GetIntValue("ttt", "key3", 123));

	ASSERT_STREQ("1024", conf.GetValue("ttt", "key2"));

	//  comment after value not supported
	ASSERT_STREQ("1024 # 1M", conf.GetValue("ttt", "key3"));

	// test overwrite.
	ASSERT_EQ(1233, conf.GetIntValue(CONFIG_DEFAULT_SESSION, "key4"));

	ConfigFile conf2(conf);

	conf2 = conf;

	std::string dump = conf2.Dump();
	std::cout << dump << std::endl;

	unlink(test_file_path);
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}

