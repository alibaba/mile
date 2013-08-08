// RecordIterator.cpp : RecordIterator
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-14

#include "RecordIterator.h"
#include "TableManager.h"

RecordIterator::RecordIterator(TableManager *table_mgr)
	: table_mgr_(table_mgr)
{
	table_mgr_->Ref();
}

RecordIterator::~RecordIterator()
{
	if (NULL != table_mgr_)
		table_mgr_->UnRef();
	table_mgr_ = NULL;
}

