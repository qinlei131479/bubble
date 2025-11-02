package  com.pig4cloud.pig.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018年06月21日 认证授权中心
 */
@EnableDiscoveryClient
@SpringBootApplication
public class BubbleAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(BubbleAuthApplication.class, args);
	}

}
