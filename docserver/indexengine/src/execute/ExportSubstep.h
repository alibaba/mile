// ExportSubstep.h : ExportSubstep
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-30

#ifndef EXPORTSUBSTEP_H
#define EXPORTSUBSTEP_H

#include "ExecuteSubstep.h"

#include <string>

class ExportSubstep : public ExecuteSubstep {
public:
	ExportSubstep(char *table, char *path, int64_t limit);
	~ExportSubstep();

	int Init(void);
	virtual int32_t Execute(TableManager *table, MileHandler *handler, void *output, MEM_POOL_PTR mem_pool);

private:
	char *table_name_;
	char *path_;
	int fd_;
	int64_t limit_;

	std::string out_buf_;
};

#endif // EXPORTSUBSTEP_H
