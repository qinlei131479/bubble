package com.bubblecloud.common.security.annotation;

import com.bubblecloud.common.security.component.CustomResourceServerAutoConfiguration;
import com.bubblecloud.common.security.component.CustomResourceServerConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.lang.annotation.*;

/**
 * @author lengleng
 * @date 2022-06-04
 * <p>
 * 资源服务注解
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({ CustomResourceServerAutoConfiguration.class, CustomResourceServerConfiguration.class })
public @interface EnableCustomResourceServer {

}
