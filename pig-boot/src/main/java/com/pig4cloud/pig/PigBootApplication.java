package  com.pig4cloud.pig;

import com.pig4cloud.pig.common.security.annotation.EnablePigResourceServer;
import com.pig4cloud.pig.common.swagger.annotation.EnablePigDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lengleng 单体版本启动器，只需要运行此模块则整个系统启动
 */
@EnablePigDoc(value = "admin", isMicro = false)
@EnablePigResourceServer
@SpringBootApplication
public class PigBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigBootApplication.class, args);
	}

}
