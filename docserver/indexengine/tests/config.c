#include "config.h"

struct hint_array * get_time_hint(MEM_POOL* mem_pool)
{
	struct hint_array* time_hint = (struct hint_array*)mem_pool_malloc(mem_pool,sizeof(struct hint_array));
	memset(time_hint,0,sizeof(time_hint));

	time_hint->n = 2;

	time_hint->hints = (uint64*)mem_pool_malloc(mem_pool,2*sizeof(uint64));
	time_hint->hints[0] = 0;
	time_hint->hints[1] = 0xFFFFFFFFFFFFFFFF;
	
	return time_hint;
}


int32 verify_low_data(struct low_data_struct* data,enum field_types type)
{
	int32 ret = 1;
	if(data == NULL)
		return 0;
	switch(type)
	{
		case HI_TYPE_STRING:
			if(data->len != 5)
				ret = 0;
			if(strcmp((char*)data->data,"ali") || 
			   data->type != HI_TYPE_STRING || 
			   strcmp(data->field_name,"HI_TYPE_STRING"))
				ret = 0;
			break;
		case HI_TYPE_LONG:
			if(data->len != get_unit_size(HI_TYPE_LONG))
				ret = 0;
			if(*(int32*)(data->data) != 6666 || 
			   data->type != HI_TYPE_LONG || 
			   strcmp(data->field_name,"HI_TYPE_LONG"))
				ret = 0;
			break;
		case HI_TYPE_LONGLONG:
			if(data->len != get_unit_size(HI_TYPE_LONGLONG)|| 
			   data->type != HI_TYPE_LONGLONG || 
			   strcmp(data->field_name,"HI_TYPE_LONGLONG"))
				ret = 0;
			if(*(int32*)(data->data) != 8888)
				ret = 0;
			break;
		case HI_TYPE_TINY:
			if(data->len != get_unit_size(HI_TYPE_TINY)|| 
			   data->type != HI_TYPE_TINY || 
			   strcmp(data->field_name,"HI_TYPE_TINY"))
				ret = 0;
			if(*(int8*)(data->data) != 1)
				ret = 0;
			break;
		case HI_TYPE_DOUBLE:
			if(data->len != get_unit_size(HI_TYPE_DOUBLE)|| 
			   data->type != HI_TYPE_DOUBLE || 
			   strcmp(data->field_name,"HI_TYPE_DOUBLE"))
				ret = 0;
			if(*(double*)(data->data) != 2.0)
				ret = 0;
			break;
		default:
			ret = 0;
			break;

	}
	
	return ret;
}


int32 verify_row_data(struct row_data* rdata,uint16 field_count,enum field_types* types)
{
	uint16 i;
	int32 ret = 1;
	
	if(rdata == NULL)
		return 0;
	
	for(i=0;i<field_count;i++)
	{
		if((ret=verify_low_data(rdata->datas+i,types[i])) == 0)
		{
			ret = 0;
			break;
		}
	}

	return ret;
}

void get_low_data2(struct low_data_struct* data, int value, enum field_types type, MEM_POOL* mem_pool)
{
	switch(type)
	{
		case HI_TYPE_STRING:
			data->len = 5;
			data->data = mem_pool_malloc(mem_pool,5);
			data->type = HI_TYPE_STRING;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_STRING");
			memset(data->data,0,5);
			strcpy((char*)data->data,"ali");
		break;
		case HI_TYPE_LONG:
			data->len = get_unit_size(HI_TYPE_LONG);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_LONG;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_LONG");
			*(int32*)(data->data) = value;
			break;
		case HI_TYPE_LONGLONG:
			data->len = get_unit_size(HI_TYPE_LONGLONG);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_LONGLONG;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_LONGLONG");
			*(long long*)(data->data) = (long long)value;
			break;
		case HI_TYPE_TINY:
			data->len = get_unit_size(HI_TYPE_TINY);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_TINY;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_TINY");
			*(int8*)(data->data) = (short)value;
			break;
		case HI_TYPE_DOUBLE:
			data->len = get_unit_size(HI_TYPE_DOUBLE);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_DOUBLE;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_DOUBLE");
			*(double*)(data->data) = 2.0;
			break;
		default:
			break;
	}

	return;
}


void get_low_data(struct low_data_struct* data,enum field_types type,MEM_POOL* mem_pool)
{
	switch(type)
	{
		case HI_TYPE_STRING:
			data->len = 5;
			data->data = mem_pool_malloc(mem_pool,5);
			data->type = HI_TYPE_STRING;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_STRING");
			memset(data->data,0,5);
			strcpy((char*)data->data,"ali");
		break;
		case HI_TYPE_LONG:
			data->len = get_unit_size(HI_TYPE_LONG);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_LONG;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_LONG");
			*(int32*)(data->data) = 6666;
			break;
		case HI_TYPE_LONGLONG:
			data->len = get_unit_size(HI_TYPE_LONGLONG);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_LONGLONG;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_LONGLONG");
			*(int64*)(data->data) = 8888;
			break;
		case HI_TYPE_TINY:
			data->len = get_unit_size(HI_TYPE_TINY);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_TINY;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_TINY");
			*(int8*)(data->data) = 1;
			break;
		case HI_TYPE_DOUBLE:
			data->len = get_unit_size(HI_TYPE_DOUBLE);
			data->data = mem_pool_malloc(mem_pool,data->len);
			data->type = HI_TYPE_DOUBLE;
			data->field_name = (char*)mem_pool_malloc(mem_pool,20);
			memset(data->field_name,0,20);
			strcpy(data->field_name,"HI_TYPE_DOUBLE");
			*(double*)(data->data) = 2.0;
			break;
		default:
			break;
	}

	return;
}

struct row_data* get_row_data(uint16 field_count,enum field_types* types,MEM_POOL* mem_pool)
{
	struct row_data*  rdata = (struct row_data*)mem_pool_malloc(mem_pool,sizeof(struct row_data));
	memset(rdata,0,sizeof(struct row_data));
	rdata->field_count = field_count;

	rdata->datas = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct) * field_count);
	memset(rdata->datas, 0, sizeof(struct low_data_struct) * field_count);

	uint16 i;
	struct low_data_struct* data = rdata->datas;
	for(i=0; i<field_count; i++,data++)
	{
		get_low_data(data,types[i],mem_pool);
	}
	return rdata;
}




