#!/bin/bash

# load java environment configure.
source /etc/profile

# open the core
ulimit -c unlimited


LANG_BAK=$LANG
export LC_ALL=C
set -e

HOME_DIR=$(dirname $(dirname $(readlink -f $0)))

error_exit()
{
	[ $# -gt 0 ] && echo "$*"
	echo FAILED, exit ...
	exit 1
} >&2

startd()
{
	sed -i "s/__HOST_NAME__/$(hostname)/g" etc/*.*
	./bin/doc_run.py -d . -k start
}

stopd()
{
	./bin/doc_run.py -d . -k stop
}

detect_java_bit()
{
	file -b $(readlink -f $(which java)) | sed 's/^[^[:digit:]]*\([[:digit:]]*\).*/\1/'
}

startm()
{
	merger_server start
}

stopm()
{
	merger_server stop
}


startc()
{
	[ -n "$LANG_BAK" ] && {
		export LANG=$LANG_BAK
		export LC_ALL=$LANG_BAK
	}
	java -jar ./sbin/cmd.jar "$@"
}


merger_server()
{
	local JSVC_OPT=
	[ "$1" = stop ] && JSVC_OPT=-stop
	local pid_file=run/mergerServer.pid
	sbin/jsvc.$(detect_java_bit) $JSVC_OPT -pidfile $pid_file -outfile /dev/null -errfile /dev/null \
		-XX:+UseParNewGC -Xms1024m -Xmx2048m -XX:MaxNewSize=256m -XX:NewSize=256m -XX:PermSize=192m -XX:MaxPermSize=256m -XX:+UseConcMarkSweepGC \
		-verbose:gc -Xloggc:/home/admin/logs/$(hostname)/gc.log  -XX:+PrintGCDetails -XX:+PrintGCTimeStamps \
		-cp ./sbin/commons-daemon-1.0.5.jar:./sbin/mergerServer.jar com.alipay.mile.server.Main -mile.home=./etc
}

update_config()
{
	sed -i "s/__HOST_NAME__/$(hostname)/g" etc/*.*

	# update docserver role
	sed -i '/^[[:space:]]*role[[:space:]]*=/s/=.*/= '"${DOCSERVER_ROLE:-master}"'/g' etc/docserver.conf
}

usage()
{
	cat <<EOF
$(basename $0) [ -d MILE_HOME_DIR ] startd|stopd|startm|stopm|update_config
	-d MILE_HOME_DIR      switch to MILE_HOME_DIR
	startd, stopd         start, stop docserver
	startm, stopm         start, stop mergeserver
	startc				  start cmd client

OR:
$(basename $0) -h
	Show this help and exit.
EOF
}

while getopts "hd:" opt ; do
	case $opt in
	h ) usage; exit 1;;
	d ) HOME_DIR=$OPTARG ;;
	esac
done

shift $((OPTIND-1))
cmd=$1
shift 1 || :

[ -z "$cmd" ] && { usage; exit 1;}
declare -f "$cmd" >/dev/null 2>&1 || { usage; exit 1;}

cd $HOME_DIR || error_exit switch to $HOME_DIR failed
"$cmd" "$@"

