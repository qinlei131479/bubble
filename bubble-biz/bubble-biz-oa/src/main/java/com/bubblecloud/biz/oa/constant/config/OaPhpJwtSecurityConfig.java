package com.bubblecloud.biz.oa.constant.config;

import com.bubblecloud.biz.oa.filter.OaPhpJwtAuthenticationFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * OA 模块 JWT 安全链路。
 *
 * @author qinlei
 * @date 2026/3/30 18:00
 */
@Configuration
@EnableConfigurationProperties(OaPhpJwtProperties.class)
public class OaPhpJwtSecurityConfig {

	@Bean
	@Order(0)
	public SecurityFilterChain oaPhpJwtSecurityFilterChain(HttpSecurity http,
			OaPhpJwtAuthenticationFilter phpJwtAuthenticationFilter) throws Exception {
		http.securityMatcher("/ent/**", "/oa/**")
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/ent/user/login", "/ent/user/register", "/ent/user/phone_login", "/ent/user/scan_key",
						"/ent/user/scan_status", "/ent/common/captcha", "/ent/common/site")
				.permitAll()
				.anyRequest()
				.authenticated())
			.addFilterBefore(phpJwtAuthenticationFilter, BasicAuthenticationFilter.class);
		return http.build();
	}

}
