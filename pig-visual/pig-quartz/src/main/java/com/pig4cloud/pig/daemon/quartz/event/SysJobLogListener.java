package com.pig4cloud.pig.daemon.quartz.event;

import com.pig4cloud.pig.daemon.quartz.entity.SysJobLog;
import com.pig4cloud.pig.daemon.quartz.service.SysJobLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author frwcloud 异步监听定时任务日志事件
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysJobLogListener {

	private final SysJobLogService sysJobLogService;

	@Async
	@Order
	@EventListener(SysJobLogEvent.class)
	public void saveSysJobLog(SysJobLogEvent event) {
		SysJobLog sysJobLog = event.getSysJobLog();
		sysJobLogService.save(sysJobLog);
		log.info("执行定时任务日志");
	}

}
