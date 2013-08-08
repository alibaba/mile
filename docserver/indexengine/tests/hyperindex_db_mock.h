#ifndef HI_DB_MOCK_H
#define HI_DB_MOCK_H
#include "../../indexengine/src/include/mem.h"
#include "../../indexengine/src/include/hyperindex_def.h"





/**
 * 设置调用mock_db_query_by_rowid方法后的函数返回值
 * @param  data_array	函数返回值的数组，之后当第i次调用方法mock_db_query_by_rowid时，会返回data_array[i-1]其中的值	
 * @param  n			数组的大小
 * @param  tid
 * @param  sid
 * @param  row_id
 * @param  field_id
 **/
void mock_db_query_by_rowid_output(void** data_array, uint32 n, uint8 tid, uint16 sid, uint32 row_id, uint16 field_id);





/**
  * mock的db_query_by_rowid方法
  * @param  tid 表号   
  * @param  sid 段号
  * @param  row_id 行号
  * @param  field_id 域号
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct low_data_struct* mock_db_query_by_rowid(uint8 tid,uint16 sid,uint32 row_id,uint16 field_id,MEM_POOL* mem_pool);





/**
  * 获取指定域的数据类型
  * @param  tid 表号
  * @param  field_id 域号
  * @return 成功返回field_types信息，失败<0
  **/ 
enum field_types mock_db_getfield_type(uint8 tid,uint16 field_id);






#endif

