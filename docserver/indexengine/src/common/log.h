/*
 * =====================================================================================
 *
 *       Filename:  log.h
 *
 *    Description:  日志头文件，日志将分级别打印在不同的文件里
 *
 *        Version:  1.0
 *        Created: 	2011年04月09日 11时41分55秒 
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  yunliang.shi, yunliang.shi@alipay.com
 *        Company:  alipay.com
 *
 * =====================================================================================
 */


#ifndef LOG_H
#define LOG_H
#include <stdarg.h>
#include <time.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <pthread.h>


#define log_error(...) log_message(LOG_LEVEL_ERROR,get_error_filename(),get_error_fd(),get_error_fp(),__VA_ARGS__)
#define log_warn(...) log_message(LOG_LEVEL_WARN, get_default_filename(),get_default_fd(),get_default_fp(),__VA_ARGS__)
#define log_info(...) log_message(LOG_LEVEL_INFO, get_default_filename(),get_default_fd(),get_default_fp(),__VA_ARGS__)
#define log_debug(...) log_message(LOG_LEVEL_DEBUG,get_default_filename(),get_default_fd(),get_default_fp(),__VA_ARGS__)
#define log_perf(...) log_simple_message(get_perf_filename(),get_perf_fd(),get_perf_fp(),__VA_ARGS__)
#define log_digest(...) log_simple_message(get_digest_filename(),get_digest_fd(),get_digest_fp(),__VA_ARGS__)

#define ASSERT(x) do { \
	if(!(x)) { \
		dump_frame_abort( LOG_LEVEL_ERROR, get_error_filename(),get_error_fd(),get_error_fp(), #x); \
	} \
} while(0) 

#define is_debug_enabled(...) log_level_enable(3)
#define is_info_enabled(...) log_level_enable(2)

#define LOG_LEVEL_ERROR 0, __FILE__, __LINE__, __FUNCTION__
#define LOG_LEVEL_WARN  1, __FILE__, __LINE__, __FUNCTION__
#define LOG_LEVEL_INFO  2, __FILE__, __LINE__, __FUNCTION__
#define LOG_LEVEL_DEBUG 3, __FILE__, __LINE__, __FUNCTION__


extern int g_level;





/** 
 *	更安全的snprintf函数, 将字符串按照格式打印到buffer数组中, 并在字符串末尾加\0. 当n的长度小于0时返回0; 当剩余的长度不足打印全部字符串时, 只打印部分字符串, 并返回打印的长度 
 *	@param 	buffer				字符串数组	
 *  @param	n					数组的大小
 *	@param 	format				格式
 *	@return						打印到buffer中的字符长度
 */
int snprintf_buffer(char* buffer, int n, const char* format, ...);




void log_message(int level, const char *file, int line, const char *function, char* filename,int fd,FILE* fp,const char *fmt, ...) __attribute__((format(printf, 8, 9)));


void log_simple_message(char* filename,int fd, FILE* fp,const char *fmt, ...);


void dump_frame_abort(int level, const char *file, int line, const char *function, char *filename, int fd, FILE* fp, const char *msg) __attribute__((noreturn));


/**
 * 设置日志级别  "ERROR","WARN","INFO","DEBUG" 
 * 设置日志文件  文件目录
 * @param level 日志级别
 * @param work_space 文件名
 * @return : 申请的内存池指针
 * */
void init_log(const char* level, const char* work_space);



int log_level_enable(int level);

void set_log_level(const char *level);


int get_error_fd();
FILE* get_error_fp();
char* get_error_filename();


int get_default_fd();
FILE* get_default_fp();
char* get_default_filename();


int get_digest_fd();
FILE* get_digest_fp();
char* get_digest_filename();



int get_perf_fd();
FILE* get_perf_fp();
char* get_perf_filename();


#endif
