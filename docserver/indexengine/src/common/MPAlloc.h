// MPAlloc.h : Allocator wrap for mem_pool (mem.h)
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-10-17

#ifndef MPALLOC_H
#define MPALLOC_H

#include "mem.h"
#include <memory>
#include <limits>

template <typename T>
class MPAlloc {
public:
	typedef T value_type;
	typedef value_type *pointer;
	typedef const value_type *const_pointer;
	typedef value_type &reference;
	typedef const value_type &const_reference;
	typedef std::size_t size_type;
	typedef std::ptrdiff_t difference_type;

	template <typename U>
		struct rebind {
			typedef MPAlloc<U> other;
		};

	inline explicit MPAlloc(MEM_POOL_PTR pool) throw() : pool_(pool) {}
	inline explicit MPAlloc(const MPAlloc &alloc) throw() : pool_(alloc.pool_) {}
	template <typename U>
		inline MPAlloc(const MPAlloc<U> &alloc) throw() : pool_(alloc.pool_) {}
	~MPAlloc() throw() {}

	inline pointer address(reference r) { return &r; }
	inline const_pointer address(const_reference r) { return &r; }

	inline pointer allocate(size_type num, typename std::allocator<void>::const_pointer hint = 0) {
		void *p = mem_pool_malloc(pool_, sizeof(T) * num);
		if (!p)
			throw std::bad_alloc();
		return reinterpret_cast<pointer>(p);
	}

	inline void deallocate(pointer, size_type) {}

	inline size_type max_size() const {
		// return pool_->size / sizeof(T);
		return std::numeric_limits<size_type>::max() / sizeof(T);
	}

	inline void construct(pointer p, const T &t) { new (p)T(t); }
	inline void destroy(pointer p) { p->~T(); }

	template <typename U>
		inline bool operator==(MPAlloc<U> const &alloc) { return pool_ == alloc.pool_; }
	template <typename U>
		inline bool operator!=(MPAlloc<U> const &alloc) { return pool_ != alloc.pool_; }

	template <typename U>
		friend class MPAlloc;
private:
	MEM_POOL_PTR pool_;
};

#endif // MPALLOC_H
