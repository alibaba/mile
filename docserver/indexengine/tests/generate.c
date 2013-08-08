#include <stdio.h>
#include <string.h>
#include <time.h>

int main(int argc, char *argv[])
{
	if(argc < 2)
	{
		fprintf(stderr, "file filename number\n");
		exit(1);
	}
	FILE *f = fopen(argv[1], "w");
	long num = atoi(argv[2]);

	long i,j;
	char temp[50];
	char data[4096];

	srand((unsigned int)time(NULL));

	for(i=0;i<num;i++)
	{
		//int num0 = rand()%300000;
		memset(data, 0, sizeof(int));
		strcpy(data, "insert#0 ");

		sprintf(temp, "%ld", rand()%num);
		strcat(data, temp);
		strcat(data, ":");
		sprintf(temp, "%ld", rand()%num);
		strcat(data, temp);
		strcat(data, "\n");
		fwrite(data, strlen(data), 1, f);
	}
	return 0;
}
