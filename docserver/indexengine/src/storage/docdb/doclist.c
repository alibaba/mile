/*
 * =====================================================================================
 *
 *       Filename:  hi_doclist.c
 *
 *    Description:  以链表形式，存储所有hash值相同的row id，提供插入以及遍历的实现
 *
 *        Version:  1.0
 *        Created: 	2011年04月09日 11时41分55秒 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */

#include "doclist.h"

struct doclist_manager* doclist_init(struct doclist_config* config,MEM_POOL* mem_pool)
{
   //初始化结构
   struct doclist_manager* doclist = (struct doclist_manager*)mem_pool_malloc(mem_pool,sizeof(struct doclist_manager));
   assert(doclist != NULL);
   memset(doclist,0,sizeof(struct doclist_manager));

   sprintf(doclist->file_name,"%s/doclist.idx",config->work_space);

   //补全文件
   //要加上4字节的内存地址，用于存储偏移量
   doclist->row_limit = config->row_limit;
   doclist->is_full = config->is_full;
   
   if(config->is_full || mile_conf.all_mmap) {
	   //mmap映射处理
	   doclist->mem_mmaped =(char*)get_mmap_memory(doclist->file_name,DOCLIST_MMAP_SIZE(doclist)); 	
   } else {
		// alloc memory like malloc
	   doclist->mem_mmaped = (char*)alloc_file_memory(doclist->file_name, DOCLIST_MMAP_SIZE(doclist)); 	
   }
   assert(doclist->mem_mmaped != NULL);

   //初始化偏移量
   ///头4位记录偏移量，不记录这个偏移量，会导致死循环
   doclist->version = *(uint32_t*)doclist->mem_mmaped;

   if(doclist->version == 0)
   {
		*(uint32_t*)doclist->mem_mmaped = DATA_STOAGE_VERSION;
		doclist->version = *(uint32_t*)doclist->mem_mmaped; 
   }

   return doclist;
}

struct doclist_manager* doclist_init_v2(struct doclist_config* config, uint8_t is_create, uint16_t index, MEM_POOL* mem_pool)
{
   //初始化结构
   struct doclist_manager* doclist = (struct doclist_manager*)mem_pool_malloc(mem_pool,sizeof(struct doclist_manager));
   assert(doclist != NULL);
   memset(doclist,0,sizeof(struct doclist_manager));

	
	sprintf(doclist->file_name,"%s/doclist.idx.%"PRIu16,config->work_space, index);

	//判断文件是否存在
	if(is_create != 1 && access(doclist->file_name, F_OK) != 0){
		log_warn("filename: %s not exit", doclist->file_name);
		return NULL;
	}	

   //补全文件
   //要加上4字节的内存地址，用于存储偏移量
   doclist->row_limit = config->row_limit;
   doclist->is_full = config->is_full;
   
   if(config->is_full || mile_conf.all_mmap) {
	   //mmap映射处理
	   doclist->mem_mmaped =(char*)get_mmap_memory(doclist->file_name,DOCLIST_MMAP_SIZE(doclist)); 	
   } else {
		// alloc memory like malloc
	   doclist->mem_mmaped = (char*)alloc_file_memory(doclist->file_name, DOCLIST_MMAP_SIZE(doclist)); 	
   }
   assert(doclist->mem_mmaped != NULL);

   //初始化偏移量
   ///头4位记录偏移量，不记录这个偏移量，会导致死循环
   doclist->cur_offset = (uint32_t*)doclist->mem_mmaped;

   return doclist;
}


uint32_t doclist_insert_v2(struct doclist_manager* doclist,uint32_t doc_id,uint32_t head_offset, uint32_t bucket_no)
{
	struct doc_row_unit* doc_row = NULL;

	if((*doclist->cur_offset) == doclist->row_limit)
		return -1;
	
    //head为空，说明哈希取模后还未冲突
    //next将不是0，而是桶号
	if(head_offset == 0)
	{
		//向map内存索取一个struct doc_row_unit结构
		//偏移量不是docid了
		doc_row = GET_DOC_ROW_STRUCT(doclist, *doclist->cur_offset);
		memset(doc_row,0,sizeof(struct doc_row_unit));
		doc_row->next = 0X80000000 | bucket_no;
	}
	//如果不为空，说明同一个value对应多个doc id
	else
	{
		struct doc_row_unit* doc_row_head = (struct doc_row_unit*)(doclist->mem_mmaped+head_offset);
		if(doc_id <= doc_row_head->doc_id)
		{
			log_warn("docid 出现乱序 插入的docid:%"PRIu32" 上一个docid:%"PRIu32, doc_id,doc_row_head->doc_id);
			return 0;
		}

		doc_row = GET_DOC_ROW_STRUCT(doclist, *doclist->cur_offset);
		doc_row->next = head_offset;
	}

	//赋值docid
	uint32_t offset = ((*doclist->cur_offset) * sizeof(struct doc_row_unit) + sizeof(uint32_t));
	doc_row->doc_id = doc_id;
	(*doclist->cur_offset) ++;
	return offset;
}

//插入的时候分两种情况，如果hash表的一个桶是空的，那么head_offset为0，只需要把offset赋值就可以了
//如果桶已经有值了，则采用头插法，这里偏移量即为指针
uint32_t doclist_insert(struct doclist_manager* doclist,uint32_t doc_id,uint32_t head_offset, uint32_t bucket_no)
{
	struct doc_row_unit* doc_row = NULL;
	
    //head为空，说明哈希取模后还未冲突
    //next将不是0，而是桶号
	if(head_offset == 0)
	{
		//向map内存索取一个struct doc_row_unit结构
		doc_row = GET_DOC_ROW_STRUCT(doclist, doc_id);
		memset(doc_row,0,sizeof(struct doc_row_unit));
		doc_row->next = 0X80000000 | bucket_no;
	}
	//如果不为空，说明同一个value对应多个doc id
	else
	{
		struct doc_row_unit* doc_row_head = (struct doc_row_unit*)(doclist->mem_mmaped+head_offset);
		if(doc_id <= doc_row_head->doc_id)
		{
			log_warn("docid 出现乱序 插入的docid:%"PRIu32" 上一个docid:%"PRIu32, doc_id,doc_row_head->doc_id);
			return 0;
		}
		
		doc_row = GET_DOC_ROW_STRUCT(doclist, doc_id);
		doc_row->next = head_offset;
	}

	//赋值docid
	doc_row->doc_id = doc_id;
	return GET_OFFSET(doc_id);
}



//将next偏移量转换为doc_row_unit结构信息
struct doc_row_unit *doclist_next(struct doclist_manager* doclist, uint32_t next)
{
	if((next & 0x80000000) || next == 0)
		return NULL;
	return (struct doc_row_unit*)(doclist->mem_mmaped + sizeof(uint32_t) +next);
}


int32_t doclist_checkpoint(struct doclist_manager* doclist)
{
	if(doclist->mem_mmaped != NULL)
	{
		if( doclist->is_full || mile_conf.all_mmap ) {
			return msync(doclist->mem_mmaped,DOCLIST_MMAP_SIZE(doclist),MS_SYNC); // make sure synced
		} else  {
			return flush_memory(doclist->file_name, doclist->mem_mmaped, DOCLIST_MMAP_SIZE(doclist), mile_conf.disk_write_limit);
		}
	}
	return 0;
}

void doclist_destroy(struct doclist_manager* doclist)
{
	//内存不需要释放，只需要关闭文件，flush到磁盘
	if(doclist->mem_mmaped != NULL)
	{
		munmap(doclist->mem_mmaped, DOCLIST_MMAP_SIZE(doclist));
	}

	//删除文件
	remove(doclist->file_name);
	return;
}


void doclist_release(struct doclist_manager* doclist)
{
	//内存不需要释放，只需要关闭文件，flush到磁盘
	if(doclist->mem_mmaped != NULL)
	{
		if( doclist->is_full || mile_conf.all_mmap ) {
			msync(doclist->mem_mmaped,DOCLIST_MMAP_SIZE(doclist),MS_SYNC); // make sure synced
		} else  {
			flush_memory(doclist->file_name, doclist->mem_mmaped, DOCLIST_MMAP_SIZE(doclist), 0); // no disk write limit
		}
		munmap(doclist->mem_mmaped, DOCLIST_MMAP_SIZE(doclist));
	}
	return;
}

int doclist_mmap_switch(struct doclist_manager *doclist)
{
	if( !doclist->is_full && !mile_conf.all_mmap ) {
		if(flush_memory(doclist->file_name, doclist->mem_mmaped, DOCLIST_MMAP_SIZE(doclist), mile_conf.disk_write_limit) != 0) {
			return ERROR_MMAP_SWITCH;
		}

		if(switch_mmaped_file(doclist->file_name, doclist->mem_mmaped, DOCLIST_MMAP_SIZE(doclist)) != 0 ) {
			return ERROR_MMAP_SWITCH;
		}
		doclist->is_full = 1;
	}
	return MILE_RETURN_SUCCESS;
}
