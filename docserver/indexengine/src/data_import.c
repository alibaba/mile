#include <signal.h>
#include <unistd.h>
#include <getopt.h>
#include <string.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include "common/def.h"
#include "storage/docdb/db.h"
#include "storage/docdb/config_parser.h"
#include <iconv.h>




void print_usage(char *prog_name)
{
   fprintf(stdout, "%s db_config data_import_config data_file workspace\n"
		   ,prog_name);
}



#define BUF_SIZE 10*KB_SIZE


int32_t parse_data_insert(char* buf,struct data_import_conf* dimport_conf,MEM_POOL* mem_pool)
{
	uint16_t i;
	
	//为处理不同数据类型时的临时变量
	uint8_t tmp_int8;
	uint16_t tmp_int16;
	uint32_t tmp_int32;
	uint64_t tmp_int64;
	long double tmp_double;
	float  tmp_float;
	
	struct row_data* rdata = (struct row_data*)mem_pool_malloc(mem_pool,sizeof(struct row_data));
	memset(rdata,0,sizeof(struct row_data));

	rdata->field_count = dimport_conf->field_count;
	rdata->datas = (struct low_data_struct*)mem_pool_malloc(mem_pool,dimport_conf->field_count * sizeof(struct low_data_struct));
	memset(rdata->datas,0,dimport_conf->field_count * sizeof(struct low_data_struct));
	
	char *token = NULL;
	struct low_data_struct* ldata = rdata->datas;
	for(i=0; i<dimport_conf->field_count; i++,ldata++)
	{
		 token = strsep(&buf,dimport_conf->split);
		 
		 ldata->field_name = (dimport_conf->fields_info+i)->field_name;
		 
		 if(token == NULL || strlen(token) == 0)
			continue;

		 ldata->type = (dimport_conf->fields_info+i)->data_type;
		 ldata->len = get_unit_size(ldata->type);
		
		 switch(ldata->type)
		 {
			//对几种整形类型需要进行字节序的转换
			case HI_TYPE_TINY:
				tmp_int8 = atoi(token);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy( ldata->data, &tmp_int8, ldata->len);
				break;
				
			case HI_TYPE_SHORT:
			case HI_TYPE_UNSIGNED_SHORT:
				tmp_int16 = atoi(token);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy(ldata->data, &tmp_int16, ldata->len);
				break;	
				
			case HI_TYPE_LONG:
			case HI_TYPE_UNSIGNED_LONG:
				tmp_int32 = atol(token);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy(ldata->data, &tmp_int32, ldata->len);
				break;
				
			case HI_TYPE_FLOAT:
				tmp_float = strtof(token,NULL);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy(ldata->data, &tmp_float, ldata->len);
				break;
				
			case HI_TYPE_DOUBLE:
				tmp_double = strtold(token,NULL);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy(ldata->data, &tmp_double, ldata->len);
				break;
				
			case HI_TYPE_LONGLONG:
			case HI_TYPE_UNSIGNED_LONGLONG:
				tmp_int64 = strtoull(token,NULL,10);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy(ldata->data, &tmp_int64, ldata->len);
				break;
			
			case HI_TYPE_VARCHAR:
			case HI_TYPE_STRING:
				ldata->len = strlen(token);
				ldata->data = mem_pool_malloc(mem_pool,ldata->len);
				memcpy(ldata->data, token, ldata->len);
				break;
			case HI_TYPE_TIMESTAMP:
				{
					struct tm time;
					sscanf(token,"%d-%d-%d %d:%d:%d",&time.tm_year,&time.tm_mon,&time.tm_mday,&time.tm_hour,&time.tm_min,&time.tm_sec);
					//时间非法
					if (time.tm_year < 1900 || time.tm_mon <= 0 || time.tm_mon > 12 || time.tm_mday <= 0 || time.tm_mday > 31)
					{
						ldata->len = 0;
						ldata->type = HI_TYPE_NULL;
					}
					else
					{
						time.tm_year = time.tm_year-1900;
						time.tm_mon = time.tm_mon-1;
						tmp_int64 = mktime(&time);
						ldata->data = mem_pool_malloc(mem_pool,ldata->len);
						memcpy(ldata->data, &tmp_int64, ldata->len);
					}
					break;
				}
			default:
				log_error("不支持的数据类型 %d!\n", ldata->type);
				return ERROR_PACKET_FORMAT;
		}
	}

	uint16_t sid;
	uint32_t docid;
	uint32_t ret = 0;
	db_read_lock();
	ret = db_insert(dimport_conf->table_name,&sid,&docid,rdata,DOCID_BY_SELF,mem_pool);
	db_read_unlock();
	
	return ret;
}


int code_convert(iconv_t cd,char *inbuf,size_t inlen,char *outbuf,size_t outlen)
{
	int rc;
	char **pin = &inbuf;
	char **pout = &outbuf;
	
	if (iconv(cd,pin,&inlen,pout,&outlen) == -1) 
	{
		log_error("编码出错 %d",errno);
		return -1;
	}
	return 0;
}


//参数 db的配置文件  导数据的配置文件 数据文件 table的工作目录
int main(int argc, char* argv[])
{	
	int32_t ret = 0;
	if(argc < 3)
	{
		print_usage(argv[0]);
		return -1;
	}

	uint64_t begin = get_time_usec();

	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);

	//解析DB配置
	struct db_conf* dconf = config_parser(argv[1],mem_pool);
	if(dconf == NULL)
	{
		log_error("db配置解析出错\n");
		return -1;
	}

	//重新赋值db的工作目录
	dconf->storage_dirs.n = 1;
	dconf->storage_dirs.strs[0] = argv[4];
	mkdirs(dconf->storage_dirs.strs[0]);
	init_log("ERROR", dconf->storage_dirs.strs[0]);

	//解析导数据配置
	struct data_import_conf* dimport_conf = data_import_config_parser(argv[2],mem_pool);

	if(dimport_conf == NULL)
	{
		log_error("db导数据配置解析出错\n");
		mem_pool_destroy(mem_pool);
		return -1;
	}

	//初始化db
	
	ret = db_init(dconf);
	assert(ret == 0);

	//建立索引列信息
	uint16_t i;
	for(i=0;i<dimport_conf->field_count;i++)
	{
		db_read_lock();
		ret = db_ensure_index(dimport_conf->table_name,
						     (dimport_conf->fields_info+i)->field_name,
						     (dimport_conf->fields_info+i)->index_type,
						     (dimport_conf->fields_info+i)->data_type,
						      mem_pool);
		db_read_unlock();
		assert(ret == MILE_RETURN_SUCCESS);
	}

	//打开数据文件
	FILE* fp = fopen(argv[3],"r");
	if(fp == NULL)
	{
		log_error("数据文件打开失败 %d\n",errno);
		mem_pool_destroy(mem_pool);
		return -1;
	}


	MEM_POOL* mem_pool_local = mem_pool_init(100*KB_SIZE); 
	char in_buf[BUF_SIZE];
	char out_buf[BUF_SIZE];
	memset(in_buf,0,sizeof(in_buf));
	memset(out_buf,0,sizeof(out_buf));

	iconv_t cd;
	cd = iconv_open("utf8",dimport_conf->encode_type);
	assert(cd != (iconv_t)-1);
	
	while(fgets(in_buf,BUF_SIZE,fp) != NULL)
	{
    	if(code_convert(cd,in_buf,strlen(in_buf),out_buf,BUF_SIZE) < 0)
			break;
		
		if(parse_data_insert(out_buf,dimport_conf,mem_pool_local) < 0)
		{
			log_error("数据解析出错");
			iconv_close(cd);
			fclose(fp);
			mem_pool_destroy(mem_pool);
			mem_pool_destroy(mem_pool_local);
			return -1;
		}

		memset(in_buf,0,sizeof(in_buf));
		memset(out_buf,0,sizeof(out_buf));
		mem_pool_reset(mem_pool_local);
	}

	iconv_close(cd);

	//压缩

	log_error("no compress time elapse %llu",(get_time_usec()-begin)/1000);
	
	db_read_lock();
	db_compress(dimport_conf->table_name,mem_pool);
	db_read_unlock();
	
	
	//卸载所有的段
	db_read_lock();
	db_replace_all_segments(dimport_conf->table_name,NULL,mem_pool);
	db_read_unlock();

	fclose(fp);
	mem_pool_destroy(mem_pool);
	mem_pool_destroy(mem_pool_local);


	log_error("time elapse %llu",(get_time_usec()-begin)/1000);
	return 0;	
}










