// this should run under mile storage/monitor dir
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <time.h>
#include <limits>
#include <sys/mman.h>
#include <cmath>

#define err_exit(fmt, ...) do { fprintf(stderr, fmt "\n", ## __VA_ARGS__); exit(EXIT_FAILURE); } while (false)

static int g_seg_records_num = 10000000;

void get_seg_time(std::pair< time_t, time_t > *tp, std::string dir_path, int scale_value = 1)
{
	std::string file_path = dir_path + "/filter_store.dat";

	int fd = open(file_path.c_str(), O_RDONLY);

	if (fd < 0) {
		err_exit("open %s failed, errno %d", file_path.c_str(), errno);
	}

	struct stat st;
	fstat(fd, &st);
	if (st.st_size < g_seg_records_num * 8)
		err_exit("wrong size: %s", file_path.c_str() );

	uint64_t created_at = std::numeric_limits< uint64_t >::max();
	uint64_t updated_at = 0;
	uint64_t *times = (uint64_t *)mmap(NULL, st.st_size, PROT_READ, MAP_SHARED, fd, 0);

	for (int i = 0; i < g_seg_records_num; i++) {
		if (times[i] > 0 && times[i] < created_at)
			created_at = times[i];
		if (times[i] > updated_at)
			updated_at = times[i];
	}
	if (created_at > updated_at)
		created_at = updated_at;

	if (scale_value > 0) {
		tp->first = created_at / scale_value;
		tp->second = updated_at / scale_value;
	}
	else {
		tp->first = created_at * ( -scale_value);
		tp->second = updated_at * ( -scale_value);
	}

	munmap(times, st.st_size);
	close(fd);
}

void usage(const char *prog_name = NULL)
{
	fprintf(stderr, "%s [-d segment_dir] [-f field_name] [-s scale] [-n records_num_per_seg] [-h]\n", prog_name == NULL ? "" : prog_name);
}

int main(int argc, char *argv[])
{

	std::string dir_path = ".";
	std::string field_name = "gmt_occur";
	int scale = 3;
	int scale_value = 1;

	int opt;

	while ( (opt = getopt(argc, argv, "hd:f:s:n:")) != -1) {
		switch (opt) {
		case 'd':
			dir_path = optarg;
			break;
		case 'f':
			field_name = optarg;
			break;
		case 's':
			scale = atoi(optarg);
			break;
		case 'n':
			g_seg_records_num = atoi(optarg);
			break;
		case 'h':
		default:
			usage();
			exit(EXIT_FAILURE);
		}
	}

	int fd = open( (dir_path + "/meta.dat").c_str(), O_RDWR);
	if (fd < 0) {
		usage();
		err_exit("open meta.dat failed, errno %d.\nThis prog should run under dumped segment data dir, or specify this dir by -d", errno);
	}
	for (int i = 0; i < std::abs(scale); i++)
		scale_value *= 10;
	if (scale < 0)
		scale_value = -scale_value;

	std::pair< time_t, time_t > time;
	get_seg_time(&time, (dir_path + "/" + field_name).c_str(), scale_value);
	printf("get seg created time %lu, updated time %lu\n", time.first, time.second);

	if (write(fd, &time.first, sizeof( time.first ) ) <= 0)
		err_exit("write wrong, errno %d", errno);
	if (write(fd, &time.second, sizeof( time.second) ) <= 0)
		err_exit("write wrong, errno %d", errno);

	close(fd);
}

