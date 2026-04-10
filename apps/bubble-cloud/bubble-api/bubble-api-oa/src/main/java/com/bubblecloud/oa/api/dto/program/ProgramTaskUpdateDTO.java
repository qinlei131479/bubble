package com.bubblecloud.oa.api.dto.program;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 单字段更新任务（对齐 PHP update + field）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务单字段更新")
public class ProgramTaskUpdateDTO {

	@Schema(description = "字段名：name/status/priority/describe/uid/version_id/program_id/pid/members/plan_start/plan_end/plan_date")
	private String field;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "优先级")
	private Integer priority;

	@Schema(description = "描述")
	private String describe;

	@Schema(description = "负责人")
	private Long uid;

	@Schema(description = "版本ID")
	private Long versionId;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "父任务ID")
	private Long pid;

	@Schema(description = "协作者")
	private List<Long> members;

	@Schema(description = "计划开始")
	private LocalDate planStart;

	@Schema(description = "计划结束")
	private LocalDate planEnd;

}
