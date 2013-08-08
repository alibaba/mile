/*
 * storage_engine.h
 *
 *  Created on: 2012-7-27
 *      Author: yuzhong.zhao
 */

#ifndef STORAGE_ENGINE_H_
#define STORAGE_ENGINE_H_


#include "../common/mem.h"
#include "TableManager.h"

struct binlog_writer;
struct binlog_record;

class StorageEngine{
public:
	virtual ~StorageEngine() {} 

	virtual int Init(void) = 0;

	// NOTE: The returnd table manager is created by placement new with memory allocated from mem_pool
	// caller must call result->~TableManager() when the result is no longer needed.
	virtual TableManager *GetTableManager(const char *table_name, MEM_POOL_PTR mem_pool) = 0;

	// handle the command
	virtual int Command(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf) = 0;

	// handle some special sql
	virtual int SpecialSql(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf) = 0;

	// get slave binlog start sync position
	virtual uint64_t SlaveSyncPos() = 0;

	// set slave binlog sync position
	virtual int SetSlaveSyncPos(uint64_t offset_cur, uint64_t offset_left) = 0;

	// get binlog writer
	virtual const binlog_writer *GetBinlogWriter() = 0;

	// apply binlog record
	virtual int ApplyBinlog(struct binlog_record *record, MEM_POOL_PTR mem_pool) = 0;

	virtual void SetReadable(bool readable) = 0;

	virtual bool GetReadable() = 0;

	// global instance
	static StorageEngine *storage;
	virtual int32_t getCutThreshold() = 0;
protected:
	friend class TableManager;

	virtual void Ref() = 0;
	virtual void UnRef() = 0;
};

#endif /* STORAGE_ENGINE_H_ */
