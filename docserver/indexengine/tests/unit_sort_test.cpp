#include "def.h"
#include <iostream>
#include <algorithm>
#include <vector>
extern "C"
{
#include "../../indexengine/src/include/hyperindex_sort.h"
}


using namespace std;





struct compareClass{
	struct order_column_array* order_cols;
	enum field_types* fields_type_array;
	
	bool operator() (struct result_data_elem *p, struct result_data_elem *q)
	{
		int8 cmp_res;
		cmp_res = compare_result_data_elem(p, q, order_cols, fields_type_array);

		if(cmp_res == -1)
		{
			return true;
		}else{
			return false;
		}
	}
};










bool check_limit_ordery_result(struct query_result_dataset* result, vector<struct result_data_elem*> answer, struct order_column_array* order_cols, enum field_types* fields_type_array)
{
	uint32 i;
	vector<struct result_data_elem*>::iterator it;


	for(it = answer.begin(), i = 0; it != answer.end() && i < result->elems_num; ++it, i++)
	{
		//print_result_data_elem(result->data_array[i], fields_type_array);
		//print_result_data_elem(*it, fields_type_array);
		//printf("\n");
		
		if(result->data_array[i]->docid == (*it)->docid)
		{
			continue;
		}
		if(compare_result_data_elem(*it, result->data_array[i], order_cols, fields_type_array) != 0)
		{
			//return false;
		}
		
	}
	return true;

}






TEST(SORT_TEST, COMPARE_LD){
	int8 result;
	struct low_data_struct lda, ldb;

	float fa, fb;
	lda.data = &fa;
	ldb.data = &fb;
	lda.len = sizeof(float);
	ldb.len = sizeof(float);
	fa = 1.0;
	fb = 1.1;
	result = compare_ld(&lda, &ldb, HI_TYPE_FLOAT);
	ASSERT_EQ(result, -1);
	result = compare_ld(&ldb, &lda, HI_TYPE_FLOAT);
	ASSERT_EQ(result, 1);
	fb = fa;
	result = compare_ld(&lda, &ldb, HI_TYPE_FLOAT);
	ASSERT_EQ(result, 0);


	int32 la, lb;
	lda.data = &la;
	ldb.data = &lb;
	lda.len = sizeof(int32);
	ldb.len = sizeof(int32);	
	la = -100;
	lb = 1000;
	result = compare_ld(&lda, &ldb, HI_TYPE_LONG);
	ASSERT_EQ(result, -1);
	result = compare_ld(&ldb, &lda, HI_TYPE_LONG);
	ASSERT_EQ(result, 1);
	lb = la;
	result = compare_ld(&lda, &ldb, HI_TYPE_LONG);
	ASSERT_EQ(result, 0);


	double da, db;
	lda.data = &da;
	ldb.data = &db;
	lda.len = sizeof(double);
	ldb.len = sizeof(double);	
	da = 10.22;
	db = 1000.0;
	result = compare_ld(&lda, &ldb, HI_TYPE_LONG);
	ASSERT_EQ(result, -1);
	result = compare_ld(&ldb, &lda, HI_TYPE_LONG);
	ASSERT_EQ(result, 1);
	db = da;
	result = compare_ld(&lda, &ldb, HI_TYPE_LONG);
	ASSERT_EQ(result, 0);


	char sa[100] = "abc";
	char sb[100] = "abc";
	lda.data = &sa;
	ldb.data = &sb;
	lda.len = strlen(sa);
	ldb.len = strlen(sb);
	result = compare_ld(&lda, &ldb, HI_TYPE_STRING);
	ASSERT_EQ(result, 0);
	strcpy(sa, "aaa");
	result = compare_ld(&lda, &ldb, HI_TYPE_STRING);
	ASSERT_EQ(result, -1);
	result = compare_ld(&ldb, &lda, HI_TYPE_STRING);
	ASSERT_EQ(result, 1);

}







TEST(SORT_TEST, COMPARE){
	int8 result;

	float fa, fb;
	fa = 1.0;
	fb = 1.1;
	result = compare(&fa, &fb, HI_TYPE_FLOAT);
	ASSERT_EQ(result, -1);
	result = compare(&fb, &fa, HI_TYPE_FLOAT);
	ASSERT_EQ(result, 1);
	fb = fa;
	result = compare(&fa, &fb, HI_TYPE_FLOAT);
	ASSERT_EQ(result, 0);


	int32 la, lb;
	la = -100;
	lb = 1000;
	result = compare(&la, &lb, HI_TYPE_LONG);
	ASSERT_EQ(result, -1);
	result = compare(&lb, &la, HI_TYPE_LONG);
	ASSERT_EQ(result, 1);
	lb = la;
	result = compare(&la, &lb, HI_TYPE_LONG);
	ASSERT_EQ(result, 0);


	uint32 ula, ulb;
	ula = 100;
	ulb = 1000;
	result = compare(&ula, &ulb, HI_TYPE_LONG);
	ASSERT_EQ(result, -1);
	result = compare(&ulb, &ula, HI_TYPE_LONG);
	ASSERT_EQ(result, 1);
	ulb = ula;
	result = compare(&ula, &ulb, HI_TYPE_LONG);
	ASSERT_EQ(result, 0);

	double da, db;
	da = 10.22;
	db = 1000.0;
	result = compare(&da, &db, HI_TYPE_LONG);
	ASSERT_EQ(result, -1);
	result = compare(&db, &da, HI_TYPE_LONG);
	ASSERT_EQ(result, 1);
	db = da;
	result = compare(&da, &db, HI_TYPE_LONG);
	ASSERT_EQ(result, 0);

}





TEST(SORT_TEST, SINGLE_COLUMN_SORT){
	uint32 i;
	uint32 limit = 10000;
	
	MEM_POOL_PTR pMemPool = mem_pool_init(M_1M);


	struct order_column_array *order_cols = (struct order_column_array*) mem_pool_malloc(pMemPool, sizeof(struct order_column_array));
	order_cols->n = 1;
	order_cols->columns = (uint32 *) mem_pool_malloc(pMemPool, sizeof(uint32)*order_cols->n);
	order_cols->columns[0] = 0;
	order_cols->ord_types = (enum order_types*) mem_pool_malloc(pMemPool, sizeof(enum order_types)*order_cols->n);
	order_cols->ord_types[0] = ORDER_TYPE_DESC;
	enum field_types* fields_type_array = (enum field_types*) mem_pool_malloc(pMemPool, sizeof(enum field_types)*order_cols->n);
	fields_type_array[0] = HI_TYPE_SHORT;



	
	struct query_result_dataset* result = init_query_result_dataset(pMemPool, 1, limit);
	vector<struct result_data_elem*> answer;
	vector<struct result_data_elem*>::iterator it;
	struct compareClass comparator;
	struct result_data_elem* data_elem;
	comparator.order_cols = order_cols;
	comparator.fields_type_array = fields_type_array;





	it = answer.begin();
	
	for(i = 0; i < 1000; i++)
	{
		data_elem = init_result_data_elem(pMemPool, 1);
		data_elem->cols_data[0] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		data_elem->docid = i;
		data_elem->cols_data[0]->len = sizeof(int16);	
		data_elem->cols_data[0]->data = mem_pool_malloc(pMemPool, sizeof(int16));
		*((int16 *)(data_elem->cols_data[0]->data)) = rand()%1000;
		
		heap_sort_add(pMemPool, result, order_cols, fields_type_array, data_elem);
		it = answer.insert(it, data_elem);
	}
	

	heap_sort(pMemPool, result, order_cols, fields_type_array);

	sort(answer.begin(), answer.end(), comparator);

	ASSERT_TRUE(check_limit_ordery_result(result, answer, order_cols, fields_type_array));

	mem_pool_destroy(pMemPool);
	
}




TEST(SORT_TEST, MULTI_COLUMN_SORT){
	uint32 i;
	uint32 limit = 10;
	
	MEM_POOL_PTR pMemPool = mem_pool_init(M_1M);


	struct order_column_array *order_cols = (struct order_column_array*) mem_pool_malloc(pMemPool, sizeof(struct order_column_array));
	order_cols->n = 2;
	order_cols->columns = (uint32 *) mem_pool_malloc(pMemPool, sizeof(uint32)*order_cols->n);
	order_cols->ord_types = (enum order_types *) mem_pool_malloc(pMemPool, sizeof(enum order_types)*order_cols->n);
	order_cols->columns[0] = 0;
	order_cols->columns[1] = 0;
	order_cols->ord_types[0] = ORDER_TYPE_ASC;
	order_cols->ord_types[1] = ORDER_TYPE_DESC;
	enum field_types* fields_type_array = (enum field_types*) mem_pool_malloc(pMemPool, sizeof(enum field_types)*order_cols->n);
	fields_type_array[0] = HI_TYPE_SHORT;
	fields_type_array[1] = HI_TYPE_LONG;



	
	struct query_result_dataset* result = init_query_result_dataset(pMemPool, 2, limit);
	vector<struct result_data_elem*> answer;
	vector<struct result_data_elem*>::iterator it;
	struct compareClass comparator;
	struct result_data_elem* data_elem;
	comparator.order_cols = order_cols;
	comparator.fields_type_array = fields_type_array;



	it = answer.begin();
	
	for(i = 0; i < 1000; i++)
	{
		data_elem = init_result_data_elem(pMemPool, 2);
		data_elem->cols_data[0] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		data_elem->cols_data[1] = (struct low_data_struct*) mem_pool_malloc(pMemPool, sizeof(struct low_data_struct));
		data_elem->docid = i;
		data_elem->cols_data[0]->len = sizeof(int16);	
		data_elem->cols_data[0]->data = mem_pool_malloc(pMemPool, sizeof(int16));
		*((int16 *)(data_elem->cols_data[0]->data)) = rand()%1000;
		data_elem->cols_data[1]->len = sizeof(int32);	
		data_elem->cols_data[1]->data = mem_pool_malloc(pMemPool, sizeof(int32));
		*((int32 *)(data_elem->cols_data[1]->data)) = rand();		
		
		heap_sort_add(pMemPool, result, order_cols, fields_type_array, data_elem);
		it = answer.insert(it, data_elem);
	}
	

	heap_sort(pMemPool, result, order_cols, fields_type_array);

	sort(answer.begin(), answer.end(), comparator);

	ASSERT_TRUE(check_limit_ordery_result(result, answer, order_cols, fields_type_array));

	mem_pool_destroy(pMemPool);

}













