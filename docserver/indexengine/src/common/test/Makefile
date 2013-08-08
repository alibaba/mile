# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2012-05-25
# load make config

TOP_DIR = ../../..
include $(TOP_DIR)/MK.config

TARGETS = $(foreach src, $(SRCS), $(basename $(src)) )

LDFLAGS += -L .. -lcommon -lgtest -lpthread  -rdynamic

all : $(TARGETS)

$(TARGETS) : % : %.o
	$(CXX) -o $@ $< $(LDFLAGS)

clean :
	-rm $(TARGETS) *.o
