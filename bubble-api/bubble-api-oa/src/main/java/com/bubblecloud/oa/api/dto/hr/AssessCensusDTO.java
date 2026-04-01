package com.bubblecloud.oa.api.dto.hr;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核统计查询 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核统计查询")
public class AssessCensusDTO {

	@Schema(description = "开始日期")
	private LocalDate startDate;

	@Schema(description = "结束日期")
	private LocalDate endDate;

	@Schema(description = "部门ID")
	private Long frameId;

	@Schema(description = "用户ID")
	private Long userId;

}
