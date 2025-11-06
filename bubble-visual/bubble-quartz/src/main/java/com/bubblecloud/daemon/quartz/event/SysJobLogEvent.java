package com.bubblecloud.daemon.quartz.event;

import com.bubblecloud.daemon.quartz.entity.SysJobLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时任务日志多线程事件
 *
 * @author frwcloud
 * @author qinlei
 * @date 2025/05/31
 */
@Getter
@AllArgsConstructor
public class SysJobLogEvent {

	private final SysJobLog sysJobLog;

}
