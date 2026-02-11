package com.bubblecloud.biz.agi;

import com.bubblecloud.common.feign.annotation.EnableCustomFeignClients;
import com.bubblecloud.common.security.annotation.EnableCustomResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * AGI模块启动类
 *
 * @author Rampart Qin
 * @date 2026/2/10 下午2:57
 */
@EnableDoc("agi")
@EnableCustomFeignClients
@EnableCustomResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleAgiApplication {
	public static void main(String[] args) {
		SpringApplication.run(BubbleAgiApplication.class, args);
	}
}
