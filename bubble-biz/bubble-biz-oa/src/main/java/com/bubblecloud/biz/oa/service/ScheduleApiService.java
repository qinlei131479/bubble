package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

/**
 * 日程接口占位（兼容 PHP ent/schedule 与待办列表）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
public interface ScheduleApiService {

	/**
	 * 日程类型列表。
	 */
	List<Map<String, Object>> typeList();

	/**
	 * 日程列表 POST /schedule/index。
	 */
	List<Map<String, Object>> scheduleIndex(Map<String, Object> body);

	/**
	 * 修改日程状态。
	 */
	void updateStatus(long id, Map<String, Object> body);

	/**
	 * GET /user/schedule 日历待办（外层为数组，首元素含 list）。
	 */
	List<Map<String, Object>> userScheduleList(Map<String, String> query);

}
