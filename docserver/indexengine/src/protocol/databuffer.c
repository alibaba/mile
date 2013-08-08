#include "databuffer.h"

static inline void make_write_room(struct data_buffer *buf, size_t n)
{
	if (buf->wpos + n > buf->size)
		databuf_reserve(buf, buf->wpos + n);
}

struct data_buffer* init_data_buffer() {
	struct data_buffer* buffer = (struct data_buffer*) malloc(
			sizeof(struct data_buffer));
	if (buffer == NULL ) {
		log_error("初始化data buffer时申请内存失败!");
		return NULL ;
	}

	memset(buffer, 0, sizeof(struct data_buffer));

	return buffer;
}

void destroy_data_buffer(struct data_buffer* buffer) {
	if (NULL == buffer) {
		return;
	}
	free(buffer->data);
	free(buffer);
}

void clear_data_buffer(struct data_buffer * buffer) {
	memset(buffer, 0, sizeof(struct data_buffer));
}

void print_data_buffer(struct data_buffer* data_buf) {
	if (!is_debug_enabled())
		return;
	uint32_t data_len = data_buf->data_len;
	uint32_t i;
	int offset = 0;
	int size = 1000;
	char buffer[1000];

	offset += snprintf_buffer(buffer + offset, size - offset, "buffer长度: %d\n",
			data_len);

	for (i = 0; i < data_len && offset < size - 1; i++) {
		offset += snprintf_buffer(buffer + offset, size - offset, "%02x ",
				data_buf->data[i]);
	}

	buffer[size - 1] = '\0';

	log_debug("%s", buffer);
}

void databuf_reserve(struct data_buffer *buf, size_t n)
{
	if (buf->size >= n)
		return;

	const int min_buf_size = 1024;
	if (n < min_buf_size / 2)
		n = min_buf_size;
	else
		n = 2 * n;
	databuf_resize(buf, n);
}

void databuf_resize(struct data_buffer *buf, size_t n)
{
	if (buf->size >= n)
		return;

	uint8_t *new_data = (uint8_t*) malloc(n);
	if (buf->data == NULL) {
		buf->data = new_data;
		buf->size = n;
	} else {
		memcpy(new_data, buf->data, buf->size);
		uint8_t *p = buf->data;
		buf->data = new_data;
		buf->size = n;
		free(p);
	}
}

uint8_t read_int8(struct data_buffer* buffer) {
	if (buffer->rpos + 1 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}
	return buffer->data[buffer->rpos++];
}

uint16_t read_int16(struct data_buffer* buffer) {
	uint16_t n;

	if (buffer->rpos + 2 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}

	uint8_t* data = buffer->data + buffer->rpos;
	n = data[0];
	n <<= 8;
	n |= data[1];
	buffer->rpos += 2;

	return n;

}

uint32_t read_int32(struct data_buffer* buffer) {
	uint32_t n;

	if (buffer->rpos + 4 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}

	uint8_t* data = buffer->data + buffer->rpos;
	n = data[0];
	n <<= 8;
	n |= data[1];
	n <<= 8;
	n |= data[2];
	n <<= 8;
	n |= data[3];
	buffer->rpos += 4;

	return n;
}

uint64_t read_int64(struct data_buffer* buffer) {
	uint64_t n;

	if (buffer->rpos + 8 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}

	uint8_t* data = buffer->data + buffer->rpos;
	n = data[0];
	n <<= 8;
	n |= data[1];
	n <<= 8;
	n |= data[2];
	n <<= 8;
	n |= data[3];
	n <<= 8;
	n |= data[4];
	n <<= 8;
	n |= data[5];
	n <<= 8;
	n |= data[6];
	n <<= 8;
	n |= data[7];
	buffer->rpos += 8;

	return n;
}

uint8_t read_pos_int8(struct data_buffer* buffer, uint32_t pos) {
	if (pos + 1 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}
	return buffer->data[pos];
}

uint16_t read_pos_int16(struct data_buffer* buffer, uint32_t pos) {
	uint16_t n;

	if (pos + 2 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}

	uint8_t* data = buffer->data + pos;
	n = data[0];
	n <<= 8;
	n |= data[1];

	return n;

}

uint32_t read_pos_int32(struct data_buffer* buffer, uint32_t pos) {
	uint32_t n;

	if (pos + 4 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}

	uint8_t* data = buffer->data + pos;
	n = data[0];
	n <<= 8;
	n |= data[1];
	n <<= 8;
	n |= data[2];
	n <<= 8;
	n |= data[3];

	return n;
}

uint64_t read_pos_int64(struct data_buffer* buffer, uint32_t pos) {
	uint64_t n;

	if (pos + 8 > buffer->data_len) {
		buffer->array_border = 1;
		return 0;
	}

	uint8_t* data = buffer->data + pos;
	n = data[0];
	n <<= 8;
	n |= data[1];
	n <<= 8;
	n |= data[2];
	n <<= 8;
	n |= data[3];
	n <<= 8;
	n |= data[4];
	n <<= 8;
	n |= data[5];
	n <<= 8;
	n |= data[6];
	n <<= 8;
	n |= data[7];

	return n;
}

void read_bytes(struct data_buffer* buffer, uint8_t* data, uint32_t len) {
	if (buffer->rpos + len > buffer->data_len) {
		buffer->array_border = 1;
		return;
	}

	memcpy(data, buffer->data + buffer->rpos, len);
	buffer->rpos += len;
}

char *read_cstring(struct data_buffer *buf, MEM_POOL_PTR mem) {
	if (buf->rpos + 4 > buf->data_len) {
		buf->array_border = 1;
		return NULL ;
	}

	int32_t len = read_int32(buf);
	if (buf->rpos + len > buf->data_len) {
		buf->array_border = 1;
		return NULL ;
	}

	char *str = (char *) mem_pool_malloc(mem, len + 1);
	memcpy(str, buf->data + buf->rpos, len);
	buf->rpos += len;
	str[len] = '\0';
	return str;
}


void write_int8(uint8_t n, struct data_buffer* buffer) {
	make_write_room(buffer, 1);

	buffer->data[buffer->wpos++] = n;
	buffer->data_len += 1;
}

void write_int16(uint16_t n, struct data_buffer* buffer) {
	make_write_room(buffer, 2);
	uint8_t* data = buffer->data + buffer->wpos;

	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*(data) = (uint8_t) n;

	buffer->wpos += 2;
	buffer->data_len += 2;

}

void write_int32(uint32_t n, struct data_buffer* buffer) {
	make_write_room(buffer, 4);
	uint8_t* data = buffer->data + buffer->wpos;

	*(data + 3) = (uint8_t) n;
	n >>= 8;
	*(data + 2) = (uint8_t) n;
	n >>= 8;
	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*(data) = (uint8_t) n;

	buffer->wpos += 4;
	buffer->data_len += 4;
}

void write_int64(uint64_t n, struct data_buffer* buffer) {
	make_write_room(buffer, 8);
	uint8_t* data = buffer->data + buffer->wpos;

	*(data + 7) = (uint8_t) n;
	n >>= 8;
	*(data + 6) = (uint8_t) n;
	n >>= 8;
	*(data + 5) = (uint8_t) n;
	n >>= 8;
	*(data + 4) = (uint8_t) n;
	n >>= 8;
	*(data + 3) = (uint8_t) n;
	n >>= 8;
	*(data + 2) = (uint8_t) n;
	n >>= 8;
	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*(data) = (uint8_t) n;

	buffer->wpos += 8;
	buffer->data_len += 8;

}

void write_bytes(uint8_t* data, uint32_t len, struct data_buffer* buffer) {
	make_write_room(buffer, len);

	memcpy(buffer->data + buffer->wpos, data, len);
	buffer->wpos += len;
	buffer->data_len += len;
}

void fill_int8(uint8_t n, struct data_buffer* buffer, uint32_t fpos) {
	if (fpos + 1 > buffer->data_len) {
		buffer->array_border = 1;
		return;
	}

	buffer->data[fpos] = n;
}

void fill_int32(uint32_t n, struct data_buffer* buffer, uint32_t fpos) {
	uint8_t* data = buffer->data + fpos;

	if (fpos + 4 > buffer->data_len) {
		buffer->array_border = 1;
		return;
	}

	*(data + 3) = (uint8_t) n;
	n >>= 8;
	*(data + 2) = (uint8_t) n;
	n >>= 8;
	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*(data) = (uint8_t) n;

}

uint16_t convert_int16(uint16_t n) {
	uint16_t res;
	uint8_t* data = (uint8_t*) (&res);

	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*data = (uint8_t) n;

	return res;

}

uint32_t convert_int32(uint32_t n) {
	uint32_t res;
	uint8_t* data = (uint8_t*) (&res);

	*(data + 3) = (uint8_t) n;
	n >>= 8;
	*(data + 2) = (uint8_t) n;
	n >>= 8;
	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*(data) = (uint8_t) n;

	return res;
}

uint64_t convert_int64(uint64_t n) {
	uint64_t res;
	uint8_t* data = (uint8_t*) (&res);

	*(data + 7) = (uint8_t) n;
	n >>= 8;
	*(data + 6) = (uint8_t) n;
	n >>= 8;
	*(data + 5) = (uint8_t) n;
	n >>= 8;
	*(data + 4) = (uint8_t) n;
	n >>= 8;
	*(data + 3) = (uint8_t) n;
	n >>= 8;
	*(data + 2) = (uint8_t) n;
	n >>= 8;
	*(data + 1) = (uint8_t) n;
	n >>= 8;
	*(data) = (uint8_t) n;

	return res;

}

