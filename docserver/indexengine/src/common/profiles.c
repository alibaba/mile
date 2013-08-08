/*
 * =====================================================================================
 *
 *       Filename:  hi_profiles.c
 *
 *    Description:  记录每步的查询时间，超过阈值，则打印到日志里
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


#include "profiles.h"


static struct profiles* g_profiles;

uint64_t get_time() {
	struct timeval time;
	gettimeofday(&time, NULL);
	return time.tv_sec * 1000000 + time.tv_usec;
}

static struct entry* get_entry(char* message,struct entry* parent,struct entry* first,MEM_POOL* mem_pool)
{
	struct entry * e = NULL;
	e = (struct entry*)mem_pool_malloc(mem_pool,sizeof(struct entry));
	memset(e,0,sizeof(struct entry));

	e->parent = parent;
	e->first = (first==NULL)?e:first;
	e->btime = (first == NULL) ? 0 : first->stime;
	e->stime = get_time();
	e->etime = 0;
	e->entry_count = 0;
	strcpy(e->message,message);
	INIT_LIST_HEAD(&e->entry_list);
	INIT_LIST_HEAD(&e->entry_list_h);
	return e;
}

static long get_start_time(struct entry* e) 
{
	return (e->btime > 0) ? (e->stime - e->btime) : 0;
}


static long get_duration(struct entry* e) 
{
	if (e->etime >= e->stime)
		return (e->etime - e->stime);
	else
		return -1;
}

static long get_my_duration(struct entry* e) 
{
    long td = get_duration(e);

	if (td < 0)
		return -1;
	else if (list_empty(&e->entry_list_h))
		return td;
	else 
	{
	    struct entry* sub_e;
		
		/*遍历*/
		list_for_each_entry(sub_e,&e->entry_list_h,entry_list){
			td -= get_duration(sub_e);
		}
		return (td < 0) ? -1 : td;
	 }
}

int16_t is_released(struct entry* e) {
	if(e->etime > 0)
		return 1;
	return 0;
}

static double get_percentage(struct entry* e) 
{
	 double pd = 0;
	 double d = get_my_duration(e);

	 if (!list_empty(&e->entry_list_h))
		pd = get_duration(e);
	 else if (e->parent && is_released(e->parent))
		pd = get_duration(e->parent);

	 if (pd > 0 && d > 0)
		return d / pd;

	 return 0;
}


double get_percentage_of_total(struct entry* e) {
	 double fd = 0;
	 double d = get_duration(e);

     if (e->first && is_released(e->first))
	 fd = get_duration(e->first);

	 if (fd > 0 && d > 0)
		return d / fd;

	 return 0;
}

void release(struct entry* e) {
    e->etime = get_time();
}


void do_subentry(char* message,struct entry* e) {
	MEM_POOL* mem_pool =(MEM_POOL*)pthread_getspecific(g_profiles->mem_key);
	struct entry* sub_e = get_entry(message,e,e->first,mem_pool);
	//入栈
	list_add(&sub_e->entry_list,&e->entry_list_h);
	e->entry_count++;
}

struct entry *get_unreleased_entry(struct entry* e) {
	struct entry  *se;
	if(list_empty(&e->entry_list_h)) 
		return NULL;

	se = list_entry((e->entry_list_h).next, typeof(*se), entry_list);
	if(is_released(se))
		return NULL;
	else
		return se;
}

void to_string(struct entry* e,char* pre1, char* pre2,char* buffer, uint32_t buf_size)
{
	uint16_t i = 0;
	uint32_t len, str_len = strlen( buffer );
	assert( str_len < buf_size );
	struct entry* ent;
	const uint32_t pre_size = 128;
	char pre_1[pre_size];
	char pre_2[pre_size];
	const uint32_t temp_size = 256;
	char temp[256];

	memset(temp,0,sizeof(temp));

	strncat(buffer,pre1, buf_size - str_len - 1);
	len = strlen( pre1 );
	if( len + str_len + 1 >= buf_size)
		return;
	else
		str_len += len;

	if (is_released(e))
	{
		len = snprintf(temp, temp_size, "%lu [%lu(us), %lu(us), %.2f%%, %.2f%%] - %s", get_start_time(e), get_duration(e), get_my_duration(e), get_percentage(e) * 100, get_percentage_of_total(e) * 100, e->message);
		if( len > temp_size - 1 ) {
			temp[temp_size - 1] = '\0';
			len  = temp_size - 1;
		}

		strncat(buffer,temp, buf_size - str_len - 1);

		if( len + str_len + 1 >= buf_size )
			return;
	}
	else
		log_warn("[UNRELEASED]");


	list_for_each_entry(ent,&e->entry_list_h,entry_list)
	{
		// 1 = strlen( "\n" )
		if( strlen( buffer ) + 1 + 1 > buf_size )
			return;
		else
			strcat(buffer,"\n");

		memset(pre_1,0,sizeof(pre_1));
		memset(pre_2,0,sizeof(pre_2));
		strncpy(pre_1,pre2, pre_size - 1);
		strncpy(pre_2,pre2, pre_size - 1);
		if (i == 0)
			to_string(ent,strcat(pre_1,"+---"), strcat(pre_2, "|  "),buffer, buf_size);
		else if (i == (e->entry_count- 1))
			to_string(ent,strcat(pre_1,"`---"), strcat(pre_2, "    "),buffer, buf_size);
		else
			to_string(ent,strcat(pre_1,"+---"), strcat(pre_2 ,"|   "),buffer, buf_size);

		i++;
	}
	return;
}



void to_string_root(struct entry* e)
{
        char pre1[128];
        char pre2[128];
        char temp[1024];
        memset(pre1,0,sizeof(pre1));
        memset(pre2,0,sizeof(pre2));
        memset(temp,0,sizeof(temp));
        to_string(e,pre1,pre2,temp, sizeof(temp) );
        log_warn("\n%s",temp);
        return;
}


/*以下是提供的接口*/
int32_t init_profile(uint16_t threshold,MEM_POOL* mem_pool)
{
	g_profiles = (struct profiles*)mem_pool_malloc(mem_pool,sizeof(struct profiles));
	memset(g_profiles,0,sizeof(struct profiles));

	g_profiles->status = 0;
	g_profiles->threshold = threshold;
	
	pthread_key_create(&g_profiles->entry_key, NULL);
	pthread_key_create(&g_profiles->mem_key, NULL);
	return 0;
}


void reset_profile() {
	if(pthread_getspecific(g_profiles->entry_key) != NULL) {
		mem_pool_destroy((MEM_POOL*)pthread_getspecific(g_profiles->mem_key));
		pthread_setspecific(g_profiles->entry_key, NULL);
		pthread_setspecific(g_profiles->mem_key,NULL);
	}
}


void start_profile(char* description)
{
	struct entry* e = NULL;
	if(pthread_getspecific(g_profiles->entry_key) != NULL){
		reset_profile();
	}

	MEM_POOL* entry_mem_pool = mem_pool_init(KB_SIZE);
    e = get_entry(description,NULL,NULL,entry_mem_pool);
	
	//设置线程局部变量
	pthread_setspecific(g_profiles->entry_key, (void *)e);
	pthread_setspecific(g_profiles->mem_key,(void *)entry_mem_pool);
}

static struct entry* get_current_entry()
{
    struct entry *se = NULL;
	struct entry *e = NULL;
	se = (struct entry*)pthread_getspecific(g_profiles->entry_key);

    if (se != NULL) {
	  do {
		    e = se;
		    se = get_unreleased_entry(e);
	     } while (se != NULL);
    }

	return e;
}


void end_profile()
{
    struct entry *ce = get_current_entry();
	if(ce != NULL)
		release(ce);
}


void stop_profile()
{
    end_profile();
}


void begin_profile(char* description)
{
	struct entry* ce = get_current_entry();
	if(ce != NULL)
		do_subentry(description,ce);
}


uint64_t get_duration_profile()
{
    struct entry *se = NULL;
	
	se = (struct entry*)pthread_getspecific(g_profiles->entry_key);

	get_duration(se);
    return 0; 

}


void dump_profile() {
	struct entry* ent = NULL;
	end_profile();
	ent = (struct entry*)pthread_getspecific(g_profiles->entry_key);
	if (ent != NULL && get_duration(ent) >= g_profiles->threshold&& g_profiles->status == 1)
		to_string_root(ent);
}

struct profiles* get_globle_profile()
{
	return g_profiles;
}


