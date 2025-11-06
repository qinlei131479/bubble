package com.bubblecloud.common.security.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 *  Feign 客户端配置类
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class CustomFeignClientConfiguration {

	/**
	 * 注入 oauth2 feign token 增强
	 * @param tokenResolver token获取处理器
	 * @return 拦截器
	 */
	@Bean
	public RequestInterceptor oauthRequestInterceptor(BearerTokenResolver tokenResolver) {
		return new CustomOAuthRequestInterceptor(tokenResolver);
	}

}
