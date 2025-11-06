package com.bubblecloud.backend;

import com.bubblecloud.common.feign.annotation.EnablePigFeignClients;
import com.bubblecloud.common.security.annotation.EnablePigResourceServer;
import com.bubblecloud.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户统一管理系统
 *
 * @author qinlei
 * @date 2025/05/30
 */
@EnablePigDoc(value = "admin")
@EnablePigFeignClients
@EnablePigResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleBackendApplication.class, args);
	}

}
