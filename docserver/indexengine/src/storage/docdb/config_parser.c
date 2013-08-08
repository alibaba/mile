/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_schema_parser.h
 *
 *    Description:  解析db的配置文件实现
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


#include "config_parser.h"

#include <string.h>
#include <ctype.h>

struct config_item {
	char *buf;
	const char *key;
	const char *value;
};


static int parse_config_items(const char *file_name, struct config_item *items, int max_item_num)
{
	memset(items, 0, sizeof( struct config_item ) * max_item_num);

	FILE *fp = fopen(file_name, "rb");
	if (NULL == fp) {
		log_error("open file error");
		return -1;
	}

	int i, n;
	size_t buf_len = 0;
	char *p, *end;
	for (i = 0; i < max_item_num; i++) {
		while ( (n = getline(&items[i].buf, &buf_len, fp)) >= 0) {
			for (p = items[i].buf; *p != '\0' && isspace(*p); p++)
				;
			// empty or comment
			if (*p == '\0' || *p == '#')
				continue;

			n = strcspn(p, "=");
			if (0 == n || '=' != p[n]) {
				log_warn("invalid config [%s]", items[i].buf);
				continue;
			}
			break;
		}
		if (n < 0)
			break;

		// key
		items[i].key = p;
		end = p + n;
		p += n + 1;
		do
			*(end--) =  '\0';
		while (isspace(*end) );

		// value
		while (*p != '\0' && isspace(*p)) p++;
		items[i].value = p;
		while (!isspace(*p) ) p++;  // not support value contain space, or comment after value
		*p = '\0';
	}

	fclose(fp);
	return 0;
}

static void free_config_items(struct config_item *items)
{
	for (; items->buf; items++) {
		free(items->buf);
		memset(items, 0, sizeof( struct config_item ) );
	}
}

static const char *get_config_value(const struct config_item *items, const char *key, const char *default_value)
{
	assert(NULL != items && NULL != key);
	for (; items->key; items++) {
		if (strcasecmp(items->key, key) == 0)
			return items->value;
	}
	log_debug("no config item [%s], default value is [%s]", key, default_value ? default_value : "(null)");
	return default_value;
}


struct data_import_conf *data_import_config_parser(const char *file_name, MEM_POOL *mem_pool)
{
	struct config_item items[MAX_CONFIG_LINE + 1];

	memset(items, 0, sizeof(items) );

	if (parse_config_items(file_name, items, MAX_CONFIG_LINE) != 0) {
		log_error("parse config file failed.");
		return NULL;
	}

	struct data_import_conf *conf = (struct data_import_conf *)mem_pool_malloc(mem_pool, sizeof(struct data_import_conf));
	memset(conf, 0, sizeof(struct data_import_conf));

#define SET_STR_VALUE(key, default_value) strcpy(conf->key, get_config_value(items, # key, default_value) )
#define SET_INT_VALUE(key, def_int) do { if (get_config_value(items, # key, NULL) ) { conf->key = atoll(get_config_value(items, # key, NULL) ); } \
										 else { log_info("set config item[%s] to default integer value [%lld]", # key, (int64_t)(def_int) ); conf->key = (def_int); } } while (0)
#define SET_INT_VALUE_NO_DEFAULT(key) do { conf->key = atoll(get_config_value(items, # key, NULL) ); } while (0)


	SET_STR_VALUE(table_name, NULL);
	SET_STR_VALUE(encode_type, NULL);
	SET_STR_VALUE(split, NULL);
	SET_INT_VALUE_NO_DEFAULT(field_count);

	//解析列信息
	const char *fields_info = get_config_value(items, "fields_info", NULL);

	//列名:列类型:列索引;列名:列类型:列索引
	conf->fields_info = (struct data_import_field_info *)mem_pool_malloc(mem_pool, conf->field_count * sizeof(struct data_import_field_info));
	memset(conf->fields_info, 0, conf->field_count * sizeof(struct data_import_field_info));

	char *token = NULL;
	char *outer_ptr = NULL;
	char *inner_ptr = NULL;

	token = strtok((char *)fields_info, "\n");

	token = strtok_r((char *)fields_info, ";", &outer_ptr);

	uint16_t i;
	char *token2 = NULL;
	for (i = 0; i < conf->field_count; i++)	{
		token2 = strtok_r(token, ":", &inner_ptr);
		strcpy((conf->fields_info + i)->field_name, token2);

		token2 = strtok_r(NULL, ":", &inner_ptr);
		(conf->fields_info + i)->index_type = (enum index_key_alg)atoi(token2);

		token2 = strtok_r(NULL, ":", &inner_ptr);
		(conf->fields_info + i)->data_type = (enum field_types)atoi(token2);

		token = strtok_r(NULL, ";", &outer_ptr);
	}

	return conf;
}

static void set_storage_dir(struct str_array_t *dirs, const char *config_value, MEM_POOL_PTR mem);

struct db_conf *config_parser(const char *file_name, MEM_POOL *mem_pool)
{
	return NULL;
#if 0
	struct config_item items[MAX_CONFIG_LINE + 1];

	memset(items, 0, sizeof(items) );

	if (parse_config_items(file_name, items, MAX_CONFIG_LINE) != 0) {
		log_error("parse config file failed.");
		return NULL;
	}

	struct db_conf *conf = (struct db_conf *)mem_pool_malloc(mem_pool, sizeof(struct db_conf));
	memset(conf, 0, sizeof(struct db_conf));

#define SET_STR_VALUE(key, default_value) strcpy(conf->key, get_config_value(items, # key, default_value) )
#define SET_INT_VALUE(key, def_int) do { if (get_config_value(items, # key, NULL) ) { conf->key = atoll(get_config_value(items, # key, NULL) ); } \
										 else { log_info("set config item[%s] to default integer value [%lld]", # key, (int64_t)(def_int) ); conf->key = (def_int); } } while (0)
#define SET_INT_VALUE_NO_DEFAULT(key) do { conf->key = atoll(get_config_value(items, # key, NULL) ); } while (0)

	// SET_STR_VALUE( work_space, NULL );

	SET_STR_VALUE(log_dir, NULL);
	SET_STR_VALUE(log_level, "DEBUG");
	SET_STR_VALUE(binlog_dir, "");

	SET_INT_VALUE(checkpoint_interval, 60 * 30);   // TODO
	SET_INT_VALUE(binlog_maxsize, 50 * 1024 * 1024L);   // 50M
	SET_INT_VALUE(binlog_flag, 0);
	SET_INT_VALUE(binlog_threshold, 0);
	SET_INT_VALUE(binlog_sync_interval, 1 * 1000);   // 1 second
	SET_INT_VALUE(slave_pull_interval, 500 * 1000);
	SET_INT_VALUE(cpu_threshold, 90);
	SET_INT_VALUE(store_raw, 1);
	SET_INT_VALUE(port, DOCSERVER_PORT);

	const char *value = get_config_value(items, "role", "master");
	if (strcasecmp(value, "master") == 0) {
		conf->role = MASTER_ROLE;
	}
	else if (strcasecmp(value, "slave") == 0) {
		conf->role = SLAVE_ROLE;
	}
	else {
		log_error("invalid value [%s] for role, only master or slave. set to master", value);
		conf->role = MASTER_ROLE;
	}

	if (SLAVE_ROLE == conf->role) {
		SET_INT_VALUE(sync_port, DOCSERVER_PORT + 1);
		SET_STR_VALUE(sync_addr, "127.0.0.1");
	}

	SET_INT_VALUE(thread_num, DEFAULT_THREAD_NUM);
	SET_INT_VALUE(hash_compress_num, 10);
	SET_INT_VALUE(max_segment_num, 1024);
	SET_INT_VALUE(row_limit, 10000000);
	SET_INT_VALUE(profiler_threshold, 10000);
	SET_INT_VALUE(perf_interval, 30);
	SET_INT_VALUE(max_result_size, DEFAULT_MAX_RESULT_SIZE);

	value = get_config_value(items, "work_space", NULL);
	set_storage_dir(&conf->storage_dirs, value, mem_pool);

	// disk write limit
	if ( (value = get_config_value(items, "disk_write_limit", NULL)) != NULL)
		mile_conf.disk_write_limit = atoll(value);
	// always use mmap
	if ( (value = get_config_value(items, "all_mmap", NULL)) != NULL)
		mile_conf.all_mmap = atoll(value);

	// set load_threshold (double)
	conf->load_threshold = atof(get_config_value(items, "load_threshold", "0"));

#undef SET_STR_VALUE
#undef SET_INT_VALUE
#undef SET_INT_VALUE_NO_DEFAULT

	free_config_items(items);
	return conf;
#endif
}

static void set_storage_dir(struct str_array_t *dirs, const char *config_value, MEM_POOL_PTR mem)
{
	assert(NULL != config_value);

	// get dir number
	dirs->n = 0;
	for (const char *p = config_value; *p != '\0'; ) {
		int n = strcspn(p, ";");
		if (n > 0)
			dirs->n++;
		p += n + 1;
	}

	assert(dirs->n > 0); // config_value only contain ',' will fail

	// alloc memory
	dirs->strs = (char **)mem_pool_malloc(mem, sizeof(char *) * dirs->n);

	char *p = (char *)mem_pool_malloc(mem, strlen(config_value) + 1);
	strcpy(p, config_value);

	// set dir pointers
	for (int i = 0; *p != '\0'; ) {
		dirs->strs[i] = p;
		int n = strcspn(p, ";");
		p[n] = '\0';
		if (n > 0)
			i++;
		p += n + 1;
	}
}

