package com.bubblecloud.common.core.config;

import cn.hutool.core.date.DatePattern;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * WebMvc配置类：用于自定义Spring MVC配置
 * <p>
 * 包含GET请求参数时间类型转换和系统国际化配置
 *
 * @author lengleng
 * @date 2025/05/30
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = SERVLET)
public class WebMvcConfiguration implements WebMvcConfigurer {

	/**
	 * 增加GET请求参数中时间类型转换
	 * @param registry 格式化注册器
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
		registrar.setTimeFormatter(DatePattern.NORM_TIME_FORMATTER);
		registrar.setDateFormatter(DatePattern.NORM_DATE_FORMATTER);
		registrar.setDateTimeFormatter(DatePattern.NORM_DATETIME_FORMATTER);
		registrar.registerFormatters(registry);
	}

	/**
	 * 创建并配置国际化消息源
	 * @return 可重载的资源包消息源
	 */
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:i18n/messages");
		return messageSource;
	}

}
