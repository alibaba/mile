#include "def.h"
#include <dirent.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/types.h>

#ifndef FILE_OP_H
#define FILE_OP_H


#ifndef S_IRWXUGO
# define S_IRWXUGO (S_IRWXU | S_IRWXG | S_IRWXO)
#endif


/**
  * 判断block的文件大小，如果小于size，则补0
  * @param  split_block block信息
  * @param  size 需要验证的大小
  * @return 成功返回0，失败返回-1
  **/ 
 int32_t ensure_file_size(int32_t fd,int32_t size);




 /**
   * 根据文件名打开一个文件，返回文件的fd
   * @param  file_name 需要打开的文件名
   * @return 成功返回fd，失败返回-1
   **/ 
 int32_t open_file(const char *file_name,int32_t flag);



 /**
   * 返回文件的偏移量
   * @param  fd 文件描述符
   * @return 成功返回文件偏移量
   **/ 
 off_t get_position(int32_t fd);


 /**
   * 返回文件的大小
   * @param  fd 文件描述符
   * @return 成功返回文件大小
   **/ 
 uint64_t get_file_size(uint32_t fd);


  /**
   * 关闭文件
   * @param  fd 文件描述符
   * @return 成功返回0，失败返回-1
   **/ 
 int32_t close_file(int32_t fd);


 /**
  * 创建目录
  * @param	dir_path 目录名
  * @return 成功返回0，失败返回-1
  **/ 
 int32_t mkdirs(char *dir_path);



 /**
  * 复制目录
  * @param	filename 原有的文件
  * @param  new_filename 新文件的名称
  * @return 成功返回0，失败返回-1
  **/ 
 int32_t cp_dir(const char* filename,const char* new_filename);



 /**
  * 判断dir_path目录是否存在
  * @param	dir_path 目录名
  * @return 是目录的话返回1，不是或不存在则返回0
  **/ 
 int32_t is_dir(const char *dir_path);


 /**
  * 移动文件filename到新的目录下
  * @param	filename 老的文件路径
  * @param  newdir  移动的新的目录
  * @param  nid     节点号
  * @return 成功返回0，失败返回-1
  **/ 
 int32_t mv_file(const char* filename,const char* newdir,uint16_t nid);

 /**
  * 建立一个link指向filename
  * @param	filename 老的文件路径
  * @param  newdir  移动的新的目录
  * @param  nid     节点号
  * @return 成功返回0，失败返回-1
  **/ 
 int32_t link_file(const char* filename,const char* newdir,uint16_t nid);


 /**
  * 设置fd为非阻塞模式
  * @param	fd 文件描述符
  * @return 成功返回0，失败返回-1
  **/ 
 int32_t set_file_noblock(int32_t fd);


 /**
  * 判断dir_path是否为目录
  * @param	dir_path 
  * @return 如果是目录，返回1，否则返回0
  **/
 int32_t is_directory(const char *dir_path);

 /**
  * 获取共享内存
  * @param	文件名
  * @param 需要映射的内存大小
  * @return 成功返回内存，失败为NULL
  **/
 void* get_mmap_memory(char* filename,uint32_t size);

/**
 * Alloc memory from os, initialized by file content. Free alloced memory by munmap.
 * Create file, if file not exist.
 * @param filename
 * @param size memory size.
 * @return alloced memory on success, NULL on error.
 */
void *alloc_file_memory(const char *filename, uint32_t size);

/**
 * Save memory to file, flushed to disk.
 * @param filename
 * @param mem
 * @param size, memory size
 * @param write_limit write throughout limit, byte per second.
 * @return on success 0 is returned, on error -1 is returned.
 */
int flush_memory(const char *filename, void *mem, uint32_t size, uint32_t write_limit);

/**
 * Switch mmaped file.
 * @param filename file switch to.
 * @param mem mem must be a multiple of the page size.
 * @param size
 * @return on success 0 is returned, on error -1 is returned.
 */
int switch_mmaped_file(const char *filename, void *mem, uint32_t size);


//定义回调函数
typedef int32_t (*traversal_dir_callback)(char* dir,void* arg);

/**
 * 遍历第一层子目录
 * @param  dir 父目录名
 * @param  func 回调函数
 * @param  arg 回调函数的参数
 * @return 成功返回0，失败<0
 **/
int32_t traversal_single_deep_childdir(char* dir,traversal_dir_callback func,void* arg);

// uninterruptable read, same with read in unistd.h
// on error, errno is set appropriately
ssize_t unintr_read(int fd, void *buf, size_t count);

// uninterruptable write, same with write in unistd.h
// on error, errno is set appropriately
ssize_t unintr_write(int fd, const void *buf, size_t count);

// uninterruptable pread, same with pread in unistd.h
// on error, errno is set appropriately
ssize_t unintr_pread(int fd, void *buf, size_t count, off_t offset);

// uninterruptable pwrite, same with pwrite in unistd.h
// on error, errno is set appropriately
ssize_t unintr_pwrite(int fd, const void *buf, size_t count, off_t offset);

#endif // FILE_OP_H
