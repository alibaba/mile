/*
 * SqlAnalyzer.cpp
 *
 *  Created on: 2012-8-25
 *      Author: yuzhong.zhao
 */

#include "SqlAnalyzer.h"

#include "CumulativeQueryStep.h"
#include "ExportSubstep.h"
#include "../storage/ldb/LdbTableMgr.h"

/**
 *	查询列中是否包含函数
 *	@param  select_field			选择列
 *	@return						如果包含函数，返回1；否则返回0
 */
int8_t is_query_contain_func(struct select_field_array* select_field) {
	int8_t flag = 0;
	int i;
	for (i = 0; i < select_field->n; i += 1) {
		if (select_field->select_fields[i].type == FUNCTION_SELECT) {
			flag = 1;
			break;
		}
	}

	return flag;
}

ExecuteSubstep* SqlAnalyzer::AnalyzeCondition(TableManager* table,
		condition_array* cond, MEM_POOL_PTR mem_pool) {
	FilterSubstep* substep = new (
			mem_pool_malloc(mem_pool, sizeof(FilterSubstep))) FilterSubstep(
			cond, mem_pool);
	table->IdentifyQueryWay(substep->sel_fields, mem_pool);
	return substep;
}


ExecutePlan* SqlAnalyzer::Analyze(StorageEngine* storage,
		struct insert_packet* packet, MEM_POOL_PTR mem_pool) {
	if (packet == NULL) {
		log_error("插入报文为空!");
		return NULL;
	}
	TableManager* table = storage->GetTableManager(packet->table_name,
			mem_pool);
	if (table == NULL) {
		log_error("在db中找不到表%s!", packet->table_name);
		return NULL;
	}

	ExecutePlan* plan =
			new (mem_pool_malloc(mem_pool, sizeof(ExecutePlan))) ExecutePlan(
					table, mem_pool);
	ExecuteStep* step;

	step = new (mem_pool_malloc(mem_pool, sizeof(InsertStep))) InsertStep(
			packet->column_num, packet->datas, mem_pool);
	plan->AddExecuteStep(step);

	return plan;
}

ExecutePlan* SqlAnalyzer::Analyze(StorageEngine* storage,
		struct delete_packet* packet, MEM_POOL_PTR mem_pool) {
	if (packet == NULL) {
		log_error("删除报文为空!");
		return NULL;
	}

	TableManager* table = storage->GetTableManager(packet->table_name,
			mem_pool);
	if (table == NULL) {
		log_error("在db中找不到表%s!", packet->table_name);
		return NULL;
	}
	ExecutePlan* plan =
			new (mem_pool_malloc(mem_pool, sizeof(ExecutePlan))) ExecutePlan(
					table, mem_pool);
	ExecuteStep* step;
	ExecuteSubstep* substep;
	uint32_t* del_num = (uint32_t*) mem_pool_malloc(mem_pool, sizeof(uint32_t));

	step = new (mem_pool_malloc(mem_pool, sizeof(IndexStep))) IndexStep(
			&packet->index_cond, &packet->hi_array);
	plan->AddExecuteStep(step);

	*del_num = 0;
	step = new (mem_pool_malloc(mem_pool, sizeof(MainStep))) MainStep(del_num,
			mem_pool);
	plan->AddExecuteStep(step);

	if (packet->filter_cond.n > 0) {
		substep = AnalyzeCondition(table, &packet->filter_cond, mem_pool);
		((MainStep*) step)->AddSubstep(substep);
	}

	substep =
			new (mem_pool_malloc(mem_pool, sizeof(DeleteSubstep))) DeleteSubstep();
	((MainStep*) step)->AddSubstep(substep);

	return plan;
}

ExecutePlan* SqlAnalyzer::Analyze(StorageEngine* storage,
		struct export_packet *packet, MEM_POOL_PTR mem_pool) {
	if (packet == NULL) {
		log_error("导出报文为空!");
		return NULL;
	}

	TableManager *table = storage->GetTableManager(packet->table_name,
			mem_pool);
	if (!table) {
		log_error("can't find table %s!", packet->table_name);
		return NULL;
	}
	ExportSubstep *export_step = NEW(mem_pool, ExportSubstep)(
			packet->table_name, packet->save_path, packet->limit);
	int rc = export_step->Init(); // create save file
	if (rc) {
		log_error("init export step failed");
		export_step->~ExportSubstep();
		return NULL;
	}

	ExecutePlan *plan = NEW(mem_pool, ExecutePlan)(table, mem_pool);

	ExecuteStep* step = NEW(mem_pool, IndexStep)(&packet->index_cond,
			&packet->hi_array);
	plan->AddExecuteStep(step);

	uint64_t *export_num = NEW(mem_pool, uint64_t)(0);
	MainStep *main_step = NEW(mem_pool, MainStep)(export_num, mem_pool);
	plan->AddExecuteStep(main_step);

	if (packet->filter_cond.n) {
		ExecuteSubstep *substep = AnalyzeCondition(table, &packet->filter_cond, mem_pool);
		main_step->AddSubstep(substep);
	}

	main_step->AddSubstep(export_step);

	return plan;
}

ExecutePlan* SqlAnalyzer::Analyze(StorageEngine* storage,
		struct update_packet* packet, MEM_POOL_PTR mem_pool) {
	if (packet == NULL) {
		log_error("更新报文为空!");
		return NULL;
	}
	TableManager* table = storage->GetTableManager(packet->table_name,
			mem_pool);
	if (table == NULL) {
		log_error("在db中找不到表%s!", packet->table_name);
		return NULL;
	}
	ExecutePlan* plan =
			new (mem_pool_malloc(mem_pool, sizeof(ExecutePlan))) ExecutePlan(
					table, mem_pool);
	ExecuteStep* step;
	ExecuteSubstep* substep;
	uint32_t* up_num = (uint32_t*) mem_pool_malloc(mem_pool, sizeof(uint32_t));

	step = new (mem_pool_malloc(mem_pool, sizeof(IndexStep))) IndexStep(
			&packet->index_cond, &packet->hi_array);
	plan->AddExecuteStep(step);

	*up_num = 0;
	step = new (mem_pool_malloc(mem_pool, sizeof(MainStep))) MainStep(up_num,
			mem_pool);
	plan->AddExecuteStep(step);

	if (packet->filter_cond.n > 0) {
		substep = AnalyzeCondition(table, &packet->filter_cond, mem_pool);
		((MainStep*) step)->AddSubstep(substep);
	}

	substep =
			new (mem_pool_malloc(mem_pool, sizeof(UpdateSubstep))) UpdateSubstep(
					packet->field_name, &packet->data, mem_pool);
	((MainStep*) step)->AddSubstep(substep);
	return plan;
}

ExecutePlan *SqlAnalyzer::Analyze(LdbTableMgr *table,
		struct query_packet *packet, MEM_POOL_PTR mem_pool) {
	ExecutePlan *plan = NEW(mem_pool, ExecutePlan)(table, mem_pool);
	plan->AddExecuteStep(
			NEW(mem_pool, CumulativeQueryStep)(&packet->select_field,
					&packet->index_cond));
	return plan;
}

ExecutePlan* SqlAnalyzer::Analyze(StorageEngine* storage,
		struct query_packet* packet, MEM_POOL_PTR mem_pool) {
	uint32_t i, j;
	if (packet == NULL) {
		log_error("查询报文为空!");
		return NULL;
	}

	TableManager* table = storage->GetTableManager(packet->table_name,
			mem_pool);
	if (table == NULL) {
		log_error("在db中找不到表%s!", packet->table_name);
		return NULL;
	}

	LdbTableMgr *ldb_table = dynamic_cast<LdbTableMgr *>(table);
	if (ldb_table && ldb_table->HasCumulativeIndex()) {
		return Analyze(ldb_table, packet, mem_pool);
	}

	ExecutePlan* plan =
			new (mem_pool_malloc(mem_pool, sizeof(ExecutePlan))) ExecutePlan(
					table, mem_pool);
	ExecuteStep* step;
	ExecuteSubstep* substep;
	ResultSet* result;
	struct select_fields_t* sel_fields = init_select_fields_t(mem_pool,
			packet->select_field.n);

	//对全表的count(*)需要特殊处理
	if (packet->select_field.n == 1 && packet->index_cond.n == 0
			&& packet->filter_cond.n == 0
			&& packet->select_field.select_fields[0].func_type == FUNC_COUNT) {
		step =
				new (mem_pool_malloc(mem_pool, sizeof(CountAllStep))) CountAllStep(
						packet->select_field.select_fields[0].select_name);
		plan->AddExecuteStep(step);
		return plan;
	}

	//hash条件过滤
	step = new (mem_pool_malloc(mem_pool, sizeof(IndexStep))) IndexStep(
			&packet->index_cond, &packet->hi_array);
	plan->AddExecuteStep(step);

	Clonable* clone =
			new (mem_pool_malloc(mem_pool, sizeof(RowClone))) RowClone(
					mem_pool);
	for (i = 0; i < packet->select_field.n; i++) {
		sel_fields->fields_name[i] =
				packet->select_field.select_fields[i].field_name;
		sel_fields->select_type[i] = SELECT_TYPE_ORIGINAL;
	}

	if (packet->group_array.n > 0
			|| is_query_contain_func(&packet->select_field)) {
		//需要计算函数列的情况
		result = new (mem_pool_malloc(mem_pool, sizeof(GroupRS))) GroupRS(
				&packet->select_field, &packet->group_array, mem_pool);
		sel_fields = ((GroupRS*) result)->src_fields;
	} else if (packet->order_array.n > 0) {
		//需要排序的情况
		Comparator* comp =
				new (mem_pool_malloc(mem_pool, sizeof(OrderComparator))) OrderComparator(
						&packet->select_field, &packet->order_array, mem_pool);

		//对于非排序列可以延后取值
		if (packet->access_type == ACCESS_TYPE_COMMON) {
			//一般的排序
			result =
					new (mem_pool_malloc(mem_pool, sizeof(OrderedRS))) OrderedRS(
							comp, clone, packet->limit, mem_pool);
			for (i = 0; i < sel_fields->n; i++) {
				for (j = 0; j < packet->order_array.n; j++) {
					if (strcmp(sel_fields->fields_name[i],
							packet->order_array.order_fields[j].field_name)
							== 0) {
						break;
					}
				}

				if (j == packet->order_array.n) {
					sel_fields->select_type[i] = SELECT_TYPE_DELAY;
				}
			}

		} else {
			//带distinct的排序
			Equals* equals =
					new (mem_pool_malloc(mem_pool, sizeof(RowEquals))) RowEquals();
			HashCoding* hash =
					new (mem_pool_malloc(mem_pool, sizeof(RowHash))) RowHash();
			result =
					new (mem_pool_malloc(mem_pool, sizeof(DistOrdRS))) DistOrdRS(
							equals, hash, comp, clone, packet->limit, mem_pool);

			//对于非排序列可以取其hash值
			for (i = 0; i < sel_fields->n; i++) {
				for (j = 0; j < packet->order_array.n; j++) {
					if (strcmp(sel_fields->fields_name[i],
							packet->order_array.order_fields[j].field_name)
							== 0) {
						break;
					}
				}

				if (j == packet->order_array.n) {
					sel_fields->select_type[i] = SELECT_TYPE_HASH;
				}
			}
		}
	} else {
		//一般的查询
		if (packet->access_type == ACCESS_TYPE_COMMON) {
			result = new (mem_pool_malloc(mem_pool, sizeof(CommonRS))) CommonRS(
					packet->limit, clone, mem_pool);
		} else {
			//带distinct的查询
			Equals* equals =
					new (mem_pool_malloc(mem_pool, sizeof(RowEquals))) RowEquals();
			HashCoding* hash =
					new (mem_pool_malloc(mem_pool, sizeof(RowHash))) RowHash();
			result =
					new (mem_pool_malloc(mem_pool, sizeof(DistinctRS))) DistinctRS(
							equals, hash, clone, packet->limit, mem_pool);
			for (i = 0; i < sel_fields->n; i++) {
				sel_fields->select_type[i] = SELECT_TYPE_HASH;
			}
		}
	}

	//主步骤
	step = new (mem_pool_malloc(mem_pool, sizeof(MainStep))) MainStep(result,
			mem_pool);
	plan->AddExecuteStep(step);

	//where条件过滤子步骤
	if (packet->filter_cond.n > 0) {
		substep = AnalyzeCondition(table, &packet->filter_cond, mem_pool);
		((MainStep*) step)->AddSubstep(substep);
	}

	//数据选择子步骤
	substep =
			new (mem_pool_malloc(mem_pool, sizeof(SelectSubstep))) SelectSubstep(
					sel_fields);
	table->IdentifyQueryWay(sel_fields, mem_pool);
	((MainStep*) step)->AddSubstep(substep);

	//数据再填充步骤
	if (packet->access_type != ACCESS_TYPE_DISTINCT_COUNT) {
		step = new (mem_pool_malloc(mem_pool, sizeof(RefillStep))) RefillStep(
				&packet->select_field, mem_pool);
		plan->AddExecuteStep(step);
	}

	return plan;
}

