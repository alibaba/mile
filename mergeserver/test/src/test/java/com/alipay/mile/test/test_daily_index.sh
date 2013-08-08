#!/bin/bash


source /etc/profile

export LC_ALL=C
set -e

home_dir=$(dirname $(dirname $(readlink -f $0)))

echo Change to $home_dir directory.
cd $home_dir


## index

./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY TEST_ID 2 254"
./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY TEST_IP 2 254"
./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY TEST_NAME 2 254"
#./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY GMT_TEST 2 10"


./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY TEST_ID 3 254"
#./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY GMT_TEST 3 10"
./sbin/client -c 127.0.0.1:12345 -l "ensure_index TEST_DAILY 11 3 254"



