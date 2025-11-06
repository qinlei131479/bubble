package com.bubblecloud.daemon.quartz.config;

import com.bubblecloud.daemon.quartz.entity.SysJob;
import com.bubblecloud.daemon.quartz.event.SysJobEvent;
import org.aspectj.lang.annotation.Aspect;
import org.quartz.Trigger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * 定时任务调用工厂类 用于初始化并发布定时任务事件
 *
 * @author qinlei
 * @date 2025/05/31
 */
@Aspect
@Service
@AllArgsConstructor
public class QuartzInvokeFactory {

	private final ApplicationEventPublisher publisher;

	/**
	 * 初始化并发布定时任务事件
	 * @param sysJob 系统任务对象
	 * @param trigger 任务触发器
	 */
	@SneakyThrows
	void init(SysJob sysJob, Trigger trigger) {
		publisher.publishEvent(new SysJobEvent(sysJob, trigger));
	}

}
