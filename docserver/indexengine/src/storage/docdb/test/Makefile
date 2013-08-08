# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2012-03-23

# load make config
TOP_DIR = ../../../../
include $(TOP_DIR)/MK.config

TARGETS = $(foreach src, $(SRCS), $(basename $(src)) )

LDFLAGS += -L .. -ldocdb
LDFLAGS += -L ../../ -lstorage
LDFLAGS += -L ../../../common -lcommon -lgtest -lpthread  -rdynamic


all : $(TARGETS)

$(TARGETS) : % : %.o
	$(CXX) $(CFLAGS) -o $@ $< $(LDFLAGS)

clean :
	-@rm -rf $(TARGETS) *.o

##################################
