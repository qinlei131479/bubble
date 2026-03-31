package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.entity.Schedule;
import com.bubblecloud.oa.api.dto.ScheduleIndexQueryDTO;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.dto.UserScheduleQueryDTO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRecordVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleTypeVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;

/**
 * 日程接口占位（兼容 PHP ent/schedule 与待办列表）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
public interface ScheduleApiService extends UpService<Schedule> {

	/**
	 * 日程类型列表。
	 */
	List<ScheduleTypeVO> typeList();

	/**
	 * 日程列表 POST /schedule/index。
	 */
	List<ScheduleRecordVO> scheduleIndex(ScheduleIndexQueryDTO body);

	/**
	 * 修改日程状态。
	 */
	void updateStatus(long id, ScheduleStatusUpdateDTO body);

	/**
	 * GET /user/schedule 日历待办（外层为数组，首元素含 list）。
	 */
	List<UserScheduleDayWrapperVO> userScheduleList(UserScheduleQueryDTO query);

}
