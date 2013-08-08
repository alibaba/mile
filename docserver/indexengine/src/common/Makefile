# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2011-06-13

# load make config
TOP_DIR = ../..
include $(TOP_DIR)/MK.config

TARGETS = libcommon.a

# CXXFLAGS += $(shell xml2-config --cflags)

all: $(TARGETS)

$(TARGETS) : $(OBJS)
	$(AR) cr $@ $^

clean :
	@rm -f $(TARGETS) $(OBJS)
