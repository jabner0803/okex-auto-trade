#!/bin/bash

FPID=/var/run/java-client.pid


is_running=1
if [ -f $FPID ];then
    PID=$(cat $FPID)
    [ -d /proc/$PID ] && is_running=0
fi
if [ $is_running -eq 1 ];then
    ps -ef | grep -sq "/[d]ata/phemex/java-client/artifacts"
    [ $? -eq 0 ] && is_running=0
fi
if [ $is_running -eq 0 ];then
    echo " ------------------------------"
    echo "ErrorExit: Process is Running java-client"
    exit 254
fi


function get_jar(){
    if [ $(ls /data/phemex/java-client/artifacts | wc -l) -gt 0 ];then
        for file in $(ls /data/phemex/java-client/artifacts); do
            if [ "$file" == "metadata" ];then
                continue
            else
                echo $file
            fi
        done
    else
        echo "Error: Jar Name Error!"
        exit 255
    fi
}


#===========================================================================================
# JVM Configuration
#===========================================================================================
JAR_NAME=$(get_jar)

JAVA_OPT="-Djava.security.egd=file:/dev/../dev/urandom -Dfile.encoding=UTF-8 -Duser.timezone=UTC"
JAVA_OPT="$JAVA_OPT -XX:+UseG1GC -XX:MaxGCPauseMillis=5000 -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime"
JAVA_OPT="$JAVA_OPT -XX:OnOutOfMemoryError=\"/data/phemex/java-client/bin/oomcheck.sh oom_alert java-client\""
JAVA_OPT="$JAVA_OPT -Xmx512M -Xms512M"
JAVA_OPT="$JAVA_OPT "

if [ "java-client" == "tezoswalletthird" ]; then
    JAVA_OPT="$JAVA_OPT -Dio.netty.leakDetection.level=paranoid -Dio.netty.leakDetection.targetRecords=10"
elif [ "java-client" == "phemex-train" ]; then
    JAVA_OPT="$JAVA_OPT -Dio.netty.leakDetection.level=paranoid -Dio.netty.leakDetection.targetRecords=10"
    JAVA_OPT="$JAVA_OPT -Xloggc:/data/phemex/java-client/logs/gc.log"
    JAVA_OPT="$JAVA_OPT -Dlogging.config=/data/phemex/java-client/conf/java-client.xml"
else
    JAVA_OPT="$JAVA_OPT -Xloggc:/data/phemex/java-client/logs/gc.log"
    JAVA_OPT="$JAVA_OPT -Dlogging.config=/data/phemex/java-client/conf/java-client.xml"
fi

JAVA_OPT="$JAVA_OPT -jar /data/phemex/java-client/artifacts/$JAR_NAME"
JAVA_OPT="$JAVA_OPT --spring.config.location=/data/phemex/java-client/conf/java-client.properties"


source /etc/profile
java -version

MONITORFILE="/data/phemex/monitor/node_exporter/textfile_collector/phemex_oom_error.prom"
[[ -f $MONITORFILE ]] && rm -f $MONITORFILE
ls -tlh /data/phemex/monitor/node_exporter/textfile_collector/

# Start
echo " ==============================="
echo "CLI:java $JAVA_OPT"


cd /data/phemex/java-client/
> nohup.out
cmd="nohup java $JAVA_OPT 2>&1 &"
eval $cmd
#nohup bash -c "java $JAVA_OPT" 2>&1 &
PID=$!
echo $PID > $FPID
echo "PID: $PID"


# Check
i=1
while [[ 1 ]];do
    sleep 2
    if [ -d /proc/$PID ];then
        break
    else
        if [ $i -eq 10 ];then
            echo " ------------------------------"
            echo "Error: Startup Process Failure"
            exit 255
        fi
    fi
    let i++
done