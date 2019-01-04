#!/usr/bin/env bash

set -o nounset   # exit the script if you try to use an uninitialised variable
set -o errexit   # exit the script if any statement returns a non-true return value

#---
# is_process_running: Checks if a process is running
# args:               Process ID of running proccess
# returns:            returns 0 if process is running, 1 if not found
#---
function is_process_running {
  local  pid=$1
  kill -0 ${pid} > /dev/null 2>&1 #exit code ($?) is 0 if pid is running, 1 if not running
  local  status=$?              #because we are returning exit code, can use with if & no [ bracket
  return ${status}
}

#---
# kill_process_with_retry: Checks and attempts to kill the running process
# args:                    PID, process name, number of kill attempts
# returns:                 returns 0 if kill succeds or nothing to kill, 1 if kill fails
# exception:               If passed a non-existant pid, function will forcefully exit
#---
function kill_process_with_retry {
   local pid="$1"
   local process_name="$2"
   local max_attempt="$3"
   local sleep_time=3

   if ! is_process_running ${pid} ; then
     echo "ERROR: process name ${process_name} with pid: ${pid} not found"
     exit 1
   fi

   for try in $(seq 1 ${max_attempt}); do
      echo "Killing $process_name. [pid: $pid], attempt: $try"
      kill ${pid}
      sleep ${sleep_time}
      if is_process_running ${pid}; then
        echo "$process_name is not dead [pid: $pid]"
        echo "sleeping for ${sleep_time} seconds before retry"
        sleep ${sleep_time}
      else
        echo "shutdown succeeded"
        return 0
      fi
   done

   echo "Error: unable to kill process for $max_attempt attempt(s), killing the process with -9"
   kill -9 ${pid}
   sleep ${sleep_time}

   if is_process_running ${pid}; then
      echo "$process_name is not dead even after kill -9 [pid: $pid]"
      return 1
   else
    echo "shutdown succeeded"
    return 0
   fi
}
