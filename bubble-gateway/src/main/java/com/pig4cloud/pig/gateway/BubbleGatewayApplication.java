package com.pig4cloud.pig.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关应用
 *
 * @author qinlei
 * @date 2025/05/30
 */
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleGatewayApplication.class, args);
	}

}
