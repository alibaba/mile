# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2011-06-13

# load make config
TOP_DIR = ../../..
include $(TOP_DIR)/MK.config

TARGETS = libldb.a
LEVELDBLIB = leveldb/libleveldb.a
SNAPPYLIB = snappy/.libs/libsnappy.a

CXXFLAGS += -I leveldb/include/

all: $(TARGETS)

unexport CXXFLAGS LDFLAGS

$(LEVELDBLIB) : $(SNAPPYLIB)
	CXXFLAGS=-g make -C leveldb

$(SNAPPYLIB) : snappy/Makefile
	make -C snappy

snappy/Makefile :
	cd snappy && ./autogen.sh && CXXFLAGS=-g LDFLAGS=-lpthread ./configure

$(TARGETS) : $(OBJS) $(LEVELDBLIB) $(SNAPPYLIB)
	@-rm -rf $@
	$(AR) cr $@ $(OBJS)
	printf "OPEN $@\nADDLIB $(LEVELDBLIB)\nADDLIB $(SNAPPYLIB)\nSAVE\nEND" | $(AR) -M

clean :
	@rm -f $(TARGETS) $(OBJS)
	@-make -C leveldb clean
	# @-make -C snappy distclean
	@-make -C snappy clean

# local clean
c:
	@rm -f $(TARGETS) $(OBJS)
