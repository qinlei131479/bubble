package com.bubblecloud.daemon.quartz.config;

import com.bubblecloud.daemon.quartz.util.TaskUtil;
import org.quartz.Scheduler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import com.bubblecloud.daemon.quartz.constants.QuartzEnum;
import com.bubblecloud.daemon.quartz.service.SysJobService;

import lombok.AllArgsConstructor;

/**
 * 初始化加载定时任务配置类
 *
 * @author qinlei
 * @author 郑健楠
 * @date 2025/05/31
 */
@Configuration
@AllArgsConstructor
public class CustomInitQuartzJob implements InitializingBean {

	private final SysJobService sysJobService;

	private final TaskUtil taskUtil;

	private final Scheduler scheduler;

	/**
	 * 在属性设置完成后执行，根据任务状态进行相应操作
	 * @throws Exception 执行过程中可能抛出的异常
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		sysJobService.list().forEach(sysjob -> {
			if (QuartzEnum.JOB_STATUS_RELEASE.getType().equals(sysjob.getJobStatus())) {
				taskUtil.removeJob(sysjob, scheduler);
			}
			else if (QuartzEnum.JOB_STATUS_RUNNING.getType().equals(sysjob.getJobStatus())) {
				taskUtil.resumeJob(sysjob, scheduler);
			}
			else if (QuartzEnum.JOB_STATUS_NOT_RUNNING.getType().equals(sysjob.getJobStatus())) {
				taskUtil.pauseJob(sysjob, scheduler);
			}
			else {
				taskUtil.removeJob(sysjob, scheduler);
			}
		});
	}

}
