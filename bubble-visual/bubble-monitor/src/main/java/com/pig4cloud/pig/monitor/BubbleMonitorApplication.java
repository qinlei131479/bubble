package com.pig4cloud.pig.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 监控中心应用启动类
 *
 * @author lengleng
 * @date 2018/06/21
 */
@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleMonitorApplication.class, args);
	}

}
