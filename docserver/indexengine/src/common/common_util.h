/*
 * =====================================================================================
 *
 *       Filename:  hyperindex_common_util.h
 *
 *    Description:  一些基本的操作函数
 *
 *        Version:  1.0
 *        Created:  2011/06/30 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yuzhong.zhao
 *        Company:  alipay
 *
 * =====================================================================================
 */



#ifndef COMMON_UTIL_H
#define COMMON_UTIL_H

#include <math.h>
#include "log.h"
#include "list.h"
#include "mem.h"
#include "def.h"

// string array
struct str_array_t {
	size_t n;
	char **strs;
};

// append string to all strings in str_array_t
struct str_array_t *all_str_append(const struct str_array_t *src, MEM_POOL_PTR mem, const char *fmt, ...) __attribute__((format(printf, 3, 4)));


/**
 * 	比较两个元素a和b的大小
 *	@param 	a
 *	@param 	b
 *	@param 	field_type	元素的数据类型
 *	@return				当a>b时，返回1；a<b时，返回-1；a=b时，返回0；如果遇到不支持的数据类型，返回-2
 */
int8_t compare(const void* a, const void* b, enum field_types field_type);






/**
 *	交换两个指针指向的内容
 *	@param a
 *	@param b
 *  @param len			指向数据的字节长度
 */
void swap(void* a, void* b, uint32_t len);






/**
 * 	比较两个元素a和b的大小
 *	@param 	a
 *	@param 	b
 *	@param 	field_type	元素的数据类型
 *	@return				当a>b时，返回1；a<b时，返回-1；a=b时，返回0；如果遇到不支持的数据类型，返回-2
 */
int8_t compare_ld(const struct low_data_struct* a, const struct low_data_struct* b);





/**
 * 	将一个low data中的数据拷贝到另一个low data中
 *	@param 	pMemPool	内存池
 *	@param 	des			目标数据
 *	@param	src			源数据
 *	@return				
 */
void copy_low_data_struct(MEM_POOL_PTR pMemPool, struct low_data_struct* des, struct low_data_struct* src);




int32_t is_ld_equal(struct low_data_struct* a, struct low_data_struct* b);



/**
 * 	将a和b的和放到a里面
 *	@param 	a			
 *	@param 	b
 *	@return		成功返回0,失败返回-1
 */
int32_t add_data(struct low_data_struct* a, struct low_data_struct* b);



/**
 * 将ld转换为double类型
 */
double ld_to_double(struct low_data_struct* ld);





struct select_row_t* init_select_row_t(MEM_POOL_PTR mem_pool, uint32_t n);


struct select_fields_t* init_select_fields_t(MEM_POOL_PTR mem_pool, uint32_t n);





/**
  * 将一行数据序列化成一个low_data_struct
  * @param  rdata 一行数据
  * @param  mem_pool 内存池模块
  * @return 返回low_data_struct
  **/ 
struct low_data_struct*  rowdata_to_lowdata(struct row_data* rdata,MEM_POOL* mem_pool);

/**
  * 将一行数据序反序列化成一个row_data
  * @param  rdata 一行数据
  * @param  mem_pool 内存池模块
  * @return 返回row_data
  **/
struct row_data*  lowdata_to_rowdata(struct low_data_struct* rdata,MEM_POOL* mem_pool);


#endif
