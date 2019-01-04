#!/usr/bin/env bash

AONE_FLOW_HOME=$(dirname $0)/..

CONF=${AONE_FLOW_HOME}/conf

JAR_FILE="${AONE_FLOW_HOME}/lib/*.jar"

if [ -z ${JVM_OPTS} ]; then
  JVM_OPTS=" -Xms256m -Xmx512m"
fi

# Set the spring configuration file
if [ -f ${CONF}/bootstrap.yml ]; then
  SPRING_OPTS="$SPRING_OPTS --spring.config.location=file:${CONF}/application.yml"
fi

# Set the log4j configuration file
if [ -f ${CONF}/log4j.properties ]; then
    LOG4J_OPTS="-Dlog4j.configuration=file:${CONF}/log4j.properties"
fi

java ${JVM_OPTS} ${LOG4J_OPTS} -jar ${JAR_FILE} ${SPRING_OPTS} $@ &

echo $! > ${AONE_FLOW_HOME}/currentpid
