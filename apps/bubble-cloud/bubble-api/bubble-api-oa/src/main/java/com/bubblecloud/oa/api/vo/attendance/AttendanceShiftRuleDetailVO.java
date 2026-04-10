package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班次规则详情（用于 info 接口 number1/number2）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "班次规则详情")
public class AttendanceShiftRuleDetailVO {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("shift_id")
	private Integer shiftId;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("first_day_after")
	private Integer firstDayAfter;

	@JsonProperty("second_day_after")
	private Integer secondDayAfter;

	@JsonProperty("work_hours")
	private String workHours;

	@JsonProperty("late")
	private Integer late;

	@JsonProperty("extreme_late")
	private Integer extremeLate;

	@JsonProperty("late_lack_card")
	private Integer lateLackCard;

	@JsonProperty("early_card")
	private Integer earlyCard;

	@JsonProperty("off_hours")
	private String offHours;

	@JsonProperty("early_leave")
	private Integer earlyLeave;

	@JsonProperty("early_lack_card")
	private Integer earlyLackCard;

	@JsonProperty("delay_card")
	private Integer delayCard;

	@JsonProperty("free_clock")
	private Integer freeClock;

}
