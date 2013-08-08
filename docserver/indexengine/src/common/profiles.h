/*
 * =====================================================================================
 *
 *       Filename:  hi_profiles.h
 *
 *    Description:  记录每步的查询时间，超过阈值，则打印到日志里
 *
 *        Version:  1.0
 *        Created: 	2011年04月09日 11时41分55秒 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */


#ifndef PROFILES_H
#define PROFILES_H
#include <pthread.h>
#include <sys/time.h>
#include "list.h"
#include "def.h"
#include "mem.h"

#define MESSAGE_LEN 128


#define PROFILER_START(s) do { if(get_globle_profile()->status == 1) start_profile((s)); } while(0)
#define PROFILER_STOP() do { if(get_globle_profile()->status == 1) reset_profile(); } while (0)
#define PROFILER_BEGIN(s) do { if(get_globle_profile()->status == 1) begin_profile((s)); } while(0)
#define PROFILER_END() do { if(get_globle_profile()->status == 1) end_profile(); } while(0)
#define PROFILER_DUMP() do { if(get_globle_profile()->status == 1) dump_profile(); } while(0)
#define PROFILER_SET_THRESHOLD(sh) get_globle_profile()->threshold = (sh)
#define PROFILER_SET_STATUS(st) get_globle_profile()->status = (st)



struct entry{
	struct entry* first;
	struct entry* parent;
	
	/*是一个栈，栈的头*/
	struct list_head entry_list_h;

	struct list_head entry_list;

	/*记录栈的个数*/
	uint16_t entry_count;

	char message[MESSAGE_LEN];

	uint64_t stime;
	uint64_t btime;
	uint64_t etime;
};


struct profiles{
	struct entry* e;
	uint16_t threshold;
	int16_t status;
	pthread_key_t entry_key;
	pthread_key_t mem_key;
};


/**
 * 多线程性能统计工具
 *
 * 使用方法：
 * PROFILER_START("test"); // 初始化一个统计实例
 *
 * PROFILER_BEGIN("entry a"); // 开始一个计时单元
 * PROFILER_END(); // 结束最近的计时单元
 *
 * PROFILER_BEGIN("entry b");
 * PROFILER_BEGIN("sub entry b1"); // 支持嵌套的计时单元
 * PROFILER_END();
 * PROFILER_END()
 *
 * PROFILER_DUMP(); // dump计时记录
 * PROFILER_STOP(); // 结束这个统计实例
 *
 * 配置参数：
 * PROFILER_SET_STATUS(status); // 设置计数器的状态，如果不是1，则禁用计数器所有功能，此时不会产生任何开销，默认为1
 * PROFILER_SET_THRESHOLD(threshold); // 设置dump的阀值，当一个计数实例的总计时超过这个阀值时才会dump信息，单位为us，默认为10000us(10ms)
 */

int32_t init_profile(uint16_t threshold,MEM_POOL* mem_pool);

void start_profile(char* description);

void stop_profile();

void begin_profile(char* description);

void end_profile();

void dump_profile();

void reset_profile();

uint64_t get_time();


struct profiles* get_globle_profile();
#endif

