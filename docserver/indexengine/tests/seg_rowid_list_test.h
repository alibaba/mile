#ifndef SEG_ROWID_LIST_TEST_H
#define SEG_ROWID_LIST_TEST_H
#include "../../indexengine/src/include/hyperindex_set_operator.h"




int check_seg_rowid_list_equal(struct list_head* lista, struct list_head* listb);


int is_seg_rowid_list_empty(struct list_head* list);


struct rowid_list* gen_rowid_list(MEM_POOL_PTR pMemPool, uint32* rowid_array, uint32 n);



void gen_seg_rowid_list(MEM_POOL_PTR pMemPool, struct list_head* head, uint16 sid, uint32* rowid_array, uint32 n);



void set_array(uint32* array, char* values);


#endif

