package com.bubblecloud.daemon.quartz.config;

import com.bubblecloud.daemon.quartz.constants.PigQuartzEnum;
import com.bubblecloud.daemon.quartz.entity.SysJob;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 郑健楠
 *
 * <p>
 * 动态任务工厂
 */
@Slf4j
@DisallowConcurrentExecution
public class PigQuartzFactory implements Job {

	@Autowired
	private PigQuartzInvokeFactory pigxQuartzInvokeFactory;

	@Override
	@SneakyThrows
	public void execute(JobExecutionContext jobExecutionContext) {
		SysJob sysJob = (SysJob) jobExecutionContext.getMergedJobDataMap()
			.get(PigQuartzEnum.SCHEDULE_JOB_KEY.getType());
		pigxQuartzInvokeFactory.init(sysJob, jobExecutionContext.getTrigger());
	}

}
