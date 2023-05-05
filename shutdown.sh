#!/bin/bash

FPID=/var/run/java-client.pid

is_kill=0
i=0

if [ -f "$FPID" ];then
    PID=$(cat $FPID)
    if [ -d /proc/$PID ];then
        while [[ 1 ]];do
            if [ -d /proc/$PID ];then
                kill $PID
                sleep 3
                if [ -d /proc/$PID ];then
                    if [ $i -eq 400 ];then
                        is_kill=255
                        break
                    fi
                fi
            else
                break
            fi
            let i++
        done
    fi
fi


if [ $is_kill -eq 0 ];then
    ps -ef | grep -sq "/[d]ata/phemex/java-client/artifacts"
    if [ $? -eq 0 ];then
        echo " ------------------------------"
        echo "Error: Multi Process java-client"
        exit 254
    else
        echo " ==============================="
        echo "Success: Shutdown Process java-client"
        [ -f "$FPID" ] && rm -rf $FPID
        exit 0
    fi
else
    echo " ------------------------------"
    echo "Error: Shutdown Process Failure java-client"
    exit 255
fi