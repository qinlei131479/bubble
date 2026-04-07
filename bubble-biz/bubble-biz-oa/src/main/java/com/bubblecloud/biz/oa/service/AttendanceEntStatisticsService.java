package com.bubblecloud.biz.oa.service;

import java.util.List;
import java.util.Map;

import com.bubblecloud.common.mybatis.base.Pg;
import com.bubblecloud.oa.api.dto.attendance.AttendanceStatisticsAdjustDTO;

/**
 * 考勤统计与打卡相关接口（对齐 PHP {@code AttendanceStatisticsController} +
 * {@code AttendanceClockController}）。
 *
 * @author qinlei
 * @date 2026/4/7
 */
public interface AttendanceEntStatisticsService {

	Map<String, Object> dailyStatistics(Pg<Object> pg, Long viewerId, Integer scope, List<Integer> personnelStatus,
			Integer frameId, Integer groupId, String time, List<Integer> userId, int filterType);

	Map<String, Object> monthlyStatistics(Pg<Object> pg, Long viewerId, Integer scope, List<Integer> personnelStatus,
			Integer frameId, Integer groupId, String month);

	void saveStatisticsResult(Long viewerId, long statisticsId, AttendanceStatisticsAdjustDTO dto);

	Map<String, Object> handleRecordList(Pg<Object> pg, Long viewerId, long statisticsId);

	Map<String, Object> attendanceStatistics(Long viewerId, Integer userId, String time);

	Map<String, Object> individualStatistics(Pg<Object> pg, Long viewerId, List<Integer> personnelStatus,
			List<Integer> userId, String time);

	Map<String, Object> clockRecordList(Pg<Object> pg, Integer scope, Integer frameId, Integer groupId, String time,
			Integer uid);

	Map<String, Object> clockRecordInfo(long id);

	List<Map<String, Object>> abnormalDateList(Long viewerId);

	List<Map<String, Object>> abnormalRecordList(Long viewerId, long statisticsId);

	void importClockRecord(List<Map<String, Object>> data);

	void importClockThirdParty(int type, List<Map<String, Object>> data);

}
