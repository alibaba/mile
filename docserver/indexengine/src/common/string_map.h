// string_map.h : string_map
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-11-07

#ifndef STRING_MAP_H
#define STRING_MAP_H

#include "mem.h"
#include "common_util.h"

typedef int32_t (*HASHMAP_FUNC) (struct low_data_struct** key, uint32_t key_elem_num, void* value, void* user_data);


/*
 * string_map : only support c string for map key
 * NOTICE: implemented by ordered array.
 */

struct key_value_t {
	char *key;
	void *value;
};

struct string_map {
	MEM_POOL_PTR mem;
	uint32_t size;
	uint32_t pos;
	struct key_value_t *data;
};

struct string_map *init_string_map( MEM_POOL_PTR mem, uint32_t size );

int string_map_put( struct string_map *map, char *key, void *value, int8_t is_copy );

void *string_map_get( struct string_map *map, char *key );

void *string_map_remove( struct string_map *map, char *key );

// break loop, if return value of func < 0
int32_t string_map_for_each( struct string_map *map, HASHMAP_FUNC func, void *user_data );

#endif // STRING_MAP_H
