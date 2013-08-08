// StorageEngineFactory.cpp : StorageEngineFactory
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-13

#include "StorageEngineFactory.h"
#include "StorageEngine.h"
#include "ldb/LdbEngine.h"
#include "docdb/DocEngine.h"

#include "../common/def.h"
#include "../common/ConfigFile.h"

#include <string>

StorageEngine *StorageEngineFactory::CreateEngine(const ConfigFile &conf)
{
	std::string storage_dir = conf.GetValue(CONF_SERVER_SESSION, "work_space", "");
	if (storage_dir.empty()) {
		log_error("storage dir is empty");
		return NULL;
	}

	StorageEngine *engine = NULL;
	std::string engine_name = conf.GetValue(CONF_SERVER_SESSION, "storage_engine", "");
	if (engine_name == "ldb") {
		engine = new LdbEngine(storage_dir.c_str(), conf);
	}
	else if (engine_name == "docdb" ) {
		engine = new DocEngine(storage_dir.c_str(), conf);
	}
	else {
		log_error("unknown storage engine %s", engine_name.c_str());
	}

	return engine;
}
