package com.bubblecloud.common.security.component;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Locale;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * 创建并配置安全相关的消息源
 *
 * @return 配置好的ReloadableResourceBundleMessageSource实例
 */
@ConditionalOnWebApplication(type = SERVLET)
public class CustomSecurityMessageSourceConfiguration implements WebMvcConfigurer {

	/**
	 * 创建并配置安全相关的消息源
	 * @return 配置好的ReloadableResourceBundleMessageSource实例
	 */
	@Bean
	public MessageSource securityMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.addBasenames("classpath:i18n/errors/messages");
		messageSource.setDefaultLocale(Locale.CHINA);
		return messageSource;
	}

}
