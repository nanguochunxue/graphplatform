#!/bin/bash

MAIN_CLASS="com.haizhi.graph.plugins.flume.BatchApplication"

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

GRAPH_NAME=$1
FILE_PATH=$2
start_batch(){
    echo "starting batch"
    RUN_ONCE="$RUN_JAVA -cp $AGENT_PATH"
    RUN_ONCE="$RUN_ONCE $JAVA_OPTS"
    RUN_ONCE="$RUN_ONCE $MAIN_CLASS -g$GRAPH_NAME -f$FILE_PATH 1>>/dev/null 2>&1 &"
    echo $RUN_ONCE
    eval $RUN_ONCE
    echo "batch process started"
}

stop_batch(){
    echo "stopping batch process"
    pid=`ps -ef | grep BatchApplication | grep -v grep | awk '{print $2}'`
    if [ -n "${pid}" ]
    then
        kill -9 $pid
        echo "batch process stopped"
    else
        echo "none batch process found. nothing will be done"
    fi
}

display_help() {
  cat <<EOF
Usage: $0 <command> <mode> [options]...
eg:
 sh bin/run-batch.sh graph conf/batch-config.properties
 sh bin/run-batch.sh stop
EOF
}

case "$1" in
    help) display_help
    ;;
    stop)
        echo "stopping batch ..."
        stop_batch
    ;;
    *)
        echo "starting batch ..."
        start_batch
    ;;
esac