package com.bubblecloud.oa.api.vo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班次详情（对齐 PHP info：主表字段 + number1/number2）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "班次详情")
public class AttendanceShiftDetailVO {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("rest_time")
	private Integer restTime;

	@JsonProperty("rest_start")
	private String restStart;

	@JsonProperty("rest_end")
	private String restEnd;

	@JsonProperty("rest_start_after")
	private Integer restStartAfter;

	@JsonProperty("rest_end_after")
	private Integer restEndAfter;

	@JsonProperty("overtime")
	private Integer overtime;

	@JsonProperty("work_time")
	private String workTime;

	@JsonProperty("color")
	private String color;

	@JsonProperty("uid")
	private Integer uid;

	@JsonProperty("number1")
	private AttendanceShiftRuleDetailVO number1;

	@JsonProperty("number2")
	private AttendanceShiftRuleDetailVO number2;

}
