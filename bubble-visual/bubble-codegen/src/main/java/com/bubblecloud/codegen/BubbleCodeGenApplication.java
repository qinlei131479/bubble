package com.bubblecloud.codegen;

import com.pig4cloud.pig.common.datasource.annotation.EnableDynamicDataSource;
import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.bubblecloud.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018/07/29 代码生成模块
 */
@EnableDynamicDataSource
@EnablePigDoc("gen")
@EnableDiscoveryClient
@EnablePigResourceServer
@SpringBootApplication
public class BubbleCodeGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleCodeGenApplication.class, args);
	}

}
