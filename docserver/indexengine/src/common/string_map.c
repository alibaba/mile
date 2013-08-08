// string_map.c : string_map
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-11-07

#include "string_map.h"

static int strmap_cmp( const void *left, const void *right )
{
	return strcmp( (const char *)left, (const char *)((struct key_value_t *)right)->key );
}

struct string_map *init_string_map( MEM_POOL_PTR mem, uint32_t size )
{
	struct string_map *map = (struct string_map *)mem_pool_malloc( mem, sizeof(struct string_map) );

	map->mem = mem;
	map->pos = 0;
	map->size = size;
	map->data = (struct key_value_t *)mem_pool_malloc( mem, sizeof( struct key_value_t ) * size );

	return map;
}

int string_map_put( struct string_map *map, char *key, void *value, int8_t is_copy )
{
	if( map->pos == map->size )
		return -1;
	string_map_remove( map, key );
	int i;
	for( i = map->pos - 1; i >= 0; i-- ) {
		if( strmap_cmp( key, &map->data[i] ) < 0 )
			map->data[i + 1] = map->data[i];
		else
			break;
	}
	i++;
	if( is_copy ) {
		map->data[i].key = (char *)mem_pool_malloc( map->mem, strlen( key ) + 1 );
		strcpy( map->data[i].key, key );
	}
	else {
		map->data[i].key = key;
	}
	map->data[i].value = value;
	++map->pos;
	return 0;
}

void *string_map_get( struct string_map *map, char *key )
{
	struct key_value_t *v = (struct key_value_t *)bsearch( key, map->data, map->pos, sizeof( map->data[0] ), &strmap_cmp );

	if( NULL == v )
		return NULL;
	return v->value;
}

void *string_map_remove( struct string_map *map, char *key )
{
	struct key_value_t *v = (struct key_value_t *)bsearch( key, map->data, map->pos, sizeof( map->data[0] ), &strmap_cmp );

	if( NULL == v ) {
		return NULL;
	}

	void *value = v->value;
	memmove( v, v + 1, (map->pos - ( v - map->data ) - 1) * sizeof( *v ) );
	--map->pos;
	return value;
}

int32_t string_map_for_each( struct string_map *map, HASHMAP_FUNC func, void *user_data )
{
	struct low_data_struct low_data, *low_data_p;

	low_data_p = &low_data;
	uint32_t i = 0;
	for( i = 0; i < map->pos; i++ ) {
		low_data.data = map->data[i].key;
		low_data.len = strlen( map->data[i].key );
		int rc = (*func)( &low_data_p, 1, map->data[i].value, user_data );
		if( rc < 0 ) {
			return rc;
		}
	}

	return MILE_RETURN_SUCCESS;
}

