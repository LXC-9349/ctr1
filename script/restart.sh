#!/bin/bash

#-------------------------------------------------------------------
#    Mb Bootstrap Script 
#-------------------------------------------------------------------

CURR_DIR=`pwd`
cd `dirname "$0"`/..
RESV_HOME=`pwd`
echo `whoami`
cd $CURR_DIR

if [ -z "$RESV_HOME" ] ; then
    echo
    echo Must set RESV_HOME
    echo
    exit 1
fi
pid=`ps -ef |grep ctr |grep -v grep | grep -v "start" |awk '{print $2}'`
if [ -z "$pid" ]; then
    echo " not runing"
else
    echo "killing process : $pid"
    echo "ctr stop..."
    kill -9 $pid
fi
sleep 3
echo "restart ... "
sudo su -m root -c "$RESV_HOME/bin/start.sh start &"
