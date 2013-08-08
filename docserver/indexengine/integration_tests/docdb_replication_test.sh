#!/bin/bash base_test.sh

# Test ldb binlog replication.

set_up_before_start()
(
	local home=$1

	add_docserver_conf '[docdb]'
	add_docserver_conf 'row_limit = 5'

	# set B b 
	if [ $home = $MILEB ] ; then
		add_docserver_conf '[server]'
		add_docserver_conf 'role = slave'
		add_docserver_conf 'sync_port = 16118'
	fi
)

set_up_after_start()
{
	local home=$1
	if [ $home = $MILEA ] ; then
		common_ensure_index
	fi
}

query_all()
{
	(
	cd $MILEA
	msg query master
	sql "query -s select id, time, value from table1 indexwhere id = 'a'"
	)

	(
	cd $MILEB
	msg query slave
	sql "query -s select id, time, value from table1 indexwhere id = 'a'"
	)
}

test_main()
{
	(
	cd $MILEA
	msg insert into master
	for i in {1..8} ; do
		sql "insert -s insert into table1 id='a' time='int32:$i' value='vv$i'"
	done
	)

	query_all

	(
	cd $MILEA
	msg delete master
	sql "delete -s delete from table1 indexwhere id='a' where time <= 'int32:1'"

	msg update master
	sql "update -s update table1 set value='udpate_value' indexwhere id = 'a' where time >= 'int32:3' and time < 'int32:5'"
	)

	query_all

	(
	cd $MILEA
	msg unload segment 0
	client unload table1 0

	client stat table1 1
	)
	query_all

	(
	cd $MILEA
	msg load segment 0
	client load table1 0 tmp/storage/table1/table1_segment_000000_dump
	)
	query_all

	(
	cd $MILEA
	msg compress
	client compress table1
	)

	sleep 1

	query_all

	(
	cd $MILEB
	msg show slave segment0

	client stat table1 1
	
	echo ls -l tmp/storage/table1/table1_segment_000000/id
	ls -l tmp/storage/table1/table1_segment_000000/id
	)
}

