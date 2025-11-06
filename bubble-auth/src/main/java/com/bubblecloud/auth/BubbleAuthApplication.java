package com.bubblecloud.auth;

import com.bubblecloud.common.feign.annotation.EnableCustomFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权中心应用启动类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@EnableCustomFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleAuthApplication.class, args);
	}

}
