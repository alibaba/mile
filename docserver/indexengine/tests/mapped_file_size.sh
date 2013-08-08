#!/bin/bash

[ -z "$1" ] && echo -e "usage: $0 docserver_work_space_table_dir\nExample: \n\t$0 /data/cr/storage/ps_consume_record" && exit 1

find "$1" -type f -and -not -name filter_vstore.dat | xargs stat -c %s | awk '{sum+=$1}END{print sum}'

