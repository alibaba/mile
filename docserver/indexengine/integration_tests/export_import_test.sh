#!/bin/bash base_test.sh

# Test ldb binlog replication.

set_up_before_start()
(
	local home=$1

	add_docserver_conf '[server]'
	add_docserver_conf "storage_engine = ldb"
)

set_up_after_start()
{
	local home=$1
}



test_main()
{
	(
	cd $MILEA
	msg insert into MILEA
	for i in {1..5} ; do
		sql "insert -s insert into table1 id='a' time='int32:$i' value='vv$i'"
	done
	)

	query_all

	(
	cd $MILEA
	msg export to _export_data.sql

	sql "export -s export to '_export_data.sql' from table1 indexwhere id = 'a'"

	msg cat _export_data.sql
	cat _export_data.sql
	)

	(
	file=$(readlink -f $MILEA/_export_data.sql)
	cd $MILEB
	msg import to MILEB
	msg java -cp sbin/perf.jar com.alipay.mile.benchmark.Benchmark  --config-file ./etc/mileCliClent.properties.prod --threads 1 --ops-count 5  --metric-type histogram  --interval 1 -i 100 --target-throughput 1000000 --record $file
	java -cp sbin/perf.jar com.alipay.mile.benchmark.Benchmark  --config-file ./etc/mileCliClent.properties.prod --threads 1 --ops-count 5  --metric-type histogram  --interval 1 -i 100 --target-throughput 1000000 --record $file
	)

	query_all
}

