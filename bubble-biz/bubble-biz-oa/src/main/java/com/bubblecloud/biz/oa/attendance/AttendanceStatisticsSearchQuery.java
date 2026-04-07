package com.bubblecloud.biz.oa.attendance;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 考勤统计列表查询条件（XML 动态 SQL 使用）。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Data
public class AttendanceStatisticsSearchQuery {

	private List<Integer> uidIn;

	private Integer frameId;

	private Integer groupId;

	private LocalDateTime rangeStart;

	private LocalDateTime rangeEnd;

	/** 月度统计：{@code yyyy-MM}，按 DATE_FORMAT(created_at) 匹配 */
	private String monthYm;

	private List<Integer> statusInShifts;

	private Integer locationEqAnyShift;

	/** 异常日期：打卡状态 OR 条件 */
	private List<Integer> repairStatusIn;

	/** 异常日期：地点状态 = */
	private Integer repairLocationStatus;

}
