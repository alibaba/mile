/*
 * =====================================================================================
 *
 *       Filename:  hi_filtercompress.h
 *
 *    Description:  compress filter index
 *
 *        Version:  1.0
 *        Created:  2011年09月02日 10时42分34秒
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  shuai.li (algol), shuai.li@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */

#ifndef FILTERCOMPRESS_H
#define FILTERCOMPRESS_H

#include "../../common/def.h"
#include "index_field.h"
#include "../../common/string_map.h"

#define DINFO_SIZE 8

#ifdef __cplusplus                                                          
extern "C" {
#endif

#if 1
	struct filter_compress_manager{
		char index_file_name[FILENAME_MAX_LENGTH];
		char data_file_name[FILENAME_MAX_LENGTH];
		char info_file_name[FILENAME_MAX_LENGTH];
		char null_file_name[FILENAME_MAX_LENGTH];

		uint8_t *index_mmap;
		uint8_t *data_mmap;

		struct bitmark_manager *bitmap_index;

		struct bitmark_manager *bitmark;

		/*
			4bytes for n_row
			4bytes for n_value
			4bytes for unit_size
			4bytes for idx_size
			1 byte for index_type
		*/
		uint32_t *info_map;
		uint32_t n_row;	//the number of rows
		uint32_t n_value;	//the number of values
		uint32_t unit_size;
		uint8_t idx_size;
		uint8_t index_type; 	//0->no index; 1->index 2->bitmap
	};

	struct bucket_entry{
		uint64_t value;
		int64_t pos;
	};


	/** 
	 * filter_compress_init
	 * @name:  filter_compress_init
	 * @desc:  filter compress index init
	 * @param  work_space
	 * @param  mem_pool
	 * @return 
	 */
	struct filter_compress_manager * filter_compress_init(char *work_space,	MEM_POOL *mem_pool);


	/** 
	 * filter_compress_load
	 * @name:  filter_compress_load
	 * @desc:  rearrange filter index
	 * @param  storage
	 * @param  unit_size: sizeof one element
	 * @param  mem_pool
	 * @return segment_filter_compress
	 */
	struct filter_compress_manager * filter_compress_load( 	
			struct storage_manager* storage, 		 		
			uint32_t unit_size,									
			MEM_POOL *mem_pool);



	/** 
	 * filter_compress_query
	 * @name:  filter_compress_query
	 * @desc:  get value by rowid 
	 * @param  filter_index --> filter compress index
	 * @param  docid
	 * @param  mem_pool
	 * @return value, null if not exist
	 */
	struct low_data_struct* filter_compress_query( 			\
			struct filter_compress_manager* filter_index, 	\
			uint32_t docid,									\
			MEM_POOL *mem_pool);


	/** 
	 * filter_compress_release
	 * @name:  filter_compress_release
	 * @desc: release filter compress index
	 * @param filter_compress
	 * @return 
	 */
	void filter_compress_release(struct filter_compress_manager * filter_compress);


	/** 
	 * filter_compress_destroy
	 * @name:  filter_compress_destroy
	 * @desc: destroy filter compress index
	 * @param filter_compress
	 * @return 
	 */
	void filter_compress_destroy(struct filter_compress_manager * filter_compress);

#endif

#ifdef __cplusplus                                                          
}
#endif

#endif /* FILTERCOMPRESS_H */
