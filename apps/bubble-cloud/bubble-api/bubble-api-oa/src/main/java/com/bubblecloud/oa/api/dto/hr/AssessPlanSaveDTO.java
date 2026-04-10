package com.bubblecloud.oa.api.dto.hr;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核计划保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核计划保存")
public class AssessPlanSaveDTO {

	@Schema(description = "计划名称")
	private String name;

	@Schema(description = "考核周期类型：month/quarter/year/custom")
	private String cycleType;

	@Schema(description = "开始日期")
	private LocalDate startDate;

	@Schema(description = "结束日期")
	private LocalDate endDate;

	@Schema(description = "参与人员ID列表")
	private List<Long> userIds;

	@Schema(description = "状态 1启用 0停用")
	private Integer status;

}
