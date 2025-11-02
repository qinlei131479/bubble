package com.bubblecloud.daemon.quartz.event;

import com.bubblecloud.daemon.quartz.util.TaskInvokUtil;
import com.bubblecloud.daemon.quartz.entity.SysJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Trigger;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author frwcloud 异步监听定时任务事件
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobListener {

	private final TaskInvokUtil taskInvokUtil;

	@Async
	@Order
	@EventListener(SysJobEvent.class)
	public void comSysJob(SysJobEvent event) {
		SysJob sysJob = event.getSysJob();
		Trigger trigger = event.getTrigger();
		taskInvokUtil.invokMethod(sysJob, trigger);
	}

}
