package com.bubblecloud.biz.oa.service;

import java.util.List;

import com.bubblecloud.common.mybatis.service.UpService;
import com.bubblecloud.oa.api.dto.ScheduleDeleteDTO;
import com.bubblecloud.oa.api.dto.ScheduleIndexQueryDTO;
import com.bubblecloud.oa.api.dto.ScheduleReplySaveDTO;
import com.bubblecloud.oa.api.dto.ScheduleStatusUpdateDTO;
import com.bubblecloud.oa.api.dto.ScheduleStoreDTO;
import com.bubblecloud.oa.api.dto.ScheduleUpdateDTO;
import com.bubblecloud.oa.api.dto.UserScheduleQueryDTO;
import com.bubblecloud.oa.api.entity.Schedule;
import com.bubblecloud.oa.api.vo.schedule.ScheduleCalendarCountItemVO;
import com.bubblecloud.oa.api.vo.schedule.ScheduleRecordVO;
import com.bubblecloud.oa.api.vo.schedule.UserScheduleDayWrapperVO;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 日程主流程（ent/schedule、用户待办日历）。
 *
 * @author qinlei
 * @date 2026/3/28 15:30
 */
public interface ScheduleApiService extends UpService<Schedule> {

	List<ScheduleRecordVO> scheduleIndex(Long userId, ScheduleIndexQueryDTO body);

	void updateStatus(Long userId, ScheduleStatusUpdateDTO body);

	List<UserScheduleDayWrapperVO> userScheduleList(Long userId, UserScheduleQueryDTO query);

	void saveSchedule(Long userId, int entid, ScheduleStoreDTO dto);

	void updateSchedule(Long userId, int entid, Long id, ScheduleUpdateDTO dto);

	void deleteSchedule(Long userId, Long id, ScheduleDeleteDTO dto);

	JsonNode scheduleInfo(Long userId, Long id, String startTime, String endTime);

	List<ScheduleCalendarCountItemVO> scheduleCount(Long userId, int entid, ScheduleIndexQueryDTO body);

	JsonNode replys(Long scheduleId);

	void saveScheduleReply(Long userId, int entid, ScheduleReplySaveDTO dto);

	void deleteScheduleReply(Long userId, Long id);

}
