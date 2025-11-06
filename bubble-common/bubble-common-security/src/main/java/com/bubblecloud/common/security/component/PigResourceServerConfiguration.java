package com.bubblecloud.common.security.component;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import lombok.RequiredArgsConstructor;

/**
 * 资源服务器认证授权配置
 *
 * @author lengleng
 * @date 2025/05/31
 */
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class PigResourceServerConfiguration {

	/**
	 * 资源认证异常处理入口点
	 */
	protected final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

	/**
	 * 允许所有URL的配置属性
	 */
	private final PermitAllUrlProperties permitAllUrl;

	/**
	 * PigBearerToken提取器
	 */
	private final PigBearerTokenExtractor pigBearerTokenExtractor;

	/**
	 * 自定义不透明令牌解析器
	 */
	private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;

	/**
	 * 资源服务器安全配置
	 * @param http http
	 * @return {@link SecurityFilterChain }
	 * @throws Exception 异常
	 */
	@Bean
	SecurityFilterChain resourceServer(HttpSecurity http) throws Exception {
		/**
		 * AntPathRequestMatcher[] permitMatchers = permitAllUrl.getUrls() .stream()
		 * .map(AntPathRequestMatcher::new) .toList() .toArray(new AntPathRequestMatcher[]
		 * {});
		 **/
		PathPatternRequestMatcher[] permitMatchers = permitAllUrl.getUrls()
			.stream()
			.map(url -> PathPatternRequestMatcher.withDefaults().matcher(url))
			.toList()
			.toArray(new PathPatternRequestMatcher[] {});

		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(permitMatchers)
			.permitAll()
			.anyRequest()
			.authenticated())
			.oauth2ResourceServer(
					oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
						.authenticationEntryPoint(resourceAuthExceptionEntryPoint)
						.bearerTokenResolver(pigBearerTokenExtractor))
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

}
