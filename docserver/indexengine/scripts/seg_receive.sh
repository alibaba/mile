#!/bin/bash

TABLE_NAME=monitor_history
SCAN_DIR=/disk11/mile_data/upload

source /home/admin/util/seg_receive.conf
# STORAGE_DIR=( /disk{1..5}/mile_data/storage )
# BACKUP_DIR=( /disk{6..10}/mile_backup/ )

NOTIFY_PIPE=/tmp/.mile_seg_migrate_pipe.$TABLE_NAME
LOCK_FILE=/tmp/.mile_seg_migrate_lock.$TABLE_NAME

HOME_DIR=/home/admin/mile

lockfile -1 -r 5 $LOCK_FILE
if [ 0 -ne $? ] ; then
	echo lock $LOCK_FILE failed, maybe another $0 is running.
	exit 1
fi >&2

trap "rm -f $LOCK_FILE" EXIT

mkfifo $NOTIFY_PIPE
exec 4<>$NOTIFY_PIPE

next_seg_id()
{
	NEXT_SEG_ID_=$(printf "%s\n" $1/$TABLE_NAME*[[:digit:]] | (grep -o '[1-9][0-9]*$' ; echo 0) | sort -u -n | awk 'BEGIN{old=0}{if ($1 != 0 && $1 != old + 1) {exit 0} old=$1}END{print old + 1}')
	[ -z "$NEXT_SEG_ID_" ] && NEXT_SEG_ID_=0
}

client()
{
	echo $HOME_DIR/sbin/client -c '127.0.0.1:18518' -l "$*"
	$HOME_DIR/sbin/client -c '127.0.0.1:18518' -l "$*"
}

load()
(
	storage_dir=$1
	file=$2

	seg_name=${file%.tar.gz}
	seg_name=${seg_name##*/}

	tmp_dir=$storage_dir/$TABLE_NAME
	mkdir -p $tmp_dir

	tar -C $tmp_dir -zxf $file
	dir=$tmp_dir/$seg_name

	next_seg_id ${STORAGE_DIR[0]}/$TABLE_NAME

	if ! [ ${STORAGE_DIR[0]} = $storage_dir ] ; then
		new_dir=$(printf "%s/${TABLE_NAME}_segment_%06d" $(dirname $dir) $NEXT_SEG_ID_)
		mv $dir $new_dir
		tmp_dir=${STORAGE_DIR[0]}/$TABLE_NAME
		mkdir -p $tmp_dir
		dir=$tmp_dir/$seg_name
		ln -sT $new_dir $dir
	fi


	client load $TABLE_NAME $NEXT_SEG_ID_ $dir
)

backup()
(
	backup_dir=$1
	file=$2

	date=$(basename $(dirname $file) )
	host=$(basename $(dirname $(dirname $file) ) )

	dir=$backup_dir/$host/$date/
	mkdir -p $dir
	cp -rf $file $dir
)

while true ; do
	while read -u 4 ; do
		for file in $SCAN_DIR/*/*/$TABLE_NAME*dump.tar.gz ; do
			[ -f $file ] || continue
			hash_value=$(echo -n $file | md5sum | xxd -r -p | hexdump -n 4 -e '1/4 "%u\n"')
			index=$((hash_value%${#STORAGE_DIR[*]}))

			load ${STORAGE_DIR[$index]} $file

			backup ${BACKUP_DIR[$index]} $file

			rm -f $file
		done
	done
done

