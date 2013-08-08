/*
 * =====================================================================================
 *
 *       Filename:  log.c
 *
 *    Description:  日志工具实现
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


#include <sys/time.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <execinfo.h>
#include <stdlib.h>
#include "log.h"

static const char *g_errstr[] = {"ERROR","WARN","INFO","DEBUG"};
static pthread_mutex_t g_rotate_mutex;
int g_level = 9;

int error_fd = -1;
FILE* error_fp = NULL;
char error_filename[100];

int default_fd = -1;
FILE* default_fp = NULL;
char default_filename[100];

int digest_fd = -1;
FILE* digest_fp = NULL;
char digest_filename[100];

int perf_fd = -1;
FILE* perf_fp = NULL;
char perf_filename[100];



int snprintf_buffer(char* buffer, int n, const char* format, ...)
{
	if(n <= 0)
	{
		return 0;
	}
	va_list args;
	int ret = 0;
	va_start(args, format);
	ret = vsnprintf(buffer, n, format, args);
	va_end(args);

	if(ret < 0)
	{
		return 0;
	}else if(ret > n){
		return n;
	}else{
		return ret;
	}
}






void set_log_level(const char *level)
{
    if (level == NULL) return;
    int i, l = sizeof(g_errstr)/sizeof(const char*);
    for(i=0; i<l; i++) {
        if (strcasecmp(level, g_errstr[i]) == 0) {
            g_level = i;
            break;
        }
    }
}


FILE* get_error_fp()
{
	return NULL == error_fp ? stderr : error_fp;
}
int get_error_fd()
{
	return -1 == error_fd ? STDERR_FILENO : error_fd;
}
char* get_error_filename()
{
	return error_filename;
}


FILE* get_default_fp()
{
	return NULL == default_fp ? stderr : default_fp;
}
int get_default_fd()
{
	return -1 == default_fd ? STDERR_FILENO : default_fd;
}

char* get_default_filename()
{
	return default_filename;
}


FILE* get_digest_fp()
{
	return NULL == digest_fp ? stderr : digest_fp;
}
int get_digest_fd()
{
	return -1 == digest_fd ? STDERR_FILENO : digest_fd;;
}
char* get_digest_filename()
{
	return digest_filename;
}


FILE* get_perf_fp()
{
	return NULL == perf_fp ? stderr : perf_fp;
}
int get_perf_fd()
{
	return -1 == perf_fd ? STDERR_FILENO : perf_fd;
}
char* get_perf_filename()
{
	return perf_filename;
}


static void set_file_name(const char* work_space)
{
    //参数判断
    if(work_space == NULL)
    {
    	return;
    }

	//拼接错误文件名
	memset(error_filename,0,sizeof(error_filename));
	sprintf(error_filename,"%s/common-error.log",work_space);

    //打开错误文件
    error_fd = open(error_filename, O_RDWR | O_CREAT | O_APPEND, 0640);
	if(error_fd == -1){
		return;
	}
	error_fp = fdopen(error_fd,"w+");

	//拼接default文件名
	memset(default_filename,0,sizeof(default_filename));
	sprintf(default_filename,"%s/common-default.log",work_space);

	//打开default文件
	default_fd = open(default_filename, O_RDWR | O_CREAT | O_APPEND, 0640);
	if(default_fd == -1){
		return;
	}
	default_fp = fdopen(default_fd,"w+");


	//拼接性能文件名
	memset(perf_filename,0,sizeof(perf_filename));
	sprintf(perf_filename,"%s/common-perf.log",work_space);

	//打开性能文件
	perf_fd = open(perf_filename, O_RDWR | O_CREAT | O_APPEND, 0640);
	if(perf_fd == -1){
		return;
	}
	perf_fp = fdopen(perf_fd,"w+");

	//拼接业务摘要文件名
	memset(digest_filename,0,sizeof(digest_filename));
	sprintf(digest_filename,"%s/monitor-digest.log",work_space);

	//拼接业务摘要文件名
	digest_fd = open(digest_filename, O_RDWR | O_CREAT | O_APPEND, 0640);
	if(digest_fd == -1){
		return;
	}
	digest_fp = fdopen(digest_fd,"w+");
	

	pthread_mutex_init(&g_rotate_mutex, NULL);
	return;
}

void init_log(const char* level, const char* work_space)
{
	set_file_name(work_space);
	set_log_level(level);
	return ;
}


static void rotate_log(int global_fd,char* filename)
{ 
    if (access(filename, R_OK) == 0) {
        char oldLogFile[256];
        time_t t; 
        time(&t);
        struct tm *tm = localtime((const time_t*)&t); 
        sprintf(oldLogFile, "%s.%04d%02d%02d%02d%02d%02d", 
            filename, tm->tm_year+1900, tm->tm_mon+1, tm->tm_mday, 
            tm->tm_hour, tm->tm_min, tm->tm_sec);
        rename(filename, oldLogFile);
    }
    int fd = open(filename, O_RDWR | O_CREAT | O_APPEND, 0640);
    dup2(fd,global_fd);
    close(fd);
}

int log_level_enable(int level)
{
	if(level>g_level) 
		return 0;
	else
		return 1;
}


void log_message(int level, const char *file, int line, const char *function, char* filename,int fd, FILE* fp,const char *fmt, ...)
{

    if (level>g_level) return;

    char buffer[1024];
    struct timeval tv;
    gettimeofday( &tv, NULL);
    struct tm tm;
    localtime_r((const time_t*)&tv.tv_sec, &tm); 
    
    // TODO : size > sizeof( buffer ) ?
    int size = snprintf(buffer,sizeof(buffer),"[%04d-%02d-%02d %02d:%02d:%02d.%06ld] [TID %d] %-5s %s (%s:%d) %s\n",
        tm.tm_year+1900, tm.tm_mon+1, tm.tm_mday,
        tm.tm_hour, tm.tm_min, tm.tm_sec, tv.tv_usec, (pid_t)syscall(SYS_gettid),
        g_errstr[level], function, file, line, fmt);
    // 去掉过多的换行
    while(buffer[size-2] == '\n') size --; 
    buffer[size] = '\0';

    va_list args;
    va_start(args, fmt);
    vfprintf(fp, buffer, args);
    fflush(fp);
    va_end(args);

    pthread_mutex_lock(&g_rotate_mutex);
    off_t offset = lseek(fd, 0, SEEK_END);
    if ( offset < 0 ){
            //忽略此错误
     } else {
       //大于64m，则回滚
       if ( offset >=  0x4000000) {
            rotate_log(fd,filename);
       }
     }
   pthread_mutex_unlock(&g_rotate_mutex);
}



void log_simple_message(char* filename,int fd, FILE* fp,const char *fmt, ...)
{

    char buffer[1024];
    struct timeval tv;
    gettimeofday( &tv, NULL);
    struct tm tm;
    localtime_r((const time_t*)&tv.tv_sec, &tm); 
    
    // TODO : size > sizeof( buffer ) ?
    int size = snprintf(buffer,sizeof(buffer),"[%04d-%02d-%02d %02d:%02d:%02d.%06ld] %s\n",
        tm.tm_year+1900, tm.tm_mon+1, tm.tm_mday,
        tm.tm_hour, tm.tm_min, tm.tm_sec, tv.tv_usec,fmt);
    // 去掉过多的换行
    while(buffer[size-2] == '\n') size --; 
    buffer[size] = '\0';

    va_list args;
    va_start(args, fmt);
    vfprintf(fp, buffer, args);
    fflush(fp);
    va_end(args);

    pthread_mutex_lock(&g_rotate_mutex);
    off_t offset = lseek(fd, 0, SEEK_END);
    if ( offset < 0 ){
            //忽略此错误
     } else {
       //大于64m，则回滚
       if ( offset >=  0x4000000) {
            rotate_log(fd,filename);
       }
     }
   pthread_mutex_unlock(&g_rotate_mutex);
}

// use addr2line to determine function address
// example: addr2line -e prog 0x8078b50
void dump_frame_abort(int level, const char *file, int line, const char *function, char *filename, int fd, FILE* fp, const char *msg)
{
	const static int max_bt_size = 128; // we only support 128 level frame, should enough
	void *btarr[max_bt_size];
	int btnum = backtrace(btarr, max_bt_size);
	char **strs = backtrace_symbols(btarr, btnum);

	const static int buf_size = 1024 * 16;
	char buf[buf_size];
	buf[0] = '\0';

	int left = buf_size, i = 0;
	for(;i < btnum && left > 2; i++) {
		strncat(buf, strs[i], left - 2);
		strcat(buf, "\n");
		left -= strlen(strs[i]);
	}
	free(strs);
	log_message(level, file, line, function, filename, fd, fp, "ASSERT[%s] failed! Frame:\n%s", msg, buf);

	abort();
}
