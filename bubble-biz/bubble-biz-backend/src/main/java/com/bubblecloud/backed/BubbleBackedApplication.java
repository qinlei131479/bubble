package com.bubblecloud.backed;

import com.bubblecloud.common.security.annotation.EnableCustomResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableCustomDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018年06月21日
 * <p>
 * 用户统一管理系统
 */
@EnableCustomDoc("admin")
@EnableCustomResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleBackedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleBackedApplication.class, args);
	}

}
