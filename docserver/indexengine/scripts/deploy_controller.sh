#!/bin/bash

set -e

####################################################
## configure session

MILE_HOME=/home/admin/mile # must be absolute path.
REMOTE_USER=

## configure session end.
#####################################################

perr()
{
	echo ERROR "$*"
	exit 1
} >&2

usage()
{
	echo "$(basename $0) host start|stop"
	echo OR:
	echo "$(basename $0) host deploy package"
	exit 1
} >&2

REMOTE_USER=${REMOTE_USER:-$USER}

TMP_DIR=/tmp/.mile_deploy

host=$1
cmd=$2

shift 2 || usage

case "$cmd" in
	start | stop );;
	deploy )
		pkg="$1"
		[ -f "$pkg" ] || perr $pkg not exist
		ssh -n $REMOTE_USER@$host mkdir -p $TMP_DIR
		scp "$pkg" $REMOTE_USER@$host:$TMP_DIR
		;;
	*)
		usage
esac

ssh -n $REMOTE_USER@$host "cd $MILE_HOME && bash ./bin/deploy_handler.sh $cmd $*"

