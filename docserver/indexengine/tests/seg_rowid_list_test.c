#include "seg_rowid_list_test.h"






int check_seg_rowid_list_equal(struct list_head* lista, struct list_head* listb)
{

	struct segment_query_rowids* qresulta;
	struct segment_query_rowids* qresultb;

	double_list_for_each_entry(qresulta, lista, qresultb, listb, rowids_list)
	{
		if(qresulta->sid != qresultb->sid)
		{
			log_error("段号不一致!\n");
			return 0;
		}
		if(!rowid_list_equals(qresulta->rowids, qresultb->rowids))
		{
			log_error("结果不正确!\n");
			return 0;
		}
	}
	
	return 1;
}






int is_seg_rowid_list_empty(struct list_head* list)
{
	struct segment_query_rowids* result;
	uint32 rowid_num = 0;
	
	list_for_each_entry(result, list, rowids_list)
	{
		rowid_num += result->rowids->rowid_num;
	}

	if(rowid_num == 0)
	{
		return 1;
	}
	return 0;
}








struct rowid_list* gen_rowid_list(MEM_POOL_PTR pMemPool, uint32* rowid_array, uint32 n)
{
	struct rowid_list* id_list;

	id_list = rowid_list_init(pMemPool);
	uint32 i;

	for(i = 0; i < n; i++)
	{
		rowid_list_add(pMemPool, id_list, rowid_array[i]);
	}

	return id_list;
}













void gen_seg_rowid_list(MEM_POOL_PTR pMemPool, struct list_head* head, uint16 sid, uint32* rowid_array, uint32 n)
{
	struct segment_query_rowids* seg_list;

	seg_list = (struct segment_query_rowids*) mem_pool_malloc(pMemPool, sizeof(struct segment_query_rowids));
	seg_list->sid = sid;
	seg_list->rowids = gen_rowid_list(pMemPool, rowid_array, n);
		
	list_add(&seg_list->rowids_list, head);
	
}







void set_array(uint32* array, char* values)
{
	uint32 i, j, k;

	k = 0;
	j = 0;
	for(i = 0; i < strlen(values); i++)
	{
		if(values[i] == ',')
		{
			array[k++] = j;
			j = 0;
		}else if(values[i] >= '0' && values[i] <= '9'){
			j = j*10 + values[i] - '0';
		}
	}
	array[k++] = j;
}







