package com.bubblecloud.oa.api.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 保存/修改班次请求体（对齐 PHP AttendanceShiftController#getRequestFields）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "考勤班次保存参数")
public class AttendanceShiftSaveDTO {

	@Schema(description = "班次名称")
	private String name;

	@Schema(description = "上下班次数 1/2")
	private Integer number;

	@Schema(description = "中途休息 1/0")
	private Integer restTime;

	@Schema(description = "休息开始")
	private String restStart;

	@Schema(description = "休息结束")
	private String restEnd;

	@Schema(description = "加班规则 JSON 字符串")
	private String overtime;

	@Schema(description = "工作时长展示")
	private String workTime;

	@Schema(description = "颜色")
	private String color;

	@Schema(description = "第一次上下班规则")
	private AttendanceShiftRuleSegmentDTO number1;

	@Schema(description = "第二次上下班规则")
	private AttendanceShiftRuleSegmentDTO number2;

	@Schema(description = "休息开始是否次日 0/1")
	private Integer restStartAfter;

	@Schema(description = "休息结束是否次日 0/1")
	private Integer restEndAfter;

}
