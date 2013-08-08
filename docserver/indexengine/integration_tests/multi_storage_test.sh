#!/bin/bash base_test.sh

# Test mutliple storage

set_up_before_start()
(
	add_docserver_conf '[server]'
	add_docserver_conf 'work_space = tmp/storage1;tmp/storage2'
)

set_up_after_start()
{
	local home=$1
	common_ensure_index
}

test_main()
{
	cd $MILEA

	msg insert 25 records
	for i in {1..25} ; do
		sql "insert -s insert into table1 id='a' value='$i'"
	done

	msg select all records
	sql "query -s select id, value from table1 indexwhere id = 'a'"

	msg show segments:
	client stat table1 1

	msg
	msg show dirs: 
	ls -l tmp/storage1/table1
}

