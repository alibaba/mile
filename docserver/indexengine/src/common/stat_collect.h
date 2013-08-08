/*
 * =====================================================================================
 *
 *       Filename:  stat_collect.h
 *
 *    Description:  用于性能统计
 *
 *        Version:  1.0
 *        Created:  2011年04月09日 11时41分55秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */

#include "def.h"

#ifndef STORE_STATS_H
#define STORE_STATS_H


// add stat item here must add stat item name in .c file.
enum stat_item_type {
	STAT_ITEM_INSERT = 0,
	STAT_ITEM_INDEX_EQUAL,
	STAT_ITEM_INDEX_VALUE,
	STAT_ITEM_INDEX_RANGE,
	STAT_ITEM_DATA_QUERY,
	STAT_ITEM_UPDATE,
	STAT_ITEM_QUERY_ALL_DOCIDS,
	STAT_ITEM_RECV_QUEUE_INSERT,
	STAT_ITEM_RECV_QUEUE_QUERY,
	STAT_ITEM_PROCESS_INSERT,
	STAT_ITEM_PROCESS_QUERY,
	STAT_ITEM_SEND_QUEUE_INSERT,
	STAT_ITEM_SEND_QUEUE_QUERY,
	STAT_ITEM_VSTORE_WRITE,
	STAT_ITEM_VSTORE_READ,
	STAT_ITEM_BINLOG_WRITE,
	STAT_ITEM_MAX
};

struct stat_item_t {
	volatile uint64_t value_sum;
	volatile uint64_t count;
	volatile uint64_t max_value;
	volatile uint64_t min_value;

	volatile uint64_t total_value_sum;
	volatile uint64_t total_count;
} __attribute__ ((aligned(LEVEL0_CACHE_LINE_SIZE)));

struct stat_collect_t {
	struct stat_item_t stats[STAT_ITEM_MAX];

	volatile uint64_t item_start_time; // us
	volatile uint64_t total_start_time; // us
	uint32_t duration_ms;
};

void stat_collect_init(uint32_t duration_ms);

// sc_ prefix is short for "stat collect"
void sc_record_value(enum stat_item_type, uint64_t value);

void sc_dump_and_reset(void);

#endif // STORE_STATS_H

