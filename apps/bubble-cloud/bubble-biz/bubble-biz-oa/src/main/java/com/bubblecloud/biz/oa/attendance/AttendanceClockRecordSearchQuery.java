package com.bubblecloud.biz.oa.attendance;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * 打卡记录列表查询条件。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Data
public class AttendanceClockRecordSearchQuery {

	private List<Integer> uidIn;

	private Integer uidSingle;

	private Integer frameId;

	private Integer groupId;

	private LocalDateTime rangeStart;

	private LocalDateTime rangeEnd;

}
