#!/bin/bash

JAR_NAME="grid-trade.jar"
PID_FILE="./${JAR_NAME}.pid"

if [ -f "${PID_FILE}" ]
then
    pid=`cat ${PID_FILE}`
    is_run=`ps -f ${pid} | grep ${JAR_NAME} | wc -l`
    if [[ ${is_run} -gt 0 ]]
    then
        echo "${JAR_NAME} is already running"
        exit 1
    fi
fi

nohup java -jar ${JAR_NAME} &
echo $! > ${PID_FILE}
echo "${JAR_NAME} started"

