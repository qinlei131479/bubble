package com.bubblecloud.biz.oa.service.impl;

import java.time.LocalDateTime;

import com.bubblecloud.biz.oa.service.AttendanceStatisticsApproveHook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 考勤统计与审批联动占位实现（完整算法见 PHP {@code AttendanceStatisticsService}）。
 *
 * @author qinlei
 * @date 2026/4/7 17:30
 */
@Slf4j
@Service
public class AttendanceStatisticsApproveHookImpl implements AttendanceStatisticsApproveHook {

	@Override
	public void updateAbnormalShiftStatus(int uid, int applyType, LocalDateTime start, LocalDateTime end,
			String othersJson) {
		log.debug("[OA] updateAbnormalShiftStatus uid={} applyType={} start={} end={} othersLen={}", uid, applyType,
				start, end, othersJson == null ? 0 : othersJson.length());
	}

	@Override
	public void calcLeaveDurationByTime(int uid, int recordId, long holidayTypeId, LocalDateTime start,
			LocalDateTime end) {
		log.debug("[OA] calcLeaveDurationByTime uid={} recordId={} holidayTypeId={} start={} end={}", uid, recordId,
				holidayTypeId, start, end);
	}

}
