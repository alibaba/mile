// DocEngine.h : DocEngine
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#ifndef DOCENGINE_H
#define DOCENGINE_H

#include "../StorageEngine.h"
#include "db.h"
#include "docdb_packet.h"
#include "DocHandler.h"
#include "../../execute/CommonRS.h"
#include "../../execute/RowClone.h"
#include <string>

struct db_manager;
struct db_conf;

class ConfigFile;

class DocEngine : public StorageEngine
{
public:
	DocEngine(const char *storage_dir, const ConfigFile &conf);
	virtual ~DocEngine();

	virtual int Init();

	virtual TableManager *GetTableManager(const char *table_name, MEM_POOL_PTR mem_pool);

	virtual int Command(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf);

	virtual int SpecialSql(struct mile_message_header* msg_head, struct data_buffer* rbuf, struct data_buffer* sbuf);

	virtual const binlog_writer *GetBinlogWriter();

	virtual int ApplyBinlog(struct binlog_record *record, MEM_POOL_PTR mem_pool);

	virtual uint64_t SlaveSyncPos();

	virtual int SetSlaveSyncPos(uint64_t offset_cur, uint64_t offset_left);

	virtual void SetReadable(bool readable);

	virtual bool GetReadable();
	int32_t getCutThreshold();
private:
	int LoadConfig();

protected:
	virtual void Ref();
	virtual void UnRef();

private:
	std::string storage_dir_;
	const ConfigFile *conf_;

	MEM_POOL_PTR mem_;
	db_conf *db_conf_;

	ResultSet* GetKvs(struct get_kvs_packet* packet, MEM_POOL_PTR mem_pool);
};

#endif // DOCENGINE_H
