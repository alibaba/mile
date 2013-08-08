#include<stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/time.h>

#define SERVPORT 18518
#define MAXDATASIZE 100 
#define SIZE 10

struct column_data{
        unsigned int id;
        char len;
        int value;
};

struct insert_packet{
        unsigned int len;
        unsigned char ver_major;
        unsigned char ver_minor;
        unsigned short type;
        unsigned int id;

        unsigned int timeout;
        unsigned int tid;
        unsigned int count;

        struct column_data data[SIZE];
};

void init_packet(struct insert_packet *packet)
{
        int i;

        packet->len = htonl(sizeof(struct insert_packet));
        packet->ver_major = 0;
        packet->ver_minor = 0;
        packet->type = htons(0x2101);
        packet->id = 0;

        packet->timeout = htonl(3000);
        packet->tid = 0;
        packet->count = htonl(10);

        for(i=0; i<SIZE; i++)
        {
                packet->data[i].id = htonl(i);
                packet->data[i].len = 4;
                packet->data[i].value = htonl(0x01 + i);
        }

        packet->data[9].len = 8;//htonl(8);     
}


static long long get_userspace_time() {
        struct timeval time;
        gettimeofday(&time, NULL);
        return (time.tv_sec * 1000000 + time.tv_usec)/1000;
}



int main(int argc, char *argv[]){
        int sockfd, recvbytes;
        char buf[MAXDATASIZE];
        struct hostent *host;
        struct sockaddr_in serv_addr;
        struct insert_packet packet;
        int i = 0;
        long long time;
        long count = 10000000;//100000;

        if (argc < 2) {
                fprintf(stderr,"Please enter the server's hostname!\n");
                exit(1);
        }


        init_packet(&packet);


        if((host=gethostbyname(argv[1]))==NULL) {
                perror("gethostbyname");
                exit(1);
        }

        if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) == -1){
                perror("socket");
                exit(1);
        }

retry:
        serv_addr.sin_family=AF_INET;
        serv_addr.sin_port=htons(SERVPORT);
        serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
        //>sin_addr.s_addr = INADDR_ANY;
        bzero(&(serv_addr.sin_zero),8);

        printf("connect.\n");
        if (connect(sockfd, (struct sockaddr *)&serv_addr, \
                sizeof(struct sockaddr)) == -1) {
                perror("connect");
                exit(1);
        }

#if 0 

        printf("unconnect.\n");
        serv_addr.sin_family=AF_UNSPEC;

        if (connect(sockfd, (struct sockaddr *)&serv_addr, \
                sizeof(struct sockaddr)) == -1) {
                perror("connect");
                exit(1);
        }

        goto retry;
#endif
        time = get_userspace_time();
        printf("now time --> %lu\n",time);
send:
        //printf("send data.\n");

        if (send(sockfd, &packet, sizeof(struct insert_packet), 0) == -1){
        //if (send(sockfd, "Hello", sizeof(struct insert_command_packet), 0) == -1){
                perror("send");
                close(sockfd);
                exit(1);
        }

        if ((recvbytes=recv(sockfd, buf, MAXDATASIZE, 0)) ==-1) {
                perror("recv");
                exit(1);
        }

        buf[recvbytes] = '\0';
        if(!(i%1000))printf("Received(count:%d)\t%d: %s;  length:%d\n",recvbytes,i,buf,sizeof(struct insert_packet));

#if 1 
        //sleep(1);

        if(i++>count){
                close(sockfd);
                time = get_userspace_time() - time;
                printf("packet %d ; time %d second\n",count,time/1000);
                return 0;
        }


        goto send;

#endif
        close(sockfd);

        return 0;

