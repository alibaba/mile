# Makefile : Makefile
# Author: liubin <bin.lb@alipay.com>
# Created: 2011-06-13

# load makefile config
TOP_DIR = ..
include $(TOP_DIR)/MK.config

TEST_SRCS = $(wildcard unit_*.cpp)
TARGETS = $(filter-out unit_hi_packet_execute_test unit_suffix unit_sql_execute_test unit_sort_test unit_packet_execute_test unit_set_operator_test unit_databuffer_test, $(foreach s, $(TEST_SRCS), $(basename $s)))



LDFLAGS += -L ../src/common -L ../src/protocol -L ../src/execute -L ../src/storage 
LDFLAGS += -l protocol -l execute -l protocol -l storage -l common
LDFLAGS +=  -levent -lpthread -lreadline -ltermcap
LDFLAGS += $(shell xml2-config --libs)
LDFLAGS += -lgtest -lgtest_main




SRCS = $(wildcard ../src/*.c)
OBJS = $(foreach s, $(SRCS), $(basename $(s)).o)
 
# filter out main()
COMMON_OBJS = $(filter-out $(foreach t, docserver client hi_dump hi_control_segment load_monitor_data, ../src/$(t).o), $(OBJS))
COMMON_OBJS += config.o 

.PHONY: all clean 

all: $(TARGETS)

$(TARGETS) : $(COMMON_OBJS)


$(TARGETS) : % : %.o
	$(CXX) -o $@ $< $(COMMON_OBJS) $(LDFLAG)

clean :
	@rm -f $(TARGETS) $(OBJS) *.o
