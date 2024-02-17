#!/bin/bash

JAR_NAME="grid-trade.jar"
PID_FILE="./${JAR_NAME}.pid"

if [ -f "./switch.txt" ]
then
  rm -rf ./switch.txt
fi

if [ -f "${PID_FILE}" ]
then
    pid=`cat ${PID_FILE}`
    is_run=`ps -f ${pid} | grep ${JAR_NAME} | wc -l`
    while [ ${is_run} -gt 0 ]
    do
        echo "${JAR_NAME} is already running"
        sleep 1s
        is_run=`ps -f ${pid} | grep ${JAR_NAME} | wc -l`
    done
fi

echo "${JAR_NAME} stopped"