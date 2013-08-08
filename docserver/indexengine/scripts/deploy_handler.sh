#!/bin/bash
# This script should in $MILE_HOME/bin dir.

set -e

####################################################
## configure session

MILE_HOME=/home/admin/mile # must be absolute path.

# true or false
RUN_DOCSERVER=true
RUN_MERGERSERVER=true

# monitor or ...
CONFIG_MODULE=monitor

# docserver role, master or slave
DOCSERVER_ROLE=master

## configure session end.
#####################################################

perr()
{
	echo ERROR "$*"
	exit 1
} >&2

usage()
{
	echo "$(basename $0) start|stop"
	echo OR:
	echo "$(basename $0) deploy package"
	exit 1
} >&2

# check configure

RUN_D=0
[ $RUN_DOCSERVER = true ] && RUN_D=1
RUN_M=0
[ $RUN_MERGERSERVER = true ] && RUN_M=1

(( !RUN_D )) && (( !RUN_M )) && perr "should run docserver or mergerserver."

MILE_SCRIPT="$MILE_HOME/bin/mile.sh -d $MILE_HOME"

start()
{
	# start docserver first.
	((RUN_D)) && eval $MILE_SCRIPT startd
	((RUN_M)) && eval $MILE_SCRIPT startm
}

stop()
{
	# stop merger server first.
	((RUN_M)) && eval $MILE_SCRIPT stopm
	((RUN_D)) && eval $MILE_SCRIPT stopd
}

deploy()
{

	TMP_DIR=/tmp/.mile_deploy

	stop || : # ignore return value.

	local pkg_name=$1
	local dir_name=${pkg_name%.tar.gz}
	trap "rm -rf $TMP_DIR" EXIT
	(
		cd $TMP_DIR
		tar zxvf $pkg_name

		# backup itself
		cp -f $MILE_HOME/bin/deploy_handler.sh{,.bak}

		cp -arTfv $dir_name $MILE_HOME

		# restore itself
		cp -f $MILE_HOME/bin/deploy_handler.sh{.bak,}
	)

	# update log dir
	export DOCSERVER_ROLE
	eval $MILE_SCRIPT update_config
}

case $1 in 
	start )
		start
		;;
	stop )
		stop
		;;
	deploy )
		[ -z "$2" ] && usage
		deploy "$(basename "$2")"
		;;
	* )
		usage
esac

