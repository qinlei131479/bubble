package com.bubblecloud.biz.oa.service;

import java.time.LocalDateTime;

/**
 * 审批通过后刷新考勤日统计（对齐 PHP {@code AttendanceStatisticsService::updateAbnormalShiftStatus} /
 * {@code calcLeaveDurationByTime}，具体算法在实现中逐步迁移）。
 *
 * @author qinlei
 * @date 2026/4/7 17:30
 */
public interface AttendanceStatisticsApproveHook {

	/**
	 * 按请假/外出/出差/加班/补卡区间更新异常班次状态。
	 */
	void updateAbnormalShiftStatus(int uid, int applyType, LocalDateTime start, LocalDateTime end, String othersJson);

	/**
	 * 按时段写入请假类型汇总（PHP {@code AttendanceStatisticsLeaveService} 链路）。
	 */
	void calcLeaveDurationByTime(int uid, int recordId, long holidayTypeId, LocalDateTime start, LocalDateTime end);

}
