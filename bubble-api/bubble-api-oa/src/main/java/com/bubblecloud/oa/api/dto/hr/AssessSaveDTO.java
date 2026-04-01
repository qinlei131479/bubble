package com.bubblecloud.oa.api.dto.hr;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 绩效考核保存 DTO。
 *
 * @author qinlei
 * @date 2026/4/1 14:00
 */
@Data
@Schema(description = "绩效考核保存")
public class AssessSaveDTO {

	@Schema(description = "考核名称")
	private String name;

	@Schema(description = "被考核人ID")
	private Long userId;

	@Schema(description = "考核人ID（上级）")
	private Long superiorId;

	@Schema(description = "审核人ID（上上级）")
	private Long examineId;

	@Schema(description = "考核计划ID")
	private Long planId;

	@Schema(description = "考核模板ID")
	private Long templateId;

	@Schema(description = "考核开始日期")
	private LocalDate startDate;

	@Schema(description = "考核结束日期")
	private LocalDate endDate;

	@Schema(description = "备注")
	private String mark;

}
