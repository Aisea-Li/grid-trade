#!/bin/bash

git reset --hard origin/main

git pull

mvn clean package -Dmaven.test.skip=true

APP_HOME="/app/grid-trade"
JAR_NAME="grid-trade.jar"

if [ ! -d "${APP_HOME}" ]
then
   mkdir ${APP_HOME}
fi

cp ./target/${JAR_NAME} ${APP_HOME}/${JAR_NAME}

cp ./start.sh ${APP_HOME}/start.sh

cp ./stop.sh ${APP_HOME}/stop.sh

cd ${APP_HOME}

sh stop.sh

sh start.sh

exit 0