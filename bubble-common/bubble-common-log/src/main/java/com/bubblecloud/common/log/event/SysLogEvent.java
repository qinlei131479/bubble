package com.bubblecloud.common.log.event;

import com.bubblecloud.api.backend.entity.SysLog;
import org.springframework.context.ApplicationEvent;

/**
 * @author lengleng 系统日志事件
 */
public class SysLogEvent extends ApplicationEvent {

	public SysLogEvent(SysLog source) {
		super(source);
	}

}
