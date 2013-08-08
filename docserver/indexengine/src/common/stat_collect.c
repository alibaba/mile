// stat_collect.c : stat_collect
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-05-24

#include "stat_collect.h"

static const char *stat_item_names[STAT_ITEM_MAX] = {
	"insert",
	"index_equal",
	"index_value",
	"index_range",
	"data_query",
	"update",
	"query_all_docids",
	"recv_queue_insert",
	"recv_queue_query",
	"process_insert",
	"process_query",
	"send_queue_insert",
	"send_queue_query",
	"vstore_write",
	"vstore_read",
	"binlog_write"
};

static struct stat_collect_t g_stat_collect;

static void reset_total();
static void reset_current(uint64_t now);
// called by detach thread
static void *dump_and_reset_thread(void *arg);

void stat_collect_init(uint32_t duration_ms)
{
	uint64_t now = get_time_usec();

	g_stat_collect.duration_ms = duration_ms;
	g_stat_collect.item_start_time = now;
	g_stat_collect.total_start_time = now;

	for (int i = 0; i < STAT_ITEM_MAX; i++) {
		g_stat_collect.stats[i].min_value = (uint64_t)-1;
	}

	// create dump and reset thread
	pthread_attr_t attr;
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);

	pthread_t tid;
	if (pthread_create(&tid, &attr, dump_and_reset_thread, NULL) != 0) {
		log_error("create thread failed, errno %d", errno);
	}
}

void sc_record_value(enum stat_item_type type, uint64_t value)
{
	struct stat_item_t *item = &g_stat_collect.stats[type];

	Mile_AtomicAddPtr(&item->total_value_sum, value);
	Mile_AtomicAddPtr(&item->total_count, 1);

	// set min
	int retry_count = 3;
	for (uint64_t v = item->min_value; value < v && retry_count > 0 &&
			!__sync_bool_compare_and_swap(&item->min_value, v, value); retry_count--) {
		__sync_synchronize();
		v = item->min_value;
	}

	// set max
	retry_count = 3;
	for (uint64_t v = item->max_value; value > v && retry_count > 0 &&
			!__sync_bool_compare_and_swap(&item->max_value, v, value); retry_count--) {
		__sync_synchronize();
		v = item->max_value;
	}

	Mile_AtomicAddPtr(&item->value_sum, value);
	Mile_AtomicAddPtr(&item->count, 1);
}

void sc_dump_and_reset(void)
{
	uint64_t now = get_time_usec();
	uint64_t dur_current = now - g_stat_collect.item_start_time;
	uint64_t dur_total = now - g_stat_collect.total_start_time;

	if (dur_current <= 0) dur_current = 1;
	if (dur_total <= 0) dur_total = 1;

	for (int i = 0; i < STAT_ITEM_MAX; i++) {

		struct stat_item_t *item = &g_stat_collect.stats[i];
		uint64_t total_count = item->total_count;
		uint64_t count = item->count;

		log_perf("%s," // item name
				"%" PRIu64 "," // total count
				"%.3f," // total average
				"%.3f," // total throughput
				"%" PRIu64 "," // current count
				"%.3f," // current average
				"%.3f," // current throughput
				"%" PRIu64 "," // current max
				"%" PRIu64, // current min

				stat_item_names[i],
				item->total_count,
				total_count == 0 ? 0 : ((double)item->total_value_sum / total_count),
				item->total_count / (double)dur_total * 1000000,
				item->count,
				count == 0 ? 0 : ((double)item->value_sum / count),
				item->count / (double)dur_current * 1000000,
				item->max_value,
				((uint64_t)-1 == item->min_value) ? 0 : item->min_value);
	}

	reset_current(now);
}

static void reset_total()
{
	g_stat_collect.total_start_time = get_time_usec();
	for (int i = 0; i < STAT_ITEM_MAX; i++) {
		struct stat_item_t *item = &g_stat_collect.stats[i];
		Mile_AtomicSetPtr(&item->total_count, 0);
		Mile_AtomicSetPtr(&item->total_value_sum, 0);
	}
}

static void reset_current(uint64_t now)
{
	g_stat_collect.item_start_time = (0 != now ? now : get_time_usec());
	for (int i = 0; i < STAT_ITEM_MAX; i++) {
		struct stat_item_t *item = &g_stat_collect.stats[i];
		Mile_AtomicSetPtr(&item->value_sum, 0);
		Mile_AtomicSetPtr(&item->max_value, 0);
		Mile_AtomicSetPtr(&item->min_value, -1);
		Mile_AtomicSetPtr(&item->count, 0);
	}
}

static void *dump_and_reset_thread(void *arg)
{
	int reseted = 0;

	while (g_running_flag) {
		usleep(g_stat_collect.duration_ms * 1000);
		if (!g_running_flag)
			break;

		sc_dump_and_reset();

		// if between 00:00 to 01:00 reset total count
		time_t t = time(NULL);
		struct tm tm;
		localtime_r(&t, &tm);

		if (tm.tm_hour == 0) {
			if (!reseted)
				reset_total();
			reseted = 1;
		}
		else
			reseted = 0;
	}

	return NULL;
}

