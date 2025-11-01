#!/bin/bash
# 变量定义，service_path等变量需要再gitlab-ci.yml配置
deploy=$DEPLOY_PATH/deploy/deploy.sh
# 按照运行变量重启应用
if [ ${cpeco_gateway} == 1 ]; then
   sh $deploy restart cpeco_gateway 512m
fi
if [ ${cpeco_monitor} == 1 ]; then
   sh $deploy restart cpeco_monitor 256m
fi
if [ ${cpeco_sentinel_dashboard} == 1 ]; then
   sh $deploy restart cpeco_sentinel_dashboard 256m
fi
if [ ${cpeco_auth} == 1 ]; then
   sh $deploy restart cpeco_auth 512m
fi
if [ ${cpeco_app_back} == 1 ]; then
   sh $deploy restart cpeco_app_back  1024m
fi
if [ ${cpeco_app_core} == 1 ]; then
   sh $deploy restart cpeco_app_core  1024m
fi
if [ ${cpeco_app_front} == 1 ]; then
   sh $deploy restart cpeco_app_front  1024m
fi
if [ ${cpeco_xxl_job_admin} == 1 ]; then
   sh $deploy restart cpeco_xxl_job_admin 512m
fi