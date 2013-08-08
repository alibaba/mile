/*
 * =====================================================================================
 *
 *       Filename:  hi_hash.h
 *
 *    Description:  hash函数的定义
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


#include "../common/def.h"


#ifndef HASH_H
#define HASH_H

/*判断是否为数字*/
#define ISNUM(ch) (ch) >= '0' && (ch) <= '9'

/*定长最长字节数*/
#define MAX_HASH_LEN 128



/**
  * 根据types，获取64位的hash值
  * @param  data 数据
  * @param  types 数据类型
  * @return 返回64位哈希后的值
  **/ 
uint64_t get_hash_value(struct low_data_struct* data);

#endif
