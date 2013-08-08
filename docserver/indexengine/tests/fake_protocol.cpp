// should never include file from ../src
// needed gcc 4.5 or higher version

#include "fake_protocol.h"

#include <stdio.h>
#include <string>
#include <iostream>
#include <arpa/inet.h>

#include <limits>

#include <unistd.h>
#include <vector>
#include <list>
#include <memory>
#include <thread>
#include <sys/time.h>
#include <atomic>

#define perr(fmt, ...) fprintf(stderr, "ERROR %s " fmt "\n", __FUNCTION__, ##__VA_ARGS__)

template < typename T >
T beswap( T x )
{
	const T *cptr = &x;

	switch( sizeof(x)) {
	case 2:
	{
		uint16_t *src = (uint16_t *)cptr;
		uint16_t value = (*src >> 8) | (*src << 8);
		T *ptr = (T *)&value;
		return *(T *)ptr;
	}
		return x;
	case 4:
	{
		auto value = __builtin_bswap32( *(const int32_t *)cptr );
		T *ptr = (T *)&value;
		return *(T *)ptr;
	}
	case 8:
	{
		auto value = __builtin_bswap64( *(const int64_t *)cptr );
		T *ptr = (T *)&value;
		return *(T *)ptr;
	}
	default:
		return x;
	}
}

template < typename T >
std::string to_net_string( T x )
{
	auto value = beswap( x );

	return std::string((const char *)&value, sizeof(x));
}

struct dyn_base_t {
	virtual std::string ToString() = 0;
	virtual ~dyn_base_t() {}
};

template < uint8_t value_type, typename T >
struct dyn_value_t : public dyn_base_t {
	T value;
	dyn_value_t( T v ) : value( v ) {}
	std::string ToString()
	{
		uint32_t size = sizeof(value);

		return to_net_string( type ) + to_net_string( size ) + to_net_string( value );
	}
	virtual ~dyn_value_t() {}

	static const uint8_t type = value_type;
};

template < >
struct dyn_value_t< HI_TYPE_STRING, std::string > : public dyn_base_t {
	std::string value;
	dyn_value_t( std::string v ) : value( v ) {}
	std::string ToString() { return to_net_string( type ) + to_net_string((uint32_t)value.size()) + value; }

	virtual ~dyn_value_t() {}

	static const uint8_t type = HI_TYPE_STRING;
};

template < typename T >
struct dyn_value_t< HI_TYPE_NULL, T > : public dyn_base_t {
	std::string ToString() { return to_net_string( type ); }

	virtual ~dyn_value_t() {}
	static const uint8_t type = HI_TYPE_NULL;
};

// dyn value define
typedef struct dyn_value_t< HI_TYPE_NULL, void > dyn_null;
typedef struct dyn_value_t< HI_TYPE_SHORT, int16_t > dyn_int16;
typedef struct dyn_value_t< HI_TYPE_UNSIGNED_SHORT, uint16_t > dyn_uint16;
typedef struct dyn_value_t< HI_TYPE_LONG, int32_t > dyn_int32;
typedef struct dyn_value_t< HI_TYPE_UNSIGNED_LONG, uint32_t > dyn_uint32;
typedef struct dyn_value_t< HI_TYPE_LONGLONG, int64_t > dyn_int64;
typedef struct dyn_value_t< HI_TYPE_UNSIGNED_LONGLONG, uint64_t > dyn_uint64;
typedef struct dyn_value_t< HI_TYPE_FLOAT, float > dyn_float;
typedef struct dyn_value_t< HI_TYPE_DOUBLE, float > dyn_double;
typedef struct dyn_value_t< HI_TYPE_STRING, std::string > dyn_string;

struct cstring_t {
	std::string str;

	cstring_t() {}
	cstring_t( std::string str ) : str( str ) {}

	std::string ToString() { return to_net_string((uint32_t)str.size()) + str; }
};

struct field_dyn_value_t {
	field_dyn_value_t( std::string field_name, dyn_base_t *value ) : field_name( field_name ), value( value ) {}

	struct cstring_t field_name;
	std::shared_ptr< dyn_base_t > value;

	std::string ToString() { return field_name.ToString() + value->ToString(); }
};

struct hint_t {
	static const int value_size = 4;
	uint64_t value[value_size];

	hint_t() : value( { 0, std::numeric_limits< uint64_t >::max(), 0, std::numeric_limits< uint64_t >::max() } ) {}
	std::string ToString()
	{
		std::string out = to_net_string( value_size );

		for( int i = 0; i < value_size; i++ ) {
			out += to_net_string( value[i] );
		}
		return out;
	}
};

class SelectField {
public:
	uint8_t type;
	virtual std::string ToString() = 0;
	virtual ~SelectField() {}
};

class NormalSelectField : public SelectField {
public:
	cstring_t field_name;
	NormalSelectField( std::string field_name ) : field_name( field_name ) { this->type = VALUE_SELECT; }

	std::string ToString()
	{
		return to_net_string( type ) + field_name.ToString()
			+ field_name.ToString(); // alias name
	}

	virtual ~NormalSelectField() {}
};

class FuncSelectField : public SelectField {
public:
	uint8_t func_type;
	cstring_t alias_name;
	cstring_t field_name;

	FuncSelectField( enum function_type func_type, std::string alias_name, std::string field_name )
		: func_type( func_type ), alias_name( alias_name ), field_name( field_name )
	{
		this->type = FUNCTION_SELECT;
	}

	std::string ToString() { return to_net_string( type ) + to_net_string( func_type )
		+ alias_name.ToString() + alias_name.ToString()/* select name */  + field_name.ToString(); }

	virtual ~FuncSelectField() {}
};

class Condition {
public:
	uint8_t type;
	virtual std::string ToString() = 0;
	virtual ~Condition() {};
};

class LogicCondition : public Condition
{
public:
	LogicCondition( uint8_t type ) { this->type = type; }
	std::string ToString() { return to_net_string( type ); }

	virtual ~LogicCondition() {}
};

class AndCondition : public LogicCondition {
public:
	AndCondition() : LogicCondition( LOGIC_AND ) {}
	virtual ~AndCondition() {}
};

class OrCondition : public LogicCondition {
public:
	OrCondition() : LogicCondition( LOGIC_OR ) {}
	virtual ~OrCondition() {}
};

class ExpCondition : public Condition
{
public:
	cstring_t field_name;
	uint8_t comparetor;
	std::list< std::shared_ptr< dyn_base_t > > values;

	ExpCondition( std::string field_name, enum compare_type comparetor, dyn_base_t *value )
		: field_name( field_name ), comparetor( comparetor )
	{
		this->type = CONDITION_EXP;
		AddValue( value );
	}
	void AddValue( dyn_base_t *value ) { values.push_back( std::shared_ptr< dyn_base_t >( value )); }

	std::string ToString()
	{
		std::string out = to_net_string( type );

		out += field_name.ToString();
		out += to_net_string( comparetor );
		out += to_net_string( (uint32_t)values.size() );
		for( auto it = values.begin(); it != values.end(); ++it ) {
			out += (*it)->ToString();
		}

		return out;
	}

	virtual ~ExpCondition() {}
};

struct order_t {
	cstring_t field_name;
	uint8_t type;

	order_t( std::string field_name, enum order_types type ) : field_name( field_name ), type( type ) {}

	std::string ToString() { return field_name.ToString() + to_net_string( type ); }
};

static int wrap_read(int fd, void *buf, int size)
{
	int read_len = 0, n;
	while( size - read_len > 0 ) {
		n = read(fd, (char *)buf + read_len, size - read_len);
		if( 0 == n ) {
			perr( "connection closed" );
			break;
		}
		else if( n < 0 ) {
			perr( "read error, errno %d", errno);
			break;
		}

		read_len += n;
	}

	return read_len;
}

struct message_header_t {
	uint32_t len;
	uint8_t ver_major;
	uint8_t ver_minor;
	uint16_t type;
	uint32_t id;
	char data[0];

	message_header_t( uint16_t type ) : len( 0 ), ver_major( 1 ), ver_minor( 0 ), type( type ), id( 0 ) { }
	std::string ToString() { return to_net_string( len ) + to_net_string( ver_major ) + to_net_string( ver_minor ) + to_net_string( type ) + to_net_string( id ); }

	void SetMessageBodyLen( size_t len ) { this->len = len + sizeof(*this); }
	uint32_t GetMessageBodyLen( void )  { return len - sizeof(*this); }

	static struct message_header_t *CreateFromStream(int fd) // blocking io
	{
		uint32_t len = 0;
		int n;
		if( (n = wrap_read(fd, &len, sizeof(len))) != sizeof(len)) {
			perr( "read failed n %d, errno %d", n, errno );
			return NULL;
		}

		len = beswap(len);
		if( len < sizeof(struct message_header_t) ) {
			perr( "invalid response message" );
			return NULL;
		}

		struct message_header_t *header = (struct message_header_t*)malloc(len);
		header->len = len;
		if( (n = wrap_read(fd, ((char *)header) + sizeof(len), len - sizeof(len))) != len - sizeof(len) ) {
			perr( "read failed n %d, errno %d", n, errno );
			free(header);
			return NULL;
		}

		header->type = beswap(header->type);
		header->id = beswap(header->id);
		return header;
	}

	static void Destroy(struct message_header_t *header) { free(header); }
} __attribute__((packed));

class BaseMessage {
public:
	struct message_header_t header;
	BaseMessage( uint16_t type ) : header( type ) {}
	std::string ToString( void )
	{
		std::string body = BodyToString();

		header.SetMessageBodyLen( body.size());
		return header.ToString() + body;
	}

protected:
	virtual std::string BodyToString() = 0;
};

// echo packet
class EchoMessage : public BaseMessage {
public:
	uint32_t timeout;
	cstring_t str;

	EchoMessage(const std::string &s) : BaseMessage(MT_TEST_REQ_ECHO), timeout(0), str(s) {}

	virtual std::string BodyToString()
	{
		std::string out = to_net_string(timeout);
		return out + str.ToString();
	}
};

// insert packet
class InsertMessage : public BaseMessage
{
public:
	uint32_t time_out;
	cstring_t table_name;
	std::list< field_dyn_value_t > value_list;

	InsertMessage() : BaseMessage( MT_MD_EXE_INSERT ) {}

protected:
	virtual std::string BodyToString()
	{
		std::string out = to_net_string( time_out );

		out += table_name.ToString();
		out += to_net_string((uint32_t)value_list.size());
		for( auto it = value_list.begin(); it != value_list.end(); ++it ) {
			out += it->ToString();
		}
		return out;
	}
};

// query packet
class QueryMessage : public BaseMessage
{
public:
	uint32_t time_out;
	uint16_t access_type;
	cstring_t table_name;
	hint_t hint;

	// select field
	std::vector< std::shared_ptr< SelectField > > select_field;
	// index conditoon
	std::vector< std::shared_ptr< Condition > > index_cond;
	// filter conditoon
	std::vector< std::shared_ptr< Condition > > filter_cond;

	// group by columns
	std::vector< cstring_t > group_field;

	// no gorup order

	// no group limit

	std::vector< order_t > order_field;
	uint32_t limit;

	QueryMessage() : BaseMessage( MT_MD_EXE_QUERY ), time_out( 10 * 1000 ), access_type( 0 ), limit( 10000 ) {}

	virtual std::string BodyToString()
	{
		std::string out = to_net_string( time_out );

		out += to_net_string( access_type );
		out += table_name.ToString();
		out += hint.ToString();

		out += to_net_string((uint32_t)select_field.size());
		for( auto it = select_field.begin(); it != select_field.end(); ++it ) {
			out += (*it)->ToString();
		}

		out += to_net_string((uint32_t)index_cond.size());
		for( auto it = index_cond.begin(); it != index_cond.end(); ++it ) {
			out += (*it)->ToString();
		}

		out += to_net_string((uint32_t)filter_cond.size());
		for( auto it = filter_cond.begin(); it != filter_cond.end(); ++it ) {
			out += (*it)->ToString();
		}

		out += to_net_string((uint32_t)group_field.size());
		for( auto it = group_field.begin(); it != group_field.end(); ++it ) {
			out += it->ToString();
		}

		// group order
		out += to_net_string((uint32_t)0 );
		// group limit
		out += to_net_string((uint32_t)0 );

		out += to_net_string((uint32_t)order_field.size());
		for( auto it = order_field.begin(); it != order_field.end(); ++it ) {
			out += it->ToString();
		}

		out += to_net_string( limit );
		return out;
	}
};

void output( const std::string &str )
{
	if( write( STDOUT_FILENO, str.c_str(), str.size()) != (ssize_t)str.size() ) {
		perr( "write failed, errno %d", errno );
	}
}

bool read_and_check(void)
{
	struct message_header_t *header = message_header_t::CreateFromStream(STDIN_FILENO);
	if( NULL == header) {
		return false;
	}

	if( MT_DM_SQL_EXC_ERROR == header->type ) {
		perr( "sql execute error" );
		message_header_t::Destroy(header);
		return false;
	}

	// perr("good");

	message_header_t::Destroy(header);
	return true;
}

std::string gen_insert_packet()
{
	InsertMessage msg;

	msg.table_name.str = "name1";
	msg.time_out = 10 * 1000; // 10s
	msg.value_list.push_back( field_dyn_value_t( "id", new dyn_string( "user_id" ) ));
	msg.value_list.push_back( field_dyn_value_t( "value", new dyn_int32( 100 ) ));

	return msg.ToString();
}

std::string gen_query_packet()
{
	QueryMessage msg;

	msg.table_name.str = "name1";
	msg.select_field.push_back( std::shared_ptr< SelectField >( new NormalSelectField( "id" )));
	msg.select_field.push_back( std::shared_ptr< SelectField >( new NormalSelectField( "value" )));

	msg.index_cond.push_back( std::shared_ptr< Condition >( new ExpCondition( "id", CT_EQ, new dyn_string( "user_id" ))));
	msg.filter_cond.push_back( std::shared_ptr< Condition >( new ExpCondition( "value", CT_EQ, new dyn_uint32( 100 ))));

	return msg.ToString();
}

void benchmark(const std::string &msg_buf)
{
	volatile uint64_t counter = 0;
	std::atomic<int32_t> in_process(0);

	std::thread read_thread( [&counter, &in_process]{ while(read_and_check()) {counter++; in_process.fetch_sub(1);} } );
	std::thread report_thread( [&counter]{
			while(true) {
				struct timeval begin_time, end_time;
				gettimeofday(&begin_time, NULL);
				uint64_t begin = counter;
				sleep(1);
				gettimeofday(&end_time, NULL);
				fprintf(stderr, "toutal: %llu, Through out: %f/s\n", counter, (float)(((double)(counter - begin) * 1000000)/
					((((uint64_t)end_time.tv_sec) * 1000000 + end_time.tv_usec ) - (((uint64_t) begin_time.tv_sec) * 1000000 + begin_time.tv_usec))));
			} 
			} );
	std::thread send_thread( [&msg_buf, &in_process]{
			while(true) {
				output(msg_buf);
				in_process.fetch_add(1);
				while( in_process.load() > 1000) {
					usleep(1000);
				}
			}
			} );

	read_thread.join();
	report_thread.join();
	send_thread.join();
}

int main( int argc, char *argv[] )
{
	// std::string msg_buf = gen_insert_packet();
	// std::string msg_buf = gen_query_packet();
	EchoMessage msg("hello world");
	std::string msg_buf = msg.ToString();

	uint64_t times = atoll(argv[1]);
	for (uint64_t i = 0; i < times; i++) {
		output( msg_buf );
		read_and_check();
	}

	// benchmark(msg_buf);

	return 0;
}

