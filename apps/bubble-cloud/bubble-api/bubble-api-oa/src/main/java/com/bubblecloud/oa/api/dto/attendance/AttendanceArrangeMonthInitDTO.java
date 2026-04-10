package com.bubblecloud.oa.api.dto.attendance;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 初始化排班月份（对齐 PHP arrange store）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "排班月份初始化")
public class AttendanceArrangeMonthInitDTO {

	@Schema(description = "月份 yyyy-MM")
	private String date;

	@Schema(description = "考勤组 ID 列表")
	private List<Integer> groups;

}
