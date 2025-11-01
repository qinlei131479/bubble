package com.pig4cloud.pig.daemon.quartz.event;

import com.pig4cloud.pig.daemon.quartz.entity.SysJob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.quartz.Trigger;

/**
 * @author frwcloud 定时任务多线程事件
 */
@Getter
@AllArgsConstructor
public class SysJobEvent {

	private final SysJob sysJob;

	private final Trigger trigger;

}
