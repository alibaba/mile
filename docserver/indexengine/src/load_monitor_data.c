#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "time.h"



int main(int argc, char* argv[])
{
	FILE* data_fp;
	FILE* tv_fp;
	FILE* cmd_fp;
	char buf[1024];
	char str[1024];
	char tv[128];
	char* token;
	char fids[10][100];
	char cmd[256];
	int pos;
	int server_flag;
	if(argc < 1)
	{
		fprintf(stderr,"%s:monitor file path",argv[0]);
		return -1;
	}

	data_fp = fopen(argv[1],"r");
	if(data_fp == NULL)
	{
		fprintf(stderr,"can not open file %s",argv[1]);
		return -1;
	}
 
	cmd_fp = fopen("monitor.dat","w+");
	if(cmd_fp == NULL)
	{
		fprintf(stderr,"can not open file");
		return -1;
	}

	memset(buf,0,sizeof(buf));

	//从文件中读取的可能是
	//./serviceIn/accordercore/accordercore-20-4/invokeCount@FIELD_1/2011-03-30.txt
	//./serviceIn/accordercore/AccOrderQueryService/queryByPartnerIdOrderTypeAndOutOrderNos/invokeTime@FIELD_0/2011-04-15.txt

	//monitorpoint  app  Server  field1  field2  field3  filed4  type  value  time
	while(fgets(buf,1024,data_fp))
	{
		pos = 0;
		server_flag = 0;
		memset(str,0,sizeof(str));
		memset(cmd,0,sizeof(cmd));
		memset(fids,0,sizeof(fids));

		strcpy(str,buf);
		
		//解析出.号
		token = strtok(str,"/");

		//解析monitorpoint，不能为空值
		token = strtok(NULL,"/");
		strcpy(fids[pos++],token);

		//解析app，不能为空值
		token = strtok(NULL,"/");
		strcpy(fids[pos++],token);

		while((token = strtok(NULL,"/")) != NULL&&pos<8)
		{
			
			if(strstr(token,fids[1])== NULL && server_flag == 0)
			{
				pos++;
				server_flag++;
			}

			if(strstr(token,"@FIELD") != NULL)
			{
				pos = 7;
				*(token+(strstr(token,"@FIELD")-token)) = '\0';
			}
			

			strcpy(fids[pos++],token);
			server_flag++;
		}

		char* file_name = strtok(buf,"\r\n");
		if((tv_fp = fopen(file_name,"r")) == NULL)
		{
			fprintf(stderr,"can not open file %s",buf);
			return -1;
		}

		time_t time_point;
		float value;
		while(fgets(tv,128,tv_fp))
		{
			if(sscanf(tv,"%ld=%f",&time_point,&value) != 2)
			{
				break;
			}
		
			//构造命令
			strcpy(cmd,"insert#0 ");

			for(pos = 0;pos<8;pos++)
			{
		   		if(*fids[pos] == '\0')
		   		{
			 		strcat(cmd,"null");
		   		}
				else
				{
					strcat(cmd,fids[pos]);
				}
				strcat(cmd,":");
			}

			char temp[30];
			memset(temp,0,sizeof(temp));
			sprintf(temp,"%lu:",time_point);
                        strcat(cmd,temp);


			memset(temp,0,sizeof(temp));
			sprintf(temp,"%f\n",value);
            strcat(cmd,temp);
			fputs(cmd,cmd_fp);
		}	

	}
	
	return 0;
}

