/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_schema_parser.h
 *
 *    Description:  解析db的配置文件接口
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


#include "../../common/def.h"
#include "db.h"

#ifndef CONFIG_PARSER_H
#define CONFIG_PARSER_H

#define MAX_CONFIG_LINE 1024

struct db_conf *config_parser(const char *file_name, MEM_POOL *mem_pool);


struct data_import_conf *data_import_config_parser(const char *file_name, MEM_POOL *mem_pool);


#endif // CONFIG_PARSER_H

