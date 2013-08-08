#include "data_field.h"
#include "../../common/common_util.h"


struct data_field_manager* data_field_init(struct data_field_config* config,MEM_POOL* mem_pool)
{
	struct data_field_manager* data_field = (struct data_field_manager*)mem_pool_malloc(mem_pool,sizeof(struct data_field_manager));
	memset(data_field,0,sizeof(struct data_field_manager));


	data_field->row_limit = config->row_limit;
	strcpy(data_field->work_space,config->work_space);
	
	//初始化不定长filter，用于存储原始数据
	struct filter_index_config filter_config;
	memset(&filter_config,0,sizeof(struct filter_index_config));
	filter_config.row_limit = data_field->row_limit;
	strcpy(filter_config.work_space,data_field->work_space);
	filter_config.type = HI_TYPE_STRING;

	data_field->filter_data = filter_index_init(&filter_config,mem_pool);

	assert(data_field->filter_data);

	return data_field;

}


int32_t data_field_insert(struct data_field_manager* data_field,struct row_data* rdata,uint32_t docid, MEM_POOL* mem_pool)
{
	int32_t ret;

	//调用接口插入
	PROFILER_BEGIN("filter index insert");
	ret = filter_index_insert(data_field->filter_data,rowdata_to_lowdata(rdata,mem_pool),docid);
	PROFILER_END();
	return ret;
}

int32_t data_field_update(struct data_field_manager* data_field,
						struct low_data_struct* new_data,
						struct low_data_struct** old_data,
						uint32_t docid,
						MEM_POOL* mem_pool)
{
	int32_t ret;
	
	PROFILER_BEGIN("data field query row");
	struct row_data* rdata = data_field_query_row(data_field,docid,mem_pool);
	PROFILER_END();

	if(rdata == NULL)
		return ERROR_UPDATE_FAIL;

	uint16_t i;
	for(i=0; i<rdata->field_count; i++)
	{
		if(strlen((rdata->datas+i)->field_name) == strlen(new_data->field_name)  
			&&strncmp((rdata->datas+i)->field_name,new_data->field_name,strlen(new_data->field_name)) == 0)
		{
			*old_data = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
			memset(*old_data,0,sizeof(struct low_data_struct));
			memcpy(*old_data,(rdata->datas+i),sizeof(struct low_data_struct));
			memcpy((rdata->datas+i), new_data,sizeof(struct low_data_struct));
			ret = data_field_insert(data_field,rdata,docid,mem_pool);
			return ret;
		}
	}

	//没找到，说明添加的新列
	struct low_data_struct* datas = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct) * (rdata->field_count+1));
	memset(datas,0,sizeof(struct low_data_struct) * (rdata->field_count+1));

	memcpy(datas,rdata->datas,sizeof(struct low_data_struct) * rdata->field_count);
	memcpy(datas + rdata->field_count, new_data, sizeof(struct low_data_struct));

	rdata->field_count++;
	rdata->datas = datas;
	*old_data = NULL;

	ret = data_field_insert(data_field,rdata,docid,mem_pool);
	
	return ret;
}




//2个字节的列数 | 1个字节的列名长度 | 列名 |  1个字节列类型 | 4个字节数据长度	| 列数据
struct low_data_struct* data_field_query_col(struct data_field_manager* data_field,char* field_name,uint32_t docid,MEM_POOL* mem_pool)
{
	struct low_data_struct* result = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
	memset(result,0,sizeof(struct low_data_struct));

	PROFILER_BEGIN("filter index query");
	struct low_data_struct* rdata = filter_index_query(data_field->filter_data,docid,mem_pool);
	PROFILER_END();
	
	if(rdata == NULL || rdata->len == 0)
	{
		log_warn("整行数据不存在，为空，或长度为0");
		return NULL;
	}

	uint16_t field_num = *(uint16_t*)rdata->data;

	uint16_t i;
	uint8_t fieldname_len = 0;
	uint32_t offset = sizeof(uint16_t);

	for(i=0; i<field_num; i++)
	{
		fieldname_len = *((uint8_t*)rdata->data+offset);
		offset += sizeof(uint8_t);

		//比较字符串，找到此列
		if(strlen(field_name) == fieldname_len && strncmp((char *)rdata->data+offset,field_name,fieldname_len) == 0)
		{
			result->field_name = (char*)mem_pool_malloc(mem_pool,fieldname_len+1);
			memset(result->field_name,0,fieldname_len+1);
			memcpy(result->field_name,(char *)rdata->data+offset,fieldname_len);
			
			offset += fieldname_len;

			result->type = (enum field_types)*((uint8_t*)rdata->data + offset);
			offset += sizeof(uint8_t);

			result->len = *(uint32_t*)((char *)rdata->data + offset);
			offset += sizeof(uint32_t);

			result->data = (char *)rdata->data + offset;

			return result;
		}
		offset += fieldname_len;
		offset += sizeof(uint8_t);
		offset += *(uint32_t*)((char *)rdata->data+offset);
		offset += sizeof(uint32_t);
		
	}

	log_warn("没有找到%s列数据",field_name);

	return NULL;
}


struct row_data* data_field_query_row(struct data_field_manager* data_field,uint32_t docid,MEM_POOL* mem_pool)
{
	PROFILER_BEGIN("filter index query");
	struct low_data_struct* rdata = filter_index_query(data_field->filter_data,docid,mem_pool);
	PROFILER_END();
	
	if(rdata == NULL || rdata->len == 0)
	{
		log_warn("整行数据不存在，为空，或长度为0");
		return NULL;
	}

	return lowdata_to_rowdata(rdata,mem_pool);
}




void data_field_release(struct data_field_manager* data_field)
{
	return filter_index_release(data_field->filter_data);
}



void data_field_checkpoint(struct data_field_manager* data_field)
{
	return filter_index_checkpoint(data_field->filter_data);
}

int data_field_recover(struct data_field_manager *data_field, uint32_t docid)
{
	return filter_index_recover(data_field->filter_data, docid);
}


