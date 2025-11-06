package com.pig4cloud.pig.common.log;

import com.bubblecloud.backend.api.feign.RemoteLogService;
import com.pig4cloud.pig.common.log.aspect.SysLogAspect;
import com.pig4cloud.pig.common.log.config.PigLogProperties;
import com.pig4cloud.pig.common.log.event.SysLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 日志自动配置类，用于配置系统日志相关功能
 *
 * @author lengleng
 * @date 2025/05/31
 */
@EnableAsync
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PigLogProperties.class)
@ConditionalOnProperty(value = "security.log.enabled", matchIfMissing = true)
public class LogAutoConfiguration {

	/**
	 * 创建并返回SysLogListener的Bean实例
	 * @param logProperties 日志属性配置
	 * @param remoteLogService 远程日志服务
	 * @return SysLogListener实例
	 */
	@Bean
	public SysLogListener sysLogListener(PigLogProperties logProperties, RemoteLogService remoteLogService) {
		return new SysLogListener(remoteLogService, logProperties);
	}

	/**
	 * 创建并返回SysLogAspect的Bean实例
	 * @return SysLogAspect实例
	 */
	@Bean
	public SysLogAspect sysLogAspect() {
		return new SysLogAspect();
	}

}
