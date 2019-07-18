#!/bin/bash

ulimit -n
source /etc/profile

#-------------------------------------------------------------------
#    Mb Bootstrap Script 
#-------------------------------------------------------------------


# find Mb home.
CURR_DIR=`pwd`
cd `dirname "$0"`/..
RESV_HOME=`pwd`
cd $CURR_DIR

if [ -z "$RESV_HOME" ] ; then
    echo
    echo Must set RESV_HOME
    echo
    exit 1
fi

echo RESV_HOME=$RESV_HOME

if [ ! -d "$RESV_HOME/logs" ]; then
  mkdir "$RESV_HOME/logs"
fi

CLASSPATH=$RESV_HOME/ctr.jar

DEBUG_INFO=" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n"
DEBUG=""

case $1  in
        "start") MAIN_CLASS="";
                DEFAULT_OPTS="-server -Xms512m -Xmx1024m -Xmn256M -Xss512k -Djava.awt.headless=true -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=127.0.0.1" ;;


        *) echo "start.sh  cmd ,cmd maybe:prop,room,rateplan";exit;;
         esac;

shift;

DEFAULT_OPTS="$DEFAULT_OPTS  -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=60 -Xloggc:$RESV_HOME/logs/gc.log -Dfile.encoding=UTF-8 "
DEFAULT_OPTS="$DEFAULT_OPTS -DMB.home=\"$RESV_HOME\""
DEFAULT_OPTS="$DEFAULT_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$RESV_HOME/logs "
DEFAULT_OPTS="$DEFAULT_OPTS -XX:MaxDirectMemorySize=96M -Dio.netty.allocator.type=pooled "

echo "java $DEBUG -jar $DEFAULT_OPTS $CLASSPATH >> "$RESV_HOME/logs/ctr.out" 2>&1 &"

nohup java $DEBUG -jar $DEFAULT_OPTS $CLASSPATH >> "$RESV_HOME/logs/ctr.out" 2>&1 &
