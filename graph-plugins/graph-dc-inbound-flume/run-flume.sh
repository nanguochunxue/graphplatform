#!/bin/bash

AGENT_CLASS="com.haizhi.graph.plugins.flume.FlumeApplication"

BASE_PATH=$(cd $(dirname $0)/..; pwd)

AGENT_PATH=$BASE_PATH/lib/*:""
#AGENT_PATH=$AGENT_PATH:$BASE_PATH/conf/log4j2.xml

TIME_POST=$(date +%s)
HeapDumpFile="heapdump-"$TIME_POST".hprof"
GC_LOG_DIR=$BASE_PATH/loggc/

if [ ! -d ${GC_LOG_DIR} ]; then
    mkdir -p ${GC_LOG_DIR}
fi

#set JAVA_OPTS
JAVA_OPTS="-server -Xms200m -Xmx200m -Xmn100m -Xss256k"
JAVA_OPTS="$JAVA_OPTS -XX:+AggressiveOpts"
JAVA_OPTS="$JAVA_OPTS -XX:+UseBiasedLocking"
JAVA_OPTS="$JAVA_OPTS -XX:+UseFastAccessorMethods"
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"

JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=$BASE_PATH/$HeapDumpFile"

JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintClassHistogram -XX:+PrintTenuringDistribution"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCApplicationStoppedTime -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=3 -XX:GCLogFileSize=100M -Xloggc:${GC_LOG_DIR}gc.log"

RUN_JAVA="$JAVA_HOME/bin/java"

CONFIG_PATH=$1
start_flume(){
    echo "starting flume .."
    RUN_AGENT="$RUN_JAVA -cp $AGENT_PATH -Dflume.monitoring.type=http -Dflume.monitoring.port=34545"
    RUN_AGENT="$RUN_AGENT $JAVA_OPTS"
    RUN_AGENT="$RUN_AGENT $AGENT_CLASS -nagent -f$CONFIG_PATH 1>>/dev/null 2>&1 &"
    echo $RUN_AGENT
    eval $RUN_AGENT
    echo "flume process started"
}

stop_flume(){
    echo "stopping flume"
    pid=`ps -ef | grep FlumeApplication | grep -v grep | awk '{print $2}'`
    if [ -n "${pid}" ]
    then
        kill -9 $pid
        echo "flume process stopped"
    else
        echo "none flume process found. nothing will be done"
    fi
}

display_help() {
  cat <<EOF
Usage: $0 <command> <mode> [options]...
eg:
 sh bin/run-flume.sh conf/inbound-flume.properties
 sh bin/run-flume.sh stop
EOF
}

PARAM_LENGTH=$#
if [ $PARAM_LENGTH -le 1 ]; then
    display_help
fi

case "$1" in
    help)
        echo "show help info ..."
        display_help
    ;;
    stop)
        echo "stopping flume ..."
        stop_flume
    ;;
    *)
        echo "starting flume ..."
        start_flume
    ;;
esac