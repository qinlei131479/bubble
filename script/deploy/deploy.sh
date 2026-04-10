#!/bin/bash

# 获取base路径
BASE_PATH=`cd $(dirname $0); pwd -P`

# 读取配置文件（APP_NAME，APP_PORT，HEALTH_CHECK_URL，APP_START_CHECK_INTERVAL，APP_START_CHECK_NUM）
source ${BASE_PATH}/$2.properties
source ${BASE_PATH}/version.properties
# 自定义xmx参数
if [[ $3 ]];then
  JAR_XMX=$3
fi
echo "JAR_XMX:${JAR_XMX}"

PROG_NAME=$0
ACTION=$1

# jar包的名字
JAR_NAME=${BASE_PATH}/${JAR_PATH}

# 应用的启动日志
JAVA_OUT=${BASE_PATH}/logs/start_${APP_NAME}.log

# 创建出相关目录
mkdir -p ${BASE_PATH}/logs

# 应用的版本，version.properties中Version变量
VERSION=${Version}

usage() {
    echo "Usage: $PROG_NAME {start|stop|restart|monitor}"
    exit 2
}

health_check() {
    exptime=0
    echo "checking ${HEALTH_CHECK_URL}"
    while true
        do
            status_code=`/usr/bin/curl -L -o /dev/null --connect-timeout 5 -s -w %{http_code}  ${HEALTH_CHECK_URL}`
            if [ "$?" != "0" ]; then
               echo -n -e "\rapplication not started"
            else
                echo "code is $status_code"
                if [ "$status_code" == "200" ];then
                    break
                fi
            fi
            sleep ${APP_START_CHECK_INTERVAL}
            ((exptime++))

            echo -e "\rWait app to pass health check: $exptime..."

            if [ $exptime -gt ${APP_START_CHECK_NUM} ]; then
                echo 'app start failed'
               exit 1
            fi
        done
    echo "check ${HEALTH_CHECK_URL} success"
}

start_application() {
    echo "starting java process"
    echo java -Xmx${JAR_XMX} -jar ${JAR_NAME} --spring.profiles.active=${SPRING_PROFILES_ACTIVE} --server.port=${APP_PORT} --instance.version=${VERSION}
    nohup java -Xmx${JAR_XMX} -jar ${JAR_NAME} --spring.profiles.active=${SPRING_PROFILES_ACTIVE} --server.port=${APP_PORT} --instance.version=${VERSION}> ${JAVA_OUT} 2>&1 &
    echo "started java process"
}

stop_application() {
   checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`

   if [[ ! $checkjavapid ]];then
      echo -e "\rno java process"
      return
   fi

   echo "stop java process"
   times=60
   for e in $(seq 60)
   do
        sleep 1
        COSTTIME=$(($times - $e ))
        checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`
        if [[ $checkjavapid ]];then
            kill -9 $checkjavapid
            echo -e  "\r        -- stopping java lasts `expr $COSTTIME` seconds."
        else
            echo -e "\rjava process has exited"
            break;
        fi
   done
   echo ""
}

start() {
    start_application
    health_check
}
stop() {
    stop_application
}

monitor() {
   checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`

   if [[ ! $checkjavapid ]];then
     echo "$APP_NAME is not running,and restarting..."
     start
   else
     echo -e "\r$APP_NAME is running..."
   fi
}
case "$ACTION" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    monitor)
        monitor
    ;;
    restart)
        stop
        start
    ;;
    *)
        usage
    ;;
esac
