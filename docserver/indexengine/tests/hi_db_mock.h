#ifndef HI_DB_MOCK_H
#define HI_DB_MOCK_H

/**
 *	mock的db相关方法
 */
 
#include "../../indexengine/src/include/hyperindex_db.h"
#include "./seg_rowid_list_test.h"






#define TEST_MAX_SEG_NUM 10

#define TEST_MAX_ROW_NUM 1000

#define TEST_MAX_FIELD_NUM 10





uint32 current_seg;

uint32 current_row;

uint32 field_num;



//mock的数据源
struct low_data_struct mock_data_source[TEST_MAX_SEG_NUM][TEST_MAX_ROW_NUM][TEST_MAX_FIELD_NUM];


//mock的删除标记
uint8 mock_delete_mask[TEST_MAX_SEG_NUM][TEST_MAX_ROW_NUM];


//mock的列类型数组
enum field_types mock_fields_type_array[TEST_MAX_FIELD_NUM];


//mock的列索引数组
enum index_key_alg mock_index_type_array[TEST_MAX_FIELD_NUM];



int32 mock_db_init(uint32 column_num, enum field_types* fields_type_array, enum index_key_alg* index_type_array);







#endif

