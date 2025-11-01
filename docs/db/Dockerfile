FROM registry.cn-hangzhou.aliyuncs.com/dockerhub_mirror/mysql-server:8.0.32

MAINTAINER lengleng(wangiegie@gmail.com)

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY paopao_oa.sql /docker-entrypoint-initdb.d

COPY paopao_oa_config.sql /docker-entrypoint-initdb.d
