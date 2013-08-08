#include "def.h"
extern "C" {
#include "../src/storage/binlog.h"
#include "../src/common/mem.h"
}
#include <iostream>
#include <string>

#define PR(var) std::cout<< #var ": " << var << std::endl;
#define PRB( p, l ) { printf( #p ": " );for(int i_ = 0;i_<l;i_++ ){printf("%02x ", (int)*(((const uint8*)p)+i_));}printf("\n");}

class BinlogTest : public testing::Test {

public:
	virtual void SetUp()
	{
		RemoveDir();
		CreateDir();
		mem_ = mem_pool_init( 10 * 1024 * 1024 );
	}

	virtual void TearDown()
	{
		RemoveDir();
		mem_pool_destroy( mem_ );
	}

	void RemoveDir()
	{
		std::string cmd = std::string( "rm -rf '" ) + g_binlog_dir_ + "'";
		if( ::system( cmd.c_str() ) );
	}

	void CreateDir()
	{
		std::string cmd = std::string( "mkdir -p '" ) + g_binlog_dir_ + "'";
		if( ::system( cmd.c_str() ) );
	}

	MEM_POOL_PTR mem_;
	const static char *g_binlog_dir_;
	const static int max_size_ = 1024 * 100;
};

const char *BinlogTest::g_binlog_dir_ = "_DIR_";

TEST_F(BinlogTest, WritRead)
{
	BL_WRITER_PTR writer = binlog_writer_init( g_binlog_dir_, max_size_, mem_ );
	ASSERT_TRUE( NULL != writer );

	// writer->sync_immediately = 1;
	const int data_len = 1021;
	struct binlog_record *record = create_binlog_record( data_len, mem_ );
	memset( record->data, 'a', data_len );
	
	const int count = 1000;
	for( int32 i = 0; i < count; i++ ) {
		memcpy( record->data, &i, 4 );
		binlog_write_record( writer, record, mem_ );
		// binlog_sync( writer );
	}

	struct binlog_record *read_record = NULL;
	BL_READER_PTR reader = binlog_reader_init_byoffset( g_binlog_dir_, max_size_, 0, NULL, mem_ );
	ASSERT_TRUE( NULL != reader );

	record->time = 0;
	for( int32 i = 0; i < count; i++ ) {
		ASSERT_EQ( 1, binlog_read_record( reader, &read_record, mem_ ) );
		memcpy( record->data, &i, 4 );
		// PR( i );
		// PR( read_record->time );
		read_record->time = 0;
		ASSERT_EQ(0, memcmp( read_record, record, record->len ) );
	}

	ASSERT_EQ( 0, binlog_read_record( reader,&read_record, mem_ ) );
	reader->writer = writer;
	ASSERT_EQ( 0, binlog_read_record( reader,&read_record, mem_ ) );

	ASSERT_EQ( 1, binlog_is_read_all( reader ) );
}

TEST_F(BinlogTest, WriteReadConfirm)
{
	BL_WRITER_PTR writer = binlog_writer_init( g_binlog_dir_, max_size_, mem_ );
	ASSERT_TRUE( NULL != writer );

	const int count = 1000;
	for( int32 i = 0; i < count; i++ ) {
		if( i % 2 )
			binlog_confirm_ok( writer, mem_ );
		else 
			binlog_confirm_fail(writer, mem_ );
	}

	struct binlog_record *read_record = NULL;
	BL_READER_PTR reader = binlog_reader_init_byoffset( g_binlog_dir_, max_size_, 0, writer, mem_ );
	ASSERT_TRUE( NULL != reader );

	for( int32 i = 0; i < count; i++ ) {
		ASSERT_EQ( 1, binlog_read_record( reader, &read_record, mem_ ) );
		ASSERT_TRUE(IS_CONFIRM_RECORD(read_record));
		if( i % 2 )
			ASSERT_EQ( OPERATION_CONFIRM_OK, read_record->op_code );
		else
			ASSERT_EQ( OPERATION_CONFIRM_FAIL, read_record->op_code );
	}

	ASSERT_EQ( 0, binlog_read_record( reader,&read_record, mem_ ) );
	ASSERT_EQ( 1, binlog_is_read_all( reader ) );
}

TEST_F(BinlogTest, SeekTime )
{
	BL_WRITER_PTR writer = binlog_writer_init( g_binlog_dir_, max_size_, mem_ );
	ASSERT_TRUE( NULL != writer );

	const int data_len = 1023;
	struct binlog_record *record = create_binlog_record( data_len, mem_ );
	memset( record->data, 'a', data_len );
	
	const int count = 1000;
	for( int32 i = 0; i < count; i++ ) {
		memcpy( record->data, &i, 4 );
		binlog_write_record( writer, record, mem_ );

		usleep( 5 * 1000 );
	}

	uint32 now = time( NULL );
	BL_READER_PTR reader = binlog_reader_init_bytime( g_binlog_dir_, max_size_, now, mem_ );
	ASSERT_TRUE( NULL != reader );
	int rc = binlog_read_record( reader, &record, mem_ );
	ASSERT_GE( rc, 0 );
	if( 1 == rc ) {
		ASSERT_GE( record->time, now );
	}
	binlog_reader_destroy( reader );

	uint32 t = now - 3;
	reader = binlog_reader_init_bytime( g_binlog_dir_, max_size_, t, mem_ );
	ASSERT_TRUE( NULL != reader );
	ASSERT_EQ( 1, binlog_read_record( reader, &record, mem_ ) );
	PR( record->time );
	PR( t );
	ASSERT_GE( record->time, t);
	binlog_reader_destroy( reader );

	t = now - 10;
	reader = binlog_reader_init_bytime( g_binlog_dir_, max_size_, t, mem_ );
	ASSERT_TRUE( NULL != reader );
	ASSERT_EQ( 1, binlog_read_record( reader, &record, mem_ ) );
	ASSERT_GE( record->time, t);
	binlog_reader_destroy( reader );
}

