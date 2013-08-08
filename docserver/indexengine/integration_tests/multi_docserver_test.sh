#!/bin/bash base_test.sh

# Test mutliple docservers on a host.

set_up_before_start()
(
	:
)

set_up_after_start()
{
	local home=$1
	common_ensure_index
}

test_main()
{
	for d in $MILEA $MILEB ; do
		(
		msg insert into $d ...
		cd $d
		for i in {1..15} ; do
			sql "insert -s insert into table1 id='a' value='$d.$i'"
		done
		)
	done

	for d in $MILEA $MILEB ; do
		(
		cd $d
		msg query from $d
		sql "query -s select id, value from table1 indexwhere id = 'a'"
		)
	done
}

