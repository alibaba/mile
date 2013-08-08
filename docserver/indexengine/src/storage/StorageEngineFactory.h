// StorageEngineFactory.h : StorageEngineFactory
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-13

#ifndef STORAGEENGINEFACTORY_H
#define STORAGEENGINEFACTORY_H

class StorageEngine;
class ConfigFile;

class StorageEngineFactory
{
public:
	static StorageEngine *CreateEngine(const ConfigFile &conf);
};

#endif // STORAGEENGINEFACTORY_H
