package com.bubblecloud.oa.api.dto.program;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 兄弟任务排序（对齐 PHP ProgramTaskController#sort）。
 *
 * @author qinlei
 * @date 2026/4/8 10:30
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "任务排序参数")
public class ProgramTaskSortDTO {

	@Schema(description = "当前任务ID")
	private Long current;

	@Schema(description = "目标兄弟任务ID")
	private Long target;

}
