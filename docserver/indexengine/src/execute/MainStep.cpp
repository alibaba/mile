/*
 * MainStep.cpp
 *
 *  Created on: 2012-8-6
 *      Author: yuzhong.zhao
 */

#include "MainStep.h"

MainStep::MainStep(void* output, MEM_POOL_PTR mem_pool) {
	substeps = new (mem_pool_malloc(mem_pool, sizeof(MileList))) MileList(
			mem_pool);
	result = output;
}

MainStep::~MainStep() {
	MileIterator* step_iter = substeps->CreateIterator();
	ExecuteSubstep* substep;
	for(step_iter->First(); !step_iter->IsDone(); step_iter->Next()){
		substep = (ExecuteSubstep*) step_iter->CurrentItem();
		substep->~ExecuteSubstep();
	}
}

void MainStep::AddSubstep(ExecuteSubstep* substep) {
	substeps->Add(substep);
}



void* MainStep::Execute(TableManager* table, void* input, int32_t &result_code,
		int64_t timeout, MEM_POOL_PTR mem_pool) {
	MileIterator* iter = (MileIterator*) input;
	MileIterator* step_iter = substeps->CreateIterator();
	uint32_t i = 0;
	uint32_t cut_threshold=table->getCutThreshold();

	ExecuteSubstep* substep;
	MEM_POOL_PTR inner_mem_pool = mem_pool_init(MB_SIZE);

	for (iter->First(); !iter->IsDone(); iter->Next()) {
		for (step_iter->First(); !step_iter->IsDone(); step_iter->Next()) {
			substep = (ExecuteSubstep*) step_iter->CurrentItem();
			result_code = substep->Execute(table,
					(MileHandler*) iter->CurrentItem(), result, inner_mem_pool);
			if (result_code != 0) {
				break;
			}
		}
		mem_pool_reset(inner_mem_pool);
		if (result_code < 0) {
			break;
		}

		if((++i) % 100 == 0){
			if(get_time_msec() > timeout){
				result_code = ERROR_TIMEOUT;
				break;
			}
		}
		if( cut_threshold > 0 && i >= cut_threshold )
		{
			break;
		}
	}

	mem_pool_destroy(inner_mem_pool);

	if (result_code == WARN_EXCEED_QUERY_LIMIT || result_code >= 0) {
		result_code = MILE_RETURN_SUCCESS;
	}
	return result;
}
