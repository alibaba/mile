// mpalloc_test.cpp : mpalloc_test
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-17

#include "../MPAlloc.h"
#include <gtest/gtest.h>
#include <string>
#include <vector>
#include <list>
#include <set>
#include <iostream>
#include <map>

TEST(MPAlloc, all) {
	MEM_POOL_PTR pool = mem_pool_init(1024);
	MPAlloc<int> alloc(pool);
	{
		// std::list test
		std::list<int, MPAlloc<int> > l(alloc);

		for (int i = 0; i < 1024; i++) {
			l.push_back(i);
		}

		std::cout << *l.begin() << std::endl;
	}

	{
		// Allocate memory exceed mem_pool's block size
		std::vector<int, MPAlloc<int> > v(alloc);
		ASSERT_DEATH(v.reserve(1024), "Assertion.*size <= pMemPool->size.*failed");
	}

	// std::map example
	{
		MPAlloc<std::pair<int, std::string> > map_alloc(pool);

		// replace map_alloc with alloc will be OK, because it will rebind.
		std::map<int, std::string, std::less<int>, MPAlloc<std::pair<int, std::string> > > m(std::less<int>(), map_alloc);
		m.insert(std::make_pair(10, "abc"));
		ASSERT_STREQ("abc", m[10].c_str());

		for (int i = 0; i < 1024; i++) {
			m.insert(std::make_pair(i, "a"));
		}

		std::cout << m.size() << std::endl;
	}

	// Use after pool destroy
	mem_pool_destroy(pool);
	std::list<int, MPAlloc<int> > l(alloc);
	ASSERT_EXIT(l.push_back(0), ::testing::KilledBySignal(SIGSEGV), "");

	// With NEW
	{
		MEM_POOL_PTR pool = mem_pool_init(1024);
		MPAlloc<int> alloc(pool);

		typedef std::list<int, MPAlloc<int> > int_list_t;

		int_list_t *list = NEW(pool, int_list_t)(alloc);
		for (int i = 0; i < 1024; i++)
			list->push_back(i);

		typedef std::set<int, std::less<int>, MPAlloc<int> >  int_set_t;
		int_set_t *set = NEW(pool, int_set_t)(std::less<int>(), alloc);
		for (int i = 0; i < 1024; i++)
			set->insert(i);

		// In this case, ~set(), ~list() can be omited.
		set->~set();
		list->~list();

		mem_pool_destroy(pool);
	}
}

int main(int argc, char **argv) {
	::testing::InitGoogleTest(&argc, argv);
	return RUN_ALL_TESTS();
}
