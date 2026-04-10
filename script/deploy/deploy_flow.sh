#!/bin/bash
# 变量定义，service_path等变量需要再gitlab-ci.yml配置
deploy=$DEPLOY_PATH/deploy/deploy.sh
# 按照运行变量重启应用
if [ ${bubble_gateway} == 1 ]; then
   sh $deploy restart bubble_gateway 512m
fi
if [ ${bubble_monitor} == 1 ]; then
   sh $deploy restart bubble_monitor 256m
fi
if [ ${bubble_sentinel_dashboard} == 1 ]; then
   sh $deploy restart bubble_sentinel_dashboard 256m
fi
if [ ${bubble_auth} == 1 ]; then
   sh $deploy restart bubble_auth 512m
fi
if [ ${bubble_app_back} == 1 ]; then
   sh $deploy restart bubble_app_back  1024m
fi
if [ ${bubble_app_core} == 1 ]; then
   sh $deploy restart bubble_app_core  1024m
fi
if [ ${bubble_app_front} == 1 ]; then
   sh $deploy restart bubble_app_front  1024m
fi
if [ ${bubble_xxl_job_admin} == 1 ]; then
   sh $deploy restart bubble_xxl_job_admin 512m
fi