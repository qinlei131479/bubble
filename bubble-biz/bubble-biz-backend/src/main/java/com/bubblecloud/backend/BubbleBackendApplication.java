package com.bubblecloud.backend;

import com.bubblecloud.common.feign.annotation.EnableCustomFeignClients;
import com.bubblecloud.common.security.annotation.EnableCustomResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户统一管理系统
 *
 * @author qinlei
 * @date 2025/05/30
 */
@EnableDoc(value = "admin")
@EnableCustomFeignClients
@EnableCustomResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleBackendApplication.class, args);
	}

}
