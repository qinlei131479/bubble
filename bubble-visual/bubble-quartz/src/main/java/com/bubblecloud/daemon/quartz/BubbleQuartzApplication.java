package com.bubblecloud.daemon.quartz;

import com.bubblecloud.common.security.annotation.EnableCustomResourceServer;
import com.bubblecloud.common.swagger.annotation.EnableCustomDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author frwcloud
 * @date 2023-07-05
 */
@EnableCustomDoc(value = "job")
@EnableCustomResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleQuartzApplication.class, args);
	}

}
