#!/bin/bash

export LC_ALL=C
export LANG=zh.GBK
set -e

[ $(getconf LONG_BIT) -ne 64 ] && echo "WARN: Not 64-bit machine!"

perr()
{
	echo "ERROR: $*"
	exit 1
} >&2

usage()
{
	echo $(basename $0) pkg_name
	exit 1
} >&2

CONFIG_DIR=config
while getopts "c:h" opt ; do
	case $opt in
		h ) usage ; exit 1;;
		*) usage; exit 1
	esac
done
shift $((OPTIND - 1 ))
PKG_NAME=${1:-mile}


PKG_DIR=
copy()
{
	local subdir=$1
	mkdir -p "$PKG_DIR/$subdir"
	shift
	while [ $# -gt 0 ] ; do
		cp -rfv "$1" "$PKG_DIR/$subdir"
		shift
	done
}

[ -z "$PKG_NAME" ] && usage

PKG_NAME=${PKG_NAME}_$(date +%Y%m%d)
if [ -d $PKG_NAME ] || [ -f $PKG_NAME ] ; then
	perr "$PKG_NAME exist!"
fi
mkdir -p $PKG_NAME
PKG_DIR=$(readlink -f $PKG_NAME)

## begin to compile sources and copy files to package dir.
# build mergerServer
{
set -e
cd mergeserver
mvn clean package -Dmaven.test.skip=true || perr build mergerServer failed!
mv server/target/server-*.jar server/target/mergerServer.jar
mv cmd/target/cmd-*.jar cmd/target/cmd.jar
mv perf/target/perf-*.jar perf/target/perf.jar
copy sbin server/target/mergerServer.jar
copy sbin cmd/target/cmd.jar
copy sbin perf/target/perf.jar
cd ..
}

# build jsvc
# build 32-bit jsvc
(
	set -e
	cd third-party/commons-daemon-native/unix/
	sh support/buildconf.sh
	CFLAGS=-m32 LDFLAGS=-m32 ./configure
	# replace CPU define.
	sed -i 's#\\"amd64\\"#\\"i386\\"#g' Makedefs
	make clean && make
	mv jsvc jsvc.32
)
# build 64-bit jsvc
if [ 64 -eq $(getconf LONG_BIT) ] ; then
	(
		set -e
		cd third-party/commons-daemon-native/unix/
		sh support/buildconf.sh
		./configure && make clean && make
		mv jsvc jsvc.64
	)
fi
for f in third-party/commons-daemon-native/unix/jsvc.[36][24] ; do
	copy sbin $f
done
copy sbin third-party/commons-daemon-1.0.5.jar

# build docserver
make -C docserver/indexengine clean && make -C docserver/indexengine/ || perr build docserver failed!

copy etc $CONFIG_DIR/*
copy etc docserver/indexengine/etc/docserver.conf

copy sbin docserver/indexengine/src/docserver
copy sbin docserver/indexengine/src/table_meta_trans
cp -f docserver/indexengine/src/client $PKG_DIR/sbin/client
copy sbin docserver/indexengine/src/{milebinlog,data_export,update_dumped_seg_time}
copy bin docserver/indexengine/scripts/doc_run.py
copy bin docserver/indexengine/scripts/mile.sh

if [ "$CONFIG_DIR" == "config_ctu" ] ; then
	mv $PKG_DIR/etc/ctu_*.sh $PKG_DIR/bin	
else
	copy bin docserver/indexengine/scripts/monitor_index.sh
	copy bin docserver/indexengine/scripts/deploy_controller.sh
	copy bin docserver/indexengine/scripts/deploy_handler.sh
fi



# make the default package runnable.
(
	cd $PKG_DIR
	mkdir run
	ln -sfT bin/mile.sh mile.sh
)

# tar pkg.
tar zcvf $PKG_DIR.tar.gz $PKG_NAME

echo build success.

