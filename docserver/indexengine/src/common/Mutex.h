// Mutex.h : Mutex
// Author: liubin <bin.lb@alipay.com>
// Created: 2012-08-31

#ifndef MUTEX_H
#define MUTEX_H

#include <pthread.h>

// lock guard
template <typename T>
class LockT {
public:
	LockT(T &t) : mutex_(t) { mutex_.Lock(); }
	~LockT() { mutex_.Unlock(); }
private:
	LockT(const LockT &);
	LockT &operator=(const LockT &);

private:
  T &mutex_;
};

// convenient for direct use pthread_mutex_t and pthread_spinlock_t
template <>
class LockT<pthread_mutex_t> {
public:
	LockT(pthread_mutex_t &t) : mutex_(t) { pthread_mutex_lock(&mutex_); }
	~LockT() { pthread_mutex_unlock(&mutex_); }
private:
	LockT(const LockT &);
	LockT &operator=(const LockT &);
private:
	pthread_mutex_t &mutex_;
};

template <>
class LockT<pthread_spinlock_t> {
public:
	LockT(pthread_spinlock_t &t) : mutex_(t) { pthread_spin_lock(&mutex_); }
	~LockT() { pthread_spin_unlock(&mutex_); }
private:
	LockT(const LockT &);
	LockT &operator=(const LockT &);
private:
	pthread_spinlock_t &mutex_;
};


// simple mutex and spin lock

class Mutex {
public:
	Mutex() { pthread_mutex_init(&mutex_, NULL);};
	~Mutex() {};
	void Lock() { pthread_mutex_lock(&mutex_); }
	void Unlock() { pthread_mutex_unlock(&mutex_); }

private:
	pthread_mutex_t mutex_;
};
typedef LockT<Mutex> MutexLockT;

class SpinMutex {
public:
	SpinMutex() { pthread_spin_init(&mutex_, PTHREAD_PROCESS_PRIVATE); }
	~SpinMutex() {}

	void Lock() { pthread_spin_lock(&mutex_); }
	void Unlock() { pthread_spin_unlock(&mutex_); }

private:
	pthread_spinlock_t mutex_;
};
typedef LockT<SpinMutex> SpinMutexLockT;

#define MUTEX_LOCK_T(mutex, name) LockT<typeof(mutex)> name(mutex)

#endif // MUTEX_H
