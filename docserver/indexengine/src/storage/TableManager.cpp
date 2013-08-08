// TableManager.cpp : TableManager
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-06

#include "TableManager.h"
#include "StorageEngine.h"

TableManager::TableManager(StorageEngine *se) : storage_engine_(se)
{
	assert(NULL != se);
	se->Ref();
}

TableManager::~TableManager()
{
	if (storage_engine_ != NULL) {
		storage_engine_->UnRef();
		storage_engine_ = NULL;
	}
}
int32_t TableManager::getCutThreshold()
{
	return storage_engine_->getCutThreshold();
}
