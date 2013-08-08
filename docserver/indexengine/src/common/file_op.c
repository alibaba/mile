#include "file_op.h"

int32_t ensure_file_size(int32_t fd,int32_t size)
{
   struct stat s;
   if(fstat(fd, &s) < 0) {
	 log_error("fstat error, {%s}", strerror(errno));
	 return -1;
   }
   if(s.st_size < size) {
	 if(ftruncate(fd, size) < 0) {
	   log_error("ftruncate file to size: [%u] failed. {%s}", size,
				 strerror(errno));
	   return -1;
	 }
   }

   return 0;
 }


int32_t open_file(const char *file_name,int32_t flag)
{
	int32_t fd = -1;
    fd =open(file_name, flag, 0600);
	if(fd < 0) {
		log_error("open file [%s] failed: %s", file_name, strerror(errno));
		return -1;
	}
	return fd;
}


uint64_t get_file_size(uint32_t fd)
{
	struct stat  s;
	if(fstat(fd, &s) < 0) {
	 log_error("fstat error, {%s}", strerror(errno));
	 return -1;
   }
	
   return s.st_size;
}


off_t get_position(int32_t fd)
{
   return lseek(fd, 0, SEEK_CUR);
}


int32_t close_file(int32_t fd)
{
	if(close(fd) == -1) {
       log_error("close file [%s] failed",strerror(errno));
	   return -1;
    }
    return 0;
}

int32_t mkdirs(char *dir_path) 
{
    struct stat stats;
    if (stat (dir_path, &stats) == 0 && S_ISDIR (stats.st_mode)) 
    {
		return 0;
    }
    mode_t umask_value = umask (0);
    umask (umask_value);
    mode_t mode = (S_IRWXUGO & (~ umask_value)) | S_IWUSR | S_IXUSR;
        
    char *slash = dir_path;
    while (*slash == '/')
    	 slash++;
        
    while (1)
    {
    	slash = strchr (slash, '/');
    	if (slash == NULL)
    	        break;
    	        
        *slash = '\0';
        int ret = mkdir(dir_path, mode);
        *slash++ = '/';
        if (ret && errno != EEXIST) {
             return -1;
        }
            
        while (*slash == '/')
    	     slash++;
    }
    if (mkdir(dir_path, mode)) {
		 log_error("mkdir file [%s] failed",strerror(errno));
         return -1;
    }
   return 0;
}


int32_t cp_dir(const char* filename,const char* new_filename)
{
	char cmd[1024];

	memset(cmd,0,sizeof(cmd));

	sprintf(cmd,"cp -r %s %s",filename,new_filename);

	return system(cmd);

}


int32_t is_dir(const char *dir_path)
{
	struct stat stats;
    if (lstat (dir_path, &stats) == 0 && S_ISDIR (stats.st_mode))
        return 1;
    return 0;
}


int32_t mv_file(const char* filename,const char* newdir,uint16_t nid)
{
	char newfilename[FILENAME_MAX_LENGTH];
	const char* oldname;
	memset(newfilename,0,sizeof(newfilename));

	oldname = strrchr(filename,'/')+1;

	//拼接新文件名
	sprintf(newfilename,"%s/%s_%04u",newdir,oldname,nid);

	if(rename(filename,newfilename) < 0)
	{
		log_error("rename file [%s] failed",strerror(errno));
		return -1;
	}
	return 0;
}

int32_t link_file(const char* filename,const char* newdir,uint16_t nid)
{
	char newfilename[FILENAME_MAX_LENGTH];
	const char* oldname;
	memset(newfilename,0,sizeof(newfilename));

	oldname = strrchr(filename,'/')+1;

	//拼接新文件名
	sprintf(newfilename,"%s/%s.%04u",newdir,oldname,nid);

	if(link(filename,newfilename) < 0)
	{
		log_error("rename file [%s] failed",strerror(errno));
		return -1;
	}
	return 0;
}

int32_t set_file_noblock(int32_t fd)
{
	int32_t flags;
	flags = fcntl(STDOUT_FILENO, F_GETFL, 0); /* 将标准输出设置为非阻塞形式 */
  	if(flags == -1){
	  log_error("set file no block[%s] failed",strerror(errno));
	  return -1;
  	}
 	flags |= O_NONBLOCK; /* 设置非阻塞标志 */
  	if(fcntl(STDOUT_FILENO, F_SETFL, flags) == -1){ /* 重新设置文件的状态标志 */
	  log_error("set file no block[%s] failed",strerror(errno));
	  return -1;
  	}
	return 0;
}


int32_t is_directory(const char *dir_path)
{
   struct stat stats;
   if (lstat (dir_path, &stats) == 0 && S_ISDIR (stats.st_mode))
        return 1;
   return 0;
}


void* get_mmap_memory(char* filename,uint32_t size)
{
	int fd = 0;
	void* mem_mmaped = NULL;
	fd = open_file(filename,O_RDWR | O_CREAT);
	if(fd < 0) {
		 log_error("%s 的文件创建不成功",filename);
		 return NULL;
	}

	//补全文件
	if(ensure_file_size(fd,size) < 0)
	{
		 log_error("%s 文件大小补全失败%u",filename,size);
		 close_file(fd);
		 return NULL;
	}
	
	mem_mmaped = mmap(0, size, PROT_READ|PROT_WRITE, MAP_SHARED|MAP_POPULATE, fd, 0); 

	if(mem_mmaped == MAP_FAILED){
	   log_error("mamap failed filename:%s size:%u error:%s",filename,size,strerror(errno));
	   mem_mmaped = NULL;

	}
	
	close_file(fd);

	return mem_mmaped;
}

void *alloc_file_memory(const char *filename, uint32_t size)
{
	log_debug( "alloc_file_memory %s", filename );

	int fd = open_file(filename, O_RDWR | O_CREAT);
	if( fd < 0 ) {
		return NULL;
	}

	// MAP_ANONYMOUS flag ensure memory initialized to zero
	void *mem = mmap(NULL, size, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0);
	if( MAP_FAILED == mem ) {
		log_error( "mmap failed, size %u, errno %d", size, errno );
		close(fd);
		return NULL;
	}

	// read file content.
	uint32_t done = 0;
	while( done < size ) {
		ssize_t n = read(fd, (char *)mem + done, size - done);
		if( n < 0 && errno != EINTR ) {
			log_error( "read failed, path %s, errno %d", filename, errno );
			munmap(mem, size);
			close(fd);
			return NULL;
		} else if( 0 == n ) { // EOF
			break;
		} else if( n > 0 ) {
			done += n;
		}
	}

	close(fd);
	return mem;
}

static ssize_t while_write( int fd, void *buf, size_t len )
{
	ssize_t done = 0;
	while( (size_t)done < len ) {
		ssize_t n = write(fd, (char *)buf + done, len - done);
		if( n < 0 && errno != EINTR ) {
			int backup = errno;
			log_error( "write to file failed, fd %d, errno %d", fd, errno );
			errno = backup;
			return -1;
		}
		else {
			done += n;
		}
	}

	return done;
}

static ssize_t limited_write( int fd, void *buf, size_t len, uint32_t limit )
{
	if( 0 == limit )
		return while_write( fd, buf, len );

	uint64_t start_time = get_time_usec() / 1000; // ms
	uint32_t step_size = getpagesize() * 256; // 256 PAGE
	ssize_t done = 0;
	while( (size_t)done < len ) {
		uint64_t now = get_time_usec() / 1000;
		while( done > 0 && now >= start_time && done / (now - start_time) > limit / 1000) {
			usleep(2000);
			now = get_time_usec() / 1000;
		}


		uint32_t step = (step_size > len - done) ? (len - done) : step_size;
		if( while_write( fd, (char *)buf + done, step ) < 0 )
			return -1;
		done += step;
	}
	return done;
}

int flush_memory(const char *filename, void *mem, uint32_t size, uint32_t write_limit)
{
	log_debug( "flush_memory %s", filename );

	if( NULL == mem )
		return 0;

	int fd = open_file(filename, O_WRONLY | O_CREAT | O_SYNC ); // O_SYNC: flush to disk
	if( fd < 0 ) {
		return -1;
	}

	int rc = 0;
	if( limited_write( fd, mem, size, write_limit ) < 0 ) {
		log_error( "write to file %s failed", filename );
		rc = -1;
	}

	close(fd);
	return rc;
}

int switch_mmaped_file(const char *filename, void *mem, uint32_t size)
{
	log_debug( "switch_mmaped_file %s", filename );

	if( NULL == mem )
		return 0;

	int fd = open_file(filename, O_RDWR | O_CREAT);
	if( fd < 0 ) {
		return -1;
	}

	void *ret = mmap(mem, size, PROT_READ | PROT_WRITE, MAP_SHARED | MAP_POPULATE | MAP_FIXED, fd, 0); 
	if( ret == MAP_FAILED ) {
		log_error( "mmap failed, path %s, fixed mem %p, size %u, errno %d", filename, mem, size, errno );
		close(fd);
		return -1;
	}
	assert(ret == mem);
	close(fd);
	return 0;
}



int32_t traversal_single_deep_childdir(char* dir,traversal_dir_callback func,void* arg)
{
    DIR              *pDir ;
    struct dirent    *ent  ;
    char              childpath[512];
	int32_t ret;

    pDir=opendir(dir);

	if(pDir == NULL)
		return MILE_RETURN_SUCCESS;
	
    memset(childpath,0,sizeof(childpath));
	

    while((ent=readdir(pDir))!=NULL)
    {   

            if(ent->d_type & DT_DIR)
            {   

                    if(strcmp(ent->d_name,".")==0 || strcmp(ent->d_name,"..")==0)
                            continue;

                    sprintf(childpath,"%s/%s",dir,ent->d_name);
					ret = func(childpath,arg);
					if(ret < 0)
						return ret;
            }   
    } 

	return MILE_RETURN_SUCCESS;
}

ssize_t unintr_read(int fd, void *buf, size_t count)
{
	ssize_t done = 0;
	while(done < count) {
		ssize_t n = read(fd, (char *)buf + done, count - done);
		if(n > 0)
			done += n;
		else if(0 == n) // end of file
			break;
		else {
			if(EINTR == errno)
				continue;
			BAKENO_CALL(log_error, "read file %d, to buf %p, count %zd, failed, errno %d", fd, buf, count, errno);
			return n;
		}
	}

	return done;
}

ssize_t unintr_write(int fd, const void *buf, size_t count)
{
	ssize_t done = 0;
	while(done < count) {
		ssize_t n = write(fd, (char *)buf + done, count - done);
		if(n >= 0)
			done +=n;
		else {
			if(EINTR == errno)
				continue;
			BAKENO_CALL(log_error, "write to file %d, buf %p, count %zd, failed, errno %d", fd, buf, count, errno);
			return n;
		}
	}

	return done;
}

ssize_t unintr_pread(int fd, void *buf, size_t count, off_t offset)
{
	ssize_t done = 0;
	while(done < count) {
		ssize_t n = pread(fd, (char *)buf + done, count - done, offset + done);
		if(n > 0)
			done += n;
		else if(0 == n) // end of file
			break;
		else {
			if(EINTR == errno)
				continue;
			BAKENO_CALL(log_error, "pread file %d, to buf %p, count %zd, offset %jd, failed, errno %d",
					fd, buf, count, (intmax_t)offset, errno);
			return n;
		}
	}

	return done;
}

ssize_t unintr_pwrite(int fd, const void *buf, size_t count, off_t offset)
{
	ssize_t done = 0;
	while(done < count) {
		ssize_t n = pwrite(fd, (char *)buf + done, count - done, offset + done);
		if(n >= 0)
			done +=n;
		else {
			if(EINTR == errno)
				continue;
			BAKENO_CALL(log_error, "pwrite to file %d, buf %p, count %zd, offset %jd, failed, errno %d",
					fd, buf, count, (intmax_t)offset, errno);
			return n;
		}
	}

	return done;
}

