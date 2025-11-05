package com.pig4cloud.pig.common.log.event;

import java.io.Serial;
import org.springframework.context.ApplicationEvent;
import com.pig4cloud.pig.admin.api.entity.SysLog;

/**
 * 系统日志事件类
 *
 * @author lengleng
 * @date 2025/05/31
 */
public class SysLogEvent extends ApplicationEvent {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 构造方法，根据源SysLog对象创建SysLogEvent
	 * @param source 源SysLog对象
	 */
	public SysLogEvent(SysLog source) {
		super(source);
	}

}
