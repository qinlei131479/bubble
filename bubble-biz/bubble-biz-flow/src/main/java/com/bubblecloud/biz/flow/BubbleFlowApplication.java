package com.bubblecloud.biz.flow;

import com.bubblecloud.common.security.annotation.EnableCustomResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableCustomDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 工作流模块启动类
 *
 * @author Rampart Qin
 * @date 2020/3/26 下午2:57
 */
@EnableCustomDoc("flow")
@EnableCustomResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleFlowApplication {
	public static void main(String[] args) {
		SpringApplication.run(BubbleFlowApplication.class, args);
	}
}
