package com.bubblecloud.biz.oa;

import com.bubblecloud.common.security.annotation.EnableCustomResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * OA模块启动类
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@EnableDoc("oa")
@EnableCustomResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleOaApplication {
	public static void main(String[] args) {
		SpringApplication.run(BubbleOaApplication.class, args);
	}
}
