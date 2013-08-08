#ifndef CONFIG_H
#define CONFIG_H
#include <stdio.h>
#include <string.h>
#include "../src/common/def.h"
#include "../src/common/mem.h"
#include "../src/storage/db.h"


struct hint_array * get_time_hint(MEM_POOL* mem_pool);

int32 verify_low_data(struct low_data_struct* data,enum field_types type);

int32 verify_row_data(struct row_data* rdata,uint16 field_count,enum field_types* types);

void get_low_data(struct low_data_struct* data,enum field_types type,MEM_POOL* mem_pool);

void get_low_data2(struct low_data_struct* data, int value, enum field_types type,MEM_POOL* mem_pool);

struct row_data* get_row_data(uint16 field_count,enum field_types* types,MEM_POOL* mem_pool);


#endif
