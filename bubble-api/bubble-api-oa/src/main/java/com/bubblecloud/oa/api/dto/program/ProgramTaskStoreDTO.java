package com.bubblecloud.oa.api.dto.program;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 创建/保存任务请求体（对齐 PHP ProgramTaskController#getRequestFields）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务保存参数")
public class ProgramTaskStoreDTO {

	@Schema(description = "父级路径（末级为直接父任务ID）")
	private List<Long> path;

	@Schema(description = "任务名称")
	private String name;

	@Schema(description = "负责人")
	private Long uid;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "优先级")
	private Integer priority;

	@Schema(description = "描述")
	private String describe;

	@Schema(description = "协作者用户ID")
	private List<Long> members;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "版本ID")
	private Long versionId;

	@Schema(description = "计划开始")
	private LocalDate planStart;

	@Schema(description = "计划结束")
	private LocalDate planEnd;

}
