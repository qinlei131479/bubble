package com.bubblecloud.oa.api.dto.program;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量更新任务（对齐 PHP ProgramTaskController#batchUpdate）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务批量更新参数")
public class ProgramTaskBatchDTO {

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "版本ID")
	private Long versionId;

	@Schema(description = "父任务ID")
	private Long pid;

	@Schema(description = "负责人")
	private Long uid;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "批量计划开始")
	private LocalDate startDate;

	@Schema(description = "批量计划结束")
	private LocalDate endDate;

	@Schema(description = "任务ID列表")
	private List<Long> data;

}
