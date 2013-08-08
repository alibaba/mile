#include "def.h"
extern "C"
{
#include "../src/common/hash.h"
}
TEST(HASH_TEST, HandleNoneZeroInput)  {
    //hash32Œª
    char str_a[5];
    char str_b[5];
    memset(str_a,0,sizeof(str_a));
    memset(str_b,0,sizeof(str_b));

	struct low_data_struct data_a;
	struct low_data_struct data_b;
    
    //≤‚ ‘π˛œ£
    uint32 a = 14141;
    sprintf(str_a,"%u",a);
	data_a.data = str_a;
	data_a.type = HI_TYPE_SHORT;
    uint64 a_hash = get_hash_value(&data_a);

    uint32 b = 12414;
    sprintf(str_b,"%u",b);
	data_b.data = str_b;
	data_b.type = HI_TYPE_SHORT;
    uint64 b_hash = get_hash_value(&data_b);  
    
    ASSERT_NE(a_hash,b_hash);

	//≤‚ ‘ø’÷µ
	data_a.len = 0;
	data_b.len = 0;

	data_a.type = HI_TYPE_STRING;
	a_hash = get_hash_value(&data_a);
	data_b.type = HI_TYPE_STRING;
	b_hash = get_hash_value(&data_b);

	ASSERT_EQ(a_hash,b_hash);
}

