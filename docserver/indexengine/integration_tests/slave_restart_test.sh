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
	for i in {1..3} ; do
		sql "insert -s insert into table2 id='a' time='int32:$i' value='vv$i'"
	done
	)

	query_all

	(
	cd $MILEB
	msg stop slave
	./mile.sh stopd
	)

	(
	cd $MILEA
	msg insert into master again
	for i in {1..3} ; do
		sql "insert -s insert into table1 id='a' time='int32:$i' value='vv$i'"
	done
	)

	msg Query all, slave will report error.
	query_all

	(
	cd $MILEB
	msg start slave
	./mile.sh stopm

	./mile.sh startd
	sleep 0.5
	./mile.sh startm
	sleep 0.5
	)

	query_all
}

