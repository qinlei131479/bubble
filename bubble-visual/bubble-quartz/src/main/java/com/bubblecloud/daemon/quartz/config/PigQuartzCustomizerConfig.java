package com.bubblecloud.daemon.quartz.config;

import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * PigQuartz 自定义配置类，用于配置 SchedulerFactoryBean
 *
 * @author qinlei
 * @author 郑健楠
 * @date 2025/05/31
 */
@Configuration
public class PigQuartzCustomizerConfig implements SchedulerFactoryBeanCustomizer {

	/**
	 * 自定义SchedulerFactoryBean配置
	 * @param schedulerFactoryBean 调度器工厂bean
	 */
	@Override
	public void customize(SchedulerFactoryBean schedulerFactoryBean) {
		schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
	}

}
