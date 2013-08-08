# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2011-06-13

# load make config
TOP_DIR = ..
include $(TOP_DIR)/MK.config

TARGETS = $(foreach s, $(wildcard *.c *.cpp *.cc), $(basename $(s)))

# CXXFLAGS+= $(shell xml2-config --cflags)

LDFLAGS += -L common -L protocol -L execute -L storage -L storage/docdb
LDFLAGS += -l protocol -l execute -l protocol -l storage -l docdb -l common

LDFLAGS += -levent -lpthread -lreadline -ltermcap -lrt
# LDFLAGS += $(shell xml2-config --libs)

.PHONY: static_libraries

all : static_libraries $(TARGETS)

static_libraries :
	for d in common storage protocol execute ; do make -C $$d ; done

$(TARGETS) : $(wildcard */lib*.a)

$(TARGETS) : % : %.o
	$(CXX) -o $@ $^ $(LDFLAGS)

clean :
	@rm -f $(TARGETS) *.o *.a 
	@for d in common storage protocol execute ; do make -C $$d clean; done

