/*
 * SqlAnalyzer.h
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#ifndef SQLANALYZER_H_
#define SQLANALYZER_H_

#include "../protocol/packet.h"
#include "../common/common_util.h"
#include "ExecutePlan.h"
#include "../storage/StorageEngine.h"
#include "InsertStep.h"
#include "MainStep.h"
#include "CountAllStep.h"
#include "RefillStep.h"
#include "IndexStep.h"
#include "GroupRS.h"
#include "CommonRS.h"
#include "OrderedRS.h"
#include "DistinctRS.h"
#include "DistOrdRS.h"
#include "FilterSubstep.h"
#include "DeleteSubstep.h"
#include "UpdateSubstep.h"
#include "SelectSubstep.h"
#include "OrderComparator.h"
#include "RowClone.h"
#include "RowEquals.h"
#include "RowHash.h"

class LdbTableMgr;

class SqlAnalyzer {
private:
	static ExecuteSubstep* AnalyzeCondition(TableManager* table, struct condition_array* cond, MEM_POOL_PTR mem_pool);
public:
	static ExecutePlan* Analyze(StorageEngine* storage, struct insert_packet* packet, MEM_POOL_PTR mem_pool);
	static ExecutePlan* Analyze(StorageEngine* storage, struct delete_packet* packet, MEM_POOL_PTR mem_pool);
	static ExecutePlan *Analyze(StorageEngine *storage, struct export_packet *packet, MEM_POOL_PTR mem_pool);
	static ExecutePlan* Analyze(StorageEngine* storage, struct update_packet* packet, MEM_POOL_PTR mem_pool);
	static ExecutePlan* Analyze(StorageEngine* storage, struct query_packet* packet, MEM_POOL_PTR mem_pool);
	static ExecutePlan *Analyze(LdbTableMgr *table, struct query_packet *packet, MEM_POOL_PTR mem_pool);
};

#endif /* SQLANALYZER_H_ */
