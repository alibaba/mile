#!/bin/bash

KEEP_DURATION=$((60 * 60 * 24 * 5))

STORAGE_DIR=/home/admin/data/storage/
TABLE_NAME=monitor_history

DEST_HOSTS=( monitorhismile-60-{1..5} )
DEST_DIR=/disk11/mile_data/upload/$(hostname)/$(date +%F)

NOTIFY_PIPE=/tmp/.mile_seg_migrate_pipe.$TABLE_NAME
HOME_DIR=/home/admin/newmile

client()
{
	$HOME_DIR/sbin/client -c '127.0.0.1:19518' -l "$*"
}

cd $HOME_DIR
client stat $TABLE_NAME 1 2>&1 | tail -n +2 | awk -v duration=$KEEP_DURATION 'BEGIN{time = systime() - duration} {if( $3 > 0 && $3 < time) print $1}' |
while read seg_num ; do
	client unload $TABLE_NAME $seg_num
	dest_host=${DEST_HOSTS[$((seg_num%${#DEST_HOSTS[*]}))]}

	dumped_dir=$(printf '%s/%s/%s_segment_%06d' $STORAGE_DIR $TABLE_NAME $TABLE_NAME $seg_num)_dump
	dumped_seg_name=$(basename $dumped_dir)

	# upload
	ssh -n $dest_host "mkdir -p $DEST_DIR && rm -rf $DEST_DIR/$dumped_seg_name{.tar.gz,.tmp}" &&
		( cd $dumped_dir/.. ; tar cf - $dumped_seg_name | gzip --fast -c - | ssh $dest_host "cd $DEST_DIR ; cat > $dumped_seg_name.tmp" )

	# rename and notify
	ssh -n $dest_host "mv $DEST_DIR/$dumped_seg_name{.tmp,.tar.gz} && echo $seg_num >> $NOTIFY_PIPE" &&
		rm -rf $dumped_dir
done
