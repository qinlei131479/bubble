package com.bubblecloud.oa.api.vo.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考勤组新建返回（对齐 PHP 首步保存返回 {@code { id }}）。
 *
 * @author qinlei
 * @date 2026/4/2 18:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "考勤组新建结果")
public class AttendanceGroupCreateVO {

	@Schema(description = "考勤组主键")
	private Integer id;

}
