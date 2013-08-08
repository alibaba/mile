/*
 * StateManager.cpp
 *
 *  Created on: 2012-10-30
 *      Author: yuzhong.zhao
 */

#include "StateManager.h"



int StateManager::QueryState(StorageEngine* engine, const char* name, stat_info* info, MEM_POOL_PTR mem_pool){
	info->name_len = strlen( name );
	info->name = (uint8_t *)mem_pool_malloc(mem_pool, info->name_len );
	strncpy( (char *)info->name, (const char *)name, info->name_len );

	info->ldata = (struct low_data_struct *)mem_pool_malloc( mem_pool, sizeof( struct low_data_struct ) );

	int rc = MILE_RETURN_SUCCESS;
	if( strcasecmp( STATE_NAME_READABLE, name ) == 0 ) {
		info->ldata->type = HI_TYPE_TINY;
		info->ldata->len = 1;
		info->ldata->data = mem_pool_malloc(mem_pool, 1 );
		if(engine->GetReadable()){
			*(uint8_t *)info->ldata->data = 1;
		}else{
			*(uint8_t *)info->ldata->data = 0;
		}
	}
	else {
		log_error( "undefined state name [%s]", name );
		return -1;
	}
	return rc;
}


struct stat_info_array* StateManager::QueryStates(StorageEngine* engine,
		struct get_state_packet *packet, MEM_POOL_PTR mem_pool){
	struct stat_info_array *info_array = (struct stat_info_array *)mem_pool_malloc(mem_pool, sizeof( struct stat_info_array) );

	info_array->n = packet->n;
	info_array->stat = (struct stat_info *)mem_pool_malloc(mem_pool, sizeof( struct stat_info ) * info_array->n );
	memset( info_array->stat, 0, sizeof( struct stat_info ) * info_array->n );

	int i = 0;
	for( i = 0; i < packet->n; i++ ) {
		if( QueryState(engine, (const char *)packet->state_names[i], &info_array->stat[i], mem_pool ) != MILE_RETURN_SUCCESS ) {
			log_error( "query state by name [%s] failed.", packet->state_names[i] );
			return NULL;
		}
	}

	return info_array;
}

