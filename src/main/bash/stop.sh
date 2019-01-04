#!/usr/bin/env bash

set -o nounset
source "$(dirname $0)/util.sh"

install_dir="$(dirname $0)/.."
max_attempt=3
p_name="aoneflow"
{
    pid=`cat ${install_dir}/currentpid`
} || {
    if [[ -z "$pid" ]];
    then
        pid=`ps -ef|grep ${p_name}|grep -v grep|awk '{print $2}'`
    fi
}
echo ${pid}

kill_process_with_retry "${pid}" "${p_name}" "${max_attempt}"

if [[ $? == 0 ]]; then
  rm -f ${install_dir}/currentpid
  exit 0
else
  exit 1
fi