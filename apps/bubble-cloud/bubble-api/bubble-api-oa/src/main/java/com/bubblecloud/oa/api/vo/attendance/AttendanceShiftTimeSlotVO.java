package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班次时间段（对齐 PHP times 关联精简列）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "班次时间段")
public class AttendanceShiftTimeSlotVO {

	@JsonProperty("work_hours")
	private String workHours;

	@JsonProperty("off_hours")
	private String offHours;

	@JsonProperty("shift_id")
	private Integer shiftId;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("first_day_after")
	private Integer firstDayAfter;

	@JsonProperty("second_day_after")
	private Integer secondDayAfter;

}
