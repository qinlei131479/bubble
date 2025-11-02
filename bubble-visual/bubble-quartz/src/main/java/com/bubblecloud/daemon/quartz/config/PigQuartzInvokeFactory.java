package com.bubblecloud.daemon.quartz.config;

import com.bubblecloud.daemon.quartz.event.SysJobEvent;
import com.bubblecloud.daemon.quartz.entity.SysJob;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.quartz.Trigger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author 郑健楠
 */
@Slf4j
@Aspect
@Service
@AllArgsConstructor
public class PigQuartzInvokeFactory {

	private final ApplicationEventPublisher publisher;

	@SneakyThrows
	void init(SysJob sysJob, Trigger trigger) {
		publisher.publishEvent(new SysJobEvent(sysJob, trigger));
	}

}
