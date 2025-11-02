package com.bubblecloud.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author qinlei
 * @date 2025年11月02日 监控中心
 */
@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleMonitorApplication.class, args);
	}

}
