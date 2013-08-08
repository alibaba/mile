// hi_slave.h : hi_slave
// Author: liubin <bin.lb@alipay.com>
// Created: 2011-07-06

#ifndef SLAVER_H
#define SLAVER_H

int sync_with_master(int master_fd, uint64_t cur_offset, struct data_buffer *buffer, uint32_t *message_id, MEM_POOL_PTR mem);

#endif // SLAVER_H
