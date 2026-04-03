package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.CalendarUpdateDTO;
import com.bubblecloud.oa.api.entity.CalendarConfig;

/**
 * 考勤日历配置（对齐 PHP CalendarConfigService）。
 *
 * @author qinlei
 * @date 2026/4/2 15:00
 */
public interface CalendarConfigService extends UpService<CalendarConfig> {

	/**
	 * 休息日列表（日期字符串 yyyy-MM-dd）。
	 */
	List<String> getRestList(String time);

	/**
	 * 批量保存某日所在月日历调整（body 可为 null，校验在实现层）。
	 */
	void updateCalendar(String date, CalendarUpdateDTO body);

}
