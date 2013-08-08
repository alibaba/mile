# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2011-06-13

# load make config
TOP_DIR = ../..
include $(TOP_DIR)/MK.config

TARGETS = libstorage.a

static_libraries = ldb/libldb.a docdb/libdocdb.a

CXXFLAGS += -I ldb/leveldb/include/

all: $(static_libraries) $(TARGETS)

$(static_libraries) :
	for d in ldb docdb ; do make -C $$d ; done

$(TARGETS) : $(OBJS) $(static_libraries)
	@-rm -rf $@
	$(AR) cr $@ $^
	printf "OPEN $@\nADDLIB ldb/libldb.a\nADDLIB docdb/libdocdb.a\nSAVE\nEND" | $(AR) -M

clean :
	@rm -f $(static_libraries) $(TARGETS) $(OBJS)
	@for d in ldb docdb ; do make -C $$d clean; done

c : 
	@rm -rf $(TARGETS) $(OBJS)
