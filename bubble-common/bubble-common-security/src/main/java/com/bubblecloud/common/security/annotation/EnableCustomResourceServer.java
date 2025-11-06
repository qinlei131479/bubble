package com.bubblecloud.common.security.annotation;

import com.bubblecloud.common.security.component.CustomResourceServerAutoConfiguration;
import com.bubblecloud.common.security.component.CustomResourceServerConfiguration;
import com.bubblecloud.common.security.feign.CustomFeignClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Pig资源服务器注解
 * <p>
 * 通过导入相关配置类启用Pig资源服务器功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Import({ CustomResourceServerAutoConfiguration.class, CustomResourceServerConfiguration.class,
		CustomFeignClientConfiguration.class })
public @interface EnableCustomResourceServer {

}
