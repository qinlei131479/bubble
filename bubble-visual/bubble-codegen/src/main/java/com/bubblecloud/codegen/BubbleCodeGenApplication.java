package com.bubblecloud.codegen;

import com.bubblecloud.common.datasource.annotation.EnableDynamicDataSource;
import com.bubblecloud.common.feign.annotation.EnablePigFeignClients;
import com.bubblecloud.common.security.annotation.EnablePigResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 代码生成模块应用启动类
 *
 * @author qinlei
 * @date 2025/05/31
 */
@EnableDynamicDataSource
@EnablePigFeignClients
@EnableDoc("gen")
@EnableDiscoveryClient
@EnablePigResourceServer
@SpringBootApplication
public class BubbleCodeGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleCodeGenApplication.class, args);
	}

}
