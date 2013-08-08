/*
 * StateManager.h
 *
 *  Created on: 2012-10-30
 *      Author: yuzhong.zhao
 */

#ifndef STATEMANAGER_H_
#define STATEMANAGER_H_

#include "../protocol/packet.h"
#include "StorageEngine.h"

#define STATE_NAME_READABLE "readable"

class StateManager {
private:
	static int QueryState(StorageEngine* engine, const char *name, struct stat_info *info, MEM_POOL_PTR mem_pool);
public:
	static struct stat_info_array* QueryStates(StorageEngine* engine,
			struct get_state_packet *packet, MEM_POOL_PTR mem_pool);
};

#endif /* STATEMANAGER_H_ */
