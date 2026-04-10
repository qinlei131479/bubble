package com.bubblecloud.gateway.config;

import com.bubblecloud.gateway.filter.RequestGlobalFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bubblecloud.gateway.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 *
 * @author qinlei
 * @date 2025/05/30
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

	/**
	 * 创建Request全局过滤器
	 * @return Request全局过滤器
	 */
	@Bean
	public RequestGlobalFilter requestGlobalFilter() {
		return new RequestGlobalFilter();
	}

	/**
	 * 创建全局异常处理程序
	 * @param objectMapper 对象映射器
	 * @return 全局异常处理程序
	 */
	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalExceptionHandler(objectMapper);
	}

}
