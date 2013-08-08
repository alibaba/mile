# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2012-05-25

TOP_DIR = ../../..
include $(TOP_DIR)/MK.config

COMMOBJS = $(foreach src, $(filter-out $(wildcard *_test.cpp), $(wildcard *.c *.cpp)), $(basename $(src)).o)
MAINOBJS = $(foreach src, $(wildcard *_test.cpp), $(basename $(src)).o)
TARGETS = $(foreach src, $(MAINOBJS), $(basename $(src)) )

CXXFLAGS += -I ../ldb/leveldb/include/
LDFLAGS += -L ../ -lstorage -L ../../execute -lexecute -L ../../protocol -lprotocol -L ../../common -lcommon 
LDFLAGS += -lgtest -lpthread  -rdynamic 

all : $(TARGETS)

$(TARGETS) : % : %.o $(COMMOBJS)
	$(CXX) -o $@ $^ $(LDFLAGS)

clean :
	-rm $(TARGETS) *.o
