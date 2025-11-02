package com.bubblecloud.backed;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018年06月21日
 * <p>
 * 用户统一管理系统
 */
@EnablePigDoc("admin")
@EnablePigResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleBackedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleBackedApplication.class, args);
	}

}
