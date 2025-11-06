package com.bubblecloud.daemon.quartz.config;

import com.bubblecloud.daemon.quartz.entity.SysJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.bubblecloud.daemon.quartz.constants.QuartzEnum;

import lombok.SneakyThrows;

/**
 * 动态任务工厂：用于执行动态任务调度
 *
 * @author qinlei
 * @author 郑健楠
 * @date 2025/05/31
 */
@DisallowConcurrentExecution
public class QuartzFactory implements Job {

	/**
	 * 定时任务调用工厂
	 */
	@Autowired
	private QuartzInvokeFactory quartzInvokeFactory;

	/**
	 * 执行定时任务
	 * @param jobExecutionContext 任务执行上下文
	 * @throws Exception 执行过程中可能抛出的异常
	 */
	@Override
	@SneakyThrows
	public void execute(JobExecutionContext jobExecutionContext) {
		SysJob sysJob = (SysJob) jobExecutionContext.getMergedJobDataMap()
			.get(QuartzEnum.SCHEDULE_JOB_KEY.getType());
		quartzInvokeFactory.init(sysJob, jobExecutionContext.getTrigger());
	}

}
