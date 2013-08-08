#include "hi_db_mock.h"






int32 mock_db_init(uint32 column_num, enum field_types* fields_type_array, enum index_key_alg* index_type_array)
{
	if(column_num > TEST_MAX_FIELD_NUM)
	{
		return -1;
	}

	uint32 i, j;

	for(i = 0; i < column_num; i++)
	{
		mock_fields_type_array[i] = fields_type_array[i];
		mock_index_type_array[i] = index_type_array[i];
	}

	for(i = 0; i < TEST_MAX_SEG_NUM; i++)
	{
		for(j = 0; j < TEST_MAX_ROW_NUM; j++)
		{
			mock_delete_mask[i][j] = 0;
		}
	}

	current_seg = 0;
	current_row = 0;
	field_num = column_num;
	
	return 0;
}







/**
  * 向指定的表插入一行记录，data是一个指针数组，数组长度为域的数量，如果超过limit则新开辟一个段
  * @param  tid 表号
  * @param  segment_id 段号
  * @param  row_id 行号
  * @param  data 一行的数据
  * @return 成功返回rowid，失败返回<0
  **/ 
int32 db_insert(uint8 tid, uint16* segment_id, uint32* row_id, struct low_data_struct** data)
{
	uint32 i;
	
	if(current_seg >= TEST_MAX_SEG_NUM && current_row >= TEST_MAX_ROW_NUM)
	{
		printf("超过测试mock数据库的最大容量!\n");
		exit(-1);
	}
	
	if(current_row >= TEST_MAX_ROW_NUM)
	{
		current_seg++;
		current_row = 0;
	}

	for(i = 0; i < field_num; i++)
	{
		mock_data_source[current_seg][current_row][i].data = (*data+i)->data;
		mock_data_source[current_seg][current_row][i].len = (*data+i)->len;
	}

	*segment_id = current_seg;
	*row_id = current_row;
	current_row++;

	return MILE_RETURN_SUCCESS;
}





/**
  * 向指定的表的域中更新值，只有filter索引才支持
  * @param  tid 表号
  * @param  sid 段号
  * @param  new_rdata 更新的数据
  * @param  row_id 行号
  * @param  field_id 域号
  * @return 成功返回0，失败返回<0
  **/ 
int32 db_update(uint8 tid,uint16 sid,struct low_data_struct* new_rdata,uint32 row_id,uint16 field_id)
{
	mock_data_source[sid][row_id][field_id].data = new_rdata->data;
	mock_data_source[sid][row_id][field_id].len = new_rdata->len;
	return MILE_RETURN_SUCCESS;
}









/**
  * 根据rowid来查找对应的value，只有filter索引类型支持
  * @param  tid 表号   
  * @param  sid 段号
  * @param  row_id 行号
  * @param  field_id 域号
  * @param  mem_pool 内存池
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
struct low_data_struct* db_query_by_rowid(uint8 tid, uint16 sid,uint32 row_id,uint16 field_id, MEM_POOL* mem_pool)
{
	if(mock_delete_mask[sid][row_id])
	{
		return NULL;
	}
	struct low_data_struct* result = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct));
	result->len = mock_data_source[sid][row_id][field_id].len;
	result->data = mem_pool_malloc(mem_pool, result->len);

	memcpy(result->data, mock_data_source[sid][row_id][field_id].data, result->len);
	return result;

}





/**
  * 根据value来查找对应的doc id lists，只有hash索引支持，所有段扫
  * @param  tid 表号
  * @param  field_id 域号
  * @param  data 数据
  * @param  mem_pool 内存池
  * @return 成功返回list head，上层可以通过这个遍历，取到所有段结果，失败返回NULL
  **/ 
struct list_head* db_query_by_value(uint8 tid,uint16 field_id,struct low_data_struct* data, MEM_POOL* mem_pool)
{
	int32 i, j, k, n;


	//初始化头
	struct list_head* list_h = (struct list_head*)mem_pool_malloc(mem_pool,sizeof(struct list_head));
	
	INIT_LIST_HEAD(list_h);

	struct segment_query_rowids* seg_rowids; 

	n = 0;
	for(i = 0; i <= current_seg; i++)
	{
		if(i == current_seg)
		{
			k = current_row;
		}else{
			k = TEST_MAX_ROW_NUM;
		}

		seg_rowids = (struct segment_query_rowids*) mem_pool_malloc(mem_pool, sizeof(struct segment_query_rowids));
		seg_rowids->sid = i;
		seg_rowids->rowids = rowid_list_init(mem_pool);
		list_add(&seg_rowids->rowids_list, list_h);

		for(j = k-1; j >= 0; j--)
		{
			if(compare(data->data, mock_data_source[i][j][field_id].data, mock_fields_type_array[field_id]) == 0)
			{
				rowid_list_add(mem_pool, seg_rowids->rowids, j);
				n++;
			}
		}
			
	}


	return list_h;
}






/**
  * 根据行号进行删除
  * @param  tid 表号
  * @param  sid 段号
  * @param  row_id 行号
  * @return 成功返回rowid_list，失败返回NULL
  **/ 
int32 db_del_rowid(uint8 tid,uint16 sid,uint32 row_id)
{
	mock_delete_mask[sid][row_id] = 1;
	return MILE_RETURN_SUCCESS;
}





/**
  * 判断指定的doc id是否标记为删除
  * @param  tid 表号
  * @param  sid 段号
  * @param  row_id 行号
  * @return 已删除返回1，未删除返回0  
  **/
int32 db_is_rowid_deleted(uint8 tid,uint16 sid,uint32 row_id)
{
	return mock_delete_mask[sid][row_id];
}




/**
  * 获取指定域的数据类型
  * @param  tid 表号
  * @param  field_id 域号
  * @return 成功返回field_types信息，失败<0
  **/ 
enum field_types db_getfield_type(uint8 tid,uint16 field_id)
{
	return mock_fields_type_array[field_id];
}





/**
  * 获取doc server的节点编号
  * @return 成功返回节点编号
  **/ 
uint16 get_node_id()
{
	return 1;
}




/**
  * 获取指定域的索引类型
  * @param  tid 表号
  * @param  field_id 域号
  * @return 成功返回index_key_alg信息，失败<0
  **/ 
enum index_key_alg db_getindex_type(uint8 tid, uint16 field_id)
{
	return mock_index_type_array[field_id];
}



/**
  * 在btree中做范围查询,在所有数据范围内查询
  * @param  tid 表号 
  * @param  field_id 域号
  * @param  range_condition 查询的条件
  * @param  mem_pool 内存池
  * @return 成功返回list head，上层可以通过这个遍历，取到所有段结果，失败返回NULL
  **/ 
struct list_head * db_btree_query_range(uint8 tid,	uint16 field_id, \
		struct db_range_query_condition *range_condition, MEM_POOL *pMemPool)
{
	int32 i, j, k, n;
	int8 cmp_res;

	//初始化头
	struct list_head* list_h = (struct list_head*)mem_pool_malloc(pMemPool,sizeof(struct list_head));
	
	INIT_LIST_HEAD(list_h);

	struct segment_query_rowids* seg_rowids; 

	n = 0;
	for(i = 0; i <= current_seg; i++)
	{
		if(i == current_seg)
		{
			k = current_row;
		}else{
			k = TEST_MAX_ROW_NUM;
		}

		seg_rowids = (struct segment_query_rowids*) mem_pool_malloc(pMemPool, sizeof(struct segment_query_rowids));
		seg_rowids->sid = i;
		seg_rowids->rowids = rowid_list_init(pMemPool);
		list_add(&seg_rowids->rowids_list, list_h);

		for(j = k-1; j >= 0; j--)
		{
			if(range_condition->min_flag >= 0)
			{
				cmp_res = compare(mock_data_source[i][j][field_id].data, range_condition->min_key->data, mock_fields_type_array[field_id]);
				if((cmp_res <= 0 && range_condition->min_flag == 1) || (cmp_res < 0 && range_condition->min_flag == 0))
				{
					continue;
				}
			}

			if(range_condition->max_flag >= 0)
			{
				cmp_res = compare(mock_data_source[i][j][field_id].data, range_condition->max_key->data, mock_fields_type_array[field_id]);
				if((cmp_res >= 0 && range_condition->max_flag == 1) || (cmp_res > 0 && range_condition->max_flag == 0))
				{
					continue;
				}
			}

			rowid_list_add(pMemPool, seg_rowids->rowids, j);
			n++;
		}
			
	}


	return list_h;	
}
















	













