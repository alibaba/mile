#! /bin/sh

COUNTER=1
while [ "$COUNTER" -lt $1 ]; do
#  nohup ./test3 10.253.34.202&
	./test3 10.253.34.202&
  COUNTER=$(($COUNTER+1))
done
