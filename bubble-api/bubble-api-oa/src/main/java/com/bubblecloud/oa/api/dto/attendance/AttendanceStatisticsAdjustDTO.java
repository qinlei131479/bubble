package com.bubblecloud.oa.api.dto.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 手工调整考勤日统计（对齐 PHP {@code AttendanceStatisticsController::update}）。
 *
 * @author qinlei
 * @date 2026/4/7
 */
@Data
@Schema(description = "考勤统计手工处理")
public class AttendanceStatisticsAdjustDTO {

	@Schema(description = "班次序号 0-3")
	private Integer number;

	@Schema(description = "备注")
	private String remark;

	@Schema(description = "打卡状态")
	private Integer status;

	@Schema(description = "外勤/地点状态")
	private Integer locationStatus;

}
