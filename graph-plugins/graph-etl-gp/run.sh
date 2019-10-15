#!/usr/bin/env bash

binDir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd ${binDir} && cd ../
curDir=`pwd`
packageName=${curDir##*/}
moduleName=${packageName%-*}
moduleName=${moduleName%-*}

if [ ! -n "$PRO_PATH" ]; then
    export PRO_PATH="${curDir}"
fi

profiles=$2
rootDir=${curDir}
libPath=${rootDir}/lib
sourcePath=${rootDir}/conf
logPath=${rootDir}/logs

if [ ! -d ${logPath} ]; then
    mkdir -p ${logPath}
fi

start() {
    echo "start ${moduleName}..."
    nohup java -Dloader.path=${libPath},${sourcePath} -XX:+UseConcMarkSweepGC \
    -Xmx1024m -Xms1024m -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:${logPath}/java_gc.log \
    -XX:-HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${logPath} -jar ${libPath}/${packageName}.jar \
    --spring.profiles.active=${profiles} --isJar=true &> ${logPath}/${moduleName}.log &
}

stop() {
    pid=`ps -ef | grep ${libPath}/${packageName}.jar | grep -v grep | awk '{print $2}'`
    echo "stop ${moduleName}..."
    if [ -n "${pid}" ]
    then
        kill -9 $pid
    fi
}

restart() {
    stop
    start
}

case "$1" in
	start|stop|restart)
  		case "$2" in
  		    haizhi|haizhi-fi|haizhi-ksyun|haizhi-tdh|test|uat)
  		        $1
  		        ;;
  		    *)
  		        echo $"Usage: $0 {start|stop|restart} {haizhi|haizhi-fi|haizhi-ksyun|haizhi-tdh|test|uat}"
  		        exit 2
  		esac
		;;
	*)
		echo $"Usage: $0 {start|stop|restart}"
		exit 1
esac
