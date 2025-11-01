package  com.pig4cloud.pig.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author lengleng
 * @date 2018年06月21日 监控中心
 */
@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class PigMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigMonitorApplication.class, args);
	}

}
