#!/bin/bash

BASE_PACKAGE=mile_base.tar.gz
TEST_DIR=_test_
MILEA=$TEST_DIR/A
MILEB=$TEST_DIR/B
MILES=($MILEA $MILEB)

# interface
set_up_before_start()
(
	local home=$1
	msg to be rewrite..
)

set_up_after_start()
(
	local home=$1
	common_ensure_index
)

test_main()
(
	cd $MILEA

	sql 'insert -s insert into table1 id="a" value="aa"'
	sql 'query -s select id, value from table1 indexwhere id="a"'
)

# common utils
client()
{
	color_green
	echo ./sbin/client -c 127.0.0.1:$(grep '^port' etc/docserver.conf | grep -o '[[:digit:]]\+') -l "$*"
	./sbin/client -c 127.0.0.1:$(grep '^port' etc/docserver.conf | grep -o '[[:digit:]]\+' etc/docserver.conf) -l "$*"
	end_color
}

sql()
{
	color_red
	echo "$@"
	./mile.sh startc "$@"
	end_color
}

common_ensure_index()
{
	client ensure_index table1 id 2 254
}

add_docserver_conf()
{
	echo "$*" >> etc/docserver.conf
}

color_red()
{
	tput setaf 1
}

color_green()
{
	tput setaf 2
}

end_color()
{
	tput sgr0
}

msg()
{
	color_green
	echo "$@"
	end_color
}

err()
{
	color_red
	echo "$@"
	end_color
	exit 1
} >&2

query_all()
{
	(
	cd $MILEA
	msg query "MILEA ($MILEA)"
	sql "query -s select id, time, value from table1 indexwhere id = 'a'"
	)

	(
	cd $MILEB
	msg query "MILEB ($MILEB)"
	sql "query -s select id, time, value from table1 indexwhere id = 'a'"
	)
}


base_package()
{
	[ -f "$BASE_PACKAGE" ] && return 0
	local svn_root=

	local cmd=svn
	git status >/dev/null 2>&1 && cmd='git svn'
	svn_root=$($cmd info 2>/dev/null | grep '^URL:' | grep -o 'http:.\+')
	svn_root=${svn_root%%/docserver/*}
	msg $svn_root

	[ -z "$svn_root" ] && err "must in svn."


	(
	export LANG=en_US.UTF8
	export LC_ALL=en_US.UTF8
	svn co $svn_root _mile_
	) || err "svn checkout $svn_root failed!"

	(
	cd _mile_
	./build.sh

	rm -f mile_[[:digit:]]*.tar.gz
	cat > mile_[[:digit:]]*/etc/servers.xml <<-'EOF'
	<?xml version="1.0" encoding="UTF-8"?>
	<Docservers>
		<Node>
			<ID>1</ID>
			<Master>
				<Ip>127.0.0.1</Ip>
				<Port>18518</Port>
			</Master>
		</Node>
	</Docservers>
	EOF

	cp docserver/indexengine/etc/docserver.conf mile_[[:digit:]]*/etc

	mv mile_[[:digit:]]* ${BASE_PACKAGE%.tar.gz}
	tar zcf $BASE_PACKAGE ${BASE_PACKAGE%.tar.gz}
	)

	mv _mile_/$BASE_PACKAGE .
	rm -rf _mile_
}

check_port()
{
	for p in $@ ; do
		if ss -ln | grep ":$p\>" -q ; then
			err listen port $p already in use.
		fi
	done
}

set_up()
{
	tear_down

	rm -rf $TEST_DIR
	mkdir -p $TEST_DIR

	DIR=(A B)
	DOCSERVER_PORT=(16118 26118)
	MERGESERVER_PORT=(1914 1915)

	check_port ${DOCSERVER_PORT[@]} ${MERGESERVER_PORT[@]}

	for (( i = 0; i < ${#DIR[@]}; i++)) ; do
		home=MILE${DIR[$i]}
		home=${!home}
		tar zxf $BASE_PACKAGE
		mv ${BASE_PACKAGE%.tar.gz} $home
		(
		cd $home
		sed -i 's/ERROR/DEBUG/g' etc/docserver.conf
		sed -i 's/storage_engine.*=.*/storage_engine = docdb/g' etc/docserver.conf
		sed -i 's/row_limit.*=.*/row_limit = 10/g' etc/docserver.conf

		sed -i 's#mile.log4j.path.*=.*#mile.log4j.path=tmp/log#g' etc/proxy.cfg

		sed -i "s/18518/${DOCSERVER_PORT[$i]}/g" etc/*
		sed -i "s/8964/${MERGESERVER_PORT[$i]}/g" etc/*

		cat >> etc/docserver.conf <<-EOF
		[ldb]
		tables = table1
		table1.row_key = id
		table1.time_key = time
		table1.time_key_len = 4
		EOF

		set_up_before_start $home

		./mile.sh startd
		sleep 1
		./mile.sh startm
		sleep 1

		set_up_after_start $home
		)
	done
}

tear_down()
{
	for (( i = 0; i < ${#MILES[@]}; i++)) ; do
		(
		cd ${MILES[$i]}
		./mile.sh stopm
		./mile.sh stopd
		) >/dev/null 2>&1
	done
}


### main ###

[ -f "$1" ] && source $1

base_package

set_up

echo
msg start main test ...
echo
test_main

tear_down
