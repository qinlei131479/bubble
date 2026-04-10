package com.bubblecloud.biz.oa.constant.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * OA 异步任务（对齐 PHP 队列：考勤导入分块投递）。
 *
 * @author qinlei
 * @date 2026/4/7 15:40
 */
@Configuration
@EnableAsync
public class OaAsyncConfig {

	public static final String ATTENDANCE_IMPORT_EXECUTOR = "attendanceImportExecutor";

	@Bean(name = ATTENDANCE_IMPORT_EXECUTOR)
	public Executor attendanceImportExecutor() {
		ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
		ex.setCorePoolSize(2);
		ex.setMaxPoolSize(6);
		ex.setQueueCapacity(500);
		ex.setThreadNamePrefix("oa-attendance-import-");
		ex.initialize();
		return ex;
	}

}
