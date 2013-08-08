#!/bin/bash

# load java environment configure.
source /etc/profile

export LC_ALL=C
set -e

home_dir=$(dirname $(dirname $(readlink -f $0)))

echo Change to $home_dir directory.
cd $home_dir

./sbin/client -c 127.0.0.1:8888 -l "ensure_index monitor field1 2 254"
./sbin/client -c 127.0.0.1:8888 -l "ensure_index monitor field2 3 254"
./sbin/client -c 127.0.0.1:8888 -l "ensure_index monitor field3 3 254"
./sbin/client -c 127.0.0.1:8888 -l "ensure_index monitor field4 3 254"
./sbin/client -c 127.0.0.1:8888 -l "ensure_index monitor time 3 10"
./sbin/client -c 127.0.0.1:8888 -l "ensure_index monitor value 3 7"
