#!/bin/sh
# author: tanghaiyang
# date:   Wed Jun 19 18:17:55 CST 2019

prepare_enviroment() {
    URL_GRAPH_API="http://192.168.1.57:10030"
    DIR_TMP="/tmp"
    IS_DEBUG=false

    JQ="${DIR_TMP}/jq"
    sed -n -e '1,/^exit 0$/!p' $0 > "${JQ}" 2>/dev/null
    chmod +x $JQ
}

log(){
   DATE=`date +%Y-%m-%d-%H:%M:%S`
   LOG=$1
   LINE_SEQ=$2

   if [ "x$LOG" == "x" ]
   then
         echo "LOG message is empty"
         return
   fi

   if [ "x$LINE_SEQ" == "x" ]
   then
         echo -en "$DATE | $LOG"
         return
   else
         echo -en "$DATE | line:$LINE_SEQ | $LOG"
         return
   fi
   echo ""
}

logdebug(){
   if [[ $IS_DEBUG != true ]]; then
        return
   fi
   DATE=`date +%Y-%m-%d-%H:%M:%S`
   LOG=$1
   LINE_SEQ=$2

   if [ "x$LOG" == "x" ]
   then
         echo "LOG message is empty"
         return
   fi

   if [ "x$LINE_SEQ" == "x" ]
   then
         echo -en "$DATE | $LOG"
         return
   else
         echo -en "$DATE | line:$LINE_SEQ | $LOG"
         return
   fi
   echo ""
}

api_task_findTask(){
    PARA_TASK_ID=$1
    if [ "x$PARA_TASK_ID" == "x" ]
    then
         echo "$PARA_TASK_ID is empty!"
         return
    fi
    RESULT=`curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d "{
       \"taskId\": $PARA_TASK_ID,
       \"taskState\": \"RUNNING\"
     }" "$URL_GRAPH_API/api/task/findTask" -s`
    log "get task info(api/task/findTask): $RESULT" "$LINENO"
}

api_task_detail_task(){
    PARA_TASK_ID=$1
    if [ "x$PARA_TASK_ID" == "x" ]
    then
         echo "$PARA_TASK_ID is empty!"
         return
    fi
    RESULT=`curl "$URL_GRAPH_API/api/task/detail/task?id=$PARA_TASK_ID" -s`
    #log "get task info(/api/task/detail/task): $RESULT" "$LINENO"
#    echo ""
    PARA_TASK_ID=$(echo $RESULT | $JQ ".payload.data.id")
    TASK_NAME=$(echo $RESULT | $JQ -r ".payload.data.taskName")
    GRAPH=$(echo $RESULT | $JQ -r ".payload.data.graph")
    SCHEMA=$(echo $RESULT | $JQ -r ".payload.data.schema")
    TASK_TYPE=$(echo $RESULT | $JQ -r ".payload.data.taskType")
    echo "-------------TASK INFO----------------"
    printf "%-25s%s\n" "PARA_TASK_ID:" $PARA_TASK_ID
    printf "%-25s%s\n" "TASK_NAME:" $TASK_NAME
    printf "%-25s%s\n" "graph:" $GRAPH
    printf "%-25s%s\n" "schema:" $SCHEMA
    printf "%-25s%s\n" "task_type:" $TASK_TYPE
}

api_task_runOnce(){
    PARA_TASK_ID=$1
    if [ "x$PARA_TASK_ID" == "x" ]
    then
         echo "$PARA_TASK_ID is empty!"
         return
    fi
    RESULT=`curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d "{
          \"errorMode\": -1,
          \"id\": $PARA_TASK_ID
        }" "$URL_GRAPH_API/api/task/runOnce" -s`
    #log "schedule task(/api/task/runOnce): $RESULT" "$LINENO"
    echo ""

    SCHEDULE_STATUS=$(echo $RESULT | $JQ ".success")
    TASK_STATE=$(echo $RESULT | $JQ ".task_state")
    if  [[ "$SCHEDULE_STATUS" == "true" ]];   then
       echo "----------START TASK SUCCESS----------"
       echo "SCHEDULE_STATUS:              $SCHEDULE_STATUS"
       #echo "task_instance_id:        $PARA_TASK_ID"
    else
       echo ""----------START TASK FAIL----------""
       echo "SCHEDULE_STATUS:              $SCHEDULE_STATUS"
       #echo "task_instance_id:        $PARA_TASK_ID"
       exit 10
    fi
}

api_task_detail_process(){
    PARA_TASK_ID=$1
    if [ "x$PARA_TASK_ID" == "x" ]
    then
         echo "$PARA_TASK_ID is empty!"
         return
    fi
    INSTANCE=`curl "$URL_GRAPH_API/api/task/detail/process?id=$PARA_TASK_ID" -s`
    if [ "x$INSTANCE" == "x" ]
    then
         log "get task detail(/api/task/detail/process): responce is empty!"
         return
    fi
    #log "get task instance info(/api/task/detail/process): $INSTANCE" "$LINENO"
    PARA_TASK_ID=$(echo $INSTANCE | $JQ ".payload.data.taskId")
    TASK_INSTANCE_ID=$(echo $INSTANCE | $JQ ".payload.data.taskInstanceId")
    TOTAL_ROWS=$(echo $INSTANCE | $JQ ".payload.data.totalRows")
    TOTAL_AFFECTD_ROWS=$(echo $INSTANCE | $JQ ".payload.data.totalAffectedRows")
    TOTAL_RATE=$(echo $INSTANCE | $JQ ".payload.data.totalRate")
    TASK_DISPLAY_STATE=$(echo $INSTANCE | $JQ -r ".payload.data.taskDisplayState")

    GDB_AFFECTED_ROWS=$(echo $INSTANCE | $JQ ".payload.data.gdbAffectedRows")
    ES_AFFECTED_ROWS=$(echo $INSTANCE | $JQ ".payload.data.esAffectedRows")
    HBASE_AFFECTED_ROWS=$(echo $INSTANCE | $JQ ".payload.data.hbaseAffectedRows")
    GDB_AFFECTED_RATE=$(echo $INSTANCE | $JQ ".payload.data.gdbAffectedRate")
    ES_AFFECTED_RATE=$(echo $INSTANCE | $JQ ".payload.data.esAffectedRate")
    if [[ $IS_DEBUG == true ]];then
        echo ""
        echo "-------------TASK DETAIL INFO----------------"
        echo "PARA_TASK_ID:$PARA_TASK_ID"
        echo "TASK_INSTANCE_ID:$TASK_INSTANCE_ID"
        echo "TOTAL_ROWS:$TOTAL_ROWS"
        echo "TOTAL_AFFECTD_ROWS:$TOTAL_AFFECTD_ROWS"
        echo "TOTAL_RATE:$TOTAL_RATE"
        echo "TASK_DISPLAY_STATE:$TASK_DISPLAY_STATE"
        echo "GDB_AFFECTED_ROWS:$GDB_AFFECTED_ROWS"
        echo "ES_AFFECTED_ROWS:$ES_AFFECTED_ROWS"
        echo "HBASE_AFFECTED_ROWS:$HBASE_AFFECTED_ROWS"
        echo "GDB_AFFECTED_RATE:$GDB_AFFECTED_RATE"
        echo "ES_AFFECTED_RATE:$ES_AFFECTED_RATE"
        echo "HBASE_AFFECTED_RATE:$HBASE_AFFECTED_RATE"

        echo ""
    fi
    if [ "$TOTAL_RATE" != "-1" ]
    then
        RATIO_TOTAL=$(echo "scale=0;$TOTAL_RATE*100/1" | bc)
    fi

    if [ "$GDB_AFFECTED_RATE" != "-1" ]
    then
        RATIO_GDB=$(echo "scale=0;$GDB_AFFECTED_RATE*100/1" | bc)
    fi

    if [ "$ES_AFFECTED_RATE" != "-1" ]
    then
        RATIO_ES=$(echo "scale=0;$ES_AFFECTED_RATE*100/1" | bc)
    fi

    if [ "$HBASE_AFFECTED_ROWS" != "-1" ]
    then
        RATIO_HBASE=$(echo "scale=0;$HBASE_AFFECTED_RATE*100/1" | bc)
    fi

   # total:0%(0),gdb:0%(0),es:0%(0),hbase(processed:0)(0%)
    sleep 0.1
  #  log "$PRINT_STR" "$LINENO"
    STATE_SUCCESS="SUCCESS"
    STATE_INTERRUPTED="INTERRUPTED"
    STATE_FAILED="FAILED"

    if [ ${TASK_DISPLAY_STATE}x = ${STATE_SUCCESS}x ]
    then
        echo
        logdebug "STATE: $TASK_DISPLAY_STATE import is successful !" "$LINENO"
        break
    fi

    if [ ${TASK_DISPLAY_STATE}x = ${STATE_INTERRUPTED}x ]
    then
        echo
        logdebug "STATE: $TASK_DISPLAY_STATE import task is interrupted !" "$LINENO"
        break
    fi

    if [ ${TASK_DISPLAY_STATE}x = ${STATE_FAILED}x ]
    then
        echo
        logdebug "\nSTATE: $TASK_DISPLAY_STATE import task is failed !" "$LINENO"
        break
    fi

    if [ ${TASK_DISPLAY_STATE}x != ${STATE_SUCCESS}x ]
    then
        #logdebug "STATE: $TASK_DISPLAY_STATE" "$LINENO"
     #   printf "STATE: $TASK_DISPLAY_STATE"
        continue
    fi
    log "there is some error in parse status!" "$LINENO"

}

print_endtime() {
    END_TIME=$(date +"%s")
    COST_SECONDS="$[$END_TIME-$START_TIME]"
    echo ""
    echo "---------------COMPLETE---------------"
    #log "%-25s%ss\nelasped_time: ${COST_SECONDS}" "$LINENO"
    log "elasped_time(sec): ${COST_SECONDS}"
    echo ""
}

check_task_state(){
    START_TIME=$(date +"%s")
    echo "---------------PROGRESS---------------"
    while ((loop < 1000 ))
    do
        ((loop++))
        api_task_detail_process $1
        sleep 3
    done
    print_endtime
}

process(){
    TASK_ID=$1
    prepare_enviroment
    api_task_detail_task $TASK_ID
    api_task_runOnce $TASK_ID
    check_task_state $TASK_ID
}

case "$1" in
  start)
    case "$2" in
      [0-9]*)
        process $2
        ;;
      *)
        echo $"Usage: export GRAPH_API_SERVER=$URL_GRAPH_API;$0 start task_id"
        exit 1
        ;;
    esac
    ;;
  *)
    echo $"Usage: export GRAPH_API_SERVER=$URL_GRAPH_API;$0 start task_id"
    exit 1
esac
exit 0