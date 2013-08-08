/*
 * =====================================================================================
 *
 *       Filename:  hi_common_util.c
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created: 	
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yuzhong.zhao
 *        Company:  alipay.com
 *
 * =====================================================================================
 */


#include "common_util.h"


#define COMP(a, b)	((int8_t)(a>b)-(int8_t)(a<b))



/**
 * 	比较两个元素a和b的大小
 *	@param 	a			
 *	@param 	b
 *	@param 	field_type	元素的数据类型
 *	@return				当a>b时，返回1；a<b时，返回-1；a=b时，返回0；如果遇到不支持的数据类型，返回-2
 */
int8_t compare(const void* a, const void* b, enum field_types field_type)
{
	if(a == NULL || b == NULL)
	{
		return -2;
	}
	switch(field_type)
	{
		case HI_TYPE_TINY:
			return COMP(*(int8_t *)a, *(int8_t *)b);
		case HI_TYPE_SHORT:
			return COMP(*((int16_t*)a), *((int16_t *)b));
		case HI_TYPE_UNSIGNED_SHORT:
			return COMP(*((uint16_t *)a), *((uint16_t *)b));
		case HI_TYPE_LONG:
			return COMP(*((int32_t *)a), *((int32_t *)b));
		case HI_TYPE_UNSIGNED_LONG:
			return COMP((*(uint32_t *)a), (*(uint32_t *)b));	
		case HI_TYPE_FLOAT:
			if(fabs(*(float*)a - *(float*)b) < 1e-10)
			{
				return 0;
			}else{
				return COMP(*((float *)a), *((float *)b));
			}
		case HI_TYPE_DOUBLE:
			if(fabs(*(double*)a - *(double*)b) < 1e-10)
			{
				return 0;
			}else{
				return COMP(*((double *)a), *((double *)b));
			}
		case HI_TYPE_LONGLONG:
			return COMP(*((int64_t *)a), *((int64_t *)b));
		case HI_TYPE_UNSIGNED_LONGLONG:
			return COMP(*((uint64_t *)a), *((uint64_t *)b));
		default:
			log_error("不支持对列类型 %d 进行比较!", field_type);
			return -2;
	}
	
}





//比较两个字符串
int8_t compare_string(const struct low_data_struct* a, const struct low_data_struct* b)
{
	uint32_t i;
	int8_t *p, *q;


	for(i = 0, p = (int8_t*)a->data, q = (int8_t*)b->data; i < a->len && i < b->len; i++, p++, q++)
	{
		if(*p < *q)
		{
			return -1;
		}
		if(*p > *q)
		{
			return 1;
		}
	}

	if(a->len == b->len)
	{
		return 0;
	}else if(a->len < b->len){
		return -1;
	}else{
		return 1;
	}
	
}





/**
 * 	比较两个元素a和b的大小
 *	@param 	a
 *	@param 	b
 *	@param 	field_type	元素的数据类型
 *	@return				当a>b时，返回1；a<b时，返回-1；a=b时，返回0；如果遇到不支持的数据类型，返回-2
 */
int8_t compare_ld(const struct low_data_struct* a, const struct low_data_struct* b)
{
	if(a == NULL || b == NULL)
	{
		return -2;
	}

	enum field_types field_type = a->type;

#define LOW_DATA_IS_NULL(data_ptr) ((HI_TYPE_NULL == (data_ptr)->type) || (0 == (data_ptr)->len))
	if(LOW_DATA_IS_NULL(a) && LOW_DATA_IS_NULL(b))
		return 0;
	else if(LOW_DATA_IS_NULL(a))
		return -1;
	else if(LOW_DATA_IS_NULL(b))
		return 1;
#undef LOW_DATA_IS_NULL

	if(a->type != b->type)
		return -2;

	switch(field_type)
	{
		case HI_TYPE_TINY:
			return COMP(*(int8_t *)a->data, *(int8_t *)b->data);
		case HI_TYPE_SHORT:
			return COMP(*((int16_t*)a->data), *((int16_t *)b->data));
		case HI_TYPE_UNSIGNED_SHORT:
			return COMP(*((uint16_t *)a->data), *((uint16_t *)b->data));
		case HI_TYPE_LONG:
			return COMP(*((int32_t *)a->data), *((int32_t *)b->data));
		case HI_TYPE_UNSIGNED_LONG:
			return COMP((*(uint32_t *)a->data), (*(uint32_t *)b->data));	
		case HI_TYPE_FLOAT:
			if(fabs(*(float*)a->data - *(float*)b->data) < 1e-10)
			{
				return 0;
			}else{
				return COMP(*((float *)a->data), *((float *)b->data));
			}
		case HI_TYPE_DOUBLE:
			if(fabs(*(double*)a->data - *(double*)b->data) < 1e-10)
			{
				return 0;
			}else{
				return COMP(*((double *)a->data), *((double *)b->data));
			}
		case HI_TYPE_LONGLONG:
			return COMP(*((int64_t *)a->data), *((int64_t *)b->data));
		case HI_TYPE_UNSIGNED_LONGLONG:
			return COMP(*((uint64_t *)a->data), *((uint64_t *)b->data));
		case HI_TYPE_VARCHAR:
		case HI_TYPE_STRING:
			return compare_string(a, b);
		default:
			log_error("不支持对列类型 %d 进行比较!", field_type);
			return -2;
	}

}





int32_t is_ld_equal(struct low_data_struct* a, struct low_data_struct* b){
	uint32_t i;


	if(a == NULL && b == NULL){
		return 1;
	}
	if(a == NULL || b == NULL)
	{
		return 0;
	}

	uint8_t* da = (uint8_t*)a->data;
	uint8_t* db = (uint8_t*)b->data;

	if(a->type != b->type){
		return 0;
	}
	if(a->len != b->len){
		return 0;
	}

	for(i = 0; i < a->len; i++){
		if(da[i] != db[i]){
			return 0;
		}
	}

	return 1;
}




/**
 * 	将一个low data中的数据拷贝到另一个low data中
 *	@param 	pMemPool	内存池
 *	@param 	des			目标数据
 *	@param	src			源数据
 *	@return				
 */
void copy_low_data_struct(MEM_POOL_PTR pMemPool, struct low_data_struct* des, struct low_data_struct* src)
{
	if(des->len < src->len)
	{
		des->data = mem_pool_malloc(pMemPool, src->len);
	}
	int fname_len = 0;

	if(src->field_name != NULL)
	{
		fname_len = strlen(src->field_name);
		des->field_name = (char*) mem_pool_malloc(pMemPool, fname_len + 1);
		strcpy(des->field_name, src->field_name);
		des->field_name[fname_len] = '\0';
	}else{
		des->field_name = NULL;
	}
	
	des->type = src->type;
	des->len = src->len;
	memcpy(des->data, src->data, src->len);
}










/**
 *	交换两个指针指向的内容
 *	@param a
 *	@param b
 *  @param len			指向数据的字节长度
 */
inline void swap(void* a, void* b, uint32_t len)
{
	uint32_t i;
	uint8_t* p = (uint8_t*)a;
	uint8_t* q = (uint8_t*)b; 
	uint8_t tmp;

	for(i = 0; i < len; i++)
	{
		tmp = *p;
		*p = *q;
		*q = tmp;
		p++;
		q++;
	}
}








int32_t add_data(struct low_data_struct* a, struct low_data_struct* b)
{
	if(a->type != b->type){
		return -1;
	}
	if(b->type == HI_TYPE_NULL){
		return 0;
	}
	if(a->type == HI_TYPE_NULL){
		a->type = b->type;
		a->data = b->data;
		a->field_name = b->field_name;
		a->len = b->len;
		return 0;
	}
	
	switch(a->type)
	{
		case HI_TYPE_TINY:
			*(int8_t *)a->data = *(int8_t *)a->data + *(int8_t *)b->data;
			return 0;
		case HI_TYPE_SHORT:
			*(int16_t *)a->data = *(int16_t *)a->data + *(int16_t *)b->data;
			return 0;
		case HI_TYPE_UNSIGNED_SHORT:
			*(int16_t *)a->data = *(int16_t *)a->data + *(int16_t *)b->data;
			return 0;
		case HI_TYPE_LONG:
			*(int32_t *)a->data = *(int32_t *)a->data + *(int32_t *)b->data;
			return 0;
		case HI_TYPE_UNSIGNED_LONG:
			*(int32_t *)a->data = *(int32_t *)a->data + *(int32_t *)b->data;
			return 0;	
		case HI_TYPE_FLOAT:
			*(float *)a->data = *(float *)a->data + *(float *)b->data;
			return 0;
		case HI_TYPE_DOUBLE:
			*(double *)a->data = *(double *)a->data + *(double *)b->data;
			return 0;
		case HI_TYPE_LONGLONG:
			*(int64_t *)a->data = *(int64_t *)a->data + *(int64_t *)b->data;
			return 0;
		case HI_TYPE_UNSIGNED_LONGLONG:
			*(uint64_t *)a->data = *(uint64_t *)a->data + *(uint64_t *)b->data;
			return 0;
		default:
			log_error("不支持对列类型 %d 进行求和!", a->type);
			return -1;
	}
	return -1;
}






double ld_to_double(struct low_data_struct* ld){
	double ret = 0;

	switch(ld->type)
	{
		case HI_TYPE_TINY:
			ret = *(int8_t *)ld->data;
			break;
		case HI_TYPE_SHORT:
			ret = *(int16_t *)ld->data;
			break;
		case HI_TYPE_UNSIGNED_SHORT:
			ret = *(uint16_t *)ld->data;
			break;
		case HI_TYPE_LONG:
			ret = *(int32_t *)ld->data;
			break;
		case HI_TYPE_UNSIGNED_LONG:
			ret = *(uint32_t *)ld->data;
			break;
		case HI_TYPE_FLOAT:
			ret = *(float *)ld->data;
			break;
		case HI_TYPE_DOUBLE:
			ret = *(double *)ld->data;
			break;
		case HI_TYPE_LONGLONG:
			ret = *(int64_t *)ld->data;
			break;
		case HI_TYPE_UNSIGNED_LONGLONG:
			ret = *(uint64_t *)ld->data;
			break;
		default:
			log_error("不支持对列类型 %d 进行double转换!", ld->type);
			return 0.0/0.0;
	}
	return ret;
}







struct select_row_t* init_select_row_t(MEM_POOL_PTR mem_pool, uint32_t n){
	struct select_row_t* row = (struct select_row_t*) mem_pool_malloc(mem_pool, sizeof(struct select_row_t));
	memset(row, 0, sizeof(struct select_row_t));

	row->n = n;
	row->data = (struct low_data_struct*) mem_pool_malloc(mem_pool, sizeof(struct low_data_struct) * n);
	memset(row->data, 0, sizeof(struct low_data_struct) * n);
	row->select_type = (enum select_types_t*) mem_pool_malloc(mem_pool, sizeof(enum select_types_t) * n);
	memset(row->select_type, 0, sizeof(enum select_types_t) * n);

	return row;
}


struct select_fields_t* init_select_fields_t(MEM_POOL_PTR mem_pool, uint32_t n){
	struct select_fields_t* fields = (struct select_fields_t*) mem_pool_malloc(mem_pool, sizeof(struct select_fields_t));
	memset(fields, 0, sizeof(struct select_fields_t));

	fields->n = n;
	fields->fields_name = (char**) mem_pool_malloc(mem_pool, sizeof(char*) * n);
	memset(fields->fields_name, 0, sizeof(char*) * n);
	fields->select_type = (enum select_types_t*) mem_pool_malloc(mem_pool, sizeof(enum select_types_t) * n);
	memset(fields->select_type, 0, sizeof(enum select_types_t) * n);
	fields->access_way = NULL;

	return fields;
}










struct str_array_t *all_str_append(const struct str_array_t *src, MEM_POOL_PTR mem, const char *fmt, ...)
{
	if (NULL == src)
		return NULL;

	va_list ap;

	// get append string length
	va_start(ap, fmt);
	int append_len = vsnprintf(NULL, 0, fmt, ap);
	va_end(ap);
	if (append_len < 0) {
		log_error("vsnprintf failed");
		return NULL;
	}

	struct str_array_t *sa = (struct str_array_t *)mem_pool_malloc(mem, sizeof(struct str_array_t));
	sa->n = src->n;
	sa->strs = (char **)mem_pool_malloc(mem, sizeof(char *) * sa->n);
	char *append_buf = NULL;

	for (size_t i = 0; i < src->n; i++) {
		int src_len = strlen(src->strs[i]);
		sa->strs[i] = (char *)mem_pool_malloc(mem, src_len + append_len + 1);
		strcpy(sa->strs[i], src->strs[i]);

		if (0 == append_len)
			continue;

		if (NULL == append_buf) {
			append_buf = sa->strs[i] + src_len;
			va_start(ap, fmt);
			int n = vsnprintf(append_buf, append_len + 1, fmt, ap);
			va_end(ap);
			if (n < 0) {
				log_error("vsnprintf failed");
				return NULL;
			}
			assert(n == append_len);
		} else {
			strcpy(sa->strs[i] + src_len, append_buf);
		}
	}

	return sa;
}

struct low_data_struct*  rowdata_to_lowdata(struct row_data* rdata,MEM_POOL* mem_pool)
{
	//计算总的长度
	uint16_t i;
	uint32_t len = 0;
	struct low_data_struct* result = (struct low_data_struct*)mem_pool_malloc(mem_pool,sizeof(struct low_data_struct));
	
	if(result == NULL )
		return NULL;
	memset(result,0,sizeof(struct low_data_struct));

	//1个字节列数
	len += sizeof(uint16_t);
	for(i = 0; i<rdata->field_count;i++)
	{
		//1个字节的列数据类型
		len += sizeof(uint8_t);
		//1个字节的列名长度
		len += sizeof(uint8_t);
		//列名
		len += strlen(rdata->datas[i].field_name);
		//4个字节的数据长度
		len += sizeof(uint32_t);
		len += rdata->datas[i].len;
	}

	void* buf = mem_pool_malloc(mem_pool,len);
	if(buf ==  NULL)
	{
		return NULL;
	}
	memset(buf,0,sizeof(buf));

	uint32_t offset = 0;
	uint8_t fieldname_len = 0;
	memcpy((char *)buf+offset,&rdata->field_count,sizeof(uint16_t));
	offset += sizeof(uint16_t);
	
	for(i = 0; i<rdata->field_count;i++)
	{
		//列名
		fieldname_len = strlen(rdata->datas[i].field_name);
		memcpy((char *)buf+offset,&fieldname_len,sizeof(uint8_t));
		offset += sizeof(uint8_t);
		memcpy((char *)buf+offset,rdata->datas[i].field_name,fieldname_len);
		offset += fieldname_len;

		//列类型
		memcpy((char *)buf+offset,&rdata->datas[i].type,sizeof(uint8_t));
		offset += sizeof(uint8_t);

		//列数据
		memcpy((char *)buf+offset,&rdata->datas[i].len,sizeof(uint32_t));
		offset += sizeof(uint32_t);
		memcpy((char *)buf+offset,rdata->datas[i].data,rdata->datas[i].len);
		offset += rdata->datas[i].len;
	}

	result->data = buf;
	result->len = len;

	return result;
}

struct row_data*  lowdata_to_rowdata(struct low_data_struct* rdata,MEM_POOL* mem_pool)
{
	struct row_data* result = (struct row_data*)mem_pool_malloc(mem_pool,sizeof(struct row_data));
	memset(result,0,sizeof(struct row_data));

	result->field_count = *(uint16_t*)rdata->data;
	result->datas = (struct low_data_struct*)mem_pool_malloc(mem_pool,result->field_count*sizeof(struct low_data_struct));

	uint16_t i;
	uint8_t fieldname_len = 0;
	uint32_t offset = sizeof(uint16_t);

	for(i=0; i < result->field_count; i++)
	{
		fieldname_len = *((uint8_t*)rdata->data+offset);
		offset += sizeof(uint8_t);
		
		//留一个字节给\0
		result->datas[i].field_name = (char*)mem_pool_malloc(mem_pool,fieldname_len+1);
		memset(result->datas[i].field_name,0,fieldname_len+1);
		strncpy(result->datas[i].field_name,(char *)rdata->data+offset,fieldname_len);
		offset += fieldname_len;
		
		result->datas[i].type = (enum field_types)*((uint8_t*)rdata->data + offset);
		offset += sizeof(uint8_t);

		result->datas[i].len = *(uint32_t*)((char *)rdata->data + offset);
		offset += sizeof(uint32_t);

		result->datas[i].data = (char *)rdata->data + offset;
		offset += result->datas[i].len;
	}

	return result;
}

