#include <signal.h>
#include <unistd.h>
#include <getopt.h>
#include <string.h>
#include <time.h>
#include "common/mem.h"
#include "common/def.h"
#include "storage/docdb/config_parser.h"


void print_help()
{
	fprintf(stderr, "config_file pid_file load table_name sid segment_dir\n"
					"DESCRIPTION:load segment\n"
					"config_file pid_file unload table_name sid\n"
					"DESCRIPTION:unload segment\n"
					"config_file pid_file ensure_index table_name field_name index_type data_type\n"
					"DESCRIPTION:add one field index\n"
					"config_file pid_file get_segment_time tid filename\n"
					"DESCRIPTION:get all segments ctime of the table\n"
					"config_file pid_file set_current_sid tid sid\n"
					"DESCRIPTION:set current segment id\n"
					"config_file pid_file compress tid sid\n"
					"DESCRIPTION:compress segment\n");
	return;
}

int main(int argc, char* argv[])
{
	MEM_POOL* mem_pool = mem_pool_init(MB_SIZE);
	char cmd_file[FILENAME_MAX_LENGTH];
	int pid;
	int signo = 0;
	if(argc < 2)
	{
		print_help();
		exit(-1);
	}
	struct db_conf* conf = config_parser(argv[1], mem_pool);

	//获取进程号
	FILE* pid_fp = NULL;
	if((pid_fp = fopen(argv[2],"r")) == NULL)
	{
		fprintf(stderr,"文件打开失败 %p",pid_fp);
		exit(-1);
	}
	char pid_str[50];
	memset(pid_str,0,sizeof(pid_str));
	if( fgets(pid_str,50,pid_fp) == NULL ) {
		fprintf(stderr,"read pid failed");
		exit(-1);
	}
	char* token = strtok(pid_str,"\r\n");
	pid = atoi(token);
	fclose(pid_fp);

	//拼接
	memset(cmd_file,0,sizeof(cmd_file));
	sprintf(cmd_file,"%s/cmd.dat",conf->storage_dirs.strs[0]);

	//打开命令文件
	FILE* cmd_fp = fopen(cmd_file,"w");
	

	if(strcmp(argv[3],"load") == 0 && argc ==7)
	{
		fprintf(cmd_fp,"%s %s %s %s",argv[3],argv[4],argv[5],argv[6]);
		signo = 40;
	}
	else if(strcmp(argv[3],"compress") == 0 && argc == 6)
	{
		fprintf(cmd_fp,"%s %s %s",argv[3],argv[4],argv[5]);
		signo = 40;
	}
	else if(strcmp(argv[3],"unload") == 0 && argc ==6)
	{
		fprintf(cmd_fp,"%s %s %s",argv[3],argv[4],argv[5]);
		signo = 40;
	}
	else if(strcmp(argv[3],"set_current_sid") == 0 && argc == 6)
	{
		fprintf(cmd_fp,"%s %s %s",argv[3],argv[4],argv[5]);
		signo = 40;
	}
	else if(strcmp(argv[3],"get_segment_time") == 0 && argc ==6)
	{
		fprintf(cmd_fp,"%s %s %s",argv[3],argv[4],argv[5]);
		signo = 40;
	}
	else if(strcmp(argv[3],"ensure_index") == 0 && argc == 8)
	{
		fprintf(cmd_fp,"%s %s %s %s %s",argv[3],argv[4],argv[5],argv[6],argv[7]);
		signo = 40;
	}
	else
	{
		fprintf(stderr,"不支持的命令:%s\n",argv[3]);
		print_help();
		fclose(cmd_fp);
		exit(-1);
	}
	
	//关闭文件
	fclose(cmd_fp);

    //发送信号
    kill(pid,signo);
   
	return 0;
}



