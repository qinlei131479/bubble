package  com.pig4cloud.pig.common.feign;

import com.pig4cloud.pig.common.feign.retry.FeignRetryAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

/**
 * 重试配置
 *
 * @author lengleng
 * @date 2023年03月09日
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RetryTemplate.class)
public class PigFeignRetryAutoConfiguration {

	@Bean
	public FeignRetryAspect feignRetryAspect() {
		return new FeignRetryAspect();
	}

}
