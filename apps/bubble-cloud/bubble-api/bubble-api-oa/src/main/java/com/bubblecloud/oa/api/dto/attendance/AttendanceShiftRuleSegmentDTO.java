package com.bubblecloud.oa.api.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班次规则段（对应 PHP number1 / number2 内层字段）。
 *
 * @author qinlei
 * @date 2026/4/7 12:15
 */
@Data
@Schema(description = "班次规则段")
public class AttendanceShiftRuleSegmentDTO {

	@Schema(description = "记录ID（更新时可选）")
	private Integer id;

	@Schema(description = "上班时间 HH:mm")
	private String workHours;

	@Schema(description = "下班时间 HH:mm")
	private String offHours;

	@Schema(description = "迟到（秒）")
	private Integer late;

	@Schema(description = "严重迟到（秒）")
	private Integer extremeLate;

	@Schema(description = "晚到缺卡（秒）")
	private Integer lateLackCard;

	@Schema(description = "提前打卡（秒）")
	private Integer earlyCard;

	@Schema(description = "早退（秒）")
	private Integer earlyLeave;

	@Schema(description = "提前缺卡（秒）")
	private Integer earlyLackCard;

	@Schema(description = "延后打卡（秒）")
	private Integer delayCard;

	@Schema(description = "下班可免打卡")
	private Integer freeClock;

	@Schema(description = "上班是否次日 0/1")
	private Integer firstDayAfter;

	@Schema(description = "下班是否次日 0/1")
	private Integer secondDayAfter;

}
