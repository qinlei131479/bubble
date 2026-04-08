package com.bubblecloud.oa.api.dto.program;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务列表查询参数（GET，对齐 PHP index where，snake_case）。
 *
 * @author qinlei
 * @date 2026/4/8 15:00
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务列表查询")
public class ProgramTaskIndexQuery {

	@Schema(description = "视图类型 0/1/2/3")
	private Integer types;

	@Schema(description = "项目ID")
	private Long programId;

	@Schema(description = "版本ID")
	private Long versionId;

	@Schema(description = "负责人筛选")
	private Long uid;

	@Schema(description = "父任务ID")
	private Long pid;

	@Schema(description = "状态")
	private Integer status;

	@Schema(description = "优先级")
	private Integer priority;

	@Schema(description = "名称模糊")
	private String nameLike;

	@Schema(description = "时间范围")
	private String time;

	@Schema(description = "时间字段 plan_start/plan_end/created_at")
	private String timeField;

	@Schema(description = "协作者用户ID列表")
	private List<Long> members;

	@Schema(description = "负责人多选")
	private List<Long> admins;

}
