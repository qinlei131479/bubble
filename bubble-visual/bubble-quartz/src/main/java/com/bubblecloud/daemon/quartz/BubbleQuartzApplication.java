package com.bubblecloud.daemon.quartz;

import com.bubblecloud.common.feign.annotation.EnablePigFeignClients;
import com.bubblecloud.common.security.annotation.EnablePigResourceServer;
import com.bubblecloud.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * PigQuartz应用启动类
 * <p>
 * 集成定时任务、Feign客户端、资源服务及服务发现功能
 *
 * @author qinlei
 * @author frwcloud
 * @date 2025/05/31
 */
@EnablePigDoc("job")
@EnablePigFeignClients
@EnablePigResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleQuartzApplication.class, args);
	}

}
