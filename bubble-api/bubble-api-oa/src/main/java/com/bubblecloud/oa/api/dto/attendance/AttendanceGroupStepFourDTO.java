package com.bubblecloud.oa.api.dto.attendance;

import java.util.Collections;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 考勤组第四步：考勤周期（PHP 写入排班周期，Java 侧待 RosterCycle 迁移后对接）。
 *
 * @author qinlei
 * @date 2026/4/2 16:00
 */
@Data
@Schema(description = "考勤组第四步周期")
public class AttendanceGroupStepFourDTO {

	@Schema(description = "周期配置（结构同前端/PHP）")
	private List<Object> data = Collections.emptyList();

}
