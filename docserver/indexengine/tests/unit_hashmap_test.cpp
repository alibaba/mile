#include "def.h"
extern "C"
{
#include "../src/common/mem.h"
#include "../src/common/hashmap.h"
}


















TEST(HASHMAP_TEST, HASHMAP_TEST){
	MEM_POOL_PTR pMemPool = mem_pool_init(M_1M);

	uint32 data = 255;
	void* value;
	
	struct low_data_struct** ldata_array = (struct low_data_struct**) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct*) * 10);
	ldata_array[0] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
	ldata_array[0]->len = 4;
	ldata_array[0]->data = mem_pool_malloc(pMemPool, 4);
	*(uint32*)(ldata_array[0]->data) = 20;

	struct hashmap* map = init_hashmap(pMemPool, 1023);

	hashmap_put(map, ldata_array, 1, &data,1);

	value = hashmap_get(map, ldata_array, 1);
	ASSERT_EQ(*(uint32*)value, data);	

}
