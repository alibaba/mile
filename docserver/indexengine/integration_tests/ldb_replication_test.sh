#!/bin/bash base_test.sh

# Test ldb binlog replication.

set_up_before_start()
(
	local home=$1

	add_docserver_conf '[server]'
	add_docserver_conf "storage_engine = ldb"

	# set B b 
	if [ $home = $MILEB ] ; then
		add_docserver_conf 'role = slave'
		add_docserver_conf 'sync_port = 16118'
	fi
)

set_up_after_start()
{
	local home=$1
}

test_main()
{
	(
	cd $MILEA
	msg insert into master
	for i in {1..5} ; do
		sql "insert -s insert into table1 id='a' time='int32:$i' value='vv$i'"
	done
	)

	msg query slave
	(
	cd $MILEB
	sql "query -s select id, time, value from table1 indexwhere id = 'a'"
	)

	(
	cd $MILEA
	msg delete master
	sql "delete -s delete from table1 indexwhere id='a' and time <= 'int32:1'"

	msg update master
	sql "update -s update table1 set value='udpate_value' indexwhere id = 'a' and time >= 'int32:3'"
	)

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

