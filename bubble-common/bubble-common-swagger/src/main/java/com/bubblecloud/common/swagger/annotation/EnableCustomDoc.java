package com.bubblecloud.common.swagger.annotation;

import com.bubblecloud.common.swagger.support.SwaggerProperties;
import com.bubblecloud.common.core.factory.YamlPropertySourceFactory;
import com.bubblecloud.common.swagger.config.OpenAPIDefinitionImportSelector;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import java.lang.annotation.*;

/**
 * 开启 spring doc
 *
 * @author lengleng
 * @date 2022-03-26
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableConfigurationProperties(SwaggerProperties.class)
@Import({ OpenAPIDefinitionImportSelector.class })
@PropertySource(value = "classpath:openapi-config.yaml", factory = YamlPropertySourceFactory.class)
public @interface EnableCustomDoc {

	/**
	 * 网关路由前缀
	 * @return String
	 */
	String value();

	/**
	 * 是否是微服务架构
	 * @return bool
	 */
	boolean isMicro() default true;

}
