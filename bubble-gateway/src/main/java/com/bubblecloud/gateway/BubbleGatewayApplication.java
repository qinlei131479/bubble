package com.bubblecloud.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018年06月21日
 * <p>
 * 网关应用
 */
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleGatewayApplication.class, args);
	}

}
